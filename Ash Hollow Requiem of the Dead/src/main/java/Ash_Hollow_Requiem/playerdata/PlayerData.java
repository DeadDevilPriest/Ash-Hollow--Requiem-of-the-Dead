package Ash_Hollow_Requiem.playerdata;

import Ash_Hollow_Requiem.skilltributes.Attributes;
import Ash_Hollow_Requiem.skilltributes.Skills;
import Ash_Hollow_Requiem.bounty.Bounty;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Stores all persistent player data including currency, attributes, and skills
 */
public class PlayerData {

    private int coins = 0;
    private int tokens = 0;
    private int skillPoints = 0;
    private int rebirthTokens = 0;

    // Attributes system (leveled by doing activities)
    private Attributes attributes = new Attributes();

    // Skills system (unlocked abilities)
    private Skills skills = new Skills();

    private List<Bounty> activeBounties = new ArrayList<>();
    private Set<String> unlockedSkills = new HashSet<>();

    // Skill levels: Map<SkillID, Level>
    private Map<String, Integer> skillLevels = new HashMap<>();

    // ========== GETTERS ========== //

    public int getCoins() {
        return coins;
    }

    public int getTokens() {
        return tokens;
    }

    public int getSkillPoints() {
        return skillPoints;
    }

    public Attributes getAttributes() {
        return attributes;
    }

    public Skills getSkills() {
        return skills;
    }

    public List<Bounty> getActiveBounties() {
        return activeBounties;
    }

    public Set<String> getUnlockedSkills() {
        return unlockedSkills;
    }

    /**
     * Get skill level (0 if not unlocked)
     */
    public int getSkillLevel(String skillId) {
        return skillLevels.getOrDefault(skillId, 0);
    }

    /**
     * Get all skill levels
     */
    public Map<String, Integer> getSkillLevels() {
        return skillLevels;
    }

    // And update these methods:
    public int getRebirthTokens() {  // ✅ Already correct
        return rebirthTokens;  // Change from rebirthtokens
    }


    // ========== SETTERS ========== //

    public void setCoins(int coins) {
        this.coins = Math.max(0, coins);
    }

    public void setTokens(int tokens) {
        this.tokens = Math.max(0, tokens);
    }

    public void setSkillPoints(int skillPoints) {
        this.skillPoints = Math.max(0, skillPoints);
    }

    public void setRebirthTokens(int rebirthTokens) {  // ✅ Already correct
        this.rebirthTokens = rebirthTokens;  // Change from rebirthtokens
    }

    // ========== MODIFY METHODS ========== //

    public void addCoins(int amount) {
        this.coins += amount;
    }

    public boolean spendCoins(int amount) {
        if (coins >= amount) {
            coins -= amount;
            return true;
        }
        return false;
    }

    public void addTokens(int amount) {
        this.tokens += amount;
    }

    public boolean spendTokens(int amount) {
        if (tokens >= amount) {
            tokens -= amount;
            return true;
        }
        return false;
    }

    public void addRebirthTokens(int amount) {
        this.rebirthTokens += amount;  // Change from rebirthtokens
    }

    public boolean spendRebirthTokens(int amount) {
        if (rebirthTokens >= amount) {  // Change from rebirthtokens
            rebirthTokens -= amount;  // Change from rebirthtokens
            return true;
        }
        return false;
    }

    public void addSkillPoints(int amount) {
        this.skillPoints += amount;
    }

    public boolean spendSkillPoints(int amount) {
        if (skillPoints >= amount) {
            skillPoints -= amount;
            return true;
        }
        return false;
    }

    /**
     * Unlock a skill by its registry name (sets to level 1)
     */
    public boolean unlockSkill(String skillName) {
        if (unlockedSkills.add(skillName)) {
            skillLevels.put(skillName, 1);
            return true;
        }
        return false;
    }

    /**
     * Check if a skill is unlocked (level > 0)
     */
    public boolean hasSkill(String skillName) {
        return skillLevels.getOrDefault(skillName, 0) > 0;
    }

    /**
     * Upgrade a skill to the next level
     * @return true if successfully upgraded
     */
    public boolean upgradeSkill(String skillName) {
        int currentLevel = skillLevels.getOrDefault(skillName, 0);
        if (currentLevel > 0) {
            skillLevels.put(skillName, currentLevel + 1);
            return true;
        }
        return false;
    }

    /**
     * Set skill to a specific level
     */
    public void setSkillLevel(String skillName, int level) {
        if (level > 0) {
            unlockedSkills.add(skillName);
            skillLevels.put(skillName, level);
        } else {
            unlockedSkills.remove(skillName);
            skillLevels.remove(skillName);
        }
    }

    // ========== NBT SERIALIZATION ========== //

    public void saveNBTData(CompoundTag nbt) {
        // Save currency
        nbt.putInt("Coins", coins);
        nbt.putInt("Tokens", tokens);
        nbt.putInt("RebirthTokens", rebirthTokens);
        nbt.putInt("SkillPoints", skillPoints);

        // Save attributes
        CompoundTag attributesTag = new CompoundTag();
        attributes.saveToNBT(attributesTag);
        nbt.put("Attributes", attributesTag);

        // Save unlocked skills
        ListTag skillsList = new ListTag();
        for (String skill : unlockedSkills) {
            skillsList.add(StringTag.valueOf(skill));
        }
        nbt.put("UnlockedSkills", skillsList);

        // Save skill levels
        CompoundTag skillLevelsTag = new CompoundTag();
        for (Map.Entry<String, Integer> entry : skillLevels.entrySet()) {
            skillLevelsTag.putInt(entry.getKey(), entry.getValue());
        }
        nbt.put("SkillLevels", skillLevelsTag);

        // TODO: Save activeBounties when Bounty implements NBT serialization
    }

    public void loadNBTData(CompoundTag nbt) {
        // Load currency
        this.coins = nbt.getInt("Coins");
        this.tokens = nbt.getInt("Tokens");
        this.skillPoints = nbt.getInt("SkillPoints");
        this.rebirthTokens= nbt.getInt("RebirthTokens");

        // Load attributes
        if (nbt.contains("Attributes")) {
            attributes.loadFromNBT(nbt.getCompound("Attributes"));
        }

        // Load unlocked skills
        if (nbt.contains("UnlockedSkills")) {
            ListTag skillsList = nbt.getList("UnlockedSkills", 8); // 8 = String type
            unlockedSkills.clear();
            for (int i = 0; i < skillsList.size(); i++) {
                unlockedSkills.add(skillsList.getString(i));
            }
        }

        // Load skill levels
        if (nbt.contains("SkillLevels")) {
            CompoundTag skillLevelsTag = nbt.getCompound("SkillLevels");
            skillLevels.clear();
            for (String key : skillLevelsTag.getAllKeys()) {
                skillLevels.put(key, skillLevelsTag.getInt(key));
            }
        }

        // TODO: Load activeBounties when implemented
    }

    // ========== COPY METHOD ========== //

    public void copyFrom(PlayerData other) {
        this.coins = other.coins;
        this.tokens = other.tokens;
        this.rebirthTokens = other.rebirthTokens;
        this.skillPoints = other.skillPoints;
        this.attributes = new Attributes();
        this.attributes.copyFrom(other.attributes);
        this.unlockedSkills = new HashSet<>(other.unlockedSkills);
        this.skillLevels = new HashMap<>(other.skillLevels);
        this.activeBounties = new ArrayList<>(other.activeBounties);
    }
}