package Ash_Hollow_Requiem.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class AttributeNotificationRenderer {

    private static final List<AttributeNotification> notifications = new ArrayList<>();

    /**
     * Add a new attribute level-up notification
     */
    public static void showAttributeLevelUp(String attributeName, int newLevel) {
        String flavorText = getFlavorText(attributeName, newLevel);
        notifications.add(new AttributeNotification(
                attributeName + " has been increased!",
                attributeName + " is now level " + newLevel + ".",
                flavorText,
                System.currentTimeMillis()
        ));
    }

    /**
     * Get flavor text based on attribute type
     */
    private static String getFlavorText(String attributeName, int level) {
        String attr = attributeName.toLowerCase();

        if (attr.equals("strength")) {
            if (level < 10) return "You feel stronger.";
            else if (level < 20) return "Your muscles bulge with power.";
            else if (level < 30) return "You feel like you could crush boulders.";
            else return "Your strength is legendary!";
        }
        else if (attr.equals("dexterity")) {
            if (level < 10) return "Your movements feel more fluid.";
            else if (level < 20) return "You move with grace and precision.";
            else if (level < 30) return "Your reflexes are lightning-fast.";
            else return "You move like a shadow!";
        }
        else if (attr.equals("constitution")) {
            if (level < 10) return "You feel more resilient.";
            else if (level < 20) return "Your body hardens like iron.";
            else if (level < 30) return "You shrug off wounds that would kill others.";
            else return "You are nearly indestructible!";
        }
        else if (attr.equals("intelligence")) {
            if (level < 10) return "Your mind feels sharper.";
            else if (level < 20) return "Complex problems seem trivial now.";
            else if (level < 30) return "Your intellect is formidable.";
            else return "You perceive truths others cannot fathom!";
        }
        else if (attr.equals("willpower")) {
            if (level < 10) return "Your resolve strengthens.";
            else if (level < 20) return "Your mind becomes unshakeable.";
            else if (level < 30) return "Fear has no hold on you.";
            else return "Your will is indomitable!";
        }

        return "You feel improved.";
    }

    @SubscribeEvent
    public static void onRenderOverlay(RenderGuiOverlayEvent.Post event) {
        if (notifications.isEmpty()) return;

        Minecraft mc = Minecraft.getInstance();
        GuiGraphics graphics = event.getGuiGraphics();

        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int notificationWidth = 300;
        int notificationX = (screenWidth - notificationWidth) / 2; // Center horizontally
        int notificationY = 30; // Near top of screen

        long currentTime = System.currentTimeMillis();
        Iterator<AttributeNotification> iterator = notifications.iterator();

        while (iterator.hasNext()) {
            AttributeNotification notification = iterator.next();
            long age = currentTime - notification.timestamp;

            // Remove after 5 seconds
            if (age > 5000) {
                iterator.remove();
                continue;
            }

            // Calculate alpha for fade-in and fade-out
            float alpha = 1.0f;
            if (age < 300) {
                // Fade in during first 300ms
                alpha = age / 300f;
            } else if (age > 4500) {
                // Fade out during last 500ms
                alpha = (5000 - age) / 500f;
            }

            // Render notification
            renderNotification(graphics, notificationX, notificationY, notificationWidth, notification, alpha);

            notificationY += 80; // Stack multiple notifications vertically
        }
    }

    private static void renderNotification(GuiGraphics graphics, int x, int y, int width, AttributeNotification notification, float alpha) {
        int height = 70;
        int backgroundColor = (int)(alpha * 200) << 24 | 0x2C1810; // Brown with alpha
        int borderColor = (int)(alpha * 255) << 24 | 0xFFD700; // Gold with alpha
        int textColor = (int)(alpha * 255) << 24 | 0xFFFFFF; // White with alpha

        Minecraft mc = Minecraft.getInstance();

        // Draw background
        graphics.fill(x, y, x + width, y + height, backgroundColor);

        // Draw gold border
        graphics.fill(x, y, x + width, y + 2, borderColor); // Top
        graphics.fill(x, y + height - 2, x + width, y + height, borderColor); // Bottom
        graphics.fill(x, y, x + 2, y + height, borderColor); // Left
        graphics.fill(x + width - 2, y, x + width, y + height, borderColor); // Right

        // Draw text lines
        int textY = y + 8;
        int lineHeight = 12;

        // Title (larger and bold)
        graphics.drawCenteredString(mc.font, notification.title, x + width / 2, textY,
                (int)(alpha * 255) << 24 | 0xFFD700); // Gold
        textY += lineHeight + 3;

        // Subtitle
        graphics.drawCenteredString(mc.font, notification.subtitle, x + width / 2, textY, textColor);
        textY += lineHeight + 2;

        // Flavor text (italic style color)
        graphics.drawCenteredString(mc.font, notification.flavorText, x + width / 2, textY,
                (int)(alpha * 255) << 24 | 0xCCCCCC); // Light gray
    }

    private static class AttributeNotification {
        final String title;
        final String subtitle;
        final String flavorText;
        final long timestamp;

        AttributeNotification(String title, String subtitle, String flavorText, long timestamp) {
            this.title = title;
            this.subtitle = subtitle;
            this.flavorText = flavorText;
            this.timestamp = timestamp;
        }
    }
}