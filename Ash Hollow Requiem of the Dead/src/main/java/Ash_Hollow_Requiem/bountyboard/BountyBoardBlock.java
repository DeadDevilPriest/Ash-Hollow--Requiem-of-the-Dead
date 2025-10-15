package Ash_Hollow_Requiem.bountyboard;

import Ash_Hollow_Requiem.interfaces.BountyBoardScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BountyBoardBlock extends Block {

    public BountyBoardBlock(Properties properties) {
        super(properties);
    }

    private static final VoxelShape SHAPE = Block.box(-16, 0, 6, 32, 32, 10
    );

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
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