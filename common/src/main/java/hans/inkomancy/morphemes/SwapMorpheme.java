package hans.inkomancy.morphemes;

import hans.inkomancy.*;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.portal.TeleportTransition;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SwapMorpheme extends Morpheme {
  public static final SwapMorpheme INSTANCE = new SwapMorpheme();

  private SwapMorpheme() {
    super("swap", Set.of(Type.ACTION));
  }

  @Override
  public void interpretAsAction(Spell spell, SpellContext context) throws InterpretError {
    var args = new Args(spell, context);
    List<Delegate<? extends Entity>> sources = args.get(Type.ENTITIES, m -> m::interpretAsEntities)
        .stream().flatMap(List::stream).collect(Collectors.toList());
    var targets = args.get(Type.POSITION, m -> m::interpretAsPositions)
        .stream().flatMap(List::stream).collect(Collectors.toList());

    if (sources.isEmpty() && context.caster() != null) {
      sources.add(Delegate.of(context.caster()));
    }

    if (targets.isEmpty()) {
      BlockPos spawn = null;
      if (context.caster() != null) {
        spawn = context.caster().getRespawnPosition();
      }

      if (spawn == null) {
        spawn = context.world().getSharedSpawnPos();
      }

      targets.add(new Position(spawn));
    }

    for (var source : sources) {
      var target = Util.randomOf(targets).absolute().add(0, 1, 0);
      var distance = Math.sqrt(source.get().blockPosition().distToCenterSqr(target));
      context.mana().consume((int) distance);
      source.update(entity ->
          EffectUtils.teleport(context.world(), entity,
              new TeleportTransition(context.world(), target, entity.getDeltaMovement(), entity.getYRot(), entity.getXRot(), TeleportTransition.DO_NOTHING)));
    }
  }
}
