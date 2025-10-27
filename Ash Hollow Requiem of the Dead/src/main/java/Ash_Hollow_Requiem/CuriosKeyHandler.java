package Ash_Hollow_Requiem;

import Ash_Hollow_Requiem.bounty.Bounty;
import Ash_Hollow_Requiem.bounty.BountyManager;
import Ash_Hollow_Requiem.interfaces.IntegratedBountyDetailsScreen;
import Ash_Hollow_Requiem.interfaces.SkillInterface;
import Ash_Hollow_Requiem.modregisters.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.List;

import static Ash_Hollow_Requiem.Ash_Hollow.MODID;

@Mod.EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
public class CuriosKeyHandler {

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        // Check if bounty book is equipped
        if (KeyBindings.OPEN_BOUNTY_KEY.consumeClick()) {
            if (isItemEquipped(mc.player, ModItems.BOUNTY_BOOK.get())) {
                // Open bounty screen
                mc.setScreen(new IntegratedBountyDetailsScreen(null, getCurrentBounty(mc.player)));
            }
        }

        // Check if alchemist tool is equipped
        if (KeyBindings.OPEN_SKILL_INTERFACE_KEY.consumeClick()) {
            if (isItemEquipped(mc.player, ModItems.ALCHEMISTS_ENHANCEMENT_TOOL.get())) {
                // Open skill interface screen
                mc.setScreen(new SkillInterface(Component.literal("Alchemist's Enhancement Tool"), mc.player)); // ✅ FIXED!
            }
        }
    }

    private static boolean isItemEquipped(Player player, Item item) {
        return CuriosApi.getCuriosInventory(player).map(handler -> {
            return handler.findFirstCurio(item).isPresent();
        }).orElse(false);
    }

    private static Bounty getCurrentBounty(Player player) {
        List<Bounty> bounties = BountyManager.getPlayerBounties(player.getUUID());
        return bounties.isEmpty() ? null : bounties.get(0);
    }
}