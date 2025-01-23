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
  public BlockPos getPosition(Spell spell) {
    return spell.pos() != null && spell.dir() != null ? spell.pos().relative(spell.dir()) : sourcePos;
  }

  public void playSound(BlockPos pos, SoundEvent event) {
    world.playSound(null, pos, event, SoundSource.NEUTRAL);
  }

  public <T extends ParticleOptions> void playParticles(T particleOptions, Vec3 pos, Vec3 offset, int number, double speed) {
    world.sendParticles(particleOptions, pos.x, pos.y, pos.z, number, offset.x, offset.y, offset.z, speed);
  }
}
