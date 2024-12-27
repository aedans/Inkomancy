package hans.inkomancy.morphemes;

import hans.inkomancy.*;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;

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
    var inputs = new Args(spell, context).get(Type.ITEMS, x -> x::interpretAsItems)
        .stream().flatMap(List::stream).toList();

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
    var transmute = context.world().canTransmute(recipe, inventory);
    if (transmute != null) {
      context.world().playSound(spell.pos(), SoundEvents.CRAFTER_CRAFT);
      context.mana().consume(mana * target.get().getCount());
      return true;
    }

    return false;
  }

  public enum TransmuteType {
    SMELT, CRAFT
  }
}
