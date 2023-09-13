package net.minecraft.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.DecoderException;
import java.util.List;
import java.util.zip.Inflater;

public class PacketDecompressor extends ByteToMessageDecoder {

    public static final int MAXIMUM_COMPRESSED_LENGTH = 2097152;
    public static final int MAXIMUM_UNCOMPRESSED_LENGTH = 8388608;
    private final Inflater inflater;
    private int threshold;
    private boolean validateDecompressed;

    public PacketDecompressor(int i, boolean flag) {
        this.threshold = i;
        this.validateDecompressed = flag;
        this.inflater = new Inflater();
    }

    protected void decode(ChannelHandlerContext channelhandlercontext, ByteBuf bytebuf, List<Object> list) throws Exception {
        if (bytebuf.readableBytes() != 0) {
            PacketDataSerializer packetdataserializer = new PacketDataSerializer(bytebuf);
            int i = packetdataserializer.readVarInt();

            if (i == 0) {
                list.add(packetdataserializer.readBytes(packetdataserializer.readableBytes()));
            } else {
                if (this.validateDecompressed) {
                    if (i < this.threshold) {
                        throw new DecoderException("Badly compressed packet - size of " + i + " is below server threshold of " + this.threshold);
                    }

                    if (i > 8388608) {
                        throw new DecoderException("Badly compressed packet - size of " + i + " is larger than protocol maximum of 8388608");
                    }
                }

                byte[] abyte = new byte[packetdataserializer.readableBytes()];

                packetdataserializer.readBytes(abyte);
                this.inflater.setInput(abyte);
                byte[] abyte1 = new byte[i];

                this.inflater.inflate(abyte1);
                list.add(Unpooled.wrappedBuffer(abyte1));
                this.inflater.reset();
            }
        }
    }

    public void setThreshold(int i, boolean flag) {
        this.threshold = i;
        this.validateDecompressed = flag;
    }
}
