package Ash_Hollow_Requiem.skilltributes;

import java.util.ArrayList;
import java.util.List;

/**
 * Skills System - Unlockable abilities
 * Inspired by Hunt: Showdown 1896
 *
 * Skills are purchased with skill points and provide passive/active bonuses
 * Each skill has prerequisites (attribute levels, other skills, etc.)
 */
public class Skills {

    public Skills() {
    }

    /**
     * Base Skill class - All skills extend this
     * NOW SUPPORTS MULTIPLE LEVELS!
     */
    public static abstract class Skill {
        private final String id;
        private final String name;
        private final String description;
        private final int baseCost;
        private final int maxLevel;
        private final SkillTier tier;
        private final SkillType type;

        public Skill(String id, String name, String description, int baseCost, int maxLevel, SkillTier tier, SkillType type) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.baseCost = baseCost;
            this.maxLevel = maxLevel;
            this.tier = tier;
            this.type = type;
        }

        public String getId() { return id; }
        public String getName() { return name; }
        public String getDescription() { return description; }
        public int getBaseCost() { return baseCost; }
        public int getMaxLevel() { return maxLevel; }
        public SkillTier getTier() { return tier; }
        public SkillType getType() { return type; }

        /**
         * Get cost for upgrading to the next level
         * Uses a FLAT cost per level to keep it affordable
         *
         * Formula: baseCost (same for all levels)
         * This prevents exponential cost explosion
         */
        public int getCostForLevel(int currentLevel) {
            if (currentLevel >= maxLevel) return 0;
            return baseCost; // FLAT COST - same for every level
        }

        /**
         * Get TOTAL cost to reach a specific level from 0
         */
        public int getTotalCostToLevel(int targetLevel) {
            return baseCost * Math.min(targetLevel, maxLevel);
        }

        /**
         * Check if player meets requirements to unlock this skill at level 1
         */
        public abstract boolean meetsRequirements(Attributes attributes);

        /**
         * Check if player meets requirements for a specific level
         */
        public abstract boolean meetsRequirementsForLevel(Attributes attributes, int level);

        /**
         * Get the effect value at a specific level
         */
        public abstract float getEffectValue(int level);

        /**
         * Get description with level information
         */
        public String getDescriptionForLevel(int level) {
            return description + " (Level " + level + "/" + maxLevel + ")";
        }
    }

    /**
     * Get all skill names for display
     */
    public static String[] getAllSkillNames() {
        return SkillRegistry.getAllSkills().stream()
                .map(Skill::getName)
                .toArray(String[]::new);
    }

    /**
     * Get all skills
     */
    public static List<Skill> getAllSkills() {
        return new ArrayList<>(SkillRegistry.getAllSkills());
    }

    /**
     * Get a skill by its ID
     */
    public static Skill getSkillById(String id) {
        return SkillRegistry.getSkill(id);
    }

    /**
     * Get skills by type
     */
    public static List<Skill> getSkillsByType(SkillType type) {
        return SkillRegistry.getSkillsByType(type);
    }

    /**
     * Get skills by tier
     */
    public static List<Skill> getSkillsByTier(SkillTier tier) {
        return SkillRegistry.getSkillsByTier(tier);
    }

    // ========== SKILL TIERS ========== //

    public enum SkillTier {
        BASIC(1, 0xFFFFFF),      // White - Easy to unlock
        ADVANCED(2, 0x00FF00),   // Green - Moderate requirements
        EXPERT(3, 0x0099FF),     // Blue - High requirements
        MASTER(4, 0xFF00FF);     // Purple - Max level requirements

        private final int level;
        private final int color;

        SkillTier(int level, int color) {
            this.level = level;
            this.color = color;
        }

        public int getLevel() { return level; }
        public int getColor() { return color; }
    }

    // ========== SKILL TYPES ========== //

    public enum SkillType {
        COMBAT,      // Combat bonuses
        SURVIVAL,    // Health, stamina, resistance
        UTILITY,     // Crafting, looting, detection
        MOVEMENT,    // Speed, jumping, climbing
        MENTAL       // Willpower, intelligence bonuses
    }

    // ========== EXAMPLE SKILLS ========== //

    /**
     * THICK SKIN - Reduces bleeding damage AND duration
     * Hunt: Showdown inspired
     *
     * Level 1: -15% damage, -8% duration per stack
     * Level 2: -30% damage, -16% duration per stack
     * Level 3: -45% damage, -24% duration per stack
     * Level 4: -60% damage, -32% duration per stack
     * Level 5: -75% damage, -40% duration per stack
     *
     * FLAT COST: 3 points per level (15 points total to max)
     */
    public static class ThickSkin extends Skill {

        public ThickSkin() {
            super(
                    "thick_skin",
                    "Thick Skin",
                    "Your skin is toughened from countless battles. Reduces bleeding damage and duration per stack.",
                    3, // FLAT cost: 3 skill points per level
                    5, // Max level: 5
                    SkillTier.BASIC,
                    SkillType.SURVIVAL
            );
        }

        @Override
        public boolean meetsRequirements(Attributes attributes) {
            return attributes.getConstitution() >= 5;
        }

        @Override
        public boolean meetsRequirementsForLevel(Attributes attributes, int level) {
            // Higher levels require more Constitution
            int requiredConstitution = 5 + (level - 1) * 3; // Level 1=5, 2=8, 3=11, 4=14, 5=17
            return attributes.getConstitution() >= requiredConstitution;
        }

        @Override
        public float getEffectValue(int level) {
            // Returns damage reduction percentage
            return 0.15f * level; // 15% per level
        }

        /**
         * Get bleeding damage reduction at this level
         */
        public float getDamageReduction(int level) {
            return 0.15f * level; // 15% per level, max 75% at level 5
        }

        /**
         * Get bleeding duration reduction per stack at this level
         */
        public float getDurationReductionPerStack(int level) {
            return 0.08f * level; // 8% per level, max 40% at level 5
        }

        @Override
        public String getDescriptionForLevel(int level) {
            int damageReduction = (int)(getDamageReduction(level) * 100);
            int durationReduction = (int)(getDurationReductionPerStack(level) * 100);
            return String.format("Reduces bleeding damage by %d%% and duration by %d%% per stack. Cost: %d pts (Level %d/%d)",
                    damageReduction, durationReduction, getBaseCost(), level, getMaxLevel());
        }
    }

    /**
     * IRON WILL - Resist fear and negative effects
     */
    public static class IronWill extends Skill {
        public IronWill() {
            super(
                    "iron_will",
                    "Iron Will",
                    "Your mind is unshakeable. Resist fear and confusion effects.",
                    4, // FLAT cost: 4 points per level (12 total)
                    3, // Max level: 3
                    SkillTier.ADVANCED,
                    SkillType.MENTAL
            );
        }

        @Override
        public boolean meetsRequirements(Attributes attributes) {
            return attributes.getWillpower() >= 10;
        }

        @Override
        public boolean meetsRequirementsForLevel(Attributes attributes, int level) {
            return attributes.getWillpower() >= 10 + (level - 1) * 5;
        }

        @Override
        public float getEffectValue(int level) {
            return 0.25f * level; // 25% per level, max 75% at level 3
        }

        @Override
        public String getDescriptionForLevel(int level) {
            int reduction = (int)(getEffectValue(level) * 100);
            return String.format("Resist negative mental effects %d%% longer. Cost: %d pts (Level %d/%d)",
                    reduction, getBaseCost(), level, getMaxLevel());
        }
    }

    /**
     * LIGHTFOOT - Move silently and faster
     */
    public static class Lightfoot extends Skill {
        public Lightfoot() {
            super(
                    "lightfoot",
                    "Lightfoot",
                    "Move faster and make less noise while crouching.",
                    3, // FLAT cost: 3 points per level (9 total)
                    3, // Max level: 3
                    SkillTier.BASIC,
                    SkillType.MOVEMENT
            );
        }

        @Override
        public boolean meetsRequirements(Attributes attributes) {
            return attributes.getDexterity() >= 7;
        }

        @Override
        public boolean meetsRequirementsForLevel(Attributes attributes, int level) {
            return attributes.getDexterity() >= 7 + (level - 1) * 3;
        }

        @Override
        public float getEffectValue(int level) {
            return 0.05f * level + 0.05f; // 10%, 15%, 20% speed boost
        }

        @Override
        public String getDescriptionForLevel(int level) {
            int speedBoost = (int)((0.05f * level + 0.05f) * 100);
            int noiseReduction = 25 + (level * 25); // 50%, 75%, 100%
            return String.format("+%d%% speed, -%d%% noise while crouching. Cost: %d pts (Level %d/%d)",
                    speedBoost, noiseReduction, getBaseCost(), level, getMaxLevel());
        }
    }

    /**
     * PHYSICIAN - Improved healing
     */
    public static class Physician extends Skill {
        public Physician() {
            super(
                    "physician",
                    "Physician",
                    "Healing items restore more health and remove bleeding stacks.",
                    4, // FLAT cost: 4 points per level (12 total)
                    3, // Max level: 3
                    SkillTier.ADVANCED,
                    SkillType.SURVIVAL
            );
        }

        @Override
        public boolean meetsRequirements(Attributes attributes) {
            return attributes.getIntelligence() >= 8 &&
                    attributes.getConstitution() >= 6;
        }

        @Override
        public boolean meetsRequirementsForLevel(Attributes attributes, int level) {
            return attributes.getIntelligence() >= 8 + (level - 1) * 4 &&
                    attributes.getConstitution() >= 6 + (level - 1) * 3;
        }

        @Override
        public float getEffectValue(int level) {
            return 0.25f * level; // 25%, 50%, 75% healing boost
        }

        @Override
        public String getDescriptionForLevel(int level) {
            int healingBoost = (int)(getEffectValue(level) * 100);
            return String.format("+%d%% healing, remove %d bleeding stack(s). Cost: %d pts (Level %d/%d)",
                    healingBoost, level, getBaseCost(), level, getMaxLevel());
        }
    }

    /**
     * STEADY HANDS - No weapon sway, better accuracy
     */
    public static class SteadyHands extends Skill {
        public SteadyHands() {
            super(
                    "steady_hands",
                    "Steady Hands",
                    "Weapon sway reduced. Ranged weapons are more accurate.",
                    5, // FLAT cost: 5 points per level (15 total)
                    3, // Max level: 3
                    SkillTier.EXPERT,
                    SkillType.COMBAT
            );
        }

        @Override
        public boolean meetsRequirements(Attributes attributes) {
            return attributes.getDexterity() >= 15 &&
                    attributes.getStrength() >= 10;
        }

        @Override
        public boolean meetsRequirementsForLevel(Attributes attributes, int level) {
            return attributes.getDexterity() >= 15 + (level - 1) * 5 &&
                    attributes.getStrength() >= 10 + (level - 1) * 3;
        }

        @Override
        public float getEffectValue(int level) {
            return 0.25f + (level * 0.125f); // 37.5%, 50%, 62.5% sway reduction
        }

        @Override
        public String getDescriptionForLevel(int level) {
            int swayReduction = (int)(getEffectValue(level) * 100);
            int accuracyBoost = 10 + (level * 5); // 15%, 20%, 25%
            return String.format("-%d%% weapon sway, +%d%% accuracy. Cost: %d pts (Level %d/%d)",
                    swayReduction, accuracyBoost, getBaseCost(), level, getMaxLevel());
        }
    }

    /**
     * BULLETPROOF - Massive damage resistance
     */
    public static class Bulletproof extends Skill {
        public Bulletproof() {
            super(
                    "bulletproof",
                    "Bulletproof",
                    "Take less damage from all sources. You've survived worse.",
                    6, // FLAT cost: 6 points per level (24 total)
                    4, // Max level: 4
                    SkillTier.MASTER,
                    SkillType.SURVIVAL
            );
        }

        @Override
        public boolean meetsRequirements(Attributes attributes) {
            return attributes.getConstitution() >= 25 &&
                    attributes.getWillpower() >= 20;
        }

        @Override
        public boolean meetsRequirementsForLevel(Attributes attributes, int level) {
            return attributes.getConstitution() >= 25 + (level - 1) * 5 &&
                    attributes.getWillpower() >= 20 + (level - 1) * 4;
        }

        @Override
        public float getEffectValue(int level) {
            return 0.1f + (level * 0.1f); // 20%, 30%, 40%, 50% damage reduction
        }

        @Override
        public String getDescriptionForLevel(int level) {
            int damageReduction = (int)(getEffectValue(level) * 100);
            return String.format("-%d%% damage from all sources. Cost: %d pts (Level %d/%d)",
                    damageReduction, getBaseCost(), level, getMaxLevel());
        }
    }

    /**
     * BLOODHOUND - Track wounded enemies
     */
    public static class Bloodhound extends Skill {
        public Bloodhound() {
            super(
                    "bloodhound",
                    "Bloodhound",
                    "Track wounded enemies through blood trails and enhanced vision.",
                    3, // FLAT cost: 3 points per level (9 total)
                    3, // Max level: 3
                    SkillTier.BASIC,
                    SkillType.UTILITY
            );
        }

        @Override
        public boolean meetsRequirements(Attributes attributes) {
            return attributes.getIntelligence() >= 6;
        }

        @Override
        public boolean meetsRequirementsForLevel(Attributes attributes, int level) {
            return attributes.getIntelligence() >= 6 + (level - 1) * 4;
        }

        @Override
        public float getEffectValue(int level) {
            return level; // Simple level-based progression
        }

        @Override
        public String getDescriptionForLevel(int level) {
            String effect = switch(level) {
                case 1 -> "See blood trails";
                case 2 -> "Bleeding enemies glow in Dark Vision";
                case 3 -> "See health bars of bleeding enemies";
                default -> "Unknown";
            };
            return String.format("%s. Cost: %d pts (Level %d/%d)",
                    effect, getBaseCost(), level, getMaxLevel());
        }
    }

    /**
     * AMBUSH - Deal bonus damage from stealth
     */
    public static class Ambush extends Skill {
        public Ambush() {
            super(
                    "ambush",
                    "Ambush",
                    "Deal bonus damage when attacking from behind or while crouching.",
                    4, // FLAT cost: 4 points per level (16 total)
                    4, // Max level: 4
                    SkillTier.ADVANCED,
                    SkillType.COMBAT
            );
        }

        @Override
        public boolean meetsRequirements(Attributes attributes) {
            return attributes.getDexterity() >= 12 &&
                    attributes.getStrength() >= 8;
        }

        @Override
        public boolean meetsRequirementsForLevel(Attributes attributes, int level) {
            return attributes.getDexterity() >= 12 + (level - 1) * 4 &&
                    attributes.getStrength() >= 8 + (level - 1) * 3;
        }

        @Override
        public float getEffectValue(int level) {
            return 0.25f * level; // 25%, 50%, 75%, 100% damage boost
        }

        @Override
        public String getDescriptionForLevel(int level) {
            int damageBoost = (int)(getEffectValue(level) * 100);
            return String.format("+%d%% damage from stealth/behind. Cost: %d pts (Level %d/%d)",
                    damageBoost, getBaseCost(), level, getMaxLevel());
        }
    }

    /**
     * NECROMANCER - Raise temporary undead allies
     */
    public static class Necromancer extends Skill {
        public Necromancer() {
            super(
                    "necromancer",
                    "Necromancer",
                    "Temporarily raise fallen enemies as undead allies. Costs tokens per use.",
                    8, // FLAT cost: 8 points per level (24 total)
                    3, // Max level: 3
                    SkillTier.MASTER,
                    SkillType.MENTAL
            );
        }

        @Override
        public boolean meetsRequirements(Attributes attributes) {
            return attributes.getIntelligence() >= 30 &&
                    attributes.getWillpower() >= 25;
        }

        @Override
        public boolean meetsRequirementsForLevel(Attributes attributes, int level) {
            return attributes.getIntelligence() >= 30 + (level - 1) * 5 &&
                    attributes.getWillpower() >= 25 + (level - 1) * 5;
        }

        @Override
        public float getEffectValue(int level) {
            return level; // Number of undead that can be raised
        }

        @Override
        public String getDescriptionForLevel(int level) {
            int duration = 30 + (level * 15); // 45s, 60s, 75s
            return String.format("Raise %d undead for %ds (5 tokens). Cost: %d pts (Level %d/%d)",
                    level, duration, getBaseCost(), level, getMaxLevel());
        }
    }

    /**
     * REGENERATION - Slow health recovery
     */
    public static class Regeneration extends Skill {
        public Regeneration() {
            super(
                    "regeneration",
                    "Regeneration",
                    "Slowly regenerate health over time.",
                    5, // FLAT cost: 5 points per level (25 total)
                    5, // Max level: 5
                    SkillTier.EXPERT,
                    SkillType.SURVIVAL
            );
        }

        @Override
        public boolean meetsRequirements(Attributes attributes) {
            return attributes.getConstitution() >= 18 &&
                    attributes.getWillpower() >= 15;
        }

        @Override
        public boolean meetsRequirementsForLevel(Attributes attributes, int level) {
            return attributes.getConstitution() >= 18 + (level - 1) * 4 &&
                    attributes.getWillpower() >= 15 + (level - 1) * 3;
        }

        @Override
        public float getEffectValue(int level) {
            // Return seconds between heals (inverted - lower is better)
            return 12f - (level * 2f); // 10s, 8s, 6s, 4s, 2s
        }

        @Override
        public String getDescriptionForLevel(int level) {
            int seconds = (int)getEffectValue(level);
            return String.format("Regenerate 1 HP every %ds. Cost: %d pts (Level %d/%d)",
                    seconds, getBaseCost(), level, getMaxLevel());
        }
    }
}