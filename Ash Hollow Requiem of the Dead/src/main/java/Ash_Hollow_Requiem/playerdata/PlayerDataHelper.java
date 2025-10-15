package Ash_Hollow_Requiem.playerdata;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

/**
 * Helper class for accessing and modifying player data
 * Handles both client and server operations
 */
public class PlayerDataHelper {

    /**
     * Get player's data
     */
    public static PlayerData getData(Player player) {
        return player.getCapability(PlayerDataProvider.PLAYER_DATA)
                .orElse(new PlayerData());
    }

    // ========== Currency Methods ========== //

    public static int getCoins(Player player) {
        return getData(player).getCoins();
    }

    public static void addCoins(Player player, int amount) {
        player.getCapability(PlayerDataProvider.PLAYER_DATA).ifPresent(data -> {
            data.addCoins(amount);
            // TODO: Add sync to client when you implement networking
            // syncToClient(player);
        });
    }

    public static boolean spendCoins(Player player, int amount) {
        boolean[] success = {false};
        player.getCapability(PlayerDataProvider.PLAYER_DATA).ifPresent(data -> {
            success[0] = data.spendCoins(amount);
            // TODO: Add sync to client when you implement networking
            // if (success[0]) syncToClient(player);
        });
        return success[0];
    }

    public static int getTokens(Player player) {
        return getData(player).getTokens();
    }

    public static void addTokens(Player player, int amount) {
        player.getCapability(PlayerDataProvider.PLAYER_DATA).ifPresent(data -> {
            data.addTokens(amount);
            // TODO: Add sync to client
        });
    }

    public static boolean spendTokens(Player player, int amount) {
        boolean[] success = {false};
        player.getCapability(PlayerDataProvider.PLAYER_DATA).ifPresent(data -> {
            success[0] = data.spendTokens(amount);
            // TODO: Add sync to client
        });
        return success[0];
    }

    // ========== Skill Points Methods ========== //

    public static int getSkillPoints(Player player) {
        return getData(player).getSkillPoints();
    }

    public static void addSkillPoints(Player player, int amount) {
        player.getCapability(PlayerDataProvider.PLAYER_DATA).ifPresent(data -> {
            data.addSkillPoints(amount);
            // TODO: Add sync to client
        });
    }

    public static boolean spendSkillPoints(Player player, int amount) {
        boolean[] success = {false};
        player.getCapability(PlayerDataProvider.PLAYER_DATA).ifPresent(data -> {
            success[0] = data.spendSkillPoints(amount);
            // TODO: Add sync to client
        });
        return success[0];
    }

    // ========== Synchronization (To be implemented later) ========== //

    /**
     * TODO: Sync player data to client (requires networking setup)
     * For now, this is commented out until you implement PacketHandler
     */
    /*
    private static void syncToClient(Player player) {
        if (!player.level().isClientSide && player instanceof ServerPlayer serverPlayer) {
            player.getCapability(PlayerDataProvider.PLAYER_DATA).ifPresent(data -> {
                // Send packet to client with updated data
                PacketHandler.sendToPlayer(
                    new SyncPlayerDataPacket(data.saveToNBT()),
                    serverPlayer
                );
            });
        }
    }
    */
}