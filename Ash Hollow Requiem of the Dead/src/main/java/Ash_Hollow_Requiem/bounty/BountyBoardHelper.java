package Ash_Hollow_Requiem.bounty;

import Ash_Hollow_Requiem.network.OpenBountyBoardPacket;
import Ash_Hollow_Requiem.network.PacketHandler;
import Ash_Hollow_Requiem.playerdata.PlayerDataAPI;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

/**
 * Helper class for opening the bounty board
 * Use this from blocks, items, or commands
 */
public class BountyBoardHelper {

    /**
     * Opens the bounty board for a player
     * Call this on the SERVER side only
     *
     * @param player The player to open the board for
     */
    public static void openBountyBoard(Player player) {
        if (player.level().isClientSide) {
            throw new IllegalStateException("openBountyBoard must be called on server side!");
        }

        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }

        // Get player's current balance
        int coins = PlayerDataAPI.getCoins(serverPlayer);
        int tokens = PlayerDataAPI.getTokens(serverPlayer);

        // Send packet to client to open the screen
        PacketHandler.sendToClient(
                new OpenBountyBoardPacket(coins, tokens),
                serverPlayer
        );
    }

    /**
     * Alternative method using the helper in OpenBountyBoardPacket
     */
    public static void openBountyBoardAlt(ServerPlayer player) {
        if (player.level().isClientSide) {
            throw new IllegalStateException("openBountyBoard must be called on server side!");
        }

        OpenBountyBoardPacket packet = OpenBountyBoardPacket.fromPlayer(player);
        PacketHandler.sendToClient(packet, player);
    }
}