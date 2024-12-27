package hans.inkomancy;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public interface InteractableWorld {
  Pair<Integer, List<? extends Delegate<ItemStack>>> breakBlock(BlockPos pos, Vec3 origin, boolean drop);

  <T extends RecipeInput> @Nullable Function<ItemStack, ItemStack> canTransmute(RecipeType<? extends Recipe<T>> recipe, T inventory);

  @Nullable Runnable canGrow(BlockPos pos);

  void throwProjectile(ServerPlayer player);

  void teleport(Entity entity, Vec3 target);

  BlockPos getSpawn(@Nullable ServerPlayer player);

  <T extends Entity> List<T> getEntities(EntityTypeTest<Entity, T> entityTypeTest, AABB aABB, Predicate<? super T> predicate);

  void addEntity(Function<Level, Entity> entity);

  void removeEntity(Entity entity);

  void playSound(BlockPos pos, SoundEvent event);

  <T extends ParticleOptions> void playParticles(T particleOptions, Vec3 pos, Vec3 offset, int number, double speed);

  static InteractableWorld of(ServerLevel level) {
    return new InteractableLevel(level);
  }
}
