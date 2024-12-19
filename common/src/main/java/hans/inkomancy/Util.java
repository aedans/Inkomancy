package hans.inkomancy;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Style;

import java.util.List;
import java.util.Random;

public class Util {
  private static final Random random = new Random();

  public static <T> T randomOf(List<T> ts) {
    return ts.get(random.nextInt(ts.size()));
  }

  public static final Style LORE_STYLE = Style.EMPTY.withColor(ChatFormatting.DARK_PURPLE).withItalic(true);
}
