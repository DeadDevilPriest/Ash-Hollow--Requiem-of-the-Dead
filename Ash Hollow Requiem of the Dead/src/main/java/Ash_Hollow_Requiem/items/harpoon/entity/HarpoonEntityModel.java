package Ash_Hollow_Requiem.items.harpoon.entity;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

@SuppressWarnings("ALL")
public class HarpoonEntityModel<AnimationEvent> extends GeoModel<HarpoonEntity> {
    @Override
    public ResourceLocation getModelResource(HarpoonEntity object) {
        return ResourceLocation.fromNamespaceAndPath("ash_hollow_requiem_of_the_dead", "geo/harpoon_entity.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(HarpoonEntity object) {
        return ResourceLocation.fromNamespaceAndPath("ash_hollow_requiem_of_the_dead", "textures/item/harpoon.png");
    }

    @Override
    public ResourceLocation getAnimationResource(HarpoonEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath("ash_hollow_requiem_of_the_dead", "animations/harpoon_entity.animation.json");
    }
}