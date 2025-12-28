package hans.inkomancy.mixin;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(ShapelessRecipe.class)
public interface ShapelessRecipeAccessorMixin {
  @Accessor("result")
  ItemStack inkomancy$result();

  @Accessor("ingredients")
  NonNullList<Ingredient> inkomancy$ingredients();
}
