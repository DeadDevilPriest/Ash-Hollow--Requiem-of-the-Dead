package Ash_Hollow_Requiem.bounty;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import java.util.List;
import java.util.UUID;

/**
 * Represents a bounty contract with target mobs and rewards
 */
public class Bounty {
    private final UUID bountyId;
    private final BountyRarity rarity;
    private final BountyType type;
    private final List<BountyTarget> targets;
    private final int totalKillsRequired;
    private int currentKills;
    private final long expirationTime;
    private boolean completed;
    private boolean claimed;

    // Reward fields
    private final int rewardCoins;
    private final int rewardExperience;

    public Bounty(UUID bountyId, BountyRarity rarity, BountyType type,
                  List<BountyTarget> targets, int totalKillsRequired, long expirationTime) {
        this.bountyId = bountyId;
        this.rarity = rarity;
        this.type = type;
        this.targets = targets;
        this.totalKillsRequired = totalKillsRequired;
        this.currentKills = 0;
        this.expirationTime = expirationTime;
        this.completed = false;
        this.claimed = false;

        // Calculate rewards based on rarity
        this.rewardCoins = rarity.getBaseReward();
        this.rewardExperience = rarity.getBaseReward() / 5; // XP is 1/5 of coins
    }

    /**
     * Constructor with explicit rewards
     */
    public Bounty(UUID bountyId, BountyRarity rarity, BountyType type,
                  List<BountyTarget> targets, int totalKillsRequired, long expirationTime,
                  int rewardCoins, int rewardExperience) {
        this.bountyId = bountyId;
        this.rarity = rarity;
        this.type = type;
        this.targets = targets;
        this.totalKillsRequired = totalKillsRequired;
        this.currentKills = 0;
        this.expirationTime = expirationTime;
        this.completed = false;
        this.claimed = false;
        this.rewardCoins = rewardCoins;
        this.rewardExperience = rewardExperience;
    }

    /**
     * Registers a kill for this bounty
     * @param entity The killed entity
     * @return true if the kill counted towards this bounty
     */
    public boolean registerKill(LivingEntity entity) {
        if (completed || claimed) return false;

        EntityType<?> entityType = entity.getType();
        for (BountyTarget target : targets) {
            if (target.getEntityType().equals(entityType)) {
                currentKills++;
                target.incrementKills();

                if (currentKills >= totalKillsRequired) {
                    completed = true;
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the bounty has expired
     */
    public boolean hasExpired(long currentTime) {
        return currentTime > expirationTime && !completed;
    }

    // Getters
    public UUID getBountyId() { return bountyId; }
    public BountyRarity getRarity() { return rarity; }
    public BountyType getType() { return type; }
    public List<BountyTarget> getTargets() { return targets; }
    public int getTotalKillsRequired() { return totalKillsRequired; }
    public int getCurrentKills() { return currentKills; }
    public long getExpirationTime() { return expirationTime; }
    public boolean isCompleted() { return completed; }
    public boolean isClaimed() { return claimed; }

    // Reward getters
    public int getRewardCoins() { return rewardCoins; }
    public int getRewardExperience() { return rewardExperience; }

    // Deprecated - tokens are no longer earned from bounties
    @Deprecated
    public int getRewardTokens() { return 0; }

    public void setClaimed(boolean claimed) { this.claimed = claimed; }

    /**
     * Gets the progress as a percentage
     */
    public float getProgress() {
        return (float) currentKills / totalKillsRequired;
    }

    /**
     * Gets star rating based on rarity (0.5 to 5 stars)
     * Uses full stars (★) and half stars (⯨)
     */
    public String getStars() {
        // For 10-tier rarity system, map to stars (0.5 to 5 stars)
        float stars = switch (rarity) {
            case COMMON -> 0.5f;
            case UNCOMMON -> 1.0f;
            case RARE -> 1.5f;
            case EPIC -> 2.0f;
            case LEGENDARY -> 2.5f;
            case ANCIENT -> 3.0f;
            case CURSED -> 3.5f;
            case EXPERIMENTAL -> 4.0f;
            case HOLLOW -> 4.5f;
            case GODLY -> 4.5f;
            case INSANE -> 5.0f;
            case UNKNOWN -> 5.0f;  // ??? rarity
        };

        int fullStars = (int) stars;
        boolean hasHalf = (stars % 1.0f) != 0;

        StringBuilder result = new StringBuilder();
        result.append("★".repeat(fullStars));
        if (hasHalf) {
            result.append("⯨");
        }

        return result.toString();
    }
}