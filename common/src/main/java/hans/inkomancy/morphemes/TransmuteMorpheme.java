package hans.inkomancy.morphemes;

import hans.inkomancy.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;

import java.util.List;
import java.util.Set;

public class TransmuteMorpheme extends Morpheme {
  public static final TransmuteMorpheme INSTANCE = new TransmuteMorpheme();

  private TransmuteMorpheme() {
    super("transmute", Set.of(Type.ACTION));
  }

  @Override
  public void interpretAsAction(Spell spell, SpellContext context) throws InterpretError {
    var inputs = new Args(spell, context).get(Type.ITEMS, x -> x::interpretAsItems)
        .stream().flatMap(List::stream).toList();

    for (var item : inputs) {
      var inventory = new SingleRecipeInput(item.get());
      if (doTransmuteSingle(item, inventory, spell, context, TransmutationRecipe.Type.INSTANCE, 4)) {
        continue;
      }

      doTransmuteSingle(item, inventory, spell, context, RecipeType.SMELTING, 2);
    }
  }

  public <T extends RecipeInput> boolean doTransmuteSingle(
      Delegate<ItemStack> target, T inventory, Spell spell, SpellContext context, RecipeType<? extends Recipe<T>> recipe, int mana) throws InterpretError {
    var match = context.world().recipeAccess().getRecipeFor(recipe, inventory, context.world());
    if (match.isPresent()) {
      context.mana().consume(mana * target.get().getCount());
      target.modify(item -> match.get().value().assemble(inventory, context.world().registryAccess()).copyWithCount(item.getCount()));
      EffectUtils.transmuteEffect(context.world(), context.getPosition(spell, 1));
    }
    return match.isPresent();
  }
}
