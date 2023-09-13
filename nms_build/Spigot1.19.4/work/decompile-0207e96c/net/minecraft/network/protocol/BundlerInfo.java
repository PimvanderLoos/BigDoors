package net.minecraft.network.protocol;

import io.netty.util.AttributeKey;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.network.PacketListener;

public interface BundlerInfo {

    AttributeKey<BundlerInfo.b> BUNDLER_PROVIDER = AttributeKey.valueOf("bundler");
    int BUNDLE_SIZE_LIMIT = 4096;
    BundlerInfo EMPTY = new BundlerInfo() {
        @Override
        public void unbundlePacket(Packet<?> packet, Consumer<Packet<?>> consumer) {
            consumer.accept(packet);
        }

        @Nullable
        @Override
        public BundlerInfo.a startPacketBundling(Packet<?> packet) {
            return null;
        }
    };

    static <T extends PacketListener, P extends BundlePacket<T>> BundlerInfo createForPacket(final Class<P> oclass, final Function<Iterable<Packet<T>>, P> function, final BundleDelimiterPacket<T> bundledelimiterpacket) {
        return new BundlerInfo() {
            @Override
            public void unbundlePacket(Packet<?> packet, Consumer<Packet<?>> consumer) {
                if (packet.getClass() == oclass) {
                    P p0 = (BundlePacket) packet;

                    consumer.accept(bundledelimiterpacket);
                    p0.subPackets().forEach(consumer);
                    consumer.accept(bundledelimiterpacket);
                } else {
                    consumer.accept(packet);
                }

            }

            @Nullable
            @Override
            public BundlerInfo.a startPacketBundling(Packet<?> packet) {
                return packet == bundledelimiterpacket ? new BundlerInfo.a() {
                    private final List<Packet<T>> bundlePackets = new ArrayList();

                    @Nullable
                    @Override
                    public Packet<?> addPacket(Packet<?> packet1) {
                        if (packet1 == bundledelimiterpacket) {
                            return (Packet) function.apply(this.bundlePackets);
                        } else if (this.bundlePackets.size() >= 4096) {
                            throw new IllegalStateException("Too many packets in a bundle");
                        } else {
                            this.bundlePackets.add(packet1);
                            return null;
                        }
                    }
                } : null;
            }
        };
    }

    void unbundlePacket(Packet<?> packet, Consumer<Packet<?>> consumer);

    @Nullable
    BundlerInfo.a startPacketBundling(Packet<?> packet);

    public interface b {

        BundlerInfo getBundlerInfo(EnumProtocolDirection enumprotocoldirection);
    }

    public interface a {

        @Nullable
        Packet<?> addPacket(Packet<?> packet);
    }
}
