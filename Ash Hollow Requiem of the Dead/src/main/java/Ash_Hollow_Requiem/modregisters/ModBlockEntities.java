package Ash_Hollow_Requiem.modregisters;

import Ash_Hollow_Requiem.Ash_Hollow;
import Ash_Hollow_Requiem.blockentities.InkPressBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * Register BlockEntities for the mod
 */
public class ModBlockEntities {
        public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, Ash_Hollow.MODID);

        public static final RegistryObject<BlockEntityType<InkPressBlockEntity>> INK_PRESS =
                BLOCK_ENTITIES.register("ink_press", () ->
                        BlockEntityType.Builder.of(InkPressBlockEntity::new,
                                ModBlocks.INK_PRESS.get()).build(null));

        public static final RegisterObject<BlockEntityType<InkMixerBlockEntity>> INK_MIXER =
                BLOCK_ENTITIES.register("ink_mixer", () ->
                        BlockEntityType.Builder.of(InkMixerBlockEntity::new,
                                ModBlocks.INK_MIXER.get()).build(null));
}
