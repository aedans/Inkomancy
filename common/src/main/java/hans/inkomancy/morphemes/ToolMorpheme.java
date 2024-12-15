package hans.inkomancy.morphemes;

import hans.inkomancy.*;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ToolMorpheme extends Morpheme {
  public static final ToolMorpheme INSTANCE = new ToolMorpheme();

  private ToolMorpheme() {
    super("tool", Set.of(Type.ITEMS, Type.ACTION));
  }

  @Override
  public List<? extends Delegate<ItemStack>> interpretAsItems(Spell spell, SpellContext context) throws InterpretError {
    var items = getArg(spell, context, 0, null, m -> m::interpretAsItems);
    var toolTags = toolTags(context);
    var tools = new HashSet<Item>();
    for (var item : items) {
      if (item.get().getItem() instanceof BlockItem blockItem) {
        var block = blockItem.getBlock().defaultBlockState();
        for (var tag : toolTags.keySet()) {
          if (block.is(tag)) {
            context.mana().consume(256);
            tools.add(toolTags.get(tag));
          }
        }
      }
    }

    return tools.stream().map(item -> {
      var stack = item.getDefaultInstance();
      stack.set(Inkomancy.CONJURED_COMPONENT_TYPE.get(), true);
      return Delegate.of(stack);
    }).toList();
  }

  private Map<TagKey<Block>, Item> toolTags(SpellContext context) {
    return Map.of(
//        BlockTags.PICKAXE_MINEABLE, context.ink().pickaxe(),
//        BlockTags.AXE_MINEABLE, context.ink().axe(),
//        BlockTags.SHOVEL_MINEABLE, context.ink().shovel(),
//        BlockTags.HOE_MINEABLE, context.ink().hoe()
    );
  }
}
