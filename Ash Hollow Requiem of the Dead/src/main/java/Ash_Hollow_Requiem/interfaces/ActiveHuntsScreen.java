package Ash_Hollow_Requiem.interfaces;

import Ash_Hollow_Requiem.bounty.Bounty;
import Ash_Hollow_Requiem.bounty.BountyTarget;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;

import java.util.List;

public class ActiveHuntsScreen extends Screen {
    private final Screen parent;
    private final List<Bounty> activeBounties;
    private int scrollOffset = 0;
    private static final int BOUNTIES_PER_PAGE = 4;

    protected ActiveHuntsScreen(Screen parent, List<Bounty> bounties) {
        super(Component.literal("Active Hunts"));
        this.parent = parent;
        this.activeBounties = bounties;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int startY = 80;

        if (activeBounties != null && !activeBounties.isEmpty()) {
            int maxDisplay = Math.min(BOUNTIES_PER_PAGE, activeBounties.size() - scrollOffset);

            for (int i = 0; i < maxDisplay; i++) {
                int bountyIndex = scrollOffset + i;
                Bounty bounty = activeBounties.get(bountyIndex);
                int yPos = startY + (i * 80);

                // View details button
                this.addRenderableWidget(Button.builder(
                        Component.literal("View Details"),
                        btn -> viewBountyDetails(bounty)
                ).bounds(centerX - 120, yPos + 50, 90, 20).build());

                // Track button (opens compass or waypoint)
                this.addRenderableWidget(Button.builder(
                        Component.literal("Track"),
                        btn -> trackBounty(bounty)
                ).bounds(centerX - 20, yPos + 50, 70, 20).build());

                // Abandon button
                this.addRenderableWidget(Button.builder(
                        Component.literal("Abandon")
                                .withStyle(ChatFormatting.RED),
                        btn -> abandonBounty(bounty)
                ).bounds(centerX + 60, yPos + 50, 70, 20).build());
            }

            // Scroll buttons
            if (activeBounties.size() > BOUNTIES_PER_PAGE) {
                if (scrollOffset > 0) {
                    this.addRenderableWidget(Button.builder(
                            Component.literal("↑ Previous"),
                            btn -> {
                                scrollOffset = Math.max(0, scrollOffset - BOUNTIES_PER_PAGE);
                                this.rebuildWidgets();
                            }
                    ).bounds(centerX - 50, this.height - 70, 100, 20).build());
                }

                if (scrollOffset + BOUNTIES_PER_PAGE < activeBounties.size()) {
                    this.addRenderableWidget(Button.builder(
                            Component.literal("↓ Next"),
                            btn -> {
                                scrollOffset += BOUNTIES_PER_PAGE;
                                this.rebuildWidgets();
                            }
                    ).bounds(centerX - 50, this.height - 45, 100, 20).build());
                }
            }
        }

        // Back button
        this.addRenderableWidget(Button.builder(
                Component.literal("Back to Board"),
                btn -> minecraft.setScreen(parent)
        ).bounds(centerX - 60, this.height - 30, 120, 20).build());
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics);

        int centerX = this.width / 2;

        graphics.drawCenteredString(this.font, "My Active Hunts", centerX, 20, 0xFFFFFF);

        if (activeBounties == null || activeBounties.isEmpty()) {
            graphics.drawCenteredString(this.font, "No active bounties",
                    centerX, 60, 0xAAAAAA);
            graphics.drawCenteredString(this.font,
                    "Accept contracts from the Bounty Board!",
                    centerX, 75, 0x888888);
        } else {
            graphics.drawCenteredString(this.font,
                    "Active: " + activeBounties.size() + "/5",
                    centerX, 45, 0xFFD700);

            // Render bounty cards
            renderBountyCards(graphics, centerX);
        }

        super.render(graphics, mouseX, mouseY, partialTick);
    }

    private void renderBountyCards(GuiGraphics graphics, int centerX) {
        int startY = 80;
        int maxDisplay = Math.min(BOUNTIES_PER_PAGE, activeBounties.size() - scrollOffset);

        for (int i = 0; i < maxDisplay; i++) {
            int bountyIndex = scrollOffset + i;
            Bounty bounty = activeBounties.get(bountyIndex);
            int yPos = startY + (i * 80);

            // Card background
            graphics.fill(centerX - 130, yPos, centerX + 140, yPos + 70, 0xFF3A3A3A);
            graphics.fill(centerX - 128, yPos + 2, centerX + 138, yPos + 68, 0xFF2A2A2A);

            // Rarity border - Fixed null check
            Integer rarityColorObj = bounty.getRarity().getColor().getColor();
            int rarityColor = (rarityColorObj != null) ? rarityColorObj : 0xFFFFFF;
            graphics.fill(centerX - 130, yPos, centerX + 140, yPos + 2, rarityColor);

            // Bounty info
            String title = getBountyTitle(bounty);
            graphics.drawString(this.font, title, centerX - 120, yPos + 8, rarityColor);

            String type = bounty.getType().getDisplayName();
            graphics.drawString(this.font, type, centerX - 120, yPos + 20, 0xAAAAAA);

            // Progress bar
            int barWidth = 200;
            int barX = centerX - 120;
            int barY = yPos + 32;
            float progress = bounty.getProgress();

            // Background bar
            graphics.fill(barX, barY, barX + barWidth, barY + 8, 0xFF555555);
            // Progress fill
            int fillWidth = (int)(barWidth * progress);
            int progressColor = bounty.isCompleted() ? 0xFF00FF00 : 0xFFFFAA00;
            graphics.fill(barX, barY, barX + fillWidth, barY + 8, progressColor);

            // Progress text
            String progressText = String.format("%d/%d (%.0f%%)",
                    bounty.getCurrentKills(),
                    bounty.getTotalKillsRequired(),
                    progress * 100);
            graphics.drawString(this.font, progressText,
                    centerX + 90, yPos + 33, 0xFFFFFF);
        }
    }

    private String getBountyTitle(Bounty bounty) {
        if (bounty.getTargets().isEmpty()) {
            return "Unknown Contract";
        }

        if (bounty.getTargets().size() == 1) {
            return bounty.getTargets().get(0).getEntityType()
                    .getDescription().getString();
        }

        return "Multi-Target Hunt";
    }

    private void viewBountyDetails(Bounty bounty) {
        // Open detailed view (could reuse IntegratedBountyDetailsScreen)
        minecraft.player.displayClientMessage(
                Component.literal("Viewing details for: " + getBountyTitle(bounty))
                        .withStyle(ChatFormatting.YELLOW),
                false
        );
    }

    private void trackBounty(Bounty bounty) {
        // TODO: Set active tracking/waypoint for this bounty
        minecraft.player.displayClientMessage(
                Component.literal("Now tracking: " + getBountyTitle(bounty))
                        .withStyle(ChatFormatting.GREEN),
                false
        );
    }

    private void abandonBounty(Bounty bounty) {
        if (minecraft.player != null) {
            // TODO: Confirm dialog before abandoning
            minecraft.player.displayClientMessage(
                    Component.literal("⚠ Are you sure? Type /bounty abandon to confirm")
                            .withStyle(ChatFormatting.YELLOW),
                    false
            );
        }
    }
}