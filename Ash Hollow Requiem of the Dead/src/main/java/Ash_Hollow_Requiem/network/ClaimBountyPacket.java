package Ash_Hollow_Requiem.network;

import Ash_Hollow_Requiem.bounty.Bounty;
import Ash_Hollow_Requiem.bounty.BountyManager;
import Ash_Hollow_Requiem.playerdata.PlayerDataAPI;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import java.util.UUID;
import java.util.function.Supplier;

/**
 * Packet sent from client to server when player claims a completed bounty
 * ✅ INTEGRATED WITH PLAYER DATA SYSTEM
 */
public class ClaimBountyPacket {
    private final UUID bountyId;

    public ClaimBountyPacket(UUID bountyId) {
        this.bountyId = bountyId;
    }

    public ClaimBountyPacket(FriendlyByteBuf buf) {
        this.bountyId = buf.readUUID();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeUUID(bountyId);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                // Get the bounty before claiming it
                Bounty bounty = BountyManager.getPlayerBounties(player.getUUID())
                        .stream()
                        .filter(b -> b.getBountyId().equals(bountyId))
                        .findFirst()
                        .orElse(null);

                if (bounty == null) {
                    player.displayClientMessage(
                            net.minecraft.network.chat.Component.literal("Bounty not found!"),
                            false
                    );
                    return;
                }

                if (!bounty.isCompleted()) {
                    player.displayClientMessage(
                            net.minecraft.network.chat.Component.literal("Bounty not completed yet!"),
                            false
                    );
                    return;
                }

                // Claim the bounty
                boolean success = BountyManager.claimBounty(player.getUUID(), bountyId);

                if (success) {
                    // ✅ AWARD COINS using PlayerDataAPI
                    int coins = bounty.getRewardCoins();
                    PlayerDataAPI.addCoins(player, coins);

                    // ✅ AWARD XP
                    int xp = bounty.getRewardExperience();
                    player.giveExperiencePoints(xp);

                    // ✅ AWARD SKILL POINTS based on rarity
                    int skillPoints = Math.max(1, bounty.getRarity().ordinal() / 2);
                    PlayerDataAPI.addSkillPoints(player, skillPoints);

                    // Send success message
                    player.displayClientMessage(
                            net.minecraft.network.chat.Component.literal(
                                    "✅ Bounty claimed! +" + coins + " coins, +" +
                                            xp + " XP, +" + skillPoints + " skill points"
                            ).withStyle(net.minecraft.ChatFormatting.GREEN),
                            false
                    );

                    // Play success sound
                    player.playSound(
                            net.minecraft.sounds.SoundEvents.PLAYER_LEVELUP,
                            1.0F,
                            1.2F
                    );
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}