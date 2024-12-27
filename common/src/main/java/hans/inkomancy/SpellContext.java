package hans.inkomancy;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

public record SpellContext(
    InteractableWorld world,
    ManaProvider mana,
    @Nullable ServerPlayer caster,
    @Nullable BlockPos sourcePos
) {
  public BlockPos getPosition(Spell spell) {
    return spell.pos() != null && spell.dir() != null ? spell.pos().relative(spell.dir()) : sourcePos;
  }
}
