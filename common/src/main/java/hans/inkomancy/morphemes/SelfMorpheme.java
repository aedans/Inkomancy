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

  // With an argument (e.g. self[hole]), self re-scopes to the players found there rather than the
  // caster; an empty result means an argument was given but held no players. Bare self is the caster.
  @Override
  public List<? extends Delegate<? extends Entity>> interpretAsEntities(Spell spell, SpellContext context) throws InterpretError {
    if (spell.connected().isEmpty()) {
      return List.of(new Delegate.Instance<>(context.caster()));
    }
    return new Args(spell, context).getFlat(Type.ENTITIES, m -> m::interpretAsEntities)
        .filter(e -> e.get() instanceof Player)
        .toList();
  }

  @Override
  public List<Position> interpretAsPositions(Spell spell, SpellContext context) throws InterpretError {
    if (spell.connected().isEmpty()) {
      return List.of(new Position(Objects.requireNonNull(context.caster()).position()));
    }
    return interpretAsEntities(spell, context).stream().map(p -> new Position(p.get().position())).toList();
  }
}
