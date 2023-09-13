package net.minecraft.network.protocol.game;

import java.util.BitSet;
import javax.annotation.Nullable;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.chunk.Chunk;
import net.minecraft.world.level.lighting.LightEngine;

public class ClientboundLevelChunkWithLightPacket implements Packet<PacketListenerPlayOut> {

    private final int x;
    private final int z;
    private final ClientboundLevelChunkPacketData chunkData;
    private final ClientboundLightUpdatePacketData lightData;

    public ClientboundLevelChunkWithLightPacket(Chunk chunk, LightEngine lightengine, @Nullable BitSet bitset, @Nullable BitSet bitset1, boolean flag) {
        ChunkCoordIntPair chunkcoordintpair = chunk.getPos();

        this.x = chunkcoordintpair.x;
        this.z = chunkcoordintpair.z;
        this.chunkData = new ClientboundLevelChunkPacketData(chunk);
        this.lightData = new ClientboundLightUpdatePacketData(chunkcoordintpair, lightengine, bitset, bitset1, flag);
    }

    public ClientboundLevelChunkWithLightPacket(PacketDataSerializer packetdataserializer) {
        this.x = packetdataserializer.readInt();
        this.z = packetdataserializer.readInt();
        this.chunkData = new ClientboundLevelChunkPacketData(packetdataserializer, this.x, this.z);
        this.lightData = new ClientboundLightUpdatePacketData(packetdataserializer, this.x, this.z);
    }

    @Override
    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeInt(this.x);
        packetdataserializer.writeInt(this.z);
        this.chunkData.write(packetdataserializer);
        this.lightData.write(packetdataserializer);
    }

    public void handle(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.handleLevelChunkWithLight(this);
    }

    public int getX() {
        return this.x;
    }

    public int getZ() {
        return this.z;
    }

    public ClientboundLevelChunkPacketData getChunkData() {
        return this.chunkData;
    }

    public ClientboundLightUpdatePacketData getLightData() {
        return this.lightData;
    }
}
