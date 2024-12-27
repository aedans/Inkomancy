package hans.inkomancy.inks;

import hans.inkomancy.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.phys.Vec3;

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
  public void handleInvalidBlock(SpellParser parser, BlockPos pos) {
    parser.world().destroyBlock(pos, true);
  }

  @Override
  public void handleBlock(ServerLevel world, BlockPos pos) {
    world.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_CLIENTS);
    InteractableWorld.of(world).playParticles(ParticleTypes.SMOKE, pos.getBottomCenter(), new Vec3(.25, .25, .25), 5, 0);
  }

  @Override
  public String lore() {
    return "...";
  }

  @Override
  public ToolMaterial material() {
    return MATERIAL;
  }

  @Override
  public InkBlock block() {
    return Inkomancy.BLACK_INK_BLOCK.get();
  }

  @Override
  public BlockEntityType<InkBlockEntity> blockEntity() {
    return Inkomancy.BLACK_INK_BLOCK_ENTITY.get();
  }

  @Override
  public BlockItem item() {
    return Inkomancy.BLACK_INK_ITEM.get();
  }
}
