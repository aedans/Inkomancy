package hans.inkomancy;

import net.minecraft.core.Direction;

import java.util.ArrayList;
import java.util.List;

public record Transform2D(Direction facing, List<Direction> directions) {
  public Direction forwards() {
    return directions.getFirst();
  }

  public Direction right() {
    return directions.get(1);
  }

  public Direction backwards() {
    return directions.get(2);
  }

  public Direction left() {
    return directions.get(3);
  }

  public Transform2D withForwards(Direction direction) {
    return of(facing, direction);
  }

  public static Transform2D of(Direction facing) {
    return of(facing, facing.getAxis() == Direction.Axis.Y ? Direction.NORTH : Direction.UP);
  }

  public static Transform2D of(Direction facing, Direction forwards) {
    var directions = new ArrayList<Direction>();
    var axis = facing.getAxis();

    for (var i = 0; i < 4; i++) {
      directions.add(forwards);
      forwards = facing == Direction.NORTH || facing == Direction.WEST || facing == Direction.DOWN
          ? forwards.getCounterClockWise(axis)
          : forwards.getClockWise(axis);
    }

    return new Transform2D(facing, directions);
  }
}
