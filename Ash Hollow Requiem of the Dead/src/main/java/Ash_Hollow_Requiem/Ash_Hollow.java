package Ash_Hollow_Requiem;

import Ash_Hollow_Requiem.bounty.BountyEventHandler;
import Ash_Hollow_Requiem.effects.ModEffects;
import Ash_Hollow_Requiem.modregisters.ModBlocks;
import Ash_Hollow_Requiem.modregisters.ModEntities;
import Ash_Hollow_Requiem.modregisters.ModItems;
import Ash_Hollow_Requiem.network.PacketHandler;
import Ash_Hollow_Requiem.playerdata.PlayerDataEvents;
import software.bernie.geckolib.GeckoLib;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import static Ash_Hollow_Requiem.interfaces.ModCreativeTab.CREATIVE_MODE_TABS;

/**
 * STEP 6: Update Main Mod Class
 *
 * This is your mod's entry point. When Forge loads your mod, this class runs.
 * You need to:
 * 1. Register all your mod's content (items, entities, etc.)
 * 2. Register event handlers
 * 3. Initialize network system
 */
@SuppressWarnings("ALL")
@Mod(Ash_Hollow.MODID)
public class Ash_Hollow {
    public static final String MODID = "ash_hollow_requiem_of_the_dead";

    public Ash_Hollow() {
        // Get the mod event bus (for registering things)
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // ✅ NEW: Register PlayerData events
        // This handles attaching capabilities and syncing
        MinecraftForge.EVENT_BUS.register(PlayerDataEvents.class);

        System.out.println("✅ Registered PlayerDataEvents");

        // Initialize GeckoLib (for animations)
        GeckoLib.initialize();

        // ========== REGISTER CONTENT ========== //
        // These register your items, blocks, entities, etc.

        ModItems.ITEMS.register(modEventBus);
        ModEntities.ENTITIES.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);
        ModEffects.EFFECTS.register(modEventBus);
        ModBlocks.BLOCKS.register(modEventBus);

        // ========== REGISTER EVENT LISTENERS ========== //
        // These listen for game events (player join, entity spawn, etc.)

        // Your existing event handlers
        MinecraftForge.EVENT_BUS.register(AudioEventHandler.class);
        MinecraftForge.EVENT_BUS.register(KeyBindingScreen.class);
        MinecraftForge.EVENT_BUS.register(MicrophoneSelectionScreen.class);
        MinecraftForge.EVENT_BUS.register(AshHollowOptionScreen.class);
        MinecraftForge.EVENT_BUS.register(BountyEventHandler.class);

        // ========== SETUP EVENT ========== //
        // This runs during common setup phase
        modEventBus.addListener(this::commonSetup);
    }

    /**
     * Common Setup Event
     * This runs ONCE during mod initialization, on both client and server
     * Perfect for registering network packets
     */
    private void commonSetup(final FMLCommonSetupEvent event) {
        // Register all network packets
        // This MUST be done during setup, not in constructor
        event.enqueueWork(() -> {
            PacketHandler.register();
            System.out.println("✅ Network packets registered in common setup");
        });
    }
}