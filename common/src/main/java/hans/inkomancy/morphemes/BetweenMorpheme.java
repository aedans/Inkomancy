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
    var ps = new Args(spell, context).get(Type.POSITION, m -> m::interpretAsPositions);
    var p1 = Util.randomOf(ps.get(0));
    var p2 = Util.randomOf(ps.get(1));
    var box = BoundingBox.fromCorners(p1.blockPos(), p2.blockPos());
    var positions = new ArrayList<Position>();
    for (int x = box.minX(); x <= box.maxX(); x++) {
      for (int y = box.minY(); y <= box.maxY(); y++) {
        for (int z = box.minZ(); z <= box.maxZ(); z++) {
          positions.add(new Position(new BlockPos(x, y, z)));
        }
      }
    }
    return positions;
  }
}
