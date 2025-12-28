package hans.inkomancy.fabric.client.datagen;

import hans.inkomancy.Inkomancy;
import hans.inkomancy.inks.ArdentInk;
import hans.inkomancy.inks.ConductiveInk;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class InkomancyRecipeGenerator extends FabricRecipeProvider {
  public InkomancyRecipeGenerator(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
    super(output, registriesFuture);
  }

  @Override
  public void buildRecipes(RecipeOutput recipeOutput) {
    for (int i = 0; i < 16; i++) {
      String color = Inkomancy.COLORS.get(i);
      ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ArdentInk.INSTANCE.getItem(color))
          .requires(Items.COAL)
          .requires(Inkomancy.DYES.get(i))
          .unlockedBy("has_coal", has(Items.COAL))
          .save(recipeOutput);

      ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ConductiveInk.INSTANCE.getItem(color))
          .requires(Items.REDSTONE)
          .requires(Inkomancy.DYES.get(i))
          .unlockedBy("has_redstone", has(Items.COAL))
          .save(recipeOutput);
    }
  }

  @Override
  public @NotNull String getName() {
    return "InkomancyRecipeGenerator";
  }
}
