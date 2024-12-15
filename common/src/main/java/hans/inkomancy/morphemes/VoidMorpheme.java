package hans.inkomancy.morphemes;

import hans.inkomancy.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
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
    return entities.stream().map(Delegate::of).toList();
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
    var entities = getArg(spell, context, 0, List.<EntityConvertible>of(), x -> this::interpretAsEntityConvertible);

    for (var entityConvertible : entities) {
      var entity = entityConvertible.withPos(pos);
      if (context.world().getEntity(entity.get().getUUID()) == null) {
        context.world().addFreshEntity(entity.get());
      }
    }
  }

  public interface EntityConvertible {
    Delegate<? extends Entity> withPos(Vec3 pos) throws InterpretError;
  }

  public List<EntityConvertible> interpretAsEntityConvertible(Spell spell, SpellContext context) throws InterpretError {
    var entities = new ArrayList<EntityConvertible>();

    if (spell.morpheme().supported.contains(Type.ENTITIES)) {
      for (var delegate : spell.morpheme().interpretAsEntities(spell, context)) {
        entities.add(pos -> {
          delegate.update(entity ->
              EffectUtils.teleport(context.world(), entity,
                  new TeleportTransition(context.world(), pos, entity.getDeltaMovement(), entity.getYRot(), entity.getXRot(), TeleportTransition.DO_NOTHING)));
          return delegate;
        });
      }
    } else if (spell.morpheme().supported.contains(Type.ITEMS)) {
      for (var item : spell.morpheme().interpretAsItems(spell, context)) {
        entities.add(pos -> Delegate.of(new ItemEntity(context.world(), pos.x(), pos.y(), pos.z(), item.get())));
      }
    } else {
      throw new InterpretError.Conversion(spell.morpheme(), List.of(Type.ENTITIES, Type.ITEMS));
    }

    return entities;
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
      if (modified == null) {
        entity.kill(context.world());
        EffectUtils.destroyEffect(context.world(), entity);
      } else {
        entity.setItem(modified.copy());
        EffectUtils.magicEffect(context.world(), entity.position());
      }
    }
  }
}
