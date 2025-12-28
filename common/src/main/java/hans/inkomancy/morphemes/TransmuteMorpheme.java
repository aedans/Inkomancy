package hans.inkomancy.morphemes;

import hans.inkomancy.*;
import hans.inkomancy.mixin.ShapedRecipeAccessorMixin;
import hans.inkomancy.mixin.ShapelessRecipeAccessorMixin;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class TransmuteMorpheme extends Morpheme {
  public static final TransmuteMorpheme INSTANCE = new TransmuteMorpheme();

  private TransmuteMorpheme() {
    super("transmute", Set.of(Type.ACTION, Type.ITEMS));
  }

  @Override
  public List<? extends Delegate<ItemStack>> interpretAsItems(Spell spell, SpellContext context) throws InterpretError {
    var inputs = new Args(spell, context).getFlat(Type.ITEMS, x -> x::interpretAsItems).toList();
    var ingredients = inputs.stream().filter(Delegate::mutable).toList();
    var result = Util.randomOf(inputs.stream().filter(x -> !x.mutable()).toList()).get();
    var success = transmuteInto(ingredients, result, context);
    if (success != null) {
      return List.of(new Delegate.Instance<>(success));
    } else {
      return List.of();
    }
  }

  @Override
  public void interpretAsAction(Spell spell, SpellContext context, boolean undo) throws InterpretError {
    interpretAsItems(spell, context);
  }

  public @Nullable ItemStack transmuteInto(List<? extends Delegate<ItemStack>> inputs, ItemStack output, SpellContext context) throws InterpretError {
    var recipes = context.world().getRecipeManager().getRecipes();
    for (var holder : recipes) {
      if (holder.value() instanceof ShapedRecipe recipe && ItemStack.isSameItem(((ShapedRecipeAccessorMixin) recipe).inkomancy$result(), output)) {
        if (transmuteIntoSingle(recipe.getIngredients().stream().map(Optional::of).toList(), inputs, context)) {
          return ((ShapedRecipeAccessorMixin) recipe).inkomancy$result().copy();
        }
      }

      if (holder.value() instanceof ShapelessRecipe recipe && ItemStack.isSameItem(((ShapelessRecipeAccessorMixin) recipe).inkomancy$result(), output)) {
        if (transmuteIntoSingle(((ShapelessRecipeAccessorMixin) recipe).inkomancy$ingredients().stream().map(Optional::of).toList(), inputs, context)) {
          return ((ShapelessRecipeAccessorMixin) recipe).inkomancy$result().copy();
        }
      }
    }

    return null;
  }

  public boolean transmuteIntoSingle(List<Optional<Ingredient>> recipeIngredients, List<? extends Delegate<ItemStack>> inputs, SpellContext context) throws InterpretError {
    var consumed = new HashMap<Delegate<ItemStack>, Integer>();
    var ingredients = recipeIngredients.stream()
        .filter(Optional::isPresent)
        .map(Optional::get)
        .filter(ingredient -> inputs.stream().anyMatch(input -> {
          var realCount = consumed.getOrDefault(input, 0);
          var realInput = input.get().copyWithCount(input.get().getCount() - realCount);
          var isValid = realInput.getCount() > 0 && ingredient.test(realInput);
          if (isValid) {
            consumed.put(input, realCount + 1);
          }
          return isValid;
        }))
        .map(Optional::of)
        .toList();

    if (new HashSet<>(ingredients).containsAll(recipeIngredients)) {
      context.mana().consume(1);

      for (var entry : consumed.entrySet()) {
        entry.getKey().update(x -> x.setCount(x.getCount() - entry.getValue()));
      }

      return true;
    } else {
      return false;
    }
  }
}
