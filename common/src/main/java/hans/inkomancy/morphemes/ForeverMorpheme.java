package hans.inkomancy.morphemes;

import hans.inkomancy.*;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

public class ForeverMorpheme extends Morpheme {
  public static final ForeverMorpheme INSTANCE = new ForeverMorpheme();

  private ForeverMorpheme() {
    super("forever", Set.of(Type.ACTION));
  }

  @Override
  public void interpretAsAction(Spell spell, SpellContext context, boolean undo) throws InterpretError {
    var args = new Args(spell, context);
    var items = args.getFlat(Type.ITEMS, m -> m::interpretAsItems).toList();
    var modifiers = items.stream().map(this::asModifier).filter(Objects::nonNull).toList();
    var targets = items.stream().filter(item -> asModifier(item) == null).toList();

    for (var modifier : modifiers) {
      for (var target : targets) {
        context.mana().consume(256);
        if (modifier.apply(target)) {
          EffectUtils.enchantEffect(context.world(), spell.pos());
          break;
        } else {
          context.mana().produce(256);
        }
      }
    }

    var spells = args.get(Type.SPELL, m -> m::interpretAsSpell).toList();
    for (var s : spells) {
      for (var target : targets) {
        context.mana().consume(256);
        target.update(item -> item.set(Inkomancy.SPELL_COMPONENT_TYPE.get(), s));
      }
    }
  }

  public @Nullable Function<Delegate<ItemStack>, Boolean> asModifier(Delegate<ItemStack> from) {
    if (EnchantmentUtils.isEnchanted(from.get())) {
      return to -> EnchantmentUtils.transferEnchantments(from, to);
    }

    Spell s = from.get().get(Inkomancy.SPELL_COMPONENT_TYPE.get());
    if (s != null) {
      return to -> {
        to.update(item -> item.set(Inkomancy.SPELL_COMPONENT_TYPE.get(), s));
        from.update(item -> item.remove(Inkomancy.SPELL_COMPONENT_TYPE.get()));
        return true;
      };
    }

    return null;
  }
}
