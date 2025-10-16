package Ash_Hollow_Requiem.items.harpoon.item;

import Ash_Hollow_Requiem.damage.ModDamageSources;
import Ash_Hollow_Requiem.effects.ModEffects;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;

public class HarpoonItem extends Item implements GeoItem {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    // Define animations
    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation STAB = RawAnimation.begin().thenPlay("animation.harpoon.stab");

    public HarpoonItem(Properties properties) {
        super(properties);
        // Register as singleton so all instances share animation state
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, state -> {
            // Default to idle
            return PlayState.CONTINUE;
        })
                // Listen for the stab trigger
                .triggerableAnim("stab", STAB));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private final HarpoonItemRenderer renderer = new HarpoonItemRenderer();

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return this.renderer;
            }
        });
    }

    // Damage when hitting entities in melee
    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        // Trigger the stab animation on server side
        if (attacker.level() instanceof ServerLevel serverLevel) {
            triggerAnim(attacker, GeoItem.getOrAssignId(stack, serverLevel), "controller", "stab");
        }

        // Apply custom harpoon damage
        target.hurt(ModDamageSources.harpoonDamageSource(target.level(), attacker), 5.0F);

        // Apply bleeding effect
        target.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                ModEffects.BLEEDING.get(),
                100,
                0
        ));

        // Damage the item durability
        stack.hurtAndBreak(1, attacker, (entity) -> {
            entity.broadcastBreakEvent(attacker.getUsedItemHand());
        });

        return true;
    }
}