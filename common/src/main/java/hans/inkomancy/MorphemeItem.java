package hans.inkomancy;

import net.minecraft.world.item.Item;

public class MorphemeItem extends Item {
  public final Morpheme morpheme;

  public MorphemeItem(Morpheme morpheme, Properties properties) {
    super(properties);
    this.morpheme = morpheme;
  }
}
