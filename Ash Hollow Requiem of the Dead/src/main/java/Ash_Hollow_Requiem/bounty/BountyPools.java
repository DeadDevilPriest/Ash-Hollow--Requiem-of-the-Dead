package Ash_Hollow_Requiem.bounty;

import net.minecraft.world.entity.EntityType;
import java.util.*;

/**
 * Defines pools of entities for different bounty types
 * This is where you configure which mobs can appear in bounties
 */
public class BountyPools {

    // Standard hostile mobs (common enemies)
    public static final List<EntityType<?>> COMMON_HOSTILES = Arrays.asList(
            EntityType.ZOMBIE,
            EntityType.SKELETON,
            EntityType.SPIDER,
            EntityType.CREEPER,
            EntityType.CAVE_SPIDER
    );

    // Uncommon hostile mobs
    public static final List<EntityType<?>> UNCOMMON_HOSTILES = Arrays.asList(
            EntityType.HUSK,
            EntityType.STRAY,
            EntityType.DROWNED,
            EntityType.WITCH,
            EntityType.SLIME
    );

    // Rare hostile mobs
    public static final List<EntityType<?>> RARE_HOSTILES = Arrays.asList(
            EntityType.ENDERMAN,
            EntityType.BLAZE,
            EntityType.GHAST,
            EntityType.WITHER_SKELETON,
            EntityType.PIGLIN_BRUTE
    );

    // Boss mobs (single powerful enemies)
    public static final List<EntityType<?>> BOSS_MOBS = Arrays.asList(
            EntityType.WITHER,
            EntityType.ENDER_DRAGON,
            EntityType.WARDEN,
            EntityType.ELDER_GUARDIAN
            // Add your custom boss mobs here
    );

    // Elite mobs (tough enemies that spawn in groups)
    public static final List<EntityType<?>> ELITE_MOBS = Arrays.asList(
            EntityType.RAVAGER,
            EntityType.VINDICATOR,
            EntityType.EVOKER,
            EntityType.PIGLIN_BRUTE,
            EntityType.WITHER_SKELETON
    );

    // Nether-specific mobs
    public static final List<EntityType<?>> NETHER_MOBS = Arrays.asList(
            EntityType.BLAZE,
            EntityType.MAGMA_CUBE,
            EntityType.GHAST,
            EntityType.ZOMBIFIED_PIGLIN,
            EntityType.HOGLIN,
            EntityType.PIGLIN,
            EntityType.WITHER_SKELETON
    );

    // End-specific mobs
    public static final List<EntityType<?>> END_MOBS = Arrays.asList(
            EntityType.ENDERMAN,
            EntityType.ENDERMITE,
            EntityType.SHULKER,
            EntityType.ENDER_DRAGON
    );

    /**
     * Gets the appropriate mob pool based on rarity
     */
    public static List<EntityType<?>> getPoolForRarity(BountyRarity rarity) {
        return switch (rarity) {
            case COMMON -> COMMON_HOSTILES;
            case UNCOMMON -> UNCOMMON_HOSTILES;
            case RARE -> RARE_HOSTILES;
            case EPIC -> ELITE_MOBS;
            case LEGENDARY -> BOSS_MOBS;
            default -> COMMON_HOSTILES; // Default for other rarities
        };
    }

    /**
     * Gets the appropriate mob pool based on bounty type
     */
    public static List<EntityType<?>> getPoolForType(BountyType type) {
        return switch (type) {
            case BOSS -> BOSS_MOBS;
            case ELITE -> ELITE_MOBS;
            case HORDE -> COMMON_HOSTILES;
            case STANDARD -> getAllStandardMobs();
        };
    }

    /**
     * Combines all standard hostile mobs into one pool
     */
    private static List<EntityType<?>> getAllStandardMobs() {
        List<EntityType<?>> combined = new ArrayList<>();
        combined.addAll(COMMON_HOSTILES);
        combined.addAll(UNCOMMON_HOSTILES);
        combined.addAll(RARE_HOSTILES);
        return combined;
    }

    /**
     * Gets mobs based on dimension
     */
    public static List<EntityType<?>> getPoolForDimension(String dimension) {
        return switch (dimension.toLowerCase()) {
            case "nether", "the_nether" -> NETHER_MOBS;
            case "end", "the_end" -> END_MOBS;
            default -> getAllStandardMobs();
        };
    }
}