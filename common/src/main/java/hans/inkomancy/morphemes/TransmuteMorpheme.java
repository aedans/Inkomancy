package hans.inkomancy.morphemes;

import hans.inkomancy.*;
import net.minecraft.sounds.SoundEvents;
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
    var transmute = context.world().canTransmute(recipe, inventory);
    if (transmute != null) {
      context.world().playSound(spell.pos(), SoundEvents.CRAFTER_CRAFT);
      context.mana().consume(mana * target.get().getCount());
      return true;
    }

    return false;
  }
}
