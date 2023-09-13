package net.minecraft.network;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.MessageToMessageDecoder;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.network.protocol.BundlerInfo;
import net.minecraft.network.protocol.EnumProtocolDirection;
import net.minecraft.network.protocol.Packet;

public class PacketBundlePacker extends MessageToMessageDecoder<Packet<?>> {

    @Nullable
    private BundlerInfo.a currentBundler;
    @Nullable
    private BundlerInfo infoForCurrentBundler;
    private final EnumProtocolDirection flow;

    public PacketBundlePacker(EnumProtocolDirection enumprotocoldirection) {
        this.flow = enumprotocoldirection;
    }

    protected void decode(ChannelHandlerContext channelhandlercontext, Packet<?> packet, List<Object> list) throws Exception {
        BundlerInfo.b bundlerinfo_b = (BundlerInfo.b) channelhandlercontext.channel().attr(BundlerInfo.BUNDLER_PROVIDER).get();

        if (bundlerinfo_b == null) {
            throw new DecoderException("Bundler not configured: " + packet);
        } else {
            BundlerInfo bundlerinfo = bundlerinfo_b.getBundlerInfo(this.flow);

            if (this.currentBundler != null) {
                if (this.infoForCurrentBundler != bundlerinfo) {
                    throw new DecoderException("Bundler handler changed during bundling");
                }

                Packet<?> packet1 = this.currentBundler.addPacket(packet);

                if (packet1 != null) {
                    this.infoForCurrentBundler = null;
                    this.currentBundler = null;
                    list.add(packet1);
                }
            } else {
                BundlerInfo.a bundlerinfo_a = bundlerinfo.startPacketBundling(packet);

                if (bundlerinfo_a != null) {
                    this.currentBundler = bundlerinfo_a;
                    this.infoForCurrentBundler = bundlerinfo;
                } else {
                    list.add(packet);
                }
            }

        }
    }
}
