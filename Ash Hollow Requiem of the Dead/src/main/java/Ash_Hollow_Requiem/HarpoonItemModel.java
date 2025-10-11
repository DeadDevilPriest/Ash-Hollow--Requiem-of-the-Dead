package Ash_Hollow_Requiem;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class HarpoonItemModel extends GeoModel<HarpoonItem> {
    @Override
    public ResourceLocation getModelResource(HarpoonItem object) {
        ResourceLocation loc = new ResourceLocation("ash_hollow_requiem_of_the_dead", "geo/harpoon.geo.json");
        System.out.println("[DEBUG] Loading model: " + loc);
        return loc;
    }

    @Override
    public ResourceLocation getTextureResource(HarpoonItem object) {
        ResourceLocation loc = new ResourceLocation("ash_hollow_requiem_of_the_dead", "textures/item/harpoon.png");
        System.out.println("[DEBUG] Loading texture: " + loc);
        return loc;
    }

    @Override
    public ResourceLocation getAnimationResource(HarpoonItem animatable) {
        return new ResourceLocation("ash_hollow_requiem_of_the_dead", "animations/harpoon_item.animation.json");
    }
}