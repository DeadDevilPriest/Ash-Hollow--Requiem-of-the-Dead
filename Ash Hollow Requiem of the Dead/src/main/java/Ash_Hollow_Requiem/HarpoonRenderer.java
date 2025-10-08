package Ash_Hollow_Requiem;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import Ash_Hollow_Requiem.HarpoonEntity;
import Ash_Hollow_Requiem.HarpoonModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

@SuppressWarnings("ALL")
public class HarpoonRenderer extends GeoEntityRenderer<HarpoonEntity> {
    public HarpoonRenderer(EntityRendererProvider.Context context) {
        super(context, new HarpoonModel());
    }

    @Override
    public ResourceLocation getTextureLocation(HarpoonEntity entity) {
        return new ResourceLocation("ash_hollow_requiem_of_the_dead", "texture/harpoon.png");
    }
}
