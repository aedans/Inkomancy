package hans.inkomancy.inks;

import hans.inkomancy.*;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.Set;

public class VoidInk extends Ink {
  public static final VoidInk INSTANCE = new VoidInk();
  public static final ToolMaterial MATERIAL = ToolMaterial.NETHERITE;

  private VoidInk() {
    super("void");
  }

  @Override
  public int getMana(Set<BlockPos> blocks) {
    return Integer.MAX_VALUE;
  }

  @Override
  public int modifyMana(int initial, int mana, int amount) {
    return mana;
  }

  @Override
  public void handleInvalidBlock(SpellParser parser, BlockPos pos) {
    parser.world().destroyBlock(pos, true);
  }

  @Override
  public void handleBlock(ServerLevel world, BlockPos pos) {

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
    return Inkomancy.VOID_INK_BLOCK.get();
  }

  @Override
  public BlockEntityType<InkBlockEntity> blockEntity() {
    return Inkomancy.VOID_INK_BLOCK_ENTITY.get();
  }

  @Override
  public BlockItem item() {
    return Inkomancy.VOID_INK_ITEM.get();
  }
}
