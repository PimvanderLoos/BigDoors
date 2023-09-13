package net.minecraft.network.protocol.game;

import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.EnumGamemode;
import net.minecraft.world.level.World;
import net.minecraft.world.level.dimension.DimensionManager;

public class PacketPlayOutRespawn implements Packet<PacketListenerPlayOut> {

    public static final byte KEEP_ATTRIBUTES = 1;
    public static final byte KEEP_ENTITY_DATA = 2;
    public static final byte KEEP_ALL_DATA = 3;
    private final ResourceKey<DimensionManager> dimensionType;
    private final ResourceKey<World> dimension;
    private final long seed;
    private final EnumGamemode playerGameType;
    @Nullable
    private final EnumGamemode previousPlayerGameType;
    private final boolean isDebug;
    private final boolean isFlat;
    private final byte dataToKeep;
    private final Optional<GlobalPos> lastDeathLocation;

    public PacketPlayOutRespawn(ResourceKey<DimensionManager> resourcekey, ResourceKey<World> resourcekey1, long i, EnumGamemode enumgamemode, @Nullable EnumGamemode enumgamemode1, boolean flag, boolean flag1, byte b0, Optional<GlobalPos> optional) {
        this.dimensionType = resourcekey;
        this.dimension = resourcekey1;
        this.seed = i;
        this.playerGameType = enumgamemode;
        this.previousPlayerGameType = enumgamemode1;
        this.isDebug = flag;
        this.isFlat = flag1;
        this.dataToKeep = b0;
        this.lastDeathLocation = optional;
    }

    public PacketPlayOutRespawn(PacketDataSerializer packetdataserializer) {
        this.dimensionType = packetdataserializer.readResourceKey(Registries.DIMENSION_TYPE);
        this.dimension = packetdataserializer.readResourceKey(Registries.DIMENSION);
        this.seed = packetdataserializer.readLong();
        this.playerGameType = EnumGamemode.byId(packetdataserializer.readUnsignedByte());
        this.previousPlayerGameType = EnumGamemode.byNullableId(packetdataserializer.readByte());
        this.isDebug = packetdataserializer.readBoolean();
        this.isFlat = packetdataserializer.readBoolean();
        this.dataToKeep = packetdataserializer.readByte();
        this.lastDeathLocation = packetdataserializer.readOptional(PacketDataSerializer::readGlobalPos);
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeResourceKey(this.dimensionType);
        packetdataserializer.writeResourceKey(this.dimension);
        packetdataserializer.writeLong(this.seed);
        packetdataserializer.writeByte(this.playerGameType.getId());
        packetdataserializer.writeByte(EnumGamemode.getNullableId(this.previousPlayerGameType));
        packetdataserializer.writeBoolean(this.isDebug);
        packetdataserializer.writeBoolean(this.isFlat);
        packetdataserializer.writeByte(this.dataToKeep);
        packetdataserializer.writeOptional(this.lastDeathLocation, PacketDataSerializer::writeGlobalPos);
    }

    public void handle(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.handleRespawn(this);
    }

    public ResourceKey<DimensionManager> getDimensionType() {
        return this.dimensionType;
    }

    public ResourceKey<World> getDimension() {
        return this.dimension;
    }

    public long getSeed() {
        return this.seed;
    }

    public EnumGamemode getPlayerGameType() {
        return this.playerGameType;
    }

    @Nullable
    public EnumGamemode getPreviousPlayerGameType() {
        return this.previousPlayerGameType;
    }

    public boolean isDebug() {
        return this.isDebug;
    }

    public boolean isFlat() {
        return this.isFlat;
    }

    public boolean shouldKeep(byte b0) {
        return (this.dataToKeep & b0) != 0;
    }

    public Optional<GlobalPos> getLastDeathLocation() {
        return this.lastDeathLocation;
    }
}
