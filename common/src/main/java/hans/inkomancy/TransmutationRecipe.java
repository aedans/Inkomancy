package hans.inkomancy;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public record TransmutationRecipe(Ingredient ingredient, ItemStack result) implements Recipe<SingleRecipeInput> {
  @Override
  public boolean matches(SingleRecipeInput input, Level world) {
    return ingredient.test(input.getItem(0));
  }

  @Override
  public @NotNull ItemStack assemble(SingleRecipeInput input, HolderLookup.Provider registries) {
    return result.copyWithCount(input.item().getCount());
  }

  @Override
  public boolean canCraftInDimensions(int i, int j) {
    return false;
  }

  @Override
  public @NotNull ItemStack getResultItem(HolderLookup.Provider provider) {
    return result;
  }

  @Override
  public @NotNull RecipeSerializer<? extends Recipe<SingleRecipeInput>> getSerializer() {
    return Serializer.INSTANCE;
  }

  @Override
  public @NotNull RecipeType<? extends Recipe<SingleRecipeInput>> getType() {
    return Type.INSTANCE;
  }

  @Override
  public boolean isSpecial() {
    return true;
  }

  public static class Type implements RecipeType<TransmutationRecipe> {
    public static final Type INSTANCE = new Type();
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(Inkomancy.MOD_ID, "transmutation");

    private Type() {
    }
  }

  public static class Serializer implements RecipeSerializer<TransmutationRecipe> {
    public static final Serializer INSTANCE = new Serializer();

    private Serializer() {
    }

    public static final MapCodec<TransmutationRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        Ingredient.CODEC.fieldOf("ingredient").forGetter(recipe -> recipe.ingredient),
        ItemStack.CODEC.fieldOf("result").forGetter(recipe -> recipe.result)
    ).apply(instance, TransmutationRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, TransmutationRecipe> PACKET_CODEC = StreamCodec.composite(
        Ingredient.CONTENTS_STREAM_CODEC, recipe -> recipe.ingredient,
        ItemStack.STREAM_CODEC, recipe -> recipe.result,
        TransmutationRecipe::new
    );

    @Override
    public @NotNull MapCodec<TransmutationRecipe> codec() {
      return CODEC;
    }

    @Override
    public @NotNull StreamCodec<RegistryFriendlyByteBuf, TransmutationRecipe> streamCodec() {
      return PACKET_CODEC;
    }
  }
}
