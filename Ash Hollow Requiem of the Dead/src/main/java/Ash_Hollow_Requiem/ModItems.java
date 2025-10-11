package Ash_Hollow_Requiem;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(
            ForgeRegistries.ITEMS,
            Ash_Hollow.MODID
    );

    // Register items with custom classes
    public static final RegistryObject<Item> HARPOON_STICK = ITEMS.register(
            "harpoonstick",
            () -> new HarpoonStick(new Item.Properties()
                    .stacksTo(1)
                    .durability(250))
    );

    public static final RegistryObject<Item> HARPOON = ITEMS.register(
            "harpoon",
            () -> new HarpoonItem(new Item.Properties()
                    .stacksTo(16))
    );

    // Register creative tab
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Ash_Hollow.MODID);

    public static final RegistryObject<CreativeModeTab> ASH_HOLLOW_TAB = CREATIVE_MODE_TABS.register(
            "ash_hollow_tab",
            () -> CreativeModeTab.builder()
                    // Set the icon (the item shown on the tab)
                    .icon(() -> new ItemStack(HARPOON.get()))
                    // Set the title
                    .title(Component.translatable("Ash Hollow: Requiem of the Dead"))
                    // Add items to the tab
                    .displayItems((parameters, output) -> {
                        output.accept(HARPOON_STICK.get());
                        output.accept(HARPOON.get());
                    })
                    .build()
    );
}