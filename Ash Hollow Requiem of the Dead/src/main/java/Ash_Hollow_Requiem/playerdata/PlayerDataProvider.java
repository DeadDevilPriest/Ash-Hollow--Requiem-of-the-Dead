package Ash_Hollow_Requiem.playerdata;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * STEP 2: The Provider Class
 *
 * This class is like a "box" that holds the PlayerData.
 * It tells Forge:
 * - "Hey, I have PlayerData attached to this player"
 * - "Here's how to save it to NBT"
 * - "Here's how to load it from NBT"
 *
 * Think of it like a USB port on a computer:
 * - The port (Provider) allows you to plug in a USB drive (PlayerData)
 * - Other code can access the USB drive through the port
 */
public class PlayerDataProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {

    // ========== THE CAPABILITY ========== //

    /**
     * This is the "type" of capability.
     * It's like saying "I have a PlayerData capability"
     *
     * Think of it like a key that opens a specific lock.
     * When you want to access PlayerData, you use this key.
     */
    public static Capability<PlayerData> PLAYER_DATA =
            CapabilityManager.get(new CapabilityToken<>() {});


    // ========== THE DATA INSTANCE ========== //

    /**
     * This is the actual PlayerData object that stores the data.
     * Each player entity will have its own instance.
     */
    private PlayerData data = new PlayerData();

    /**
     * LazyOptional is like a safe box:
     * - It might contain the data (if it exists)
     * - It might be empty (if something went wrong)
     *
     * It prevents null pointer errors and makes code safer.
     */
    private final LazyOptional<PlayerData> optional = LazyOptional.of(() -> data);


    // ========== CAPABILITY PROVIDER METHOD ========== //

    /**
     * This method is called when something asks:
     * "Hey player, do you have a PLAYER_DATA capability?"
     *
     * @param cap - The capability being requested (what type of data?)
     * @param side - Which side is asking (usually null for players)
     * @return The data if we have it, empty otherwise
     */
    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        // Check if they're asking for OUR capability (PLAYER_DATA)
        if (cap == PLAYER_DATA) {
            // Yes! Return our data (cast to their type T)
            return optional.cast();
        }

        // They're asking for something else we don't have
        return LazyOptional.empty();
    }


    // ========== NBT SERIALIZATION METHODS ========== //

    /**
     * SAVE: Called when the game needs to save this capability to disk
     *
     * This happens when:
     * - Player logs out
     * - Server saves
     * - Player dies (to copy to new entity)
     *
     * @return A CompoundTag containing all our data
     */
    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        // Ask our PlayerData to save itself
        data.saveNBTData(nbt);
        return nbt;
    }

    /**
     * LOAD: Called when the game needs to load this capability from disk
     *
     * This happens when:
     * - Player logs in
     * - Server loads
     * - Player respawns (data is copied)
     *
     * @param nbt The CompoundTag containing saved data
     */
    @Override
    public void deserializeNBT(CompoundTag nbt) {
        // Ask our PlayerData to load itself
        data.loadNBTData(nbt);
    }
}