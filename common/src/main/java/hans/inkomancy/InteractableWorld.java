package hans.inkomancy;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
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
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Predicate;

public interface InteractableWorld {
  Pair<Integer, List<? extends Delegate<ItemStack>>> breakBlock(BlockPos pos, Vec3 origin, boolean drop);

  <T extends RecipeInput> @Nullable Function<ItemStack, ItemStack> canTransmute(RecipeType<? extends Recipe<T>> recipe, T inventory);

  @Nullable Runnable canGrow(BlockPos pos);

  void throwProjectile(EntityRef<? extends LivingEntity> player);

  void teleport(BlockPos source, Vec3 target);

  BlockPos getSpawn(@Nullable EntityRef<ServerPlayer> player);

  <T extends Entity> List<T> getEntities(EntityTypeTest<Entity, T> entityTypeTest, AABB aABB, Predicate<? super T> predicate);

  void addEntity(Function<Level, Entity> entity);

  void removeEntity(Entity entity);

  Morpheme.Position getPosition(EntityRef<? extends Entity> entityRef);

  void playSound(BlockPos pos, SoundEvent event);

  <T extends ParticleOptions> void playParticles(T particleOptions, Vec3 pos, Vec3 offset, int number, double speed);

  static InteractableWorld of(ServerLevel level) {
    return new InteractableLevel(level);
  }

  abstract class EntityRef<T extends Entity> {
    protected abstract T get();

    public abstract UUID uuid();

    public static class Instance<T extends Entity> extends EntityRef<T> {
      private final T entity;

      public Instance(T entity) {
        this.entity = entity;
      }

      @Override
      public T get() {
        return entity;
      }

      @Override
      public UUID uuid() {
        return entity.getUUID();
      }
    }

    public static class Null<T extends Entity> extends EntityRef<T> {
      private final UUID uuid = UUID.randomUUID();

      @Override
      public T get() {
        return null;
      }

      @Override
      public UUID uuid() {
        return uuid;
      }
    }
  }
}
