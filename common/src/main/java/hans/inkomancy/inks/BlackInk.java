package hans.inkomancy.inks;

import hans.inkomancy.*;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.Set;

public class BlackInk extends Ink {
  public static final BlackInk INSTANCE = new BlackInk();
  public static final ToolMaterial MATERIAL = ToolMaterial.STONE;

  private BlackInk() {
    super("black");
  }

  @Override
  public int getMana(Set<BlockPos> blocks) {
    return blocks.size() * 8;
  }

  @Override
  public int modifyMana(int initial, int mana, int amount) throws InterpretError {
    mana -= amount;
    if (mana < -initial) {
      throw new InterpretError.OOM();
    }
    return mana;
  }

  @Override
  public void handleBlock(ServerLevel world, BlockPos pos) {
    world.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_CLIENTS);
    EffectUtils.smokeEffect(world, pos);
  }

  @Override
  public String lore() {
    return "...";
  }

  @Override
  public ToolMaterial material() {
    return MATERIAL;
  }
}
