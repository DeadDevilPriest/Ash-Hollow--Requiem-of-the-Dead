package Ash_Hollow_Requiem;

import software.bernie.geckolib.GeckoLib;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@SuppressWarnings("ALL")
@Mod(Ash_Hollow.MODID)
public class Ash_Hollow {
    public static final String MODID = "ash_hollow_requiem_of_the_dead";

    public Ash_Hollow() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        GeckoLib.initialize();

        // Register items, entities, creative tabs
        ModItems.ITEMS.register(modEventBus);
        Ash_Hollow_Requiem.common.entities.ModEntities.ENTITIES.register(modEventBus);
        ModItems.CREATIVE_MODE_TABS.register(modEventBus);
        ModEffects.EFFECTS.register(modEventBus);

        // Forge event listeners
        MinecraftForge.EVENT_BUS.register(AudioEventHandler.class);
        MinecraftForge.EVENT_BUS.register(KeyBindingScreen.class);
        MinecraftForge.EVENT_BUS.register(MicrophoneSelectionScreen.class);
        MinecraftForge.EVENT_BUS.register(AshHollowOptionScreen.class);
        MinecraftForge.EVENT_BUS.register(ModDamageSources.class);
        MinecraftForge.EVENT_BUS.register(ModEffects.class);
    }
}
