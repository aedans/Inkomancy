package hans.inkomancy.fabric;

import hans.inkomancy.Inkomancy;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class InkomancyImpl {
  public static <T extends BlockEntity> BlockEntityType<T> createBlockEntityType(Inkomancy.Factory<T> factory, Block... blocks) {
    return FabricBlockEntityTypeBuilder.create(factory::create, blocks).build();
  }
}
