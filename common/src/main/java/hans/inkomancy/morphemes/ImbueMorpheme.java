package hans.inkomancy.morphemes;

import hans.inkomancy.*;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

public class ImbueMorpheme extends Morpheme {
  public static final ImbueMorpheme INSTANCE = new ImbueMorpheme();

  private ImbueMorpheme() {
    super("imbue", Set.of(Type.ACTION));
  }

  @Override
  public void interpretAsAction(Spell spell, SpellContext context, boolean undo) throws InterpretError {
    var args = new Args(spell, context);
    // Consumed before reading ITEMS so `break` (which supports ITEMS) is not misread as an item arg.
    var onBreak = args.flag(BreakMorpheme.INSTANCE);
    var items = args.getFlat(Type.ITEMS, m -> m::interpretAsItems).toList();
    var modifiers = items.stream().map(this::asModifier).filter(Objects::nonNull).toList();
    var targets = items.stream().filter(item -> asModifier(item) == null).toList();

    for (var modifier : modifiers) {
      for (var target : targets) {
        if (!context.mana().canConsume(256)) {
          break;
        }

        if (modifier.apply(target)) {
          EffectUtils.enchantEffect(context.world(), spell.pos());
          context.mana().consume(256);
          break;
        }
      }
    }

    // A `break` flag imbues the on-break slot, re-rooting under the items source; otherwise the
    // on-cast slot, rooted under the action source. Read hands us a source-rooted spell either way.
    var component = onBreak ? Inkomancy.BREAK_SPELL_COMPONENT_TYPE.get() : Inkomancy.SPELL_COMPONENT_TYPE.get();
    var root = onBreak ? SourceMorpheme.BREAK : SourceMorpheme.CAST;
    var spells = args.get(Type.SPELL, m -> m::interpretAsSpell).toList();
    for (var s : spells) {
      var imbued = new Spell(root, s.connected());
      for (var target : targets) {
        context.mana().consume(256);
        target.update(item -> item.set(component, imbued));
      }
    }
  }

  public @Nullable Function<Delegate<ItemStack>, Boolean> asModifier(Delegate<ItemStack> from) {
    if (EnchantmentUtils.isEnchanted(from.get())) {
      return to -> EnchantmentUtils.transferEnchantments(from, to);
    }

    var castSpell = from.get().get(Inkomancy.SPELL_COMPONENT_TYPE.get());
    var breakSpell = from.get().get(Inkomancy.BREAK_SPELL_COMPONENT_TYPE.get());
    if (castSpell != null || breakSpell != null) {
      return to -> {
        transfer(from, to, Inkomancy.SPELL_COMPONENT_TYPE.get(), castSpell);
        transfer(from, to, Inkomancy.BREAK_SPELL_COMPONENT_TYPE.get(), breakSpell);
        return true;
      };
    }

    return null;
  }

  private void transfer(Delegate<ItemStack> from, Delegate<ItemStack> to, DataComponentType<Spell> component, @Nullable Spell spell) {
    if (spell != null) {
      to.update(item -> item.set(component, spell));
      from.update(item -> item.remove(component));
    }
  }
}
