package hans.inkomancy.mixin;

import hans.inkomancy.Inkomancy;
import net.minecraft.core.component.DataComponentHolder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.function.Consumer;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements DataComponentHolder {
  @Shadow
  protected abstract <T extends TooltipProvider> void addToTooltip(DataComponentType<T> dataComponentType, Item.TooltipContext tooltipContext, Consumer<Component> consumer, TooltipFlag tooltipFlag);

  @Shadow
  public abstract void setEntityRepresentation(@Nullable Entity entity);

  @Shadow
  public abstract Item getItem();

  @Inject(at = @At("TAIL"), method = "addToTooltip")
  public <T extends TooltipProvider> void addToTooltip(
      DataComponentType<T> componentType, Item.TooltipContext context, Consumer<Component> textConsumer, TooltipFlag type,
      CallbackInfo info) {
    if (componentType == DataComponents.ENCHANTMENTS) {
      this.addToTooltip(Inkomancy.SPELL_COMPONENT_TYPE.get(), context, textConsumer, type);

      if (Boolean.TRUE.equals(this.get(Inkomancy.CONJURED_COMPONENT_TYPE.get()))) {
        textConsumer.accept(Component.literal("Conjured").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xFFAA00))));
      }
    }
  }

  @Inject(at = @At("HEAD"), method = "getTooltipLines")
  public void getTooltipLines(Item.TooltipContext context, @Nullable Player player, TooltipFlag type, CallbackInfoReturnable<List<Component>> cir) {
    this.setEntityRepresentation(player);
  }
}