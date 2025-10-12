package Ash_Hollow_Requiem.playerdata;

import net.minecraft.world.entity.player.Player;

public class PlayerDataAPI {

    public static int getPlayerCoins(Player player) {
        return player.getCapability(PlayerDataProvider.PLAYER_DATA)
                .map(PlayerData::getCoins)
                .orElse(0);
    }

    public static int getPlayerTokens(Player player) {
        return player.getCapability(PlayerDataProvider.PLAYER_DATA)
                .map(PlayerData::getTokens)
                .orElse(0);
    }

    public static void addPlayerCoins(Player player, int amount) {
        player.getCapability(PlayerDataProvider.PLAYER_DATA).ifPresent(data -> data.addCoins(amount));
    }

    public static void addPlayerTokens(Player player, int amount) {
        player.getCapability(PlayerDataProvider.PLAYER_DATA).ifPresent(data -> data.addTokens(amount));
    }

    public static void setPlayerCoins(Player player, int amount) {
        player.getCapability(PlayerDataProvider.PLAYER_DATA).ifPresent(data -> data.setCoins(amount));
    }

    public static void setPlayerTokens(Player player, int amount) {
        player.getCapability(PlayerDataProvider.PLAYER_DATA).ifPresent(data -> data.setTokens(amount));
    }
}
