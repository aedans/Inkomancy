package hans.inkomancy.fabric.client.datagen;

import hans.inkomancy.Inkomancy;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;

import java.util.concurrent.CompletableFuture;

public class InkomancyBlockTagGenerator extends FabricTagProvider<Block> {
  public InkomancyBlockTagGenerator(FabricDataOutput output,
                                    CompletableFuture<HolderLookup.Provider> registriesFuture) {
    super(output, Registries.BLOCK, registriesFuture);
  }

  @Override
  protected void addTags(HolderLookup.Provider provider) {
    getOrCreateTagBuilder(BlockTags.LOGS_THAT_BURN).add(Inkomancy.FOG_LOG.get(), Inkomancy.STRIPPED_FOG_LOG.get(), Inkomancy.FOG_WOOD.get(), Inkomancy.STRIPPED_FOG_WOOD.get());
    getOrCreateTagBuilder(BlockTags.PLANKS).add(Inkomancy.FOG_PLANKS.get(), Inkomancy.FOG_PLANKS_ERODED.get());
    getOrCreateTagBuilder(BlockTags.LEAVES).add(Inkomancy.FOG_LEAVES.get());
  }
}
