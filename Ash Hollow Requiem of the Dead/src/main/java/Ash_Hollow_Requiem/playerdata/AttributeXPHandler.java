package Ash_Hollow_Requiem.playerdata;

import Ash_Hollow_Requiem.client.AttributeNotificationRenderer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "ash_hollow_requiem_of_the_dead")
public class AttributeXPHandler {

    @SubscribeEvent
    public static void onMeleeDamage(LivingHurtEvent event) {
        if (event.getSource().getEntity() instanceof Player player) {
            if (!event.getSource().is(net.minecraft.tags.DamageTypeTags.IS_PROJECTILE)) {
                player.getCapability(PlayerDataProvider.PLAYER_DATA).ifPresent(data -> {
                    float xpGained = event.getAmount() * 0.5f;

                    if (data.getAttributes().addStrengthXP(xpGained)) {
                        if (player.level().isClientSide) {
                            AttributeNotificationRenderer.showAttributeLevelUp("Strength",
                                    data.getAttributes().getStrength());
                        }
                    }
                });
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerHurt(LivingHurtEvent event) {
        if (event.getEntity() instanceof Player player) {
            player.getCapability(PlayerDataProvider.PLAYER_DATA).ifPresent(data -> {
                float xpGained = event.getAmount() * 0.75f;

                if (data.getAttributes().addConstitutionXP(xpGained)) {
                    if (player.level().isClientSide) {
                        AttributeNotificationRenderer.showAttributeLevelUp("Constitution",
                                data.getAttributes().getConstitution());
                    }
                }
            });
        }
    }

    public static void onPlayerSprint(Player player, float distance) {
        player.getCapability(PlayerDataProvider.PLAYER_DATA).ifPresent(data -> {
            float xpGained = distance * 0.1f;

            if (data.getAttributes().addDexterityXP(xpGained)) {
                if (player.level().isClientSide) {
                    AttributeNotificationRenderer.showAttributeLevelUp("Dexterity",
                            data.getAttributes().getDexterity());
                }
            }
        });
    }

    @SubscribeEvent
    public static void onCraft(PlayerInteractEvent.RightClickItem event) {
        Player player = event.getEntity();
        player.getCapability(PlayerDataProvider.PLAYER_DATA).ifPresent(data -> {
            if (data.getAttributes().addIntelligenceXP(5.0f)) {
                if (player.level().isClientSide) {
                    AttributeNotificationRenderer.showAttributeLevelUp("Intelligence",
                            data.getAttributes().getIntelligence());
                }
            }
        });
    }

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        Player player = event.getPlayer();
        player.getCapability(PlayerDataProvider.PLAYER_DATA).ifPresent(data -> {
            float hardness = event.getState().getDestroySpeed(event.getLevel(), event.getPos());
            float xpGained = Math.max(0.5f, hardness * 0.3f);

            if (data.getAttributes().addStrengthXP(xpGained)) {
                if (player.level().isClientSide) {
                    AttributeNotificationRenderer.showAttributeLevelUp("Strength",
                            data.getAttributes().getStrength());
                }
            }
        });
    }

    @SubscribeEvent
    public static void onEntityKill(LivingDeathEvent event) {
        if (event.getSource().getEntity() instanceof Player player) {
            LivingEntity killed = event.getEntity();
            player.getCapability(PlayerDataProvider.PLAYER_DATA).ifPresent(data -> {
                float maxHealth = killed.getMaxHealth();
                float xpGained = maxHealth * 0.2f;

                if (data.getAttributes().addWillpowerXP(xpGained)) {
                    if (player.level().isClientSide) {
                        AttributeNotificationRenderer.showAttributeLevelUp("Willpower",
                                data.getAttributes().getWillpower());
                    }
                }
            });
        }
    }

    public static void onClueDiscovered(Player player) {
        player.getCapability(PlayerDataProvider.PLAYER_DATA).ifPresent(data -> {
            if (data.getAttributes().addIntelligenceXP(25.0f)) {
                if (player.level().isClientSide) {
                    AttributeNotificationRenderer.showAttributeLevelUp("Intelligence",
                            data.getAttributes().getIntelligence());
                }
            }
        });
    }

    public static void onNearDeath(Player player) {
        if (player.getHealth() < player.getMaxHealth() * 0.1f) {
            player.getCapability(PlayerDataProvider.PLAYER_DATA).ifPresent(data -> {
                if (data.getAttributes().addConstitutionXP(15.0f)) {
                    if (player.level().isClientSide) {
                        AttributeNotificationRenderer.showAttributeLevelUp("Constitution",
                                data.getAttributes().getConstitution());
                    }
                }
            });
        }
    }

    public static void onSuccessfulDodge(Player player) {
        player.getCapability(PlayerDataProvider.PLAYER_DATA).ifPresent(data -> {
            if (data.getAttributes().addDexterityXP(10.0f)) {
                if (player.level().isClientSide) {
                    AttributeNotificationRenderer.showAttributeLevelUp("Dexterity",
                            data.getAttributes().getDexterity());
                }
            }
        });
    }
}