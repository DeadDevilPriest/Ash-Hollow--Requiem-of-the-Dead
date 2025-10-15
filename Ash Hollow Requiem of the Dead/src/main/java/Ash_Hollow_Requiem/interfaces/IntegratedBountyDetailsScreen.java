package Ash_Hollow_Requiem.interfaces;

import Ash_Hollow_Requiem.bounty.*;
import Ash_Hollow_Requiem.network.AcceptBountyPacket;
import Ash_Hollow_Requiem.network.PacketHandler;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;

/**
 * Integrated bounty details screen showing all targets and progress
 */
public class IntegratedBountyDetailsScreen extends Screen {
    private final Screen parent;
    private final Bounty bounty;
    private final int playerCoins;
    private final int playerTokens;
    private final boolean isBossBounty;
    private final int tokenCost;

    public IntegratedBountyDetailsScreen(Screen parent, Bounty bounty,
                                         int coins, int tokens, boolean isBoss) {
        this(parent, bounty, coins, tokens, isBoss, 0);
    }

    public IntegratedBountyDetailsScreen(Screen parent, Bounty bounty,
                                         int coins, int tokens,
                                         boolean isBoss, int tokenCost) {
        super(Component.literal(isBoss ? "Boss Bounty Details" : "Bounty Details"));
        this.parent = parent;
        this.bounty = bounty;
        this.playerCoins = coins;
        this.playerTokens = tokens;
        this.isBossBounty = isBoss;
        this.tokenCost = tokenCost;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        boolean canAfford = !isBossBounty || playerTokens >= tokenCost;

        Button acceptBtn = Button.builder(
                isBossBounty ?
                        Component.literal("Purchase (" + tokenCost + " Tokens)")
                                .withStyle(ChatFormatting.GOLD) :
                        Component.literal("Accept Contract")
                                .withStyle(ChatFormatting.GREEN),
                btn -> acceptBounty()
        ).bounds(centerX - 110, centerY + 80, 100, 20).build();

        acceptBtn.active = canAfford;
        this.addRenderableWidget(acceptBtn);

        this.addRenderableWidget(Button.builder(
                Component.literal("Back"),
                btn -> minecraft.setScreen(parent)
        ).bounds(centerX + 10, centerY + 80, 100, 20).build());
    }

    /**
     * Accept the bounty and send packet to server
     */
    private void acceptBounty() {
        // Send packet to server to accept the bounty
        PacketHandler.sendToServer(
                new AcceptBountyPacket(
                        bounty.getRarity(),
                        bounty.getType(),
                        isBossBounty
                )
        );

        // Close screen and return to parent
        minecraft.setScreen(parent);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics);

        int centerX = this.width / 2;
        int centerY = this.height / 2 - 100;

        if (isBossBounty) {
            renderBossPoster(graphics, centerX, centerY);
        } else {
            renderStandardPoster(graphics, centerX, centerY);
        }

        super.render(graphics, mouseX, mouseY, partialTick);
    }

    private void renderStandardPoster(GuiGraphics graphics, int centerX, int centerY) {
        // Outer frame (dark brown)
        graphics.fill(centerX - 120, centerY, centerX + 120, centerY + 220, 0xFF8B6F47);
        // Inner background (light brown/tan)
        graphics.fill(centerX - 116, centerY + 4, centerX + 116, centerY + 216, 0xFFC9A677);

        // Title
        graphics.drawCenteredString(this.font, "WANTED",
                centerX, centerY + 10, 0x8B1A1A);

        // Rarity with color
        graphics.drawCenteredString(this.font,
                bounty.getRarity().getDisplayName().toUpperCase(),
                centerX, centerY + 22, bounty.getRarity().getColorInt());

        // Stars for difficulty (use Bounty's method)
        String stars = bounty.getStars();
        graphics.drawCenteredString(this.font, stars, centerX, centerY + 33, 0xFFD700);

        int yOffset = centerY + 50;
        graphics.drawCenteredString(this.font, "TARGETS:",
                centerX, yOffset, 0x3A1A0A);

        // List all targets
        yOffset += 15;
        for (BountyTarget target : bounty.getTargets()) {
            String mobName = target.getEntityType().getDescription().getString();
            String targetInfo = String.format("%s x%d", mobName, target.getRequiredKills());

            graphics.drawString(this.font, targetInfo,
                    centerX - 100, yOffset, 0x3A1A0A);
            yOffset += 12;
        }

        // Type
        yOffset += 10;
        graphics.drawCenteredString(this.font,
                "Type: " + bounty.getType().getDisplayName(),
                centerX, yOffset, 0x555555);

        // Total kills
        yOffset += 12;
        graphics.drawCenteredString(this.font,
                "Total Kills Required: " + bounty.getTotalKillsRequired(),
                centerX, yOffset, 0x3A1A0A);

        // Time remaining
        yOffset += 15;
        long timeLeft = bounty.getExpirationTime() -
                minecraft.player.level().getGameTime();
        int minutesLeft = (int) (timeLeft / 20 / 60);
        graphics.drawCenteredString(this.font,
                "Time: " + minutesLeft + " minutes",
                centerX, yOffset, 0x666666);

        // Rewards section
        yOffset += 20;
        graphics.drawCenteredString(this.font, "REWARDS:",
                centerX, yOffset, 0x8B1A1A);

        yOffset += 12;
        int coinReward = bounty.getRarity().getBaseReward();
        graphics.drawCenteredString(this.font,
                coinReward + " Coins",
                centerX, yOffset, 0xFFD700);

        // Progress (if any)
        if (bounty.getCurrentKills() > 0) {
            yOffset += 15;
            graphics.drawCenteredString(this.font,
                    "Progress: " + bounty.getCurrentKills() + "/" +
                            bounty.getTotalKillsRequired() +
                            String.format(" (%.1f%%)", bounty.getProgress() * 100),
                    centerX, yOffset, 0xFFD700);
        }
    }

    private void renderBossPoster(GuiGraphics graphics, int centerX, int centerY) {
        // Dark red/black theme for boss bounties
        graphics.fill(centerX - 120, centerY, centerX + 120, centerY + 220, 0xFF2A0A0A);
        graphics.fill(centerX - 116, centerY + 4, centerX + 116, centerY + 216, 0xFF5A1A1A);

        // Dramatic title
        graphics.drawCenteredString(this.font, "!!! BOSS BOUNTY !!!",
                centerX, centerY + 10, 0xFF0000);

        // Rarity
        graphics.drawCenteredString(this.font,
                bounty.getRarity().getDisplayName().toUpperCase(),
                centerX, centerY + 22, bounty.getRarity().getColorInt());

        // Skull symbols
        String skulls = getSkullSymbols(bounty.getRarity());
        graphics.drawCenteredString(this.font, skulls, centerX, centerY + 33, 0xFF0000);

        int yOffset = centerY + 50;
        graphics.drawCenteredString(this.font, "TARGET:",
                centerX, yOffset, 0xFFFFFF);

        yOffset += 15;
        if (!bounty.getTargets().isEmpty()) {
            BountyTarget target = bounty.getTargets().get(0);
            String bossName = target.getEntityType().getDescription().getString();

            graphics.drawCenteredString(this.font, bossName,
                    centerX, yOffset, 0xFFD700);
            yOffset += 12;
            graphics.drawCenteredString(this.font, "x" + target.getRequiredKills(),
                    centerX, yOffset, 0xFFFFFF);
        }

        yOffset += 20;
        graphics.drawCenteredString(this.font, "DANGER LEVEL: EXTREME",
                centerX, yOffset, 0xFF0000);

        yOffset += 15;
        long timeLeft = bounty.getExpirationTime() -
                minecraft.player.level().getGameTime();
        int minutesLeft = (int) (timeLeft / 20 / 60);
        graphics.drawCenteredString(this.font,
                "Time: " + minutesLeft + " minutes",
                centerX, yOffset, 0xCCCCCC);

        yOffset += 20;
        graphics.drawCenteredString(this.font, "REWARDS:",
                centerX, yOffset, 0xFFD700);

        yOffset += 12;
        int coinReward = bounty.getRarity().getBaseReward();
        graphics.drawCenteredString(this.font,
                coinReward + " Coins",
                centerX, yOffset, 0xFFD700);

        yOffset += 12;
        graphics.drawCenteredString(this.font,
                "Legendary Loot",
                centerX, yOffset, 0xFF00FF);

        // Cost
        yOffset += 20;
        graphics.drawCenteredString(this.font,
                "Cost: " + tokenCost + " Tokens",
                centerX, yOffset, playerTokens >= tokenCost ? 0x00FF00 : 0xFF0000);

        // Progress
        if (bounty.getCurrentKills() > 0) {
            yOffset += 15;
            graphics.drawCenteredString(this.font,
                    "Progress: " + bounty.getCurrentKills() + "/" +
                            bounty.getTotalKillsRequired(),
                    centerX, yOffset, 0xFFD700);
        }
    }

    /**
     * Get skull symbols for boss bounties
     */
    private String getSkullSymbols(BountyRarity rarity) {
        int count = rarity.ordinal() + 1;
        return "☠ ".repeat(count);
    }

    @Override
    public boolean isPauseScreen() {
        return true;
    }
}