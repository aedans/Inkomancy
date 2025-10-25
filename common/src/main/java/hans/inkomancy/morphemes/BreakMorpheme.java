package hans.inkomancy.morphemes;

import hans.inkomancy.*;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.BlockItem;
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
    var positions = new Args(spell, context).getFlat(Type.POSITION, m -> m::interpretAsPositions).map(Position::blockPos).collect(Collectors.toList());
    return interpretBreak(spell, context, positions);
  }

  @Override
  public void interpretAsAction(Spell spell, SpellContext context, boolean undo) throws InterpretError {
    var args = new Args(spell, context);
    if (undo) {
      var items = args.getFlat(Type.ITEMS, m -> m::interpretAsItems)
          .filter(x -> x.get().getItem() instanceof BlockItem).collect(Collectors.toList());
      var positions = args.getFlat(Type.POSITION, m -> m::interpretAsPositions).map(Position::blockPos).toList();
      for (var pos : positions) {
        var item = Util.randomOf(items);
        if (context.world().getBlockState(pos).isAir() && item.get().getItem() instanceof BlockItem blockItem) {
          var state = blockItem.getBlock().defaultBlockState();
          if (state.canSurvive(context.world(), pos)) {
            context.mana().consume(1);
            if (item.get().getCount() > 1) {
              item.update(f -> f.setCount(f.getCount() - 1));
            } else {
              item.destroy();
              items.remove(item);
            }
            context.world().setBlock(pos, state, InkBlock.UPDATE_ALL);
            blockItem.getBlock().setPlacedBy(context.world(), pos, state, context.caster(), item.get());
            context.world().playSound(null, pos, blockItem.getBlock().defaultBlockState().getSoundType().getPlaceSound(), SoundSource.NEUTRAL);
          }
        }
      }
    } else {
      var positions = args.getFlat(Type.POSITION, m -> m::interpretAsPositions).map(Position::blockPos).toList();
      for (var delegate : interpretBreak(spell, context, positions)) {
        context.world().destroyBlock(delegate.pos, true);
      }
    }
  }

  public List<BlockItemDelegate> interpretBreak(Spell spell, SpellContext context, List<BlockPos> positions) {
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

    @Override
    public boolean mutable() {
      return true;
    }
  }
}
