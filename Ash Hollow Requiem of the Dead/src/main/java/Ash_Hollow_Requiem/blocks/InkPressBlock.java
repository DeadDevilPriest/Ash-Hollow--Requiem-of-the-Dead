package Ash_Hollow_Requiem.blocks;

import Ash_Hollow_Requiem.modregisters.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import

/**
 * Ink Press Block - Converts paper + ink sac into bounty paper
 * Right-click with paper (requires ink sac in inventory)
 */
public class InkPressBlock extends Block {

    public InkPressBlock(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos,
                                 Player player, InteractionHand hand, BlockHitResult hit) {

        ItemStack heldItem = player.getItemInHand(hand);

        // ✅ Check if player is holding paper
        if (heldItem.is(Items.PAPER)) {
            if (!level.isClientSide) {
                // ✅ Check if player has ink sac in inventory
                if (player.getInventory().contains(new ItemStack(Items.INK_SAC))) {
                    // Consume 1 paper
                    heldItem.shrink(1);

                    // Consume 1 ink sac
                    removeSoulInkBottleFromInventory(player);

                    // Create bounty paper
                    ItemStack bountyPaper = new ItemStack(ModItems.BOUNTY_PAPER.get(), 1);

                    // Give to player or drop
                    if (!player.addItem(bountyPaper)) {
                        ItemEntity itemEntity = new ItemEntity(level,
                                pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5,
                                bountyPaper);
                        level.addFreshEntity(itemEntity);
                    }

                    // Play stamping sound
                    level.playSound(null, pos, SoundEvents.BOOK_PAGE_TURN,
                            SoundSource.BLOCKS, 1.0f, 0.8f);

                    // Success message
                    player.displayClientMessage(
                            Component.literal("✓ Bounty Paper stamped!")
                                    .withStyle(ChatFormatting.GREEN),
                            true
                    );

                    return InteractionResult.SUCCESS;
                } else {
                    // No ink sac
                    player.displayClientMessage(
                            Component.literal("⚠ Need Soul Ink Bottles in inventory!")
                                    .withStyle(ChatFormatting.RED),
                            true
                    );

                    // Play error sound
                    level.playSound(null, pos, SoundEvents.VILLAGER_NO,
                            SoundSource.BLOCKS, 1.0f, 1.0f);

                    return InteractionResult.FAIL;
                }
            }
            return InteractionResult.SUCCESS;
        } else {
            // Not holding paper - show hint
            if (!level.isClientSide) {
                player.displayClientMessage(
                        Component.literal("Place Paper here (needs Soul Ink Bottles)")
                                .withStyle(ChatFormatting.GRAY),
                        true
                );
            }
            return InteractionResult.PASS;
        }
    }

    /**
     * ✅ Remove one ink sac from player's inventory
     */
    private void removeSoulInkBottleFromInventory(Player player) {
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.is(Items.SOUL_INK_BOTTLE)) {
                stack.shrink(1);
                return;
            }
        }
    }
}