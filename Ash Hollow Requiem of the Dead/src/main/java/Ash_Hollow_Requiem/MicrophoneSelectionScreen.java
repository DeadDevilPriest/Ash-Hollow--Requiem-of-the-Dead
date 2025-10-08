package Ash_Hollow_Requiem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class MicrophoneSelectionScreen extends Screen {
    private final Screen parent;
    private final List<String> microphones = new ArrayList<>();
    private int selectedMicIndex = 0;
    private Button micButton;

    public MicrophoneSelectionScreen(Screen parent) {
        super(Component.literal("Microphone Settings"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        super.init();

        // ✅ Fetch available microphones
        microphones.clear();
        microphones.addAll(MicrophoneUtils.getAvailableMicrophones());

        // ✅ If none found, add placeholder
        if (microphones.isEmpty()) {
            microphones.add("No microphones detected");
        }

        // ✅ Create button to cycle microphones
        micButton = Button.builder(Component.literal(getMicLabel()), button -> {
            cycleMicrophone();
        }).bounds(this.width / 2 - 100, this.height / 2 - 10, 200, 20).build();
        this.addRenderableWidget(micButton);

        // ✅ Back button
        this.addRenderableWidget(Button.builder(Component.literal("Done"), btn -> {
            Minecraft.getInstance().setScreen(parent);
        }).bounds(this.width / 2 - 100, this.height / 2 + 30, 200, 20).build());
    }

    private void cycleMicrophone() {
        if (microphones.isEmpty()) return;

        selectedMicIndex = (selectedMicIndex + 1) % microphones.size();
        String micName = microphones.get(selectedMicIndex);

        // Save the selected mic (you’ll add this in AudioEventHandler)
        AudioEventHandler.setSelectedMicrophone(micName);

        micButton.setMessage(Component.literal(getMicLabel()));
    }

    private String getMicLabel() {
        return "Microphone: " + microphones.get(selectedMicIndex);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics);
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 20, 0xFFFFFF);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }
}
