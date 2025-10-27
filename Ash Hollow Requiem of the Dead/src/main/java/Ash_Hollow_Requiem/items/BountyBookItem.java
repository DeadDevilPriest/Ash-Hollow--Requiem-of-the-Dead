package Ash_Hollow_Requiem.items;

import Ash_Hollow_Requiem.Ash_Hollow;
import Ash_Hollow_Requiem.bounty.Bounty;
import Ash_Hollow_Requiem.bounty.BountyManager;
import Ash_Hollow_Requiem.interfaces.IntegratedBountyDetailsScreen;
import Ash_Hollow_Requiem.modregisters.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;

/**
 * The Bounty Book - Opens a screen showing your active bounty
 * Can be equipped in Curios book slot
 */
public class BountyBookItem extends Item implements ICurioItem {

    public BountyBookItem(Properties properties) {
        super(properties);
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, CompoundTag nbt) {
        System.out.println("🔧 BountyBook initCapabilities called!");

        ICapabilityProvider provider = CuriosApi.createCurioProvider(new ICurio() {
            @Override
            public ItemStack getStack() {
                return stack;
            }

            @Override
            public void curioTick(SlotContext slotContext) {
                BountyBookItem.this.curioTick(slotContext, stack);
            }

            @Override
            public void onEquip(SlotContext slotContext, ItemStack prevStack) {
                BountyBookItem.this.onEquip(slotContext, prevStack, stack);
            }

            @Override
            public void onUnequip(SlotContext slotContext, ItemStack newStack) {
                BountyBookItem.this.onUnequip(slotContext, newStack, stack);
            }

            /*@Override
            public boolean canEquipFromUse(SlotContext slotContext) {
                System.out.println("🎯 canEquipFromUse called!");
                return true;
            }*/

            @Override
            public SoundInfo getEquipSound(SlotContext slotContext) {
                return new ICurio.SoundInfo(SoundEvents.BOOK_PUT, 1.0f, 1.0f);
            }
        });

        System.out.println("✅ Provider created: " + (provider != null ? "SUCCESS" : "NULL"));
        return provider;
    }


    // ========== HAND USE ========== //
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (level.isClientSide) {
            openBountyBook(player);
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    // ========== CURIOS INTEGRATION ========== //

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        if (slotContext.entity() instanceof Player player) {
            if (!player.level().isClientSide) {
                onBookEquippedTick(player, stack);
            }
        }
    }

    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
        if (slotContext.entity() instanceof Player player) {
            if (!player.level().isClientSide) {
                player.sendSystemMessage(
                        Component.literal("The Bounty Book pulses with dark energy...")
                                .withStyle(net.minecraft.ChatFormatting.DARK_PURPLE)
                );
                onBookEquipped(player, stack);
            }
        }
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        if (slotContext.entity() instanceof Player player) {
            if (!player.level().isClientSide) {
                player.sendSystemMessage(
                        Component.literal("The connection to your bounties fades...")
                                .withStyle(net.minecraft.ChatFormatting.GRAY)
                );
                onBookUnequipped(player, stack);
            }
        }
    }

    /*@Override
    public boolean canEquipFromUse(SlotContext slotContext, ItemStack stack) {
        return true;
    }*/

    @Override
    public ICurio.SoundInfo getEquipSound(SlotContext slotContext, ItemStack stack) {
        return new ICurio.SoundInfo(SoundEvents.BOOK_PUT, 1.0f, 1.0f);
    }

    // ========== CUSTOM BOOK BEHAVIOR ========== //

    protected void onBookEquippedTick(Player player, ItemStack stack) {
        if (player.tickCount % 100 == 0) {
            List<Bounty> activeBounties = BountyManager.getPlayerBounties(player.getUUID());
            if (!activeBounties.isEmpty()) {
                Bounty bounty = activeBounties.get(0);
                // Add progress notifications or effects here
            }
        }
    }

    protected void onBookEquipped(Player player, ItemStack stack) {
        // Enable hotkey for Active Bounty interface when book is equipped
        if (player.level().isClientSide) {
            // Register/enable the bounty hotkey
        }
    }

    protected void onBookUnequipped(Player player, ItemStack stack) {
        // Disable hotkey for Active Bounty interface when book is unequipped
        if (player.level().isClientSide) {
            // Unregister/disable the bounty hotkey
        }
    }

    // ========== SCREEN OPENING ========== //

    @OnlyIn(Dist.CLIENT)
    private void openBountyBook(Player player) {
        List<Bounty> activeBounties = BountyManager.getPlayerBounties(player.getUUID());
        Bounty currentBounty = activeBounties.isEmpty() ? null : activeBounties.get(0);

        Minecraft.getInstance().setScreen(
                new IntegratedBountyDetailsScreen(null, currentBounty)
        );
    }

    // ========== TOOLTIP & VISUALS ========== //

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("Right-click to view your active bounty")
                .withStyle(net.minecraft.ChatFormatting.GRAY));

        if (Ash_Hollow.curiosLoaded) {
            tooltip.add(Component.literal("Can be equipped in Curios book slot")
                    .withStyle(net.minecraft.ChatFormatting.DARK_PURPLE));
        }

        tooltip.add(Component.literal("Track progress and claim rewards")
                .withStyle(net.minecraft.ChatFormatting.DARK_GRAY));
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }
}