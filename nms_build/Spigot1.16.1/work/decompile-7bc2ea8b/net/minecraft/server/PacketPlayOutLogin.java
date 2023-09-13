package net.minecraft.server;

import com.google.common.collect.Sets;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

public class PacketPlayOutLogin implements Packet<PacketListenerPlayOut> {

    private int a;
    private long b;
    private boolean c;
    private EnumGamemode d;
    private EnumGamemode e;
    private Set<ResourceKey<World>> f;
    private IRegistryCustom.Dimension g;
    private ResourceKey<DimensionManager> h;
    private ResourceKey<World> i;
    private int j;
    private int k;
    private boolean l;
    private boolean m;
    private boolean n;
    private boolean o;

    public PacketPlayOutLogin() {}

    public PacketPlayOutLogin(int i, EnumGamemode enumgamemode, EnumGamemode enumgamemode1, long j, boolean flag, Set<ResourceKey<World>> set, IRegistryCustom.Dimension iregistrycustom_dimension, ResourceKey<DimensionManager> resourcekey, ResourceKey<World> resourcekey1, int k, int l, boolean flag1, boolean flag2, boolean flag3, boolean flag4) {
        this.a = i;
        this.f = set;
        this.g = iregistrycustom_dimension;
        this.h = resourcekey;
        this.i = resourcekey1;
        this.b = j;
        this.d = enumgamemode;
        this.e = enumgamemode1;
        this.j = k;
        this.c = flag;
        this.k = l;
        this.l = flag1;
        this.m = flag2;
        this.n = flag3;
        this.o = flag4;
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        this.a = packetdataserializer.readInt();
        short short0 = packetdataserializer.readUnsignedByte();

        this.c = (short0 & 8) == 8;
        int i = short0 & -9;

        this.d = EnumGamemode.getById(i);
        this.e = EnumGamemode.getById(packetdataserializer.readUnsignedByte());
        int j = packetdataserializer.i();

        this.f = Sets.newHashSet();

        for (int k = 0; k < j; ++k) {
            this.f.add(ResourceKey.a(IRegistry.ae, packetdataserializer.o()));
        }

        this.g = (IRegistryCustom.Dimension) packetdataserializer.a(IRegistryCustom.Dimension.a);
        this.h = ResourceKey.a(IRegistry.ad, packetdataserializer.o());
        this.i = ResourceKey.a(IRegistry.ae, packetdataserializer.o());
        this.b = packetdataserializer.readLong();
        this.j = packetdataserializer.readUnsignedByte();
        this.k = packetdataserializer.i();
        this.l = packetdataserializer.readBoolean();
        this.m = packetdataserializer.readBoolean();
        this.n = packetdataserializer.readBoolean();
        this.o = packetdataserializer.readBoolean();
    }

    @Override
    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        packetdataserializer.writeInt(this.a);
        int i = this.d.getId();

        if (this.c) {
            i |= 8;
        }

        packetdataserializer.writeByte(i);
        packetdataserializer.writeByte(this.e.getId());
        packetdataserializer.d(this.f.size());
        Iterator iterator = this.f.iterator();

        while (iterator.hasNext()) {
            ResourceKey<World> resourcekey = (ResourceKey) iterator.next();

            packetdataserializer.a(resourcekey.a());
        }

        packetdataserializer.a(IRegistryCustom.Dimension.a, this.g);
        packetdataserializer.a(this.h.a());
        packetdataserializer.a(this.i.a());
        packetdataserializer.writeLong(this.b);
        packetdataserializer.writeByte(this.j);
        packetdataserializer.d(this.k);
        packetdataserializer.writeBoolean(this.l);
        packetdataserializer.writeBoolean(this.m);
        packetdataserializer.writeBoolean(this.n);
        packetdataserializer.writeBoolean(this.o);
    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }
}
