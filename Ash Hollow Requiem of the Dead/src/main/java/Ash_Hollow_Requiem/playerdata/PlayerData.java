package Ash_Hollow_Requiem.playerdata;

import net.minecraft.nbt.CompoundTag;

public class PlayerData {
    private int coins;
    private int tokens;

    public int getCoins() {
        return coins;
    }

    public int getTokens() {
        return tokens;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }

    public void setTokens(int tokens) {
        this.tokens = tokens;
    }

    public void addCoins(int amount) {
        this.coins += amount;
    }

    public void addTokens(int amount) {
        this.tokens += amount;
    }

    public void saveNBTData(CompoundTag nbt) {
        nbt.putInt("Coins", coins);
        nbt.putInt("Tokens", tokens);
    }

    public void loadNBTData(CompoundTag nbt) {
        this.coins = nbt.getInt("Coins");
        this.tokens = nbt.getInt("Tokens");
    }
}
