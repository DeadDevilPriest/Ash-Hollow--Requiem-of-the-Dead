package Ash_Hollow_Requiem.network;

import Ash_Hollow_Requiem.playerdata.PlayerDataAPI;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Packet sent from client to server when purchasing items
 */
public class PurchaseItemPacket {
    private final String itemName;
    private final int cost;

    public PurchaseItemPacket(String itemName, int cost) {
        this.itemName = itemName;
        this.cost = cost;
    }

    public PurchaseItemPacket(FriendlyByteBuf buf) {
        this.itemName = buf.readUtf();
        this.cost = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeUtf(itemName);
        buf.writeInt(cost);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                // Check if player can afford it
                int currentCoins = PlayerDataAPI.getCoins(player);

                if (currentCoins >= cost) {
                    // Spend coins
                    if (PlayerDataAPI.spendCoins(player, cost)) {
                        // TODO: Give the actual item to the player
                        // For now, just confirm purchase

                        player.displayClientMessage(
                                Component.literal("✓ Purchased " + itemName + "!"),
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