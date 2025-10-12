package Ash_Hollow_Requiem.harpoon.item;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class HarpoonItemModel extends GeoModel<HarpoonItem> {
    @Override
    public ResourceLocation getModelResource(HarpoonItem object) {
        ResourceLocation loc = ResourceLocation.fromNamespaceAndPath("ash_hollow_requiem_of_the_dead", "geo/harpoon.geo.json");
        return loc;
    }

    @Override
    public ResourceLocation getTextureResource(HarpoonItem object) {
        ResourceLocation loc = ResourceLocation.fromNamespaceAndPath("ash_hollow_requiem_of_the_dead", "textures/item/harpoon.png");
        return loc;
    }

    @Override
    public ResourceLocation getAnimationResource(HarpoonItem animatable) {
        return ResourceLocation.fromNamespaceAndPath("ash_hollow_requiem_of_the_dead", "animations/harpoon_item.animation.json");
    }
}