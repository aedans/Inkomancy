package hans.inkomancy.inks;

import hans.inkomancy.*;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;

import java.util.Set;

public class VoidInk extends Ink {
  public static final VoidInk INSTANCE = new VoidInk();

  private VoidInk() {
    super("void");
  }

  @Override
  public SoundEvent sound() {
    return SoundEvents.ENDER_EYE_DEATH;
  }

  @Override
  public int getMana(Set<BlockPos> blocks) {
    return Integer.MAX_VALUE;
  }

  @Override
  public void handleBlock(ServerLevel world, BlockPos pos, String color) {

  }

  @Override
  public String lore() {
    return "...";
  }
}
