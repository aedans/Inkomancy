package hans.inkomancy.morphemes;

import hans.inkomancy.*;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Set;

public class ManifestMorpheme extends Morpheme {
  public final Morpheme morpheme;

  public ManifestMorpheme(Morpheme morpheme) {
    super("manifest", Set.of(Type.ITEMS));
    this.morpheme = morpheme;
  }

  @Override
  public List<? extends Delegate<ItemStack>> interpretAsItems(Spell spell, SpellContext context) {
    return List.of(new Delegate.Instance<>(this.morpheme.getItem().getDefaultInstance()));
  }
}
