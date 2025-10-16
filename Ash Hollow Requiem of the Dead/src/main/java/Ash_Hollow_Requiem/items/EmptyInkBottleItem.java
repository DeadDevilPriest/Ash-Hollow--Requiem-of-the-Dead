package Ash_Hollow_Requiem.items;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

/**
 * Empty Ink Bottle - Can be filled with ink or soul ink
 */
public class EmptyInkBottleItem extends Item {

    public EmptyInkBottleItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("An empty glass bottle")
                .withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.literal("Can be filled at an Ink Mixer")
                .withStyle(ChatFormatting.DARK_GRAY));
    }
}