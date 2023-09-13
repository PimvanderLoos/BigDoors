package net.minecraft.network.protocol.game;

import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.core.IRegistry;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.EnumGamemode;
import net.minecraft.world.level.World;
import net.minecraft.world.level.dimension.DimensionManager;

public class PacketPlayOutRespawn implements Packet<PacketListenerPlayOut> {

    private final DimensionManager dimensionType;
    private final ResourceKey<World> dimension;
    private final long seed;
    private final EnumGamemode playerGameType;
    @Nullable
    private final EnumGamemode previousPlayerGameType;
    private final boolean isDebug;
    private final boolean isFlat;
    private final boolean keepAllPlayerData;

    public PacketPlayOutRespawn(DimensionManager dimensionmanager, ResourceKey<World> resourcekey, long i, EnumGamemode enumgamemode, @Nullable EnumGamemode enumgamemode1, boolean flag, boolean flag1, boolean flag2) {
        this.dimensionType = dimensionmanager;
        this.dimension = resourcekey;
        this.seed = i;
        this.playerGameType = enumgamemode;
        this.previousPlayerGameType = enumgamemode1;
        this.isDebug = flag;
        this.isFlat = flag1;
        this.keepAllPlayerData = flag2;
    }

    public PacketPlayOutRespawn(PacketDataSerializer packetdataserializer) {
        this.dimensionType = (DimensionManager) ((Supplier) packetdataserializer.a(DimensionManager.CODEC)).get();
        this.dimension = ResourceKey.a(IRegistry.DIMENSION_REGISTRY, packetdataserializer.q());
        this.seed = packetdataserializer.readLong();
        this.playerGameType = EnumGamemode.getById(packetdataserializer.readUnsignedByte());
        this.previousPlayerGameType = EnumGamemode.b(packetdataserializer.readByte());
        this.isDebug = packetdataserializer.readBoolean();
        this.isFlat = packetdataserializer.readBoolean();
        this.keepAllPlayerData = packetdataserializer.readBoolean();
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) {
        packetdataserializer.a(DimensionManager.CODEC, (Object) (() -> {
            return this.dimensionType;
        }));
        packetdataserializer.a(this.dimension.a());
        packetdataserializer.writeLong(this.seed);
        packetdataserializer.writeByte(this.playerGameType.getId());
        packetdataserializer.writeByte(EnumGamemode.a(this.previousPlayerGameType));
        packetdataserializer.writeBoolean(this.isDebug);
        packetdataserializer.writeBoolean(this.isFlat);
        packetdataserializer.writeBoolean(this.keepAllPlayerData);
    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }

    public DimensionManager b() {
        return this.dimensionType;
    }

    public ResourceKey<World> c() {
        return this.dimension;
    }

    public long d() {
        return this.seed;
    }

    public EnumGamemode e() {
        return this.playerGameType;
    }

    @Nullable
    public EnumGamemode f() {
        return this.previousPlayerGameType;
    }

    public boolean g() {
        return this.isDebug;
    }

    public boolean h() {
        return this.isFlat;
    }

    public boolean i() {
        return this.keepAllPlayerData;
    }
}
