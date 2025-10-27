package Ash_Hollow_Requiem.client;

import Ash_Hollow_Requiem.Ash_Hollow;
import Ash_Hollow_Requiem.playerdata.PlayerDataProvider;
import Ash_Hollow_Requiem.skilltributes.Attributes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Ash_Hollow.MODID, value = Dist.CLIENT)
public class AttributeOverlayRenderer {

    @SubscribeEvent
    public static void onRenderInventory(ScreenEvent.Render.Post event) {
        if (!(event.getScreen() instanceof InventoryScreen)) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        // Check if Curios screen is open
        if (isCuriosScreenOpen(event.getScreen())) {
            return;
        }

        // Get player data (attributes are inside PlayerData!)
        mc.player.getCapability(PlayerDataProvider.PLAYER_DATA).ifPresent(data -> {
            renderAttributesPanel(event.getGuiGraphics(), event.getScreen().width,
                    event.getScreen().height, data.getAttributes());
        });
    }

    private static boolean isCuriosScreenOpen(net.minecraft.client.gui.screens.Screen screen) {
        if (!Ash_Hollow.curiosLoaded) {
            return false;
        }

        try {
            String screenClass = screen.getClass().getName();
            return screenClass.toLowerCase().contains("curios");
        } catch (Exception e) {
            return false;
        }
    }

    private static void renderAttributesPanel(GuiGraphics graphics, int screenWidth, int screenHeight, Attributes attributes) {
        int panelX = 10; // Left side
        int panelY = 40;
        int panelWidth = 110;
        int panelHeight = 140;

        // Draw panel background
        graphics.fill(panelX, panelY, panelX + panelWidth, panelY + panelHeight, 0xAA2C1810);
        graphics.fill(panelX + 2, panelY + 2, panelX + panelWidth - 2, panelY + panelHeight - 2, 0xAA8B6F47);

        // Draw title
        graphics.drawString(Minecraft.getInstance().font, "Attributes", panelX + 5, panelY + 5, 0xFFFFFF);

        int yOffset = panelY + 18;
        int lineHeight = 12;

        // Draw each attribute
        drawAttribute(graphics, "STR", attributes.getStrength(), panelX + 5, yOffset, 0xFF5555);
        yOffset += lineHeight;
        drawAttribute(graphics, "DEX", attributes.getDexterity(), panelX + 5, yOffset, 0x55FF55);
        yOffset += lineHeight;
        drawAttribute(graphics, "CON", attributes.getConstitution(), panelX + 5, yOffset, 0xFF5555);
        yOffset += lineHeight;
        drawAttribute(graphics, "INT", attributes.getIntelligence(), panelX + 5, yOffset, 0x5555FF);
        yOffset += lineHeight;
        drawAttribute(graphics, "WIL", attributes.getWillpower(), panelX + 5, yOffset, 0xFF55FF);
        yOffset += lineHeight;

        // Draw divider
        yOffset += 3;
        graphics.fill(panelX + 5, yOffset, panelX + panelWidth - 5, yOffset + 1, 0xFF444444);
        yOffset += 5;

        // Draw total
        int totalPoints = attributes.getTotalAttributePoints();
        graphics.drawString(Minecraft.getInstance().font, "Total: " + totalPoints, panelX + 5, yOffset, 0xFFD700);
    }

    private static void drawAttribute(GuiGraphics graphics, String name, int value, int x, int y, int color) {
        Minecraft mc = Minecraft.getInstance();

        graphics.drawString(mc.font, name + ":", x, y, 0xAAAAAA);
        graphics.drawString(mc.font, String.valueOf(value), x + 35, y, color);

        // Draw bar
        int barX = x + 55;
        int barY = y + 2;
        int barWidth = 40;
        int barHeight = 6;
        int maxValue = 50;

        graphics.fill(barX, barY, barX + barWidth, barY + barHeight, 0xFF000000);
        int fillWidth = (int)((value / (float)maxValue) * barWidth);
        graphics.fill(barX, barY, barX + fillWidth, barY + barHeight, color);

        // Border
        graphics.fill(barX, barY, barX + barWidth, barY + 1, 0xFF666666);
        graphics.fill(barX, barY + barHeight - 1, barX + barWidth, barY + barHeight, 0xFF666666);
        graphics.fill(barX, barY, barX + 1, barY + barHeight, 0xFF666666);
        graphics.fill(barX + barWidth - 1, barY, barX + barWidth, barY + barHeight, 0xFF666666);
    }
}