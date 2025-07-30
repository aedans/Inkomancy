package hans.inkomancy;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

import java.util.*;

public record SpellWriter(Random random, Level world) {
  public boolean addGlyphs(Spell spell, BlockPos rootPos, Transform2D transform, LinkedList<PositionedGlyph> glyphs) {
    var glyph = randomGlyph(spell.morpheme());
    var box = glyph.box(rootPos, transform);

    if (intersects(box, glyphs)) {
      return false;
    }

    for (var block : glyph.blocks(rootPos, transform)) {
      if (!world.isEmptyBlock(block) || InkBlock.isInvalidPlacement(world, block, transform.facing())) {
        return false;
      }
    }

    var positionedGlyph = new PositionedGlyph(rootPos, transform.forwards(), glyph, box.inflatedBy(1));
    glyphs.push(positionedGlyph);

    var connectors = glyph.getConnectors(rootPos, transform);
    Collections.shuffle(connectors);
    for (var connected : spell.connected()) {
      var canConnect = false;

      for (var connector : connectors) {
        var newTransform = transform.withForwards(connector.dir());
        var newRootPos = connector.pos().relative(newTransform.forwards());
        if (addGlyphs(connected, newRootPos, newTransform, glyphs)) {
          canConnect = true;
          break;
        }
      }

      if (!canConnect) {
        while (glyphs.peek() != positionedGlyph) {
          glyphs.pop();
        }
        glyphs.pop();
        return false;
      }
    }

    return true;
  }

  public List<BlockPos> getBlocks(List<PositionedGlyph> glyphs, Transform2D transform) {
    var blocks = new ArrayList<BlockPos>();

    for (var glyph : glyphs) {
      blocks.addAll(glyph.glyph().blocks(glyph.pos(), transform.withForwards(glyph.dir())));

      if (glyph.glyph() != Glyph.START) {
        blocks.add(glyph.pos().relative(glyph.dir().getOpposite()));
      }
    }

    return blocks;
  }

  public boolean intersects(BoundingBox box, List<PositionedGlyph> bounds) {
    for (var bound : bounds) {
      if (box.intersects(bound.box())) {
        return true;
      }
    }

    return false;
  }

  public Glyph randomGlyph(Morpheme morpheme) {
    return Util.randomOf(Glyph.GLYPH_MAP.get(morpheme));
  }

  public record PositionedGlyph(BlockPos pos, Direction dir, Glyph glyph, BoundingBox box) {
  }
}
