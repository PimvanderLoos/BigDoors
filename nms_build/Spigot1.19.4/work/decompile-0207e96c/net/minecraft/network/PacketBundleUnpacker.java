package net.minecraft.network;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.EncoderException;
import io.netty.handler.codec.MessageToMessageEncoder;
import java.util.List;
import java.util.Objects;
import net.minecraft.network.protocol.BundlerInfo;
import net.minecraft.network.protocol.EnumProtocolDirection;
import net.minecraft.network.protocol.Packet;

public class PacketBundleUnpacker extends MessageToMessageEncoder<Packet<?>> {

    private final EnumProtocolDirection flow;

    public PacketBundleUnpacker(EnumProtocolDirection enumprotocoldirection) {
        this.flow = enumprotocoldirection;
    }

    protected void encode(ChannelHandlerContext channelhandlercontext, Packet<?> packet, List<Object> list) throws Exception {
        BundlerInfo.b bundlerinfo_b = (BundlerInfo.b) channelhandlercontext.channel().attr(BundlerInfo.BUNDLER_PROVIDER).get();

        if (bundlerinfo_b == null) {
            throw new EncoderException("Bundler not configured: " + packet);
        } else {
            BundlerInfo bundlerinfo = bundlerinfo_b.getBundlerInfo(this.flow);

            Objects.requireNonNull(list);
            bundlerinfo.unbundlePacket(packet, list::add);
        }
    }
}
