package hans.inkomancy.morphemes;

import hans.inkomancy.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Set;

public class VoidMorpheme extends Morpheme {
  public static final VoidMorpheme INSTANCE = new VoidMorpheme();

  private VoidMorpheme() {
    super("void", Set.of(Type.ENTITIES, Type.ITEMS, Type.POSITION, Type.ACTION));
  }

  @Override
  public List<? extends Delegate<? extends Entity>> interpretAsEntities(Spell spell, SpellContext context) {
    var box = getBox(spell, context);
    var entities = context.world().getEntities(EntityTypeTest.forClass(Entity.class), box, x -> true);
    return entities.stream().map(Delegate.Instance::new).toList();
  }

  @Override
  public List<? extends Delegate<ItemStack>> interpretAsItems(Spell spell, SpellContext context) {
    var box = getBox(spell, context);
    var entities = context.world().getEntities(EntityTypeTest.forClass(ItemEntity.class), box, x -> true);
    return entities.stream().map(entity -> new ItemStackEntityDelegate(context, entity)).toList();
  }

  @Override
  public List<Position> interpretAsPositions(Spell spell, SpellContext context) {
    var items = interpretAsItems(spell, context);
    for (var item : items) {
      var tracker = item.get().get(DataComponents.LODESTONE_TRACKER);
      if (tracker != null && tracker.target().isPresent()) {
        return List.of(new Position(tracker.target().get().pos()));
      }
    }

    return List.of(new Position(context.getPosition(spell)));
  }

  @Override
  public void interpretAsAction(Spell spell, SpellContext context) throws InterpretError {
    var pos = context.getPosition(spell).getCenter();

    var items = new Args(spell, context).get(Type.ITEMS, x -> x::interpretAsItems)
        .stream().flatMap(List::stream).toList();
    for (var delegate : items) {
      context.world().addFreshEntity(new ItemEntity(context.world(), pos.x(), pos.y(), pos.z(), delegate.get()));
      delegate.action(true);
    }
  }

  public AABB getBox(Spell spell, SpellContext context) {
    var position = context.getPosition(spell);
    return AABB.encapsulatingFullBlocks(position.offset(-1, -1, -1), position.offset(1, 1, 1));
  }

  private record ItemStackEntityDelegate(SpellContext context, ItemEntity entity) implements Delegate<ItemStack> {
    public ItemStack get() {
      return entity.getItem();
    }

    public void set(ItemStack modified) {
      entity.setItem(modified.copy());
      EffectUtils.magicEffect(context.world(), entity.position());
    }

    @Override
    public void action(boolean replace) {
      if (replace) {
        entity.kill(context.world());
        var particle = new ItemParticleOption(ParticleTypes.ITEM, entity.getItem());
        context.playParticles(particle, entity.position(), Vec3.ZERO, 10, .1);
        context.playSound(entity.blockPosition(), SoundEvents.ITEM_BREAK);
      }
    }
  }
}
