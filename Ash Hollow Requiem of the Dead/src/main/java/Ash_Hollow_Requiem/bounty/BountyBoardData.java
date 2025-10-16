package Ash_Hollow_Requiem.bounty;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;

import java.util.*;

/**
 * Persistent storage for the bounty board
 * Bounties only refresh when their timers expire
 */
public class BountyBoardData extends SavedData {
    private static final String DATA_NAME = "ash_hollow_bounty_board";

    // ✅ Match BountyBoardScreen categories
    public enum BountyCategory {
        BOUNTY("Bounty", ChatFormatting.GREEN),
        SPECIAL("Special", ChatFormatting.BLUE),
        HORDE("Horde", ChatFormatting.YELLOW),
        ELITE("Elite", ChatFormatting.GOLD),
        BOSS("Boss", ChatFormatting.DARK_RED),
        EVENT("Event", ChatFormatting.LIGHT_PURPLE);

        private final String displayName;
        private final ChatFormatting color;

        BountyCategory(String displayName, ChatFormatting color) {
            this.displayName = displayName;
            this.color = color;
        }

        public String getDisplayName() { return displayName; }
        public ChatFormatting getColor() { return color; }
    }

    // Each category has 8 persistent bounties
    private final Map<BountyCategory, List<Bounty>> categoryBounties = new HashMap<>();

    public BountyBoardData() {
        // Initialize empty lists for each category
        for (BountyCategory category : BountyCategory.values()) {
            categoryBounties.put(category, new ArrayList<>());
        }
    }

    /**
     * ✅ Get bounties for a category, auto-refreshing expired ones
     */
    public List<Bounty> getBountiesForCategory(BountyCategory category, long currentTime) {
        List<Bounty> bounties = categoryBounties.get(category);

        // Replace any expired bounties
        for (int i = 0; i < bounties.size(); i++) {
            if (bounties.get(i).hasExpired(currentTime)) {
                bounties.set(i, generateBountyForCategory(category, currentTime));
                setDirty(); // Mark for saving
            }
        }

        // Fill to 8 bounties if needed
        while (bounties.size() < 8) {
            bounties.add(generateBountyForCategory(category, currentTime));
            setDirty();
        }

        return new ArrayList<>(bounties); // Return copy
    }

    /**
     * ✅ Remove a bounty when player accepts it (replaces with new one)
     */
    public void removeBounty(BountyCategory category, UUID bountyId, long currentTime) {
        List<Bounty> bounties = categoryBounties.get(category);

        for (int i = 0; i < bounties.size(); i++) {
            if (bounties.get(i).getBountyId().equals(bountyId)) {
                // Replace with new bounty immediately
                bounties.set(i, generateBountyForCategory(category, currentTime));
                setDirty();
                return;
            }
        }
    }

    /**
     * ✅ Get a specific bounty by ID
     */
    public Bounty getBounty(UUID bountyId) {
        for (List<Bounty> bounties : categoryBounties.values()) {
            for (Bounty bounty : bounties) {
                if (bounty.getBountyId().equals(bountyId)) {
                    return bounty;
                }
            }
        }
        return null;
    }

    /**
     * ✅ Generate a bounty based on category
     */
    private Bounty generateBountyForCategory(BountyCategory category, long currentTime) {
        BountyRarity rarity = getRandomRarityForCategory(category);

        return switch(category) {
            case BOUNTY -> BountyGenerator.generateStandardBounty(rarity, currentTime);

            case SPECIAL -> BountyGenerator.generateStandardBounty(rarity, currentTime);

            case HORDE -> BountyGenerator.generateHordeBounty(rarity, currentTime);

            case ELITE -> BountyGenerator.generateEliteBounty(rarity, currentTime);

            case BOSS -> {
                // ✅ BOSS category: Only Epic+ with TOKEN COST
                BountyRarity bossRarity = getRandomBossRarity();
                yield BountyGenerator.generateBossBounty(bossRarity, currentTime);
            }

            case EVENT -> {
                // Event bounties can be any type with higher rarities
                Random random = new Random();
                int roll = random.nextInt(100);
                if (roll < 40) yield BountyGenerator.generateStandardBounty(rarity, currentTime);
                if (roll < 70) yield BountyGenerator.generateHordeBounty(rarity, currentTime);
                yield BountyGenerator.generateEliteBounty(rarity, currentTime);
            }
        };
    }

    /**
     * Get random rarity based on category
     */
    private BountyRarity getRandomRarityForCategory(BountyCategory category) {
        Random random = new Random();
        int roll = random.nextInt(100);

        return switch (category) {
            case BOSS, EVENT -> {
                if (roll < 30) yield BountyRarity.EPIC;
                if (roll < 70) yield BountyRarity.LEGENDARY;
                yield BountyRarity.EPIC;
            }
            case ELITE -> {
                if (roll < 40) yield BountyRarity.RARE;
                if (roll < 80) yield BountyRarity.EPIC;
                yield BountyRarity.LEGENDARY;
            }
            default -> {
                if (roll < 40) yield BountyRarity.COMMON;
                if (roll < 70) yield BountyRarity.UNCOMMON;
                if (roll < 90) yield BountyRarity.RARE;
                yield BountyRarity.EPIC;
            }
        };
    }

    /**
     * Get random boss rarity (Epic to Unknown) - ONLY FOR BOSS CATEGORY
     */
    private BountyRarity getRandomBossRarity() {
        Random random = new Random();
        int roll = random.nextInt(100);

        if (roll < 30) return BountyRarity.EPIC;
        if (roll < 55) return BountyRarity.LEGENDARY;
        if (roll < 75) return BountyRarity.ANCIENT;
        if (roll < 87) return BountyRarity.CURSED;
        if (roll < 93) return BountyRarity.EXPERIMENTAL;
        if (roll < 96) return BountyRarity.HOLLOW;
        if (roll < 98) return BountyRarity.GODLY;
        if (roll < 99) return BountyRarity.INSANE;
        return BountyRarity.UNKNOWN;
    }

    /**
     * ✅ Force refresh all bounties in a category (admin command)
     */
    public void forceRefreshCategory(BountyCategory category, long currentTime) {
        List<Bounty> newBounties = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            newBounties.add(generateBountyForCategory(category, currentTime));
        }
        categoryBounties.put(category, newBounties);
        setDirty();
    }

    // ============ NBT SAVE/LOAD ============

    @Override
    public CompoundTag save(CompoundTag tag) {
        for (BountyCategory category : BountyCategory.values()) {
            ListTag categoryList = new ListTag();

            for (Bounty bounty : categoryBounties.get(category)) {
                categoryList.add(saveBountyToNBT(bounty));
            }

            tag.put(category.name(), categoryList);
        }

        return tag;
    }

    /**
     * ✅ Save a single bounty to NBT
     */
    private CompoundTag saveBountyToNBT(Bounty bounty) {
        CompoundTag bountyTag = new CompoundTag();

        bountyTag.putUUID("BountyId", bounty.getBountyId());
        bountyTag.putString("Rarity", bounty.getRarity().name());
        bountyTag.putString("Type", bounty.getType().name());
        bountyTag.putInt("TotalKills", bounty.getTotalKillsRequired());
        bountyTag.putInt("CurrentKills", bounty.getCurrentKills());
        bountyTag.putLong("ExpirationTime", bounty.getExpirationTime());
        bountyTag.putInt("RewardCoins", bounty.getRewardCoins());
        bountyTag.putInt("RewardExp", bounty.getRewardExperience());
        bountyTag.putInt("TokenCost", bounty.getTokenCost()); // ✅ Save token cost
        bountyTag.putBoolean("Completed", bounty.isCompleted());
        bountyTag.putBoolean("Claimed", bounty.isClaimed());

        // Save targets
        ListTag targetsList = new ListTag();
        for (BountyTarget target : bounty.getTargets()) {
            CompoundTag targetTag = new CompoundTag();
            targetTag.putString("EntityType", target.getEntityType().getDescriptionId());
            targetTag.putInt("Count", target.getCount());
            targetTag.putInt("CurrentKills", target.getCurrentKills());
            targetTag.putBoolean("IsBoss", target.isBoss());
            targetsList.add(targetTag);
        }
        bountyTag.put("Targets", targetsList);

        return bountyTag;
    }

    /**
     * ✅ Load bounty board from NBT
     */
    public static BountyBoardData load(CompoundTag tag) {
        BountyBoardData data = new BountyBoardData();

        for (BountyCategory category : BountyCategory.values()) {
            if (tag.contains(category.name())) {
                ListTag categoryList = tag.getList(category.name(), Tag.TAG_COMPOUND);
                List<Bounty> bounties = new ArrayList<>();

                for (Tag t : categoryList) {
                    CompoundTag bountyTag = (CompoundTag) t;
                    Bounty bounty = loadBountyFromNBT(bountyTag);
                    if (bounty != null) {
                        bounties.add(bounty);
                    }
                }

                data.categoryBounties.put(category, bounties);
            }
        }

        return data;
    }

    /**
     * ✅ Load a single bounty from NBT (simplified - you'll need full EntityType deserialization)
     */
    private static Bounty loadBountyFromNBT(CompoundTag tag) {
        try {
            UUID bountyId = tag.getUUID("BountyId");
            BountyRarity rarity = BountyRarity.valueOf(tag.getString("Rarity"));
            BountyType type = BountyType.valueOf(tag.getString("Type"));
            int totalKills = tag.getInt("TotalKills");
            long expirationTime = tag.getLong("ExpirationTime");
            int rewardCoins = tag.getInt("RewardCoins");
            int rewardExp = tag.getInt("RewardExp");
            int tokenCost = tag.getInt("TokenCost");

            // Load targets (simplified)
            List<BountyTarget> targets = new ArrayList<>();
            // TODO: Implement full target deserialization with EntityType

            return new Bounty(
                    bountyId,
                    rarity,
                    type,
                    targets,
                    totalKills,
                    expirationTime,
                    rewardCoins,
                    rewardExp,
                    tokenCost
            );
        } catch (Exception e) {
            System.err.println("Failed to load bounty from NBT: " + e.getMessage());
            return null;
        }
    }

    /**
     * ✅ Get or create bounty board data
     */
    public static BountyBoardData get(MinecraftServer server) {
        DimensionDataStorage storage = server.overworld().getDataStorage();
        return storage.computeIfAbsent(
                BountyBoardData::load,
                BountyBoardData::new,
                DATA_NAME
        );
    }
}