package hans.inkomancy.inks;

import hans.inkomancy.*;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.Set;

public class ArdentInk extends Ink {
  public static final ArdentInk INSTANCE = new ArdentInk();

  private ArdentInk() {
    super("ardent");
  }

  @Override
  public SoundEvent sound() {
    return SoundEvents.FIRECHARGE_USE;
  }

  @Override
  public int getMana(Set<BlockPos> blocks) {
    return blocks.size() * 8;
  }

  @Override
  public void handleBlock(ServerLevel world, BlockPos pos, String color) {
    world.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
    EffectUtils.smokeEffect(world, pos);
  }

  @Override
  public String lore() {
    return "...";
  }
}
