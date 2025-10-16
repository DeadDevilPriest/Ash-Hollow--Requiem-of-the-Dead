package Ash_Hollow_Requiem.interfaces;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;


import static Ash_Hollow_Requiem.modregisters.ModItems.*;

public class ModCreativeTab {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, "ash_hollow_requiem_of_the_dead");

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
                        output.accept(BOUNTY_BOARD_ITEM.get());
                        output.accept(BOUNTY_BOOK.get());
                    })
                    .build()
    );
}
