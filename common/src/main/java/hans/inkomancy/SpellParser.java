package hans.inkomancy;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;

import java.util.*;

public record SpellParser(ServerLevel world, Ink ink) {
  public Set<BlockPos> connectedBlocks(BlockPos initial, Transform2D transform) {
    var toScan = new LinkedHashSet<BlockPos>();
    var scanned = new HashSet<BlockPos>();
    var connected = new HashSet<BlockPos>();
    toScan.add(initial);

    while (!toScan.isEmpty()) {
      var current = toScan.removeFirst();
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
      var transform = Transform2D.of(world.getBlockState(current).getValue(InkBlock.FACING));
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

  public Spell parseSpell(BlockPos rootPos, Transform2D transform, Glyph parentGlyph, LinkedHashSet<BlockPos> blocks, int depth) {
    var spell = new Spell(parentGlyph.morpheme(), new ArrayList<>(), rootPos, transform.forwards());

    blocks.addAll(parentGlyph.blocks(rootPos, transform));

    if (parentGlyph == Glyph.ACTIVATE) {
      var activated = rootPos.relative(transform.forwards());
      var state = world.getBlockState(activated);
      if (state.is(ink.getBlock())) {
        var localTransform = Transform2D.of(state.getValue(InkBlock.FACING));
        spell.connected().addAll(parseSpell(activated.relative(transform.facing().getOpposite()), localTransform.withForwards(transform.facing()), Glyph.FORWARDS, blocks, depth + 1).connected());
        spell.connected().addAll(parseSpell(activated.relative(transform.facing()), localTransform.withForwards(transform.facing().getOpposite()), Glyph.FORWARDS, blocks, depth + 1).connected());
        return spell;
      }
    }

    for (var connector : parentGlyph.getConnectors(rootPos, transform)) {
      if (depth > 0 && connector.dir().getOpposite() == transform.forwards()) {
        continue;
      }

      if (ink.getBlock().canAttach(world.getBlockState(connector.pos()), transform)) {
        var localTransform = transform.withForwards(connector.dir());
        var glyphPos = connector.pos().relative(localTransform.forwards());
        var connectorBlocks = new ArrayList<BlockPos>();
        connectorBlocks.add(connector.pos());

        while (true) {
          if (Glyph.FORWARDS.test(world, glyphPos, localTransform, ink.getBlock())) {
            connectorBlocks.add(glyphPos);
            glyphPos = glyphPos.relative(localTransform.forwards());
          } else if (Glyph.LEFT.test(world, glyphPos, localTransform, ink.getBlock())) {
            connectorBlocks.add(glyphPos);
            localTransform = localTransform.withForwards(localTransform.left());
            glyphPos = glyphPos.relative(localTransform.forwards());
          } else if (Glyph.RIGHT.test(world, glyphPos, localTransform, ink.getBlock())) {
            connectorBlocks.add(glyphPos);
            localTransform = localTransform.withForwards(localTransform.right());
            glyphPos = glyphPos.relative(localTransform.forwards());
          } else {
            break;
          }
        }

        if (Glyph.ACTIVATE.test(world, glyphPos, localTransform, ink.getBlock())) {
          blocks.addAll(connectorBlocks);
          spell.connected().addAll(parseSpell(glyphPos, localTransform, Glyph.ACTIVATE, blocks, depth + 1).connected());
          continue;
        }

        for (var glyph : Glyph.GLYPHS) {
          if (glyph.test(world, glyphPos, localTransform, ink.getBlock())) {
            blocks.addAll(connectorBlocks);
            spell.connected().add(parseSpell(glyphPos, localTransform, glyph, blocks, depth + 1));
            break;
          }
        }
      }
    }

    return spell;
  }
}
