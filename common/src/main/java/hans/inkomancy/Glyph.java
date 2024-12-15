package hans.inkomancy;

import hans.inkomancy.morphemes.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record Glyph(Morpheme morpheme, int width, int height, int center, boolean[] values) {
  public static final Glyph START = Glyph.create(SourceMorpheme.INSTANCE, 1, """
      +_+
      +_+
      +++
      """);

  public static final Glyph[] GLYPHS = new Glyph[]{
      Glyph.create(BetweenMorpheme.INSTANCE, 0, """
          _++_
          ++_+
          +_++
          +++_
          """),
      Glyph.create(BetweenMorpheme.INSTANCE, 3, """
          _++_
          +_++
          ++_+
          _+++
          """),
      Glyph.create(GrowMorpheme.INSTANCE, 1, """
          _++
          ++_
          _+_
          """),
      Glyph.create(RepairMorpheme.INSTANCE, 1, """
          _++
          ++_
          ++_
          """),
      Glyph.create(SelfMorpheme.INSTANCE, 1, """
          _+_
          +++
          _+_
          """),
      Glyph.create(SwapMorpheme.UP, 1, """
          _++
          _+_
          ++_
          """),
      Glyph.create(SwapMorpheme.DOWN, 1, """
          ++_
          _+_
          _++
          """),
      Glyph.create(ForeverMorpheme.INSTANCE, 2, """
          ++_
          +++
          _++
          """),
      Glyph.create(ForeverMorpheme.INSTANCE, 0, """
          _++
          +++
          ++_
          """),
      Glyph.create(TransmuteMorpheme.SMELT, 1, """
          ++_
          +++
          _++
          """),
      Glyph.create(TransmuteMorpheme.CRAFT, 1, """
          _++
          +++
          ++_
          """),
      Glyph.create(VoidMorpheme.INSTANCE, 1, """
          +++
          +_+
          +++
          """),
      Glyph.create(BreakMorpheme.INSTANCE, 1, """
          +++
          +_+
          _++
          """),
      Glyph.create(BreakMorpheme.INSTANCE, 1, """
          +++
          +_+
          ++_
          """),
      Glyph.create(ToolMorpheme.INSTANCE, 1, """
          +++
          ++_
          """),
      Glyph.create(ToolMorpheme.INSTANCE, 1, """
          +++
          _++
          """),
      Glyph.create(StarMorpheme.INSTANCE, 1, """
          _+_
          +++
          """),
      Glyph.create(ReadMorpheme.INSTANCE, 1, """
          +++
          +++
          """),
      START,
  };

  public static final Map<Morpheme, List<Glyph>> GLYPH_MAP = new HashMap<>();

  static {
    for (var glyph : GLYPHS) {
      var list = GLYPH_MAP.getOrDefault(glyph.morpheme, new ArrayList<>());
      list.add(glyph);
      GLYPH_MAP.put(glyph.morpheme, list);
    }
  }

  public boolean valueAt(int row, int col) {
    return values[col + row * width];
  }

  public BlockPos localToBlockPos(BlockPos rootPos, Transform2D transform, int row, int col) {
    return rootPos.relative(transform.forwards(), row).relative(transform.right(), col - center);
  }

  public List<BlockPos> blocks(BlockPos rootPos, Transform2D transform) {
    var blocks = new ArrayList<BlockPos>();
    for (var col = 0; col < width; col++) {
      for (var row = 0; row < height; row++) {
        if (valueAt(row, col)) {
          blocks.add(localToBlockPos(rootPos, transform, row, col));
        }
      }
    }
    return blocks;
  }

  public boolean test(ServerLevel world, BlockPos rootPos, Transform2D transform, InkBlock block) {
    for (var col = 0; col < width; col++) {
      for (var row = 0; row < height; row++) {
        var pos = localToBlockPos(rootPos, transform, row, col);
        if (block.canAttach(world.getBlockState(pos), transform) != valueAt(row, col)) {
          return false;
        }
      }
    }

    return true;
  }

  public List<Connector> getConnectors(BlockPos rootPos, Transform2D transform) {
    var list = new ArrayList<Connector>();

    for (var col = center; col >= 0; col--) {
      if (valueAt(0, col)) {
        list.add(new Connector(localToBlockPos(rootPos, transform, -1, col), transform.backwards()));
      }
    }

    for (var row = 0; row < height; row++) {
      if (valueAt(row, 0)) {
        list.add(new Connector(localToBlockPos(rootPos, transform, row, -1), transform.left()));
      }
    }

    for (var col = 0; col < width; col++) {
      if (valueAt(height - 1, col)) {
        list.add(new Connector(localToBlockPos(rootPos, transform, height, col), transform.forwards()));
      }
    }

    for (var row = height - 1; row >= 0; row--) {
      if (valueAt(row, width - 1)) {
        list.add(new Connector(localToBlockPos(rootPos, transform, row, width), transform.right()));
      }
    }

    for (var col = width - 1; col > center; col--) {
      if (valueAt(0, col)) {
        list.add(new Connector(localToBlockPos(rootPos, transform, -1, col), transform.backwards()));
      }
    }

    return list;
  }

  public BoundingBox box(BlockPos rootPos, Transform2D transform2D) {
    return BoundingBox.fromCorners(localToBlockPos(rootPos, transform2D, 0, 0), localToBlockPos(rootPos, transform2D, height - 1, width - 1));
  }

  public record Connector(BlockPos pos, Direction dir) {
  }

  public static Glyph create(Morpheme morpheme, int center, String s) {
    var values = new ArrayList<Boolean>();
    var rows = s.split("\n");
    var height = 0;
    var length = 0;

    for (var row : rows) {
      if (row.isEmpty()) {
        continue;
      }

      var chars = row.toCharArray();
      for (var i = chars.length - 1; i >= 0; i--) {
        length++;
        values.addFirst(chars[i] == '+');
      }

      height++;
    }

    return new Glyph(morpheme, length / height, height, center, toPrimitiveArray(values));
  }

  // https://stackoverflow.com/a/5615737
  private static boolean[] toPrimitiveArray(final List<Boolean> booleanList) {
    final boolean[] primitives = new boolean[booleanList.size()];
    int index = 0;
    for (Boolean object : booleanList) {
      primitives[index++] = object;
    }
    return primitives;
  }
}
