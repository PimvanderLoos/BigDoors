package net.minecraft.network.protocol.game;

import com.google.common.collect.Lists;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.SectionPosition;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagLongArray;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.entity.TileEntityTypes;
import net.minecraft.world.level.chunk.Chunk;
import net.minecraft.world.level.chunk.ChunkSection;
import net.minecraft.world.level.levelgen.HeightMap;

public class ClientboundLevelChunkPacketData {

    private static final int TWO_MEGABYTES = 2097152;
    private final NBTTagCompound heightmaps;
    private final byte[] buffer;
    private final List<ClientboundLevelChunkPacketData.a> blockEntitiesData;

    public ClientboundLevelChunkPacketData(Chunk chunk) {
        this.heightmaps = new NBTTagCompound();
        Iterator iterator = chunk.getHeightmaps().iterator();

        Entry entry;

        while (iterator.hasNext()) {
            entry = (Entry) iterator.next();
            if (((HeightMap.Type) entry.getKey()).sendToClient()) {
                this.heightmaps.put(((HeightMap.Type) entry.getKey()).getSerializationKey(), new NBTTagLongArray(((HeightMap) entry.getValue()).getRawData()));
            }
        }

        this.buffer = new byte[calculateChunkSize(chunk)];
        extractChunkData(new PacketDataSerializer(this.getWriteBuffer()), chunk);
        this.blockEntitiesData = Lists.newArrayList();
        iterator = chunk.getBlockEntities().entrySet().iterator();

        while (iterator.hasNext()) {
            entry = (Entry) iterator.next();
            this.blockEntitiesData.add(ClientboundLevelChunkPacketData.a.create((TileEntity) entry.getValue()));
        }

    }

    public ClientboundLevelChunkPacketData(PacketDataSerializer packetdataserializer, int i, int j) {
        this.heightmaps = packetdataserializer.readNbt();
        if (this.heightmaps == null) {
            throw new RuntimeException("Can't read heightmap in packet for [" + i + ", " + j + "]");
        } else {
            int k = packetdataserializer.readVarInt();

            if (k > 2097152) {
                throw new RuntimeException("Chunk Packet trying to allocate too much memory on read.");
            } else {
                this.buffer = new byte[k];
                packetdataserializer.readBytes(this.buffer);
                this.blockEntitiesData = packetdataserializer.readList(ClientboundLevelChunkPacketData.a::new);
            }
        }
    }

    public void write(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeNbt(this.heightmaps);
        packetdataserializer.writeVarInt(this.buffer.length);
        packetdataserializer.writeBytes(this.buffer);
        packetdataserializer.writeCollection(this.blockEntitiesData, (packetdataserializer1, clientboundlevelchunkpacketdata_a) -> {
            clientboundlevelchunkpacketdata_a.write(packetdataserializer1);
        });
    }

    private static int calculateChunkSize(Chunk chunk) {
        int i = 0;
        ChunkSection[] achunksection = chunk.getSections();
        int j = achunksection.length;

        for (int k = 0; k < j; ++k) {
            ChunkSection chunksection = achunksection[k];

            i += chunksection.getSerializedSize();
        }

        return i;
    }

    private ByteBuf getWriteBuffer() {
        ByteBuf bytebuf = Unpooled.wrappedBuffer(this.buffer);

        bytebuf.writerIndex(0);
        return bytebuf;
    }

    public static void extractChunkData(PacketDataSerializer packetdataserializer, Chunk chunk) {
        ChunkSection[] achunksection = chunk.getSections();
        int i = achunksection.length;

        for (int j = 0; j < i; ++j) {
            ChunkSection chunksection = achunksection[j];

            chunksection.write(packetdataserializer);
        }

    }

    public Consumer<ClientboundLevelChunkPacketData.b> getBlockEntitiesTagsConsumer(int i, int j) {
        return (clientboundlevelchunkpacketdata_b) -> {
            this.getBlockEntitiesTags(clientboundlevelchunkpacketdata_b, i, j);
        };
    }

    private void getBlockEntitiesTags(ClientboundLevelChunkPacketData.b clientboundlevelchunkpacketdata_b, int i, int j) {
        int k = 16 * i;
        int l = 16 * j;
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();
        Iterator iterator = this.blockEntitiesData.iterator();

        while (iterator.hasNext()) {
            ClientboundLevelChunkPacketData.a clientboundlevelchunkpacketdata_a = (ClientboundLevelChunkPacketData.a) iterator.next();
            int i1 = k + SectionPosition.sectionRelative(clientboundlevelchunkpacketdata_a.packedXZ >> 4);
            int j1 = l + SectionPosition.sectionRelative(clientboundlevelchunkpacketdata_a.packedXZ);

            blockposition_mutableblockposition.set(i1, clientboundlevelchunkpacketdata_a.y, j1);
            clientboundlevelchunkpacketdata_b.accept(blockposition_mutableblockposition, clientboundlevelchunkpacketdata_a.type, clientboundlevelchunkpacketdata_a.tag);
        }

    }

    public PacketDataSerializer getReadBuffer() {
        return new PacketDataSerializer(Unpooled.wrappedBuffer(this.buffer));
    }

    public NBTTagCompound getHeightmaps() {
        return this.heightmaps;
    }

    private static class a {

        final int packedXZ;
        final int y;
        final TileEntityTypes<?> type;
        @Nullable
        final NBTTagCompound tag;

        private a(int i, int j, TileEntityTypes<?> tileentitytypes, @Nullable NBTTagCompound nbttagcompound) {
            this.packedXZ = i;
            this.y = j;
            this.type = tileentitytypes;
            this.tag = nbttagcompound;
        }

        private a(PacketDataSerializer packetdataserializer) {
            this.packedXZ = packetdataserializer.readByte();
            this.y = packetdataserializer.readShort();
            this.type = (TileEntityTypes) packetdataserializer.readById(BuiltInRegistries.BLOCK_ENTITY_TYPE);
            this.tag = packetdataserializer.readNbt();
        }

        void write(PacketDataSerializer packetdataserializer) {
            packetdataserializer.writeByte(this.packedXZ);
            packetdataserializer.writeShort(this.y);
            packetdataserializer.writeId(BuiltInRegistries.BLOCK_ENTITY_TYPE, this.type);
            packetdataserializer.writeNbt(this.tag);
        }

        static ClientboundLevelChunkPacketData.a create(TileEntity tileentity) {
            NBTTagCompound nbttagcompound = tileentity.getUpdateTag();
            BlockPosition blockposition = tileentity.getBlockPos();
            int i = SectionPosition.sectionRelative(blockposition.getX()) << 4 | SectionPosition.sectionRelative(blockposition.getZ());

            return new ClientboundLevelChunkPacketData.a(i, blockposition.getY(), tileentity.getType(), nbttagcompound.isEmpty() ? null : nbttagcompound);
        }
    }

    @FunctionalInterface
    public interface b {

        void accept(BlockPosition blockposition, TileEntityTypes<?> tileentitytypes, @Nullable NBTTagCompound nbttagcompound);
    }
}
