package Ash_Hollow_Requiem.items.harpoon.entity;

import Ash_Hollow_Requiem.modregisters.ModItems;
import Ash_Hollow_Requiem.damage.ModDamageSources;
import Ash_Hollow_Requiem.effects.ModEffects;
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
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class HarpoonEntity extends AbstractArrow implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    // Define the fly animation matching your JSON file
    private static final RawAnimation FLY_ANIM = RawAnimation.begin().thenLoop("animation.harpoon_entity.fly");

    public HarpoonEntity(EntityType<? extends HarpoonEntity> type, Level level) {
        super(type, level);
        // Make it drop faster for more realistic arc
        this.setNoGravity(false);
    }

    public HarpoonEntity(EntityType<? extends HarpoonEntity> type, LivingEntity shooter, Level level) {
        super(type, shooter, level);
        // Make it drop faster for more realistic arc
        this.setNoGravity(false);
    }

    @Override
    public void tick() {
        super.tick();

        // Make the harpoon face its flight direction
        if (!this.inGround) {
            // Calculate rotation based on velocity
            double dx = this.getDeltaMovement().x;
            double dy = this.getDeltaMovement().y;
            double dz = this.getDeltaMovement().z;

            // Set yaw (horizontal rotation) based on horizontal velocity
            this.setYRot((float)(Math.atan2(dx, dz) * (180.0 / Math.PI)));

            // Set pitch (vertical rotation) based on vertical velocity
            double horizontalDistance = Math.sqrt(dx * dx + dz * dz);
            this.setXRot((float)(Math.atan2(dy, horizontalDistance) * (180.0 / Math.PI)));
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        // Register the fly animation controller
        controllers.add(new AnimationController<>(this, "fly_controller", 0, state -> {
            // Only spin while flying, stop when stuck in ground
            if (!this.inGround) {
                return state.setAndContinue(FLY_ANIM);
            }
            // When stuck, return null to stop the animation
            return null;
        }));
    }

    @Override
    protected ItemStack getPickupItem() {
        return new ItemStack(ModItems.HARPOON.get());
    }

    @Override
    protected void onHitEntity(EntityHitResult hitResult) {
        super.onHitEntity(hitResult);
        Entity target = hitResult.getEntity();
        Entity owner = this.getOwner();

        if (!this.level().isClientSide && target instanceof LivingEntity livingTarget) {
            livingTarget.hurt(ModDamageSources.harpoonDamageSource(level(), (LivingEntity) owner), 13.0F);
            livingTarget.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                    ModEffects.BLEEDING.get(),
                    120, // 6 seconds
                    0
            ));
        }
    }

    @Override
    protected void tickDespawn() {
        if (this.inGroundTime > 600) { // 30 seconds
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
}