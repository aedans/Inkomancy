package hans.inkomancy.morphemes;

import hans.inkomancy.*;
import net.minecraft.world.item.ItemStack;

import java.util.*;
import java.util.stream.Collectors;

public class MatchMorpheme extends Morpheme {
  public static final MatchMorpheme INSTANCE = new MatchMorpheme();

  private MatchMorpheme() {
    super("match", Set.of(Type.ITEMS, Type.ACTION));
  }

  @Override
  public List<? extends Delegate<ItemStack>> interpretAsItems(Spell spell, SpellContext context) throws InterpretError {
    var inputs = new Args(spell, context).get(Type.ITEMS, x -> x::interpretAsItems);
    var items = inputs.getFirst().stream().map(d -> d.get().getItem()).collect(Collectors.toSet());
    for (var input : inputs.subList(1, inputs.size())) {
      items.retainAll(input.stream().map(d -> d.get().getItem()).toList());
    }

    return inputs.stream().flatMap(Collection::stream).filter(d -> items.contains(d.get().getItem())).toList();
  }

  @Override
  public void interpretAsAction(Spell spell, SpellContext context) throws InterpretError {
    for (var delegate : interpretAsItems(spell, context)) {
      delegate.action(false);
    }
  }
}
