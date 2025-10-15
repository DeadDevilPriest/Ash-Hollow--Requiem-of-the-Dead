package Ash_Hollow_Requiem.bounty;

import net.minecraft.ChatFormatting;

/**
 * Rarity tiers for bounties
 */
public enum BountyRarity {
    COMMON("Common", ChatFormatting.GRAY, 50, 1, 3, 0x9E9E9E),
    UNCOMMON("Uncommon", ChatFormatting.GREEN, 100, 1, 3, 0x4CAF50),
    RARE("Rare", ChatFormatting.BLUE, 250, 2, 4, 0x2196F3),
    EPIC("Epic", ChatFormatting.DARK_PURPLE, 500, 2, 5, 0x9C27B0),
    LEGENDARY("Legendary", ChatFormatting.GOLD, 1000, 3, 6, 0xFFD700),
    ANCIENT("Ancient", ChatFormatting.DARK_AQUA, 1500, 3, 6, 0x00BCD4),
    CURSED("Cursed", ChatFormatting.DARK_RED, 2000, 4, 7, 0x8B0000),
    EXPERIMENTAL("Experimental", ChatFormatting.AQUA, 2500, 4, 7, 0x00FFFF),
    HOLLOW("Hollow", ChatFormatting.DARK_GRAY, 3000, 5, 8, 0x404040),
    GODLY("Godly", ChatFormatting.LIGHT_PURPLE, 5000, 5, 8, 0xFF69B4),
    INSANE("Insane", ChatFormatting.RED, 10000, 6, 10, 0xFF0000),
    UNKNOWN("???", ChatFormatting.DARK_PURPLE, 20000, 7, 12, 0x000000);

    private final String displayName;
    private final ChatFormatting color;
    private final int baseReward;
    private final int minTargets;
    private final int maxTargets;
    private final int colorInt; // For GUI rendering

    BountyRarity(String displayName, ChatFormatting color, int baseReward,
                 int minTargets, int maxTargets, int colorInt) {
        this.displayName = displayName;
        this.color = color;
        this.baseReward = baseReward;
        this.minTargets = minTargets;
        this.maxTargets = maxTargets;
        this.colorInt = colorInt;
    }

    public String getDisplayName() {
        return displayName;
    }

    public ChatFormatting getColor() {
        return color;
    }

    /**
     * Gets the color as an integer for GUI rendering
     */
    public int getColorInt() {
        return colorInt;
    }

    public int getBaseReward() {
        return baseReward;
    }

    public int getMinTargets() {
        return minTargets;
    }

    public int getMaxTargets() {
        return maxTargets;
    }
}