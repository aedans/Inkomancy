package hans.inkomancy.neoforge;

import hans.inkomancy.Inkomancy;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.ItemAbility;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class InkomancyForgeRotatedPillarBlock extends RotatedPillarBlock {
  public InkomancyForgeRotatedPillarBlock(Properties arg) {
    super(arg);
  }

  @Override
  public @Nullable BlockState getToolModifiedState(@NotNull BlockState state, UseOnContext context, @NotNull ItemAbility itemAbility, boolean simulate) {
    if (context.getItemInHand().getItem() instanceof AxeItem) {
      if (state.is(Inkomancy.FOG_LOG.get())) {
        return Inkomancy.STRIPPED_FOG_LOG.get().defaultBlockState().setValue(AXIS, state.getValue(AXIS));
      }

      if (state.is(Inkomancy.FOG_WOOD.get())) {
        return Inkomancy.STRIPPED_FOG_WOOD.get().defaultBlockState().setValue(AXIS, state.getValue(AXIS));
      }
    }

    return super.getToolModifiedState(state, context, itemAbility, simulate);
  }
}
