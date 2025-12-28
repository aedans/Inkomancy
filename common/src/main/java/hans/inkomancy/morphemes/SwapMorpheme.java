package hans.inkomancy.morphemes;

import hans.inkomancy.*;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.portal.DimensionTransition;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class SwapMorpheme extends Morpheme {
  public static final SwapMorpheme INSTANCE = new SwapMorpheme();

  private SwapMorpheme() {
    super("swap", Set.of(Type.ACTION));
  }

  @Override
  public void interpretAsAction(Spell spell, SpellContext context, boolean undo) throws InterpretError {
    var args = new ArrayList<SwapArg>();
    for (var arg : spell.connected()) {
      args.addAll(interpretSwapArg(arg, context));
    }

    var sources = new ArrayList<SwapArg>();
    var destinations = new ArrayList<SwapArg>();
    for (var arg : args) {
      if (arg.entity() != null) {
        sources.add(arg);
      } else {
        destinations.add(arg);
      }
    }

    if (destinations.isEmpty() && sources.size() <= 1) {
      BlockPos spawn = null;
      if (context.caster() != null) {
        spawn = context.caster().getRespawnPosition();
      }

      if (spawn == null) {
        spawn = context.world().getSharedSpawnPos();
      }

      var center = spawn.getBottomCenter();
      destinations.add(new SwapArg() {
        @Override
        public Position pos() {
          return new Position(center.add(0, .5, 0));
        }

        @Override
        public @Nullable Delegate<? extends Entity> entity() {
          return null;
        }
      });
    }

    for (var source : sources) {
      var dest = Util.randomOf(destinations).pos().absolute();
      var distance = Math.sqrt(source.pos().blockPos().distToCenterSqr(dest));
      context.mana().consume((int) distance);
      Objects.requireNonNull(source.entity()).update(entity ->
          EffectUtils.teleport(context.world(), entity,
              new DimensionTransition(context.world(), dest, entity.getDeltaMovement(), entity.getYRot(), entity.getXRot(), DimensionTransition.DO_NOTHING)));
    }
  }

  private interface SwapArg {
    Position pos();

    @Nullable Delegate<? extends Entity> entity();
  }

  private List<? extends SwapArg> interpretSwapArg(Spell arg, SpellContext context) throws InterpretError {
    if (arg.morpheme().supported.contains(Type.ENTITIES)) {
      var entities = arg.morpheme().interpretAsEntities(arg, context).stream().map(entity -> new SwapArg() {
        @Override
        public Position pos() {
          return new Position(entity.get().position());
        }

        @Override
        public Delegate<? extends Entity> entity() {
          return entity;
        }
      }).toList();

      if (!entities.isEmpty()) {
        return entities;
      }
    }

    if (arg.morpheme().supported.contains(Type.POSITION)) {
      var positions = arg.morpheme().interpretAsPositions(arg, context).stream().map(position -> new SwapArg() {
        @Override
        public Position pos() {
          return position;
        }

        @Override
        public @Nullable Delegate<? extends Entity> entity() {
          return null;
        }
      }).toList();

      if (!positions.isEmpty()) {
        return positions;
      }
    }

    return List.of();
  }
}
