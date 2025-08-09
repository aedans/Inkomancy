package hans.inkomancy.morphemes;

import hans.inkomancy.*;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SourceMorpheme extends Morpheme {
  public static final SourceMorpheme INSTANCE = new SourceMorpheme();

  private SourceMorpheme() {
    super("source", Set.of(Type.ACTION, Type.ITEMS));
  }

  @Override
  public void interpretAsAction(Spell spell, SpellContext context, boolean undo) throws InterpretError {
    for (var s : spell.connected()) {
      if (s.morpheme().supported.contains(Type.ACTION)) {
        s.morpheme().interpretAsAction(s, context, undo);
      } else if (s.morpheme().supported.contains(Type.ITEMS)) {
        s.morpheme().interpretAsItems(s, context);
      }
    }
  }

  @Override
  public List<? extends Delegate<ItemStack>> interpretAsItems(Spell spell, SpellContext context) throws InterpretError {
    var items = new ArrayList<Delegate<ItemStack>>();
    for (var s : spell.connected()) {
      if (s.morpheme().supported.contains(Type.ITEMS)) {
        items.addAll(s.morpheme().interpretAsItems(s, context));
      }
    }

    return items;
  }
}
