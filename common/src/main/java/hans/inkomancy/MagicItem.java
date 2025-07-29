package hans.inkomancy;

import hans.inkomancy.inks.VoidInk;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;

import java.util.Set;

public interface MagicItem {
  class Instance extends Item implements MagicItem {
    public Instance(Properties properties) {
      super(properties);
    }
  }

  class PickaxeInstance extends PickaxeItem implements MagicItem {
    public PickaxeInstance(ToolMaterial toolMaterial, float f, float g, Properties properties) {
      super(toolMaterial, f, g, properties);
    }
  }

  class ShovelInstance extends ShovelItem implements MagicItem {
    public ShovelInstance(ToolMaterial toolMaterial, float f, float g, Properties properties) {
      super(toolMaterial, f, g, properties);
    }
  }

  static boolean isMagicItem(ItemStack stack) {
    return stack.getItem() instanceof MagicItem || stack.has(DataComponents.DAMAGE);
  }

  static boolean tryUseSpell(Player playerEntity, ItemStack stack, BlockPos pos) {
    if (playerEntity.level() instanceof ServerLevel server && playerEntity instanceof ServerPlayer player) {
      var spell = stack.get(Inkomancy.SPELL_COMPONENT_TYPE.get());
      if (spell != null) {
        var mana = new ManaProvider(VoidInk.INSTANCE, VoidInk.INSTANCE.getMana(Set.of()));
        var context = new SpellContext(server, player, VoidInk.INSTANCE, mana, pos, null);
        spell.morpheme().interpret(spell.base(), context);
        return true;
      }
    }

    return false;
  }
}
