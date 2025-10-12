package Ash_Hollow_Requiem;

import Ash_Hollow_Requiem.harpoon.item.HarpoonItem;
import Ash_Hollow_Requiem.interfaces.ModCreativeTab;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
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

    public static final RegistryObject<Item> BOUNTY_BOARD_ITEM = ITEMS.register("bounty_board",
            () -> new BlockItem(ModBlocks.BOUNTY_BOARD.get(), new Item.Properties()));

}