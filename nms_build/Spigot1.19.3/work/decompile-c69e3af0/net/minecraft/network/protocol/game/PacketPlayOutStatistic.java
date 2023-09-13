package net.minecraft.network.protocol.game;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Map;
import net.minecraft.core.registries.BuiltInRegistries;
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
        this.stats = (Object2IntMap) packetdataserializer.readMap(Object2IntOpenHashMap::new, (packetdataserializer1) -> {
            StatisticWrapper<?> statisticwrapper = (StatisticWrapper) packetdataserializer1.readById(BuiltInRegistries.STAT_TYPE);

            return readStatCap(packetdataserializer, statisticwrapper);
        }, PacketDataSerializer::readVarInt);
    }

    private static <T> Statistic<T> readStatCap(PacketDataSerializer packetdataserializer, StatisticWrapper<T> statisticwrapper) {
        return statisticwrapper.get(packetdataserializer.readById(statisticwrapper.getRegistry()));
    }

    public void handle(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.handleAwardStats(this);
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeMap(this.stats, PacketPlayOutStatistic::writeStatCap, PacketDataSerializer::writeVarInt);
    }

    private static <T> void writeStatCap(PacketDataSerializer packetdataserializer, Statistic<T> statistic) {
        packetdataserializer.writeId(BuiltInRegistries.STAT_TYPE, statistic.getType());
        packetdataserializer.writeId(statistic.getType().getRegistry(), statistic.getValue());
    }

    public Map<Statistic<?>, Integer> getStats() {
        return this.stats;
    }
}
