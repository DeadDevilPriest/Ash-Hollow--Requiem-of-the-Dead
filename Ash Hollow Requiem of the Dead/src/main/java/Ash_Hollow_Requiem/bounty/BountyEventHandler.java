package Ash_Hollow_Requiem.bounty;

import Ash_Hollow_Requiem.playerdata.PlayerDataAPI;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

import static Ash_Hollow_Requiem.Ash_Hollow.MODID;

/**
 * Handles Forge events for the bounty system
 * ✅ INTEGRATED WITH PLAYER DATA SYSTEM
 */
@Mod.EventBusSubscriber(modid = MODID)
public class BountyEventHandler {

    private static long tickCounter = 0;
    private static final long CLEANUP_INTERVAL = 20 * 60; // Every minute

    /**
     * Called when an entity dies - checks if it counts towards any bounties
     * ✅ NOW AWARDS COINS AND SKILL POINTS AUTOMATICALLY
     */
    @SubscribeEvent
    public static void onEntityDeath(LivingDeathEvent event) {
        if (event.getEntity().level().isClientSide) return;

        if (!(event.getSource().getEntity() instanceof ServerPlayer killer)) return;

        LivingEntity killedEntity = event.getEntity();

        if (killedEntity instanceof ServerPlayer) return;

        // Register the kill with the bounty manager
        List<Bounty> completedBounties = BountyManager.registerKill(killer, killedEntity);

        // Auto-claim and reward for completed bounties
        for (Bounty bounty : completedBounties) {
            autoClaimBounty(killer, bounty);
        }
    }

    /**
     * Automatically claims and rewards a completed bounty
     * ✅ AWARDS COINS, XP, AND SKILL POINTS
     */
    private static void autoClaimBounty(ServerPlayer player, Bounty bounty) {
        // Get rewards
        int coins = bounty.getRewardCoins();
        int xp = bounty.getRewardExperience();
        int skillPoints = Math.max(1, bounty.getRarity().ordinal() / 2);

        // Award coins
        PlayerDataAPI.addCoins(player, coins);

        // Award XP
        player.giveExperiencePoints(xp);

        // Award skill points
        PlayerDataAPI.addSkillPoints(player, skillPoints);

        // Mark as claimed
        BountyManager.claimBounty(player.getUUID(), bounty.getBountyId());

        // Notify player
        player.sendSystemMessage(
                Component.literal("✅ BOUNTY COMPLETE: ")
                        .withStyle(ChatFormatting.GREEN, ChatFormatting.BOLD)
                        .append(Component.literal(bounty.getRarity().getDisplayName())
                                .withStyle(bounty.getRarity().getColor()))
        );

        player.sendSystemMessage(
                Component.literal("Rewards: +" + coins + " coins, +" +
                                xp + " XP, +" + skillPoints + " skill points")
                        .withStyle(ChatFormatting.GOLD)
        );

        // Play sound effect
        player.playSound(
                net.minecraft.sounds.SoundEvents.PLAYER_LEVELUP,
                1.0F,
                1.2F
        );
    }

    /**
     * Called when a player joins - could auto-assign bounties or load saved bounties
     */
    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        // Optional: Auto-assign a welcome bounty
        // Uncomment if you want this behavior
        /*
        long currentTime = player.level().getGameTime();
        if (BountyManager.getPlayerBounties(player.getUUID()).isEmpty()) {
            Bounty welcomeBounty = BountyManager.generateAndAssignBounty(player, currentTime);
            if (welcomeBounty != null) {
                player.sendSystemMessage(Component.literal("A new bounty awaits you!")
                    .withStyle(ChatFormatting.GOLD));
            }
        }
        */
    }

    /**
     * Server tick event - cleanup expired bounties periodically
     */
    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        tickCounter++;

        if (tickCounter % CLEANUP_INTERVAL == 0) {
            cleanupExpiredBounties();
        }
    }

    /**
     * Cleans up expired bounties for all online players
     */
    private static void cleanupExpiredBounties() {
        // Get server instance
        net.minecraft.server.MinecraftServer server =
                net.minecraftforge.server.ServerLifecycleHooks.getCurrentServer();

        if (server != null) {
            long currentTime = server.overworld().getGameTime();
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                List<Bounty> expired = BountyManager.cleanupExpiredBounties(
                        player.getUUID(), currentTime
                );
                for (Bounty bounty : expired) {
                    notifyBountyExpired(player, bounty);
                }
            }
        }
    }

    /**
     * Notifies player that a bounty has expired
     */
    private static void notifyBountyExpired(ServerPlayer player, Bounty bounty) {
        player.sendSystemMessage(
                Component.literal("⚠️ BOUNTY EXPIRED: ")
                        .withStyle(ChatFormatting.RED)
                        .append(Component.literal(bounty.getRarity().getDisplayName())
                                .withStyle(bounty.getRarity().getColor()))
        );
    }

    /**
     * Sends progress update to player
     */
    public static void sendProgressUpdate(ServerPlayer player, Bounty bounty) {
        int current = bounty.getCurrentKills();
        int total = bounty.getTotalKillsRequired();
        float progress = bounty.getProgress() * 100;

        player.sendSystemMessage(
                Component.literal(String.format("Bounty Progress: %d/%d (%.1f%%)",
                                current, total, progress))
                        .withStyle(ChatFormatting.YELLOW)
        );
    }
}