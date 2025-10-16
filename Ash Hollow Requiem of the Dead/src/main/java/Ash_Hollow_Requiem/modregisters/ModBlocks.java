package Ash_Hollow_Requiem.modregisters;

import Ash_Hollow_Requiem.Ash_Hollow;
import Ash_Hollow_Requiem.blocks.InkMixerBlock;
import Ash_Hollow_Requiem.blocks.InkPressBlock;
import Ash_Hollow_Requiem.bountyboard.BountyBoardBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(
            ForgeRegistries.BLOCKS,
            Ash_Hollow.MODID);

    public static final RegistryObject<Block> BOUNTY_BOARD = BLOCKS.register(
            "bounty_board",
            () -> new BountyBoardBlock(Block.Properties.of()
                    .strength(2.0f)
                    .noOcclusion()
                    .isRedstoneConductor((state, level, pos) -> false))
    );

    public static final RegistryObject<Block> INK_PRESS = BLOCKS.register(
            "ink_press",
            () -> new InkPressBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE)
                    .strength(2.5f, 3.5f)
                    .sound(SoundType.STONE)
                    .requiresCorrectToolForDrops())
    );

    // ✅ NEW: Ink Mixer Block
    public static final RegistryObject<Block> INK_MIXER = BLOCKS.register(
            "ink_mixer",
            () -> new InkMixerBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .strength(3.0f, 4.0f)
                    .sound(SoundType.METAL)
                    .requiresCorrectToolForDrops())
    );
}
