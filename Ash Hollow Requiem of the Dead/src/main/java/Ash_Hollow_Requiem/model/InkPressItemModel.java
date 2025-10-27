package Ash_Hollow_Requiem.model;

import Ash_Hollow_Requiem.items.InkPressBlockItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

/**
 * Model definition for Ink Press when displayed as an item
 * Uses the same geometry and texture as the block
 */
public class InkPressItemModel extends GeoModel<InkPressBlockItem> {

    @Override
    public ResourceLocation getModelResource(InkPressBlockItem animatable) {
        return new ResourceLocation("ash_hollow_requiem_of_the_dead",
                "geo/ink_press.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(InkPressBlockItem animatable) {
        // Items use static texture (never animated)
        return new ResourceLocation("ash_hollow_requiem_of_the_dead",
                "textures/block/ink_press_static.png");
    }

    @Override
    public ResourceLocation getAnimationResource(InkPressBlockItem animatable) {
        return new ResourceLocation("ash_hollow_requiem_of_the_dead",
                "animations/ink_press.animation.json");
    }
}