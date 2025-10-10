package Ash_Hollow_Requiem.effects;

import Ash_Hollow_Requiem.damage.ModDamageSources;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class TraumaEffect extends MobEffect {

    public TraumaEffect() {
        super(MobEffectCategory.HARMFUL, 0x4B0082); // Purple/dark color for corruption
    }

    // Optional: Override to add custom behavior when the effect ticks
    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        // Add your corruption effect logic here
        // For example: deal damage, apply debuffs, etc.
    }

    // Optional: Control how often the effect ticks (in ticks, 20 ticks = 1 second)
    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true; // Modify this based on when you want the effect to trigger
    }
}