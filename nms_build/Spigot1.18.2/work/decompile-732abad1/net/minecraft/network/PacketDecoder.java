package net.minecraft.network;

import com.mojang.logging.LogUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.io.IOException;
import java.util.List;
import net.minecraft.network.protocol.EnumProtocolDirection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.util.profiling.jfr.JvmProfiler;
import org.slf4j.Logger;

public class PacketDecoder extends ByteToMessageDecoder {

    private static final Logger LOGGER = LogUtils.getLogger();
    private final EnumProtocolDirection flow;

    public PacketDecoder(EnumProtocolDirection enumprotocoldirection) {
        this.flow = enumprotocoldirection;
    }

    protected void decode(ChannelHandlerContext channelhandlercontext, ByteBuf bytebuf, List<Object> list) throws Exception {
        int i = bytebuf.readableBytes();

        if (i != 0) {
            PacketDataSerializer packetdataserializer = new PacketDataSerializer(bytebuf);
            int j = packetdataserializer.readVarInt();
            Packet<?> packet = ((EnumProtocol) channelhandlercontext.channel().attr(NetworkManager.ATTRIBUTE_PROTOCOL).get()).createPacket(this.flow, j, packetdataserializer);

            if (packet == null) {
                throw new IOException("Bad packet id " + j);
            } else {
                int k = ((EnumProtocol) channelhandlercontext.channel().attr(NetworkManager.ATTRIBUTE_PROTOCOL).get()).getId();

                JvmProfiler.INSTANCE.onPacketReceived(k, j, channelhandlercontext.channel().remoteAddress(), i);
                if (packetdataserializer.readableBytes() > 0) {
                    int l = ((EnumProtocol) channelhandlercontext.channel().attr(NetworkManager.ATTRIBUTE_PROTOCOL).get()).getId();

                    throw new IOException("Packet " + l + "/" + j + " (" + packet.getClass().getSimpleName() + ") was larger than I expected, found " + packetdataserializer.readableBytes() + " bytes extra whilst reading packet " + j);
                } else {
                    list.add(packet);
                    if (PacketDecoder.LOGGER.isDebugEnabled()) {
                        PacketDecoder.LOGGER.debug(NetworkManager.PACKET_RECEIVED_MARKER, " IN: [{}:{}] {}", new Object[]{channelhandlercontext.channel().attr(NetworkManager.ATTRIBUTE_PROTOCOL).get(), j, packet.getClass().getName()});
                    }

                }
            }
        }
    }
}
