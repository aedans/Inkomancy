package hans.inkomancy.interaction;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

public interface Interaction {
  record Teleport(BlockPos source, Vec3 target) implements Interaction {}
}
