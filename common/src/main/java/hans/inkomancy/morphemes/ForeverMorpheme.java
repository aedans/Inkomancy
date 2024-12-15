package hans.inkomancy.morphemes;

import hans.inkomancy.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.ItemEnchantments;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

public class ForeverMorpheme extends Morpheme {
  public static final ForeverMorpheme INSTANCE = new ForeverMorpheme();

  private ForeverMorpheme() {
    super("forever", Set.of(Type.ACTION));
  }

  @Override
  public void interpretAsAction(Spell spell, SpellContext context) throws InterpretError {
    var items = getArg(spell, context, 0, null, m -> m::interpretAsItems);
    var itemModifiers = getArg(spell, context, 1, null, m -> this::interpretAsItemModifiers);

    for (var item : items) {
      for (var modifier : itemModifiers) {
        context.mana().consume(256);
        if (modifier.apply(item)) {
          EffectUtils.enchantEffect(context.world(), spell.pos());
        }
      }
    }
  }

  public List<Function<Delegate<ItemStack>, Boolean>> interpretAsItemModifiers(Spell spell, SpellContext context)
      throws InterpretError {
    var modifiers = new ArrayList<Function<Delegate<ItemStack>, Boolean>>();
    if (spell.morpheme().supported.contains(Type.ITEMS)) {
      var items = spell.morpheme().interpretAsItems(spell, context);
      for (var from : items) {
        if (EnchantmentUtils.isEnchanted(from.get())) {
          modifiers.add(to -> transferEnchantments(from, to));
        }

        var s = from.get().get(Inkomancy.SPELL_COMPONENT_TYPE.get());
        if (s != null) {
          modifiers.add(to -> {
            to.update(item -> item.set(Inkomancy.SPELL_COMPONENT_TYPE.get(), s));
            from.update(item -> item.remove(Inkomancy.SPELL_COMPONENT_TYPE.get()));
            return true;
          });
        }
      }
    } else if (spell.morpheme().supported.contains(Type.SPELL)) {
      var s = spell.morpheme().interpretAsSpell(spell, context);
      modifiers.add(d -> {
        d.update(item -> item.set(Inkomancy.SPELL_COMPONENT_TYPE.get(), s));
        return true;
      });
    } else {
      throw new InterpretError.Conversion(spell.morpheme(), List.of(Type.ITEMS, Type.SPELL));
    }

    return modifiers;
  }

  public boolean transferEnchantments(Delegate<ItemStack> from, Delegate<ItemStack> to) {
    var enchantmentComponent = EnchantmentUtils.getEnchantmentsComponent(from.get());
    if (enchantmentComponent == null) {
      return false;
    }

    var builder = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);

    for (var enchantment : enchantmentComponent.keySet()) {
      var level = enchantmentComponent.getLevel(enchantment);
      if (EnchantmentUtils.canAddEnchantment(to.get(), enchantment)) {
        to.modify(item -> EnchantmentUtils.addEnchantment(item, enchantment, level));
      } else {
        builder.set(enchantment, level);
      }
    }

    var newEnchantmentComponent = builder.toImmutable();
    if (!enchantmentComponent.equals(newEnchantmentComponent)) {
      from.modify(item -> EnchantmentUtils.setEnchantments(item, newEnchantmentComponent));
      return true;
    }

    return false;
  }
}
