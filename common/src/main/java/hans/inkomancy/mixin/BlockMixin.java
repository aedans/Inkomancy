package hans.inkomancy.mixin;

import hans.inkomancy.*;
import hans.inkomancy.inks.VoidInk;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Mixin(Block.class)
public class BlockMixin {
  @Inject(at = @At("TAIL"), method = "getDrops(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/item/ItemStack;)Ljava/util/List;", cancellable = true)
  private static void getDrops(
      BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, @Nullable BlockEntity blockEntity, @Nullable Entity entity, ItemStack itemStack, CallbackInfoReturnable<List<ItemStack>> cir
  ) {
    if (MagicItem.isMagicItem(itemStack) && entity instanceof ServerPlayer player) {
      var spell = itemStack.get(Inkomancy.SPELL_COMPONENT_TYPE.get());
      if (spell != null) {
        var mana = new ManaProvider(VoidInk.INSTANCE, VoidInk.INSTANCE.getMana(Set.of()));
        var context = new SpellContext(serverLevel, player, VoidInk.INSTANCE, mana, blockPos, cir.getReturnValue().stream().map(Delegate.Instance::new).toList());
        try {
          var delegates = spell.morpheme().interpretAsItems(spell.base(), context);
          var items = new ArrayList<ItemStack>();
          for (var delegate : delegates) {
            if (delegate.get() != null) {
              items.add(delegate.get());
              delegate.destroy();
            }
          }
          cir.setReturnValue(items);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }
  }
}
