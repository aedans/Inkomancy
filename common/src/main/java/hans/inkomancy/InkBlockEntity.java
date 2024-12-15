package hans.inkomancy;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class InkBlockEntity extends BlockEntity {
  public int ticks = 0;

  public InkBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
    super(type, pos, state);
  }

  @Override
  protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider registries) {
    nbt.putInt("ticks", ticks);
    super.saveAdditional(nbt, registries);
  }

  @Override
  protected void loadAdditional(CompoundTag nbt, HolderLookup.Provider registries) {
    super.loadAdditional(nbt, registries);
    ticks = nbt.getInt("ticks");
  }
}
