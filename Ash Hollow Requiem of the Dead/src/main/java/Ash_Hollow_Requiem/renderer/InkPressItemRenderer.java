package Ash_Hollow_Requiem.renderer;

import Ash_Hollow_Requiem.items.InkPressBlockItem;
import Ash_Hollow_Requiem.model.InkPressItemModel;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoItemRenderer;

/**
 * Renderer for Ink Press when held as an item
 * Shows the static texture since items can't be "powered"
 */
public class InkPressItemRenderer extends GeoItemRenderer<InkPressBlockItem> {

    public InkPressItemRenderer() {
        super(new InkPressItemModel());
    }

    @Override
    public ResourceLocation getTextureLocation(InkPressBlockItem item) {
        // Items always use static texture (can't be powered)
        return new ResourceLocation("ash_hollow_requiem_of_the_dead",
                "textures/block/ink_press_static.png");
    }
}