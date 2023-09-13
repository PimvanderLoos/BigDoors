package net.minecraft.network.protocol.game;

import com.google.common.collect.Sets;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.IRegistry;
import net.minecraft.core.IRegistryCustom;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.EnumGamemode;
import net.minecraft.world.level.World;
import net.minecraft.world.level.dimension.DimensionManager;

public record PacketPlayOutLogin(int playerId, boolean hardcore, EnumGamemode gameType, @Nullable EnumGamemode previousGameType, Set<ResourceKey<World>> levels, IRegistryCustom.Dimension registryHolder, ResourceKey<DimensionManager> dimensionType, ResourceKey<World> dimension, long seed, int maxPlayers, int chunkRadius, int simulationDistance, boolean reducedDebugInfo, boolean showDeathScreen, boolean isDebug, boolean isFlat, Optional<GlobalPos> lastDeathLocation) implements Packet<PacketListenerPlayOut> {

    public PacketPlayOutLogin(PacketDataSerializer packetdataserializer) {
        this(packetdataserializer.readInt(), packetdataserializer.readBoolean(), EnumGamemode.byId(packetdataserializer.readByte()), EnumGamemode.byNullableId(packetdataserializer.readByte()), (Set) packetdataserializer.readCollection(Sets::newHashSetWithExpectedSize, (packetdataserializer1) -> {
            return packetdataserializer1.readResourceKey(IRegistry.DIMENSION_REGISTRY);
        }), ((IRegistryCustom) packetdataserializer.readWithCodec(IRegistryCustom.NETWORK_CODEC)).freeze(), packetdataserializer.readResourceKey(IRegistry.DIMENSION_TYPE_REGISTRY), packetdataserializer.readResourceKey(IRegistry.DIMENSION_REGISTRY), packetdataserializer.readLong(), packetdataserializer.readVarInt(), packetdataserializer.readVarInt(), packetdataserializer.readVarInt(), packetdataserializer.readBoolean(), packetdataserializer.readBoolean(), packetdataserializer.readBoolean(), packetdataserializer.readBoolean(), packetdataserializer.readOptional(PacketDataSerializer::readGlobalPos));
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeInt(this.playerId);
        packetdataserializer.writeBoolean(this.hardcore);
        packetdataserializer.writeByte(this.gameType.getId());
        packetdataserializer.writeByte(EnumGamemode.getNullableId(this.previousGameType));
        packetdataserializer.writeCollection(this.levels, PacketDataSerializer::writeResourceKey);
        packetdataserializer.writeWithCodec(IRegistryCustom.NETWORK_CODEC, this.registryHolder);
        packetdataserializer.writeResourceKey(this.dimensionType);
        packetdataserializer.writeResourceKey(this.dimension);
        packetdataserializer.writeLong(this.seed);
        packetdataserializer.writeVarInt(this.maxPlayers);
        packetdataserializer.writeVarInt(this.chunkRadius);
        packetdataserializer.writeVarInt(this.simulationDistance);
        packetdataserializer.writeBoolean(this.reducedDebugInfo);
        packetdataserializer.writeBoolean(this.showDeathScreen);
        packetdataserializer.writeBoolean(this.isDebug);
        packetdataserializer.writeBoolean(this.isFlat);
        packetdataserializer.writeOptional(this.lastDeathLocation, PacketDataSerializer::writeGlobalPos);
    }

    public void handle(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.handleLogin(this);
    }
}
