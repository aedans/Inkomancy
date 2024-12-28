package hans.inkomancy;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public record InteractableLevel(ServerLevel level) implements InteractableWorld {
  @Override
  public Pair<Integer, List<? extends Delegate<ItemStack>>> breakBlock(BlockPos pos, Vec3 origin, boolean drop) {
    var items = level.getBlockState(pos).getDrops(new LootParams.Builder(level)
        .withParameter(LootContextParams.ORIGIN, origin)
        .withParameter(LootContextParams.TOOL, ItemStack.EMPTY));
    return new Pair<>(
        (int) level.getBlockState(pos).getDestroySpeed(level, pos),
        items.stream().map(item -> new BlockItemDelegate(level, item, pos, drop)).toList()
    );
  }

  @Override
  public @Nullable <T extends RecipeInput> Function<ItemStack, ItemStack> canTransmute(RecipeType<? extends Recipe<T>> recipe, T inventory) {
    var match = level.recipeAccess().getRecipeFor(recipe, inventory, level);
    return match.<Function<ItemStack, ItemStack>>map(recipeHolder -> (stack) ->
        recipeHolder.value().assemble(inventory, level.registryAccess()).copyWithCount(stack.getCount())).orElse(null);
  }

  @Override
  public @Nullable Runnable canGrow(BlockPos pos) {
    var state = level.getBlockState(pos);
    if (state.getBlock() instanceof BonemealableBlock fertilizable
        && fertilizable.isValidBonemealTarget(level, pos, state)
        && fertilizable.isBonemealSuccess(level, level.random, pos, state)) {
      return () -> {
        fertilizable.performBonemeal(level, level.random, pos, state);
        level.sendParticles(ParticleTypes.HAPPY_VILLAGER, pos.getX() + .5, pos.getY() + .25, pos.getZ() + .5, 10, .25, .25, .25, 0);
        level.playSound(null, pos, SoundEvents.BONE_MEAL_USE, SoundSource.NEUTRAL);
      };
    }

    return null;
  }

  @Override
  public void throwProjectile(EntityRef<? extends LivingEntity> player) {
    ThrowableItemProjectile.spawnProjectileFromRotation(InkBallEntity::new, level, Inkomancy.INK_BALL.get().getDefaultInstance(), player.get(), 0, 1F, 1);
  }

  @Override
  public void teleport(BlockPos source, Vec3 target) {
    for (var entity : getEntities(EntityTypeTest.forClass(Entity.class), Util.getBox(source), x -> true)) {
      teleportEffect(entity);
      entity.teleport(new TeleportTransition(level, target, entity.getDeltaMovement(), entity.getYRot(), entity.getXRot(), TeleportTransition.DO_NOTHING));
      teleportEffect(entity);
    }
  }

  public void teleportEffect(Entity entity) {
    var particles = Math.min(1000, (int) (Math.max(1, entity.getBbWidth()) * Math.max(1, entity.getBbHeight())) * 50);
    playParticles(ParticleTypes.PORTAL, entity.position(), new Vec3(entity.getBbWidth(), entity.getBbHeight(), entity.getBbWidth()), particles, 0);
    playSound(entity.blockPosition(), SoundEvents.ENDERMAN_TELEPORT);
  }

  @Override
  public BlockPos getSpawn(@Nullable EntityRef<ServerPlayer> player) {
    if (player != null && player.get().getRespawnPosition() != null) {
      return player.get().getRespawnPosition();
    } else {
      return level.getSharedSpawnPos();
    }
  }

  @Override
  public <T extends Entity> List<T> getEntities(EntityTypeTest<Entity, T> entityTypeTest, AABB aABB, Predicate<? super T> predicate) {
    return level.getEntities(entityTypeTest, aABB, predicate);
  }

  @Override
  public void addEntity(Function<Level, Entity> entity) {
    level.addFreshEntity(entity.apply(level));
  }

  @Override
  public void removeEntity(Entity entity) {
    entity.kill(level);
  }

  @Override
  public Morpheme.Position getPosition(EntityRef<? extends Entity> entityRef) {
    return new Morpheme.Position(entityRef.get().position(), entityRef.get().blockPosition());
  }

  @Override
  public void playSound(BlockPos pos, SoundEvent event) {
    level.playSound(null, pos, event, SoundSource.NEUTRAL);
  }

  @Override
  public <T extends ParticleOptions> void playParticles(T particleOptions, Vec3 pos, Vec3 offset, int number, double speed) {
    level.sendParticles(particleOptions, pos.x, pos.y, pos.z, number, offset.x, offset.y, offset.z, speed);
  }

  record BlockItemDelegate(Level level, ItemStack item, BlockPos pos, boolean drop) implements Delegate<ItemStack> {
    @Override
    public ItemStack get() {
      return item;
    }

    @Override
    public void set(ItemStack modified) {

    }

    @Override
    public void action(boolean replace) {
      level.destroyBlock(pos, !replace);
    }
  }
}
