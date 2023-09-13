package net.minecraft.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import java.util.List;
import javax.crypto.Cipher;

public class PacketDecrypter extends MessageToMessageDecoder<ByteBuf> {

    private final PacketEncryptionHandler cipher;

    public PacketDecrypter(Cipher cipher) {
        this.cipher = new PacketEncryptionHandler(cipher);
    }

    protected void decode(ChannelHandlerContext channelhandlercontext, ByteBuf bytebuf, List<Object> list) throws Exception {
        list.add(this.cipher.decipher(channelhandlercontext, bytebuf));
    }
}
