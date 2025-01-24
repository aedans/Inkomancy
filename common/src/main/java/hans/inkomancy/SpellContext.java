package hans.inkomancy;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public record SpellContext(
    ServerLevel world,
    @Nullable ServerPlayer caster,
    @Nullable BlockPos sourcePos,
    Ink ink,
    ManaProvider mana) {
  public BlockPos getPosition(Spell spell, int offset) {
    return spell.pos() != null && spell.dir() != null ? spell.pos().relative(spell.dir(), offset) : sourcePos;
  }
}
