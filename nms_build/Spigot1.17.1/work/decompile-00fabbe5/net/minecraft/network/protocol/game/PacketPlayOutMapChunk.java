package net.minecraft.network.protocol.game;

import com.google.common.collect.Lists;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.util.BitSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import net.minecraft.core.BlockPosition;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagLongArray;
import net.minecraft.network.PacketDataSerializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.chunk.BiomeStorage;
import net.minecraft.world.level.chunk.Chunk;
import net.minecraft.world.level.chunk.ChunkSection;
import net.minecraft.world.level.levelgen.HeightMap;

public class PacketPlayOutMapChunk implements Packet<PacketListenerPlayOut> {

    public static final int TWO_MEGABYTES = 2097152;
    private final int x;
    private final int z;
    private final BitSet availableSections;
    private final NBTTagCompound heightmaps;
    private final int[] biomes;
    private final byte[] buffer;
    private final List<NBTTagCompound> blockEntitiesTags;

    public PacketPlayOutMapChunk(Chunk chunk) {
        ChunkCoordIntPair chunkcoordintpair = chunk.getPos();

        this.x = chunkcoordintpair.x;
        this.z = chunkcoordintpair.z;
        this.heightmaps = new NBTTagCompound();
        Iterator iterator = chunk.e().iterator();

        Entry entry;

        while (iterator.hasNext()) {
            entry = (Entry) iterator.next();
            if (((HeightMap.Type) entry.getKey()).b()) {
                this.heightmaps.set(((HeightMap.Type) entry.getKey()).a(), new NBTTagLongArray(((HeightMap) entry.getValue()).a()));
            }
        }

        this.biomes = chunk.getBiomeIndex().a();
        this.buffer = new byte[this.a(chunk)];
        this.availableSections = this.a(new PacketDataSerializer(this.i()), chunk);
        this.blockEntitiesTags = Lists.newArrayList();
        iterator = chunk.getTileEntities().entrySet().iterator();

        while (iterator.hasNext()) {
            entry = (Entry) iterator.next();
            TileEntity tileentity = (TileEntity) entry.getValue();
            NBTTagCompound nbttagcompound = tileentity.Z_();

            this.blockEntitiesTags.add(nbttagcompound);
        }

    }

    public PacketPlayOutMapChunk(PacketDataSerializer packetdataserializer) {
        this.x = packetdataserializer.readInt();
        this.z = packetdataserializer.readInt();
        this.availableSections = packetdataserializer.t();
        this.heightmaps = packetdataserializer.m();
        if (this.heightmaps == null) {
            throw new RuntimeException("Can't read heightmap in packet for [" + this.x + ", " + this.z + "]");
        } else {
            this.biomes = packetdataserializer.c(BiomeStorage.MAX_SIZE);
            int i = packetdataserializer.j();

            if (i > 2097152) {
                throw new RuntimeException("Chunk Packet trying to allocate too much memory on read.");
            } else {
                this.buffer = new byte[i];
                packetdataserializer.readBytes(this.buffer);
                this.blockEntitiesTags = packetdataserializer.a(PacketDataSerializer::m);
            }
        }
    }

    @Override
    public void a(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeInt(this.x);
        packetdataserializer.writeInt(this.z);
        packetdataserializer.a(this.availableSections);
        packetdataserializer.a(this.heightmaps);
        packetdataserializer.a(this.biomes);
        packetdataserializer.d(this.buffer.length);
        packetdataserializer.writeBytes(this.buffer);
        packetdataserializer.a((Collection) this.blockEntitiesTags, PacketDataSerializer::a);
    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }

    public PacketDataSerializer b() {
        return new PacketDataSerializer(Unpooled.wrappedBuffer(this.buffer));
    }

    private ByteBuf i() {
        ByteBuf bytebuf = Unpooled.wrappedBuffer(this.buffer);

        bytebuf.writerIndex(0);
        return bytebuf;
    }

    public BitSet a(PacketDataSerializer packetdataserializer, Chunk chunk) {
        BitSet bitset = new BitSet();
        ChunkSection[] achunksection = chunk.getSections();
        int i = 0;

        for (int j = achunksection.length; i < j; ++i) {
            ChunkSection chunksection = achunksection[i];

            if (chunksection != Chunk.EMPTY_SECTION && !chunksection.c()) {
                bitset.set(i);
                chunksection.b(packetdataserializer);
            }
        }

        return bitset;
    }

    protected int a(Chunk chunk) {
        int i = 0;
        ChunkSection[] achunksection = chunk.getSections();
        int j = 0;

        for (int k = achunksection.length; j < k; ++j) {
            ChunkSection chunksection = achunksection[j];

            if (chunksection != Chunk.EMPTY_SECTION && !chunksection.c()) {
                i += chunksection.j();
            }
        }

        return i;
    }

    public int c() {
        return this.x;
    }

    public int d() {
        return this.z;
    }

    public BitSet e() {
        return this.availableSections;
    }

    public NBTTagCompound f() {
        return this.heightmaps;
    }

    public List<NBTTagCompound> g() {
        return this.blockEntitiesTags;
    }

    public int[] h() {
        return this.biomes;
    }
}
