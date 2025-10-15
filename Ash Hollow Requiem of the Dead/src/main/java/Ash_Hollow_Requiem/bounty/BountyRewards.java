package Ash_Hollow_Requiem.bounty;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Defines rewards for different bounty rarities
 * Each rarity has progressively better rewards
 */
public class BountyRewards {
    private static final Random RANDOM = new Random();

    /**
     * Extended rarity system with 10 tiers
     * NOTE: Tokens are ONLY purchasable, never earned from bounties
     */
    public enum ExtendedRarity {
        COMMON("Common", 0x9E9E9E, 50, 10, 0),
        UNCOMMON("Uncommon", 0x4CAF50, 100, 20, 0),
        RARE("Rare", 0x2196F3, 250, 50, 5),
        ANCIENT("Ancient", 0x9C27B0, 500, 100, 10),
        CURSED("Cursed", 0x6A1B9A, 750, 150, 15),
        EXPERIMENTAL("Experimental", 0x00BCD4, 1000, 200, 20),
        HOLLOW("Hollow", 0x006064, 1500, 300, 30),
        GODLY("Godly", 0xFFD700, 2500, 500, 50),
        INSANE("Insane", 0xFF0000, 5000, 1000, 100),
        MYSTERY("???", 0x000000, 10000, 2000, 200);

        private final String displayName;
        private final int color;
        private final int baseCoins;
        private final int baseExperience;
        private final int rareLootChance; // percentage

        ExtendedRarity(String displayName, int color, int baseCoins,
                       int baseExperience, int rareLootChance) {
            this.displayName = displayName;
            this.color = color;
            this.baseCoins = baseCoins;
            this.baseExperience = baseExperience;
            this.rareLootChance = rareLootChance;
        }

        public String getDisplayName() { return displayName; }
        public int getColor() { return color; }
        public int getBaseCoins() { return baseCoins; }
        public int getBaseExperience() { return baseExperience; }
        public int getRareLootChance() { return rareLootChance; }
    }

    /**
     * Complete reward package for a bounty
     * NOTE: Tokens are never included - they're premium currency only
     */
    public static class RewardPackage {
        private final int coins;
        private final int experience;
        private final List<ItemStack> items;
        private final List<String> specialRewards; // Custom mod items/effects

        public RewardPackage(int coins, int experience) {
            this.coins = coins;
            this.experience = experience;
            this.items = new ArrayList<>();
            this.specialRewards = new ArrayList<>();
        }

        public void addItem(ItemStack item) { items.add(item); }
        public void addSpecialReward(String reward) { specialRewards.add(reward); }

        public int getCoins() { return coins; }
        public int getExperience() { return experience; }
        public List<ItemStack> getItems() { return items; }
        public List<String> getSpecialRewards() { return specialRewards; }
    }

    /**
     * Generates a reward package based on rarity
     * Tokens are NEVER included - they can only be purchased
     */
    public static RewardPackage generateRewards(ExtendedRarity rarity, BountyCategory category) {
        // Base rewards with some randomness (±20%)
        int coinVariance = (int)(rarity.getBaseCoins() * 0.2);
        int coins = rarity.getBaseCoins() + RANDOM.nextInt(coinVariance * 2) - coinVariance;

        int exp = rarity.getBaseExperience();

        // Boss categories give more experience (since no token bonus)
        if (category == BountyCategory.BOSS) {
            exp *= 2.0; // Double XP for boss hunts
            coins *= 1.5; // 50% more coins too
        }

        // Elite categories give more experience
        if (category == BountyCategory.ELITE) {
            exp *= 1.5;
            coins *= 1.2;
        }

        // Horde categories give more coins
        if (category == BountyCategory.HORDE) {
            coins *= 1.5;
        }

        // Event categories get special treatment
        if (category == BountyCategory.EVENT) {
            coins *= 1.3;
            exp *= 1.3;
        }

        RewardPackage rewards = new RewardPackage(coins, exp);

        // Add items based on rarity
        addItemRewards(rewards, rarity, category);

        // Add special rewards for higher rarities
        addSpecialRewards(rewards, rarity, category);

        return rewards;
    }

    /**
     * Adds item rewards based on rarity
     */
    private static void addItemRewards(RewardPackage rewards, ExtendedRarity rarity, BountyCategory category) {
        switch (rarity) {
            case COMMON -> {
                // Basic materials
                if (RANDOM.nextInt(100) < 50) {
                    rewards.addItem(new ItemStack(Items.IRON_INGOT, RANDOM.nextInt(5) + 3));
                }
            }
            case UNCOMMON -> {
                // Better materials
                rewards.addItem(new ItemStack(Items.GOLD_INGOT, RANDOM.nextInt(3) + 2));
                if (RANDOM.nextInt(100) < 30) {
                    rewards.addItem(new ItemStack(Items.DIAMOND, RANDOM.nextInt(2) + 1));
                }
            }
            case RARE -> {
                // Rare materials + possible enchanted item
                rewards.addItem(new ItemStack(Items.DIAMOND, RANDOM.nextInt(3) + 2));
                if (RANDOM.nextInt(100) < 40) {
                    rewards.addItem(new ItemStack(Items.EMERALD, RANDOM.nextInt(3) + 1));
                }
                if (RANDOM.nextInt(100) < 25) {
                    rewards.addItem(createEnchantedBook(1));
                }
            }
            case ANCIENT -> {
                // Ancient materials
                rewards.addItem(new ItemStack(Items.NETHERITE_SCRAP, RANDOM.nextInt(2) + 1));
                rewards.addItem(new ItemStack(Items.EMERALD, RANDOM.nextInt(5) + 3));
                if (RANDOM.nextInt(100) < 50) {
                    rewards.addItem(createEnchantedBook(2));
                }
                rewards.addSpecialReward("Ancient Fragment");
            }
            case CURSED -> {
                // Cursed rewards (powerful but with risk)
                rewards.addItem(new ItemStack(Items.NETHERITE_INGOT, RANDOM.nextInt(2) + 1));
                rewards.addItem(createEnchantedBook(2));
                rewards.addSpecialReward("Cursed Relic");
                rewards.addSpecialReward("Shadow Essence");
            }
            case EXPERIMENTAL -> {
                // Experimental tech items
                rewards.addItem(new ItemStack(Items.NETHERITE_INGOT, RANDOM.nextInt(3) + 2));
                rewards.addItem(createEnchantedBook(3));
                rewards.addSpecialReward("Experimental Core");
                rewards.addSpecialReward("Quantum Shard");
            }
            case HOLLOW -> {
                // Hollow realm items
                rewards.addItem(new ItemStack(Items.NETHERITE_INGOT, RANDOM.nextInt(4) + 3));
                rewards.addItem(new ItemStack(Items.NETHER_STAR, 1));
                rewards.addItem(createEnchantedBook(4));
                rewards.addSpecialReward("Hollow Crystal");
                rewards.addSpecialReward("Void Essence");
            }
            case GODLY -> {
                // Divine rewards
                rewards.addItem(new ItemStack(Items.NETHERITE_INGOT, RANDOM.nextInt(5) + 5));
                rewards.addItem(new ItemStack(Items.NETHER_STAR, RANDOM.nextInt(2) + 1));
                rewards.addItem(new ItemStack(Items.DRAGON_BREATH, RANDOM.nextInt(10) + 5));
                rewards.addItem(createEnchantedBook(5));
                rewards.addSpecialReward("Divine Fragment");
                rewards.addSpecialReward("Celestial Core");
                rewards.addSpecialReward("God Shard");
            }
            case INSANE -> {
                // Insane tier rewards
                rewards.addItem(new ItemStack(Items.NETHERITE_INGOT, RANDOM.nextInt(8) + 8));
                rewards.addItem(new ItemStack(Items.NETHER_STAR, RANDOM.nextInt(3) + 2));
                rewards.addItem(new ItemStack(Items.ELYTRA, 1));
                rewards.addItem(createEnchantedBook(6));
                rewards.addSpecialReward("Insanity Crystal");
                rewards.addSpecialReward("Chaos Essence");
                rewards.addSpecialReward("Reality Fragment");
            }
            case MYSTERY -> {
                // ??? tier - completely random but guaranteed epic
                rewards.addItem(new ItemStack(Items.NETHERITE_INGOT, RANDOM.nextInt(16) + 10));
                rewards.addItem(new ItemStack(Items.NETHER_STAR, RANDOM.nextInt(5) + 3));
                rewards.addItem(new ItemStack(Items.DRAGON_EGG, 1));
                rewards.addItem(createMaxEnchantedBook());
                rewards.addSpecialReward("??? Fragment");
                rewards.addSpecialReward("Unknown Artifact");
                rewards.addSpecialReward("Mysterious Power");
                rewards.addSpecialReward("Reality Warper");
            }
        }

        // Category-specific bonus items
        addCategoryBonusItems(rewards, category, rarity);
    }

    /**
     * Adds bonus items based on bounty category
     */
    private static void addCategoryBonusItems(RewardPackage rewards, BountyCategory category, ExtendedRarity rarity) {
        switch (category) {
            case BOSS -> {
                // Boss bounties give extra combat items
                if (rarity.ordinal() >= ExtendedRarity.RARE.ordinal()) {
                    rewards.addItem(new ItemStack(Items.TOTEM_OF_UNDYING, 1));
                }
            }
            case HORDE -> {
                // Horde bounties give extra arrows/potions
                rewards.addItem(new ItemStack(Items.ARROW, RANDOM.nextInt(64) + 32));
            }
            case ELITE -> {
                // Elite bounties give enchanted gear
                if (RANDOM.nextInt(100) < 30) {
                    rewards.addItem(createEnchantedGear(rarity));
                }
            }
            case EVENT -> {
                // Event bounties give unique seasonal items
                rewards.addSpecialReward("Event Token");
            }
        }
    }

    /**
     * Adds special rewards for high-tier bounties
     */
    private static void addSpecialRewards(RewardPackage rewards, ExtendedRarity rarity, BountyCategory category) {
        // Unlock special abilities
        if (rarity.ordinal() >= ExtendedRarity.GODLY.ordinal()) {
            rewards.addSpecialReward("Permanent +5% Damage Boost");
        }

        // Titles/cosmetics
        if (rarity == ExtendedRarity.INSANE) {
            rewards.addSpecialReward("Title: 'The Insane Hunter'");
        }

        if (rarity == ExtendedRarity.MYSTERY) {
            rewards.addSpecialReward("Title: '???'");
            rewards.addSpecialReward("Unlock: Mystery Shop Access");
        }
    }

    /**
     * Creates an enchanted book with random enchantments
     */
    private static ItemStack createEnchantedBook(int powerLevel) {
        ItemStack book = new ItemStack(Items.ENCHANTED_BOOK);

        // Add enchantments based on power level
        switch (powerLevel) {
            case 1 -> {
                EnchantmentHelper.setEnchantments(
                        Map.of(Enchantments.SHARPNESS, 1),
                        book
                );
            }
            case 2 -> {
                EnchantmentHelper.setEnchantments(
                        Map.of(Enchantments.SHARPNESS, 2),
                        book
                );
            }
            case 3 -> {
                EnchantmentHelper.setEnchantments(
                        Map.of(Enchantments.SHARPNESS, 3,
                                Enchantments.UNBREAKING, 2),
                        book
                );
            }
            case 4 -> {
                EnchantmentHelper.setEnchantments(
                        Map.of(Enchantments.SHARPNESS, 4,
                                Enchantments.UNBREAKING, 3),
                        book
                );
            }
            case 5 -> {
                EnchantmentHelper.setEnchantments(
                        Map.of(Enchantments.SHARPNESS, 5,
                                Enchantments.UNBREAKING, 3,
                                Enchantments.MENDING, 1),
                        book
                );
            }
            case 6 -> {
                EnchantmentHelper.setEnchantments(
                        Map.of(Enchantments.SHARPNESS, 5,
                                Enchantments.UNBREAKING, 3,
                                Enchantments.MENDING, 1,
                                Enchantments.MOB_LOOTING, 3),
                        book
                );
            }
        }

        return book;
    }

    /**
     * Creates max enchanted book for mystery tier
     */
    private static ItemStack createMaxEnchantedBook() {
        ItemStack book = new ItemStack(Items.ENCHANTED_BOOK);
        EnchantmentHelper.setEnchantments(
                Map.of(
                        Enchantments.SHARPNESS, 5,
                        Enchantments.UNBREAKING, 3,
                        Enchantments.MENDING, 1,
                        Enchantments.MOB_LOOTING, 3,
                        Enchantments.SWEEPING_EDGE, 3
                ),
                book
        );
        return book;
    }

    /**
     * Creates random enchanted gear
     */
    private static ItemStack createEnchantedGear(ExtendedRarity rarity) {
        Item[] gearTypes = {
                Items.DIAMOND_SWORD, Items.DIAMOND_HELMET,
                Items.DIAMOND_CHESTPLATE, Items.DIAMOND_LEGGINGS,
                Items.DIAMOND_BOOTS
        };

        Item gear = gearTypes[RANDOM.nextInt(gearTypes.length)];
        ItemStack item = new ItemStack(gear);

        int enchantLevel = Math.min(rarity.ordinal() + 1, 5);
        EnchantmentHelper.setEnchantments(
                Map.of(Enchantments.ALL_DAMAGE_PROTECTION, enchantLevel),
                item
        );

        return item;
    }

    /**
     * Gets reward summary as text
     */
    public static String getRewardSummary(RewardPackage rewards) {
        StringBuilder summary = new StringBuilder();
        summary.append("§6").append(rewards.getCoins()).append(" Coins\n");

        if (rewards.getExperience() > 0) {
            summary.append("§b").append(rewards.getExperience()).append(" XP\n");
        }

        if (!rewards.getItems().isEmpty()) {
            summary.append("§d").append(rewards.getItems().size()).append(" Items\n");
        }

        if (!rewards.getSpecialRewards().isEmpty()) {
            summary.append("§5Special: ").append(rewards.getSpecialRewards().get(0));
        }

        return summary.toString();
    }
}