package Ash_Hollow_Requiem.network;

import Ash_Hollow_Requiem.bounty.*;
import Ash_Hollow_Requiem.interfaces.BountyBoardScreen; // ✅ Import the screen class
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import java.util.UUID;
import java.util.function.Supplier;

public class AcceptBountyPacket {
    private final UUID bountyId;
    private final BountyBoardScreen.BountyCategory category; // ✅ Use BountyBoardScreen.BountyCategory

    public AcceptBountyPacket(UUID bountyId, BountyBoardScreen.BountyCategory category) {
        this.bountyId = bountyId;
        this.category = category;
    }

    public AcceptBountyPacket(FriendlyByteBuf buf) {
        this.bountyId = buf.readUUID();
        this.category = buf.readEnum(BountyBoardScreen.BountyCategory.class); // ✅ Read correct enum
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeUUID(bountyId);
        buf.writeEnum(category);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                long currentTime = player.level().getGameTime();

                // ✅ For now, generate a new bounty based on the category
                // (In the future, this would fetch from persistent BountyBoardData)
                Bounty bounty = generateBountyForCategory(category, currentTime);

                if (bounty == null) {
                    player.displayClientMessage(
                            Component.literal("❌ Bounty no longer available!")
                                    .withStyle(ChatFormatting.RED),
                            false
                    );
                    return;
                }

                // ✅ Check if player has room for more bounties
                if (BountyManager.getPlayerBounties(player.getUUID()).size() >= 5) {
                    player.displayClientMessage(
                            Component.literal("❌ You have too many active bounties!")
                                    .withStyle(ChatFormatting.RED),
                            false
                    );
                    return;
                }

                // ✅ Check token cost for BOSS bounties ONLY
                if (bounty.isBossBounty()) {
                    int tokenCost = bounty.getTokenCost();

                    // Get player's tokens from NBT
                    CompoundTag playerData = player.getPersistentData();
                    int currentTokens = playerData.getInt("BountyTokens");

                    // ❌ Check if player can afford it
                    if (currentTokens < tokenCost) {
                        player.displayClientMessage(
                                Component.literal("❌ Insufficient tokens!")
                                        .withStyle(ChatFormatting.RED),
                                false
                        );
                        player.displayClientMessage(
                                Component.literal("  Required: " + tokenCost + " | Available: " + currentTokens)
                                        .withStyle(ChatFormatting.GRAY),
                                false
                        );
                        return;
                    }

                    // ✅ Deduct tokens
                    int newTokenCount = currentTokens - tokenCost;
                    playerData.putInt("BountyTokens", newTokenCount);

                    player.displayClientMessage(
                            Component.literal("━━━━━━━━━━━━━━━━━━━━")
                                    .withStyle(ChatFormatting.DARK_GRAY),
                            false
                    );
                    player.displayClientMessage(
                            Component.literal("⚔ Boss Bounty Accepted!")
                                    .withStyle(ChatFormatting.GOLD),
                            false
                    );
                    player.displayClientMessage(
                            Component.literal("  Cost: -" + tokenCost + " tokens")
                                    .withStyle(ChatFormatting.YELLOW),
                            false
                    );
                    player.displayClientMessage(
                            Component.literal("  Balance: " + newTokenCount + "/100")
                                    .withStyle(ChatFormatting.GREEN),
                            false
                    );
                    player.displayClientMessage(
                            Component.literal("━━━━━━━━━━━━━━━━━━━━")
                                    .withStyle(ChatFormatting.DARK_GRAY),
                            false
                    );
                } else {
                    // ✅ Non-boss bounties are FREE
                    player.displayClientMessage(
                            Component.literal("✅ Bounty accepted! (FREE)")
                                    .withStyle(ChatFormatting.GREEN),
                            false
                    );
                }

                // ✅ Assign bounty to player
                boolean success = BountyManager.assignBounty(player, bounty);

                if (success) {
                    // Success sound
                    player.playSound(
                            net.minecraft.sounds.SoundEvents.PLAYER_LEVELUP,
                            1.0f,
                            1.2f
                    );
                } else {
                    // If assignment failed, refund tokens for boss bounties
                    if (bounty.isBossBounty()) {
                        CompoundTag playerData = player.getPersistentData();
                        int currentTokens = playerData.getInt("BountyTokens");
                        playerData.putInt("BountyTokens", currentTokens + bounty.getTokenCost());

                        player.displayClientMessage(
                                Component.literal("⚠ Refunded " + bounty.getTokenCost() + " tokens")
                                        .withStyle(ChatFormatting.YELLOW),
                                false
                        );
                    }
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }

    /**
     * ✅ Generate a bounty based on category (temporary until server persistence is implemented)
     */
    private Bounty generateBountyForCategory(BountyBoardScreen.BountyCategory category, long currentTime) {
        BountyRarity rarity = getRandomRarityForCategory(category);

        return switch(category) {
            case BOUNTY -> BountyGenerator.generateStandardBounty(rarity, currentTime);
            case SPECIAL -> BountyGenerator.generateStandardBounty(rarity, currentTime);
            case HORDE -> BountyGenerator.generateHordeBounty(rarity, currentTime);
            case ELITE -> BountyGenerator.generateEliteBounty(rarity, currentTime);
            case BOSS -> {
                // ✅ BOSS category: Epic+ with TOKEN COST
                BountyRarity bossRarity = getRandomBossRarity();
                yield BountyGenerator.generateBossBounty(bossRarity, currentTime);
            }
            case EVENT -> BountyGenerator.generateStandardBounty(rarity, currentTime);
        };
    }

    private BountyRarity getRandomRarityForCategory(BountyBoardScreen.BountyCategory category) {
        java.util.Random random = new java.util.Random();
        int roll = random.nextInt(100);

        return switch (category) {
            case BOSS, EVENT -> {
                if (roll < 30) yield BountyRarity.EPIC;
                if (roll < 70) yield BountyRarity.LEGENDARY;
                yield BountyRarity.EPIC;
            }
            case ELITE -> {
                if (roll < 40) yield BountyRarity.RARE;
                if (roll < 80) yield BountyRarity.EPIC;
                yield BountyRarity.LEGENDARY;
            }
            default -> {
                if (roll < 40) yield BountyRarity.COMMON;
                if (roll < 70) yield BountyRarity.UNCOMMON;
                if (roll < 90) yield BountyRarity.RARE;
                yield BountyRarity.EPIC;
            }
        };
    }

    /**
     * Get random boss rarity (Epic to Unknown)
     */
    private BountyRarity getRandomBossRarity() {
        java.util.Random random = new java.util.Random();
        int roll = random.nextInt(100);

        if (roll < 30) return BountyRarity.EPIC;
        if (roll < 55) return BountyRarity.LEGENDARY;
        if (roll < 75) return BountyRarity.ANCIENT;
        if (roll < 87) return BountyRarity.CURSED;
        if (roll < 93) return BountyRarity.EXPERIMENTAL;
        if (roll < 96) return BountyRarity.HOLLOW;
        if (roll < 98) return BountyRarity.GODLY;
        if (roll < 99) return BountyRarity.INSANE;
        return BountyRarity.UNKNOWN;
    }
}