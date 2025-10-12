package Ash_Hollow_Requiem.harpoon.item;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class HarpoonItemRenderer extends GeoItemRenderer<HarpoonItem> {
    public HarpoonItemRenderer() {
        super(new HarpoonItemModel());
    }

    @Override
    public ResourceLocation getTextureLocation(HarpoonItem item) {
        return ResourceLocation.fromNamespaceAndPath("ash_hollow_requiem_of_the_dead", "textures/item/harpoon.png");
    }
}