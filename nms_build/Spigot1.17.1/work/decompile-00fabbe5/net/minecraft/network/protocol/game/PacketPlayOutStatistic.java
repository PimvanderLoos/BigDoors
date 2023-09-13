package net.minecraft.network.protocol.game;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Map;
import net.minecraft.core.IRegistry;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.stats.Statistic;
import net.minecraft.stats.StatisticWrapper;

public class PacketPlayOutStatistic implements Packet<PacketListenerPlayOut> {

    private final Object2IntMap<Statistic<?>> stats;

    public PacketPlayOutStatistic(Object2IntMap<Statistic<?>> object2intmap) {
        this.stats = object2intmap;
    }

    public PacketPlayOutStatistic(PacketDataSerializer packetdataserializer) {
        this.stats = (Object2IntMap) packetdataserializer.a(Object2IntOpenHashMap::new, (packetdataserializer1) -> {
            int i = packetdataserializer1.j();
            int j = packetdataserializer1.j();

            return a((StatisticWrapper) IRegistry.STAT_TYPE.fromId(i), j);
        }, PacketDataSerializer::j);
    }

    private static <T> Statistic<T> a(StatisticWrapper<T> statisticwrapper, int i) {
        return statisticwrapper.b(statisticwrapper.getRegistry().fromId(i));
    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) {
        packetdataserializer.a((Map) this.stats, (packetdataserializer1, statistic) -> {
            packetdataserializer1.d(IRegistry.STAT_TYPE.getId(statistic.getWrapper()));
            packetdataserializer1.d(this.a(statistic));
        }, PacketDataSerializer::d);
    }

    private <T> int a(Statistic<T> statistic) {
        return statistic.getWrapper().getRegistry().getId(statistic.b());
    }

    public Map<Statistic<?>, Integer> b() {
        return this.stats;
    }
}
