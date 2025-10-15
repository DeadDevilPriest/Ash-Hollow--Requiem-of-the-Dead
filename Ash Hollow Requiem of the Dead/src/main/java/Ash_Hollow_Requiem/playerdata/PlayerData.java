package Ash_Hollow_Requiem.playerdata;

import net.minecraft.nbt.CompoundTag;

/**
 * STEP 1: The Data Class
 *
 * This class stores all the custom data for a player.
 * Think of it as a container with:
 * - Variables to hold data (coins, tokens, etc.)
 * - Methods to modify data (addCoins, spendCoins, etc.)
 * - Methods to save/load from NBT
 */
public class PlayerData {

    // ========== THE DATA WE WANT TO STORE ========== //

    private int coins = 0;           // Player's coins (starts at 0)
    private int tokens = 0;          // Player's tokens (starts at 0)
    private int skillPoints = 0;     // Player's skill points (starts at 0)

    // We could add more later like:
    // private List<Bounty> activeBounties = new ArrayList<>();
    // private Set<String> unlockedSkills = new HashSet<>();


    // ========== GETTERS (Read the data) ========== //

    public int getCoins() {
        return coins;
    }

    public int getTokens() {
        return tokens;
    }

    public int getSkillPoints() {
        return skillPoints;
    }


    // ========== SETTERS (Change the data) ========== //

    public void setCoins(int coins) {
        this.coins = Math.max(0, coins);  // Can't have negative coins
    }

    public void setTokens(int tokens) {
        this.tokens = Math.max(0, tokens);
    }

    public void setSkillPoints(int skillPoints) {
        this.skillPoints = Math.max(0, skillPoints);
    }


    // ========== MODIFY METHODS (Add/Spend) ========== //

    /**
     * Add coins to the player
     */
    public void addCoins(int amount) {
        this.coins += amount;
    }

    /**
     * Try to spend coins. Returns true if successful.
     */
    public boolean spendCoins(int amount) {
        if (coins >= amount) {
            coins -= amount;
            return true;  // Success!
        }
        return false;  // Not enough coins
    }

    /**
     * Add tokens to the player
     */
    public void addTokens(int amount) {
        this.tokens += amount;
    }

    /**
     * Try to spend tokens. Returns true if successful.
     */
    public boolean spendTokens(int amount) {
        if (tokens >= amount) {
            tokens -= amount;
            return true;
        }
        return false;
    }

    /**
     * Add skill points to the player
     */
    public void addSkillPoints(int amount) {
        this.skillPoints += amount;
    }

    /**
     * Try to spend skill points. Returns true if successful.
     */
    public boolean spendSkillPoints(int amount) {
        if (skillPoints >= amount) {
            skillPoints -= amount;
            return true;
        }
        return false;
    }


    // ========== NBT SERIALIZATION (Save/Load) ========== //

    /**
     * SAVE: Convert our data into NBT format
     *
     * This is called when:
     * - Player logs out
     * - Player dies
     * - Server saves the world
     *
     * Think of it like: Converting your data into a file that can be saved
     */
    public void saveNBTData(CompoundTag nbt) {
        // CompoundTag is like a dictionary/map
        // We put our data in with a key (string) and value

        nbt.putInt("Coins", coins);              // Save coins with key "Coins"
        nbt.putInt("Tokens", tokens);            // Save tokens with key "Tokens"
        nbt.putInt("SkillPoints", skillPoints);  // Save skill points with key "SkillPoints"

        // Later you can add more complex data:
        // nbt.put("ActiveBounties", bountiesListTag);
        // nbt.put("UnlockedSkills", skillsListTag);
    }

    /**
     * LOAD: Read data from NBT format
     *
     * This is called when:
     * - Player logs in
     * - Player respawns
     * - Server loads the world
     *
     * Think of it like: Reading your data from a saved file
     */
    public void loadNBTData(CompoundTag nbt) {
        // Read the data using the same keys we used to save

        this.coins = nbt.getInt("Coins");              // Load coins
        this.tokens = nbt.getInt("Tokens");            // Load tokens
        this.skillPoints = nbt.getInt("SkillPoints");  // Load skill points

        // If the key doesn't exist, getInt returns 0 (perfect for new players!)
    }


    // ========== COPY METHOD (For respawning) ========== //

    /**
     * Copy data from another PlayerData instance
     *
     * This is used when a player dies:
     * - Old player entity is deleted
     * - New player entity is created
     * - We need to copy data from old to new
     */
    public void copyFrom(PlayerData other) {
        this.coins = other.coins;
        this.tokens = other.tokens;
        this.skillPoints = other.skillPoints;
    }
}