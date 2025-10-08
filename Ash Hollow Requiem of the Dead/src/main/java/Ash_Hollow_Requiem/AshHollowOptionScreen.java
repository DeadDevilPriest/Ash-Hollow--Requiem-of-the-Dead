package Ash_Hollow_Requiem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class AshHollowOptionScreen extends Screen {

    private final Screen parent;

    public AshHollowOptionScreen(Screen parent) {
        super(Component.literal("Ash Hollow Settings"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int startY = this.height / 4;

        // 🎙️ Microphone Selection button
        this.addRenderableWidget(Button.builder(
                Component.literal("Microphone Settings"),
                btn -> Minecraft.getInstance().setScreen(new MicrophoneSelectionScreen(this))
        ).bounds(centerX - 75, startY, 150, 20).build());

        // 🎮 Key Bindings button
        this.addRenderableWidget(Button.builder(
                Component.literal("Key Bindings"),
                btn -> Minecraft.getInstance().setScreen(new KeyBindingScreen(this))
        ).bounds(centerX - 75, startY + 30, 150, 20).build());

        // ⚙️ (Optional) Audio Effects or other submenu
        this.addRenderableWidget(Button.builder(
                Component.literal("Audio Effects (WIP)"),
                btn -> {} // Placeholder for future features
        ).bounds(centerX - 75, startY + 60, 150, 20).build());

        // ✅ Done button
        this.addRenderableWidget(Button.builder(
                Component.literal("Done"),
                btn -> Minecraft.getInstance().setScreen(parent)
        ).bounds(centerX - 75, startY + 120, 150, 20).build());
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // Draw background and title
        this.renderBackground(guiGraphics);
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 15, 0xFFFFFF);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }
}

