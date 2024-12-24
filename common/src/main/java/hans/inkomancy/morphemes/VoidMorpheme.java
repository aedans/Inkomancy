package hans.inkomancy.morphemes;

import hans.inkomancy.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.AABB;

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

    var entities = getArgs(spell, context, Type.ENTITIES, x -> x::interpretAsEntities)
        .stream().flatMap(List::stream).toList();
    for (var delegate : entities) {
      delegate.update(entity ->
          EffectUtils.teleport(context.world(), entity,
              new TeleportTransition(context.world(), pos, entity.getDeltaMovement(), entity.getYRot(), entity.getXRot(), TeleportTransition.DO_NOTHING)));
    }

    var items = getArgs(spell, context, Type.ITEMS, x -> x::interpretAsItems)
        .stream().flatMap(List::stream).toList();
    for (var delegate : items) {
      context.world().addFreshEntity(new ItemEntity(context.world(), pos.x(), pos.y(), pos.z(), delegate.get()));
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
