package hans.inkomancy.inks;

import hans.inkomancy.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.phys.Vec3;

import java.util.Set;

public class RedInk extends Ink {
  public static final RedInk INSTANCE = new RedInk();
  public static final ToolMaterial MATERIAL = ToolMaterial.WOOD;

  private RedInk() {
    super("red");
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
  public void handleInvalidBlock(SpellParser parser, BlockPos pos) {
    parser.world().destroyBlock(pos, true);
  }

  @Override
  public void handleBlock(ServerLevel world, BlockPos pos) {
    InteractableWorld.of(world).playParticles(DustParticleOptions.REDSTONE, pos.getBottomCenter(), new Vec3(.25, .25, .25), 3, 0);
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
