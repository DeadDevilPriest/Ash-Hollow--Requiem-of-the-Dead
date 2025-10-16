package Ash_Hollow_Requiem.items;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

/**
 * Soul Ink Bottle - Rare ink infused with soul fragments
 * Used for special/cursed contracts
 */
public class SoulInkBottleItem extends Item {

    public SoulInkBottleItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("Ink infused with tormented souls")
                .withStyle(ChatFormatting.DARK_PURPLE));
        tooltip.add(Component.literal("Used for rare bounty contracts")
                .withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.literal("Whispers can be heard from within...")
                .withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true; // Enchanted glint effect
    }
}