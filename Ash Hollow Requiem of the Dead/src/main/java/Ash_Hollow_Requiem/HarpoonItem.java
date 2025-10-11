package Ash_Hollow_Requiem;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;

public class HarpoonItem extends Item implements GeoItem {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public HarpoonItem(Properties properties) {
        super(properties);
        System.out.println("[DEBUG] HarpoonItem constructed");
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        // Add a basic animation controller even if you don't have animations yet
        controllers.add(new AnimationController<>(this, "controller", 0, state -> {
            return state.setAndContinue(RawAnimation.begin());
        }));
        System.out.println("[DEBUG] HarpoonItem controllers registered");
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private HarpoonItemRenderer renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (this.renderer == null) {
                    this.renderer = new HarpoonItemRenderer();
                    System.out.println("[DEBUG] HarpoonItemRenderer created");
                }
                return this.renderer;
            }
        });
        System.out.println("[DEBUG] HarpoonItem client initialized");
    }
}