package Ash_Hollow_Requiem;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import com.mojang.logging.LogUtils;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.slf4j.Logger;
import software.bernie.geckolib.renderer.GeoItemRenderer;

/**
 * Handles client-side mod events such as rendering setup.
 */
@Mod.EventBusSubscriber(modid = "ash_hollow_requiem_of_the_dead", bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModEvents {
    private static final Logger LOGGER = LogUtils.getLogger();

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(Ash_Hollow_Requiem.ModEntities.HARPOON.get(), HarpoonEntityRenderer::new);
    }
}
