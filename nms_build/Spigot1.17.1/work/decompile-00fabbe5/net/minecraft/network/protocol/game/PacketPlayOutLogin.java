package net.minecraft.network.protocol.game;

import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Set;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.core.IRegistry;
import net.minecraft.core.IRegistryCustom;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.EnumGamemode;
import net.minecraft.world.level.World;
import net.minecraft.world.level.dimension.DimensionManager;

public class PacketPlayOutLogin implements Packet<PacketListenerPlayOut> {

    private static final int HARDCORE_FLAG = 8;
    private final int playerId;
    private final long seed;
    private final boolean hardcore;
    private final EnumGamemode gameType;
    @Nullable
    private final EnumGamemode previousGameType;
    private final Set<ResourceKey<World>> levels;
    private final IRegistryCustom.Dimension registryHolder;
    private final DimensionManager dimensionType;
    private final ResourceKey<World> dimension;
    private final int maxPlayers;
    private final int chunkRadius;
    private final boolean reducedDebugInfo;
    private final boolean showDeathScreen;
    private final boolean isDebug;
    private final boolean isFlat;

    public PacketPlayOutLogin(int i, EnumGamemode enumgamemode, @Nullable EnumGamemode enumgamemode1, long j, boolean flag, Set<ResourceKey<World>> set, IRegistryCustom.Dimension iregistrycustom_dimension, DimensionManager dimensionmanager, ResourceKey<World> resourcekey, int k, int l, boolean flag1, boolean flag2, boolean flag3, boolean flag4) {
        this.playerId = i;
        this.levels = set;
        this.registryHolder = iregistrycustom_dimension;
        this.dimensionType = dimensionmanager;
        this.dimension = resourcekey;
        this.seed = j;
        this.gameType = enumgamemode;
        this.previousGameType = enumgamemode1;
        this.maxPlayers = k;
        this.hardcore = flag;
        this.chunkRadius = l;
        this.reducedDebugInfo = flag1;
        this.showDeathScreen = flag2;
        this.isDebug = flag3;
        this.isFlat = flag4;
    }

    public PacketPlayOutLogin(PacketDataSerializer packetdataserializer) {
        this.playerId = packetdataserializer.readInt();
        this.hardcore = packetdataserializer.readBoolean();
        this.gameType = EnumGamemode.getById(packetdataserializer.readByte());
        this.previousGameType = EnumGamemode.b(packetdataserializer.readByte());
        this.levels = (Set) packetdataserializer.a(Sets::newHashSetWithExpectedSize, (packetdataserializer1) -> {
            return ResourceKey.a(IRegistry.DIMENSION_REGISTRY, packetdataserializer1.q());
        });
        this.registryHolder = (IRegistryCustom.Dimension) packetdataserializer.a(IRegistryCustom.Dimension.NETWORK_CODEC);
        this.dimensionType = (DimensionManager) ((Supplier) packetdataserializer.a(DimensionManager.CODEC)).get();
        this.dimension = ResourceKey.a(IRegistry.DIMENSION_REGISTRY, packetdataserializer.q());
        this.seed = packetdataserializer.readLong();
        this.maxPlayers = packetdataserializer.j();
        this.chunkRadius = packetdataserializer.j();
        this.reducedDebugInfo = packetdataserializer.readBoolean();
        this.showDeathScreen = packetdataserializer.readBoolean();
        this.isDebug = packetdataserializer.readBoolean();
        this.isFlat = packetdataserializer.readBoolean();
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeInt(this.playerId);
        packetdataserializer.writeBoolean(this.hardcore);
        packetdataserializer.writeByte(this.gameType.getId());
        packetdataserializer.writeByte(EnumGamemode.a(this.previousGameType));
        packetdataserializer.a((Collection) this.levels, (packetdataserializer1, resourcekey) -> {
            packetdataserializer1.a(resourcekey.a());
        });
        packetdataserializer.a(IRegistryCustom.Dimension.NETWORK_CODEC, (Object) this.registryHolder);
        packetdataserializer.a(DimensionManager.CODEC, (Object) (() -> {
            return this.dimensionType;
        }));
        packetdataserializer.a(this.dimension.a());
        packetdataserializer.writeLong(this.seed);
        packetdataserializer.d(this.maxPlayers);
        packetdataserializer.d(this.chunkRadius);
        packetdataserializer.writeBoolean(this.reducedDebugInfo);
        packetdataserializer.writeBoolean(this.showDeathScreen);
        packetdataserializer.writeBoolean(this.isDebug);
        packetdataserializer.writeBoolean(this.isFlat);
    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }

    public int b() {
        return this.playerId;
    }

    public long c() {
        return this.seed;
    }

    public boolean d() {
        return this.hardcore;
    }

    public EnumGamemode e() {
        return this.gameType;
    }

    @Nullable
    public EnumGamemode f() {
        return this.previousGameType;
    }

    public Set<ResourceKey<World>> g() {
        return this.levels;
    }

    public IRegistryCustom h() {
        return this.registryHolder;
    }

    public DimensionManager i() {
        return this.dimensionType;
    }

    public ResourceKey<World> j() {
        return this.dimension;
    }

    public int k() {
        return this.maxPlayers;
    }

    public int l() {
        return this.chunkRadius;
    }

    public boolean m() {
        return this.reducedDebugInfo;
    }

    public boolean n() {
        return this.showDeathScreen;
    }

    public boolean o() {
        return this.isDebug;
    }

    public boolean p() {
        return this.isFlat;
    }
}
