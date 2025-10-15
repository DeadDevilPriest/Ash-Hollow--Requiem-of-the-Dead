package Ash_Hollow_Requiem.bounty;

import net.minecraft.world.entity.EntityType;
import java.util.*;

/**
 * Handles the generation of random bounties with shuffled mob selections
 */
public class BountyGenerator {
    private static final Random RANDOM = new Random();

    // Duration constants (in ticks: 20 ticks = 1 second)
    private static final long COMMON_DURATION = 20 * 60 * 30; // 30 minutes
    private static final long UNCOMMON_DURATION = 20 * 60 * 60; // 1 hour
    private static final long RARE_DURATION = 20 * 60 * 120; // 2 hours
    private static final long EPIC_DURATION = 20 * 60 * 240; // 4 hours
    private static final long LEGENDARY_DURATION = 20 * 60 * 480; // 8 hours

    /**
     * Generates a random standard bounty
     */
    public static Bounty generateStandardBounty(BountyRarity rarity, long currentTime) {
        // Get the appropriate mob pool
        List<EntityType<?>> mobPool = BountyPools.getPoolForRarity(rarity);

        // Determine number of different mob types
        int targetCount = RANDOM.nextInt(
                rarity.getMaxTargets() - rarity.getMinTargets() + 1
        ) + rarity.getMinTargets();

        // Shuffle and select mobs
        List<EntityType<?>> selectedMobs = shuffleAndSelect(mobPool, targetCount);

        // Create targets with kill requirements
        List<BountyTarget> targets = new ArrayList<>();
        int totalKills = 0;

        for (EntityType<?> mobType : selectedMobs) {
            int killsRequired = calculateKillRequirement(rarity);
            targets.add(new BountyTarget(mobType, killsRequired, false));
            totalKills += killsRequired;
        }

        long duration = getDurationForRarity(rarity);

        return new Bounty(
                UUID.randomUUID(),
                rarity,
                BountyType.STANDARD,
                targets,
                totalKills,
                currentTime + duration
        );
    }

    /**
     * Generates a boss bounty (single powerful enemy)
     */
    public static Bounty generateBossBounty(BountyRarity rarity, long currentTime) {
        List<EntityType<?>> bossPool = BountyPools.BOSS_MOBS;

        // For boss bounties, typically 1-2 different boss types
        int bossCount = (rarity.ordinal() >= BountyRarity.RARE.ordinal()) ? 2 : 1;

        List<EntityType<?>> selectedBosses = shuffleAndSelect(bossPool, bossCount);

        List<BountyTarget> targets = new ArrayList<>();
        int totalKills = 0;

        for (EntityType<?> bossType : selectedBosses) {
            // Boss kills are always 1-3 depending on rarity
            int killsRequired = Math.min(rarity.ordinal() + 1, 3);
            targets.add(new BountyTarget(bossType, killsRequired, true));
            totalKills += killsRequired;
        }

        long duration = getDurationForRarity(rarity) * 2; // Bosses get double time

        return new Bounty(
                UUID.randomUUID(),
                rarity,
                BountyType.BOSS,
                targets,
                totalKills,
                currentTime + duration
        );
    }

    /**
     * Generates a horde bounty (many kills of common mobs)
     */
    public static Bounty generateHordeBounty(BountyRarity rarity, long currentTime) {
        List<EntityType<?>> mobPool = BountyPools.COMMON_HOSTILES;

        // Horde bounties have 1-2 mob types with high kill counts
        List<EntityType<?>> selectedMobs = shuffleAndSelect(mobPool,
                RANDOM.nextInt(2) + 1);

        List<BountyTarget> targets = new ArrayList<>();
        int totalKills = 0;

        for (EntityType<?> mobType : selectedMobs) {
            // Horde bounties require many kills
            int killsRequired = calculateHordeKillRequirement(rarity);
            targets.add(new BountyTarget(mobType, killsRequired, false));
            totalKills += killsRequired;
        }

        long duration = getDurationForRarity(rarity);

        return new Bounty(
                UUID.randomUUID(),
                rarity,
                BountyType.HORDE,
                targets,
                totalKills,
                currentTime + duration
        );
    }

    /**
     * Generates an elite bounty (tough enemies)
     */
    public static Bounty generateEliteBounty(BountyRarity rarity, long currentTime) {
        List<EntityType<?>> elitePool = BountyPools.ELITE_MOBS;

        int targetCount = RANDOM.nextInt(
                Math.min(3, rarity.getMaxTargets() - rarity.getMinTargets() + 1)
        ) + rarity.getMinTargets();

        List<EntityType<?>> selectedElites = shuffleAndSelect(elitePool, targetCount);

        List<BountyTarget> targets = new ArrayList<>();
        int totalKills = 0;

        for (EntityType<?> eliteType : selectedElites) {
            int killsRequired = Math.max(rarity.ordinal() * 2, 3);
            targets.add(new BountyTarget(eliteType, killsRequired, false));
            totalKills += killsRequired;
        }

        long duration = (long) (getDurationForRarity(rarity) * 1.5f);

        return new Bounty(
                UUID.randomUUID(),
                rarity,
                BountyType.ELITE,
                targets,
                totalKills,
                (long)(currentTime + duration)
        );
    }

    /**
     * Shuffles a list and returns a random selection
     * This is the core randomization function
     */
    private static <T> List<T> shuffleAndSelect(List<T> source, int count) {
        if (source.isEmpty()) return new ArrayList<>();

        // Create a copy to avoid modifying the original
        List<T> shuffled = new ArrayList<>(source);
        Collections.shuffle(shuffled, RANDOM);

        // Take only the requested number of items
        count = Math.min(count, shuffled.size());
        return new ArrayList<>(shuffled.subList(0, count));
    }

    /**
     * Calculates kill requirement based on rarity
     */
    private static int calculateKillRequirement(BountyRarity rarity) {
        int base = switch (rarity) {
            case COMMON -> 5;
            case UNCOMMON -> 8;
            case RARE -> 12;
            case EPIC -> 20;
            case LEGENDARY -> 30;
            default -> 15; // For other rarities
        };

        // Add some randomness (±20%)
        int variance = base / 5;
        return base + RANDOM.nextInt(variance * 2 + 1) - variance;
    }

    /**
     * Calculates kill requirement for horde bounties (higher numbers)
     */
    private static int calculateHordeKillRequirement(BountyRarity rarity) {
        return calculateKillRequirement(rarity) * 3;
    }

    /**
     * Gets the duration for a bounty based on rarity
     */
    private static long getDurationForRarity(BountyRarity rarity) {
        return switch (rarity) {
            case COMMON -> COMMON_DURATION;
            case UNCOMMON -> UNCOMMON_DURATION;
            case RARE -> RARE_DURATION;
            case EPIC -> EPIC_DURATION;
            case LEGENDARY -> LEGENDARY_DURATION;
            default -> RARE_DURATION; // Default for other rarities
        };
    }

    /**
     * Generates a completely random bounty
     */
    public static Bounty generateRandomBounty(long currentTime) {
        // Random rarity with weighted chances
        BountyRarity rarity = getRandomRarity();

        // Random type (but bosses are rarer)
        BountyType type = getRandomType(rarity);

        return switch (type) {
            case BOSS -> generateBossBounty(rarity, currentTime);
            case HORDE -> generateHordeBounty(rarity, currentTime);
            case ELITE -> generateEliteBounty(rarity, currentTime);
            default -> generateStandardBounty(rarity, currentTime);
        };
    }

    /**
     * Gets a weighted random rarity
     */
    private static BountyRarity getRandomRarity() {
        int roll = RANDOM.nextInt(100);

        if (roll < 40) return BountyRarity.COMMON;
        if (roll < 70) return BountyRarity.UNCOMMON;
        if (roll < 88) return BountyRarity.RARE;
        if (roll < 97) return BountyRarity.EPIC;
        return BountyRarity.LEGENDARY;
    }

    /**
     * Gets a weighted random bounty type based on rarity
     */
    private static BountyType getRandomType(BountyRarity rarity) {
        // Boss bounties only appear at higher rarities
        if (rarity.ordinal() < BountyRarity.RARE.ordinal()) {
            int roll = RANDOM.nextInt(100);
            if (roll < 60) return BountyType.STANDARD;
            if (roll < 85) return BountyType.HORDE;
            return BountyType.ELITE;
        }

        int roll = RANDOM.nextInt(100);
        if (roll < 40) return BountyType.STANDARD;
        if (roll < 60) return BountyType.HORDE;
        if (roll < 80) return BountyType.ELITE;
        return BountyType.BOSS;
    }
}