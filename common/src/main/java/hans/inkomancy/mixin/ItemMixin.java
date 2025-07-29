package hans.inkomancy.mixin;

import hans.inkomancy.Inkomancy;
import hans.inkomancy.MagicItem;
import hans.inkomancy.ManaProvider;
import hans.inkomancy.SpellContext;
import hans.inkomancy.inks.VoidInk;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
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
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Set;

@Mixin(Item.class)
public abstract class ItemMixin {
  @Shadow
  protected static BlockHitResult getPlayerPOVHitResult(Level level, Player player, ClipContext.Fluid fluid) {
    throw new NotImplementedException();
  }

  @Unique
  private boolean inkomancy$isMagicItem(ItemStack stack) {
    return stack.getItem() instanceof MagicItem || stack.has(DataComponents.DAMAGE);
  }

  @Unique
  private boolean inkomancy$tryUseSpell(Player playerEntity, ItemStack stack, BlockPos pos) {
    if (playerEntity.level() instanceof ServerLevel server && playerEntity instanceof ServerPlayer player) {
      var spell = stack.get(Inkomancy.SPELL_COMPONENT_TYPE.get());
      if (spell != null) {
        var mana = new ManaProvider(VoidInk.INSTANCE, VoidInk.INSTANCE.getMana(Set.of()));
        var context = new SpellContext(server, player, pos, VoidInk.INSTANCE, mana);
        spell.morpheme().interpret(spell.base(), context);
        return true;
      }
    }

    return false;
  }

  @Inject(at = @At("TAIL"), method = "use", cancellable = true)
  public void use(Level level, Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
    if (!inkomancy$isMagicItem(player.getItemInHand(hand))) {
      return;
    }

    var blockHitResult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.NONE);
    if (blockHitResult.getType() == HitResult.Type.MISS && inkomancy$tryUseSpell(player, player.getItemInHand(hand), null)) {
      cir.setReturnValue(InteractionResult.SUCCESS_SERVER);
    }
  }

  @Inject(at = @At("TAIL"), method = "useOn", cancellable = true)
  public void useOn(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir) {
    if (!inkomancy$isMagicItem(context.getItemInHand())) {
      return;
    }

    if (context.getPlayer() != null && inkomancy$tryUseSpell(context.getPlayer(), context.getItemInHand(), context.getClickedPos())) {
      cir.setReturnValue(InteractionResult.SUCCESS_SERVER);
    }
  }

  @Inject(at = @At("HEAD"), method = "finishUsingItem")
  public void finishUsingItem(ItemStack itemStack, Level level, LivingEntity livingEntity, CallbackInfoReturnable<ItemStack> cir) {
    if (itemStack.has(DataComponents.CONSUMABLE) && livingEntity instanceof Player player) {
      inkomancy$tryUseSpell(player, itemStack, null);
    }
  }
}
