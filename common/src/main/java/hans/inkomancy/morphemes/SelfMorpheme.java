package hans.inkomancy.morphemes;

import hans.inkomancy.Delegate;
import hans.inkomancy.Morpheme;
import hans.inkomancy.Spell;
import hans.inkomancy.SpellContext;
import net.minecraft.world.entity.Entity;

import java.util.List;
import java.util.Set;

public class SelfMorpheme extends Morpheme {
  public static final SelfMorpheme INSTANCE = new SelfMorpheme();

  private SelfMorpheme() {
    super("self", Set.of(Type.ENTITIES));
  }

  @Override
  public List<? extends Delegate<? extends Entity>> interpretAsEntities(Spell spell, SpellContext context) {
    return List.of(Delegate.of(context.caster()));
  }
}
