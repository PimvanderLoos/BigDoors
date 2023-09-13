package net.minecraft.server;

import java.io.IOException;

public class PacketPlayOutLogin implements Packet<PacketListenerPlayOut> {

    private int a;
    private long b;
    private boolean c;
    private EnumGamemode d;
    private DimensionManager e;
    private int f;
    private WorldType g;
    private int h;
    private boolean i;
    private boolean j;

    public PacketPlayOutLogin() {}

    public PacketPlayOutLogin(int i, EnumGamemode enumgamemode, long j, boolean flag, DimensionManager dimensionmanager, int k, WorldType worldtype, int l, boolean flag1, boolean flag2) {
        this.a = i;
        this.e = dimensionmanager;
        this.b = j;
        this.d = enumgamemode;
        this.f = k;
        this.c = flag;
        this.g = worldtype;
        this.h = l;
        this.i = flag1;
        this.j = flag2;
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        this.a = packetdataserializer.readInt();
        short short0 = packetdataserializer.readUnsignedByte();

        this.c = (short0 & 8) == 8;
        int i = short0 & -9;

        this.d = EnumGamemode.getById(i);
        this.e = DimensionManager.a(packetdataserializer.readInt());
        this.b = packetdataserializer.readLong();
        this.f = packetdataserializer.readUnsignedByte();
        this.g = WorldType.getType(packetdataserializer.e(16));
        if (this.g == null) {
            this.g = WorldType.NORMAL;
        }

        this.h = packetdataserializer.i();
        this.i = packetdataserializer.readBoolean();
        this.j = packetdataserializer.readBoolean();
    }

    @Override
    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        packetdataserializer.writeInt(this.a);
        int i = this.d.getId();

        if (this.c) {
            i |= 8;
        }

        packetdataserializer.writeByte(i);
        packetdataserializer.writeInt(this.e.getDimensionID());
        packetdataserializer.writeLong(this.b);
        packetdataserializer.writeByte(this.f);
        packetdataserializer.a(this.g.name());
        packetdataserializer.d(this.h);
        packetdataserializer.writeBoolean(this.i);
        packetdataserializer.writeBoolean(this.j);
    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }
}
