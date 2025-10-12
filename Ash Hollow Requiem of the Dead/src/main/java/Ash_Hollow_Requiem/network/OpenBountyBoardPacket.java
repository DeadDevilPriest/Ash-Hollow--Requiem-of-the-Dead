package Ash_Hollow_Requiem.network;

import Ash_Hollow_Requiem.BountyBoardScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class OpenBountyBoardPacket {
    private final int coins;
    private final int tokens;

    public OpenBountyBoardPacket(int coins, int tokens) {
        this.coins = coins;
        this.tokens = tokens;
    }

    public OpenBountyBoardPacket(FriendlyByteBuf buf) {
        this.coins = buf.readInt();
        this.tokens = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(coins);
        buf.writeInt(tokens);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> openClientScreen(coins, tokens));
        ctx.get().setPacketHandled(true);
    }

    @OnlyIn(Dist.CLIENT)
    private static void openClientScreen(int coins, int tokens) {
        Minecraft.getInstance().setScreen(new BountyBoardScreen(coins, tokens));
    }
}
