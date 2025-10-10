package Ash_Hollow_Requiem.effects;

import Ash_Hollow_Requiem.damage.ModDamageSources;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class BleedingEffect extends MobEffect {

    public BleedingEffect() {
        // HARMFUL = negative effect; color = dark red (#8B0000)
        super(MobEffectCategory.HARMFUL, 0x8B0000);
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        Level level = entity.level();  // Works for both client + server

        // Only apply damage on the server side
        if (level.isClientSide()) {
            return;
        }

        // Get a bleeding-type DamageSource from your ModDamageTypes
        DamageSource bleedingSource = ModDamageSources.bleedingDamageSource(level, entity);

        // Apply 0.5 damage per tick (or more if you want to scale with amplifier)
        entity.hurt(bleedingSource, 0.5F + amplifier);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        // Run every tick (20x per second)
        return true;
    }
}
