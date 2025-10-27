package Ash_Hollow_Requiem.playerdata;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * STEP 2: The Capability System
 *
 * This attaches PlayerData to players and handles saving/loading
 * Think of it as the "glue" between Minecraft and our custom data
 */
@Mod.EventBusSubscriber(modid = "ash_hollow_requiem_of_the_dead")
public class PlayerDataProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {

    // Create the capability (this is how Forge knows about our data)
    public static Capability<PlayerData> PLAYER_DATA = CapabilityManager.get(new CapabilityToken<>() {});

    // The actual data instance
    private PlayerData playerData = null;
    private final LazyOptional<PlayerData> optional = LazyOptional.of(this::createPlayerData);

    private PlayerData createPlayerData() {
        if (this.playerData == null) {
            this.playerData = new PlayerData();
        }
        return this.playerData;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == PLAYER_DATA) {
            return optional.cast();
        }
        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        createPlayerData().saveNBTData(nbt);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        createPlayerData().loadNBTData(nbt);
    }

    // ========== EVENTS ========== //

    /**
     * Attach our capability to every player when they join
     */
    @SubscribeEvent
    public static void onAttachCapabilitiesPlayer(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) {
            if (!event.getObject().getCapability(PLAYER_DATA).isPresent()) {
                event.addCapability(
                        new ResourceLocation("ash_hollow_requiem_of_the_dead", "player_data"),
                        new PlayerDataProvider()
                );
            }
        }
    }

    /**
     * When player dies, copy data to the new player entity
     */
    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (event.isWasDeath()) {
            // Copy data from old player to new player
            event.getOriginal().getCapability(PLAYER_DATA).ifPresent(oldStore -> {
                event.getEntity().getCapability(PLAYER_DATA).ifPresent(newStore -> {
                    newStore.copyFrom(oldStore);
                });
            });
        }
    }

    // ========== HELPER METHODS ========== //

    /**
     * Easy way to get PlayerData from a player
     */
    public static PlayerData getPlayerData(Player player) {
        return player.getCapability(PLAYER_DATA).orElse(null);
    }

    /**
     * Check if player has enough coins
     */
    public static boolean hasCoins(Player player, int amount) {
        PlayerData data = getPlayerData(player);
        return data != null && data.getCoins() >= amount;
    }

    /**
     * Check if player has enough tokens
     */
    public static boolean hasTokens(Player player, int amount) {
        PlayerData data = getPlayerData(player);
        return data != null && data.getTokens() >= amount;
    }

    /**
     * Check if player has enough rebirth tokens
     */
    public static boolean hasRebirthTokens(Player player, int amount) {
        PlayerData data = getPlayerData(player);
        return data != null && data.getTokens() >= amount;
    }

    /**
     * Check if player has enough skill points
     */
    public static boolean hasSkillPoints(Player player, int amount) {
        PlayerData data = getPlayerData(player);
        return data != null && data.getSkillPoints() >= amount;
    }

    /**
     * Check if player has a specific skill
     */
    public static boolean hasSkill(Player player, String skillId) {
        PlayerData data = getPlayerData(player);
        return data != null && data.hasSkill(skillId);
    }

    /**
     * Try to unlock a skill for the player
     */
    public static boolean unlockSkill(Player player, String skillId, int cost) {
        PlayerData data = getPlayerData(player);
        if (data != null && data.spendSkillPoints(cost)) {
            return data.unlockSkill(skillId);
        }
        return false;
    }
}