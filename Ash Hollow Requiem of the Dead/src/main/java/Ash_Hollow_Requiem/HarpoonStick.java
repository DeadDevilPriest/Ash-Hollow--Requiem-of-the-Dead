package Ash_Hollow_Requiem;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import Ash_Hollow_Requiem.common.entities.ModEntities;

public class HarpoonStick extends Item {
    public HarpoonStick(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!level.isClientSide) {
            // Server-side logic for using the Harpoon Stick
            // For example, launching a harpoon entity
            HarpoonEntity harpoon = new HarpoonEntity(ModEntities.HARPOON.get(), player, level);
            harpoon.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1.5F, 1.0F);
            level.addFreshEntity(harpoon);
        }

        player.swing(hand);
        stack.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(hand));

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }
}