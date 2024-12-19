package hans.inkomancy;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

public record SpellContext(
    ServerLevel world,
    @Nullable ServerPlayer caster,
    @Nullable BlockPos sourcePos,
    Ink ink,
    ManaProvider mana) {
  public BlockPos getPosition(Spell spell) {
    return spell.pos() != null && spell.dir() != null ? spell.pos().relative(spell.dir()) : sourcePos;
  }
}
