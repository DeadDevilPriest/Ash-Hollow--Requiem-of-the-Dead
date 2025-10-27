package Ash_Hollow_Requiem.network;

import Ash_Hollow_Requiem.playerdata.PlayerDataAPI;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PurchaseRebirthTokensPacket {
    private final int coinCost;
    private final int rebirthTokensGained;

    // Constructor for creating packet
    public PurchaseRebirthTokensPacket(int coinCost, int rebirthTokensGained) {
        this.coinCost = coinCost;
        this.rebirthTokensGained = rebirthTokensGained;
    }

    // ✅ ENCODER (you already have this)
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(coinCost);
        buf.writeInt(rebirthTokensGained);
    }

    // ✅ DECODER (ADD THIS - it was missing!)
    public PurchaseRebirthTokensPacket(FriendlyByteBuf buf) {
        this.coinCost = buf.readInt();
        this.rebirthTokensGained = buf.readInt();
    }

    // ✅ HANDLER (you already have this)
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                int currentCoins = PlayerDataAPI.getCoins(player);

                if (currentCoins >= coinCost) {
                    if (PlayerDataAPI.spendCoins(player, coinCost)) {
                        PlayerDataAPI.addRebirthTokens(player, rebirthTokensGained);

                        player.displayClientMessage(
                                Component.literal("✓ Exchanged " + coinCost + " coins for " +
                                        rebirthTokensGained + " rebirth tokens!"),
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