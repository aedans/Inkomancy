package hans.inkomancy;

import com.mojang.serialization.Codec;
import hans.inkomancy.morphemes.*;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

public abstract class Morpheme {
  public static final Codec<Morpheme> CODEC = Codec.STRING.xmap(Morpheme::of, (m) -> m.name);
  public static final StreamCodec<ByteBuf, Morpheme> PACKET_CODEC = ByteBufCodecs.STRING_UTF8.map(Morpheme::of, (m) -> m.name);

  public String name;
  public Set<Type> supported;

  protected Morpheme(String name, Set<Type> supported) {
    this.name = name;
    this.supported = supported;
  }

  public static Morpheme[] getMorphemes() {
    return new Morpheme[]{
        BetweenMorpheme.INSTANCE,
        BreakMorpheme.INSTANCE,
        ForeverMorpheme.INSTANCE,
        GrowMorpheme.INSTANCE,
        ReadMorpheme.INSTANCE,
        RepairMorpheme.INSTANCE,
        SelfMorpheme.INSTANCE,
        SourceMorpheme.INSTANCE,
        StarMorpheme.INSTANCE,
        SwapMorpheme.INSTANCE,
        ToolMorpheme.INSTANCE,
        TransmuteMorpheme.SMELT,
        TransmuteMorpheme.CRAFT,
        VoidMorpheme.INSTANCE
    };
  }

  public static Morpheme of(String name) {
    for (var morpheme : getMorphemes()) {
      if (name.equals(morpheme.name)) {
        return morpheme;
      }
    }

    throw new Error("No morpheme " + name);
  }

  public Spell interpretAsSpell(Spell spell, SpellContext context) throws InterpretError {
    throw new InterpretError.Conversion(this, Type.SPELL);
  }

  public List<? extends Delegate<ItemStack>> interpretAsItems(Spell spell, SpellContext context) throws InterpretError {
    throw new InterpretError.Conversion(this, Type.ITEMS);
  }

  public List<? extends Delegate<? extends Entity>> interpretAsEntities(Spell spell, SpellContext context) throws InterpretError {
    throw new InterpretError.Conversion(this, Type.ENTITIES);
  }

  public List<Position> interpretAsPositions(Spell spell, SpellContext context) throws InterpretError {
    throw new InterpretError.Conversion(this, Type.POSITION);
  }

  public void interpretAsAction(Spell spell, SpellContext context) throws InterpretError {
    throw new InterpretError.Conversion(this, Type.ACTION);
  }

  public void interpret(Spell spell, SpellContext context) {
    try {
      interpretAsAction(spell, context);
    } catch (Exception ignored) {

    }
  }

  @Override
  public String toString() {
    return this.name;
  }

  public enum Type {
    SPELL, ITEMS, ENTITIES, POSITION, ACTION
  }

  public interface Interpreter<T> {
    T interpret(Spell spell, SpellContext context) throws InterpretError;
  }

  public <T> List<T> getArgs(Spell spell, SpellContext context, Type type, Function<Morpheme, Interpreter<T>> f) throws InterpretError {
    var list = new ArrayList<T>();
    for (var s : spell.connected()) {
      if (s.morpheme().supported.contains(type)) {
        list.add(f.apply(s.morpheme()).interpret(s, context));
      }
    }
    Collections.shuffle(list);
    return list;
  }

  public record Position(Vec3 absolute, BlockPos blockPos) {
    public Position(BlockPos pos) {
      this(pos.getCenter(), pos);
    }
  }
}
