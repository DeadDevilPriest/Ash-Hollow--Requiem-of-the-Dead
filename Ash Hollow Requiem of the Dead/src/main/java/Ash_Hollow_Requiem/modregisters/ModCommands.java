package Ash_Hollow_Requiem.modregisters;

import Ash_Hollow_Requiem.Ash_Hollow;
import Ash_Hollow_Requiem.bounty.BountyCommand;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Ash_Hollow.MODID)
public class ModCommands {
    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        BountyCommand.register(event.getDispatcher());
    }
}