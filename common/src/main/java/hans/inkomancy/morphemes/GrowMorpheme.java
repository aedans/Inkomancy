package hans.inkomancy.morphemes;

import hans.inkomancy.*;
import net.minecraft.core.BlockPos;

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
          var grow = context.world().canGrow(pos);
          if (grow != null) {
            context.mana().consume(16);
            grow.run();
            break;
          }
        }
      }
    }
  }
}
