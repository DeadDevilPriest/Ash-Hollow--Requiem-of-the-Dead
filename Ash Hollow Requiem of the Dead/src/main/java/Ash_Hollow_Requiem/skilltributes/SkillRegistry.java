package Ash_Hollow_Requiem.skilltributes;

import java.util.*;

/**
 * Central registry for all available skills in the game
 * Manages skill unlocking, requirements checking, and lookups
 */
public class SkillRegistry {

    private static final Map<String, Skills.Skill> SKILLS = new HashMap<>();

    // Initialize all skills
    static {
        registerSkill(new Skills.ThickSkin());
        registerSkill(new Skills.IronWill());
        registerSkill(new Skills.Lightfoot());
        registerSkill(new Skills.Physician());
        registerSkill(new Skills.SteadyHands());
        registerSkill(new Skills.Bulletproof());
        registerSkill(new Skills.Bloodhound());
        registerSkill(new Skills.Ambush());
        registerSkill(new Skills.Necromancer());
        registerSkill(new Skills.Regeneration());
    }

    /**
     * Register a skill in the registry
     */
    private static void registerSkill(Skills.Skill skill) {
        SKILLS.put(skill.getId(), skill);
    }

    /**
     * Get a skill by its ID
     */
    public static Skills.Skill getSkill(String id) {
        return SKILLS.get(id);
    }

    /**
     * Get all registered skills
     */
    public static Collection<Skills.Skill> getAllSkills() {
        return SKILLS.values();
    }

    /**
     * Get skills by tier
     */
    public static List<Skills.Skill> getSkillsByTier(Skills.SkillTier tier) {
        List<Skills.Skill> result = new ArrayList<>();
        for (Skills.Skill skill : SKILLS.values()) {
            if (skill.getTier() == tier) {
                result.add(skill);
            }
        }
        return result;
    }

    /**
     * Get skills by type
     */
    public static List<Skills.Skill> getSkillsByType(Skills.SkillType type) {
        List<Skills.Skill> result = new ArrayList<>();
        for (Skills.Skill skill : SKILLS.values()) {
            if (skill.getType() == type) {
                result.add(skill);
            }
        }
        return result;
    }

    /**
     * Get all skills that a player can currently unlock or upgrade
     */
    public static List<SkillUpgradeOption> getAvailableSkills(Attributes attributes, Map<String, Integer> skillLevels, int skillPoints) {
        List<SkillUpgradeOption> available = new ArrayList<>();

        for (Skills.Skill skill : SKILLS.values()) {
            int currentLevel = skillLevels.getOrDefault(skill.getId(), 0);

            // Check if can unlock (level 0 -> 1)
            if (currentLevel == 0) {
                if (skill.meetsRequirements(attributes)) {
                    int cost = skill.getCostForLevel(0); // Cost for level 1
                    if (skillPoints >= cost) {
                        available.add(new SkillUpgradeOption(skill, 0, 1, cost));
                    }
                }
            }
            // Check if can upgrade (level X -> X+1)
            else if (currentLevel < skill.getMaxLevel()) {
                if (skill.meetsRequirementsForLevel(attributes, currentLevel + 1)) {
                    int cost = skill.getCostForLevel(currentLevel);
                    if (skillPoints >= cost) {
                        available.add(new SkillUpgradeOption(skill, currentLevel, currentLevel + 1, cost));
                    }
                }
            }
        }

        return available;
    }

    /**
     * Get all skills the player has unlocked with their levels
     */
    public static List<UnlockedSkillInfo> getUnlockedSkills(Map<String, Integer> skillLevels) {
        List<UnlockedSkillInfo> unlocked = new ArrayList<>();

        for (Map.Entry<String, Integer> entry : skillLevels.entrySet()) {
            Skills.Skill skill = SKILLS.get(entry.getKey());
            if (skill != null && entry.getValue() > 0) {
                unlocked.add(new UnlockedSkillInfo(skill, entry.getValue()));
            }
        }

        return unlocked;
    }

    /**
     * Check if a player can afford and unlock/upgrade a skill
     */
    public static boolean canUpgradeSkill(String skillId, Attributes attributes, Map<String, Integer> skillLevels, int skillPoints) {
        Skills.Skill skill = SKILLS.get(skillId);
        if (skill == null) return false;

        int currentLevel = skillLevels.getOrDefault(skillId, 0);

        // Already at max level
        if (currentLevel >= skill.getMaxLevel()) return false;

        // Check if unlocking (0 -> 1)
        if (currentLevel == 0) {
            if (!skill.meetsRequirements(attributes)) return false;
        } else {
            // Check if upgrading (X -> X+1)
            if (!skill.meetsRequirementsForLevel(attributes, currentLevel + 1)) return false;
        }

        // Not enough skill points
        int cost = skill.getCostForLevel(currentLevel);
        if (skillPoints < cost) return false;

        return true;
    }

    /**
     * Get the total cost of all skills at max level
     */
    public static int getTotalSkillCost() {
        int total = 0;
        for (Skills.Skill skill : SKILLS.values()) {
            // Sum cost for all levels
            for (int i = 0; i < skill.getMaxLevel(); i++) {
                total += skill.getCostForLevel(i);
            }
        }
        return total;
    }

    /**
     * Check if a player has a specific skill at a minimum level
     */
    public static boolean hasSkillAtLevel(String skillId, Map<String, Integer> skillLevels, int minLevel) {
        return skillLevels.getOrDefault(skillId, 0) >= minLevel;
    }

    // ========== HELPER CLASSES ========== //

    /**
     * Represents a skill that can be unlocked or upgraded
     */
    public static class SkillUpgradeOption {
        private final Skills.Skill skill;
        private final int currentLevel;
        private final int targetLevel;
        private final int cost;

        public SkillUpgradeOption(Skills.Skill skill, int currentLevel, int targetLevel, int cost) {
            this.skill = skill;
            this.currentLevel = currentLevel;
            this.targetLevel = targetLevel;
            this.cost = cost;
        }

        public Skills.Skill getSkill() { return skill; }
        public int getCurrentLevel() { return currentLevel; }
        public int getTargetLevel() { return targetLevel; }
        public int getCost() { return cost; }
        public boolean isUnlock() { return currentLevel == 0; }
        public boolean isUpgrade() { return currentLevel > 0; }
    }

    /**
     * Represents an unlocked skill with its current level
     */
    public static class UnlockedSkillInfo {
        private final Skills.Skill skill;
        private final int level;

        public UnlockedSkillInfo(Skills.Skill skill, int level) {
            this.skill = skill;
            this.level = level;
        }

        public Skills.Skill getSkill() { return skill; }
        public int getLevel() { return level; }
        public float getEffectValue() { return skill.getEffectValue(level); }
        public boolean isMaxLevel() { return level >= skill.getMaxLevel(); }
    }
}