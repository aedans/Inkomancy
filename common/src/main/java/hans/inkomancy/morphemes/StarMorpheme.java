package hans.inkomancy.morphemes;

import hans.inkomancy.InterpretError;
import hans.inkomancy.Morpheme;
import hans.inkomancy.Spell;
import hans.inkomancy.SpellContext;
import net.minecraft.sounds.SoundEvents;

import java.util.Set;

public class StarMorpheme extends Morpheme {
  public static final StarMorpheme INSTANCE = new StarMorpheme();

  private StarMorpheme() {
    super("star", Set.of(Type.ACTION));
  }

  @Override
  public void interpretAsAction(Spell spell, SpellContext context) throws InterpretError {
    if (context.caster() != null) {
      context.mana().consume(1);
      context.world().throwProjectile(context.caster());
      context.world().playSound(context.world().getPosition(context.caster()).blockPos(), SoundEvents.INK_SAC_USE);
    }
  }
}
