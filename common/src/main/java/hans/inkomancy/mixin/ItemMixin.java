package hans.inkomancy.mixin;

import hans.inkomancy.MagicItem;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.apache.commons.lang3.NotImplementedException;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public abstract class ItemMixin {
  @Shadow
  protected static BlockHitResult getPlayerPOVHitResult(Level level, Player player, ClipContext.Fluid fluid) {
    throw new NotImplementedException();
  }

  @Inject(at = @At("TAIL"), method = "use", cancellable = true)
  public void use(Level level, Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
    if (!MagicItem.isMagicItem(player.getItemInHand(hand))) {
      return;
    }

    var blockHitResult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.NONE);
    if (blockHitResult.getType() == HitResult.Type.MISS && MagicItem.tryUseSpell(player, player.getItemInHand(hand), null)) {
      cir.setReturnValue(InteractionResult.SUCCESS_SERVER);
    }
  }

  @Inject(at = @At("TAIL"), method = "useOn", cancellable = true)
  public void useOn(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir) {
    if (!MagicItem.isMagicItem(context.getItemInHand())) {
      return;
    }

    if (context.getPlayer() != null && MagicItem.tryUseSpell(context.getPlayer(), context.getItemInHand(), context.getClickedPos())) {
      cir.setReturnValue(InteractionResult.SUCCESS_SERVER);
    }
  }

  @Inject(at = @At("HEAD"), method = "finishUsingItem")
  public void finishUsingItem(ItemStack itemStack, Level level, LivingEntity livingEntity, CallbackInfoReturnable<ItemStack> cir) {
    if (itemStack.has(DataComponents.CONSUMABLE) && livingEntity instanceof Player player) {
      MagicItem.tryUseSpell(player, itemStack, null);
    }
  }
}
