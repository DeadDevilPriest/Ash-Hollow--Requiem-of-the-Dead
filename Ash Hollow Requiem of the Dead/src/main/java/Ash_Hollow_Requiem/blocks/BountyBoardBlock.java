package Ash_Hollow_Requiem.blocks;

import Ash_Hollow_Requiem.interfaces.BountyBoardScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BountyBoardBlock extends HorizontalDirectionalBlock {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    private static final VoxelShape SHAPE_NORTH = Block.box(-16, 0, 6, 32, 32, 10);
    private static final VoxelShape SHAPE_SOUTH = Block.box(-16, 0, 6, 32, 32, 10);
    private static final VoxelShape SHAPE_EAST = Block.box(6, 0, -16, 10, 32, 32);
    private static final VoxelShape SHAPE_WEST = Block.box(6, 0, -16, 10, 32, 32);


    public BountyBoardBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(FACING)) {
            case NORTH -> SHAPE_NORTH;
            case SOUTH -> SHAPE_SOUTH;
            case EAST -> SHAPE_EAST;
            case WEST -> SHAPE_WEST;
            default -> SHAPE_NORTH;
        };
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos,
                                 Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        // Open bounty board GUI
        net.minecraft.client.Minecraft.getInstance().setScreen(
                new BountyBoardScreen(
                        getPlayerCoins(player),
                        getPlayerTokens(player)
                )
        );

        return InteractionResult.SUCCESS;
    }

    private int getPlayerCoins(Player player) {
        // TODO: Get from player capability/NBT
        return 1000;
    }

    private int getPlayerTokens(Player player) {
        // TODO: Get from player capability/NBT
        return 5;
    }
}