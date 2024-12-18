package hans.inkomancy;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;

public class EnchantmentUtils {
  public static ItemEnchantments getEnchantmentsComponent(ItemStack item) {
    var storedEnchantments = item.get(DataComponents.STORED_ENCHANTMENTS);
    if (storedEnchantments != null) {
      return storedEnchantments;
    }

    if (item.isEnchanted()) {
      return item.getEnchantments();
    }

    return null;
  }

  public static boolean canAddEnchantment(ItemStack item, Holder<Enchantment> enchantment) {
    return enchantment.value().canEnchant(item)
        || item.is(Items.BOOK)
        || item.is(Items.ENCHANTED_BOOK);
  }

  public static int newLevel(Holder<Enchantment> enchantment, int l1, int l2) {
    return Math.min(enchantment.value().getMaxLevel(), l1 + l2);
  }

  public static boolean isEnchanted(ItemStack item) {
    var enchantments = item.get(DataComponents.STORED_ENCHANTMENTS);
    return item.isEnchanted() || (enchantments != null && !enchantments.isEmpty());
  }

  public static ItemStack addEnchantment(ItemStack item, Holder<Enchantment> enchantment, int level) {
    if (item.is(Items.BOOK)) {
      var stack = Items.ENCHANTED_BOOK.getDefaultInstance();
      stack.enchant(enchantment, level);
      return stack;
    }

    var storedEnchantments = item.get(DataComponents.STORED_ENCHANTMENTS);
    if (storedEnchantments != null) {
      var builder = new ItemEnchantments.Mutable(storedEnchantments);
      builder.set(enchantment, newLevel(enchantment, storedEnchantments.getLevel(enchantment), level));
      item.set(DataComponents.STORED_ENCHANTMENTS, builder.toImmutable());
      return item;
    }

    item.enchant(enchantment, newLevel(enchantment, item.getEnchantments().getLevel(enchantment), level));
    return item;
  }

  public static ItemStack setEnchantments(ItemStack item, ItemEnchantments modified) {
    if (item.is(Items.BOOK)) {
      var stack = Items.ENCHANTED_BOOK.getDefaultInstance();
      stack.set(DataComponents.STORED_ENCHANTMENTS, modified);
      return stack;
    }

    var storedEnchantments = item.get(DataComponents.STORED_ENCHANTMENTS);
    if (storedEnchantments != null) {
      if (modified.equals(ItemEnchantments.EMPTY)) {
        return Items.BOOK.getDefaultInstance();
      } else {
        item.set(DataComponents.STORED_ENCHANTMENTS, modified);
        return item;
      }
    }

    if (item.isEnchanted()) {
      item.set(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
    }

    for (var enchantment : modified.keySet()) {
      item.enchant(enchantment, modified.getLevel(enchantment));
    }

    return item;
  }
}
