package Ash_Hollow_Requiem.effects;

import Ash_Hollow_Requiem.damage.ModDamageSources;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class NecroticEffect extends MobEffect {

    private static final float BASE_HEALTH_PER_STACK = 2.0f;
    private static final int BASE_DURATION_PER_STACK = 120;

    public NecroticEffect() {
        super(MobEffectCategory.HARMFUL, 0x4B0082); // Purple/dark color for corruption
    }

    // Optional: Override to add custom behavior when the effect ticks
    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        // Add your corruption effect logic here
        // For example: deal damage, apply debuffs, etc.
        Level level = entity.level();

        if (level.isClientSide()) {
            return;
        }

        int necroticStacks = amplifier + 1;

        float necroticHealth = BASE_HEALTH_PER_STACK * necroticStacks;

        if (health_points ) {
            DamageSource necroticSource = ModDamageSources.necroticDamageSource(level, entity);
            entity.setMaxHealth(MaxHealth - necroticHealth);
        }
    }

    // Optional: Control how often the effect ticks (in ticks, 20 ticks = 1 second)
    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true; // Modify this based on when you want the effect to trigger
    }

    /**public static void applyNecrotic(LivingEntity target, int stacks, int baseDuration) {
    *    int totalDuration = baseDuration * stacks;
    *
    *    //TODO: Skill for reducing Necrotic
    *
    *    //only apply if there is duration remaining
    *    if (totalDuration > 0 && stacks > 0) {
    *        target.addEffect(new net.minecraft.world.effect.MobEffectInstance(
    *            MobEffect.NECROTIC.get(),
    *            totalDuration,
    *            stacks - 1 // amplifier is stacks - 1
    *        ));
    *    }
    *}
    **/

    public static void applyNecrotic(LivingEntity target, int stacks) {
        applyNecrotic(target, stacks, BASE_DURATION_PER_STACK);
    }

    public static int getActualDuration(LivingEntity entity, int stacks, int baseDuration) {
        int totalDuration = baseDuration * stacks;
        return totalDuration;
    }
}