package Ash_Hollow_Requiem;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import static Ash_Hollow_Requiem.KeyBindings.*;

public class KeyBindingScreen extends Screen {

    private final Screen parentScreen;
    private Button pushToTalkButton;
    private Button shoutButton;
    private Button whisperButton;
    private Button darkVisionButton;
    private boolean awaitingKey = false;
    private int keyToAssign;

    protected KeyBindingScreen(Screen parent) {
        super(Component.literal("Keybindings"));
        this.parentScreen = parent;
    }

    @Override
    protected void init() {
        super.init();

        // Load current key names from KeyBindings
        String pushToTalkKeyName = AudioEventHandler.getPushToTalkKeyName();
        String shoutKeyName = AudioEventHandler.getShoutKeyName();
        String whisperKeyName = AudioEventHandler.getWhisperKeyName();
        String darkVisionKeyName = DARK_VISION_KEY.getKey().getDisplayName().getString();

        // Buttons
        pushToTalkButton = Button.builder(Component.literal("Push to Talk: " + pushToTalkKeyName), button -> {
            awaitingKey = true;
            keyToAssign = 0;
            button.setMessage(Component.literal("Press a key..."));
        }).bounds(this.width / 2 - 100, this.height / 4, 200, 20).build();
        this.addRenderableWidget(pushToTalkButton);

        shoutButton = Button.builder(Component.literal("Shout: " + shoutKeyName), button -> {
            awaitingKey = true;
            keyToAssign = 1;
            button.setMessage(Component.literal("Press a key..."));
        }).bounds(this.width / 2 - 100, this.height / 4 + 30, 200, 20).build();
        this.addRenderableWidget(shoutButton);

        whisperButton = Button.builder(Component.literal("Whisper: " + whisperKeyName), button -> {
            awaitingKey = true;
            keyToAssign = 2;
            button.setMessage(Component.literal("Press a key..."));
        }).bounds(this.width / 2 - 100, this.height / 4 + 60, 200, 20).build();
        this.addRenderableWidget(whisperButton);

        darkVisionButton = Button.builder(
                Component.literal("Dark Vision Toggle: " + KeyBindings.DARK_VISION_KEY.getTranslatedKeyMessage().getString()),
                button -> {
                    awaitingKey = true;
                    keyToAssign = 3;
                    button.setMessage(Component.literal("Press a key..."));
                }
        ).bounds(this.width / 2 - 100, this.height / 4 + 90, 200, 20).build();

        this.addRenderableWidget(darkVisionButton);

        Button doneButton = Button.builder(Component.literal("Done"), button ->
                Minecraft.getInstance().setScreen(parentScreen)
        ).bounds(this.width / 2 - 100, this.height / 4 + 120, 200, 20).build();
        this.addRenderableWidget(doneButton);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (awaitingKey) {
            assignKey(InputConstants.Type.KEYSYM.getOrCreate(keyCode), keyCode, scanCode);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (awaitingKey) {
            // Mouse button pressed
            InputConstants.Key key = InputConstants.Type.MOUSE.getOrCreate(button);
            assignKey(key, button, 0);
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private void assignKey(InputConstants.Key key, int keyCode, int scanCode) {
        awaitingKey = false;
        String keyName = key.getDisplayName().getString();

        switch (keyToAssign) {
            case 0 -> {
                KeyBindings.PUSH_TO_TALK_KEY.setKey(key);
                pushToTalkButton.setMessage(Component.literal("Push to Talk: " + keyName));
            }
            case 1 -> {
                KeyBindings.SHOUT_KEY.setKey(key);
                shoutButton.setMessage(Component.literal("Shout: " + keyName));
            }
            case 2 -> {
                KeyBindings.WHISPER_KEY.setKey(key);
                whisperButton.setMessage(Component.literal("Whisper: " + keyName));
            }
            case 3 -> {
                KeyBindings.DARK_VISION_KEY.setKey(key);
                darkVisionButton.setMessage(Component.literal("Dark Vision Toggle: " + keyName));
            }
        }

        // Update mappings
        KeyMapping.resetMapping();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics);
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 15, 0xFFFFFF);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }
}
