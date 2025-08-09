package hans.inkomancy.fabric.client.datagen;

import hans.inkomancy.Inkomancy;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import java.util.concurrent.CompletableFuture;

public class InkomancyItemTagGenerator extends FabricTagProvider<Item> {
  public static final TagKey<Item> INK_TOOL_MATERIALS = TagKey.create(Registries.ITEM,
      ResourceLocation.fromNamespaceAndPath(Inkomancy.MOD_ID, "ink_tool_materials"));

  public InkomancyItemTagGenerator(FabricDataOutput output,
                                   CompletableFuture<HolderLookup.Provider> registriesFuture) {
    super(output, Registries.ITEM, registriesFuture);
  }

  @Override
  protected void addTags(HolderLookup.Provider provider) {
    getOrCreateTagBuilder(INK_TOOL_MATERIALS);
  }
}
