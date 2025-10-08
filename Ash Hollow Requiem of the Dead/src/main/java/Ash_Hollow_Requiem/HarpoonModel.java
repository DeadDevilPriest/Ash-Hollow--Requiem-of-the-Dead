package Ash_Hollow_Requiem;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

@SuppressWarnings("ALL")
public class HarpoonModel<AnimationEvent> extends GeoModel<HarpoonEntity> {
    @Override
    public ResourceLocation getModelResource(HarpoonEntity object) {
        return new ResourceLocation("ash_hollow_requiem_of_the_dead","geo/harpoon.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(HarpoonEntity object) {
        return new ResourceLocation("ash_hollow_requiem_of_the_dead","texture/harpoon.png");
    }

    @Override
    public ResourceLocation getAnimationResource(HarpoonEntity animatable) {
        return new ResourceLocation("ash_hollow_requiem_of_the_dead","animations/harpoon.animation.json");
    }
}
