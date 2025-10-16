package Ash_Hollow_Requiem.interfaces;

import Ash_Hollow_Requiem.bounty.Bounty;
import Ash_Hollow_Requiem.network.AcceptBountyPacket;
import Ash_Hollow_Requiem.network.PacketHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.List;

/**
 * Swipeable bounty viewer - SMALLER VERSION
 */
public class BountyViewerScreen extends Screen {
    private final Screen parent;
    private final BountyBoardScreen.BountyCategory category;
    private final List<Bounty> bounties;
    private final int playerCoins;
    private final int playerTokens;

    private int currentBountyIndex = 0;

    // Swipe detection
    private double dragStartX = -1;
    private double dragCurrentX = -1;
    private boolean isDragging = false;
    private static final double SWIPE_THRESHOLD = 50.0;

    // Animation
    private float slideOffset = 0.0f;
    private boolean isAnimating = false;

    // ✅ SMALLER DIMENSIONS
    private static final int BASE_WIDTH = 180;  // Was 350
    private static final int BASE_HEIGHT = 240; // Was 400

    private int scaledWidth;
    private int scaledHeight;
    private int screenX;
    private int screenY;

    // Custom button areas
    private int acceptButtonX, acceptButtonY, acceptButtonWidth, acceptButtonHeight;
    private int backButtonX, backButtonY, backButtonWidth, backButtonHeight;

    public BountyViewerScreen(Screen parent, BountyBoardScreen.BountyCategory category,
                              List<Bounty> bounties, int coins, int tokens) {
        super(Component.literal(category.getDisplayName() + " Bounties"));
        this.parent = parent;
        this.category = category;
        this.bounties = bounties;
        this.playerCoins = coins;
        this.playerTokens = tokens;
    }

    @Override
    protected void init() {
        super.init();

        calculateScaledDimensions();

        screenX = (this.width - scaledWidth) / 2;
        screenY = (this.height - scaledHeight) / 2;

        // Calculate custom button areas (text-based, no Minecraft buttons)
        acceptButtonWidth = 80;
        acceptButtonHeight = 15;
        acceptButtonX = screenX + (scaledWidth / 2) - (acceptButtonWidth / 2);
        acceptButtonY = screenY + scaledHeight - 35;

        backButtonWidth = 60;
        backButtonHeight = 15;
        backButtonX = screenX + (scaledWidth / 2) - (backButtonWidth / 2);
        backButtonY = screenY + scaledHeight - 18;
    }

    private void calculateScaledDimensions() {
        Minecraft mc = Minecraft.getInstance();
        int guiScale = mc.options.guiScale().get();

        if (guiScale == 0) {
            guiScale = 3;
        }

        float scaleFactor = Math.min(1.0f, guiScale / 3.0f);
        scaledWidth = (int)(BASE_WIDTH * scaleFactor);
        scaledHeight = (int)(BASE_HEIGHT * scaleFactor);

        scaledWidth = Math.max(scaledWidth, 200);
        scaledHeight = Math.max(scaledHeight, 220);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            // Check accept button click
            if (mouseX >= acceptButtonX && mouseX <= acceptButtonX + acceptButtonWidth &&
                    mouseY >= acceptButtonY && mouseY <= acceptButtonY + acceptButtonHeight) {
                acceptCurrentBounty();
                return true;
            }

            // Check back button click
            if (mouseX >= backButtonX && mouseX <= backButtonX + backButtonWidth &&
                    mouseY >= backButtonY && mouseY <= backButtonY + backButtonHeight) {
                minecraft.setScreen(parent);
                return true;
            }

            // Check card drag
            int cardX = screenX + 15;
            int cardY = screenY + 50;
            int cardWidth = scaledWidth - 30;
            int cardHeight = scaledHeight - 100;

            if (mouseX >= cardX && mouseX <= cardX + cardWidth &&
                    mouseY >= cardY && mouseY <= cardY + cardHeight) {
                dragStartX = mouseX;
                isDragging = true;
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (isDragging && dragStartX != -1) {
            dragCurrentX = mouseX;
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (isDragging) {
            double dragDistance = dragCurrentX - dragStartX;

            if (Math.abs(dragDistance) > SWIPE_THRESHOLD) {
                if (dragDistance > 0) {
                    previousBounty();
                } else {
                    nextBounty();
                }
            }

            isDragging = false;
            dragStartX = -1;
            dragCurrentX = -1;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    private void previousBounty() {
        if (currentBountyIndex > 0) {
            currentBountyIndex--;
            startSlideAnimation();
        }
    }

    private void nextBounty() {
        if (currentBountyIndex < bounties.size() - 1) {
            currentBountyIndex++;
            startSlideAnimation();
        }
    }

    private void startSlideAnimation() {
        isAnimating = true;
        slideOffset = 0.0f;
    }

    private void acceptCurrentBounty() {
        Bounty bounty = bounties.get(currentBountyIndex);

        // ✅ Check if it's a boss bounty and if player has enough tokens
        if (bounty.isBossBounty()) {
            int tokenCost = bounty.getTokenCost();

            if (playerTokens < tokenCost) {
                // ❌ Not enough tokens
                minecraft.player.sendSystemMessage(
                        Component.literal("❌ Insufficient tokens!")
                                .withStyle(net.minecraft.ChatFormatting.RED)
                );
                minecraft.player.sendSystemMessage(
                        Component.literal("  Need: " + tokenCost + " | Have: " + playerTokens)
                                .withStyle(net.minecraft.ChatFormatting.GRAY)
                );

                // Play error sound
                minecraft.player.playSound(
                        net.minecraft.sounds.SoundEvents.VILLAGER_NO,
                        1.0f,
                        1.0f
                );
                return;
            }
        }

        // ✅ Send packet with bounty ID and category
        PacketHandler.sendToServer(new AcceptBountyPacket(
                bounty.getBountyId(),
                category
        ));

        minecraft.setScreen(parent);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics);

        if (isAnimating) {
            slideOffset += 0.15f;
            if (slideOffset >= 1.0f) {
                slideOffset = 0.0f;
                isAnimating = false;
            }
        }

        // Outer border
        graphics.fill(screenX, screenY, screenX + scaledWidth, screenY + scaledHeight, 0xFF2C1810);
        // Main background
        graphics.fill(screenX + 4, screenY + 4, screenX + scaledWidth - 4, screenY + scaledHeight - 4, 0xFF8B6F47);

        // Title
        graphics.drawCenteredString(this.font, category.getDisplayName() + " Bounties",
                this.width / 2, screenY + 10, 0xFFFFFF);

        // Page indicator
        graphics.drawCenteredString(this.font, (currentBountyIndex + 1) + " / " + bounties.size(),
                this.width / 2, screenY + 22, 0xCCCCCC);

        // Bounty card
        renderBountyCard(graphics, bounties.get(currentBountyIndex), mouseX, mouseY);

        // Swipe indicator
        if (isDragging && dragStartX != -1 && dragCurrentX != -1) {
            double dragDistance = dragCurrentX - dragStartX;
            if (Math.abs(dragDistance) > 10) {
                String indicator = dragDistance > 0 ? "< Swipe" : "Swipe >";
                graphics.drawCenteredString(this.font, indicator,
                        this.width / 2, screenY + 38, 0x88FFFFFF);
            }
        }

        // ✅ CUSTOM TEXT BUTTONS (no Minecraft button style)
        boolean acceptHover = mouseX >= acceptButtonX && mouseX <= acceptButtonX + acceptButtonWidth &&
                mouseY >= acceptButtonY && mouseY <= acceptButtonY + acceptButtonHeight;
        boolean backHover = mouseX >= backButtonX && mouseX <= backButtonX + backButtonWidth &&
                mouseY >= backButtonY && mouseY <= backButtonY + backButtonHeight;

        // Accept button (text only)
        int acceptColor = acceptHover ? 0x55FF55 : 0x00FF00; // Brighter on hover
        graphics.drawCenteredString(this.font, "[ Accept Bounty ]",
                screenX + scaledWidth / 2, acceptButtonY, acceptColor);

        // Back button (text only)
        int backColor = backHover ? 0xFFAAAA : 0xFF5555; // Brighter on hover
        graphics.drawCenteredString(this.font, "[ Back ]",
                screenX + scaledWidth / 2, backButtonY, backColor);

        super.render(graphics, mouseX, mouseY, partialTick);
    }

    private void renderBountyCard(GuiGraphics graphics, Bounty bounty, int mouseX, int mouseY) {
        int cardX = screenX + 15;
        int cardY = screenY + 50;
        int cardWidth = scaledWidth - 30;
        int cardHeight = scaledHeight - 100;

        // Card background
        graphics.fill(cardX, cardY, cardX + cardWidth, cardY + cardHeight, 0xFFC9A677);
        graphics.fill(cardX + 2, cardY + 2, cardX + cardWidth - 2, cardY + cardHeight - 2, 0xFFDDBB99);

        int textY = cardY + 8;
        int lineHeight = 10;

        // Rarity with color
        graphics.drawCenteredString(this.font,
                bounty.getRarity().name().toUpperCase(),
                cardX + cardWidth / 2, textY,
                bounty.getRarity().getColorInt());
        textY += lineHeight + 3;

        // ✅ Show type badge
        String typeBadge = switch(bounty.getType()) {
            case STANDARD -> "⚔";
            case HORDE -> "👥";
            case ELITE -> "💀";
            case BOSS -> "👑";
        };
        graphics.drawCenteredString(this.font,
                typeBadge + " " + bounty.getType().name(),
                cardX + cardWidth / 2, textY, 0x666666);
        textY += lineHeight + 3;

        // Target
        if (!bounty.getTargets().isEmpty()) {
            String targetName = bounty.getTargets().get(0).getEntityType()
                    .getDescription().getString();

            if (font.width(targetName) > cardWidth - 20) {
                targetName = targetName.substring(0, Math.min(15, targetName.length())) + "...";
            }

            graphics.drawCenteredString(this.font,
                    "Target: " + targetName,
                    cardX + cardWidth / 2, textY, 0x333333);
            textY += lineHeight;

            graphics.drawCenteredString(this.font,
                    "Count: " + bounty.getTargets().get(0).getCount(),
                    cardX + cardWidth / 2, textY, 0x666666);
            textY += lineHeight + 5;
        }

        // ✅ TOKEN COST - ONLY for BOSS bounties
        if (bounty.isBossBounty()) {
            int tokenCost = bounty.getTokenCost();
            boolean canAfford = playerTokens >= tokenCost;

            graphics.drawString(this.font, "Cost:", cardX + 8, textY, 0x333333);
            textY += lineHeight;

            int costColor = canAfford ? 0x5AFF5A : 0xFF5555;
            graphics.drawString(this.font,
                    "  ⚔ " + tokenCost + " Tokens",
                    cardX + 15, textY, costColor);
            textY += lineHeight;

            graphics.drawString(this.font,
                    "  (You: " + playerTokens + "/100)",
                    cardX + 15, textY, 0x888888);
            textY += lineHeight + 3;

            if (!canAfford) {
                graphics.drawCenteredString(this.font,
                        "⚠ Need " + (tokenCost - playerTokens) + " more!",
                        cardX + cardWidth / 2, textY, 0xFF5555);
                textY += lineHeight + 3;
            }
        } else {
            // ✅ Show "FREE" for non-boss bounties
            graphics.drawCenteredString(this.font,
                    "✓ FREE TO ACCEPT",
                    cardX + cardWidth / 2, textY, 0x5AFF5A);
            textY += lineHeight + 5;
        }

        // Rewards
        graphics.drawString(this.font, "Rewards:", cardX + 8, textY, 0x333333);
        textY += lineHeight;

        graphics.drawString(this.font, "  Coins: " + bounty.getRewardCoins(),
                cardX + 15, textY, 0xFFD700);
        textY += lineHeight;

        graphics.drawString(this.font, "  XP: " + bounty.getRewardExperience(),
                cardX + 15, textY, 0x55FF55);
        textY += lineHeight + 3;

        // Time remaining
        long timeLeft = bounty.getExpirationTime() - minecraft.player.level().getGameTime();
        long minutes = timeLeft / 1200;
        long hours = minutes / 60;
        minutes = minutes % 60;

        String timeString = hours > 0 ? hours + "h " + minutes + "m" : minutes + "m";
        graphics.drawCenteredString(this.font,
                "⏱ " + timeString + " left",
                cardX + cardWidth / 2, textY, 0x666666);
    }

    @Override
    public boolean isPauseScreen() {
        return true;
    }
}