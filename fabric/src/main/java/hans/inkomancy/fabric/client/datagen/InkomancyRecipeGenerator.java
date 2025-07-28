package hans.inkomancy.fabric.client.datagen;

import hans.inkomancy.Inkomancy;
import hans.inkomancy.inks.ArdentInk;
import hans.inkomancy.inks.ConductiveInk;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class InkomancyRecipeGenerator extends FabricRecipeProvider {
  public InkomancyRecipeGenerator(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
    super(output, registriesFuture);
  }

  @Override
  protected @NotNull RecipeProvider createRecipeProvider(HolderLookup.Provider provider, RecipeOutput recipeOutput) {
    return new RecipeProvider(provider, recipeOutput) {
      @Override
      public void buildRecipes() {
        for (var i = 0; i < 16; i++) {
          shapeless(RecipeCategory.MISC, ArdentInk.INSTANCE.getItem(Inkomancy.COLORS.get(i)))
              .requires(Items.COAL)
              .requires(Inkomancy.DYES.get(i))
              .unlockedBy("has_coal", this.has(Items.COAL))
              .save(this.output, Inkomancy.COLORS.get(i) + "_ardent_ink");

          shapeless(RecipeCategory.MISC, ConductiveInk.INSTANCE.getItem(Inkomancy.COLORS.get(i)))
              .requires(Items.REDSTONE)
              .requires(Inkomancy.DYES.get(i))
              .unlockedBy("has_redstone", this.has(Items.REDSTONE))
              .save(this.output, Inkomancy.COLORS.get(i) + "_conductive_ink");
        }
      }
    };
  }

  @Override
  public String getName() {
    return "InkomancyRecipeGenerator";
  }
}
