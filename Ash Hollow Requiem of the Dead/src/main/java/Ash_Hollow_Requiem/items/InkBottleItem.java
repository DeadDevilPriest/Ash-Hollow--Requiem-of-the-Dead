package Ash_Hollow_Requiem.items;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

/**
 * Ink Bottle - Standard ink for regular contracts
 */
public class InkBottleItem extends Item {

    public InkBottleItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("Standard quality ink")
                .withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.literal("Used for stamping bounty papers")
                .withStyle(ChatFormatting.DARK_GRAY));
    }
}