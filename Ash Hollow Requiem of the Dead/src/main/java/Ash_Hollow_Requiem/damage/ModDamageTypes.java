package Ash_Hollow_Requiem.damage;

import net.minecraft.core.registries.Registries;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import Ash_Hollow_Requiem.Ash_Hollow;

public final class ModDamageTypes {
    public static final ResourceKey<DamageType> HARPOON_KEY =
            ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(Ash_Hollow.MODID, "harpoon"));

    public static final ResourceKey<DamageType> BLEEDING_KEY =
            ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(Ash_Hollow.MODID, "bleeding"));

    private ModDamageTypes() {}

    /** Get the Holder for a key or throw with clear message (use on server/world where registry is available). */
    public static Holder<DamageType> getHolderOrThrow(Level level, ResourceKey<DamageType> key) {
        Registry<DamageType> reg = level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE);
        // getHolder returns Optional<Holder<T>>; you can use getHolderOrThrow on some registry impls:
        return reg.getHolder(key).orElseThrow(() -> new IllegalStateException("Missing damage type: " + key.location()));
    }

    /** Build a DamageSource for the harpoon projectile (use level.registryAccess to resolve holder). */
    public static DamageSource harpoonDamageSource(Level level, Entity projectile, Entity owner) {
        Holder<DamageType> h = getHolderOrThrow(level, HARPOON_KEY);
        return new DamageSource(h, projectile, owner);
    }

    /** Build a DamageSource for bleeding damage (used by the effect per-tick). */
    public static DamageSource bleedingDamageSource(Level level, Entity source) {
        Holder<DamageType> h = getHolderOrThrow(level, BLEEDING_KEY);
        // there are DamageSource constructors that accept only the holder in some mappings;
        // here use the holder + null for entities if needed by your mappings:
        return new DamageSource(h, source, null);
    }
}
