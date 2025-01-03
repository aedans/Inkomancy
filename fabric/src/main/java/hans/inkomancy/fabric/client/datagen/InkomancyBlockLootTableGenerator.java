package hans.inkomancy.fabric.client.datagen;

import hans.inkomancy.Ink;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.core.HolderLookup;

import java.util.concurrent.CompletableFuture;

public class InkomancyBlockLootTableGenerator extends FabricBlockLootTableProvider {
  protected InkomancyBlockLootTableGenerator(FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
    super(dataOutput, registryLookup);
  }

  @Override
  public void generate() {
    for (var ink : Ink.getInks()) {
      add(ink.getBlock(), this.createSingleItemTable(ink.getItem()));
    }
  }
}
