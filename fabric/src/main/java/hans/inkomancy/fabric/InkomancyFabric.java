package hans.inkomancy.fabric;

import net.fabricmc.api.ModInitializer;

import hans.inkomancy.Inkomancy;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public final class InkomancyFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        // Run our common setup.
        Inkomancy.init();

        var tab = FabricItemGroup.builder()
            .icon(() -> new ItemStack(Inkomancy.VOID_INK_ITEM.get()))
            .title(Component.translatable("itemGroup.inkomancy_tab"))
            .build();

        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, Inkomancy.INKOMANCY_TAB_RL, tab);

        ItemGroupEvents.modifyEntriesEvent(Inkomancy.INKOMANCY_TAB_RK).register((group) -> {
            for (var item : Inkomancy.items()) {
                group.accept(item);
            }
        });
    }
}
