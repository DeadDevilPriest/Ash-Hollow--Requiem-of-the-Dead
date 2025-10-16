package Ash_Hollow_Requiem.items;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

/**
 * Soul Fragment - Dropped by hostile mobs
 * Used to create Soul Ink for special contracts
 */
public class SoulFragmentItem extends Item {

    public SoulFragmentItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("Fragment of a tormented soul")
                .withStyle(ChatFormatting.DARK_PURPLE));
        tooltip.add(Component.literal("Used to create Soul Ink")
                .withStyle(ChatFormatting.GRAY));
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true; // Enchanted glint effect
    }
}