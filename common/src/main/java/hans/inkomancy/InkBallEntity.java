package hans.inkomancy;

import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;

public class InkBallEntity extends ThrowableItemProjectile {
  public InkBallEntity(EntityType<? extends InkBallEntity> entityType, Level world) {
    super(entityType, world);
  }

  public InkBallEntity(ServerLevel world, LivingEntity shooter, ItemStack stack) {
    super(Inkomancy.INK_BALL_ENTITY.get(), shooter, world, stack);
  }

  @Override
  protected double getDefaultGravity() {
    return 0.02;
  }

  @Override
  public void tick() {
    super.tick();
    if (level() instanceof ServerLevel world) {
      var particle = new ItemParticleOption(ParticleTypes.ITEM, getItem());
      world.sendParticles(particle, getX(), getY() + .5, getZ(), 1, 0, 0, 0, .1);
    }
  }

  @Override
  protected void onHit(HitResult state) {
    super.onHit(state);
    if (level() instanceof ServerLevel world) {
      var particle = new ItemParticleOption(ParticleTypes.ITEM, getItem());
      world.sendParticles(particle, getX(), getY() + .5, getZ(), 10, 0, 0, 0, .1);
      discard();
    }
  }

  @Override
  protected @NotNull Item getDefaultItem() {
    return Inkomancy.INK_BALL.get();
  }
}
