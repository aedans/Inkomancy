package hans.inkomancy.morphemes;

import hans.inkomancy.*;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class VoidMorpheme extends Morpheme {
  public static final VoidMorpheme INSTANCE = new VoidMorpheme();

  protected VoidMorpheme() {
    super("void", Set.of(Type.ITEMS, Type.ACTION));
  }

  @Override
  public List<? extends Delegate<ItemStack>> interpretAsItems(Spell spell, SpellContext context) throws InterpretError {
    var colors = getColors(spell, context);
    var server = (InkomancyLevelData) context.world().getServer().getWorldData();
    store(spell, context, colors, server);
    return server.inkomancy$getVoidContainer(colors).getItems().stream()
        .filter(x -> !x.equals(ItemStack.EMPTY))
        .map(x -> new VoidItemDelegate(x, server.inkomancy$getVoidContainer(colors)))
        .toList();
  }

  @Override
  public void interpretAsAction(Spell spell, SpellContext context) throws InterpretError {
    var colors = getColors(spell, context);
    var server = (InkomancyLevelData) context.world().getServer().getWorldData();
    store(spell, context, colors, server);
  }

  public void store(Spell spell, SpellContext context, List<String> colors, InkomancyLevelData server) throws InterpretError {
    var items = new Args(spell, context).get(Type.ITEMS, m -> m::interpretAsItems)
        .stream().flatMap(List::stream).toList();
    for (var item : items) {
      var leftover = server.inkomancy$getVoidContainer(colors).addItem(item.get());
      if (leftover.isEmpty()) {
        item.destroy();
      } else if (leftover.getCount() != item.get().getCount()) {
        item.set(leftover);
      }
    }
  }

  public List<String> getColors(Spell spell, SpellContext context) {
    var list = new ArrayList<String>();
    for (int i = 1; i < 4; i++) {
      if (context.world().getBlockState(context.getPosition(spell, i)).getBlock() instanceof InkBlock block) {
        list.add(Ink.colorOf(block));
      } else {
        list.add("");
      }
    }
    return list;
  }

  public static final class VoidItemDelegate implements Delegate<ItemStack> {
    public ItemStack itemStack;
    private final VoidContainer container;

    public VoidItemDelegate(ItemStack itemStack, VoidContainer container) {
      this.itemStack = itemStack;
      this.container = container;
    }

    @Override
    public ItemStack get() {
      var index = container.getItems().indexOf(itemStack);
      if (index >= 0) {
        return itemStack;
      } else {
        return null;
      }
    }

    @Override
    public void set(ItemStack modified) {
      var index = container.getItems().indexOf(itemStack);
      if (index >= 0) {
        container.setItem(index, modified);
        itemStack = modified;
      }
    }

    @Override
    public void destroy() {
      var index = container.getItems().indexOf(itemStack);
      if (index >= 0) {
        container.setItem(index, ItemStack.EMPTY);
        set(null);
      }
    }
  }
}
