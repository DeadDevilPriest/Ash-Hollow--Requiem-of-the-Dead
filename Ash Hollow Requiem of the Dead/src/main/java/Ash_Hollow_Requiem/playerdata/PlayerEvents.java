package Ash_Hollow_Requiem.playerdata;

import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

@Mod.EventBusSubscriber(modid = "ash_hollow_requiem_of_the_dead", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PlayerEvents {

    @SubscribeEvent
    public static void onAttachCapabilities(AttachCapabilitiesEvent<Player> event) {
        if (!event.getObject().getCapability(PlayerDataProvider.PLAYER_DATA).isPresent()) {
            event.addCapability(
                    new ResourceLocation("ash_hollow_requiem_of_the_dead", "playerdata"),
                    new PlayerDataProvider()
            );
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        event.getOriginal().reviveCaps();
        event.getOriginal().getCapability(PlayerDataProvider.PLAYER_DATA).ifPresent(oldData -> {
            event.getEntity().getCapability(PlayerDataProvider.PLAYER_DATA).ifPresent(newData -> {
                newData.setCoins(oldData.getCoins());
                newData.setTokens(oldData.getTokens());
            });
        });
        event.getOriginal().invalidateCaps();
    }
}
