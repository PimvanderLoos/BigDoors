package net.minecraft.network.protocol.game;

import com.google.common.collect.Sets;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Supplier;
import net.minecraft.core.IRegistry;
import net.minecraft.core.IRegistryCustom;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.EnumGamemode;
import net.minecraft.world.level.World;
import net.minecraft.world.level.dimension.DimensionManager;

public class PacketPlayOutLogin implements Packet<PacketListenerPlayOut> {

    private int a;
    private long b;
    private boolean c;
    private EnumGamemode d;
    private EnumGamemode e;
    private Set<ResourceKey<World>> f;
    private IRegistryCustom.Dimension g;
    private DimensionManager h;
    private ResourceKey<World> i;
    private int j;
    private int k;
    private boolean l;
    private boolean m;
    private boolean n;
    private boolean o;

    public PacketPlayOutLogin() {}

    public PacketPlayOutLogin(int i, EnumGamemode enumgamemode, EnumGamemode enumgamemode1, long j, boolean flag, Set<ResourceKey<World>> set, IRegistryCustom.Dimension iregistrycustom_dimension, DimensionManager dimensionmanager, ResourceKey<World> resourcekey, int k, int l, boolean flag1, boolean flag2, boolean flag3, boolean flag4) {
        this.a = i;
        this.f = set;
        this.g = iregistrycustom_dimension;
        this.h = dimensionmanager;
        this.i = resourcekey;
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
        this.c = packetdataserializer.readBoolean();
        this.d = EnumGamemode.getById(packetdataserializer.readByte());
        this.e = EnumGamemode.getById(packetdataserializer.readByte());
        int i = packetdataserializer.i();

        this.f = Sets.newHashSet();

        for (int j = 0; j < i; ++j) {
            this.f.add(ResourceKey.a(IRegistry.L, packetdataserializer.p()));
        }

        this.g = (IRegistryCustom.Dimension) packetdataserializer.a(IRegistryCustom.Dimension.a);
        this.h = (DimensionManager) ((Supplier) packetdataserializer.a(DimensionManager.n)).get();
        this.i = ResourceKey.a(IRegistry.L, packetdataserializer.p());
        this.b = packetdataserializer.readLong();
        this.j = packetdataserializer.i();
        this.k = packetdataserializer.i();
        this.l = packetdataserializer.readBoolean();
        this.m = packetdataserializer.readBoolean();
        this.n = packetdataserializer.readBoolean();
        this.o = packetdataserializer.readBoolean();
    }

    @Override
    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        packetdataserializer.writeInt(this.a);
        packetdataserializer.writeBoolean(this.c);
        packetdataserializer.writeByte(this.d.getId());
        packetdataserializer.writeByte(this.e.getId());
        packetdataserializer.d(this.f.size());
        Iterator iterator = this.f.iterator();

        while (iterator.hasNext()) {
            ResourceKey<World> resourcekey = (ResourceKey) iterator.next();

            packetdataserializer.a(resourcekey.a());
        }

        packetdataserializer.a(IRegistryCustom.Dimension.a, this.g);
        packetdataserializer.a(DimensionManager.n, () -> {
            return this.h;
        });
        packetdataserializer.a(this.i.a());
        packetdataserializer.writeLong(this.b);
        packetdataserializer.d(this.j);
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
