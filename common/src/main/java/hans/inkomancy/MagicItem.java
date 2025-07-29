package hans.inkomancy;

import hans.inkomancy.inks.VoidInk;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Set;

public class MagicItem extends Item {
  public MagicItem(Item.Properties settings) {
    super(settings);
  }

  public static boolean isMagicItem(ItemStack stack) {
    return stack.getItem() instanceof MagicItem || stack.has(DataComponents.DAMAGE);
  }

  public static boolean tryUseSpell(Player playerEntity, ItemStack stack, BlockPos pos) {
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
