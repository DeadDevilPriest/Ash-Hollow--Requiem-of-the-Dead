package Ash_Hollow_Requiem;

import io.netty.util.Attribute;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(
            (ResourceLocation) ForgeRegistries.ITEMS,
        Ash_Hollow.MODID
    );

    // Add item registrations here
    public static final RegistryObject<Item> HarpoonStick = ITEMS.register(
        "Harpoonstick",
        () -> new Item(new Item.Properties()
                .stacksTo(1)
                .defaultDurability(250))
    );

    public static final RegistryObject<Item> Harpoon = ITEMS.register(
        "Harpoon",
        () -> new Item(new Item.Properties()
                .stacksTo(16)
                .defaultDurability(250))
    );

    //Register creative tab separately
    public static final DeferredRegister<net.minecraft.world.item.CreativeModeTab> CREATIVE_MODE_TABS =
        DeferredRegister.create(net.minecraft.core.registries.Registries.CREATIVE_MODE_TAB, Ash_Hollow.MODID);
}
