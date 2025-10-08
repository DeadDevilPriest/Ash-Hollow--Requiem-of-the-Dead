package Ash_Hollow_Requiem;

import Ash_Hollow_Requiem.damage.ModDamageSources;
import Ash_Hollow_Requiem.effects.ModEffects;
import com.mojang.datafixers.util.Either;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderOwner;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class HarpoonEntity extends AbstractArrow implements GeoEntity, Holder<DamageType> {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public HarpoonEntity(EntityType<? extends HarpoonEntity> type, Level level) {
        super(type, level);
    }

	public HarpoonEntity(EntityType<? extends HarpoonEntity> type, LivingEntity shooter, Level level) {
        super(type, shooter, level);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        // Optional: add animation controllers here
    }

    @Override
    protected ItemStack getPickupItem() {
        return new ItemStack(ModItems.Harpoon.get());
    }

    @Override
    protected void onHitEntity(EntityHitResult hitResult) {
        super.onHitEntity(hitResult);
        Entity target = hitResult.getEntity();
        Entity owner = this.getOwner();

        if (!this.level().isClientSide && target instanceof LivingEntity livingTarget) {
            // ✅ Apply your custom harpoon damage
            livingTarget.hurt(ModDamageSources.harpoonDamageSource(level(), (LivingEntity) owner), 13.0F);

            // ✅ Apply your custom bleeding effect
            livingTarget.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                    ModEffects.BLEEDING.get(), // Custom effect registered in ModEffects
                    120, // 6 seconds (20 ticks * 6)
                    0   // amplifier level
            ));

            // Optional: destroy the harpoon entity after impact
            this.discard();
        }
    }

    @Override
    protected void tickDespawn() {
        // You can control when the harpoon despawns naturally
        if (this.inGroundTime > 40) {
            this.discard();
        }
    }

    @Override
        public void playerTouch(net.minecraft.world.entity.player.Player player) {
            if (!this.level().isClientSide && this.pickup != Pickup.ALLOWED) {
                this.remove(RemovalReason.DISCARDED);
            } else {
                super.playerTouch(player);
            }
        }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public DamageType value() {
        return null;
    }

    @Override
    public boolean isBound() {
        return false;
    }

    @Override
    public boolean is(ResourceLocation p_205713_) {
        return false;
    }

    @Override
    public boolean is(ResourceKey<DamageType> p_205712_) {
        return false;
    }

    @Override
    public boolean is(Predicate<ResourceKey<DamageType>> p_205711_) {
        return false;
    }

    @Override
    public boolean is(TagKey<DamageType> p_205705_) {
        return false;
    }

    @Override
    public Stream<TagKey<DamageType>> tags() {
        return Stream.empty();
    }

    @Override
    public Either<ResourceKey<DamageType>, DamageType> unwrap() {
        return null;
    }

    @Override
    public Optional<ResourceKey<DamageType>> unwrapKey() {
        return Optional.empty();
    }

    @Override
    public Kind kind() {
        return null;
    }

    @Override
    public boolean canSerializeIn(HolderOwner<DamageType> p_255833_) {
        return false;
    }
}
