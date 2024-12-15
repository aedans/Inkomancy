package hans.inkomancy;

import java.util.List;
import java.util.Random;

public class Util {
  private static final Random random = new Random();

  public static <T> T randomOf(List<T> ts) {
    return ts.get(random.nextInt(ts.size()));
  }
}
