package hans.inkomancy.morphemes;

import hans.inkomancy.Morpheme;
import hans.inkomancy.Spell;
import hans.inkomancy.SpellContext;

import java.util.List;
import java.util.Set;

public class MarkMorpheme extends Morpheme {
  public static final MarkMorpheme INSTANCE = new MarkMorpheme();

  public MarkMorpheme() {
    super("mark", Set.of(Type.POSITION));
  }

  @Override
  public List<Position> interpretAsPositions(Spell spell, SpellContext context) {
    return List.of(new Position(context.getPosition(spell, 0)));
  }
}
