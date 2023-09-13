package net.minecraft.network.protocol.game;

import com.google.common.collect.Sets;
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

public record PacketPlayOutLogin(int a, boolean b, EnumGamemode c, @Nullable EnumGamemode d, Set<ResourceKey<World>> e, IRegistryCustom.Dimension f, DimensionManager g, ResourceKey<World> h, long i, int j, int k, int l, boolean m, boolean n, boolean o, boolean p) implements Packet<PacketListenerPlayOut> {

    private final int playerId;
    private final boolean hardcore;
    private final EnumGamemode gameType;
    @Nullable
    private final EnumGamemode previousGameType;
    private final Set<ResourceKey<World>> levels;
    private final IRegistryCustom.Dimension registryHolder;
    private final DimensionManager dimensionType;
    private final ResourceKey<World> dimension;
    private final long seed;
    private final int maxPlayers;
    private final int chunkRadius;
    private final int simulationDistance;
    private final boolean reducedDebugInfo;
    private final boolean showDeathScreen;
    private final boolean isDebug;
    private final boolean isFlat;

    public PacketPlayOutLogin(PacketDataSerializer packetdataserializer) {
        this(packetdataserializer.readInt(), packetdataserializer.readBoolean(), EnumGamemode.byId(packetdataserializer.readByte()), EnumGamemode.byNullableId(packetdataserializer.readByte()), (Set) packetdataserializer.readCollection(Sets::newHashSetWithExpectedSize, (packetdataserializer1) -> {
            return ResourceKey.create(IRegistry.DIMENSION_REGISTRY, packetdataserializer1.readResourceLocation());
        }), (IRegistryCustom.Dimension) packetdataserializer.readWithCodec(IRegistryCustom.Dimension.NETWORK_CODEC), (DimensionManager) ((Supplier) packetdataserializer.readWithCodec(DimensionManager.CODEC)).get(), ResourceKey.create(IRegistry.DIMENSION_REGISTRY, packetdataserializer.readResourceLocation()), packetdataserializer.readLong(), packetdataserializer.readVarInt(), packetdataserializer.readVarInt(), packetdataserializer.readVarInt(), packetdataserializer.readBoolean(), packetdataserializer.readBoolean(), packetdataserializer.readBoolean(), packetdataserializer.readBoolean());
    }

    public PacketPlayOutLogin(int i, boolean flag, EnumGamemode enumgamemode, @Nullable EnumGamemode enumgamemode1, Set<ResourceKey<World>> set, IRegistryCustom.Dimension iregistrycustom_dimension, DimensionManager dimensionmanager, ResourceKey<World> resourcekey, long j, int k, int l, int i1, boolean flag1, boolean flag2, boolean flag3, boolean flag4) {
        this.playerId = i;
        this.hardcore = flag;
        this.gameType = enumgamemode;
        this.previousGameType = enumgamemode1;
        this.levels = set;
        this.registryHolder = iregistrycustom_dimension;
        this.dimensionType = dimensionmanager;
        this.dimension = resourcekey;
        this.seed = j;
        this.maxPlayers = k;
        this.chunkRadius = l;
        this.simulationDistance = i1;
        this.reducedDebugInfo = flag1;
        this.showDeathScreen = flag2;
        this.isDebug = flag3;
        this.isFlat = flag4;
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeInt(this.playerId);
        packetdataserializer.writeBoolean(this.hardcore);
        packetdataserializer.writeByte(this.gameType.getId());
        packetdataserializer.writeByte(EnumGamemode.getNullableId(this.previousGameType));
        packetdataserializer.writeCollection(this.levels, (packetdataserializer1, resourcekey) -> {
            packetdataserializer1.writeResourceLocation(resourcekey.location());
        });
        packetdataserializer.writeWithCodec(IRegistryCustom.Dimension.NETWORK_CODEC, this.registryHolder);
        packetdataserializer.writeWithCodec(DimensionManager.CODEC, () -> {
            return this.dimensionType;
        });
        packetdataserializer.writeResourceLocation(this.dimension.location());
        packetdataserializer.writeLong(this.seed);
        packetdataserializer.writeVarInt(this.maxPlayers);
        packetdataserializer.writeVarInt(this.chunkRadius);
        packetdataserializer.writeVarInt(this.simulationDistance);
        packetdataserializer.writeBoolean(this.reducedDebugInfo);
        packetdataserializer.writeBoolean(this.showDeathScreen);
        packetdataserializer.writeBoolean(this.isDebug);
        packetdataserializer.writeBoolean(this.isFlat);
    }

    public void handle(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.handleLogin(this);
    }

    public int playerId() {
        return this.playerId;
    }

    public boolean hardcore() {
        return this.hardcore;
    }

    public EnumGamemode gameType() {
        return this.gameType;
    }

    @Nullable
    public EnumGamemode previousGameType() {
        return this.previousGameType;
    }

    public Set<ResourceKey<World>> levels() {
        return this.levels;
    }

    public IRegistryCustom.Dimension registryHolder() {
        return this.registryHolder;
    }

    public DimensionManager dimensionType() {
        return this.dimensionType;
    }

    public ResourceKey<World> dimension() {
        return this.dimension;
    }

    public long seed() {
        return this.seed;
    }

    public int maxPlayers() {
        return this.maxPlayers;
    }

    public int chunkRadius() {
        return this.chunkRadius;
    }

    public int simulationDistance() {
        return this.simulationDistance;
    }

    public boolean reducedDebugInfo() {
        return this.reducedDebugInfo;
    }

    public boolean showDeathScreen() {
        return this.showDeathScreen;
    }

    public boolean isDebug() {
        return this.isDebug;
    }

    public boolean isFlat() {
        return this.isFlat;
    }
}
