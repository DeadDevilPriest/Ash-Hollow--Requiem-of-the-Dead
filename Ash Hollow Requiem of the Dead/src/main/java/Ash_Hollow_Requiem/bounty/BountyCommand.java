package Ash_Hollow_Requiem.bounty;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;
import java.util.UUID;

/**
 * Commands for players to interact with the bounty system
 * Register this in your main mod class
 */
public class BountyCommand {

    /**
     * Registers all bounty commands
     * Call this in your mod's setup (e.g., in FMLCommonSetupEvent)
     * ignores the bountyIndex just ask for confirmation before abandoning
     * and claims when bounty is complete
     */
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("bounty")
                .then(Commands.literal("list")
                        .executes(BountyCommand::listBounties))
                .then(Commands.literal("new")
                        .executes(BountyCommand::createNewBounty)
                        .then(Commands.argument("type", StringArgumentType.string())
                                .suggests((context, builder) -> {
                                    builder.suggest("standard");
                                    builder.suggest("boss");
                                    builder.suggest("horde");
                                    builder.suggest("elite");
                                    return builder.buildFuture();
                                })
                                .then(Commands.argument("rarity", StringArgumentType.string())
                                        .suggests((context, builder) -> {
                                            builder.suggest("common");
                                            builder.suggest("uncommon");
                                            builder.suggest("rare");
                                            builder.suggest("epic");
                                            builder.suggest("legendary");
                                            return builder.buildFuture();
                                        })
                                        .executes(BountyCommand::createSpecificBounty))))
                .then(Commands.literal("abandon")
                        .then(Commands.argument("bountyIndex", StringArgumentType.string())
                                .executes(BountyCommand::abandonBounty)))
                .then(Commands.literal("progress")
                        .executes(BountyCommand::showProgress))
                .then(Commands.literal("claim")
                        .then(Commands.argument("bountyIndex", StringArgumentType.string())
                                .executes(BountyCommand::claimBounty)))
        );
    }

    /**
     * Lists all active bounties for the player
     */
    private static int listBounties(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        if (player == null) return 0;

        List<Bounty> bounties = BountyManager.getPlayerBounties(player.getUUID());

        if (bounties.isEmpty()) {
            player.sendSystemMessage(
                    Component.literal("You have no active bounties. Use /bounty new to get one!")
                            .withStyle(ChatFormatting.YELLOW)
            );
            return 0;
        }

        player.sendSystemMessage(
                Component.literal("=== Your Active Bounties ===")
                        .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD)
        );

        for (int i = 0; i < bounties.size(); i++) {
            Bounty bounty = bounties.get(i);
            displayBounty(player, bounty, i + 1);
        }

        return 1;
    }

    /**
     * Creates a random new bounty
     */
    private static int createNewBounty(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        if (player == null) return 0;

        long currentTime = player.level().getGameTime();
        Bounty bounty = BountyManager.generateAndAssignBounty(player, currentTime);

        if (bounty == null) {
            player.sendSystemMessage(
                    Component.literal("You have too many active bounties! Complete or abandon some first.")
                            .withStyle(ChatFormatting.RED)
            );
            return 0;
        }

        player.sendSystemMessage(
                Component.literal("New bounty acquired!")
                        .withStyle(ChatFormatting.GREEN, ChatFormatting.BOLD)
        );
        displayBounty(player, bounty, -1);

        return 1;
    }

    /**
     * Creates a specific type of bounty
     */
    private static int createSpecificBounty(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        if (player == null) return 0;

        String typeStr = StringArgumentType.getString(context, "type").toUpperCase();
        String rarityStr = StringArgumentType.getString(context, "rarity").toUpperCase();

        try {
            BountyType type = BountyType.valueOf(typeStr);
            BountyRarity rarity = BountyRarity.valueOf(rarityStr);

            long currentTime = player.level().getGameTime();
            Bounty bounty = BountyManager.generateSpecificBounty(player, type, rarity, currentTime);

            if (bounty == null) {
                player.sendSystemMessage(
                        Component.literal("Failed to create bounty - you may have too many active bounties.")
                                .withStyle(ChatFormatting.RED)
                );
                return 0;
            }

            player.sendSystemMessage(
                    Component.literal("Custom bounty created!")
                            .withStyle(ChatFormatting.GREEN, ChatFormatting.BOLD)
            );
            displayBounty(player, bounty, -1);

            return 1;
        } catch (IllegalArgumentException e) {
            player.sendSystemMessage(
                    Component.literal("Invalid type or rarity! Use: standard/boss/horde/elite and common/uncommon/rare/epic/legendary")
                            .withStyle(ChatFormatting.RED)
            );
            return 0;
        }
    }

    /**
     * Abandons a bounty
     */
    private static int abandonBounty(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        if (player == null) return 0;

        try {
            int index = Integer.parseInt(StringArgumentType.getString(context, "bountyIndex")) - 1;
            List<Bounty> bounties = BountyManager.getPlayerBounties(player.getUUID());

            if (index < 0 || index >= bounties.size()) {
                player.sendSystemMessage(
                        Component.literal("Invalid bounty number!")
                                .withStyle(ChatFormatting.RED)
                );
                return 0;
            }

            Bounty bounty = bounties.get(index);
            boolean success = BountyManager.abandonBounty(player.getUUID(), bounty.getBountyId());

            if (success) {
                player.sendSystemMessage(
                        Component.literal("Bounty abandoned.")
                                .withStyle(ChatFormatting.YELLOW)
                );
                return 1;
            }
        } catch (NumberFormatException e) {
            player.sendSystemMessage(
                    Component.literal("Please provide a valid bounty number!")
                            .withStyle(ChatFormatting.RED)
            );
        }

        return 0;
    }

    /**
     * Shows detailed progress for all bounties
     */
    private static int showProgress(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        if (player == null) return 0;

        List<Bounty> bounties = BountyManager.getPlayerBounties(player.getUUID());

        if (bounties.isEmpty()) {
            player.sendSystemMessage(
                    Component.literal("No active bounties!")
                            .withStyle(ChatFormatting.YELLOW)
            );
            return 0;
        }

        player.sendSystemMessage(
                Component.literal("=== Bounty Progress ===")
                        .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD)
        );

        for (int i = 0; i < bounties.size(); i++) {
            Bounty bounty = bounties.get(i);
            displayDetailedProgress(player, bounty, i + 1);
        }

        return 1;
    }

    /**
     * Claims a completed bounty
     */
    private static int claimBounty(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        if (player == null) return 0;

        try {
            int index = Integer.parseInt(StringArgumentType.getString(context, "bountyIndex")) - 1;
            List<Bounty> bounties = BountyManager.getPlayerBounties(player.getUUID());

            if (index < 0 || index >= bounties.size()) {
                player.sendSystemMessage(
                        Component.literal("Invalid bounty number!")
                                .withStyle(ChatFormatting.RED)
                );
                return 0;
            }

            Bounty bounty = bounties.get(index);

            if (!bounty.isCompleted()) {
                player.sendSystemMessage(
                        Component.literal("This bounty is not completed yet!")
                                .withStyle(ChatFormatting.RED)
                );
                return 0;
            }

            boolean success = BountyManager.claimBounty(player.getUUID(), bounty.getBountyId());

            if (success) {
                int reward = bounty.getRarity().getBaseReward();
                player.sendSystemMessage(
                        Component.literal("âœ… Bounty claimed! Reward: " + reward + " coins")
                                .withStyle(ChatFormatting.GREEN, ChatFormatting.BOLD)
                );

                // TODO: Give actual rewards here (items, experience, currency, etc.)
                // player.giveExperiencePoints(reward);

                return 1;
            }
        } catch (NumberFormatException e) {
            player.sendSystemMessage(
                    Component.literal("Please provide a valid bounty number!")
                            .withStyle(ChatFormatting.RED)
            );
        }

        return 0;
    }

    /**
     * Helper: Displays a single bounty
     */
    private static void displayBounty(ServerPlayer player, Bounty bounty, int index) {
        String prefix = index > 0 ? "#" + index + " - " : "";

        player.sendSystemMessage(
                Component.literal(prefix)
                        .withStyle(ChatFormatting.WHITE)
                        .append(Component.literal(bounty.getRarity().getDisplayName())
                                .withStyle(bounty.getRarity().getColor(), ChatFormatting.BOLD))
                        .append(Component.literal(" " + bounty.getType().getDisplayName())
                                .withStyle(ChatFormatting.GRAY))
        );

        player.sendSystemMessage(
                Component.literal("  Progress: " + bounty.getCurrentKills() + "/" +
                                bounty.getTotalKillsRequired() + " (" +
                                String.format("%.1f", bounty.getProgress() * 100) + "%)")
                        .withStyle(ChatFormatting.YELLOW)
        );

        if (bounty.isCompleted()) {
            player.sendSystemMessage(
                    Component.literal("  âœ… COMPLETE - Use /bounty claim " + (index > 0 ? index : "1"))
                            .withStyle(ChatFormatting.GREEN, ChatFormatting.BOLD)
            );
        }
    }

    /**
     * Helper: Displays detailed progress with individual targets
     */
    private static void displayDetailedProgress(ServerPlayer player, Bounty bounty, int index) {
        displayBounty(player, bounty, index);

        player.sendSystemMessage(
                Component.literal("  Targets:")
                        .withStyle(ChatFormatting.AQUA)
        );

        for (BountyTarget target : bounty.getTargets()) {
            String mobName = target.getEntityType().getDescription().getString();
            String status = target.isComplete() ? "âœ…" : "â—Ź";

            player.sendSystemMessage(
                    Component.literal("    " + status + " " + mobName + ": " +
                                    target.getCurrentKills() + "/" + target.getRequiredKills())
                            .withStyle(target.isComplete() ? ChatFormatting.GREEN : ChatFormatting.WHITE)
            );
        }

        player.sendSystemMessage(Component.literal(""));
    }
}