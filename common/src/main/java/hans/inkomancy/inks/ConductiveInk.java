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
  public int modifyMana(int initial, int mana, int amount) throws InterpretError {
    mana -= amount;
    if (mana < 0) {
      throw new InterpretError.OOM();
    }
    return mana;
  }

  @Override
  public void handleBlock(ServerLevel world, BlockPos pos) {
    EffectUtils.redstoneEffect(world, pos);
  }

  @Override
  public String lore() {
    return "...";
  }
}
