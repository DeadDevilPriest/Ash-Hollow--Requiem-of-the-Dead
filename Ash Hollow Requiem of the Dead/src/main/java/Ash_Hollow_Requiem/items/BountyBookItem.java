package Ash_Hollow_Requiem.items;

import Ash_Hollow_Requiem.bounty.Bounty;
import Ash_Hollow_Requiem.bounty.BountyManager;
import Ash_Hollow_Requiem.interfaces.IntegratedBountyDetailsScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import

import java.util.List;

/**
 * The Bounty Book - Opens a screen showing your active bounty
 */
public class BountyBookItem extends Item {

    public BountyBookItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (level.isClientSide) {
            openBountyBook(player);
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    /**
     * ✅ Open the bounty book screen (client-side only)
     */
    @OnlyIn(Dist.CLIENT)
    private void openBountyBook(Player player) {
        // Get player's active bounties
        List<Bounty> activeBounties = BountyManager.getPlayerBounties(player.getUUID());

        // Show the first active bounty (or empty book if none)
        Bounty currentBounty = activeBounties.isEmpty() ? null : activeBounties.get(0);

        Minecraft.getInstance().setScreen(
                new IntegratedBountyDetailsScreen(null, currentBounty)
        );
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("Right-click to view your active bounty")
                .withStyle(net.minecraft.ChatFormatting.GRAY));
        tooltip.add(Component.literal("Track progress and claim rewards")
                .withStyle(net.minecraft.ChatFormatting.DARK_GRAY));
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true; // Enchanted glint effect
    }


}