package Ash_Hollow_Requiem.damage;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class ModDamageSources {

    private static Holder<DamageType> getType(Level level, String name) {
        return level.registryAccess()
                .registryOrThrow(Registries.DAMAGE_TYPE)
                .getHolderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE,
                        new ResourceLocation("ash_hollow_requiem_of_the_dead", name)));
    }

    public static DamageSource harpoonDamageSource(Level level, LivingEntity owner) {
        return new DamageSource(getType(level, "harpoon"), owner);
    }

    public static DamageSource bleedingDamageSource(Level level, LivingEntity target) {
        return new DamageSource(getType(level, "bleeding"), target);
    }

    public static DamageSource traumaDamageSource(Level level, LivingEntity target) {
        return new DamageSource(getType(level, "trauma"), target);
    }

    
}
