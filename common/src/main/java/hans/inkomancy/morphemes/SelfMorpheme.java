package hans.inkomancy.morphemes;

import hans.inkomancy.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

import java.util.List;
import java.util.Objects;
import java.util.Set;

public class SelfMorpheme extends Morpheme {
  public static final SelfMorpheme INSTANCE = new SelfMorpheme();

  private SelfMorpheme() {
    super("self", Set.of(Type.ENTITIES, Type.POSITION));
  }

  @Override
  public List<? extends Delegate<? extends Entity>> interpretAsEntities(Spell spell, SpellContext context) throws InterpretError {
    var players = players(spell, context);
    if (players != null) {
      return players;
    }
    return List.of(new Delegate.Instance<>(context.caster()));
  }

  @Override
  public List<Position> interpretAsPositions(Spell spell, SpellContext context) throws InterpretError {
    var players = players(spell, context);
    if (players != null) {
      return players.stream().map(p -> new Position(p.get().position())).toList();
    }
    return List.of(new Position(Objects.requireNonNull(context.caster()).position()));
  }

  // With an entity-typed argument (e.g. self[hole]), self re-scopes to the players found in that
  // region rather than the caster. Returns null when no such argument is written, so callers fall
  // back to the caster; an empty list means an argument was given but held no players.
  private List<? extends Delegate<? extends Entity>> players(Spell spell, SpellContext context) throws InterpretError {
    if (spell.connected().stream().noneMatch(s -> s.morpheme().supported.contains(Type.ENTITIES))) {
      return null;
    }
    return new Args(spell, context).getFlat(Type.ENTITIES, m -> m::interpretAsEntities)
        .filter(e -> e.get() instanceof Player)
        .toList();
  }
}
