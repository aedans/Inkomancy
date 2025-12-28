package hans.inkomancy.morphemes;

import hans.inkomancy.*;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;

import java.util.Set;

public class RepairMorpheme extends Morpheme {
  public static final RepairMorpheme INSTANCE = new RepairMorpheme();

  private RepairMorpheme() {
    super("repair", Set.of(Type.ACTION));
  }

  @Override
  public void interpretAsAction(Spell spell, SpellContext context, boolean undo) throws InterpretError {
    var items = new Args(spell, context).getFlat(Type.ITEMS, m -> m::interpretAsItems).toList();
    var entities = new Args(spell, context).getFlat(Type.ENTITIES, m -> m::interpretAsEntities).toList();

    for (var item : items) {
      if (undo) {
        if (item.get().getDamageValue() == item.get().getMaxDamage() - 1) {
          item.destroy();
        } else {
          item.update(stack -> stack.setDamageValue(stack.getDamageValue() + 1));
        }
      } else {
        if (item.get().isDamaged()) {
          item.update(stack -> stack.setDamageValue(stack.getDamageValue() - 1));
        }
      }
    }

    for (var entity : entities) {
      if (entity.get() instanceof LivingEntity livingEntity) {
        if (undo) {
          livingEntity.hurt(livingEntity.damageSources().generic(), 1);
        } else {
          livingEntity.heal(1);
        }
      }
    }
  }
}
