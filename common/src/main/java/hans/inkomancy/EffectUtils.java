package hans.inkomancy;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.Vec3;

public class EffectUtils {
  public static void magicEffect(ServerLevel world, Vec3 pos) {
    world.sendParticles(ParticleTypes.EFFECT, pos.x(), pos.y() + .2, pos.z(), 10, 0, 0, 0, .1);
  }

  public static void inkEffect(ServerLevel world, BlockPos pos) {
    world.playSound(null, pos, SoundEvents.INK_SAC_USE, SoundSource.NEUTRAL);
  }

  public static void enchantEffect(ServerLevel world, BlockPos pos) {
    world.playSound(null, pos, SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.BLOCKS);
  }

  public static void growEffect(ServerLevel world, BlockPos pos) {
    world.sendParticles(ParticleTypes.HAPPY_VILLAGER, pos.getX() + .5, pos.getY() + .25, pos.getZ() + .5, 10, .25, .25, .25, 0);
    world.playSound(null, pos, SoundEvents.BONE_MEAL_USE, SoundSource.NEUTRAL);
  }

  public static void repairEffect(ServerLevel world, BlockPos pos) {
    world.playSound(null, pos, SoundEvents.ANVIL_USE, SoundSource.NEUTRAL);
  }

  public static void smokeEffect(ServerLevel world, BlockPos pos) {
    var p = pos.getCenter();
    world.sendParticles(ParticleTypes.SMOKE, p.x(), p.y(), p.z(), 5, .25, .25, .25, 0);
  }

  public static void dustEffect(ServerLevel world, BlockPos pos, String color) {
    var p = pos.getCenter();
    world.sendParticles(new DustParticleOptions(Inkomancy.HEXES.get(Inkomancy.COLORS.indexOf(color)), 1.0F), p.x(), p.y(), p.z(), 3, .25, .25, .25, 0);
  }

  public static void transmuteEffect(ServerLevel world, BlockPos pos) {
    world.playSound(null, pos, SoundEvents.CRAFTER_CRAFT, SoundSource.NEUTRAL);
  }

  public static void teleport(ServerLevel world, Entity entity, TeleportTransition to) {
    EffectUtils.teleportEffect(world, entity);
    entity.teleport(to);
    EffectUtils.teleportEffect(world, entity);
  }

  public static void teleportEffect(ServerLevel world, Entity entity) {
    var pos = entity.position();
    var particles = Math.min(1000, (int) (Math.max(1, entity.getBbWidth()) * Math.max(1, entity.getBbHeight())) * 50);
    world.sendParticles(ParticleTypes.PORTAL, pos.x(), pos.y(), pos.z(), particles,
        entity.getBbWidth(), entity.getBbHeight(), entity.getBbWidth(), 0);
    world.playSound(null, entity.blockPosition(), SoundEvents.ENDERMAN_TELEPORT, SoundSource.NEUTRAL);
  }

  public static void destroyEffect(ServerLevel world, ItemEntity entity) {
    var particle = new ItemParticleOption(ParticleTypes.ITEM, entity.getItem());
    world.sendParticles(particle, entity.getX(), entity.getY() + .5, entity.getZ(), 10, 0, 0, 0, .1);
    world.playSound(null, entity.blockPosition(), SoundEvents.ITEM_BREAK, SoundSource.NEUTRAL);
  }
}
