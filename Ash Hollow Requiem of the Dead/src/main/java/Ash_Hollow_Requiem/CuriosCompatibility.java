package Ash_Hollow_Requiem;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.items.IItemHandler;

public class CuriosCompatibility {

    public static final EntityCapability<IItemHandler, Void> CURIOS_INVENTORY =
            EntityCapability.createVoid(ResourceLocation.fromNamespaceAndPath("curios", "item_handler"), IItemHandler.class);
}
