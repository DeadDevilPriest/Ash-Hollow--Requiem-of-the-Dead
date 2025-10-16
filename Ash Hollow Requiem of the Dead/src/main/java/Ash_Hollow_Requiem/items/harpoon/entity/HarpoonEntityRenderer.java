package Ash_Hollow_Requiem.items.harpoon.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

@SuppressWarnings("ALL")
public class HarpoonEntityRenderer extends GeoEntityRenderer<HarpoonEntity> {
    public HarpoonEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new HarpoonEntityModel());
    }

    @Override
    public ResourceLocation getTextureLocation(HarpoonEntity entity) {
        return ResourceLocation.parse("ash_hollow_requiem_of_the_dead:textures/item/harpoon.png");
    }
}