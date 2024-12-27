package hans.inkomancy.morphemes;

import hans.inkomancy.*;
import net.minecraft.world.item.ItemStack;

import java.util.*;
import java.util.stream.Collectors;

public class BreakMorpheme extends Morpheme {
  public static final BreakMorpheme INSTANCE = new BreakMorpheme();

  private BreakMorpheme() {
    super("break", Set.of(Type.ACTION, Type.ITEMS));
  }

  @Override
  public List<? extends Delegate<ItemStack>> interpretAsItems(Spell spell, SpellContext context) throws InterpretError {
    return interpretBreak(spell, context, false);
  }

  @Override
  public void interpretAsAction(Spell spell, SpellContext context) throws InterpretError {
    for (var delegate : interpretBreak(spell, context, true)) {
      delegate.action(false);
    }
  }

  public List<? extends Delegate<ItemStack>> interpretBreak(Spell spell, SpellContext context, boolean drop) throws InterpretError {
    var positions = new Args(spell, context).get(Type.POSITION, m -> m::interpretAsPositions)
        .stream().flatMap(Collection::stream).map(Position::blockPos).collect(Collectors.toList());
    Collections.shuffle(positions);
    var drops = new ArrayList<Delegate<ItemStack>>();
    for (var pos : positions) {
      try {
        var result = context.world().breakBlock(pos, context.getPosition(spell).getCenter(), drop);
        context.mana().consume(1 + (result.getFirst() * 10));
        drops.addAll(result.getSecond());
      } catch (Exception ignored) {
        break;
      }
    }
    return drops;
  }
}
