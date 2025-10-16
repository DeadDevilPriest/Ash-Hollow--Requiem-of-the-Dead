package Ash_Hollow_Requiem.interfaces;

import Ash_Hollow_Requiem.bounty.*;
import Ash_Hollow_Requiem.playerdata.PlayerDataAPI;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;

/**
 * Bounty Book screen - shows YOUR active bounties
 * Opened by right-clicking the Bounty Book item
 */
public class IntegratedBountyDetailsScreen extends Screen {
    private final Screen parent;
    private final Bounty bounty;
    private final boolean isFromBountyBook;

    /**
     * Constructor for Bounty Book (active bounty view)
     */
    public IntegratedBountyDetailsScreen(Screen parent, Bounty bounty) {
        super(Component.literal("Bounty Book - Active Contract"));
        this.parent = parent;
        this.bounty = bounty;
        this.isFromBountyBook = true;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        // ✅ Options for active bounties
        if (bounty != null && !bounty.isCompleted()) {
            // Abandon bounty button (refunds tokens for boss bounties)
            this.addRenderableWidget(Button.builder(
                    Component.literal("Abandon Bounty")
                            .withStyle(ChatFormatting.RED),
                    btn -> abandonBounty()
            ).bounds(centerX - 110, centerY + 80, 100, 20).build());
        } else if (bounty != null && bounty.isCompleted() && !bounty.isClaimed()) {
            // Claim reward button
            this.addRenderableWidget(Button.builder(
                    Component.literal("Claim Reward!")
                            .withStyle(ChatFormatting.GOLD),
                    btn -> claimReward()
            ).bounds(centerX - 110, centerY + 80, 100, 20).build());
        }

        // Back/Close button
        this.addRenderableWidget(Button.builder(
                Component.literal("Close Book"),
                btn -> minecraft.setScreen(parent)
        ).bounds(centerX + 10, centerY + 80, 100, 20).build());
    }

    /**
     * ✅ Abandon the bounty (with token refund for boss bounties)
     */
    private void abandonBounty() {
        if (bounty == null) return;

        // Check if it's a boss bounty for token refund
        if (bounty.isBossBounty()) {
            int tokenCost = bounty.getTokenCost();

            minecraft.player.sendSystemMessage(
                    Component.literal("⚠ Abandoning boss bounty...")
                            .withStyle(ChatFormatting.YELLOW)
            );
            minecraft.player.sendSystemMessage(
                    Component.literal("  Refunded: " + tokenCost + " tokens")
                            .withStyle(ChatFormatting.GREEN)
            );

            // TODO: Send packet to server to abandon bounty and refund tokens
            // PacketHandler.sendToServer(new AbandonBountyPacket(bounty.getBountyId()));
        } else {
            minecraft.player.sendSystemMessage(
                    Component.literal("Bounty abandoned.")
                            .withStyle(ChatFormatting.GRAY)
            );

            // TODO: Send packet to server to abandon bounty
            // PacketHandler.sendToServer(new AbandonBountyPacket(bounty.getBountyId()));
        }

        minecraft.setScreen(parent);
    }

    /**
     * ✅ Claim the bounty reward
     */
    private void claimReward() {
        if (bounty == null || !bounty.isCompleted() || bounty.isClaimed()) return;

        minecraft.player.sendSystemMessage(
                Component.literal("━━━━━━━━━━━━━━━━━━━━")
                        .withStyle(ChatFormatting.GOLD)
        );
        minecraft.player.sendSystemMessage(
                Component.literal("✓ BOUNTY COMPLETED!")
                        .withStyle(ChatFormatting.GREEN)
        );
        minecraft.player.sendSystemMessage(
                Component.literal("  Coins: +" + bounty.getRewardCoins())
                        .withStyle(ChatFormatting.YELLOW)
        );
        minecraft.player.sendSystemMessage(
                Component.literal("  XP: +" + bounty.getRewardExperience())
                        .withStyle(ChatFormatting.AQUA)
        );
        minecraft.player.sendSystemMessage(
                Component.literal("━━━━━━━━━━━━━━━━━━━━")
                        .withStyle(ChatFormatting.GOLD)
        );

        // TODO: Send packet to server to claim rewards
        // PacketHandler.sendToServer(new ClaimBountyPacket(bounty.getBountyId()));

        minecraft.setScreen(parent);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics);

        int centerX = this.width / 2;
        int centerY = this.height / 2 - 100;

        if (bounty == null) {
            renderEmptyBook(graphics, centerX, centerY);
        } else if (bounty.isBossBounty()) {
            renderBossBountyPage(graphics, centerX, centerY);
        } else {
            renderStandardBountyPage(graphics, centerX, centerY);
        }

        super.render(graphics, mouseX, mouseY, partialTick);
    }

    /**
     * ✅ Render when no bounty is active
     */
    private void renderEmptyBook(GuiGraphics graphics, int centerX, int centerY) {
        // Book-style background
        graphics.fill(centerX - 120, centerY, centerX + 120, centerY + 220, 0xFF3A2A1A);
        graphics.fill(centerX - 116, centerY + 4, centerX + 116, centerY + 216, 0xFFF5E6D3);

        graphics.drawCenteredString(this.font, "BOUNTY BOOK",
                centerX, centerY + 20, 0x3A1A0A);

        graphics.drawCenteredString(this.font, "━━━━━━━━━━━━",
                centerX, centerY + 35, 0x8B6F47);

        graphics.drawCenteredString(this.font, "No Active Bounty",
                centerX, centerY + 80, 0x666666);

        graphics.drawCenteredString(this.font, "Visit the Bounty Board",
                centerX, centerY + 100, 0x888888);
        graphics.drawCenteredString(this.font, "to accept a contract",
                centerX, centerY + 112, 0x888888);
    }

    /**
     * ✅ Render standard bounty page
     */
    private void renderStandardBountyPage(GuiGraphics graphics, int centerX, int centerY) {
        // Book-style background (aged paper)
        graphics.fill(centerX - 120, centerY, centerX + 120, centerY + 220, 0xFF3A2A1A);
        graphics.fill(centerX - 116, centerY + 4, centerX + 116, centerY + 216, 0xFFF5E6D3);

        // Title
        graphics.drawCenteredString(this.font, "ACTIVE CONTRACT",
                centerX, centerY + 10, 0x8B1A1A);

        // Rarity badge
        graphics.drawCenteredString(this.font,
                "【" + bounty.getRarity().getDisplayName().toUpperCase() + "】",
                centerX, centerY + 22, bounty.getRarity().getColorInt());

        // Difficulty stars
        String stars = bounty.getStars();
        graphics.drawCenteredString(this.font, stars, centerX, centerY + 33, 0xFFD700);

        int yOffset = centerY + 50;

        // Type
        String typeIcon = switch(bounty.getType()) {
            case STANDARD -> "⚔";
            case HORDE -> "👥";
            case ELITE -> "💀";
            case BOSS -> "👑";
        };
        graphics.drawCenteredString(this.font,
                typeIcon + " " + bounty.getType().getDisplayName(),
                centerX, yOffset, 0x555555);
        yOffset += 15;

        // Targets section
        graphics.drawString(this.font, "TARGETS:",
                centerX - 100, yOffset, 0x3A1A0A);
        yOffset += 12;

        for (BountyTarget target : bounty.getTargets()) {
            String mobName = target.getEntityType().getDescription().getString();
            int current = target.getCurrentKills();
            int required = target.getRequiredKills();

            // Truncate long names
            if (font.width(mobName) > 150) {
                mobName = mobName.substring(0, 12) + "...";
            }

            String targetLine = String.format("• %s: %d/%d", mobName, current, required);
            int color = current >= required ? 0x00AA00 : 0x3A1A0A;

            graphics.drawString(this.font, targetLine,
                    centerX - 100, yOffset, color);
            yOffset += 10;
        }

        // Progress bar
        yOffset += 5;
        int barWidth = 180;
        int barHeight = 8;
        int barX = centerX - barWidth / 2;

        // Background bar
        graphics.fill(barX, yOffset, barX + barWidth, yOffset + barHeight, 0xFF8B6F47);

        // Progress fill
        float progress = bounty.getProgress();
        int fillWidth = (int)(barWidth * progress);
        int fillColor = progress >= 1.0f ? 0xFF00AA00 : 0xFFFFD700;
        graphics.fill(barX, yOffset, barX + fillWidth, yOffset + barHeight, fillColor);

        yOffset += barHeight + 2;

        // Progress percentage
        graphics.drawCenteredString(this.font,
                String.format("%.1f%% Complete", progress * 100),
                centerX, yOffset, 0x666666);
        yOffset += 15;

        // Time remaining
        long timeLeft = bounty.getExpirationTime() - minecraft.player.level().getGameTime();
        long minutes = timeLeft / 1200;
        long hours = minutes / 60;
        minutes = minutes % 60;

        String timeString = hours > 0 ? hours + "h " + minutes + "m" : minutes + "m";
        String timeColor = hours < 1 ? (minutes < 10 ? "§c" : "§e") : "§a";

        graphics.drawCenteredString(this.font,
                "⏱ Time Left: " + timeString,
                centerX, yOffset, hours < 1 ? (minutes < 10 ? 0xFF0000 : 0xFFFF00) : 0x00AA00);
        yOffset += 15;

        // Rewards
        graphics.drawCenteredString(this.font, "━━━ REWARDS ━━━",
                centerX, yOffset, 0x8B6F47);
        yOffset += 12;

        graphics.drawCenteredString(this.font,
                "💰 " + bounty.getRewardCoins() + " Coins",
                centerX, yOffset, 0xFFD700);
        yOffset += 10;

        graphics.drawCenteredString(this.font,
                "⭐ " + bounty.getRewardExperience() + " XP",
                centerX, yOffset, 0x55FF55);

        // Completion status
        if (bounty.isCompleted() && !bounty.isClaimed()) {
            yOffset += 15;
            graphics.drawCenteredString(this.font,
                    "✓ READY TO CLAIM!",
                    centerX, yOffset, 0x00FF00);
        }
    }

    /**
     * ✅ Render boss bounty page (dramatic style)
     */
    private void renderBossBountyPage(GuiGraphics graphics, int centerX, int centerY) {
        // Dark book background for boss bounties
        graphics.fill(centerX - 120, centerY, centerX + 120, centerY + 220, 0xFF1A0A0A);
        graphics.fill(centerX - 116, centerY + 4, centerX + 116, centerY + 216, 0xFF3A1A1A);

        // Dramatic title
        graphics.drawCenteredString(this.font, "⚠ BOSS CONTRACT ⚠",
                centerX, centerY + 10, 0xFF0000);

        // Rarity
        graphics.drawCenteredString(this.font,
                bounty.getRarity().getDisplayName().toUpperCase(),
                centerX, centerY + 22, bounty.getRarity().getColorInt());

        // Skull rating
        String skulls = getSkullSymbols(bounty.getRarity());
        graphics.drawCenteredString(this.font, skulls, centerX, centerY + 33, 0xFF0000);

        int yOffset = centerY + 50;

        graphics.drawCenteredString(this.font, "TARGET:",
                centerX, yOffset, 0xFFFFFF);
        yOffset += 15;

        if (!bounty.getTargets().isEmpty()) {
            BountyTarget target = bounty.getTargets().get(0);
            String bossName = target.getEntityType().getDescription().getString();
            int current = target.getCurrentKills();
            int required = target.getRequiredKills();

            graphics.drawCenteredString(this.font, bossName,
                    centerX, yOffset, 0xFFD700);
            yOffset += 12;

            graphics.drawCenteredString(this.font,
                    current + " / " + required + " Slain",
                    centerX, yOffset, current >= required ? 0x00FF00 : 0xFFFFFF);
            yOffset += 15;
        }

        // Progress bar
        int barWidth = 180;
        int barHeight = 8;
        int barX = centerX - barWidth / 2;

        graphics.fill(barX, yOffset, barX + barWidth, yOffset + barHeight, 0xFF3A0A0A);

        float progress = bounty.getProgress();
        int fillWidth = (int)(barWidth * progress);
        int fillColor = progress >= 1.0f ? 0xFFFF0000 : 0xFFAA0000;
        graphics.fill(barX, yOffset, barX + fillWidth, yOffset + barHeight, fillColor);

        yOffset += barHeight + 10;

        // Time
        long timeLeft = bounty.getExpirationTime() - minecraft.player.level().getGameTime();
        long minutes = timeLeft / 1200;
        graphics.drawCenteredString(this.font,
                "⏱ " + minutes + " minutes remaining",
                centerX, yOffset, 0xCCCCCC);
        yOffset += 15;

        // Token investment reminder
        int tokenCost = bounty.getTokenCost();
        graphics.drawCenteredString(this.font,
                "Investment: " + tokenCost + " Tokens",
                centerX, yOffset, 0xFFAA00);
        yOffset += 15;

        // Rewards
        graphics.drawCenteredString(this.font, "━━━ REWARDS ━━━",
                centerX, yOffset, 0xFFD700);
        yOffset += 12;

        graphics.drawCenteredString(this.font,
                "💰 " + bounty.getRewardCoins() + " Coins",
                centerX, yOffset, 0xFFD700);
        yOffset += 10;

        graphics.drawCenteredString(this.font,
                "⭐ " + bounty.getRewardExperience() + " XP",
                centerX, yOffset, 0x55FF55);
        yOffset += 10;

        graphics.drawCenteredString(this.font,
                "🎁 Legendary Loot",
                centerX, yOffset, 0xFF00FF);

        // Status
        if (bounty.isCompleted() && !bounty.isClaimed()) {
            yOffset += 15;
            graphics.drawCenteredString(this.font,
                    "⚔ BOSS DEFEATED! ⚔",
                    centerX, yOffset, 0xFF0000);
        }
    }

    /**
     * Get skull symbols for boss bounties
     */
    private String getSkullSymbols(BountyRarity rarity) {
        int count = Math.min(rarity.ordinal() + 1, 5);
        return "☠ ".repeat(count);
    }

    @Override
    public boolean isPauseScreen() {
        return false; // Don't pause game when reading bounty book
    }
}