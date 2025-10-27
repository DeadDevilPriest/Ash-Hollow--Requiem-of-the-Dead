package Ash_Hollow_Requiem.model;

import Ash_Hollow_Requiem.blockentities.InkPressBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

/**
 * Model definition for the Ink Press Block Entity
 *
 * Note: Texture switching is handled in the renderer based on powered state:
 * - OFF (powered=false): Uses ink_press_static.png
 * - ON (powered=true): Uses ink_press_animated.png
 */
public class InkPressModel extends GeoModel<InkPressBlockEntity> {

    @Override
    public ResourceLocation getModelResource(InkPressBlockEntity animatable) {
        return new ResourceLocation("ash_hollow_requiem_of_the_dead",
                "geo/ink_press.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(InkPressBlockEntity animatable) {
        // Default texture (overridden by renderer for dynamic switching)
        return new ResourceLocation("ash_hollow_requiem_of_the_dead",
                "textures/block/ink_press_static.png");
    }

    @Override
    public ResourceLocation getAnimationResource(InkPressBlockEntity animatable) {
        return new ResourceLocation("ash_hollow_requiem_of_the_dead",
                "animations/ink_press.animation.json");
    }
}
