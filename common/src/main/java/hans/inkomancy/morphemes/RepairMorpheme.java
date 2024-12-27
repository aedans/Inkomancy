package hans.inkomancy.morphemes;

import hans.inkomancy.InterpretError;
import hans.inkomancy.Morpheme;
import hans.inkomancy.Spell;
import hans.inkomancy.SpellContext;
import net.minecraft.sounds.SoundEvents;

import java.util.List;
import java.util.Set;

public class RepairMorpheme extends Morpheme {
  public static final RepairMorpheme INSTANCE = new RepairMorpheme();

  private RepairMorpheme() {
    super("repair", Set.of(Type.ACTION));
  }

  @Override
  public void interpretAsAction(Spell spell, SpellContext context) throws InterpretError {
    var items = new Args(spell, context).get(Type.ITEMS, m -> m::interpretAsItems)
        .stream().flatMap(List::stream).toList();
    for (var item : items) {
      if (item.get().isDamaged()) {
        context.world().playSound(context.getPosition(spell), SoundEvents.ANVIL_USE);
        var toRepair = Math.min(item.get().getDamageValue(), context.mana().current);
        context.mana().consume(toRepair);
        item.update(stack -> stack.setDamageValue(stack.getDamageValue() - toRepair));
      }
    }
  }
}
