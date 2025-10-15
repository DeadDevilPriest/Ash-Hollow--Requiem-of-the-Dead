package Ash_Hollow_Requiem.modregisters;

import Ash_Hollow_Requiem.Ash_Hollow;
import Ash_Hollow_Requiem.bountyboard.BountyBoardBlock;
import net.minecraft.world.level.block.Block;
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
}
