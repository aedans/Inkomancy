package hans.inkomancy.morphemes;

import hans.inkomancy.*;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Set;

public class MatchMorpheme extends Morpheme {
  public static final MatchMorpheme INSTANCE = new MatchMorpheme();

  private MatchMorpheme() {
    super("match", Set.of(Type.ITEMS));
  }

  @Override
  public List<? extends Delegate<ItemStack>> interpretAsItems(Spell spell, SpellContext context) throws InterpretError {
    if (spell.connected().isEmpty()) {
      return List.of();
    }

    var s = spell.connected().getFirst();
    var items = s.morpheme().interpretAsItems(s, context);

    if (spell.connected().size() == 1) {
      return List.of();
    }

    return items;
  }
}
