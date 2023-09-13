package net.minecraft.server;

import java.io.IOException;

public class PacketPlayOutRespawn implements Packet<PacketListenerPlayOut> {

    private ResourceKey<DimensionManager> a;
    private ResourceKey<World> b;
    private long c;
    private EnumGamemode d;
    private EnumGamemode e;
    private boolean f;
    private boolean g;
    private boolean h;

    public PacketPlayOutRespawn() {}

    public PacketPlayOutRespawn(ResourceKey<DimensionManager> resourcekey, ResourceKey<World> resourcekey1, long i, EnumGamemode enumgamemode, EnumGamemode enumgamemode1, boolean flag, boolean flag1, boolean flag2) {
        this.a = resourcekey;
        this.b = resourcekey1;
        this.c = i;
        this.d = enumgamemode;
        this.e = enumgamemode1;
        this.f = flag;
        this.g = flag1;
        this.h = flag2;
    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        this.a = ResourceKey.a(IRegistry.ad, packetdataserializer.o());
        this.b = ResourceKey.a(IRegistry.ae, packetdataserializer.o());
        this.c = packetdataserializer.readLong();
        this.d = EnumGamemode.getById(packetdataserializer.readUnsignedByte());
        this.e = EnumGamemode.getById(packetdataserializer.readUnsignedByte());
        this.f = packetdataserializer.readBoolean();
        this.g = packetdataserializer.readBoolean();
        this.h = packetdataserializer.readBoolean();
    }

    @Override
    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        packetdataserializer.a(this.a.a());
        packetdataserializer.a(this.b.a());
        packetdataserializer.writeLong(this.c);
        packetdataserializer.writeByte(this.d.getId());
        packetdataserializer.writeByte(this.e.getId());
        packetdataserializer.writeBoolean(this.f);
        packetdataserializer.writeBoolean(this.g);
        packetdataserializer.writeBoolean(this.h);
    }
}
