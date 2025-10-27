package Ash_Hollow_Requiem;

import Ash_Hollow_Requiem.items.harpoon.entity.HarpoonEntityRenderer;
import Ash_Hollow_Requiem.items.harpoon.item.HarpoonItem;
import Ash_Hollow_Requiem.modregisters.ModBlockEntities;
import Ash_Hollow_Requiem.modregisters.ModEntities;
import Ash_Hollow_Requiem.modregisters.ModItems;
import Ash_Hollow_Requiem.renderer.InkPressRenderer;
import com.mojang.logging.LogUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.slf4j.Logger;

/**
 * Handles client-side mod events such as rendering setup.
 */
@Mod.EventBusSubscriber(modid = "ash_hollow_requiem_of_the_dead", bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModEvents {
    private static final Logger LOGGER = LogUtils.getLogger();

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.HARPOON.get(), HarpoonEntityRenderer::new);
        LOGGER.info("Registered HarpoonEntity renderer");
        // ✅ ADD THIS LINE - BlockEntity renderers
        event.registerBlockEntityRenderer(ModBlockEntities.INK_PRESS.get(),
                InkPressRenderer::new);
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            LOGGER.info("Setting up HarpoonItem renderer");
            // Register the custom renderer for HarpoonItem
            registerItemRenderer(ModItems.HARPOON.get());
            registerItemRenderer(ModItems.INK_PRESS.get());
        });
    }

    private static void registerItemRenderer(net.minecraft.world.item.Item item) {
        if (item instanceof HarpoonItem) {
            LOGGER.info("Registering GeckoLib renderer for HarpoonItem");
        }
    }
}