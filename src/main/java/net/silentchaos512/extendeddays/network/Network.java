package net.silentchaos512.extendeddays.network;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import net.silentchaos512.extendeddays.ExtendedDays;

import java.util.Objects;

public class Network {
    private static final ResourceLocation NAME = ExtendedDays.getId("network");
    private static final String VERSION = "exd-1";

    public static SimpleChannel channel;

    static {
        channel = NetworkRegistry.ChannelBuilder.named(NAME)
                .clientAcceptedVersions(s -> Objects.equals(s, VERSION))
                .serverAcceptedVersions(s -> Objects.equals(s, VERSION))
                .networkProtocolVersion(() -> VERSION)
                .simpleChannel();

        channel.messageBuilder(SetClientTimePacket.class, 1)
                .decoder(SetClientTimePacket::decode)
                .encoder(SetClientTimePacket::encode)
                .consumer(SetClientTimePacket::handle)
                .add();
    }

    private Network() {}

    public static void init() {}
}
