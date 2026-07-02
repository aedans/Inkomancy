package hans.inkomancy.fabric.gametest;

import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.level.block.Blocks;

import static hans.inkomancy.fabric.gametest.Spells.*;

/**
 * Coverage of {@code expand}, which fans a single position out into the 3x3x3 box of positions
 * centered on it. Each test drives it through {@code break} the way the hammer's on-break spell does:
 * a positionless {@code hole} reads the broken block ({@code positionInput}, supplied via castAt).
 */
public class ExpandGameTest implements FabricGameTest {

  // break[expand[hole]]: every block in the 3x3x3 box around the broken block is destroyed.
  @GameTest(template = EMPTY_STRUCTURE)
  public void expandBreaksThreeByThreeByThree(GameTestHelper helper) {
    for (int x = 1; x <= 3; x++) {
      for (int y = 1; y <= 3; y++) {
        for (int z = 1; z <= 3; z++) {
          helper.setBlock(new BlockPos(x, y, z), Blocks.STONE);
        }
      }
    }

    castAt(helper, breakBlocks(expand(hole())), 2, 2, 2);

    for (int x = 1; x <= 3; x++) {
      for (int y = 1; y <= 3; y++) {
        for (int z = 1; z <= 3; z++) {
          helper.assertBlockPresent(Blocks.AIR, new BlockPos(x, y, z));
        }
      }
    }
    helper.succeed();
  }

  // The box extends exactly one block in each direction: a block two out on any axis is untouched.
  @GameTest(template = EMPTY_STRUCTURE)
  public void expandLeavesBlocksOutsideTheBox(GameTestHelper helper) {
    helper.setBlock(new BlockPos(2, 2, 2), Blocks.STONE); // center, inside the box
    helper.setBlock(new BlockPos(4, 2, 2), Blocks.STONE); // two east, outside the box

    castAt(helper, breakBlocks(expand(hole())), 2, 2, 2);

    helper.assertBlockPresent(Blocks.AIR, new BlockPos(2, 2, 2));
    helper.assertBlockPresent(Blocks.STONE, new BlockPos(4, 2, 2));
    helper.succeed();
  }
}
