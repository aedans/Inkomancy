package hans.inkomancy;

import hans.inkomancy.inks.BlackInk;
import hans.inkomancy.inks.RedInk;
import hans.inkomancy.inks.VoidInk;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.Set;
import java.util.function.Function;

public abstract class Ink {
  public final String name;

  public Ink(String name) {
    this.name = name;
  }

  public static Ink[] getInks() {
    return new Ink[]{
        BlackInk.INSTANCE,
        RedInk.INSTANCE,
        VoidInk.INSTANCE
    };
  }

  public static <T> Ink getBy(Function<Ink, T> f, T t) {
    for (var ink : getInks()) {
      if (f.apply(ink) == t) {
        return ink;
      }
    }

    throw new Error("No ink for " + t);
  }

  public abstract int getMana(Set<BlockPos> blocks);

  public abstract int consumeMana(int initial, int mana, int amount) throws InterpretError;

  public abstract void handleInvalidBlock(SpellParser parser, BlockPos pos);

  public abstract void handleBlock(ServerLevel world, BlockPos pos);

  public abstract String lore();

  public abstract ToolMaterial material();

  public abstract InkBlock block();

  public abstract BlockEntityType<InkBlockEntity> blockEntity();

  public abstract BlockItem item();
}
