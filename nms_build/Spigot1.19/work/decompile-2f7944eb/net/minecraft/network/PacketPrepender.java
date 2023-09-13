package net.minecraft.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

@Sharable
public class PacketPrepender extends MessageToByteEncoder<ByteBuf> {

    private static final int MAX_BYTES = 3;

    public PacketPrepender() {}

    protected void encode(ChannelHandlerContext channelhandlercontext, ByteBuf bytebuf, ByteBuf bytebuf1) {
        int i = bytebuf.readableBytes();
        int j = PacketDataSerializer.getVarIntSize(i);

        if (j > 3) {
            throw new IllegalArgumentException("unable to fit " + i + " into 3");
        } else {
            PacketDataSerializer packetdataserializer = new PacketDataSerializer(bytebuf1);

            packetdataserializer.ensureWritable(j + i);
            packetdataserializer.writeVarInt(i);
            packetdataserializer.writeBytes(bytebuf, bytebuf.readerIndex(), i);
        }
    }
}
