package Ash_Hollow_Requiem.bounty;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import Ash_Hollow_Requiem.network.AcceptBountyPacket;


/**
 * Manages all active bounties for players
 * This is where bounties are stored and tracked
 */
public class BountyManager {
    // Player UUID -> List of active bounties
    private static final Map<UUID, List<Bounty>> PLAYER_BOUNTIES = new ConcurrentHashMap<>();

    // Global active bounties (shared by all players)
    private static final List<Bounty> GLOBAL_BOUNTIES = new ArrayList<>();

    // Configuration
    private static final int MAX_BOUNTIES_PER_PLAYER = 5;
    private static final int MAX_GLOBAL_BOUNTIES = 10;

    /**
     * Assigns a new bounty to a player
     */
    public static boolean assignBounty(ServerPlayer player, Bounty bounty) {
        UUID playerId = player.getUUID();

        List<Bounty> bounties = PLAYER_BOUNTIES.computeIfAbsent(
                playerId,
                k -> new ArrayList<>()
        );

        if (bounties.size() >= MAX_BOUNTIES_PER_PLAYER) {
            return false; // Player has too many bounties
        }

        bounties.add(bounty);
        return true;
    }

    /**
     * Generates and assigns a random bounty to a player
     */
    public static Bounty generateAndAssignBounty(ServerPlayer player, long currentTime) {
        Bounty bounty = BountyGenerator.generateRandomBounty(currentTime);

        if (assignBounty(player, bounty)) {
            return bounty;
        }

        return null;
    }

    /**
     * Generates a specific type of bounty for a player
     */
    public static Bounty generateSpecificBounty(ServerPlayer player,
                                                BountyType type,
                                                BountyRarity rarity,
                                                long currentTime) {
        Bounty bounty = switch (type) {
            case BOSS -> BountyGenerator.generateBossBounty(rarity, currentTime);
            case HORDE -> BountyGenerator.generateHordeBounty(rarity, currentTime);
            case ELITE -> BountyGenerator.generateEliteBounty(rarity, currentTime);
            default -> BountyGenerator.generateStandardBounty(rarity, currentTime);
        };

        assignBounty(player, bounty);
        return bounty;
    }

    /**
     * Registers a kill towards player's bounties
     */
    public static List<Bounty> registerKill(ServerPlayer player, LivingEntity killedEntity) {
        UUID playerId = player.getUUID();
        List<Bounty> bounties = PLAYER_BOUNTIES.get(playerId);

        if (bounties == null || bounties.isEmpty()) {
            return Collections.emptyList();
        }

        List<Bounty> completedBounties = new ArrayList<>();

        for (Bounty bounty : bounties) {
            if (bounty.registerKill(killedEntity) && bounty.isCompleted()) {
                completedBounties.add(bounty);
            }
        }

        return completedBounties;
    }

    /**
     * Gets all active bounties for a player
     */
    public static List<Bounty> getPlayerBounties(UUID playerId) {
        return PLAYER_BOUNTIES.getOrDefault(playerId, new ArrayList<>());
    }

    /**
     * Removes expired bounties and returns them
     */
    public static List<Bounty> cleanupExpiredBounties(UUID playerId, long currentTime) {
        List<Bounty> bounties = PLAYER_BOUNTIES.get(playerId);

        if (bounties == null) {
            return Collections.emptyList();
        }

        List<Bounty> expired = new ArrayList<>();
        Iterator<Bounty> iterator = bounties.iterator();

        while (iterator.hasNext()) {
            Bounty bounty = iterator.next();
            if (bounty.hasExpired(currentTime)) {
                expired.add(bounty);
                iterator.remove();
            }
        }

        return expired;
    }

    /**
     * Claims a completed bounty and removes it
     */
    public static boolean claimBounty(UUID playerId, UUID bountyId) {
        List<Bounty> bounties = PLAYER_BOUNTIES.get(playerId);

        if (bounties == null) {
            return false;
        }

        for (int i = 0; i < bounties.size(); i++) {
            Bounty bounty = bounties.get(i);
            if (bounty.getBountyId().equals(bountyId) &&
                    bounty.isCompleted() &&
                    !bounty.isClaimed()) {

                bounty.setClaimed(true);
                bounties.remove(i);
                return true;
            }
        }

        return false;
    }

    /**
     * Abandons a bounty
     */
    public static boolean abandonBounty(UUID playerId, UUID bountyId) {
        List<Bounty> bounties = PLAYER_BOUNTIES.get(playerId);

        if (bounties == null) {
            return false;
        }

        return bounties.removeIf(b -> b.getBountyId().equals(bountyId));
    }

    /**
     * Creates a global bounty that all players can contribute to
     */
    public static void createGlobalBounty(Bounty bounty) {
        if (GLOBAL_BOUNTIES.size() < MAX_GLOBAL_BOUNTIES) {
            GLOBAL_BOUNTIES.add(bounty);
        }
    }

    /**
     * Gets all global bounties
     */
    public static List<Bounty> getGlobalBounties() {
        return new ArrayList<>(GLOBAL_BOUNTIES);
    }

    /**
     * Cleans up all expired global bounties
     */
    public static void cleanupGlobalBounties(long currentTime) {
        GLOBAL_BOUNTIES.removeIf(b -> b.hasExpired(currentTime));
    }

    /**
     * Gets statistics for a player's bounty progress
     */
    public static BountyStatistics getPlayerStatistics(UUID playerId) {
        List<Bounty> bounties = getPlayerBounties(playerId);

        int total = bounties.size();
        int completed = (int) bounties.stream().filter(Bounty::isCompleted).count();
        int active = total - completed;

        return new BountyStatistics(total, completed, active);
    }

    public static void addPlayerBounty(UUID uuid, Bounty bounty) {
        PLAYER_BOUNTIES.computeIfAbsent(uuid, k -> new ArrayList<>()).add(bounty);

    }

    /**
     * Simple statistics class
     */
    public static class BountyStatistics {
        public final int totalBounties;
        public final int completedBounties;
        public final int activeBounties;

        public BountyStatistics(int total, int completed, int active) {
            this.totalBounties = total;
            this.completedBounties = completed;
            this.activeBounties = active;
        }
    }
}