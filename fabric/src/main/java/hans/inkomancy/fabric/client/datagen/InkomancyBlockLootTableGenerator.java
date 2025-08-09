package hans.inkomancy.fabric.client.datagen;

import hans.inkomancy.Ink;
import hans.inkomancy.Inkomancy;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.core.HolderLookup;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static hans.inkomancy.Inkomancy.*;

public class InkomancyBlockLootTableGenerator extends FabricBlockLootTableProvider {
  protected InkomancyBlockLootTableGenerator(FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
    super(dataOutput, registryLookup);
  }

  @Override
  public void generate() {
    for (var ink : Ink.getInks()) {
      for (var color : Inkomancy.COLORS) {
        add(ink.getBlock(color), this.createSingleItemTable(ink.getItem(color)));
      }
    }

    for (var block : List.of(FOG_LOG, STRIPPED_FOG_LOG, FOG_WOOD, STRIPPED_FOG_WOOD, FOG_PLANKS, FOG_PLANKS_ERODED)) {
      this.dropSelf(block.get());
    }

    this.add(FOG_LEAVES.get(), block -> this.createLeavesDrops(block, block, 0.000f, 0.000f, 0.000f, 0.000f));
  }
}
