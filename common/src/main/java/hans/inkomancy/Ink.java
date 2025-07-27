package hans.inkomancy;

import dev.architectury.registry.registries.RegistrySupplier;
import hans.inkomancy.inks.ArdentInk;
import hans.inkomancy.inks.ConductiveInk;
import hans.inkomancy.inks.VoidInk;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;

public abstract class Ink {
  public final String name;
  public final Map<String, RegistrySupplier<InkBlock>> block = new HashMap<>();
  public final Map<String, RegistrySupplier<InkItem>> item = new HashMap<>();

  public Ink(String name) {
    this.name = name;
  }

  public void register() {
    for (var color : Inkomancy.COLORS) {
      this.block.put(color, Inkomancy.registerInkBlock(this, color));
      this.item.put(color, Inkomancy.registerInkItem(this, color));
    }
  }

  public static Ink[] getInks() {
    return new Ink[]{
        ArdentInk.INSTANCE,
        ConductiveInk.INSTANCE,
        VoidInk.INSTANCE
    };
  }

  public static <T> Ink getBy(BiFunction<Ink, String, T> f, T t) {
    for (var ink : getInks()) {
      for (var color : Inkomancy.COLORS) {
        if (f.apply(ink, color) == t) {
          return ink;
        }
      }
    }

    throw new Error("No ink for " + t);
  }

  public InkBlock getBlock(String color) {
    return block.get(color).get();
  }

  public InkItem getItem(String color) {
    return item.get(color).get();
  }

  public abstract SoundEvent sound();

  public abstract int getMana(Set<BlockPos> blocks);

  public abstract int modifyMana(int initial, int mana, int amount) throws InterpretError;

  public abstract void handleBlock(ServerLevel world, BlockPos pos);

  public abstract String lore();
}
