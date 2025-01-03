package hans.inkomancy;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;

import java.util.List;

public class InkItem extends BlockItem {
  public InkItem(Block block, Properties properties) {
    super(block, properties);
  }

  @Override
  public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag type) {
    if (InkHelperItem.hasHelper(stack)) {
      tooltip.add(Component.literal(Ink.getBy(Ink::getItem, this).lore()).withStyle(Util.LORE_STYLE));
    }
  }
}
