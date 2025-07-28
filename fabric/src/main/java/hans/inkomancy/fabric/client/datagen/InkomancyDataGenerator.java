package hans.inkomancy.fabric.client.datagen;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class InkomancyDataGenerator implements DataGeneratorEntrypoint {
  @Override
  public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
    var pack = fabricDataGenerator.createPack();
    pack.addProvider(InkomancyBlockLootTableGenerator::new);
    pack.addProvider(InkomancyEnglishLanguageGenerator::new);
    pack.addProvider(InkomancyModelGenerator::new);
    pack.addProvider(InkomancyTagGenerator::new);
    pack.addProvider(InkomancyRecipeGenerator::new);
  }
}
