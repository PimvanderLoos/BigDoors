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
        this.stats = (Object2IntMap) packetdataserializer.readMap(Object2IntOpenHashMap::new, (packetdataserializer1) -> {
            int i = packetdataserializer1.readVarInt();
            int j = packetdataserializer1.readVarInt();

            return readStatCap((StatisticWrapper) IRegistry.STAT_TYPE.byId(i), j);
        }, PacketDataSerializer::readVarInt);
    }

    private static <T> Statistic<T> readStatCap(StatisticWrapper<T> statisticwrapper, int i) {
        return statisticwrapper.get(statisticwrapper.getRegistry().byId(i));
    }

    public void handle(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.handleAwardStats(this);
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeMap(this.stats, (packetdataserializer1, statistic) -> {
            packetdataserializer1.writeVarInt(IRegistry.STAT_TYPE.getId(statistic.getType()));
            packetdataserializer1.writeVarInt(this.getStatIdCap(statistic));
        }, PacketDataSerializer::writeVarInt);
    }

    private <T> int getStatIdCap(Statistic<T> statistic) {
        return statistic.getType().getRegistry().getId(statistic.getValue());
    }

    public Map<Statistic<?>, Integer> getStats() {
        return this.stats;
    }
}
