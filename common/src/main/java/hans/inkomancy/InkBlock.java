package hans.inkomancy;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.serialization.MapCodec;
import hans.inkomancy.inks.ConductiveInk;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashSet;
import java.util.Map;

public class InkBlock extends DirectionalBlock {
  public static final MapCodec<InkBlock> CODEC = BlockBehaviour.simpleCodec(InkBlock::new);
  public static final BooleanProperty NORTH_INK_CONNECTION = BooleanProperty.create("north");
  public static final BooleanProperty EAST_INK_CONNECTION = BooleanProperty.create("east");
  public static final BooleanProperty SOUTH_INK_CONNECTION = BooleanProperty.create("south");
  public static final BooleanProperty WEST_INK_CONNECTION = BooleanProperty.create("west");

  public static final Map<Direction, BooleanProperty> DIRECTION_CONNECTION = Maps
      .newEnumMap(
          ImmutableMap.of(
              Direction.NORTH, NORTH_INK_CONNECTION,
              Direction.EAST, EAST_INK_CONNECTION,
              Direction.SOUTH, SOUTH_INK_CONNECTION,
              Direction.WEST, WEST_INK_CONNECTION));

  public InkBlock(BlockBehaviour.Properties settings) {
    super(settings);

    this.registerDefaultState(defaultBlockState()
        .setValue(NORTH_INK_CONNECTION, false)
        .setValue(SOUTH_INK_CONNECTION, false)
        .setValue(EAST_INK_CONNECTION, false)
        .setValue(WEST_INK_CONNECTION, false)
        .setValue(FACING, Direction.UP));
  }

  public boolean canAttach(BlockState state, Transform2D transform) {
    return state.is(this) && state.getValue(InkBlock.FACING) == transform.facing();
  }

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    builder.add(NORTH_INK_CONNECTION, SOUTH_INK_CONNECTION, EAST_INK_CONNECTION, WEST_INK_CONNECTION);
    builder.add(FACING);
  }

  public InteractionResult use(BlockState state, BlockPos pos, Level world, @Nullable Player player) {
    if (world instanceof ServerLevel server && (player == null || player instanceof ServerPlayer)) {
      var ink = Ink.getBy(Ink::getBlock, this);
      var color = Ink.colorOf(this);
      var transform = Transform2D.of(state.getValue(FACING));
      var parser = new SpellParser(server, this);
      var connected = parser.connectedBlocks(pos, transform);

      var start = parser.findStart(connected);
      if (start == null) {
        return InteractionResult.CONSUME;
      }

      var blocks = new LinkedHashSet<BlockPos>();
      var facing = world.getBlockState(start.getFirst()).getValue(InkBlock.FACING);
      var spell = parser.parseSpell(start.getFirst(), Transform2D.of(facing, start.getSecond()), Glyph.START, blocks, 0);

      var mana = new ManaProvider(ink, ink.getMana(blocks));
      var context = new SpellContext(server, (ServerPlayer) player, null, ink, mana);
      spell.morpheme().interpret(spell, context);

      world.playSound(null, pos, ink.sound(), SoundSource.BLOCKS);

      for (var block : blocks) {
        ink.handleBlock(server, block, color);
      }
    }

    return InteractionResult.SUCCESS;
  }

  @Override
  protected @NotNull InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
    for (var itemStack : player.getHandSlots()) {
      if (itemStack.getItem() instanceof BlockItem item && item.getBlock() == this) {
        return InteractionResult.CONSUME;
      }
    }

    return use(state, pos, world, player);
  }

  @Override
  public BlockState getStateForPlacement(BlockPlaceContext ctx) {
    return getState(ctx.getLevel(), ctx.getClickedPos(), ctx.getClickedFace());
  }

  @Override
  protected void neighborChanged(BlockState state, Level world, BlockPos pos, Block sourceBlock,
                                 Orientation wireOrientation, boolean notify) {
    super.neighborChanged(state, world, pos, sourceBlock, wireOrientation, notify);

    var newState = getState(world, pos, state.getValue(FACING));
    if (newState == null) {
      dropResources(state, world, pos);
      world.removeBlock(pos, false);
      return;
    }

    if (Ink.getBy(Ink::getBlock, this) == ConductiveInk.INSTANCE && world.hasNeighborSignal(pos)) {
      use(state, pos, world, null);
    }

    world.setBlockAndUpdate(pos, newState);
  }

  public static boolean isInvalidPlacement(Level world, BlockPos pos, Direction facing) {
    return !world.getBlockState(pos.relative(facing.getOpposite())).isFaceSturdy(world, pos, facing);
  }

  public BlockState getState(Level world, BlockPos pos, Direction facing) {
    if (isInvalidPlacement(world, pos, facing)) {
      return null;
    }

    BlockState state = this.defaultBlockState().setValue(FACING, facing);
    var transform = Transform2D.of(facing);

    var properties = new BooleanProperty[]{
        NORTH_INK_CONNECTION, EAST_INK_CONNECTION,
        SOUTH_INK_CONNECTION, WEST_INK_CONNECTION
    };
    var directions = transform.directions();

    for (var i = 0; i < 4; i++) {
      var connection = properties[i];
      var direction = directions.get(i);

      var neighborState = world.getBlockState(pos.relative(direction));
      if (canAttach(neighborState, transform)) {
        state = state.setValue(connection, true);
      }
    }

    return state;
  }

  @Override
  protected @NotNull VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
    var facing = state.getValue(FACING);
    return Block.box(
        facing == Direction.WEST ? 15.0 : 0.0,
        facing == Direction.DOWN ? 15.0 : 0.0,
        facing == Direction.NORTH ? 15.0 : 0.0,
        facing == Direction.EAST ? 1.0 : 16.0,
        facing == Direction.UP ? 1.0 : 16.0,
        facing == Direction.SOUTH ? 1.0 : 16.0);
  }

  @Override
  protected @NotNull BlockState rotate(BlockState state, Rotation rotation) {
    return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
  }

  @Override
  protected @NotNull BlockState mirror(BlockState state, Mirror mirror) {
    return state.rotate(mirror.getRotation(state.getValue(FACING)));
  }

  @Override
  protected @NotNull MapCodec<? extends DirectionalBlock> codec() {
    return CODEC;
  }
}
