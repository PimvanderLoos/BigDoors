package net.minecraft.util.profiling.jfr.stats;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import jdk.jfr.consumer.RecordedEvent;
import net.minecraft.network.EnumProtocol;
import net.minecraft.network.protocol.EnumProtocolDirection;
import net.minecraft.network.protocol.Packet;

public final class NetworkPacketSummary {

    private final NetworkPacketSummary.a totalPacketCountAndSize;
    private final List<Pair<NetworkPacketSummary.b, NetworkPacketSummary.a>> largestSizeContributors;
    private final Duration recordingDuration;

    public NetworkPacketSummary(Duration duration, List<Pair<NetworkPacketSummary.b, NetworkPacketSummary.a>> list) {
        this.recordingDuration = duration;
        this.totalPacketCountAndSize = (NetworkPacketSummary.a) list.stream().map(Pair::getSecond).reduce(NetworkPacketSummary.a::add).orElseGet(() -> {
            return new NetworkPacketSummary.a(0L, 0L);
        });
        this.largestSizeContributors = list.stream().sorted(Comparator.comparing(Pair::getSecond, NetworkPacketSummary.a.SIZE_THEN_COUNT)).limit(10L).toList();
    }

    public double getCountsPerSecond() {
        return (double) this.totalPacketCountAndSize.totalCount / (double) this.recordingDuration.getSeconds();
    }

    public double getSizePerSecond() {
        return (double) this.totalPacketCountAndSize.totalCount / (double) this.recordingDuration.getSeconds();
    }

    public long getTotalCount() {
        return this.totalPacketCountAndSize.totalCount;
    }

    public long getTotalSize() {
        return this.totalPacketCountAndSize.totalSize;
    }

    public List<Pair<NetworkPacketSummary.b, NetworkPacketSummary.a>> largestSizeContributors() {
        return this.largestSizeContributors;
    }

    public static record a(long a, long b) {

        final long totalCount;
        final long totalSize;
        static final Comparator<NetworkPacketSummary.a> SIZE_THEN_COUNT = Comparator.comparing(NetworkPacketSummary.a::totalSize).thenComparing(NetworkPacketSummary.a::totalCount).reversed();

        public a(long i, long j) {
            this.totalCount = i;
            this.totalSize = j;
        }

        NetworkPacketSummary.a add(NetworkPacketSummary.a networkpacketsummary_a) {
            return new NetworkPacketSummary.a(this.totalCount + networkpacketsummary_a.totalCount, this.totalSize + networkpacketsummary_a.totalSize);
        }

        public long totalCount() {
            return this.totalCount;
        }

        public long totalSize() {
            return this.totalSize;
        }
    }

    public static record b(EnumProtocolDirection a, int b, int c) {

        private final EnumProtocolDirection direction;
        private final int protocolId;
        private final int packetId;
        private static final Map<NetworkPacketSummary.b, String> PACKET_NAME_BY_ID;

        public b(EnumProtocolDirection enumprotocoldirection, int i, int j) {
            this.direction = enumprotocoldirection;
            this.protocolId = i;
            this.packetId = j;
        }

        public String packetName() {
            return (String) NetworkPacketSummary.b.PACKET_NAME_BY_ID.getOrDefault(this, "unknown");
        }

        public static NetworkPacketSummary.b from(RecordedEvent recordedevent) {
            return new NetworkPacketSummary.b(recordedevent.getEventType().getName().equals("minecraft.PacketSent") ? EnumProtocolDirection.CLIENTBOUND : EnumProtocolDirection.SERVERBOUND, recordedevent.getInt("protocolId"), recordedevent.getInt("packetId"));
        }

        public EnumProtocolDirection direction() {
            return this.direction;
        }

        public int protocolId() {
            return this.protocolId;
        }

        public int packetId() {
            return this.packetId;
        }

        static {
            Builder<NetworkPacketSummary.b, String> builder = ImmutableMap.builder();
            EnumProtocol[] aenumprotocol = EnumProtocol.values();
            int i = aenumprotocol.length;

            for (int j = 0; j < i; ++j) {
                EnumProtocol enumprotocol = aenumprotocol[j];
                EnumProtocolDirection[] aenumprotocoldirection = EnumProtocolDirection.values();
                int k = aenumprotocoldirection.length;

                for (int l = 0; l < k; ++l) {
                    EnumProtocolDirection enumprotocoldirection = aenumprotocoldirection[l];
                    Int2ObjectMap<Class<? extends Packet<?>>> int2objectmap = enumprotocol.getPacketsByIds(enumprotocoldirection);

                    int2objectmap.forEach((integer, oclass) -> {
                        builder.put(new NetworkPacketSummary.b(enumprotocoldirection, enumprotocol.getId(), integer), oclass.getSimpleName());
                    });
                }
            }

            PACKET_NAME_BY_ID = builder.build();
        }
    }
}
