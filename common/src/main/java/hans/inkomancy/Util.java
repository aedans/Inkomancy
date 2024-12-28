package hans.inkomancy;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Style;
import net.minecraft.world.phys.AABB;

import java.util.List;
import java.util.Random;

public class Util {
  private static final Random random = new Random();

  public static final Style LORE_STYLE = Style.EMPTY.withColor(ChatFormatting.DARK_PURPLE).withItalic(true);

  public static <T> T randomOf(List<T> ts) {
    return ts.get(random.nextInt(ts.size()));
  }

  public static AABB getBox(BlockPos position) {
    return AABB.encapsulatingFullBlocks(position.offset(-1, -1, -1), position.offset(1, 1, 1));
  }
}
