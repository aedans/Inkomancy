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

public record Glyph(Morpheme morpheme, int width, int height, int center, Value[] values) {
  public enum Value {
    FALSE,
    OPTIONAL,
    TRUE
  }

  public static final Glyph START = Glyph.create(SourceMorpheme.INSTANCE, 1, """
      +++
      +++
      +++
      """);

  public static final Glyph FORWARDS = Glyph.create(ContinueMorpheme.INSTANCE, 1, """
      _+_
      """);

  public static final Glyph LEFT = Glyph.create(ContinueMorpheme.INSTANCE, 0, """
      _?
      +_
      """);

  public static final Glyph RIGHT = Glyph.create(ContinueMorpheme.INSTANCE, 1, """
      ?_
      _+
      """);

  public static final Glyph ACTIVATE = Glyph.create(ContinueMorpheme.INSTANCE, 2, """
      ?_?_?
      _+_+_
      _+++_
      """);

  public static final List<Glyph> AMBIGUOUS = List.of(
      Glyph.create(DirectionMorpheme.FORWARDS_LEFT, 4, """
          ??_+++_
          ?_+++++
          _+__+__
          ++__+__
          +++++++
          ++__+__
          _+__+_?
          """),
      Glyph.create(DirectionMorpheme.FORWARDS_RIGHT, 2, """
          _+++_??
          +++++_?
          __+__+_
          __+__++
          +++++++
          __+__++
          ?_+__+_
          """),
      Glyph.create(DirectionMorpheme.BACKWARDS_LEFT, 4, """
          _+__+_?
          ++__+__
          +++++++
          ++__+__
          _+__+__
          ?_+++++
          ??_+++_
          """),
      Glyph.create(DirectionMorpheme.BACKWARDS_RIGHT, 2, """
          ?_+__+_
          __+__++
          +++++++
          __+__++
          __+__+_
          +++++_?
          _+++_??
          """),
      Glyph.create(DirectionMorpheme.FORWARDS, 2, """
          _+++_
          +++++
          __+__
          __+__
          +++++
          __+__
          ?_+_?
          """),
      Glyph.create(DirectionMorpheme.BACKWARDS, 2, """
          ?_+_?
          __+__
          +++++
          __+__
          __+__
          +++++
          _+++_
          """),
      Glyph.create(DirectionMorpheme.LEFT, 4, """
          _+__+_?
          ++__+__
          +++++++
          ++__+__
          _+__+_?
          """),
      Glyph.create(DirectionMorpheme.RIGHT, 2, """
          ?_+__+_
          __+__++
          +++++++
          __+__++
          ?_+__+_
          """),
      Glyph.create(UndoMorpheme.INSTANCE, 1, """
          ++++
          +__+
          +__+
          _+++
          """),
      Glyph.create(UndoMorpheme.INSTANCE, 2, """
          ++++
          +__+
          +__+
          +++_
          """),
      Glyph.create(SelfMorpheme.INSTANCE, 1, """
          _+_
          +++
          _+_
          """));

  public static final List<Glyph> GLYPHS = new ArrayList<>(List.of(
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
      Glyph.create(VoidMorpheme.INSTANCE, 1, """
          +_+
          +_+
          +_+
          +++
          """),
      Glyph.create(DirectionMorpheme.UP, 1, """
          ?_+_
          _++_
          +++_
          """),
      Glyph.create(DirectionMorpheme.DOWN, 2, """
          _+_?
          _++_
          _+++
          """),
      Glyph.create(GrowMorpheme.INSTANCE, 1, """
          _++
          ++_
          _+_
          """),
      Glyph.create(GrowMorpheme.INSTANCE, 1, """
          ++_
          _++
          _+_
          """),
      Glyph.create(RepairMorpheme.INSTANCE, 1, """
          __+
          +++
          ++_
          """),
      Glyph.create(RepairMorpheme.INSTANCE, 1, """
          +__
          +++
          _++
          """),
      Glyph.create(MatchMorpheme.INSTANCE, 1, """
          +++
          _+_
          +++
          """),
      Glyph.create(SwapMorpheme.INSTANCE, 1, """
          _++
          _+_
          ++_
          """),
      Glyph.create(SwapMorpheme.INSTANCE, 1, """
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
      Glyph.create(TransmuteMorpheme.INSTANCE, 1, """
          ++_
          _++
          _++
          """),
      Glyph.create(TransmuteMorpheme.INSTANCE, 1, """
          _++
          ++_
          ++_
          """),
      Glyph.create(HoleMorpheme.INSTANCE, 1, """
          +++
          +_+
          +++
          """),
      Glyph.create(BreakMorpheme.INSTANCE, 2, """
          +++
          +++
          _++
          """),
      Glyph.create(BreakMorpheme.INSTANCE, 0, """
          +++
          +++
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
          """)
  ));

  static {
    var manifestGlyphs = new ArrayList<Glyph>();

    for (var glyph : GLYPHS) {
      var width = glyph.width + 4;
      var height = glyph.height + 4;
      var values = new Value[width * height];
      for (var col = 0; col < width; col++) {
        for (var row = 0; row < height; row++) {
          var i = col + row * width;
          if (col == 0 || col == width - 1 || row == 0 || row == height - 1) {
            values[i] = Value.TRUE;
          } else if (col >= 2 && col <= width - 3 && row >= 2 && row <= height - 3) {
            values[i] = glyph.valueAt(row - 2, col - 2);
          } else {
            values[i] = Value.FALSE;
          }
        }
      }

      manifestGlyphs.add(new Glyph(new ManifestMorpheme(glyph.morpheme), width, height, glyph.center + 2, values));
    }

    GLYPHS.addAll(0, AMBIGUOUS);
    GLYPHS.addAll(0, manifestGlyphs);
    GLYPHS.add(START);
  }

  public static final Map<Morpheme, List<Glyph>> GLYPH_MAP = new HashMap<>();

  static {
    for (var glyph : GLYPHS) {
      var list = GLYPH_MAP.getOrDefault(glyph.morpheme, new ArrayList<>());
      list.add(glyph);
      GLYPH_MAP.put(glyph.morpheme, list);
    }
  }

  public Value valueAt(int row, int col) {
    return values[col + row * width];
  }

  public BlockPos localToBlockPos(BlockPos rootPos, Transform2D transform, int row, int col) {
    return rootPos.relative(transform.forwards(), row).relative(transform.right(), col - center);
  }

  public List<BlockPos> blocks(BlockPos rootPos, Transform2D transform) {
    var blocks = new ArrayList<BlockPos>();
    for (var col = 0; col < width; col++) {
      for (var row = 0; row < height; row++) {
        var value = valueAt(row, col);
        if (value == Value.TRUE) {
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
        var value = valueAt(row, col);
        if (value == Value.OPTIONAL) {
          continue;
        }

        if (block.canAttach(world.getBlockState(pos), transform) != (value == Value.TRUE)) {
          return false;
        }
      }
    }

    return true;
  }

  public List<Connector> getConnectors(BlockPos rootPos, Transform2D transform) {
    var list = new ArrayList<Connector>();

    for (var col = center; col >= 0; col--) {
      if (valueAt(0, col) == Value.TRUE) {
        list.add(new Connector(localToBlockPos(rootPos, transform, -1, col), transform.backwards()));
      }
    }

    for (var row = 0; row < height; row++) {
      if (valueAt(row, 0) == Value.TRUE) {
        list.add(new Connector(localToBlockPos(rootPos, transform, row, -1), transform.left()));
      }
    }

    for (var col = 0; col < width; col++) {
      if (valueAt(height - 1, col) == Value.TRUE) {
        list.add(new Connector(localToBlockPos(rootPos, transform, height, col), transform.forwards()));
      }
    }

    for (var row = height - 1; row >= 0; row--) {
      if (valueAt(row, width - 1) == Value.TRUE) {
        list.add(new Connector(localToBlockPos(rootPos, transform, row, width), transform.right()));
      }
    }

    for (var col = width - 1; col > center; col--) {
      if (valueAt(0, col) == Value.TRUE) {
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
    var values = new ArrayList<Value>();
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
        values.addFirst(chars[i] == '_' ? Value.FALSE : chars[i] == '+' ? Value.TRUE : Value.OPTIONAL);
      }

      height++;
    }

    return new Glyph(morpheme, length / height, height, center, values.toArray(Value[]::new));
  }
}
