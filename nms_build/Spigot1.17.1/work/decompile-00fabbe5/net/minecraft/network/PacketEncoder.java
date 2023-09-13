package net.minecraft.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import java.io.IOException;
import net.minecraft.network.protocol.EnumProtocolDirection;
import net.minecraft.network.protocol.Packet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public class PacketEncoder extends MessageToByteEncoder<Packet<?>> {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Marker MARKER = MarkerManager.getMarker("PACKET_SENT", NetworkManager.PACKET_MARKER);
    private final EnumProtocolDirection flow;

    public PacketEncoder(EnumProtocolDirection enumprotocoldirection) {
        this.flow = enumprotocoldirection;
    }

    protected void encode(ChannelHandlerContext channelhandlercontext, Packet<?> packet, ByteBuf bytebuf) throws Exception {
        EnumProtocol enumprotocol = (EnumProtocol) channelhandlercontext.channel().attr(NetworkManager.ATTRIBUTE_PROTOCOL).get();

        if (enumprotocol == null) {
            throw new RuntimeException("ConnectionProtocol unknown: " + packet);
        } else {
            Integer integer = enumprotocol.a(this.flow, packet);

            if (PacketEncoder.LOGGER.isDebugEnabled()) {
                PacketEncoder.LOGGER.debug(PacketEncoder.MARKER, "OUT: [{}:{}] {}", channelhandlercontext.channel().attr(NetworkManager.ATTRIBUTE_PROTOCOL).get(), integer, packet.getClass().getName());
            }

            if (integer == null) {
                throw new IOException("Can't serialize unregistered packet");
            } else {
                PacketDataSerializer packetdataserializer = new PacketDataSerializer(bytebuf);

                packetdataserializer.d(integer);

                try {
                    int i = packetdataserializer.writerIndex();

                    packet.a(packetdataserializer);
                    int j = packetdataserializer.writerIndex() - i;

                    if (j > 8388608) {
                        throw new IllegalArgumentException("Packet too big (is " + j + ", should be less than 8388608): " + packet);
                    }
                } catch (Throwable throwable) {
                    PacketEncoder.LOGGER.error(throwable);
                    if (packet.a()) {
                        throw new SkipEncodeException(throwable);
                    } else {
                        throw throwable;
                    }
                }
            }
        }
    }
}
