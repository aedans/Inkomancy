package hans.inkomancy.morphemes;

import hans.inkomancy.*;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ReadMorpheme extends Morpheme {
  public static final ReadMorpheme INSTANCE = new ReadMorpheme();

  private ReadMorpheme() {
    super("read", Set.of(Type.SPELL, Type.ITEMS, Type.POSITION));
  }

  @Override
  public Spell interpretAsSpell(Spell spell, SpellContext context) {
    return new Spell(SourceMorpheme.INSTANCE, spell.connected()).base();
  }

  @Override
  public List<? extends Delegate<ItemStack>> interpretAsItems(Spell spell, SpellContext context) throws InterpretError {
    var items = new ArrayList<Delegate<ItemStack>>();
    for (var s : spell.connected()) {
      items.addAll(s.morpheme().interpretAsItems(s, context));
    }
    return items.stream().map(x -> new Delegate.Instance<>(x.get(), false)).toList();
  }

  @Override
  public List<Position> interpretAsPositions(Spell spell, SpellContext context) throws InterpretError {
    var positions = new ArrayList<Position>();
    for (var s : spell.connected()) {
      positions.addAll(s.morpheme().interpretAsPositions(s, context));
    }
    return positions;
  }
}
