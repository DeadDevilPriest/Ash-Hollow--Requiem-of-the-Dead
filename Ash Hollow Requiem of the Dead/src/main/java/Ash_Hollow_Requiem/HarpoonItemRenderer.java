package Ash_Hollow_Requiem;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class HarpoonItemRenderer extends GeoItemRenderer<HarpoonItem> {
    public HarpoonItemRenderer() {
        super(new HarpoonItemModel());
    }

    @Override
    public ResourceLocation getTextureLocation(HarpoonItem item) {
        // Fixed: Changed "texture" to "textures" (plural)
        return new ResourceLocation("ash_hollow_requiem_of_the_dead", "textures/item/harpoon.png");
    }
}