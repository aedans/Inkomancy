package hans.inkomancy.morphemes;

import hans.inkomancy.*;
import net.minecraft.world.entity.Entity;

import java.util.List;
import java.util.Objects;
import java.util.Set;

public class SelfMorpheme extends Morpheme {
  public static final SelfMorpheme INSTANCE = new SelfMorpheme();

  private SelfMorpheme() {
    super("self", Set.of(Type.ENTITIES, Type.POSITION));
  }

  @Override
  public List<? extends Delegate<? extends Entity>> interpretAsEntities(Spell spell, SpellContext context) {
    return List.of(new Delegate.Instance<>(context.caster()));
  }

  @Override
  public List<Position> interpretAsPositions(Spell spell, SpellContext context) {
    return List.of(new Position(Objects.requireNonNull(context.caster()).position()));
  }
}
