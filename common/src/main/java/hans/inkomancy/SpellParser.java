package hans.inkomancy;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public record SpellParser(ServerLevel world, Transform2D transform, Ink ink) {
  public Set<BlockPos> connectedBlocks(BlockPos initial) {
    var toScan = new LinkedHashSet<BlockPos>();
    var scanned = new HashSet<BlockPos>();
    var connected = new HashSet<BlockPos>();
    toScan.add(initial);

    while (!toScan.isEmpty()) {
      BlockPos current = toScan.removeFirst();
      scanned.add(current);

      if (!ink.getBlock().canAttach(world.getBlockState(current), transform)) {
        continue;
      } else {
        connected.add(current);
      }

      for (var direction : transform.directions()) {
        var position = current.relative(direction);
        if (!scanned.contains(position)) {
          toScan.add(position);
        }
      }
    }

    return connected;
  }

  public Pair<BlockPos, Direction> findStart(Set<BlockPos> connected) {
    var starts = new ArrayList<Pair<BlockPos, Direction>>();
    for (var current : connected) {
      for (var direction : transform.directions()) {
        if (Glyph.START.test(world, current, transform.withForwards(direction), ink.getBlock())) {
          starts.add(new Pair<>(current, direction));
        }
      }
    }

    if (starts.isEmpty()) {
      return null;
    } else {
      return Util.randomOf(starts);
    }
  }

  public Spell parseSpell(BlockPos rootPos, Direction forwards, Glyph parentGlyph, LinkedHashSet<BlockPos> blocks, int depth) {
    var spell = new Spell(parentGlyph.morpheme(), new ArrayList<>(), rootPos, forwards);
    var forwardsTransform = transform.withForwards(forwards);

    blocks.addAll(parentGlyph.blocks(rootPos, forwardsTransform));

    if (depth >= 100) {
      return spell;
    }

    for (var connector : parentGlyph.getConnectors(rootPos, forwardsTransform)) {
      if (depth > 0 && connector.dir().getOpposite() == forwards) {
        continue;
      }

      if (ink.getBlock().canAttach(world.getBlockState(connector.pos()), transform)) {
        var localTransform = transform.withForwards(connector.dir());
        var glyphPos = connector.pos().relative(localTransform.forwards());

        for (var glyph : Glyph.GLYPHS) {
          if (glyph.test(world, glyphPos, localTransform, ink.getBlock())) {
            blocks.add(connector.pos());
            spell.connected().add(parseSpell(glyphPos, connector.dir(), glyph, blocks, depth + 1));
            break;
          }
        }
      }
    }

    return spell;
  }

  public void handleInvalidBlocks(Set<BlockPos> connected, Set<BlockPos> blocks) {
    for (var block : connected) {
      if (!blocks.contains(block)) {
        ink.handleInvalidBlock(this, block);
      }
    }
  }
}
