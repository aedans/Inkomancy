package hans.inkomancy;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class MagicItem extends Item {
  public final Ink ink;

  public MagicItem(Item.Properties settings, Ink ink) {
    super(settings);
    this.ink = ink;
  }

  public boolean tryUseSpell(Player playerEntity, ItemStack stack, BlockPos pos) {
    if (playerEntity.level() instanceof ServerLevel server && playerEntity instanceof ServerPlayer player) {
      var spell = stack.get(Inkomancy.SPELL_COMPONENT_TYPE.get());
      if (spell != null) {
        var mana = new ManaProvider(ink, ink.getMana(Set.of()));
        var context = new SpellContext(InteractableWorld.of(server), mana, player, pos);
        spell.morpheme().interpret(spell.base(), context);
        return true;
      }
    }

    return false;
  }

  @Override
  public @NotNull InteractionResult use(Level world, Player player, InteractionHand hand) {
    var blockHitResult = getPlayerPOVHitResult(world, player, ClipContext.Fluid.NONE);
    if (blockHitResult.getType() == HitResult.Type.MISS && tryUseSpell(player, player.getItemInHand(hand), null)) {
      return InteractionResult.SUCCESS_SERVER;
    }

    return super.use(world, player, hand);
  }

  @Override
  public @NotNull InteractionResult useOn(UseOnContext context) {
    if (context.getPlayer() != null && tryUseSpell(context.getPlayer(), context.getItemInHand(), context.getClickedPos())) {
      return InteractionResult.SUCCESS_SERVER;
    }

    return super.useOn(context);
  }
}
