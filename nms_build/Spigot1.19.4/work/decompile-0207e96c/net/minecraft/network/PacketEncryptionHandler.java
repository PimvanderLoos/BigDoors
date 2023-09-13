package net.minecraft.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import javax.crypto.Cipher;
import javax.crypto.ShortBufferException;

public class PacketEncryptionHandler {

    private final Cipher cipher;
    private byte[] heapIn = new byte[0];
    private byte[] heapOut = new byte[0];

    protected PacketEncryptionHandler(Cipher cipher) {
        this.cipher = cipher;
    }

    private byte[] bufToByte(ByteBuf bytebuf) {
        int i = bytebuf.readableBytes();

        if (this.heapIn.length < i) {
            this.heapIn = new byte[i];
        }

        bytebuf.readBytes(this.heapIn, 0, i);
        return this.heapIn;
    }

    protected ByteBuf decipher(ChannelHandlerContext channelhandlercontext, ByteBuf bytebuf) throws ShortBufferException {
        int i = bytebuf.readableBytes();
        byte[] abyte = this.bufToByte(bytebuf);
        ByteBuf bytebuf1 = channelhandlercontext.alloc().heapBuffer(this.cipher.getOutputSize(i));

        bytebuf1.writerIndex(this.cipher.update(abyte, 0, i, bytebuf1.array(), bytebuf1.arrayOffset()));
        return bytebuf1;
    }

    protected void encipher(ByteBuf bytebuf, ByteBuf bytebuf1) throws ShortBufferException {
        int i = bytebuf.readableBytes();
        byte[] abyte = this.bufToByte(bytebuf);
        int j = this.cipher.getOutputSize(i);

        if (this.heapOut.length < j) {
            this.heapOut = new byte[j];
        }

        bytebuf1.writeBytes(this.heapOut, 0, this.cipher.update(abyte, 0, i, this.heapOut));
    }
}
