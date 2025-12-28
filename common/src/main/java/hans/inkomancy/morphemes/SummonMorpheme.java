package hans.inkomancy.morphemes;

import hans.inkomancy.*;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.SpawnEggItem;
import java.util.Set;

public class SummonMorpheme extends Morpheme {
  public static final SummonMorpheme INSTANCE = new SummonMorpheme();

  protected SummonMorpheme() {
    super("summon", Set.of(Type.ACTION));
  }

  @Override
  public void interpretAsAction(Spell spell, SpellContext context, boolean undo) throws InterpretError {
    var items = new Args(spell, context).getFlat(Type.ITEMS, x -> x::interpretAsItems).toList();
    for (var item : items) {
      if (item.get().getItem() instanceof SpawnEggItem egg) {
        var entityType = egg.getType(item.get());
        entityType.spawn(context.world(), item.get(), context.caster(), context.getPosition(spell, 2), MobSpawnType.MOB_SUMMONED, false, false);
      }
    }
  }
}
