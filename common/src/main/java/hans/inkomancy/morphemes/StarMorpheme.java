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
  public void interpretAsAction(Spell spell, SpellContext context) throws InterpretError {
    context.mana().consume(1);
    ThrowableItemProjectile.spawnProjectileFromRotation(InkBallEntity::new, context.world(), Inkomancy.INK_BALL.get().getDefaultInstance(), context.caster(), 0, 1F, 1);
    EffectUtils.inkEffect(context.world(), context.caster().blockPosition());
  }
}
