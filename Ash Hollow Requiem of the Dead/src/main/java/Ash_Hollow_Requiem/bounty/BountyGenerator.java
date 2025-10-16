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
     * Generates a random standard bounty (NO TOKEN COST)
     */
    public static Bounty generateStandardBounty(BountyRarity rarity, long currentTime) {
        List<EntityType<?>> mobPool = BountyPools.getPoolForRarity(rarity);

        int targetCount = RANDOM.nextInt(
                rarity.getMaxTargets() - rarity.getMinTargets() + 1
        ) + rarity.getMinTargets();

        List<EntityType<?>> selectedMobs = shuffleAndSelect(mobPool, targetCount);

        List<BountyTarget> targets = new ArrayList<>();
        int totalKills = 0;

        for (EntityType<?> mobType : selectedMobs) {
            int killsRequired = calculateKillRequirement(rarity);
            targets.add(new BountyTarget(mobType, killsRequired, false));
            totalKills += killsRequired;
        }

        long duration = getDurationForRarity(rarity);

        // ✅ Standard bounties have NO token cost
        return new Bounty(
                UUID.randomUUID(),
                rarity,
                BountyType.STANDARD,
                targets,
                totalKills,
                currentTime + duration,
                rarity.getBaseReward(),
                rarity.getBaseReward() / 5,
                0 // No token cost
        );
    }

    /**
     * ✅ Generates a BOSS bounty with token cost (Epic to Unknown only)
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

        // ✅ ONLY BOSS bounties have token cost (5-100 tokens)
        int tokenCost = calculateTokenCost(rarity);

        return new Bounty(
                UUID.randomUUID(),
                rarity,
                BountyType.BOSS,
                targets,
                totalKills,
                currentTime + duration,
                rarity.getBaseReward() * 2, // Double coin reward for bosses
                rarity.getBaseReward() / 3, // XP reward
                tokenCost // ✅ Token cost ONLY for boss bounties
        );
    }

    /**
     * ✅ Calculate token cost for BOSS bounties (5-100 tokens)
     * Epic to Unknown rarities only
     */
    private static int calculateTokenCost(BountyRarity rarity) {
        return switch (rarity) {
            // Epic to Unknown (boss bounties only)
            case EPIC -> 5 + RANDOM.nextInt(6);         // 5-10 tokens
            case LEGENDARY -> 10 + RANDOM.nextInt(11);  // 10-20 tokens
            case ANCIENT -> 20 + RANDOM.nextInt(11);    // 20-30 tokens
            case CURSED -> 30 + RANDOM.nextInt(11);     // 30-40 tokens
            case EXPERIMENTAL -> 40 + RANDOM.nextInt(11); // 40-50 tokens
            case HOLLOW -> 50 + RANDOM.nextInt(16);     // 50-65 tokens
            case GODLY -> 65 + RANDOM.nextInt(16);      // 65-80 tokens
            case INSANE -> 80 + RANDOM.nextInt(16);     // 80-95 tokens
            case UNKNOWN -> 95 + RANDOM.nextInt(6);     // 95-100 tokens

            default -> 0; // Lower rarities shouldn't have boss bounties
        };
    }

    /**
     * Generates a horde bounty (NO TOKEN COST)
     */
    public static Bounty generateHordeBounty(BountyRarity rarity, long currentTime) {
        List<EntityType<?>> mobPool = BountyPools.COMMON_HOSTILES;

        List<EntityType<?>> selectedMobs = shuffleAndSelect(mobPool, RANDOM.nextInt(2) + 1);

        List<BountyTarget> targets = new ArrayList<>();
        int totalKills = 0;

        for (EntityType<?> mobType : selectedMobs) {
            int killsRequired = calculateHordeKillRequirement(rarity);
            targets.add(new BountyTarget(mobType, killsRequired, false));
            totalKills += killsRequired;
        }

        long duration = getDurationForRarity(rarity);

        // ✅ Horde bounties have NO token cost
        return new Bounty(
                UUID.randomUUID(),
                rarity,
                BountyType.HORDE,
                targets,
                totalKills,
                currentTime + duration,
                rarity.getBaseReward(),
                rarity.getBaseReward() / 5,
                0 // No token cost
        );
    }

    /**
     * Generates an elite bounty (NO TOKEN COST)
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

        // ✅ Elite bounties have NO token cost
        return new Bounty(
                UUID.randomUUID(),
                rarity,
                BountyType.ELITE,
                targets,
                totalKills,
                (long)(currentTime + duration),
                rarity.getBaseReward(),
                rarity.getBaseReward() / 5,
                0 // No token cost
        );
    }

    // ... rest of helper methods stay the same ...

    private static <T> List<T> shuffleAndSelect(List<T> source, int count) {
        if (source.isEmpty()) return new ArrayList<>();
        List<T> shuffled = new ArrayList<>(source);
        Collections.shuffle(shuffled, RANDOM);
        count = Math.min(count, shuffled.size());
        return new ArrayList<>(shuffled.subList(0, count));
    }

    private static int calculateKillRequirement(BountyRarity rarity) {
        int base = switch (rarity) {
            case COMMON -> 5;
            case UNCOMMON -> 8;
            case RARE -> 12;
            case EPIC -> 20;
            case LEGENDARY -> 30;
            default -> 15;
        };
        int variance = base / 5;
        return base + RANDOM.nextInt(variance * 2 + 1) - variance;
    }

    private static int calculateHordeKillRequirement(BountyRarity rarity) {
        return calculateKillRequirement(rarity) * 3;
    }

    private static long getDurationForRarity(BountyRarity rarity) {
        return switch (rarity) {
            case COMMON -> COMMON_DURATION;
            case UNCOMMON -> UNCOMMON_DURATION;
            case RARE -> RARE_DURATION;
            case EPIC -> EPIC_DURATION;
            case LEGENDARY -> LEGENDARY_DURATION;
            default -> RARE_DURATION;
        };
    }

    public static Bounty generateRandomBounty(long currentTime) {
        BountyRarity rarity = getRandomRarity();
        BountyType type = getRandomType(rarity);

        return switch (type) {
            case BOSS -> generateBossBounty(rarity, currentTime);
            case HORDE -> generateHordeBounty(rarity, currentTime);
            case ELITE -> generateEliteBounty(rarity, currentTime);
            default -> generateStandardBounty(rarity, currentTime);
        };
    }

    private static BountyRarity getRandomRarity() {
        int roll = RANDOM.nextInt(100);
        if (roll < 40) return BountyRarity.COMMON;
        if (roll < 70) return BountyRarity.UNCOMMON;
        if (roll < 88) return BountyRarity.RARE;
        if (roll < 97) return BountyRarity.EPIC;
        return BountyRarity.LEGENDARY;
    }

    private static BountyType getRandomType(BountyRarity rarity) {
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