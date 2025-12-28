package hans.inkomancy.morphemes;

import hans.inkomancy.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;

import java.util.List;
import java.util.Set;

public class HoleMorpheme extends Morpheme {
  public static final HoleMorpheme INSTANCE = new HoleMorpheme();

  private HoleMorpheme() {
    super("hole", Set.of(Type.ENTITIES, Type.ITEMS, Type.ACTION, Type.POSITION));
  }

  @Override
  public List<Position> interpretAsPositions(Spell spell, SpellContext context) {
    return List.of(new Position(context.getPosition(spell, 1)));
  }

  @Override
  public List<? extends Delegate<? extends Entity>> interpretAsEntities(Spell spell, SpellContext context) {
    var box = getBox(spell, context);
    var entities = context.world().getEntities(EntityTypeTest.forClass(Entity.class), box, x -> true);
    return entities.stream().map(Delegate.Instance::new).toList();
  }

  @Override
  public List<? extends Delegate<ItemStack>> interpretAsItems(Spell spell, SpellContext context) {
    if (context.itemsInput() != null) {
      return context.itemsInput();
    }

    var box = getBox(spell, context);
    var entities = context.world().getEntities(EntityTypeTest.forClass(ItemEntity.class), box, x -> true);
    return entities.stream().map(entity -> new ItemStackEntityDelegate(context, entity)).toList();
  }

  @Override
  public void interpretAsAction(Spell spell, SpellContext context, boolean undo) throws InterpretError {
    var pos = context.getPosition(spell, 1).getCenter();

    var items = new Args(spell, context).getFlat(Type.ITEMS, x -> x::interpretAsItems).toList();
    for (var delegate : items) {
      var entity = new ItemEntity(context.world(), pos.x(), pos.y(), pos.z(), delegate.get());
      context.world().addFreshEntity(entity);
      EffectUtils.magicEffect(context.world(), entity.position());
      delegate.destroy();
    }
  }

  public AABB getBox(Spell spell, SpellContext context) {
    var position = context.getPosition(spell, 1);
    return AABB.encapsulatingFullBlocks(position.offset(-1, -1, -1), position.offset(1, 1, 1));
  }

  public record ItemStackEntityDelegate(SpellContext context, ItemEntity entity) implements Delegate<ItemStack> {
    public ItemStack get() {
      return entity.getItem();
    }

    public void set(ItemStack modified) {
      entity.setItem(modified.copy());
      EffectUtils.magicEffect(context.world(), entity.position());
    }

    @Override
    public boolean mutable() {
      return true;
    }

    @Override
    public void destroy() {
      entity.kill();
      EffectUtils.magicEffect(context.world(), entity.position());
    }
  }
}
