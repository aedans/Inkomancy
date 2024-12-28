package hans.inkomancy.morphemes;

import hans.inkomancy.*;

import java.util.List;
import java.util.Set;

public class SelfMorpheme extends Morpheme {
  public static final SelfMorpheme INSTANCE = new SelfMorpheme();

  private SelfMorpheme() {
    super("self", Set.of(Type.POSITION));
  }

  @Override
  public List<Position> interpretAsPositions(Spell spell, SpellContext context) {
    if (context.caster() != null) {
      return List.of(context.world().getPosition(context.caster()));
    } else {
      return List.of();
    }
  }
}
