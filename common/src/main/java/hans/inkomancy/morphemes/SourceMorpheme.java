package hans.inkomancy.morphemes;

import hans.inkomancy.*;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SourceMorpheme extends Morpheme {
  // Root of an on-cast (right-click) spell: activates its children's actions.
  public static final SourceMorpheme CAST = new SourceMorpheme("source", Type.ACTION);
  // Root of an on-break spell: produces the items that replace a block's drops.
  public static final SourceMorpheme BREAK = new SourceMorpheme("sink", Type.ITEMS);

  private SourceMorpheme(String name, Type type) {
    super(name, Set.of(type));
  }

  @Override
  public void interpretAsAction(Spell spell, SpellContext context, boolean undo) throws InterpretError {
    for (var s : spell.connected()) {
      if (s.morpheme().supported.contains(Type.ACTION)) {
        s.morpheme().interpretAsAction(s, context, undo);
      }
    }
  }

  @Override
  public List<? extends Delegate<ItemStack>> interpretAsItems(Spell spell, SpellContext context) throws InterpretError {
    return new Args(spell, context).getFlat(Type.ITEMS, m -> m::interpretAsItems).collect(Collectors.toList());
  }
}
