package hans.inkomancy.morphemes;

import hans.inkomancy.InterpretError;
import hans.inkomancy.Morpheme;
import hans.inkomancy.Spell;
import hans.inkomancy.SpellContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.phys.Vec3;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class GrowMorpheme extends Morpheme {
  public static final GrowMorpheme INSTANCE = new GrowMorpheme();

  private GrowMorpheme() {
    super("grow", Set.of(Type.ACTION));
  }

  @Override
  public void interpretAsAction(Spell spell, SpellContext context) throws InterpretError {
    var centers = new Args(spell, context).get(Type.POSITION, m -> m::interpretAsPositions)
        .stream().flatMap(List::stream).map(Position::blockPos).collect(Collectors.toList());
    Collections.shuffle(centers);
    for (var center : centers) {
      for (var horizontal : new BlockPos[]{center, center.north(), center.east(), center.south(), center.west()}) {
        for (var pos : new BlockPos[]{horizontal, horizontal.above(), horizontal.below()}) {
          var state = context.world().getBlockState(pos);
          if (state.getBlock() instanceof BonemealableBlock fertilizable
              && fertilizable.isValidBonemealTarget(context.world(), pos, state)
              && fertilizable.isBonemealSuccess(context.world(), context.world().random, pos, state)) {
            context.mana().consume(16);
            fertilizable.performBonemeal(context.world(), context.world().random, pos, state);
            context.playParticles(ParticleTypes.HAPPY_VILLAGER, pos.getBottomCenter().add(.5, .25, .5), new Vec3(.25, .25, .25), 10, 0);
            context.playSound(pos, SoundEvents.BONE_MEAL_USE);
            break;
          }
        }
      }
    }
  }
}
