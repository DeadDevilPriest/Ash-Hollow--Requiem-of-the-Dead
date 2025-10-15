package Ash_Hollow_Requiem.network;

import Ash_Hollow_Requiem.bounty.BountyManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import java.util.UUID;
import java.util.function.Supplier;

public class AbandonBountyPacket {
    private final UUID bountyId;

    public AbandonBountyPacket(UUID bountyId) {
        this.bountyId = bountyId;
    }

    public AbandonBountyPacket(FriendlyByteBuf buf) {
        this.bountyId = buf.readUUID();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeUUID(bountyId);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                boolean success = BountyManager.abandonBounty(player.getUUID(), bountyId);

                if (success) {
                    player.displayClientMessage(
                            net.minecraft.network.chat.Component.literal("Bounty abandoned."),
                            false
                    );
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}