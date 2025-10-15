package Ash_Hollow_Requiem.network;

import Ash_Hollow_Requiem.playerdata.PlayerDataProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Packet to sync PlayerData from server to client
 * With improved error handling
 */
public class SyncPlayerDataPacket {
    private final CompoundTag data;

    public SyncPlayerDataPacket(CompoundTag data) {
        this.data = data != null ? data : new CompoundTag();
    }

    /**
     * ENCODE: Write packet data to buffer
     */
    public static void encode(SyncPlayerDataPacket packet, FriendlyByteBuf buffer) {
        try {
            buffer.writeNbt(packet.data);
        } catch (Exception e) {
            System.err.println("❌ Failed to encode SyncPlayerDataPacket: " + e.getMessage());
            buffer.writeNbt(new CompoundTag()); // Send empty data as fallback
        }
    }

    /**
     * DECODE: Read packet data from buffer
     */
    public static SyncPlayerDataPacket decode(FriendlyByteBuf buffer) {
        try {
            CompoundTag data = buffer.readNbt();
            return new SyncPlayerDataPacket(data != null ? data : new CompoundTag());
        } catch (Exception e) {
            System.err.println("❌ Failed to decode SyncPlayerDataPacket: " + e.getMessage());
            return new SyncPlayerDataPacket(new CompoundTag());
        }
    }

    /**
     * HANDLE: Process the received packet on client
     */
    public static void handle(SyncPlayerDataPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            try {
                Minecraft mc = Minecraft.getInstance();

                if (mc == null) {
                    System.err.println("❌ Minecraft instance is null!");
                    return;
                }

                if (mc.player == null) {
                    System.err.println("❌ Client player is null!");
                    return;
                }

                mc.player.getCapability(PlayerDataProvider.PLAYER_DATA).ifPresent(
                        data -> {
                            try {
                                if (packet.data != null && !packet.data.isEmpty()) {
                                    data.loadNBTData(packet.data);
                                    System.out.println("✅ Client received and updated PlayerData!");
                                    System.out.println("   Coins: " + data.getCoins());
                                    System.out.println("   Tokens: " + data.getTokens());
                                    System.out.println("   Skill Points: " + data.getSkillPoints());
                                } else {
                                    System.out.println("⚠️ Received empty PlayerData packet");
                                }
                            } catch (Exception e) {
                                System.err.println("❌ Failed to load PlayerData from packet: " + e.getMessage());
                                e.printStackTrace();
                            }
                        }
                );
            } catch (Exception e) {
                System.err.println("❌ Failed to handle SyncPlayerDataPacket: " + e.getMessage());
                e.printStackTrace();
            }
        });

        ctx.get().setPacketHandled(true);
    }
}