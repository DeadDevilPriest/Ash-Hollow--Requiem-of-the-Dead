package Ash_Hollow_Requiem.renderer;

import Ash_Hollow_Requiem.blockentities.InkPressBlockEntity;
import Ash_Hollow_Requiem.model.InkPressModel;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

/**
 * Renderer for the Ink Press Block Entity
 * Switches between static and animated textures based on powered state
 */
public class InkPressRenderer extends GeoBlockRenderer<InkPressBlockEntity> {

    private static final ResourceLocation STATIC_TEXTURE = new ResourceLocation(
            "ash_hollow_requiem_of_the_dead",
            "textures/block/ink_press_static.png"
    );

    private static final ResourceLocation ANIMATED_TEXTURE = new ResourceLocation(
            "ash_hollow_requiem_of_the_dead",
            "textures/block/ink_press_animated.png"
    );

    public InkPressRenderer(BlockEntityRendererProvider.Context context) {
        super(new InkPressModel());
    }

    @Override
    public ResourceLocation getTextureLocation(InkPressBlockEntity blockEntity) {
        // Switch texture based on powered state
        return blockEntity.isPowered() ? ANIMATED_TEXTURE : STATIC_TEXTURE;
    }
}
