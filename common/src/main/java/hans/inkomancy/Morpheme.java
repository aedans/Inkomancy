package hans.inkomancy;

import com.mojang.serialization.Codec;
import dev.architectury.registry.registries.RegistrySupplier;
import hans.inkomancy.morphemes.*;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class Morpheme {
  public static final Codec<Morpheme> CODEC = Codec.STRING.xmap(Morpheme::of, (m) -> m.name);
  public static final StreamCodec<ByteBuf, Morpheme> PACKET_CODEC = ByteBufCodecs.STRING_UTF8.map(Morpheme::of, (m) -> m.name);

  public final String name;
  public final Set<Type> supported;
  private RegistrySupplier<MorphemeItem> item;

  protected Morpheme(String name, Set<Type> supported) {
    this.name = name;
    this.supported = supported;
  }

  public void register() {
    this.item = Inkomancy.registerMorphemeItem(this);
  }

  public MorphemeItem getItem() {
    return item.get();
  }

  public static Morpheme[] getMorphemes() {
    return new Morpheme[]{
        BetweenMorpheme.INSTANCE,
        BreakMorpheme.INSTANCE,
        DirectionMorpheme.LEFT,
        DirectionMorpheme.RIGHT,
        DirectionMorpheme.FORWARDS,
        DirectionMorpheme.BACKWARDS,
        DirectionMorpheme.FORWARDS_LEFT,
        DirectionMorpheme.FORWARDS_RIGHT,
        DirectionMorpheme.BACKWARDS_LEFT,
        DirectionMorpheme.BACKWARDS_RIGHT,
        DirectionMorpheme.UP,
        DirectionMorpheme.DOWN,
        ForeverMorpheme.INSTANCE,
        GrowMorpheme.INSTANCE,
        HoleMorpheme.INSTANCE,
        MatchMorpheme.INSTANCE,
        ReadMorpheme.INSTANCE,
        RepairMorpheme.INSTANCE,
        SelfMorpheme.INSTANCE,
        SourceMorpheme.INSTANCE,
        StarMorpheme.INSTANCE,
        SummonMorpheme.INSTANCE,
        SwapMorpheme.INSTANCE,
        ToolMorpheme.INSTANCE,
        TransmuteMorpheme.INSTANCE,
        UndoMorpheme.INSTANCE,
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

  public void interpretAsAction(Spell spell, SpellContext context, boolean undo) throws InterpretError {
    throw new InterpretError.Conversion(this, Type.ACTION);
  }

  public void interpret(Spell spell, SpellContext context) {
    try {
      interpretAsAction(spell, context, false);
    } catch (Exception e) {
      e.printStackTrace();
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

  // One option in a multi-type value argument (see Args.getAny). A child that supports this
  // candidate's type and yields a non-empty result resolves to it.
  public record Candidate<T>(Type type, Function<Morpheme, Interpreter<List<T>>> resolve) {
    // Read the child as `type`, mapping each interpreted value into the common argument type T.
    public static <V, T> Candidate<T> value(Type type, Function<Morpheme, Interpreter<List<V>>> interpreter, Function<V, T> map) {
      return new Candidate<>(type, m -> (s, c) -> interpreter.apply(m).interpret(s, c).stream().map(map).collect(Collectors.toList()));
    }
  }

  public record Args(List<Spell> connected, Spell spell, SpellContext context) {
    public Args(Spell spell, SpellContext context) {
      this(new ArrayList<>(spell.connected()), spell, context);
    }

    public <T> Stream<T> getFlat(Type type, Function<Morpheme, Interpreter<List<T>>> f) throws InterpretError {
      var list = get(type, f).flatMap(Collection::stream).collect(Collectors.toList());
      Collections.shuffle(list);
      return list.stream();
    }

    public <T> Stream<T> get(Type type, Function<Morpheme, Interpreter<T>> f) throws InterpretError {
      var list = new ArrayList<T>();
      for (var s : List.copyOf(connected)) {
        if (s.morpheme().supported.contains(type)) {
          connected.remove(s);
          list.add(f.apply(s.morpheme()).interpret(s, context));
        }
      }
      Collections.shuffle(list);
      return list.stream();
    }

    // Presence of a specific child morpheme, matched by identity and consumed so it is never also
    // read as a value. This is how a morpheme declares a flag (e.g. HoleMorpheme's `self` scope).
    public boolean flag(Morpheme flag) {
      for (var s : List.copyOf(connected)) {
        if (s.morpheme() == flag) {
          connected.remove(s);
          return true;
        }
      }
      return false;
    }

    // Multi-type value argument: each child resolves to the highest-precedence candidate whose type
    // it supports and that yields a non-empty result. The sole home of the precedence +
    // first-non-empty rule. Resolved children are consumed.
    @SafeVarargs
    public final <T> Stream<T> getAny(Candidate<T>... candidates) throws InterpretError {
      var list = new ArrayList<T>();
      for (var s : List.copyOf(connected)) {
        for (var candidate : candidates) {
          if (s.morpheme().supported.contains(candidate.type())) {
            var values = candidate.resolve().apply(s.morpheme()).interpret(s, context);
            if (!values.isEmpty()) {
              list.addAll(values);
              connected.remove(s);
              break;
            }
          }
        }
      }
      Collections.shuffle(list);
      return list.stream();
    }
  }

  public record Position(Vec3 absolute, BlockPos blockPos) {
    public Position(BlockPos pos) {
      this(pos.getCenter(), pos);
    }

    public Position(Vec3 absolute) {
      this(absolute, BlockPos.containing(absolute.x, absolute.y, absolute.z));
    }
  }
}
