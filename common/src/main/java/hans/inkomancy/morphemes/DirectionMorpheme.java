package hans.inkomancy.morphemes;

import hans.inkomancy.InterpretError;
import hans.inkomancy.Morpheme;
import hans.inkomancy.Spell;
import hans.inkomancy.SpellContext;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public class DirectionMorpheme extends Morpheme {
  public final int x;
  public final int y;

  public DirectionMorpheme(String name, int x, int y) {
    super("direction-" + name, Set.of(Type.POSITION));
    this.x = x;
    this.y = y;
  }

  @Override
  public List<Position> interpretAsPositions(Spell spell, SpellContext context) throws InterpretError {
    var positions = new Args(spell, context).get(Type.POSITION, m -> m::interpretAsPositions).stream().flatMap(Collection::stream).toList();
    var facing = context.caster() != null ? context.caster().getDirection() : spell.dir() != null ? spell.dir() : Direction.NORTH;
    return positions.stream().map(pos -> {
      Vec3 newPos = pos.absolute();

      if (x < 0) {
        newPos = newPos.add(facing.getCounterClockWise().getUnitVec3());
      } else if (x > 0) {
        newPos = newPos.add(facing.getClockWise().getUnitVec3());
      }

      if (y > 0) {
        newPos = newPos.add(facing.getUnitVec3());
      } else if (y < 0) {
        newPos = newPos.add(facing.getOpposite().getUnitVec3());
      }

      return new Position(newPos);
    }).toList();
  }
}
