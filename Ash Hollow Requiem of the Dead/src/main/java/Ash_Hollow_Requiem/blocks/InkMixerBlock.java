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

/**
 * Ink Mixer Block - Creates ink bottles
 * Regular Ink: Empty Bottle + Ink Sac
 * Soul Ink: Empty Bottle + Ink Sac + Soul Fragment
 */
public class InkMixerBlock extends Block {

    public InkMixerBlock(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos,
                                 Player player, InteractionHand hand, BlockHitResult hit) {

        ItemStack heldItem = player.getItemInHand(hand);

        // ✅ Check if player is holding an empty ink bottle
        if (heldItem.is(ModItems.EMPTY_INK_BOTTLE.get())) {
            if (!level.isClientSide) {

                // Check for Soul Ink creation (Empty Bottle + Ink Sac + Soul Fragment)
                if (player.getInventory().contains(new ItemStack(Items.INK_SAC)) &&
                        player.getInventory().contains(new ItemStack(ModItems.SOUL_FRAGMENT.get()))) {

                    return createSoulInk(level, pos, player, heldItem);

                }
                // Check for Regular Ink creation (Empty Bottle + Ink Sac)
                else if (player.getInventory().contains(new ItemStack(Items.INK_SAC))) {

                    return createRegularInk(level, pos, player, heldItem);

                } else {
                    // Missing ingredients
                    player.displayClientMessage(
                            Component.literal("⚠ Need Ink Sac in inventory!")
                                    .withStyle(ChatFormatting.RED),
                            true
                    );
                    level.playSound(null, pos, SoundEvents.VILLAGER_NO,
                            SoundSource.BLOCKS, 1.0f, 1.0f);
                    return InteractionResult.FAIL;
                }
            }
            return InteractionResult.SUCCESS;
        } else {
            // Not holding empty bottle - show hint
            if (!level.isClientSide) {
                player.displayClientMessage(
                        Component.literal("Place Empty Ink Bottle here")
                                .withStyle(ChatFormatting.GRAY),
                        true
                );
            }
            return InteractionResult.PASS;
        }
    }

    /**
     * ✅ Create regular ink bottle
     */
    private InteractionResult createRegularInk(Level level, BlockPos pos, Player player, ItemStack heldItem) {
        // Consume items
        heldItem.shrink(1); // Empty bottle
        removeItemFromInventory(player, Items.INK_SAC); // Ink sac

        // Create ink bottle
        ItemStack inkBottle = new ItemStack(ModItems.INK_BOTTLE.get(), 1);

        // Give to player or drop
        if (!player.addItem(inkBottle)) {
            ItemEntity itemEntity = new ItemEntity(level,
                    pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5,
                    inkBottle);
            level.addFreshEntity(itemEntity);
        }

        // Play mixing sound
        level.playSound(null, pos, SoundEvents.BREWING_STAND_BREW,
                SoundSource.BLOCKS, 1.0f, 1.2f);

        // Success message
        player.displayClientMessage(
                Component.literal("✓ Ink Bottle created!")
                        .withStyle(ChatFormatting.GREEN),
                true
        );

        return InteractionResult.SUCCESS;
    }

    /**
     * ✅ Create soul ink bottle
     */
    private InteractionResult createSoulInk(Level level, BlockPos pos, Player player, ItemStack heldItem) {
        // Consume items
        heldItem.shrink(1); // Empty bottle
        removeItemFromInventory(player, Items.INK_SAC); // Ink sac
        removeItemFromInventory(player, ModItems.SOUL_FRAGMENT.get()); // Soul fragment

        // Create soul ink bottle
        ItemStack soulInkBottle = new ItemStack(ModItems.SOUL_INK_BOTTLE.get(), 1);

        // Give to player or drop
        if (!player.addItem(soulInkBottle)) {
            ItemEntity itemEntity = new ItemEntity(level,
                    pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5,
                    soulInkBottle);
            level.addFreshEntity(itemEntity);
        }

        // Play eerie mixing sound
        level.playSound(null, pos, SoundEvents.SOUL_ESCAPE,
                SoundSource.BLOCKS, 1.0f, 0.8f);
        level.playSound(null, pos, SoundEvents.BREWING_STAND_BREW,
                SoundSource.BLOCKS, 0.7f, 0.6f);

        // Success message with dramatic flair
        player.displayClientMessage(
                Component.literal("✓ Soul Ink created!")
                        .withStyle(ChatFormatting.DARK_PURPLE, ChatFormatting.BOLD),
                true
        );

        player.displayClientMessage(
                Component.literal("The bottle whispers...")
                        .withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC),
                true
        );

        return InteractionResult.SUCCESS;
    }

    /**
     * ✅ Remove one item from player's inventory
     */
    private void removeItemFromInventory(Player player, net.minecraft.world.item.Item item) {
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.is(item)) {
                stack.shrink(1);
                return;
            }
        }
    }
}