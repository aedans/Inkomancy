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
    return interpretBreak(spell, context);
  }

  @Override
  public void interpretAsAction(Spell spell, SpellContext context) throws InterpretError {
    for (var delegate : interpretBreak(spell, context)) {
      context.world().destroyBlock(delegate.pos, true);
    }
  }

  public List<BlockItemDelegate> interpretBreak(Spell spell, SpellContext context) throws InterpretError {
    var positions = new Args(spell, context).get(Type.POSITION, m -> m::interpretAsPositions)
        .stream().flatMap(Collection::stream).map(Position::blockPos).collect(Collectors.toList());
    Collections.shuffle(positions);
    var drops = new ArrayList<BlockItemDelegate>();
    for (var pos : positions) {
      try {
        context.mana().consume(1 + (int) (context.world().getBlockState(pos).getDestroySpeed(context.world(), pos) * 10));
        var items = context.world().getBlockState(pos).getDrops(new LootParams.Builder(context.world())
            .withParameter(LootContextParams.ORIGIN, context.getPosition(spell, 1).getCenter())
            .withParameter(LootContextParams.TOOL, ItemStack.EMPTY));
        drops.addAll(items.stream().map(item -> new BlockItemDelegate(context, item, pos)).toList());
      } catch (Exception ignored) {
        break;
      }
    }
    return drops;
  }

  public static final class BlockItemDelegate implements Delegate<ItemStack> {
    private ItemStack item;
    private final SpellContext context;
    private final BlockPos pos;

    public BlockItemDelegate(SpellContext context, ItemStack item, BlockPos pos) {
      this.context = context;
      this.item = item;
      this.pos = pos;
    }

    @Override
    public ItemStack get() {
      return item;
    }

    @Override
    public void set(ItemStack modified) {
      context.world().destroyBlock(pos, false);
      item = modified;
    }

    @Override
    public void destroy() {
      set(null);
    }
  }
}
