package hans.inkomancy.fabric.client.datagen;

import hans.inkomancy.Inkomancy;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.core.HolderLookup;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

public class InkomancyEnglishLanguageGenerator extends FabricLanguageProvider {
  protected InkomancyEnglishLanguageGenerator(FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
    super(dataOutput, "en_us", registryLookup);
  }

  private String capitalizeWord(String s) {
    return s.substring(0, 1).toUpperCase() + s.substring(1);
  }

  private String capitalize(String s) {
    return Arrays.stream(s.replace("_", " ").split("[ _]")).map(this::capitalizeWord).reduce("", (a, b) -> a + " " + b).trim();
  }

  @Override
  public void generateTranslations(HolderLookup.Provider provider, TranslationBuilder translationBuilder) {
    translationBuilder.add("itemGroup.inkomancy.inkomancy_tab", "Inkomancy");
    translationBuilder.add("tag.item.inkomancy.ink_tool_materials", "Ink Tool Materials");

    for (var item : Inkomancy.items()) {
      translationBuilder.add(item.getItem(), capitalize(item.getItem().toString().replace("inkomancy:", "")));
    }
  }
}
