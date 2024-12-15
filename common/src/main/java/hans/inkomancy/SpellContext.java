package hans.inkomancy;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public record SpellContext(
    ServerLevel world,
    ServerPlayer caster,
    BlockPos sourcePos,
    Ink ink,
    ManaProvider mana) {
  public BlockPos getPosition(Spell spell) {
    return spell.pos() != null && spell.dir() != null ? spell.pos().relative(spell.dir()) : sourcePos;
  }
}
