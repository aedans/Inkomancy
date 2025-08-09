package hans.inkomancy.inks;

import hans.inkomancy.*;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;

import java.util.Set;

public class ConductiveInk extends Ink {
  public static final ConductiveInk INSTANCE = new ConductiveInk();

  private ConductiveInk() {
    super("conductive");
  }

  @Override
  public SoundEvent sound() {
    return SoundEvents.REDSTONE_TORCH_BURNOUT;
  }

  @Override
  public int getMana(Set<BlockPos> blocks) {
    return 256;
  }

  @Override
  public void handleBlock(ServerLevel world, BlockPos pos, String color) {
    EffectUtils.dustEffect(world, pos, color);
  }

  @Override
  public String lore() {
    return "...";
  }
}
