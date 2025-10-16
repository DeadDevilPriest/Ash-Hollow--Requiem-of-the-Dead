package Ash_Hollow_Requiem.interfaces;

import Ash_Hollow_Requiem.network.PacketHandler;
import Ash_Hollow_Requiem.network.PurchaseTokensPacket;
import Ash_Hollow_Requiem.playerdata.PlayerDataAPI;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;

public class TokenMerchantScreen extends Screen {
    private final Screen parent;

    protected TokenMerchantScreen(Screen parent, int coins, int tokens) {
        super(Component.literal("Token Merchant"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int startY = this.height / 4 + 60;

        // Exchange buttons with bonus deals
        this.addRenderableWidget(Button.builder(
                Component.literal("100 Coins → 1 Token"),
                btn -> exchange(100, 1)
        ).bounds(centerX - 100, startY, 200, 20).build());

        this.addRenderableWidget(Button.builder(
                Component.literal("500 Coins → 6 Tokens (BONUS!)"),
                btn -> exchange(500, 6)
        ).bounds(centerX - 100, startY + 25, 200, 20).build());

        this.addRenderableWidget(Button.builder(
                Component.literal("1000 Coins → 15 Tokens (MEGA BONUS!)"),
                btn -> exchange(1000, 15)
        ).bounds(centerX - 100, startY + 50, 200, 20).build());

        this.addRenderableWidget(Button.builder(
                Component.literal("5000 Coins → 100 Tokens (LEGENDARY!)"),
                btn -> exchange(5000, 100)
        ).bounds(centerX - 100, startY + 75, 200, 20).build());

        // Back button
        this.addRenderableWidget(Button.builder(
                Component.literal("Back to Board"),
                btn -> minecraft.setScreen(parent)
        ).bounds(centerX - 60, startY + 110, 120, 20).build());
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics);

        int centerX = this.width / 2;

        // ✅ Get LIVE player data
        int playerCoins = minecraft.player != null ? PlayerDataAPI.getCoins(minecraft.player) : 0;
        int playerTokens = minecraft.player != null ? PlayerDataAPI.getTokens(minecraft.player) : 0;

        // Title with merchant flavor text
        graphics.drawCenteredString(this.font, "Token Merchant", centerX, 20, 0xFFFFFF);
        graphics.drawCenteredString(this.font,
                "\"Trade your coins for Boss Tokens!\"",
                centerX, 35, 0xAAAAAA);

        // Player balance (live data)
        graphics.drawCenteredString(this.font, "Your Coins: " + playerCoins,
                centerX, 60, 0xFFD700);
        graphics.drawCenteredString(this.font, "Boss Tokens: " + playerTokens,
                centerX, 75, 0xFF5AFF5A);

        // Hint text
        graphics.drawCenteredString(this.font,
                "Larger purchases give bonus tokens!",
                centerX, this.height - 30, 0xFF888888);

        super.render(graphics, mouseX, mouseY, partialTick);
    }

    private void exchange(int coinCost, int tokensGained) {
        // ✅ Send packet to server to handle the exchange
        PacketHandler.sendToServer(new PurchaseTokensPacket(coinCost, tokensGained));

        // Screen will automatically update on next render since it reads live data
    }
}