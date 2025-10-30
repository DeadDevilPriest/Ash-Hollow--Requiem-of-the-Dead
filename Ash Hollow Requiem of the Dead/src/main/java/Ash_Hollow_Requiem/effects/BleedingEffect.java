package Ash_Hollow_Requiem.effects;

import Ash_Hollow_Requiem.damage.ModDamageSources;
import Ash_Hollow_Requiem.playerdata.PlayerData;
import Ash_Hollow_Requiem.playerdata.PlayerDataProvider;
import Ash_Hollow_Requiem.skilltributes.Skills;
import Ash_Hollow_Requiem.skilltributes.SkillRegistry;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * Bleeding Effect - Now integrated with Multi-Level Skills system
 * Thick Skin skill reduces bleeding damage AND duration based on level and stacks
 *
 * Each bleeding stack increases BOTH damage and duration
 *
 * Example without Thick Skin:
 * - 1 stack: 1 damage/tick for 120 ticks (6 seconds)
 * - 5 stacks: 5 damage/tick for 600 ticks (30 seconds)
 *
 * Example with Thick Skin Level 5 (75% damage reduction, 40% duration reduction per stack):
 * - 1 stack: 0.25 damage/tick for 72 ticks (40% of 120)
 * - 5 stacks: 1.25 damage/tick for 180 ticks (40% of 300 per stack = 60% total)
 *
 * Bleeding Effect - Now integrated with Skills system
 * Thick Skin skill reduces bleeding stacks
 */
public class BleedingEffect extends MobEffect {

    private static final float BASE_DAMAGE_PER_STACK = 1.0f;
    private static final int BASE_DURATION_PER_STACK = 120; // 6 seconds in ticks

    public BleedingEffect() {
        // HARMFUL = negative effect; color = dark red (#8B0000)
        super(MobEffectCategory.HARMFUL, 0x8B0000);
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        Level level = entity.level();

        // Only apply damage on the server side
        if (level.isClientSide()) {
            return;
        }

        // Calculate bleeding stacks (amplifier + 1)
        int bleedingStacks = amplifier + 1;

        // Calculate base damage (1 damage per stack per tick)
        float damage = BASE_DAMAGE_PER_STACK * bleedingStacks;

        // Check if entity is a player with Thick Skin skill
        if (entity instanceof Player player) {
            PlayerData data = PlayerDataProvider.getPlayerData(player);
            if (data != null) {
                int thickSkinLevel = data.getSkillLevel("thick_skin");

                if (thickSkinLevel > 0) {
                    Skills.ThickSkin thickSkin = (Skills.ThickSkin) SkillRegistry.getSkill("thick_skin");
                    if (thickSkin != null) {
                        // Get damage reduction (15% per level, max 75%)
                        float damageReduction = thickSkin.getDamageReduction(thickSkinLevel);

                        // Apply reduction
                        damage *= (1.0f - damageReduction);

                        // Visual feedback at max level
                        if (thickSkinLevel >= 5 && entity.tickCount % 100 == 0) {
                            player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                                    "§6Thick Skin is reducing bleeding damage by 75%!"));
                        }
                    }
                }
            }
        }

        // Apply damage
        if (damage > 0) {
            DamageSource bleedingSource = ModDamageSources.bleedingDamageSource(level, entity);
            entity.hurt(bleedingSource, damage);
        }
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        // Run every tick (20x per second)
        return true;
    }

    /**
     * Helper method to apply bleeding with skill consideration
     *
     * @param target The entity to apply bleeding to
     * @param stacks Number of bleeding stacks (1-10 recommended)
     * @param baseDuration Base duration in ticks PER STACK
     */
    public static void applyBleeding(LivingEntity target, int stacks, int baseDuration) {
        // Calculate total duration based on stacks
        // Each stack adds to the duration
        int totalDuration = baseDuration * stacks;

        // If target has Thick Skin, reduce duration
        if (target instanceof Player player) {
            PlayerData data = PlayerDataProvider.getPlayerData(player);
            if (data != null) {
                int thickSkinLevel = data.getSkillLevel("thick_skin");

                if (thickSkinLevel > 0) {
                    Skills.ThickSkin thickSkin = (Skills.ThickSkin) SkillRegistry.getSkill("thick_skin");
                    if (thickSkin != null) {
                        // Get duration reduction per stack (8% per level, max 40%)
                        float durationReductionPerStack = thickSkin.getDurationReductionPerStack(thickSkinLevel);

                        // Calculate total duration reduction based on stacks
                        // More stacks = more total reduction
                        float totalReduction = durationReductionPerStack * stacks;
                        totalReduction = Math.min(0.9f, totalReduction); // Cap at 90% reduction

                        // Apply reduction
                        totalDuration = (int)(totalDuration * (1.0f - totalReduction));

                        // Notify player
                        if (thickSkinLevel >= 3) {
                            player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                                    String.format("§6Thick Skin reduced bleeding duration by %.0f%%!", totalReduction * 100)));
                        }
                    }
                }
            }
        }

        // Only apply if there is duration remaining
        if (totalDuration > 0 && stacks > 0) {
            target.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                    ModEffects.BLEEDING.get(),
                    totalDuration,
                    stacks - 1  // amplifier is stacks - 1
            ));
        } else if (target instanceof Player player) {
            // Bleeding completely negated!
            player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                    "§aThick Skin completely negated the bleeding!"));
        }
    }

    /**
     * Convenience method with default base duration
     */
    public static void applyBleeding(LivingEntity target, int stacks) {
        applyBleeding(target, stacks, BASE_DURATION_PER_STACK);
    }

    /**
     * Get the actual damage per tick for a given stack count and player
     */
    public static float getActualDamagePerTick(LivingEntity entity, int stacks) {
        float damage = BASE_DAMAGE_PER_STACK * stacks;

        if (entity instanceof Player player) {
            PlayerData data = PlayerDataProvider.getPlayerData(player);
            if (data != null) {
                int thickSkinLevel = data.getSkillLevel("thick_skin");
                if (thickSkinLevel > 0) {
                    Skills.ThickSkin thickSkin = (Skills.ThickSkin) SkillRegistry.getSkill("thick_skin");
                    if (thickSkin != null) {
                        float damageReduction = thickSkin.getDamageReduction(thickSkinLevel);
                        damage *= (1.0f - damageReduction);
                    }
                }
            }
        }

        return damage;
    }

    /**
     * Get the actual duration for a given stack count and player
     */
    public static int getActualDuration(LivingEntity entity, int stacks, int baseDuration) {
        int totalDuration = baseDuration * stacks;

        if (entity instanceof Player player) {
            PlayerData data = PlayerDataProvider.getPlayerData(player);
            if (data != null) {
                int thickSkinLevel = data.getSkillLevel("thick_skin");
                if (thickSkinLevel > 0) {
                    Skills.ThickSkin thickSkin = (Skills.ThickSkin) SkillRegistry.getSkill("thick_skin");
                    if (thickSkin != null) {
                        float durationReductionPerStack = thickSkin.getDurationReductionPerStack(thickSkinLevel);
                        float totalReduction = Math.min(0.9f, durationReductionPerStack * stacks);
                        totalDuration = (int)(totalDuration * (1.0f - totalReduction));
                    }
                }
            }
        }

        return totalDuration;
    }
}