package Ash_Hollow_Requiem.blockentities;

import Ash_Hollow_Requiem.modregisters.ModBlockEntities;
import Ash_Hollow_Requiem.modregisters.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

/**
 * Ink Press BlockEntity - Automated bounty paper creation machine
 *
 * Features:
 * - Stores paper and soul ink
 * - Processes materials into bounty papers
 * - Animated belt and pressing mechanism
 * - Auto-processing when powered and materials available
 */
public class InkPressBlockEntity extends BlockEntity implements GeoBlockEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    // Animations
    private static final RawAnimation TURN_ON = RawAnimation.begin().thenPlayAndHold("animation.ink_press.turn_on");
    private static final RawAnimation TURN_OFF = RawAnimation.begin().thenPlayAndHold("animation.ink_press.turn_off");
    private static final RawAnimation WORKING = RawAnimation.begin().thenLoop("animation.ink_press.press");

    // Inventory
    private int paperCount = 0;
    private int inkCount = 0;
    private int outputCount = 0;

    // Processing
    private boolean isPowered = false;
    private int processingTicks = 0;
    private static final int PROCESSING_TIME = 60; // 3 seconds (20 ticks per second)
    private static final int PAPER_PER_CRAFT = 4;
    private static final int INK_PER_CRAFT = 1;
    private static final int OUTPUT_PER_CRAFT = 4;

    // Capacity limits
    private static final int MAX_PAPER = 64;
    private static final int MAX_INK = 16;
    private static final int MAX_OUTPUT = 64;

    public InkPressBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.INK_PRESS.get(), pos, state);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, state -> {
            if (isPowered && isProcessing()) {
                state.getController().setAnimation(WORKING);
            } else {
                state.getController().setAnimation(TURN_ON);
            }
            return PlayState.CONTINUE;
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    /**
     * Main tick method - called every game tick
     */
    public void tick() {
        if (level == null || level.isClientSide) {
            return;
        }

        // Only process if powered and has materials
        if (isPowered && canProcess()) {
            processingTicks++;

            if (processingTicks >= PROCESSING_TIME) {
                // Complete the crafting
                processCraft();
                processingTicks = 0;
            }
        } else {
            // Reset processing if conditions not met
            processingTicks = 0;
        }

        // Mark dirty to save changes
        setChanged();
    }

    /**
     * Check if machine can process materials
     */
    private boolean canProcess() {
        return paperCount >= PAPER_PER_CRAFT
                && inkCount >= INK_PER_CRAFT
                && outputCount < MAX_OUTPUT;
    }

    /**
     * Check if currently processing
     */
    private boolean isProcessing() {
        return processingTicks > 0;
    }

    /**
     * Process materials into bounty paper
     */
    private void processCraft() {
        if (!canProcess()) {
            return;
        }

        // Consume materials
        paperCount -= PAPER_PER_CRAFT;
        inkCount -= INK_PER_CRAFT;

        // Add output
        outputCount += OUTPUT_PER_CRAFT;

        setChanged();
    }

    // ========== PUBLIC METHODS FOR BLOCK INTERACTION ==========

    public void turnOn() {
        this.isPowered = true;
        setChanged();
    }

    public void turnOff() {
        this.isPowered = false;
        this.processingTicks = 0;
        setChanged();
    }

    public void addPaper(int amount) {
        this.paperCount = Math.min(paperCount + amount, MAX_PAPER);
        setChanged();
    }

    public void addInk(int amount) {
        this.inkCount = Math.min(inkCount + amount, MAX_INK);
        setChanged();
    }

    public int getPaperCount() {
        return paperCount;
    }

    public int getInkCount() {
        return inkCount;
    }

    public int getOutputCount() {
        return outputCount;
    }

    public boolean isPowered() {
        return isPowered;
    }

    public void clearOutput() {
        this.outputCount = 0;
        setChanged();
    }

    public float getProcessingProgress() {
        return processingTicks / (float) PROCESSING_TIME;
    }

    /**
     * Drop all contents when block is broken
     */
    public void dropContents(Level level, BlockPos pos) {
        // Drop paper
        if (paperCount > 0) {
            Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(),
                    new ItemStack(net.minecraft.world.item.Items.PAPER, paperCount));
        }

        // Drop soul ink
        if (inkCount > 0) {
            Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(),
                    new ItemStack(ModItems.SOUL_INK_BOTTLE.get(), inkCount));
        }

        // Drop bounty papers
        if (outputCount > 0) {
            Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(),
                    new ItemStack(ModItems.BOUNTY_PAPER.get(), outputCount));
        }
    }

    // ========== NBT SAVE/LOAD ==========

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("PaperCount", paperCount);
        tag.putInt("InkCount", inkCount);
        tag.putInt("OutputCount", outputCount);
        tag.putBoolean("Powered", isPowered);
        tag.putInt("ProcessingTicks", processingTicks);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        this.paperCount = tag.getInt("PaperCount");
        this.inkCount = tag.getInt("InkCount");
        this.outputCount = tag.getInt("OutputCount");
        this.isPowered = tag.getBoolean("Powered");
        this.processingTicks = tag.getInt("ProcessingTicks");
    }
}