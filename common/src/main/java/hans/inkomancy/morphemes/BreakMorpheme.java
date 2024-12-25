package hans.inkomancy.morphemes;

import hans.inkomancy.*;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

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

  public List<BlockItemDelegate> interpretBreak(Spell spell, SpellContext context, boolean drop) throws InterpretError {
    var positions = new Args(spell, context).get(Type.POSITION, m -> m::interpretAsPositions)
        .stream().flatMap(Collection::stream).map(Position::blockPos).collect(Collectors.toList());
    Collections.shuffle(positions);
    var drops = new ArrayList<BlockItemDelegate>();
    for (var pos : positions) {
      try {
        context.mana().consume(1 + (int) (context.world().getBlockState(pos).getDestroySpeed(context.world(), pos) * 10));
        var items = context.world().getBlockState(pos).getDrops(new LootParams.Builder(context.world())
            .withParameter(LootContextParams.ORIGIN, context.getPosition(spell).getCenter())
            .withParameter(LootContextParams.TOOL, ItemStack.EMPTY));
        drops.addAll(items.stream().map(item -> new BlockItemDelegate(context, item, pos, drop)).toList());
      } catch (Exception ignored) {
        break;
      }
    }
    return drops;
  }

  public record BlockItemDelegate(SpellContext context, ItemStack item, BlockPos pos, boolean drop) implements Delegate<ItemStack> {
    @Override
    public ItemStack get() {
      return item;
    }

    @Override
    public void set(ItemStack modified) {

    }

    @Override
    public void action(boolean replace) {
      context.world().destroyBlock(pos, !replace);
    }
  }
}
