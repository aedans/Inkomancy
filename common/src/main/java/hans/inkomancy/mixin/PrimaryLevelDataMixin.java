package hans.inkomancy.mixin;

import com.mojang.serialization.Dynamic;
import com.mojang.serialization.Lifecycle;
import hans.inkomancy.InkomancyLevelData;
import hans.inkomancy.VoidContainer;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraft.world.level.storage.PrimaryLevelData;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(PrimaryLevelData.class)
public abstract class PrimaryLevelDataMixin implements InkomancyLevelData {
  @Unique
  public VoidContainer voidContainer = new VoidContainer();

  @Override
  public VoidContainer inkomancy$getVoidContainer() {
    return voidContainer;
  }

  @Inject(method = "parse", at = @At("RETURN"))
  private static <T> void parse(
      Dynamic<T> dynamic, LevelSettings levelSettings, @Deprecated PrimaryLevelData.SpecialWorldProperty specialWorldProperty, WorldOptions worldOptions, Lifecycle lifecycle, CallbackInfoReturnable<PrimaryLevelData> cir
  ) {
    var items = dynamic.get("InkomancyVoid").asList(ItemStack.CODEC::parse).stream().map(x -> x.result().orElse(null)).filter(Objects::nonNull).toList();
    for (var item : items) {
      ((InkomancyLevelData) cir.getReturnValue()).inkomancy$getVoidContainer().addItem(item);
    }
  }

  @Inject(method = "setTagData", at = @At("TAIL"))
  private void setTagData(RegistryAccess registryAccess, CompoundTag compoundTag, @Nullable CompoundTag compoundTag2, CallbackInfo ci) {
    var list = new ListTag();
    for (var item : voidContainer.getItems()) {
      if (!item.isEmpty()) {
        list.add(ItemStack.CODEC.encodeStart(NbtOps.INSTANCE, item).getOrThrow());
      }
    }
    compoundTag.put("InkomancyVoid", list);
  }
}
