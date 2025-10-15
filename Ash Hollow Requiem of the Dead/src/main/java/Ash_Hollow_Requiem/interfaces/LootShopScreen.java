package Ash_Hollow_Requiem.interfaces;

import Ash_Hollow_Requiem.bounty.Bounty;
import Ash_Hollow_Requiem.bounty.BountyTarget;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;

import java.util.List;

public class LootShopScreen extends Screen {
    private final Screen parent;
    private int playerCoins;

    protected LootShopScreen(Screen parent, int coins) {
        super(Component.literal("Loot Shop"));
        this.parent = parent;
        this.playerCoins = coins;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int startY = this.height / 4 + 60;

        // Weapon upgrades
        this.addRenderableWidget(Button.builder(
                Component.literal("Iron Harpoon - 75 Coins"),
                btn -> buyItem("Iron Harpoon", 75)
        ).bounds(centerX - 100, startY, 200, 20).build());

        this.addRenderableWidget(Button.builder(
                Component.literal("Steel Harpoon - 200 Coins"),
                btn -> buyItem("Steel Harpoon", 200)
        ).bounds(centerX - 100, startY + 25, 200, 20).build());

        this.addRenderableWidget(Button.builder(
                Component.literal("Diamond Harpoon - 500 Coins"),
                btn -> buyItem("Diamond Harpoon", 500)
        ).bounds(centerX - 100, startY + 50, 200, 20).build());

        // Consumables
        this.addRenderableWidget(Button.builder(
                Component.literal("Health Potion x3 - 30 Coins"),
                btn -> buyItem("Health Potion x3", 30)
        ).bounds(centerX - 100, startY + 80, 200, 20).build());

        this.addRenderableWidget(Button.builder(
                Component.literal("Strength Potion - 50 Coins"),
                btn -> buyItem("Strength Potion", 50)
        ).bounds(centerX - 100, startY + 105, 200, 20).build());

        // Back button
        this.addRenderableWidget(Button.builder(
                Component.literal("Back to Board"),
                btn -> minecraft.setScreen(parent)
        ).bounds(centerX - 60, startY + 140, 120, 20).build());
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics);

        int centerX = this.width / 2;

        graphics.drawCenteredString(this.font, "Loot Shop", centerX, 20, 0xFFFFFF);
        graphics.drawCenteredString(this.font,
                "\"Spend your hard-earned coins!\"",
                centerX, 35, 0xAAAAAA);
        graphics.drawCenteredString(this.font, "Your Coins: " + playerCoins,
                centerX, 60, 0xFFD700);

        super.render(graphics, mouseX, mouseY, partialTick);
    }

    private void buyItem(String itemName, int cost) {
        if (playerCoins >= cost) {
            // TODO: Send packet to server to purchase item
            playerCoins -= cost;

            minecraft.player.displayClientMessage(
                    Component.literal("✓ Purchased " + itemName + "!")
                            .withStyle(ChatFormatting.GREEN),
                    false
            );

            this.rebuildWidgets();
        } else {
            minecraft.player.displayClientMessage(
                    Component.literal("✗ Not enough coins!")
                            .withStyle(ChatFormatting.RED),
                    false
            );
        }
    }
}
