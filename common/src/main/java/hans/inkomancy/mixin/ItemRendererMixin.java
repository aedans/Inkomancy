package hans.inkomancy.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import hans.inkomancy.Inkomancy;
import hans.inkomancy.InkomancyClient;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {
  @Shadow
  public abstract void renderStatic(LivingEntity arg, ItemStack arg2, ItemDisplayContext arg3, boolean bl, PoseStack arg4, MultiBufferSource arg5, Level arg6, int i, int j, int k);

  @Inject(
      method = "renderStatic(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/world/level/Level;III)V",
      at = @At("HEAD"),
      cancellable = true
  )
  public void renderStatic(
      @Nullable LivingEntity entity,
      ItemStack stack,
      ItemDisplayContext itemDisplayContext,
      boolean bl,
      PoseStack matrices,
      MultiBufferSource multiBufferSource,
      @Nullable Level level,
      int i,
      int j,
      int k,
      CallbackInfo info
  ) {
    if (Boolean.TRUE.equals(stack.get(Inkomancy.CONJURED_COMPONENT_TYPE.get()))) {
      MultiBufferSource finalVertexConsumers = multiBufferSource;
      multiBufferSource = layer -> {
        var delegate = finalVertexConsumers.getBuffer(layer);
        return new InkomancyClient.TransparentDelegateVertexConsumer(delegate);
      };
      stack = stack.copy();
      stack.remove(Inkomancy.CONJURED_COMPONENT_TYPE.get());
      this.renderStatic(entity, stack, itemDisplayContext, bl, matrices, multiBufferSource, level, i, j, k);
      info.cancel();
    }
  }
}
