package Ash_Hollow_Requiem.network;

import Ash_Hollow_Requiem.Ash_Hollow;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

/**
 * Handles network packet registration and sending
 * This is your MAIN network handler - use this instead of ModNetwork
 */
public class PacketHandler {
    private static final String PROTOCOL_VERSION = "1";
    private static int packetId = 0;

    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(Ash_Hollow.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private static int id() {
        return packetId++;
    }

    /**
     * Register all network packets
     * Call this in your mod's constructor
     */
    public static void register() {
        // ========== PLAYER DATA SYNC (Server -> Client) ========== //
        INSTANCE.messageBuilder(SyncPlayerDataPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(SyncPlayerDataPacket::decode)
                .encoder(SyncPlayerDataPacket::encode)
                .consumerMainThread(SyncPlayerDataPacket::handle)
                .add();

        // ========== BOUNTY PACKETS ========== //

        // Open Bounty Board (Server -> Client)
        INSTANCE.messageBuilder(OpenBountyBoardPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .encoder(OpenBountyBoardPacket::toBytes)
                .decoder(OpenBountyBoardPacket::new)
                .consumerMainThread(OpenBountyBoardPacket::handle)
                .add();

        // Accept Bounty (Client -> Server)
        INSTANCE.messageBuilder(AcceptBountyPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .encoder(AcceptBountyPacket::toBytes)
                .decoder(AcceptBountyPacket::new)
                .consumerMainThread(AcceptBountyPacket::handle)
                .add();

        // Abandon Bounty (Client -> Server)
        INSTANCE.messageBuilder(AbandonBountyPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .encoder(AbandonBountyPacket::toBytes)
                .decoder(AbandonBountyPacket::new)
                .consumerMainThread(AbandonBountyPacket::handle)
                .add();

        // Claim Bounty (Client -> Server)
        INSTANCE.messageBuilder(ClaimBountyPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .encoder(ClaimBountyPacket::toBytes)
                .decoder(ClaimBountyPacket::new)
                .consumerMainThread(ClaimBountyPacket::handle)
                .add();

        // Purchase Tokens (Client -> Server)
        INSTANCE.messageBuilder(PurchaseTokensPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .encoder(PurchaseTokensPacket::toBytes)
                .decoder(PurchaseTokensPacket::new)
                .consumerMainThread(PurchaseTokensPacket::handle)
                .add();

        // Purchase Item (Client -> Server)
        INSTANCE.messageBuilder(PurchaseItemPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .encoder(PurchaseItemPacket::toBytes)
                .decoder(PurchaseItemPacket::new)
                .consumerMainThread(PurchaseItemPacket::handle)
                .add();

        System.out.println("✅ All network packets registered!");
    }

    // ========== HELPER METHODS ========== //

    public static <MSG> void sendToServer(MSG message) {
        INSTANCE.sendToServer(message);
    }

    public static <MSG> void sendToPlayer(MSG message, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
    }

    // Alias for sendToPlayer (for compatibility)
    public static <MSG> void sendToClient(MSG message, ServerPlayer player) {
        sendToPlayer(message, player);
    }

    public static <MSG> void sendToAllPlayers(MSG message) {
        INSTANCE.send(PacketDistributor.ALL.noArg(), message);
    }
}