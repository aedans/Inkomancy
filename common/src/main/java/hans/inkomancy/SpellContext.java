package hans.inkomancy;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public record SpellContext(
    ServerLevel world,
    @Nullable ServerPlayer caster,
    Ink ink,
    ManaProvider mana,
    @Nullable BlockPos positionInput,
    @Nullable List<? extends Delegate<ItemStack>> itemsInput) {
  public BlockPos getPosition(Spell spell, int offset) {
    return spell.pos() != null && spell.dir() != null ? spell.pos().relative(spell.dir(), offset) : positionInput;
  }
}
