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
  public static final DirectionMorpheme FORWARDS_LEFT = new DirectionMorpheme("forwards_left", -1, 1, 0);
  public static final DirectionMorpheme FORWARDS_RIGHT = new DirectionMorpheme("forwards_right", 1, 1, 0);
  public static final DirectionMorpheme BACKWARDS_LEFT = new DirectionMorpheme("backwards_left", -1, -1, 0);
  public static final DirectionMorpheme BACKWARDS_RIGHT = new DirectionMorpheme("backwards_right", 1, -1, 0);
  public static final DirectionMorpheme FORWARDS = new DirectionMorpheme("forwards", 0, 1, 0);
  public static final DirectionMorpheme BACKWARDS = new DirectionMorpheme("backwards", 0, -1, 0);
  public static final DirectionMorpheme LEFT = new DirectionMorpheme("left", -1, 0, 0);
  public static final DirectionMorpheme RIGHT = new DirectionMorpheme("right", 1, 0, 0);
  public static final DirectionMorpheme UP = new DirectionMorpheme("up", 0, 0, 1);
  public static final DirectionMorpheme DOWN = new DirectionMorpheme("down", 0, 0, -1);

  public final int x;
  public final int y;
  public final int z;

  public DirectionMorpheme(String name, int x, int y, int z) {
    super("direction_" + name, Set.of(Type.POSITION));
    this.x = x;
    this.y = y;
    this.z = z;
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

      if (z > 0) {
        newPos = newPos.add(0, 1, 0);
      } else if (z < 0) {
        newPos = newPos.add(0, -1, 0);
      }

      return new Position(newPos);
    }).toList();
  }
}
