package hans.inkomancy.morphemes;

import hans.inkomancy.*;

import java.util.ArrayList;
import java.util.Set;

public class SwapMorpheme extends Morpheme {
  public static final SwapMorpheme INSTANCE = new SwapMorpheme();

  private SwapMorpheme() {
    super("swap", Set.of(Type.ACTION));
  }

  @Override
  public void interpretAsAction(Spell spell, SpellContext context) throws InterpretError {
    var args = new Args(spell, context);
    var positions = args.get(Type.POSITION, m -> m::interpretAsPositions);

    var sources = positions.isEmpty() ? new ArrayList<Position>() : positions.get(0);
    var targets = positions.size() < 2 ? new ArrayList<Position>() : positions.get(1);

    if (sources.isEmpty() && context.caster() != null) {
      sources.add(context.world().getPosition(context.caster()));
    }

    if (targets.isEmpty()) {
      targets.add(new Position(context.world().getSpawn(context.caster())));
    }

    for (var source : sources) {
      var target = Util.randomOf(targets).absolute().add(0, 1, 0);
      var distance = Math.sqrt(source.blockPos().distToCenterSqr(target));
      context.mana().consume((int) distance);
      context.world().teleport(source.blockPos(), target);
    }
  }
}
