package Ash_Hollow_Requiem;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

@SuppressWarnings("ALL")
public class HarpoonEntityModel<AnimationEvent> extends GeoModel<HarpoonEntity> {
    @Override
    public ResourceLocation getModelResource(HarpoonEntity object) {
        return new ResourceLocation("ash_hollow_requiem_of_the_dead", "geo/harpoon_entity.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(HarpoonEntity object) {
        // Fixed: Changed "texture" to "textures" (plural)
        return new ResourceLocation("ash_hollow_requiem_of_the_dead", "textures/item/harpoon.png");
    }

    @Override
    public ResourceLocation getAnimationResource(HarpoonEntity animatable) {
        return new ResourceLocation("ash_hollow_requiem_of_the_dead", "animations/harpoon_fly.animation.json");
    }
}