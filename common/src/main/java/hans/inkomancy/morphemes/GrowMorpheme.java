package hans.inkomancy.morphemes;

import hans.inkomancy.*;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.BonemealableBlock;

import java.util.Set;

public class GrowMorpheme extends Morpheme {
  public static final GrowMorpheme INSTANCE = new GrowMorpheme();

  private GrowMorpheme() {
    super("grow", Set.of(Type.ACTION));
  }

  @Override
  public void interpretAsAction(Spell spell, SpellContext context, boolean undo) throws InterpretError {
    var centers = new Args(spell, context).getFlat(Type.POSITION, m -> m::interpretAsPositions).map(Position::blockPos).toList();
    for (var center : centers) {
      for (var horizontal : new BlockPos[]{center, center.north(), center.east(), center.south(), center.west()}) {
        for (var pos : new BlockPos[]{horizontal, horizontal.above(), horizontal.below()}) {
          var state = context.world().getBlockState(pos);
          if (state.getBlock() instanceof BonemealableBlock fertilizable
              && fertilizable.isValidBonemealTarget(context.world(), pos, state)
              && fertilizable.isBonemealSuccess(context.world(), context.world().random, pos, state)) {
            context.mana().consume(16);
            fertilizable.performBonemeal(context.world(), context.world().random, pos, state);
            EffectUtils.growEffect(context.world(), pos);
            break;
          }
        }
      }
    }
  }
}
