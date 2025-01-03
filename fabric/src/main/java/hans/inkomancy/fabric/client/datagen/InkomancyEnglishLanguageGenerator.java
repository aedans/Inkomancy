package hans.inkomancy.fabric.client.datagen;

import hans.inkomancy.Ink;
import hans.inkomancy.Inkomancy;
import hans.inkomancy.Morpheme;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.core.HolderLookup;

import java.util.concurrent.CompletableFuture;

public class InkomancyEnglishLanguageGenerator extends FabricLanguageProvider {
  protected InkomancyEnglishLanguageGenerator(FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
    super(dataOutput, "en_us", registryLookup);
  }

  @Override
  public void generateTranslations(HolderLookup.Provider provider, TranslationBuilder translationBuilder) {
    translationBuilder.add("itemGroup.inkomancy.inkomancy_tab", "Inkomancy");

    for (var ink : Ink.getInks()) {
      var name = ink.name.substring(0, 1).toUpperCase() + ink.name.substring(1);
      translationBuilder.add(ink.getItem(), name + " Ink");
      translationBuilder.add("tag.item.inkomancy." + ink.name + "_ink_tool_materials", name + " Ink Tool Materials");
    }

    for (var morpheme : Morpheme.getMorphemes()) {
      var name = morpheme.name.substring(0, 1).toUpperCase() + morpheme.name.substring(1);
      translationBuilder.add(morpheme.getItem(), name + " Morpheme");
    }

    translationBuilder.add(Inkomancy.INK_HELPER.get(), "Inky");
    translationBuilder.add(Inkomancy.SPELL_SCRIBE.get(), "Amanuensis");
    translationBuilder.add(Inkomancy.MIRROR.get(), "Mirror");
    translationBuilder.add(Inkomancy.BLUE_QUILL.get(), "Blue Quill");
    translationBuilder.add(Inkomancy.RED_QUILL.get(), "Red Quill");
    translationBuilder.add(Inkomancy.INK_WAND.get(), "Ink Wand");
    translationBuilder.add(Inkomancy.FLOWER_WAND.get(), "Flower Wand");

    translationBuilder.add(Inkomancy.INK_BALL.get(), "Ink Ball");
  }
}
