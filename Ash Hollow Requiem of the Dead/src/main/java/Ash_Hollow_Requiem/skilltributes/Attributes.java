package Ash_Hollow_Requiem.skilltributes;

import net.minecraft.nbt.CompoundTag;

/**
 * Attributes System - Leveled by performing actions
 * Inspired by Minecraft + Hunt: Showdown 1896
 *
 * Attributes level up through gameplay:
 * - Strength: Dealing melee damage, mining, lifting heavy objects
 * - Constitution: Taking damage, healing, surviving
 * - Dexterity: Sprinting, dodging, precise actions
 * - Willpower: Using abilities, resisting effects, staying calm
 * - Intelligence: Crafting, discovering clues, solving puzzles
 */
public class Attributes {

    // Attribute levels (1-50)
    private int strength = 1;
    private int constitution = 1;
    private int dexterity = 1;
    private int willpower = 1;
    private int intelligence = 1;

    // Experience points for each attribute
    private float strengthXP = 0f;
    private float constitutionXP = 0f;
    private float dexterityXP = 0f;
    private float willpowerXP = 0f;
    private float intelligenceXP = 0f;

    // Constants
    private static final int MAX_LEVEL = 50;
    private static final int MIN_LEVEL = 1;
    private static final float BASE_XP = 100f;
    private static final float XP_MULTIPLIER = 1.15f; // 15% increase per level

    // ========== CONSTRUCTORS ========== //

    public Attributes() {
        // Default: All attributes start at level 1
    }

    public Attributes(int strength, int constitution, int dexterity, int willpower, int intelligence) {
        this.strength = clampLevel(strength);
        this.constitution = clampLevel(constitution);
        this.dexterity = clampLevel(dexterity);
        this.willpower = clampLevel(willpower);
        this.intelligence = clampLevel(intelligence);
    }

    // ========== GETTERS - LEVELS ========== //

    public int getStrength() {
        return strength;
    }

    public int getConstitution() {
        return constitution;
    }

    public int getDexterity() {
        return dexterity;
    }

    public int getWillpower() {
        return willpower;
    }

    public int getIntelligence() {
        return intelligence;
    }

    // ========== GETTERS - EXPERIENCE ========== //

    public float getStrengthXP() {
        return strengthXP;
    }

    public float getConstitutionXP() {
        return constitutionXP;
    }

    public float getDexterityXP() {
        return dexterityXP;
    }

    public float getWillpowerXP() {
        return willpowerXP;
    }

    public float getIntelligenceXP() {
        return intelligenceXP;
    }

    // ========== SETTERS ========== //

    public void setStrength(int strength) {
        this.strength = clampLevel(strength);
    }

    public void setConstitution(int constitution) {
        this.constitution = clampLevel(constitution);
    }

    public void setDexterity(int dexterity) {
        this.dexterity = clampLevel(dexterity);
    }

    public void setWillpower(int willpower) {
        this.willpower = clampLevel(willpower);
    }

    public void setIntelligence(int intelligence) {
        this.intelligence = clampLevel(intelligence);
    }

    // ========== XP GAIN METHODS ========== //

    /**
     * Add Strength XP (from melee combat, mining, carrying)
     * @return true if leveled up
     */
    public boolean addStrengthXP(float amount) {
        return addXP(AttributeType.STRENGTH, amount);
    }

    /**
     * Add Constitution XP (from taking damage, healing, surviving)
     * @return true if leveled up
     */
    public boolean addConstitutionXP(float amount) {
        return addXP(AttributeType.CONSTITUTION, amount);
    }

    /**
     * Add Dexterity XP (from sprinting, dodging, precise movements)
     * @return true if leveled up
     */
    public boolean addDexterityXP(float amount) {
        return addXP(AttributeType.DEXTERITY, amount);
    }

    /**
     * Add Willpower XP (from using abilities, resisting effects)
     * @return true if leveled up
     */
    public boolean addWillpowerXP(float amount) {
        return addXP(AttributeType.WILLPOWER, amount);
    }

    /**
     * Add Intelligence XP (from crafting, finding clues, solving puzzles)
     * @return true if leveled up
     */
    public boolean addIntelligenceXP(float amount) {
        return addXP(AttributeType.INTELLIGENCE, amount);
    }

    /**
     * Generic XP addition with automatic level-up
     * Uses EXPONENTIAL XP requirements
     */
    private boolean addXP(AttributeType type, float amount) {
        if (amount <= 0) return false;

        float currentXP;
        int currentLevel;

        switch (type) {
            case STRENGTH:
                currentXP = strengthXP;
                currentLevel = strength;
                break;
            case CONSTITUTION:
                currentXP = constitutionXP;
                currentLevel = constitution;
                break;
            case DEXTERITY:
                currentXP = dexterityXP;
                currentLevel = dexterity;
                break;
            case WILLPOWER:
                currentXP = willpowerXP;
                currentLevel = willpower;
                break;
            case INTELLIGENCE:
                currentXP = intelligenceXP;
                currentLevel = intelligence;
                break;
            default:
                return false;
        }

        // Can't gain XP at max level
        if (currentLevel >= MAX_LEVEL) {
            return false;
        }

        // Add XP
        currentXP += amount;
        boolean leveledUp = false;

        // Check for level up with EXPONENTIAL requirements
        while (currentLevel < MAX_LEVEL) {
            float requiredXP = getXPForLevel(currentLevel);

            if (currentXP >= requiredXP) {
                currentXP -= requiredXP;
                currentLevel++;
                leveledUp = true;
            } else {
                break;
            }
        }

        // Update the attribute
        switch (type) {
            case STRENGTH:
                strengthXP = currentXP;
                strength = currentLevel;
                break;
            case CONSTITUTION:
                constitutionXP = currentXP;
                constitution = currentLevel;
                break;
            case DEXTERITY:
                dexterityXP = currentXP;
                dexterity = currentLevel;
                break;
            case WILLPOWER:
                willpowerXP = currentXP;
                willpower = currentLevel;
                break;
            case INTELLIGENCE:
                intelligenceXP = currentXP;
                intelligence = currentLevel;
                break;
        }

        return leveledUp;
    }

    /**
     * Calculate EXPONENTIAL XP required for a given level
     * Formula: BASE_XP * (MULTIPLIER ^ (level - 1))
     *
     * Examples:
     * Level 1->2: 100 XP
     * Level 2->3: 115 XP
     * Level 5->6: 175 XP
     * Level 10->11: 405 XP
     * Level 20->21: 1,637 XP
     * Level 50: 133,387 XP (total for 1->50: ~7 million XP)
     */
    private float getXPForLevel(int level) {
        return BASE_XP * (float) Math.pow(XP_MULTIPLIER, level - 1);
    }

    /**
     * Get TOTAL XP required to reach a level from level 1
     */
    public float getTotalXPForLevel(int targetLevel) {
        float total = 0f;
        for (int i = 1; i < targetLevel; i++) {
            total += getXPForLevel(i);
        }
        return total;
    }

    // ========== UTILITY METHODS ========== //

    /**
     * Get total attribute points invested
     */
    public int getTotalAttributePoints() {
        return strength + constitution + dexterity + willpower + intelligence;
    }

    /**
     * Get progress to next level (0.0 - 1.0)
     */
    public float getProgressToNextLevel(AttributeType type) {
        float xp = 0f;
        int level = 1;

        switch (type) {
            case STRENGTH:
                xp = strengthXP;
                level = strength;
                break;
            case CONSTITUTION:
                xp = constitutionXP;
                level = constitution;
                break;
            case DEXTERITY:
                xp = dexterityXP;
                level = dexterity;
                break;
            case WILLPOWER:
                xp = willpowerXP;
                level = willpower;
                break;
            case INTELLIGENCE:
                xp = intelligenceXP;
                level = intelligence;
                break;
        }

        float requiredXP = getXPForLevel(level);
        return Math.min(1.0f, xp / requiredXP);
    }

    /**
     * Get XP required for current level to next level
     */
    public float getXPRequiredForNextLevel(AttributeType type) {
        int level = 1;
        switch (type) {
            case STRENGTH: level = strength; break;
            case CONSTITUTION: level = constitution; break;
            case DEXTERITY: level = dexterity; break;
            case WILLPOWER: level = willpower; break;
            case INTELLIGENCE: level = intelligence; break;
        }
        return getXPForLevel(level);
    }

    /**
     * Clamp level between MIN and MAX
     */
    private int clampLevel(int level) {
        return Math.max(MIN_LEVEL, Math.min(MAX_LEVEL, level));
    }

    /**
     * Reset all attributes to level 1
     */
    public void reset() {
        this.strength = 1;
        this.constitution = 1;
        this.dexterity = 1;
        this.willpower = 1;
        this.intelligence = 1;
        this.strengthXP = 0f;
        this.constitutionXP = 0f;
        this.dexterityXP = 0f;
        this.willpowerXP = 0f;
        this.intelligenceXP = 0f;
    }

    // ========== NBT SERIALIZATION ========== //

    public void saveToNBT(CompoundTag nbt) {
        // Save levels
        nbt.putInt("Strength", strength);
        nbt.putInt("Constitution", constitution);
        nbt.putInt("Dexterity", dexterity);
        nbt.putInt("Willpower", willpower);
        nbt.putInt("Intelligence", intelligence);

        // Save XP
        nbt.putFloat("StrengthXP", strengthXP);
        nbt.putFloat("ConstitutionXP", constitutionXP);
        nbt.putFloat("DexterityXP", dexterityXP);
        nbt.putFloat("WillpowerXP", willpowerXP);
        nbt.putFloat("IntelligenceXP", intelligenceXP);
    }

    public void loadFromNBT(CompoundTag nbt) {
        // Load levels
        this.strength = nbt.getInt("Strength");
        this.constitution = nbt.getInt("Constitution");
        this.dexterity = nbt.getInt("Dexterity");
        this.willpower = nbt.getInt("Willpower");
        this.intelligence = nbt.getInt("Intelligence");

        // Load XP
        this.strengthXP = nbt.getFloat("StrengthXP");
        this.constitutionXP = nbt.getFloat("ConstitutionXP");
        this.dexterityXP = nbt.getFloat("DexterityXP");
        this.willpowerXP = nbt.getFloat("WillpowerXP");
        this.intelligenceXP = nbt.getFloat("IntelligenceXP");
    }

    public void copyFrom(Attributes other) {
        this.strength = other.strength;
        this.constitution = other.constitution;
        this.dexterity = other.dexterity;
        this.willpower = other.willpower;
        this.intelligence = other.intelligence;
        this.strengthXP = other.strengthXP;
        this.constitutionXP = other.constitutionXP;
        this.dexterityXP = other.dexterityXP;
        this.willpowerXP = other.willpowerXP;
        this.intelligenceXP = other.intelligenceXP;
    }

    // ========== ENUM ========== //

    public enum AttributeType {
        STRENGTH,
        CONSTITUTION,
        DEXTERITY,
        WILLPOWER,
        INTELLIGENCE
    }
}