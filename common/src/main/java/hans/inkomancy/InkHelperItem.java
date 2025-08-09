package hans.inkomancy;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class InkHelperItem extends Item {
  public InkHelperItem(Item.Properties settings) {
    super(settings);
  }

  public static boolean hasHelper(ItemStack stack) {
    return stack.getEntityRepresentation() instanceof Player player
        && player.getInventory().contains(x -> x.getItem() == Inkomancy.INKY.get());
  }
}
