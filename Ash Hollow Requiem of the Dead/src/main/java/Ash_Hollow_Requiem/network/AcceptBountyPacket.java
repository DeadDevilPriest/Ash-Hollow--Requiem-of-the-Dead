package Ash_Hollow_Requiem.network;

import Ash_Hollow_Requiem.bounty.*;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import java.util.function.Supplier;

public class AcceptBountyPacket {
    private final BountyRarity rarity;
    private final BountyType type;
    private final boolean isBoss;

    public AcceptBountyPacket(BountyRarity rarity, BountyType type, boolean isBoss) {
        this.rarity = rarity;
        this.type = type;
        this.isBoss = isBoss;
    }

    public AcceptBountyPacket(FriendlyByteBuf buf) {
        this.rarity = buf.readEnum(BountyRarity.class);
        this.type = buf.readEnum(BountyType.class);
        this.isBoss = buf.readBoolean();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeEnum(rarity);
        buf.writeEnum(type);
        buf.writeBoolean(isBoss);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                long currentTime = player.level().getGameTime();

                // Generate and assign the bounty
                Bounty bounty;
                if (isBoss) {
                    bounty = BountyGenerator.generateBossBounty(rarity, currentTime);
                } else {
                    bounty = BountyGenerator.generateStandardBounty(rarity, currentTime);
                }

                boolean success = BountyManager.assignBounty(player, bounty);

                if (success) {
                    player.displayClientMessage(
                            net.minecraft.network.chat.Component.literal("✅ Bounty accepted!"),
                            false
                    );
                } else {
                    player.displayClientMessage(
                            net.minecraft.network.chat.Component.literal("❌ You have too many active bounties!"),
                            false
                    );
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}