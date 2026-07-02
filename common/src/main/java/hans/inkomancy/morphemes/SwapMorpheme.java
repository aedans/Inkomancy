package hans.inkomancy.morphemes;

import hans.inkomancy.*;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.portal.DimensionTransition;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Set;

public class SwapMorpheme extends Morpheme {
  public static final SwapMorpheme INSTANCE = new SwapMorpheme();

  private SwapMorpheme() {
    super("swap", Set.of(Type.ACTION));
  }

  @Override
  public void interpretAsAction(Spell spell, SpellContext context, boolean undo) throws InterpretError {
    // Each child is a source (an entity) or a destination (a position). A child that could be both
    // resolves to whichever has content: entities first, else its position. `read` masks off
    // ENTITIES to force a destination (the entity-to-entity `swap[read[hole], hole]` case).
    var args = new Args(spell, context).getAny(
        Candidate.value(Type.ENTITIES, m -> m::interpretAsEntities, entity -> (SwapArg) new SwapArg() {
          @Override
          public Position pos() {
            return new Position(entity.get().position());
          }

          @Override
          public Delegate<? extends Entity> entity() {
            return entity;
          }
        }),
        Candidate.value(Type.POSITION, m -> m::interpretAsPositions, position -> (SwapArg) new SwapArg() {
          @Override
          public Position pos() {
            return position;
          }

          @Override
          public @Nullable Delegate<? extends Entity> entity() {
            return null;
          }
        })
    ).toList();

    // Every arg is a candidate landing spot; the ones that carry an entity are also the things that
    // move. Including each source's own position as a spot is what lets entities trade places:
    // swap[hole, hole] gives two entity spots and no fixed ones, so each entity's only non-self spot
    // is the other's — they swap. swap[read[hole], hole] adds a fixed position, so the lone entity's
    // only non-self spot is that position — it teleports there.
    var sources = new ArrayList<SwapArg>();
    var spots = new ArrayList<Position>();
    for (var arg : args) {
      spots.add(arg.pos());
      if (arg.entity() != null) {
        sources.add(arg);
      }
    }

    // With no fixed destination and at most one entity there's nothing to swap with, so send the lone
    // source home (its respawn point, else world spawn).
    var fixedDestinations = args.size() - sources.size();
    if (fixedDestinations == 0 && sources.size() <= 1) {
      BlockPos spawn = null;
      if (context.caster() != null) {
        spawn = context.caster().getRespawnPosition();
      }

      if (spawn == null) {
        spawn = context.world().getSharedSpawnPos();
      }

      spots.add(new Position(spawn.getBottomCenter().add(0, .5, 0)));
    }

    for (var source : sources) {
      var self = source.pos();
      var candidates = spots.stream().filter(spot -> !spot.equals(self)).toList();
      if (candidates.isEmpty()) {
        continue; // nowhere else to go (e.g. stacked entities) — leave it put
      }

      var dest = Util.randomOf(candidates).absolute();
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
}
