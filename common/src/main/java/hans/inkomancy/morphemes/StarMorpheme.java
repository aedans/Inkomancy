package hans.inkomancy.morphemes;

import hans.inkomancy.*;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;

import java.util.Set;

public class StarMorpheme extends Morpheme {
  public static final StarMorpheme INSTANCE = new StarMorpheme();

  private StarMorpheme() {
    super("star", Set.of(Type.ACTION));
  }

  @Override
  public void interpretAsAction(Spell spell, SpellContext context, boolean undo) throws InterpretError {
    if (context.caster() != null) {
      context.mana().consume(1);
      var entity = new InkBallEntity(context.world(), context.caster());
      entity.shootFromRotation(context.caster(), context.caster().getXRot(), context.caster().getYRot(), 0, 1, 0);
      context.world().addFreshEntity(entity);
      EffectUtils.inkEffect(context.world(), context.caster().blockPosition());
    }
  }
}
