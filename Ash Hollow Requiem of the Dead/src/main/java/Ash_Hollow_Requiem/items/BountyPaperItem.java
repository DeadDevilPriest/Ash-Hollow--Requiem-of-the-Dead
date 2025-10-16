package Ash_Hollow_Requiem.items;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

/**
 * Bounty Paper - Special paper for official bounty contracts
 * Created by processing regular paper in an Ink Press
 */
public class BountyPaperItem extends Item {

    public BountyPaperItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("Official contract paper")
                .withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.literal("Stamped and certified for bounty work")
                .withStyle(ChatFormatting.DARK_GRAY));
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true; // Enchanted glint to show it's special
    }
}