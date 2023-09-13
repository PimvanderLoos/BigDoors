package net.minecraft.network.protocol.game;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.io.IOException;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;

public class PacketPlayOutGameStateChange implements Packet<PacketListenerPlayOut> {

    public static final PacketPlayOutGameStateChange.a a = new PacketPlayOutGameStateChange.a(0);
    public static final PacketPlayOutGameStateChange.a b = new PacketPlayOutGameStateChange.a(1);
    public static final PacketPlayOutGameStateChange.a c = new PacketPlayOutGameStateChange.a(2);
    public static final PacketPlayOutGameStateChange.a d = new PacketPlayOutGameStateChange.a(3);
    public static final PacketPlayOutGameStateChange.a e = new PacketPlayOutGameStateChange.a(4);
    public static final PacketPlayOutGameStateChange.a f = new PacketPlayOutGameStateChange.a(5);
    public static final PacketPlayOutGameStateChange.a g = new PacketPlayOutGameStateChange.a(6);
    public static final PacketPlayOutGameStateChange.a h = new PacketPlayOutGameStateChange.a(7);
    public static final PacketPlayOutGameStateChange.a i = new PacketPlayOutGameStateChange.a(8);
    public static final PacketPlayOutGameStateChange.a j = new PacketPlayOutGameStateChange.a(9);
    public static final PacketPlayOutGameStateChange.a k = new PacketPlayOutGameStateChange.a(10);
    public static final PacketPlayOutGameStateChange.a l = new PacketPlayOutGameStateChange.a(11);
    private PacketPlayOutGameStateChange.a m;
    private float n;

    public PacketPlayOutGameStateChange() {}

    public PacketPlayOutGameStateChange(PacketPlayOutGameStateChange.a packetplayoutgamestatechange_a, float f) {
        this.m = packetplayoutgamestatechange_a;
        this.n = f;
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        this.m = (PacketPlayOutGameStateChange.a) PacketPlayOutGameStateChange.a.a.get(packetdataserializer.readUnsignedByte());
        this.n = packetdataserializer.readFloat();
    }

    @Override
    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        packetdataserializer.writeByte(this.m.b);
        packetdataserializer.writeFloat(this.n);
    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }

    public static class a {

        private static final Int2ObjectMap<PacketPlayOutGameStateChange.a> a = new Int2ObjectOpenHashMap();
        private final int b;

        public a(int i) {
            this.b = i;
            PacketPlayOutGameStateChange.a.a.put(i, this);
        }
    }
}
