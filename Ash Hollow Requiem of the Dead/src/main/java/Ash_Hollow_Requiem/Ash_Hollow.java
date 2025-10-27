package Ash_Hollow_Requiem;

import Ash_Hollow_Requiem.bounty.BountyEventHandler;
import Ash_Hollow_Requiem.client.AttributeNotificationRenderer;
import Ash_Hollow_Requiem.client.AttributeOverlayRenderer;
import Ash_Hollow_Requiem.effects.ModEffects;
import Ash_Hollow_Requiem.modregisters.ModBlockEntities;
import Ash_Hollow_Requiem.modregisters.ModBlocks;
import Ash_Hollow_Requiem.modregisters.ModEntities;
import Ash_Hollow_Requiem.modregisters.ModItems;
import Ash_Hollow_Requiem.network.PacketHandler;
import Ash_Hollow_Requiem.playerdata.PlayerDataEvents;
import software.bernie.geckolib.GeckoLib;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import static Ash_Hollow_Requiem.interfaces.ModCreativeTab.CREATIVE_MODE_TABS;

/**
 * Main Mod Class - Entry point for Ash Hollow: Requiem of the Dead
 */
@SuppressWarnings("ALL")
@Mod(Ash_Hollow.MODID)
public class Ash_Hollow {
    public static final String MODID = "ash_hollow_requiem_of_the_dead";

    // Track if Curios is loaded
    public static boolean curiosLoaded = false;

    public Ash_Hollow() {
        // Get the mod event bus (for registering things)
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Check if Curios is loaded
        curiosLoaded = ModList.get().isLoaded("curios");
        if (curiosLoaded) {
            System.out.println("✅ Curios API detected - Bounty Book can be equipped in book slot");
        } else {
            System.out.println("⚠️ Curios API not found - Bounty Book will work in hand only");
        }

        // Register PlayerData events
        MinecraftForge.EVENT_BUS.register(PlayerDataEvents.class);
        System.out.println("✅ Registered PlayerDataEvents");

        // Initialize GeckoLib (for animations)
        GeckoLib.initialize();

        // ========== REGISTER CONTENT ========== //
        ModItems.ITEMS.register(modEventBus);
        ModEntities.ENTITIES.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);
        ModEffects.EFFECTS.register(modEventBus);
        ModBlocks.BLOCKS.register(modEventBus);
        ModBlockEntities.BLOCK_ENTITIES.register(modEventBus);

        // ========== REGISTER EVENT LISTENERS ========== //
        MinecraftForge.EVENT_BUS.register(AudioEventHandler.class);
        MinecraftForge.EVENT_BUS.register(KeyBindingScreen.class);
        MinecraftForge.EVENT_BUS.register(MicrophoneSelectionScreen.class);
        MinecraftForge.EVENT_BUS.register(AshHollowOptionScreen.class);
        MinecraftForge.EVENT_BUS.register(BountyEventHandler.class);
        MinecraftForge.EVENT_BUS.register(AttributeOverlayRenderer.class);
        MinecraftForge.EVENT_BUS.register(AttributeNotificationRenderer.class);
        MinecraftForge.EVENT_BUS.register(CuriosKeyHandler.class);

        // ========== SETUP EVENT ========== //
        modEventBus.addListener(this::commonSetup);
    }

    /**
     * Common Setup Event
     * This runs ONCE during mod initialization, on both client and server
     */
    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            PacketHandler.register();
            System.out.println("✅ Network packets registered in common setup");

            if (curiosLoaded) {
                System.out.println("✅ Curios integration active - book slot registered via data files");
            }
        });
    }
}