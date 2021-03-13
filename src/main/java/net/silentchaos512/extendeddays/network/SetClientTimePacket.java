package net.silentchaos512.extendeddays.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import net.silentchaos512.extendeddays.client.ClientInfo;

import java.util.function.Supplier;

public class SetClientTimePacket {
    private final long vanillaTime;
    private final long trueTime;

    public SetClientTimePacket() {
        this(-1, -1);
    }

    public SetClientTimePacket(long vanillaTime, long trueTime) {
        this.vanillaTime = vanillaTime;
        this.trueTime = trueTime;
    }

    public static SetClientTimePacket decode(PacketBuffer buffer) {
        long vanillaTime = buffer.readVarLong();
        long trueTime = buffer.readVarLong();
        return new SetClientTimePacket(vanillaTime, trueTime);
    }

    public void encode(PacketBuffer buffer) {
        buffer.writeVarLong(this.vanillaTime);
        buffer.writeVarLong(this.trueTime);
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        ClientPlayerEntity player = Minecraft.getInstance().player;
        if (player != null) {
            if (player.world instanceof ClientWorld) {
                ClientWorld world = (ClientWorld) player.world;
                ClientWorld.ClientWorldInfo info = world.getWorldInfo();
                info.setGameTime(this.vanillaTime);

                updateClientInfo();
            }
        }
        context.get().setPacketHandled(true);
    }

    private void updateClientInfo() {
        ClientInfo.trueTime = this.trueTime;

        ClientWorld world = Minecraft.getInstance().world;
        if (world != null) {
            world.getWorldInfo().setGameTime(vanillaTime);
        }
    }
}
