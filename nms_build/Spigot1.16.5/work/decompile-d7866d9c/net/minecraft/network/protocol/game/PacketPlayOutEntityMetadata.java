package net.minecraft.network.protocol.game;

import java.io.IOException;
import java.util.List;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.DataWatcher;

public class PacketPlayOutEntityMetadata implements Packet<PacketListenerPlayOut> {

    private int a;
    private List<DataWatcher.Item<?>> b;

    public PacketPlayOutEntityMetadata() {}

    public PacketPlayOutEntityMetadata(int i, DataWatcher datawatcher, boolean flag) {
        this.a = i;
        if (flag) {
            this.b = datawatcher.c();
            datawatcher.e();
        } else {
            this.b = datawatcher.b();
        }

    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        this.a = packetdataserializer.i();
        this.b = DataWatcher.a(packetdataserializer);
    }

    @Override
    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        packetdataserializer.d(this.a);
        DataWatcher.a(this.b, packetdataserializer);
    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }
}
