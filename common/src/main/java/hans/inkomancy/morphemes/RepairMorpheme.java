package hans.inkomancy.morphemes;

import hans.inkomancy.*;

import java.util.Set;

public class RepairMorpheme extends Morpheme {
  public static final RepairMorpheme INSTANCE = new RepairMorpheme();

  private RepairMorpheme() {
    super("repair", Set.of(Type.ACTION));
  }

  @Override
  public void interpretAsAction(Spell spell, SpellContext context, boolean undo) throws InterpretError {
    var items = new Args(spell, context).getFlat(Type.ITEMS, m -> m::interpretAsItems).toList();
    for (var item : items) {
      if (item.get().isDamaged()) {
        EffectUtils.repairEffect(context.world(), context.getPosition(spell, 1));
        var toRepair = Math.min(item.get().getDamageValue(), context.mana().current);
        context.mana().consume(toRepair);
        item.update(stack -> stack.setDamageValue(stack.getDamageValue() - toRepair));
      }
    }
  }
}
