package Ash_Hollow_Requiem.modregisters;

import Ash_Hollow_Requiem.Ash_Hollow;
import Ash_Hollow_Requiem.HarpoonStick;
import Ash_Hollow_Requiem.items.*;
import Ash_Hollow_Requiem.items.harpoon.item.HarpoonItem;
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

    // ✅ Ink Press Block Item
    public static final RegistryObject<Item> INK_PRESS = ITEMS.register(
            "ink_press",
            () -> new BlockItem(ModBlocks.INK_PRESS.get(), new Item.Properties()));

    public static final RegistryObject<Item> BOUNTY_BOOK = ITEMS.register(
            "bounty_book",
            () -> new BountyBookItem(new Item.Properties()
                    .stacksTo(1)
                    .rarity(net.minecraft.world.item.Rarity.UNCOMMON)) // ✅ Added rarity
    );

    public static final RegistryObject<Item> SOUL_FRAGMENT = ITEMS.register(
            "soul_fragment",
            () -> new SoulFragmentItem(new Item.Properties()
                    .stacksTo(64))
    );

    // ✅ Bounty Paper - Updated to use custom class
    public static final RegistryObject<Item> BOUNTY_PAPER = ITEMS.register(
            "bounty_paper",
            () -> new BountyPaperItem(new Item.Properties()
                    .stacksTo(64))
    );

    public static final RegistryObject<Item> HEAL_SYRINGE = ITEMS.register(
            "heal_syringe",
            () -> new HealSyringeItem(new Item.Properties()
                    .stacksTo(16))
    );

    public static final RegistryObject<Item> WEAK_HEAL_SYRINGE = ITEMS.register(
            "weak_heal_syringe",
            () -> new WeakHealSyringeItem(new Item.Properties()
                    .stacksTo(16))
    );

    public static final RegistryObject<Item> ANTIDOTE_SYRINGE = ITEMS.register(
            "antidote_syringe",
            () -> new AntidoteSyringeItem(new Item.Properties()
                    .stacksTo(16))
    );

    public static final RegistryObject<Item> WEAK_ANTIDOTE_SYRINGE = ITEMS.register(
            "weak_antidote_syringe",
            () -> new WeakAntidoteSyringeItem(new Item.Properties()
                    .stacksTo(16))
    );

    public static final RegistryObject<Item> REGENERATION_SYRINGE = ITEMS.register(
            "regeneration_syringe",
            () -> new RegenerationSyringeItem(new Item.Properties()
                    .stacksTo(16))
    );

    public static final RegistryObject<Item> WEAK_REGENERATION_SYRINGE = ITEMS.register(
            "weak_regeneration_syringe",
            () -> new WeakRegenerationSyringeItem(new Item.Properties()
                    .stacksTo(16))
    );

    public static final RegistryObject<Item> RAGE_SYRINGE = ITEMS.register(
            "rage_syringe",
            () -> new RageSyringeItem(new Item.Properties()
                    .stacksTo(16))
    );

    public static final RegistryObject<Item> WEAK_RAGE_SYRINGE = ITEMS.register(
            "weak_rage_syringe",
            () -> new WeakRageSyringeItem(new Item.Properties()
                    .stacksTo(16))
    );

    public static final RegistryObject<Item> THICK_SKIN_SYRINGE = ITEMS.register(
            "thick_skin_syringe",
            () -> new ThickSkinSyringeItem(new Item.Properties()
                    .stacksTo(16))
    );

    public static final RegistryObject<Item> WEAK_THICK_SKIN_SYRINGE = ITEMS.register(
            "weak_thick_skin_syringe",
            () -> new WeakThickSkinSyringeItem(new Item.Properties()
                    .stacksTo(16))
    );

    public static final RegistryObject<Item> SPEED_SYRINGE = ITEMS.register(
            "speed_syringe",
            () -> new SpeedSyringeItem(new Item.Properties()
                    .stacksTo(16))
    );

    public static final RegistryObject<Item> WEAK_SPEED_SYRINGE = ITEMS.register( // ✅ Fixed typo
            "weak_speed_syringe",
            () -> new WeakSpeedSyringeItem(new Item.Properties()
                    .stacksTo(16))
    );

    public static final RegistryObject<Item> INK_BOTTLE = ITEMS.register(
            "ink_bottle",
            () -> new InkBottleItem(new Item.Properties()
                    .stacksTo(16))
    );

    public static final RegistryObject<Item> EMPTY_SYRINGE = ITEMS.register(
            "empty_syringe",
            () -> new EmptySyringeItem(new Item.Properties()
                    .stacksTo(16))
    );

    public static final RegistryObject<Item> SOUL_INK_BOTTLE = ITEMS.register(
            "soul_ink_bottle",
            () -> new SoulInkBottleItem(new Item.Properties()
                    .stacksTo(16))
    );

    public static final RegistryObject <Item> INK_MIXER = ITEMS.register(
            "ink_mixer",
            () -> new BlockItem(ModBlocks.INK_MIXER.get(), new Item.Properties()
                    .stacksTo(1))
    );

    public static final RegistryObject <Item> EMPTY_INK_BOTTLE = ITEMS.register(
            "empty_ink_bottle",
            () -> new EmptyInkBottleItem(new Item.Properties()
                    .stacksTo(16))
    );
}