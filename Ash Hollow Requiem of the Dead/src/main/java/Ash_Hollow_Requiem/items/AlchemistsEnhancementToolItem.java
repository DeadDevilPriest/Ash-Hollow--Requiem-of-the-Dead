package Ash_Hollow_Requiem.items;

import Ash_Hollow_Requiem.Ash_Hollow;
import Ash_Hollow_Requiem.bounty.Bounty;
import Ash_Hollow_Requiem.bounty.BountyManager;
import Ash_Hollow_Requiem.interfaces.IntegratedBountyDetailsScreen;
import Ash_Hollow_Requiem.interfaces.SkillInterface;
import Ash_Hollow_Requiem.skilltributes.Skills;
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
import Ash_Hollow_Requiem.skilltributes.Skills;

import java.util.List;

public class AlchemistsEnhancementToolItem extends Item implements ICurioItem {
    public AlchemistsEnhancementToolItem(Item.Properties properties) {
        super(properties);
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, CompoundTag nbt) {
        System.out.println("🔧 AlchemistTool initCapabilities called!");

        ICapabilityProvider provider = CuriosApi.createCurioProvider(new ICurio() {
            @Override
            public ItemStack getStack() {
                return stack;
            }

            @Override
            public void curioTick(SlotContext slotContext) {
                AlchemistsEnhancementToolItem.this.curioTick(slotContext, stack);
            }

            @Override
            public void onEquip(SlotContext slotContext, ItemStack prevStack) {
                AlchemistsEnhancementToolItem.this.onEquip(slotContext, prevStack, stack);
            }

            @Override
            public void onUnequip(SlotContext slotContext, ItemStack newStack) {
                AlchemistsEnhancementToolItem.this.onUnequip(slotContext, newStack, stack);
            }

            /*@Override
            public boolean canEquipFromUse(SlotContext slotContext) {
                System.out.println("🎯 AlchemistTool canEquipFromUse called!");
                return true;
            }*/

            @Override
            public SoundInfo getEquipSound(SlotContext slotContext) {
                return new ICurio.SoundInfo(SoundEvents.BREWING_STAND_BREW, 1.0f, 1.0f);
            }
        });

        System.out.println("✅ AlchemistTool Provider created: " + (provider != null ? "SUCCESS" : "NULL"));
        return provider;
    }

    // ========== HAND USE ========== //
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (level.isClientSide) {
            //openAlchemistsEnhancmentTool(player);
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    // ========== CURIOS INTEGRATION ========== //

    /**
     * Called every tick while equipped in Curios slot
     */
    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        if (slotContext.entity() instanceof Player player) {
            if (!player.level().isClientSide) {
                onAlchemistsEnhancementToolEquippedTick(player, stack);
            }
        }
    }

    /**
     * Called when the tool is equipped in Curios slot
    **/
    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
        if (slotContext.entity() instanceof Player player) {
            if (!player.level().isClientSide) {
                player.sendSystemMessage(
                        Component.literal("The Alchemist's Enhancement Tool pulses with arcane energy...")
                                .withStyle(net.minecraft.ChatFormatting.DARK_PURPLE)
                );
                onAlchemistsEnhancementToolEquipped(player, stack);
            }
        }
    }

    /**
     * Called when the tool is unequipped from Curios slot
     */
    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        if (slotContext.entity() instanceof Player player) {
            if (!player.level().isClientSide) {
                player.sendSystemMessage(
                        Component.literal("The alchemical connection fades...")
                                .withStyle(net.minecraft.ChatFormatting.GRAY)
                );
                onAlchemistsEnhancementToolUnequipped(player, stack);
            }
        }
    }

    @Override
    public boolean canEquip(SlotContext slotContext, ItemStack stack) {
        String identifier = slotContext.identifier();
        System.out.println("===========================================");
        System.out.println("🧪 AlchemistTool canEquip DEBUG:");
        System.out.println("   Slot identifier: '" + identifier + "'");
        System.out.println("   Contains 'alchemists_tool': " + identifier.contains("alchemists_tool"));
        System.out.println("   Stack: " + stack.getItem().toString());
        System.out.println("===========================================");

        boolean canEquip = identifier.contains("alchemists_tool");
        System.out.println("🧪 AlchemistTool canEquip called! Slot: " + identifier + ", Can Equip: " + canEquip);
        return canEquip;
    }

    /**
     * Allow right-clicking to equip from inventory
     */
    /*@Override
    public boolean canEquipFromUse(SlotContext slotContext, ItemStack stack) {
        return true;
    }*/

    /**
     * Play a sound when equipped
     */
    @Override
    public ICurio.SoundInfo getEquipSound(SlotContext slotContext, ItemStack stack) {
        return new ICurio.SoundInfo(SoundEvents.BREWING_STAND_BREW, 1.0f, 1.0f);
    }

    // ========== CUSTOM TOOL BEHAVIOR ========== //

    protected void onAlchemistsEnhancementToolEquippedTick(Player player, ItemStack stack) {
        // Passive effects while equipped
        if (player.tickCount % 100 == 0) {
            // Add any passive effects here
        }
    }

    protected void onAlchemistsEnhancementToolEquipped(Player player, ItemStack stack) {
        // Enable hotkey for Skill Interface when tool is equipped
        if (player.level().isClientSide) {
            // Register/enable the skill system hotkey
        }
    }

    protected void onAlchemistsEnhancementToolUnequipped(Player player, ItemStack stack) {
        // Disable hotkey for Skill Interface when tool is unequipped
        if (player.level().isClientSide) {
            // Unregister/disable the skill system hotkey
        }
    }


    /**
     * Opens the SkillInterface
     */
    @OnlyIn(Dist.CLIENT)
    private void openAlchemistsEnhancementTool(Player player) {
        Minecraft.getInstance().setScreen(
                new SkillInterface(null, player)
        );
    }

    // ========== TOOLTIP & VISUALS ========== //

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("Right-click to open the Alchemist's Enhancement Tool")
                .withStyle(net.minecraft.ChatFormatting.GRAY));

        // Show Curios hint if loaded
        if (Ash_Hollow.curiosLoaded) {
            tooltip.add(Component.literal("Can be equipped in Curios Alchemist Enhancement Tool slot")
                    .withStyle(net.minecraft.ChatFormatting.DARK_PURPLE));
        }

        tooltip.add(Component.literal("Use to enhance you with alchemical powers")
                .withStyle(net.minecraft.ChatFormatting.DARK_GRAY));
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true; // Enchanted glint effect
    }
}