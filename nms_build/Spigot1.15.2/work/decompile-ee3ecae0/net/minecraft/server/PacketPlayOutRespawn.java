package net.minecraft.server;

import java.io.IOException;

public class PacketPlayOutRespawn implements Packet<PacketListenerPlayOut> {

    private DimensionManager a;
    private long b;
    private EnumGamemode c;
    private WorldType d;

    public PacketPlayOutRespawn() {}

    public PacketPlayOutRespawn(DimensionManager dimensionmanager, long i, WorldType worldtype, EnumGamemode enumgamemode) {
        this.a = dimensionmanager;
        this.b = i;
        this.c = enumgamemode;
        this.d = worldtype;
    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        this.a = DimensionManager.a(packetdataserializer.readInt());
        this.b = packetdataserializer.readLong();
        this.c = EnumGamemode.getById(packetdataserializer.readUnsignedByte());
        this.d = WorldType.getType(packetdataserializer.e(16));
        if (this.d == null) {
            this.d = WorldType.NORMAL;
        }

    }

    @Override
    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        packetdataserializer.writeInt(this.a.getDimensionID());
        packetdataserializer.writeLong(this.b);
        packetdataserializer.writeByte(this.c.getId());
        packetdataserializer.a(this.d.name());
    }
}
