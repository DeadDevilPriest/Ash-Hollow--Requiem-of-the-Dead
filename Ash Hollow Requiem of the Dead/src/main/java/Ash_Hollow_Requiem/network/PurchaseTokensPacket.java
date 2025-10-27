package Ash_Hollow_Requiem.network;

import Ash_Hollow_Requiem.playerdata.PlayerDataAPI;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Packet sent from client to server when purchasing tokens
 */
public class PurchaseTokensPacket {
    private final int coinCost;
    private final int tokensGained;

    public PurchaseTokensPacket(int coinCost, int tokensGained) {
        this.coinCost = coinCost;
        this.tokensGained = tokensGained;
    }

    public PurchaseTokensPacket(FriendlyByteBuf buf) {
        this.coinCost = buf.readInt();
        this.tokensGained = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(coinCost);
        buf.writeInt(tokensGained);
    }

    /**
     * Tokens Gained logic
     */
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                // Check if player can afford it
                int currentCoins = PlayerDataAPI.getCoins(player);

                if (currentCoins >= coinCost) {
                    // Spend coins and give tokens
                    if (PlayerDataAPI.spendCoins(player, coinCost)) {
                        PlayerDataAPI.addTokens(player, tokensGained);

                        player.displayClientMessage(
                                Component.literal("✓ Exchanged " + coinCost + " coins for " +
                                        tokensGained + " tokens!"),
                                false
                        );
                    }
                } else {
                    player.displayClientMessage(
                            Component.literal("✗ Not enough coins!"),
                            false
                    );
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}