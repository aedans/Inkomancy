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

import java.util.*;

@Mixin(PrimaryLevelData.class)
public abstract class PrimaryLevelDataMixin implements InkomancyLevelData {
  @Unique
  public Map<String, VoidContainer> inkomancy$voidContainers = new HashMap<>();

  @Override
  public VoidContainer inkomancy$getVoidContainer(List<String> colors) {
    var key = colors.stream().reduce((a, b) -> a + "-" + b).orElse("");
    if (!inkomancy$voidContainers.containsKey(key)) {
      inkomancy$voidContainers.put(key, new VoidContainer());
    }

    return inkomancy$voidContainers.get(key);
  }

  @Inject(method = "parse", at = @At("RETURN"))
  private static <T> void parse(
      Dynamic<T> dynamic, LevelSettings levelSettings, @Deprecated PrimaryLevelData.SpecialWorldProperty specialWorldProperty, WorldOptions worldOptions, Lifecycle lifecycle, CallbackInfoReturnable<PrimaryLevelData> cir
  ) {
    var voids = dynamic.get("InkomancyVoid").flatMap(CompoundTag.CODEC::parse).result().orElse(new CompoundTag());
    for (var inkomancyVoid : voids.getAllKeys()) {
      var items = (ListTag) voids.get(inkomancyVoid);
      if (items != null) {
        var container = ((InkomancyLevelData) cir.getReturnValue()).inkomancy$getVoidContainer(Arrays.stream(inkomancyVoid.split("-")).toList());
        for (var item : items) {
          container.addItem(ItemStack.CODEC.parse(NbtOps.INSTANCE, item).getOrThrow());
        }
      }
    }
  }

  @Inject(method = "setTagData", at = @At("TAIL"))
  private void setTagData(RegistryAccess registryAccess, CompoundTag compoundTag, @Nullable CompoundTag compoundTag2, CallbackInfo ci) {
    var inkomancyVoid = new CompoundTag();

    for (var key : inkomancy$voidContainers.keySet()) {
      var list = new ListTag();
      for (var item : inkomancy$voidContainers.get(key).getItems()) {
        if (!item.isEmpty()) {
          list.add(ItemStack.CODEC.encodeStart(NbtOps.INSTANCE, item).getOrThrow());
        }
      }

      if (!list.isEmpty()) {
        inkomancyVoid.put(key, list);
      }
    }

    compoundTag.put("InkomancyVoid", inkomancyVoid);
  }
}
