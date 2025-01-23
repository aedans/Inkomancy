package hans.inkomancy;

import dev.architectury.registry.registries.RegistrySupplier;
import hans.inkomancy.inks.BlackInk;
import hans.inkomancy.inks.RedInk;
import hans.inkomancy.inks.VoidInk;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.Set;
import java.util.function.Function;

public abstract class Ink {
  public final String name;
  private RegistrySupplier<InkBlock> block;
  private RegistrySupplier<InkItem> item;
  private RegistrySupplier<BlockEntityType<InkBlockEntity>> blockEntity;

  public Ink(String name) {
    this.name = name;
  }

  public void register() {
    this.block = Inkomancy.registerInkBlock(this);
    this.item = Inkomancy.registerInkItem(this);
    this.blockEntity = Inkomancy.registerInkBlockEntity(this);
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

  public InkBlock getBlock() {
    return block.get();
  }

  public InkItem getItem() {
    return item.get();
  }

  public BlockEntityType<InkBlockEntity> getBlockEntity() {
    return blockEntity.get();
  }

  public abstract int getMana(Set<BlockPos> blocks);

  public abstract int modifyMana(int initial, int mana, int amount) throws InterpretError;

  public abstract void handleBlock(ServerLevel world, BlockPos pos);

  public abstract String lore();

  public abstract ToolMaterial material();
}
