package hans.inkomancy;

import hans.inkomancy.interaction.InteractableWorldListener;
import hans.inkomancy.interaction.Interaction;
import hans.inkomancy.morphemes.SwapMorpheme;
import net.minecraft.core.BlockPos;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class MirrorTest {
  Spell spell = new Spell(SwapMorpheme.INSTANCE);

  @Test
  void testMirror() throws InterpretError {
    var world = new InteractableWorldListener();
    spell.morpheme().interpretAsAction(spell, new SpellContext(world, ManaProvider.infinite(), world.player, null));
    Assertions.assertIterableEquals(
        List.of(new Interaction.Teleport(BlockPos.ZERO, BlockPos.ZERO.above().getCenter())),
        world.interactions
    );
  }

  @Test
  void testMirrorOOM() {
    var world = new InteractableWorldListener();
    world.positions.put(world.player.uuid(), new Morpheme.Position(new BlockPos(0, 1000, 0)));
    Assertions.assertThrowsExactly(InterpretError.OOM.class, () ->
        spell.morpheme().interpretAsAction(spell, new SpellContext(world, ManaProvider.of(1), world.player, null)));
    Assertions.assertEquals(0, world.interactions.size());
  }
}
