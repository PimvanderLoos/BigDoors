package net.minecraft.network.protocol.game;

import java.io.IOException;
import java.util.function.Supplier;
import net.minecraft.core.IRegistry;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.EnumGamemode;
import net.minecraft.world.level.World;
import net.minecraft.world.level.dimension.DimensionManager;

public class PacketPlayOutRespawn implements Packet<PacketListenerPlayOut> {

    private DimensionManager a;
    private ResourceKey<World> b;
    private long c;
    private EnumGamemode d;
    private EnumGamemode e;
    private boolean f;
    private boolean g;
    private boolean h;

    public PacketPlayOutRespawn() {}

    public PacketPlayOutRespawn(DimensionManager dimensionmanager, ResourceKey<World> resourcekey, long i, EnumGamemode enumgamemode, EnumGamemode enumgamemode1, boolean flag, boolean flag1, boolean flag2) {
        this.a = dimensionmanager;
        this.b = resourcekey;
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
        this.a = (DimensionManager) ((Supplier) packetdataserializer.a(DimensionManager.n)).get();
        this.b = ResourceKey.a(IRegistry.L, packetdataserializer.p());
        this.c = packetdataserializer.readLong();
        this.d = EnumGamemode.getById(packetdataserializer.readUnsignedByte());
        this.e = EnumGamemode.getById(packetdataserializer.readUnsignedByte());
        this.f = packetdataserializer.readBoolean();
        this.g = packetdataserializer.readBoolean();
        this.h = packetdataserializer.readBoolean();
    }

    @Override
    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        packetdataserializer.a(DimensionManager.n, () -> {
            return this.a;
        });
        packetdataserializer.a(this.b.a());
        packetdataserializer.writeLong(this.c);
        packetdataserializer.writeByte(this.d.getId());
        packetdataserializer.writeByte(this.e.getId());
        packetdataserializer.writeBoolean(this.f);
        packetdataserializer.writeBoolean(this.g);
        packetdataserializer.writeBoolean(this.h);
    }
}
