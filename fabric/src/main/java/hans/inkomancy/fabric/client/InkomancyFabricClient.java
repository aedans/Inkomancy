package hans.inkomancy.fabric.client;

import hans.inkomancy.Ink;
import hans.inkomancy.Inkomancy;
import hans.inkomancy.InkomancyClient;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.renderer.RenderType;

public final class InkomancyFabricClient implements ClientModInitializer {
  @Override
  public void onInitializeClient() {
    // This entrypoint is suitable for setting up client-specific logic, such as rendering.
    InkomancyClient.init();

    for (var ink : Ink.getInks()) {
      for (var color : Inkomancy.COLORS) {
        BlockRenderLayerMap.INSTANCE.putBlock(ink.getBlock(color), RenderType.translucent());
      }
    }
  }
}
