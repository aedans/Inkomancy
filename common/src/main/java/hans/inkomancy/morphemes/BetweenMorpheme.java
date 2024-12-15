package hans.inkomancy.morphemes;

import hans.inkomancy.*;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BetweenMorpheme extends Morpheme {
  public static final BetweenMorpheme INSTANCE = new BetweenMorpheme();

  protected BetweenMorpheme() {
    super("between", Set.of(Type.POSITION));
  }

  @Override
  public List<Position> interpretAsPositions(Spell spell, SpellContext context) throws InterpretError {
    var p1 = Util.randomOf(getArg(spell, context, 0, null, m -> m::interpretAsPositions));
    var p2 = Util.randomOf(getArg(spell, context, 1, null, m -> m::interpretAsPositions));
    var box = BoundingBox.fromCorners(p1.blockPos(), p2.blockPos());
    var positions = new ArrayList<Position>();
    for (int x = box.minX() + 1; x < box.maxX() - 1; x++) {
      for (int y = box.minY(); y < box.maxY(); y++) {
        for (int z = box.minZ() + 1; z < box.maxZ() - 1; z++) {
          positions.add(new Position(new BlockPos(x, y, z)));
        }
      }
    }
    return positions;
  }
}
