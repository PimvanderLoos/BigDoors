package net.minecraft.network.protocol.game;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.DataWatcher;

public class PacketPlayOutEntityMetadata implements Packet<PacketListenerPlayOut> {

    private final int id;
    @Nullable
    private final List<DataWatcher.Item<?>> packedItems;

    public PacketPlayOutEntityMetadata(int i, DataWatcher datawatcher, boolean flag) {
        this.id = i;
        if (flag) {
            this.packedItems = datawatcher.getAll();
            datawatcher.e();
        } else {
            this.packedItems = datawatcher.b();
        }

    }

    public PacketPlayOutEntityMetadata(PacketDataSerializer packetdataserializer) {
        this.id = packetdataserializer.j();
        this.packedItems = DataWatcher.a(packetdataserializer);
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) {
        packetdataserializer.d(this.id);
        DataWatcher.a(this.packedItems, packetdataserializer);
    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }

    @Nullable
    public List<DataWatcher.Item<?>> b() {
        return this.packedItems;
    }

    public int c() {
        return this.id;
    }
}
