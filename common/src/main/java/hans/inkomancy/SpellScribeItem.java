package hans.inkomancy;

import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class SpellScribeItem extends Item {
  public final Ink ink;

  public SpellScribeItem(Item.Properties settings, Ink ink) {
    super(settings);
    this.ink = ink;
  }

  @Override
  public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag type) {
    if (InkHelperItem.hasHelper(stack)) {
      tooltip.add(Component.literal("Paints the inscribed spell onto a flat surface").withStyle(Util.LORE_STYLE));
    }
  }

  @Override
  public @NotNull InteractionResult useOn(UseOnContext context) {
    if (context.getPlayer() instanceof ServerPlayer player) {
      var spell = context.getItemInHand().get(Inkomancy.SPELL_COMPONENT_TYPE.get());
      if (spell != null) {
        var writer = new SpellWriter(new Random(), context.getLevel());
        var forwards = Util.randomOf(Direction.stream().filter(x -> x != context.getClickedFace() && x != context.getClickedFace().getOpposite()).toList());
        var transform = Transform2D.of(context.getClickedFace(), forwards);
        var glyphs = new LinkedList<SpellWriter.PositionedGlyph>();
        if (!writer.addGlyphs(spell, context.getClickedPos().relative(transform.facing()).relative(transform.backwards()), transform, glyphs)) {
          player.displayClientMessage(Component.literal("Not enough space to paint inscribed spell"), true);
        }

        for (var block : writer.getBlocks(glyphs, transform)) {
          context.getLevel().setBlockAndUpdate(block, ink.block().defaultBlockState().setValue(InkBlock.FACING, transform.facing()));
        }

        context.getLevel().playSound(null, context.getClickedPos(), SoundEvents.INK_SAC_USE, SoundSource.NEUTRAL);

        return InteractionResult.SUCCESS_SERVER;
      }
    }

    return super.useOn(context);
  }
}
