package hans.inkomancy.morphemes;

import hans.inkomancy.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

// Turns each input position into the 3x3x3 box of positions centered on it, so a spell can act on a
// region without hand-building corners out of `direction` offsets (see the hammer's on-break spell).
public class ExpandMorpheme extends Morpheme {
  public static final ExpandMorpheme INSTANCE = new ExpandMorpheme();

  private ExpandMorpheme() {
    super("expand", Set.of(Type.POSITION));
  }

  @Override
  public List<Position> interpretAsPositions(Spell spell, SpellContext context) throws InterpretError {
    var centers = new Args(spell, context).getFlat(Type.POSITION, m -> m::interpretAsPositions).toList();
    var positions = new ArrayList<Position>();
    for (var center : centers) {
      var c = center.blockPos();
      for (int x = -1; x <= 1; x++) {
        for (int y = -1; y <= 1; y++) {
          for (int z = -1; z <= 1; z++) {
            positions.add(new Position(c.offset(x, y, z)));
          }
        }
      }
    }
    return positions;
  }
}
