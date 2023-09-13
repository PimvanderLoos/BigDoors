package net.minecraft.server;

import java.io.IOException;
import java.util.Collection;

public class PacketPlayOutMap implements Packet<PacketListenerPlayOut> {

    private int a;
    private byte b;
    private boolean c;
    private MapIcon[] d;
    private int e;
    private int f;
    private int g;
    private int h;
    private byte[] i;

    public PacketPlayOutMap() {}

    public PacketPlayOutMap(int i, byte b0, boolean flag, Collection<MapIcon> collection, byte[] abyte, int j, int k, int l, int i1) {
        this.a = i;
        this.b = b0;
        this.c = flag;
        this.d = (MapIcon[]) collection.toArray(new MapIcon[collection.size()]);
        this.e = j;
        this.f = k;
        this.g = l;
        this.h = i1;
        this.i = new byte[l * i1];

        for (int j1 = 0; j1 < l; ++j1) {
            for (int k1 = 0; k1 < i1; ++k1) {
                this.i[j1 + k1 * l] = abyte[j + j1 + (k + k1) * 128];
            }
        }

    }

    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        this.a = packetdataserializer.g();
        this.b = packetdataserializer.readByte();
        this.c = packetdataserializer.readBoolean();
        this.d = new MapIcon[packetdataserializer.g()];

        for (int i = 0; i < this.d.length; ++i) {
            MapIcon.Type mapicon_type = (MapIcon.Type) packetdataserializer.a(MapIcon.Type.class);

            this.d[i] = new MapIcon(mapicon_type, packetdataserializer.readByte(), packetdataserializer.readByte(), (byte) (packetdataserializer.readByte() & 15), packetdataserializer.readBoolean() ? packetdataserializer.f() : null);
        }

        this.g = packetdataserializer.readUnsignedByte();
        if (this.g > 0) {
            this.h = packetdataserializer.readUnsignedByte();
            this.e = packetdataserializer.readUnsignedByte();
            this.f = packetdataserializer.readUnsignedByte();
            this.i = packetdataserializer.a();
        }

    }

    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        packetdataserializer.d(this.a);
        packetdataserializer.writeByte(this.b);
        packetdataserializer.writeBoolean(this.c);
        packetdataserializer.d(this.d.length);
        MapIcon[] amapicon = this.d;
        int i = amapicon.length;

        for (int j = 0; j < i; ++j) {
            MapIcon mapicon = amapicon[j];

            packetdataserializer.a((Enum) mapicon.b());
            packetdataserializer.writeByte(mapicon.getX());
            packetdataserializer.writeByte(mapicon.getY());
            packetdataserializer.writeByte(mapicon.getRotation() & 15);
            if (mapicon.g() != null) {
                packetdataserializer.writeBoolean(true);
                packetdataserializer.a(mapicon.g());
            } else {
                packetdataserializer.writeBoolean(false);
            }
        }

        packetdataserializer.writeByte(this.g);
        if (this.g > 0) {
            packetdataserializer.writeByte(this.h);
            packetdataserializer.writeByte(this.e);
            packetdataserializer.writeByte(this.f);
            packetdataserializer.a(this.i);
        }

    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }
}
