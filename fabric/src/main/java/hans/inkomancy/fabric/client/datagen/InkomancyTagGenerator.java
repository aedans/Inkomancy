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

public class InkomancyTagGenerator extends FabricTagProvider<Item> {
  public static final TagKey<Item> BLACK_INK_TOOL_MATERIALS = TagKey.create(Registries.ITEM,
      ResourceLocation.fromNamespaceAndPath(Inkomancy.MOD_ID, "black_ink_tool_materials"));
  public static final TagKey<Item> RED_INK_TOOL_MATERIALS = TagKey.create(Registries.ITEM,
      ResourceLocation.fromNamespaceAndPath(Inkomancy.MOD_ID, "red_ink_tool_materials"));
  public static final TagKey<Item> VOID_INK_TOOL_MATERIALS = TagKey.create(Registries.ITEM,
      ResourceLocation.fromNamespaceAndPath(Inkomancy.MOD_ID, "void_ink_tool_materials"));

  public InkomancyTagGenerator(FabricDataOutput output,
                               CompletableFuture<HolderLookup.Provider> registriesFuture) {
    super(output, Registries.ITEM, registriesFuture);
  }

  @Override
  protected void addTags(HolderLookup.Provider provider) {
    getOrCreateTagBuilder(BLACK_INK_TOOL_MATERIALS);
    getOrCreateTagBuilder(RED_INK_TOOL_MATERIALS);
    getOrCreateTagBuilder(VOID_INK_TOOL_MATERIALS);
  }
}
