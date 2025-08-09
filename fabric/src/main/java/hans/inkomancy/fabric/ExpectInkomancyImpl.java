package hans.inkomancy.fabric;

import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class ExpectInkomancyImpl {
  public static RotatedPillarBlock createRotatedPillarBlock(BlockBehaviour.Properties properties) {
    return new RotatedPillarBlock(properties);
  }
}
