package hans.inkomancy.fabric.gametest;

import hans.inkomancy.*;
import hans.inkomancy.inks.ConductiveInk;
import hans.inkomancy.morphemes.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestAssertException;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * A tiny DSL for building a {@link Spell} tree and casting it against a GameTest world, so each test
 * reads like a spell a player could paint. Coordinates are relative to the test structure (same
 * convention as {@link GameTestHelper#spawn}); {@link #cast} converts them to world space.
 */
public final class Spells {
  private Spells() {}

  public static Spell swap(Spell... args) {
    return new Spell(SwapMorpheme.INSTANCE, List.of(args));
  }

  public static Spell read(Spell... args) {
    return new Spell(ReadMorpheme.INSTANCE, List.of(args));
  }

  /** Bare {@code self()} is the caster; {@code self(hole(...))} re-scopes to players found there. */
  public static Spell self(Spell... args) {
    return new Spell(SelfMorpheme.INSTANCE, List.of(args));
  }

  /**
   * A {@code hole} whose 3x3x3 search box is centered on the block {@code (x, y, z)}.
   * HoleMorpheme searches around {@code getPosition(spell, 1) == pos.relative(dir, 1)}, so we face
   * the hole {@code UP} and drop {@code pos} one block down to land the center exactly on the block.
   */
  public static Spell hole(int x, int y, int z) {
    return new Spell(HoleMorpheme.INSTANCE, List.of(), new BlockPos(x, y - 1, z), Direction.UP);
  }

  /** Cast with no caster and effectively unlimited mana. */
  public static void cast(GameTestHelper helper, Spell spell) {
    cast(helper, spell, null, Integer.MAX_VALUE, false);
  }

  /** Cast as the given caster (its respawn point feeds the "send home" fallback). */
  public static void cast(GameTestHelper helper, Spell spell, ServerPlayer caster) {
    cast(helper, spell, caster, Integer.MAX_VALUE, false);
  }

  /**
   * Interpret {@code spell} as an action against the test world. Positions in the tree are relative
   * to the structure and are absolutized here. Exceptions propagate (unlike {@link Morpheme#interpret},
   * which swallows them) so a broken spell fails the test loudly.
   */
  public static void cast(GameTestHelper helper, Spell spell, @Nullable ServerPlayer caster, int mana, boolean undo) {
    var absolute = absolutize(helper, spell);
    var context = new SpellContext(
        helper.getLevel(), caster, ConductiveInk.INSTANCE,
        new ManaProvider(ConductiveInk.INSTANCE, mana), null, null);
    try {
      absolute.morpheme().interpretAsAction(absolute, context, undo);
    } catch (InterpretError e) {
      throw new RuntimeException("spell failed to interpret: " + spell, e);
    }
  }

  /** True if {@code entity} sits at the center of the (structure-relative) block {@code (x, y, z)}. */
  public static boolean isAt(GameTestHelper helper, Entity entity, int x, int y, int z) {
    return entity.position().distanceTo(helper.absolutePos(new BlockPos(x, y, z)).getCenter()) < 1e-6;
  }

  /** Assert {@code entity} sits at an absolute world position (for targets outside the structure, e.g. world spawn). */
  public static void assertAt(Entity entity, Vec3 expected) {
    if (entity.position().distanceTo(expected) > 1e-6) {
      throw new GameTestAssertException("expected entity at " + expected + " but was " + entity.position());
    }
  }

  private static Spell absolutize(GameTestHelper helper, Spell spell) {
    var children = spell.connected().stream().map(child -> absolutize(helper, child)).toList();
    var pos = spell.pos() == null ? null : helper.absolutePos(spell.pos());
    return new Spell(spell.morpheme(), children, pos, spell.dir());
  }
}
