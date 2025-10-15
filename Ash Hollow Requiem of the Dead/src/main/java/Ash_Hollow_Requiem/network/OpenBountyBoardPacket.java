package Ash_Hollow_Requiem.network;

import Ash_Hollow_Requiem.interfaces.BountyBoardScreen;
import Ash_Hollow_Requiem.playerdata.PlayerDataAPI;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Packet sent from server to client to open the bounty board
 * Sends player's current coin and token balance
 */
public class OpenBountyBoardPacket {
    private final int coins;
    private final int tokens;

    public OpenBountyBoardPacket(int coins, int tokens) {
        this.coins = coins;
        this.tokens = tokens;
    }

    public OpenBountyBoardPacket(FriendlyByteBuf buf) {
        this.coins = buf.readInt();
        this.tokens = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(coins);
        buf.writeInt(tokens);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> openClientScreen(coins, tokens));
        ctx.get().setPacketHandled(true);
    }

    @OnlyIn(Dist.CLIENT)
    private static void openClientScreen(int coins, int tokens) {
        Minecraft.getInstance().setScreen(new BountyBoardScreen(coins, tokens));
    }

    /**
     * Helper method to create packet from player data
     * Call this on the server side when opening the board
     */
    public static OpenBountyBoardPacket fromPlayer(net.minecraft.server.level.ServerPlayer player) {
        int coins = PlayerDataAPI.getCoins(player);
        int tokens = PlayerDataAPI.getTokens(player);
        return new OpenBountyBoardPacket(coins, tokens);
    }
}