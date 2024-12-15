package hans.inkomancy.neoforge;

import hans.inkomancy.Inkomancy;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class InkomancyImpl {
  public static <T extends BlockEntity> BlockEntityType<T> createBlockEntityType(Inkomancy.Factory<T> factory, Block... blocks) {
    return new BlockEntityType<>(factory::create, blocks);
  }
}
