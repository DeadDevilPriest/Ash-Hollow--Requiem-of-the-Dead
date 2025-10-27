package Ash_Hollow_Requiem;

import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

import static Ash_Hollow_Requiem.Ash_Hollow.MODID;
/**
 * Handles the registration of custom key bindings for the mod.
 */
@Mod.EventBusSubscriber(modid = MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class KeyBindings {
    public static final KeyMapping DARK_VISION_KEY = new KeyMapping(
        "Dark Vision",
        GLFW.GLFW_KEY_M,
        "key.category." + MODID
    );

    public static final KeyMapping PUSH_TO_TALK_KEY = new KeyMapping(
        "Push to Talk",
        GLFW.GLFW_KEY_B,
        "key.category." + MODID
    );

    public static final KeyMapping WHISPER_KEY = new KeyMapping(
        "Whisper",
        GLFW.GLFW_KEY_N,
        "key.category." + MODID
    );

    public static KeyMapping SHOUT_KEY = new KeyMapping(
        "Shout",
        GLFW.GLFW_KEY_V,
        "key.category." + MODID
    );

    public static final KeyMapping OPEN_BOUNTY_KEY = new KeyMapping(
            "Open Active Bounty",
            GLFW.GLFW_KEY_O, // Or whatever key you want
            "key.category." + MODID
    );

    public static final KeyMapping OPEN_SKILL_INTERFACE_KEY = new KeyMapping(
            "Open Skill Interface",
            GLFW.GLFW_KEY_K, // Or whatever key you want
            "key.category." + MODID
    );

    @SubscribeEvent
    public static void registerKeyBindings(RegisterKeyMappingsEvent event) {
        event.register(DARK_VISION_KEY);
        event.register(PUSH_TO_TALK_KEY);
        event.register(WHISPER_KEY);
        event.register(SHOUT_KEY);
        event.register(OPEN_BOUNTY_KEY);
        event.register(OPEN_SKILL_INTERFACE_KEY);
    }
}