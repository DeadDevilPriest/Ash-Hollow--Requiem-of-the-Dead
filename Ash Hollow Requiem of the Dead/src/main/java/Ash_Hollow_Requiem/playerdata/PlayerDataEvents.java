package Ash_Hollow_Requiem.playerdata;

import Ash_Hollow_Requiem.network.PacketHandler;
import Ash_Hollow_Requiem.network.SyncPlayerDataPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Event handlers for PlayerData capability
 * With improved error handling and null safety
 */
public class PlayerDataEvents {

    /**
     * MOD Bus events - runs during mod initialization
     */
    @Mod.EventBusSubscriber(modid = "ash_hollow_requiem_of_the_dead", bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModBusEvents {
        @SubscribeEvent
        public static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
            try {
                event.register(PlayerData.class);
                System.out.println("✅ PlayerData capability registered!");
            } catch (Exception e) {
                System.err.println("❌ Failed to register PlayerData capability: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * FORGE Bus events - runs during gameplay
     */
    @Mod.EventBusSubscriber(modid = "ash_hollow_requiem_of_the_dead")
    public static class ForgeBusEvents {

        @SubscribeEvent
        public static void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
            if (!(event.getObject() instanceof Player player)) {
                return;
            }

            try {
                // Check if capability already exists
                if (!player.getCapability(PlayerDataProvider.PLAYER_DATA).isPresent()) {
                    event.addCapability(
                            new ResourceLocation("ash_hollow_requiem_of_the_dead", "playerdata"),
                            new PlayerDataProvider()
                    );
                    System.out.println("✅ Attached PlayerData to player: " + player.getName().getString());
                }
            } catch (Exception e) {
                System.err.println("❌ Failed to attach PlayerData to player: " + e.getMessage());
                e.printStackTrace();
            }
        }

        @SubscribeEvent
        public static void onPlayerClone(PlayerEvent.Clone event) {
            try {
                Player oldPlayer = event.getOriginal();
                Player newPlayer = event.getEntity();

                if (oldPlayer == null || newPlayer == null) {
                    System.err.println("❌ Clone event has null player!");
                    return;
                }

                oldPlayer.reviveCaps();

                oldPlayer.getCapability(PlayerDataProvider.PLAYER_DATA).ifPresent(oldData -> {
                    newPlayer.getCapability(PlayerDataProvider.PLAYER_DATA).ifPresent(newData -> {
                        newData.copyFrom(oldData);
                        System.out.println("✅ Copied PlayerData from old to new player");
                    });
                });

                oldPlayer.invalidateCaps();

                if (event.isWasDeath() && newPlayer instanceof ServerPlayer serverPlayer) {
                    syncToClient(serverPlayer);
                }
            } catch (Exception e) {
                System.err.println("❌ Failed to clone PlayerData: " + e.getMessage());
                e.printStackTrace();
            }
        }

        @SubscribeEvent
        public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
            try {
                if (event.getEntity() instanceof ServerPlayer serverPlayer) {
                    // Delay sync to ensure everything is loaded
                    net.minecraft.server.MinecraftServer server = serverPlayer.getServer();
                    if (server != null) {
                        // Wait 20 ticks (1 second) before syncing in singleplayer
                        // This ensures the client is fully ready
                        final int delay = server.isDedicatedServer() ? 1 : 20;

                        server.execute(() -> {
                            // Schedule the sync
                            scheduleSync(server, serverPlayer, delay);
                        });
                    }
                }
            } catch (Exception e) {
                System.err.println("❌ Failed to sync PlayerData on login: " + e.getMessage());
                e.printStackTrace();
            }
        }

        private static void scheduleSync(net.minecraft.server.MinecraftServer server, ServerPlayer player, int tickDelay) {
            if (tickDelay <= 0) {
                syncToClient(player);
                System.out.println("✅ Synced PlayerData to client for: " + player.getName().getString());
            } else {
                server.execute(() -> scheduleSync(server, player, tickDelay - 1));
            }
        }

        @SubscribeEvent
        public static void onPlayerChangeDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
            try {
                if (event.getEntity() instanceof ServerPlayer serverPlayer) {
                    syncToClient(serverPlayer);
                }
            } catch (Exception e) {
                System.err.println("❌ Failed to sync PlayerData on dimension change: " + e.getMessage());
            }
        }

        @SubscribeEvent
        public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
            try {
                if (event.getEntity() instanceof ServerPlayer serverPlayer) {
                    syncToClient(serverPlayer);
                }
            } catch (Exception e) {
                System.err.println("❌ Failed to sync PlayerData on respawn: " + e.getMessage());
            }
        }

        /**
         * Sync player data to client using PacketHandler
         * With null safety and error handling
         */
        private static void syncToClient(ServerPlayer player) {
            if (player == null) {
                System.err.println("❌ Cannot sync to null player!");
                return;
            }

            try {
                player.getCapability(PlayerDataProvider.PLAYER_DATA).ifPresent(
                        data -> {
                            try {
                                CompoundTag nbt = new CompoundTag();
                                data.saveNBTData(nbt);
                                PacketHandler.sendToPlayer(new SyncPlayerDataPacket(nbt), player);
                            } catch (Exception e) {
                                System.err.println("❌ Failed to create sync packet: " + e.getMessage());
                            }
                        }
                );
            } catch (Exception e) {
                System.err.println("❌ Failed to sync PlayerData: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}