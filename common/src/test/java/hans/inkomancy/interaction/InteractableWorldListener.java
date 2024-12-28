package hans.inkomancy.interaction;

import com.mojang.datafixers.util.Pair;
import hans.inkomancy.Delegate;
import hans.inkomancy.InteractableWorld;
import hans.inkomancy.Morpheme;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
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

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

public class InteractableWorldListener implements InteractableWorld {
  public final List<Interaction> interactions = new ArrayList<>();
  public final Map<UUID, Morpheme.Position> positions = new HashMap<>();
  public final InteractableWorld.EntityRef.Null<ServerPlayer> player = new InteractableWorld.EntityRef.Null<>();

  {
    positions.put(player.uuid(), new Morpheme.Position(BlockPos.ZERO));
  }

  @Override
  public Pair<Integer, List<? extends Delegate<ItemStack>>> breakBlock(BlockPos pos, Vec3 origin, boolean drop) {
    return null;
  }

  @Override
  public @Nullable <T extends RecipeInput> Function<ItemStack, ItemStack> canTransmute(RecipeType<? extends Recipe<T>> recipe, T inventory) {
    return null;
  }

  @Override
  public @Nullable Runnable canGrow(BlockPos pos) {
    return null;
  }

  @Override
  public void throwProjectile(EntityRef<? extends LivingEntity> player) {

  }

  @Override
  public void teleport(BlockPos source, Vec3 target) {
    interactions.add(new Interaction.Teleport(source, target));
  }

  @Override
  public BlockPos getSpawn(@Nullable EntityRef<ServerPlayer> player) {
    return BlockPos.ZERO;
  }

  @Override
  public <T extends Entity> List<T> getEntities(EntityTypeTest<Entity, T> entityTypeTest, AABB aABB, Predicate<? super T> predicate) {
    return List.of();
  }

  @Override
  public void addEntity(Function<Level, Entity> entity) {

  }

  @Override
  public void removeEntity(Entity entity) {

  }

  @Override
  public Morpheme.Position getPosition(EntityRef<? extends Entity> entityRef) {
    return positions.get(entityRef.uuid());
  }

  @Override
  public void playSound(BlockPos pos, SoundEvent event) {

  }

  @Override
  public <T extends ParticleOptions> void playParticles(T particleOptions, Vec3 pos, Vec3 offset, int number, double speed) {

  }
}
