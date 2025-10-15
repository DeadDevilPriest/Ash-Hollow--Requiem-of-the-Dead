package Ash_Hollow_Requiem.bounty;

import net.minecraft.world.entity.EntityType;

/**
 * Represents a single target entity type within a bounty
 */
public class BountyTarget {
    private final EntityType<?> entityType;
    private final int requiredKills;
    private int currentKills;
    private final boolean isBoss;

    public BountyTarget(EntityType<?> entityType, int requiredKills, boolean isBoss) {
        this.entityType = entityType;
        this.requiredKills = requiredKills;
        this.currentKills = 0;
        this.isBoss = isBoss;
    }

    /**
     * Increments the kill count for this target
     */
    public void incrementKills() {
        if (currentKills < requiredKills) {
            currentKills++;
        }
    }

    /**
     * Checks if this target is complete
     */
    public boolean isComplete() {
        return currentKills >= requiredKills;
    }

    // Getters
    public EntityType<?> getEntityType() {
        return entityType;
    }

    public int getRequiredKills() {
        return requiredKills;
    }

    public int getCurrentKills() {
        return currentKills;
    }

    public boolean isBoss() {
        return isBoss;
    }

    /**
     * Alias for getRequiredKills() - used in some UI code
     */
    public int getCount() {
        return requiredKills;
    }

    /**
     * Gets progress as a percentage
     */
    public float getProgress() {
        return (float) currentKills / requiredKills;
    }
}