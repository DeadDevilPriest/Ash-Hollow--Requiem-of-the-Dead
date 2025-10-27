package Ash_Hollow_Requiem.playerdata;

import Ash_Hollow_Requiem.network.PacketHandler;
import Ash_Hollow_Requiem.network.SyncPlayerDataPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

/**
 * Easy API for accessing player data
 * Now with null-safety checks
 */
public class PlayerDataAPI {

    // ========== COINS ========== //

    public static int getCoins(Player player) {
        if (player == null) return 0;
        return player.getCapability(PlayerDataProvider.PLAYER_DATA)
                .map(PlayerData::getCoins)
                .orElse(0);
    }

    public static void addCoins(Player player, int amount) {
        if (player == null) return;
        player.getCapability(PlayerDataProvider.PLAYER_DATA).ifPresent(data -> {
            data.addCoins(amount);
            syncToClient(player);
        });
    }

    public static boolean spendCoins(Player player, int amount) {
        if (player == null) return false;
        boolean[] success = {false};
        player.getCapability(PlayerDataProvider.PLAYER_DATA).ifPresent(data -> {
            success[0] = data.spendCoins(amount);
            if (success[0]) {
                syncToClient(player);
            }
        });
        return success[0];
    }

    public static void setCoins(Player player, int amount) {
        if (player == null) return;
        player.getCapability(PlayerDataProvider.PLAYER_DATA).ifPresent(data -> {
            data.setCoins(amount);
            syncToClient(player);
        });
    }

    // ========== TOKENS ========== //

    public static int getTokens(Player player) {
        if (player == null) return 0;
        return player.getCapability(PlayerDataProvider.PLAYER_DATA)
                .map(PlayerData::getTokens)
                .orElse(0);
    }

    public static void addTokens(Player player, int amount) {
        if (player == null) return;
        player.getCapability(PlayerDataProvider.PLAYER_DATA).ifPresent(data -> {
            data.addTokens(amount);
            syncToClient(player);
        });
    }

    public static boolean spendTokens(Player player, int amount) {
        if (player == null) return false;
        boolean[] success = {false};
        player.getCapability(PlayerDataProvider.PLAYER_DATA).ifPresent(data -> {
            success[0] = data.spendTokens(amount);
            if (success[0]) {
                syncToClient(player);
            }
        });
        return success[0];
    }

    public static void setTokens(Player player, int amount) {
        if (player == null) return;
        player.getCapability(PlayerDataProvider.PLAYER_DATA).ifPresent(data -> {
            data.setTokens(amount);
            syncToClient(player);
        });
    }

    // ========== REBIRTH TOKENS ========== //

    public static int getRebirthTokens(Player player) {
        if (player == null) return 0;
        return player.getCapability(PlayerDataProvider.PLAYER_DATA)
                .map(PlayerData::getRebirthTokens)
                .orElse(0);
    }

    public static void addRebirthTokens(Player player, int amount) {
        if (player == null) return;
        player.getCapability(PlayerDataProvider.PLAYER_DATA).ifPresent(data -> {
            data.addRebirthTokens(amount);
            syncToClient(player);
        });
    }

    public static boolean spendRebirthTokens(Player player, int amount) {
        if (player == null) return false;
        boolean[] success = {false};
        player.getCapability(PlayerDataProvider.PLAYER_DATA).ifPresent(data -> {
            success[0] = data.spendRebirthTokens(amount);
            if (success[0]) {
                syncToClient(player);
            }
        });
        return success[0];
    }

    public static void setRebirthTokens(Player player, int amount) {
        if (player == null) return;
        player.getCapability(PlayerDataProvider.PLAYER_DATA).ifPresent(data -> {
            data.setRebirthTokens(amount);
            syncToClient(player);
        });
    }

    // ========== SKILL POINTS ========== //

    public static int getSkillPoints(Player player) {
        if (player == null) return 0;
        return player.getCapability(PlayerDataProvider.PLAYER_DATA)
                .map(PlayerData::getSkillPoints)
                .orElse(0);
    }

    public static void addSkillPoints(Player player, int amount) {
        if (player == null) return;
        player.getCapability(PlayerDataProvider.PLAYER_DATA).ifPresent(data -> {
            data.addSkillPoints(amount);
            syncToClient(player);
        });
    }

    public static boolean spendSkillPoints(Player player, int amount) {
        if (player == null) return false;
        boolean[] success = {false};
        player.getCapability(PlayerDataProvider.PLAYER_DATA).ifPresent(data -> {
            success[0] = data.spendSkillPoints(amount);
            if (success[0]) {
                syncToClient(player);
            }
        });
        return success[0];
    }

    public static void setSkillPoints(Player player, int amount) {
        if (player == null) return;
        player.getCapability(PlayerDataProvider.PLAYER_DATA).ifPresent(data -> {
            data.setSkillPoints(amount);
            syncToClient(player);
        });
    }

    // ========== HELPER: SYNC TO CLIENT ========== //

    /**
     * Send updated data to the player's client
     * Uses PacketHandler - only works on server side
     */
    private static void syncToClient(Player player) {
        if (player == null) return;

        // Only sync from server to client
        if (!player.level().isClientSide && player instanceof ServerPlayer serverPlayer) {
            player.getCapability(PlayerDataProvider.PLAYER_DATA).ifPresent(data -> {
                try {
                    CompoundTag nbt = new CompoundTag();
                    data.saveNBTData(nbt);
                    PacketHandler.sendToPlayer(new SyncPlayerDataPacket(nbt), serverPlayer);
                } catch (Exception e) {
                    System.err.println("Failed to sync PlayerData: " + e.getMessage());
                }
            });
        }
    }
}