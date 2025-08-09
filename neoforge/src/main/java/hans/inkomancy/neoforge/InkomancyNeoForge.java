package hans.inkomancy.neoforge;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

import hans.inkomancy.Inkomancy;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

@Mod(Inkomancy.MOD_ID)
public final class InkomancyNeoForge {
  public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Inkomancy.MOD_ID);

  public static final Supplier<CreativeModeTab> INKOMANCY_TAB = CREATIVE_MODE_TAB.register("inkomancy_tab", () -> CreativeModeTab.builder()
      .title(Component.translatable("itemGroup." + Inkomancy.MOD_ID + ".inkomancy_tab"))
      .icon(() -> new ItemStack(Inkomancy.INKY.get()))
      .displayItems((params, output) -> {
        for (var item : Inkomancy.items()) {
          output.accept(item);
        }
      })
      .build()
  );

  public InkomancyNeoForge(IEventBus bus) {
    // Run our common setup.
    Inkomancy.init();

    CREATIVE_MODE_TAB.register(bus);
  }
}
