package hans.inkomancy.morphemes;

import hans.inkomancy.*;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Set;

public class VoidMorpheme extends Morpheme {
  public static final VoidMorpheme INSTANCE = new VoidMorpheme();

  protected VoidMorpheme() {
    super("void", Set.of(Type.ITEMS, Type.ACTION));
  }

  @Override
  public List<? extends Delegate<ItemStack>> interpretAsItems(Spell spell, SpellContext context) throws InterpretError {
    var server = (InkomancyLevelData) context.world().getServer().getWorldData();
    return server.inkomancy$getVoidContainer().removeAllItems().stream().map(Delegate.Instance::new).toList();
  }

  @Override
  public void interpretAsAction(Spell spell, SpellContext context) throws InterpretError {
    var server = (InkomancyLevelData) context.world().getServer().getWorldData();
    var items = new Args(spell, context).get(Type.ITEMS, m -> m::interpretAsItems)
        .stream().flatMap(List::stream).toList();
    for (var item : items) {
      var leftover = server.inkomancy$getVoidContainer().addItem(item.get());
      if (leftover.isEmpty()) {
        item.destroy();
      } else if (leftover.getCount() != item.get().getCount()) {
        item.set(leftover);
      }
    }
  }
}
