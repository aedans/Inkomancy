package hans.inkomancy.morphemes;

import hans.inkomancy.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class TransmuteMorpheme extends Morpheme {
  public static final TransmuteMorpheme SMELT = new TransmuteMorpheme(TransmuteType.SMELT);
  public static final TransmuteMorpheme CRAFT = new TransmuteMorpheme(TransmuteType.CRAFT);

  public final TransmuteType type;

  private TransmuteMorpheme(TransmuteType type) {
    super("transmute_" + type.toString().toLowerCase(), Set.of(Type.ACTION));
    this.type = type;
  }

  @Override
  public void interpretAsAction(Spell spell, SpellContext context) throws InterpretError {
    var inputs = new ArrayList<Delegate<ItemStack>>();
    for (var s : spell.connected()) {
      inputs.addAll(s.morpheme().interpretAsItems(s, context));
    }

    for (var item : inputs) {
      var inventory = new SingleRecipeInput(item.get());
      var craftingInventory = CraftingInput.of(1, 1, List.of(item.get()));
      if (doTransmuteSingle(item, inventory, spell, context, TransmutationRecipe.Type.INSTANCE, 4)) {
        continue;
      }

      if (type == TransmuteType.SMELT) {
        doTransmuteSingle(item, inventory, spell, context, RecipeType.SMELTING, 2);
      } else if (type == TransmuteType.CRAFT) {
        doTransmuteSingle(item, craftingInventory, spell, context, RecipeType.CRAFTING, 1);
      }
    }
  }

  public <T extends RecipeInput> boolean doTransmuteSingle(
      Delegate<ItemStack> target, T inventory, Spell spell, SpellContext context, RecipeType<? extends Recipe<T>> recipe, int mana) throws InterpretError {
    var match = context.world().recipeAccess().getRecipeFor(recipe, inventory, context.world());
    if (match.isPresent()) {
      context.mana().consume(mana * target.get().getCount());
      target.modify(item -> match.get().value().assemble(inventory, context.world().registryAccess()).copyWithCount(item.getCount()));
      EffectUtils.transmuteEffect(context.world(), context.getPosition(spell));
    }
    return match.isPresent();
  }

  public enum TransmuteType {
    SMELT, CRAFT
  }
}
