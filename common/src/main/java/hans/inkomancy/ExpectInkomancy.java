package hans.inkomancy;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class ExpectInkomancy {
  @ExpectPlatform
  public static RotatedPillarBlock createRotatedPillarBlock(BlockBehaviour.Properties properties) {
    throw new AssertionError();
  }
}
