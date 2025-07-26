package hans.inkomancy.mixin;

import hans.inkomancy.Inkomancy;
import hans.inkomancy.ManaProvider;
import hans.inkomancy.SpellContext;
import hans.inkomancy.inks.VoidInk;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentHolder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Set;
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

  @Inject(method = "mineBlock", at = @At("HEAD"))
  public void mineBlock(Level level, BlockState blockState, BlockPos blockPos, Player player, CallbackInfo ci) {
    if (this.getItem() instanceof DiggerItem && level instanceof ServerLevel serverLevel && player instanceof ServerPlayer serverPlayer) {
      var spell = this.get(Inkomancy.SPELL_COMPONENT_TYPE.get());
      if (spell != null) {
        var mana = new ManaProvider(VoidInk.INSTANCE, VoidInk.INSTANCE.getMana(Set.of()));
        var context = new SpellContext(serverLevel, serverPlayer, blockPos, VoidInk.INSTANCE, mana);
        spell.morpheme().interpret(spell.base(), context);
      }
    }
  }
}