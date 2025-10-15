package Ash_Hollow_Requiem.bounty;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;

/**
 * Defines the 6 bounty categories with their properties and modifiers
 */
public enum BountyCategory {
    BOUNTY(
            "Bounty",
            "Standard bounty hunts",
            ChatFormatting.GREEN,
            0x4CAF50,
            1.0f,    // coin multiplier
            1.0f,    // exp multiplier
            false,   // requires tokens
            0        // token cost
    ),

    SPECIAL(
            "Special",
            "Unique target bounties",
            ChatFormatting.BLUE,
            0x2196F3,
            1.0f,
            1.0f,
            false,
            0
    ),

    HORDE(
            "Horde",
            "Kill many enemies",
            ChatFormatting.YELLOW,
            0xFFEB3B,
            1.5f,    // +50% coins
            1.0f,
            false,
            0
    ),

    ELITE(
            "Elite",
            "Hunt powerful foes",
            ChatFormatting.GOLD,
            0xFFD700,
            1.2f,    // +20% coins
            1.5f,    // +50% exp
            false,
            0
    ),

    BOSS(
            "Boss",
            "Face legendary bosses",
            ChatFormatting.DARK_RED,
            0x8B0000,
            1.5f,    // +50% coins
            2.0f,    // +100% exp (DOUBLE!)
            true,    // requires tokens to unlock
            5        // costs 5 tokens
    ),

    EVENT(
            "Event",
            "Limited time challenges",
            ChatFormatting.LIGHT_PURPLE,
            0xE91E63,
            1.3f,    // +30% coins
            1.3f,    // +30% exp
            false,
            0
    );

    private final String displayName;
    private final String description;
    private final ChatFormatting chatColor;
    private final int hexColor;
    private final float coinMultiplier;
    private final float expMultiplier;
    private final boolean requiresTokens;
    private final int tokenCost;

    BountyCategory(String displayName, String description, ChatFormatting chatColor,
                   int hexColor, float coinMultiplier, float expMultiplier,
                   boolean requiresTokens, int tokenCost) {
        this.displayName = displayName;
        this.description = description;
        this.chatColor = chatColor;
        this.hexColor = hexColor;
        this.coinMultiplier = coinMultiplier;
        this.expMultiplier = expMultiplier;
        this.requiresTokens = requiresTokens;
        this.tokenCost = tokenCost;
    }

    // Getters
    public String getDisplayName() { return displayName; }
    public String getDescription() { return description; }
    public ChatFormatting getChatColor() { return chatColor; }
    public int getHexColor() { return hexColor; }
    public float getCoinMultiplier() { return coinMultiplier; }
    public float getExpMultiplier() { return expMultiplier; }
    public boolean requiresTokens() { return requiresTokens; }
    public int getTokenCost() { return tokenCost; }

    /**
     * Gets the colored display name component
     */
    public Component getColoredName() {
        return Component.literal(displayName).withStyle(chatColor);
    }

    /**
     * Gets the colored name with bold styling
     */
    public Component getBoldColoredName() {
        return Component.literal(displayName).withStyle(chatColor, ChatFormatting.BOLD);
    }

    /**
     * Gets full display with description
     */
    public Component getFullDisplay() {
        return Component.literal(displayName)
                .withStyle(chatColor, ChatFormatting.BOLD)
                .append(Component.literal("\n" + description)
                        .withStyle(ChatFormatting.GRAY));
    }

    /**
     * Applies category modifiers to base coin reward
     */
    public int applyRewardModifiers(int baseCoins, int baseExp, RewardResult result) {
        result.coins = (int)(baseCoins * coinMultiplier);
        result.exp = (int)(baseExp * expMultiplier);
        return result.coins;
    }

    /**
     * Gets the icon/symbol for this category
     */
    public String getIcon() {
        return switch (this) {
            case BOUNTY -> "⚔";
            case SPECIAL -> "✦";
            case HORDE -> "☠";
            case ELITE -> "♕";
            case BOSS -> "☬";
            case EVENT -> "★";
        };
    }

    /**
     * Gets bonus item description for this category
     */
    public String getBonusItemDescription() {
        return switch (this) {
            case BOUNTY -> "None";
            case SPECIAL -> "None";
            case HORDE -> "+32-96 Arrows";
            case ELITE -> "30% chance for Enchanted Gear";
            case BOSS -> "Totem of Undying (Rare+)";
            case EVENT -> "Event Token";
        };
    }

    /**
     * Checks if player can access this category
     */
    public boolean canAccess(int playerTokens) {
        return !requiresTokens || playerTokens >= tokenCost;
    }

    /**
     * Gets lock status message
     */
    public Component getLockMessage() {
        if (!requiresTokens) {
            return Component.empty();
        }
        return Component.literal("🔒 Requires " + tokenCost + " Tokens")
                .withStyle(ChatFormatting.RED);
    }

    /**
     * Gets detailed info component
     */
    public Component getDetailedInfo() {
        Component info = getColoredName()
                .copy()
                .append(Component.literal(" " + getIcon() + "\n")
                        .withStyle(ChatFormatting.WHITE));

        info = info.copy().append(Component.literal(description + "\n")
                .withStyle(ChatFormatting.GRAY));

        // Reward modifiers
        if (coinMultiplier != 1.0f) {
            int percent = (int)((coinMultiplier - 1.0f) * 100);
            String sign = percent > 0 ? "+" : "";
            info = info.copy().append(Component.literal("  Coins: " + sign + percent + "%\n")
                    .withStyle(ChatFormatting.GOLD));
        }

        if (expMultiplier != 1.0f) {
            int percent = (int)((expMultiplier - 1.0f) * 100);
            String sign = percent > 0 ? "+" : "";
            info = info.copy().append(Component.literal("  XP: " + sign + percent + "%\n")
                    .withStyle(ChatFormatting.AQUA));
        }

        // Bonus items
        String bonusDesc = getBonusItemDescription();
        if (!bonusDesc.equals("None")) {
            info = info.copy().append(Component.literal("  Bonus: " + bonusDesc + "\n")
                    .withStyle(ChatFormatting.LIGHT_PURPLE));
        }

        // Token requirement
        if (requiresTokens) {
            info = info.copy().append(Component.literal("  Cost: " + tokenCost + " Tokens")
                    .withStyle(ChatFormatting.RED));
        }

        return info;
    }

    /**
     * Helper class for returning multiple reward values
     */
    public static class RewardResult {
        public int coins;
        public int exp;

        public RewardResult() {
            this.coins = 0;
            this.exp = 0;
        }

        public RewardResult(int coins, int exp) {
            this.coins = coins;
            this.exp = exp;
        }
    }

    /**
     * Gets category by name (case-insensitive)
     */
    public static BountyCategory fromString(String name) {
        for (BountyCategory category : values()) {
            if (category.displayName.equalsIgnoreCase(name)) {
                return category;
            }
        }
        return BOUNTY; // default
    }

    /**
     * Gets all unlocked categories for a player
     */
    public static BountyCategory[] getUnlockedCategories(int playerTokens) {
        return java.util.Arrays.stream(values())
                .filter(cat -> cat.canAccess(playerTokens))
                .toArray(BountyCategory[]::new);
    }

    /**
     * Gets all locked categories for a player
     */
    public static BountyCategory[] getLockedCategories(int playerTokens) {
        return java.util.Arrays.stream(values())
                .filter(cat -> !cat.canAccess(playerTokens))
                .toArray(BountyCategory[]::new);
    }
}