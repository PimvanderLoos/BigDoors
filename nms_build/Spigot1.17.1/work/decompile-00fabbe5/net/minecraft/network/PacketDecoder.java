package net.minecraft.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.io.IOException;
import java.util.List;
import net.minecraft.network.protocol.EnumProtocolDirection;
import net.minecraft.network.protocol.Packet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public class PacketDecoder extends ByteToMessageDecoder {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Marker MARKER = MarkerManager.getMarker("PACKET_RECEIVED", NetworkManager.PACKET_MARKER);
    private final EnumProtocolDirection flow;

    public PacketDecoder(EnumProtocolDirection enumprotocoldirection) {
        this.flow = enumprotocoldirection;
    }

    protected void decode(ChannelHandlerContext channelhandlercontext, ByteBuf bytebuf, List<Object> list) throws Exception {
        if (bytebuf.readableBytes() != 0) {
            PacketDataSerializer packetdataserializer = new PacketDataSerializer(bytebuf);
            int i = packetdataserializer.j();
            Packet<?> packet = ((EnumProtocol) channelhandlercontext.channel().attr(NetworkManager.ATTRIBUTE_PROTOCOL).get()).a(this.flow, i, packetdataserializer);

            if (packet == null) {
                throw new IOException("Bad packet id " + i);
            } else if (packetdataserializer.readableBytes() > 0) {
                int j = ((EnumProtocol) channelhandlercontext.channel().attr(NetworkManager.ATTRIBUTE_PROTOCOL).get()).a();

                throw new IOException("Packet " + j + "/" + i + " (" + packet.getClass().getSimpleName() + ") was larger than I expected, found " + packetdataserializer.readableBytes() + " bytes extra whilst reading packet " + i);
            } else {
                list.add(packet);
                if (PacketDecoder.LOGGER.isDebugEnabled()) {
                    PacketDecoder.LOGGER.debug(PacketDecoder.MARKER, " IN: [{}:{}] {}", channelhandlercontext.channel().attr(NetworkManager.ATTRIBUTE_PROTOCOL).get(), i, packet.getClass().getName());
                }

            }
        }
    }
}
