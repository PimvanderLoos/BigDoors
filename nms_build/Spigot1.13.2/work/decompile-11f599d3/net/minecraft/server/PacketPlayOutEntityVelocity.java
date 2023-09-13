package net.minecraft.server;

import java.io.IOException;

public class PacketPlayOutEntityVelocity implements Packet<PacketListenerPlayOut> {

    private int a;
    private int b;
    private int c;
    private int d;

    public PacketPlayOutEntityVelocity() {}

    public PacketPlayOutEntityVelocity(Entity entity) {
        this(entity.getId(), entity.motX, entity.motY, entity.motZ);
    }

    public PacketPlayOutEntityVelocity(int i, double d0, double d1, double d2) {
        this.a = i;
        double d3 = 3.9D;

        if (d0 < -3.9D) {
            d0 = -3.9D;
        }

        if (d1 < -3.9D) {
            d1 = -3.9D;
        }

        if (d2 < -3.9D) {
            d2 = -3.9D;
        }

        if (d0 > 3.9D) {
            d0 = 3.9D;
        }

        if (d1 > 3.9D) {
            d1 = 3.9D;
        }

        if (d2 > 3.9D) {
            d2 = 3.9D;
        }

        this.b = (int) (d0 * 8000.0D);
        this.c = (int) (d1 * 8000.0D);
        this.d = (int) (d2 * 8000.0D);
    }

    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        this.a = packetdataserializer.g();
        this.b = packetdataserializer.readShort();
        this.c = packetdataserializer.readShort();
        this.d = packetdataserializer.readShort();
    }

    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        packetdataserializer.d(this.a);
        packetdataserializer.writeShort(this.b);
        packetdataserializer.writeShort(this.c);
        packetdataserializer.writeShort(this.d);
    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }
}
