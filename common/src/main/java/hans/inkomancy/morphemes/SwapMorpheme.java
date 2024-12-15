package hans.inkomancy.morphemes;

import hans.inkomancy.*;
import net.minecraft.world.level.portal.TeleportTransition;

import java.util.List;
import java.util.Set;

public class SwapMorpheme extends Morpheme {
  public static final SwapMorpheme UP = new SwapMorpheme(Vertical.UP);
  public static final SwapMorpheme DOWN = new SwapMorpheme(Vertical.DOWN);

  public final Vertical vertical;

  private SwapMorpheme(Vertical vertical) {
    super("swap", Set.of(Type.ACTION));
    this.vertical = vertical;
  }

  @Override
  public void interpretAsAction(Spell spell, SpellContext context) throws InterpretError {
    var spawn = context.caster().getRespawnPosition();
    if (spawn == null) {
      spawn = context.caster().level().getSharedSpawnPos();
    }

    var sources = getArg(spell, context, vertical == Vertical.UP ? 0 : 1, List.of(Delegate.of(context.caster())), m -> m::interpretAsEntities);
    var targets = getArg(spell, context, vertical == Vertical.UP ? 1 : 0, List.of(new Position(spawn)), m -> m::interpretAsPositions);

    for (var source : sources) {
      var target = Util.randomOf(targets).absolute().add(0, 1, 0);
      var distance = source.get().blockPosition().distToCenterSqr(target);
      context.mana().consume((int) distance);
      source.update(entity ->
          EffectUtils.teleport(context.world(), entity,
              new TeleportTransition(context.world(), target, entity.getDeltaMovement(), entity.getYRot(), entity.getXRot(), TeleportTransition.DO_NOTHING)));
    }
  }

  public enum Vertical {
    UP, DOWN
  }
}
