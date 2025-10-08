package Ash_Hollow_Requiem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.OptionsScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "ash_hollow_requiem_of_the_dead", value = Dist.CLIENT)
public class MenuButtonInjector {

    @SubscribeEvent
    public static void onGuiInit(ScreenEvent.Init.Post event) {
        Screen screen = event.getScreen();

        // Check if the current screen is the Options screen
        if (screen instanceof OptionsScreen) {

            // Create your mod button
            Button myButton = Button.builder(
                    Component.literal("Ash Hollow Options"),
                    btn -> Minecraft.getInstance().setScreen(new AshHollowOptionScreen(screen))
            ).bounds(
                    screen.width / 2 + 105,  // X position
                    screen.height / 6 + 168, // Y position
                    150,                     // Width
                    20                       // Height
            ).build();

            // Add the button to the screen
            event.addListener(myButton);
        }
    }
}

