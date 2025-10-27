package Ash_Hollow_Requiem.blocks;

import Ash_Hollow_Requiem.blockentities.InkPressBlockEntity;
import Ash_Hollow_Requiem.modregisters.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

/**
 * Ink Press Block - Advanced automated bounty paper creation machine
 *
 * Features:
 * - Toggle on/off with Shift + Right Click
 * - Three interaction zones: Paper Input, Ink Input, Output
 * - Automatic processing when materials are present
 * - Animated belt and pressing mechanism
 * - Dynamic texture switching (static when OFF, animated when ON)
 *
 * Hitboxes calculated from actual Blockbench model data
 */
public class InkPressBlock extends BaseEntityBlock {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty POWERED = BooleanProperty.create("powered");

    public InkPressBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(POWERED, false));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection().getOpposite())
                .setValue(POWERED, false);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, POWERED);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new InkPressBlockEntity(pos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide ? null : (lvl, pos, st, blockEntity) -> {
            if (blockEntity instanceof InkPressBlockEntity inkPress) {
                inkPress.tick();
            }
        };
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos,
                                 Player player, InteractionHand hand, BlockHitResult hit) {

        if (!(level.getBlockEntity(pos) instanceof InkPressBlockEntity blockEntity)) {
            return InteractionResult.PASS;
        }

        boolean isPowered = state.getValue(POWERED);
        ItemStack heldItem = player.getItemInHand(hand);
        Vec3 hitPos = hit.getLocation();
        Direction facing = state.getValue(FACING);

        // Calculate relative hit position
        Vec3 relativeHit = getRelativeHitPosition(hitPos, pos, facing);

        // ===== SHIFT + RIGHT CLICK: Toggle Power =====
        if (player.isShiftKeyDown() && heldItem.isEmpty()) {
            if (!level.isClientSide) {
                boolean newPoweredState = !isPowered;
                level.setBlock(pos, state.setValue(POWERED, newPoweredState), 3);

                if (newPoweredState) {
                    blockEntity.turnOn();
                    player.displayClientMessage(
                            Component.literal("⚡ Ink Press turned ON")
                                    .withStyle(ChatFormatting.GREEN),
                            true
                    );
                    level.playSound(null, pos, SoundEvents.LEVER_CLICK,
                            SoundSource.BLOCKS, 0.8f, 1.2f);
                } else {
                    blockEntity.turnOff();
                    player.displayClientMessage(
                            Component.literal("🔴 Ink Press turned OFF")
                                    .withStyle(ChatFormatting.RED),
                            true
                    );
                    level.playSound(null, pos, SoundEvents.LEVER_CLICK,
                            SoundSource.BLOCKS, 0.8f, 0.8f);
                }
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.CONSUME;
        }

        // Machine must be powered for other interactions
        if (!isPowered) {
            if (!level.isClientSide) {
                player.displayClientMessage(
                        Component.literal("⚠ Machine is OFF (Shift + Right Click to turn on)")
                                .withStyle(ChatFormatting.GRAY),
                        true
                );
            }
            return InteractionResult.FAIL;
        }

        // ===== PAPER INPUT ZONE =====
        if (isInPaperInputZone(relativeHit) && heldItem.is(Items.PAPER)) {
            if (!level.isClientSide) {
                int amount = heldItem.getCount();
                blockEntity.addPaper(amount);
                heldItem.shrink(amount);

                player.displayClientMessage(
                        Component.literal("📄 Added " + amount + " paper")
                                .withStyle(ChatFormatting.YELLOW),
                        true
                );
                level.playSound(null, pos, SoundEvents.ITEM_PICKUP,
                        SoundSource.BLOCKS, 0.5f, 1.0f);
            }
            return InteractionResult.SUCCESS;
        }

        // ===== INK INPUT ZONE =====
        if (isInInkInputZone(relativeHit) && heldItem.is(ModItems.SOUL_INK_BOTTLE.get())) {
            if (!level.isClientSide) {
                int amount = heldItem.getCount();
                blockEntity.addInk(amount);
                heldItem.shrink(amount);

                player.displayClientMessage(
                        Component.literal("🖊 Added " + amount + " Soul Ink")
                                .withStyle(ChatFormatting.DARK_PURPLE),
                        true
                );
                level.playSound(null, pos, SoundEvents.BOTTLE_FILL,
                        SoundSource.BLOCKS, 0.5f, 0.8f);
            }
            return InteractionResult.SUCCESS;
        }

        // ===== OUTPUT ZONE =====
        if (isInOutputZone(relativeHit) && heldItem.isEmpty()) {
            if (!level.isClientSide) {
                int outputCount = blockEntity.getOutputCount();
                if (outputCount > 0) {
                    ItemStack output = new ItemStack(ModItems.BOUNTY_PAPER.get(), outputCount);

                    if (!player.addItem(output)) {
                        ItemEntity itemEntity = new ItemEntity(level,
                                pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5,
                                output);
                        level.addFreshEntity(itemEntity);
                    }

                    blockEntity.clearOutput();
                    player.displayClientMessage(
                            Component.literal("✓ Collected " + outputCount + " Bounty Paper")
                                    .withStyle(ChatFormatting.GREEN),
                            true
                    );
                    level.playSound(null, pos, SoundEvents.ITEM_PICKUP,
                            SoundSource.BLOCKS, 0.7f, 1.2f);
                    return InteractionResult.SUCCESS;
                } else {
                    player.displayClientMessage(
                            Component.literal("⚠ Output is empty")
                                    .withStyle(ChatFormatting.GRAY),
                            true
                    );
                }
            }
            return InteractionResult.CONSUME;
        }

        // ===== DEFAULT: Show status =====
        if (!level.isClientSide) {
            player.displayClientMessage(
                    Component.literal("📊 Paper: " + blockEntity.getPaperCount() +
                                    " | Ink: " + blockEntity.getInkCount() +
                                    " | Output: " + blockEntity.getOutputCount())
                            .withStyle(ChatFormatting.AQUA),
                    true
            );
        }
        return InteractionResult.SUCCESS;
    }

    /**
     * Convert world hit position to relative position based on block rotation
     */
    private Vec3 getRelativeHitPosition(Vec3 hitPos, BlockPos pos, Direction facing) {
        Vec3 localHit = hitPos.subtract(pos.getX(), pos.getY(), pos.getZ());

        // Rotate based on facing direction
        return switch (facing) {
            case NORTH -> localHit;
            case SOUTH -> new Vec3(1 - localHit.x, localHit.y, 1 - localHit.z);
            case WEST -> new Vec3(localHit.z, localHit.y, 1 - localHit.x);
            case EAST -> new Vec3(1 - localHit.z, localHit.y, localHit.x);
            default -> localHit;
        };
    }

    /**
     * Check if hit position is in paper input zone
     *
     * Model data: origin [-20, 16, -3], size [1, 11, 6]
     * Location: Left side, upper area
     * Player-friendly hitbox with generous bounds
     */
    private boolean isInPaperInputZone(Vec3 relativeHit) {
        return relativeHit.x >= 0.0 && relativeHit.x <= 0.15 &&
                relativeHit.y >= 0.45 && relativeHit.y <= 0.90 &&
                relativeHit.z >= 0.35 && relativeHit.z <= 0.65;
    }

    /**
     * Check if hit position is in ink input zone
     *
     * Model data: origin [-23, 6.712, -18.5], size [6, 8, 2], rotation [-45, 0, 0]
     * Location: Front-left bottom area, rotated 45° forward
     * Expanded hitbox to account for rotation
     */
    private boolean isInInkInputZone(Vec3 relativeHit) {
        return relativeHit.x >= 0.0 && relativeHit.x <= 0.25 &&
                relativeHit.y >= 0.15 && relativeHit.y <= 0.55 &&
                relativeHit.z >= 0.0 && relativeHit.z <= 0.25;
    }

    /**
     * Check if hit position is in output zone
     *
     * Model data: origin [3, 16, -3], size [1, 11, 6], rotation [0, 180, 0]
     * Location: Right side, upper area (mirror of paper input)
     * Matches paper input dimensions but on opposite side
     */
    private boolean isInOutputZone(Vec3 relativeHit) {
        return relativeHit.x >= 0.85 && relativeHit.x <= 1.0 &&
                relativeHit.y >= 0.45 && relativeHit.y <= 0.90 &&
                relativeHit.z >= 0.35 && relativeHit.z <= 0.65;
    }

    /**
     * Check if hit position is in control panel zone
     *
     * Model data: origin [-12, 12, -19.5], size [8, 4, 1], rotation [-45, 0, 0]
     * Location: Center-front, rotated down 45°
     * Currently unused but reserved for future features (GUI, upgrades, etc.)
     */
    @SuppressWarnings("unused")
    private boolean isInControlPanelZone(Vec3 relativeHit) {
        return relativeHit.x >= 0.2 && relativeHit.x <= 0.6 &&
                relativeHit.y >= 0.3 && relativeHit.y <= 0.6 &&
                relativeHit.z >= 0.0 && relativeHit.z <= 0.15;
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            if (level.getBlockEntity(pos) instanceof InkPressBlockEntity blockEntity) {
                blockEntity.dropContents(level, pos);
            }
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }
}
