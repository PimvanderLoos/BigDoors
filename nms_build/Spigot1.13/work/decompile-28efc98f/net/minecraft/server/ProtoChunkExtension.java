package net.minecraft.server;

import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.BitSet;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.annotation.Nullable;

public class ProtoChunkExtension extends ProtoChunk {

    private final IChunkAccess a;

    public ProtoChunkExtension(IChunkAccess ichunkaccess) {
        super(ichunkaccess.getPos(), ChunkConverter.a);
        this.a = ichunkaccess;
    }

    @Nullable
    public TileEntity getTileEntity(BlockPosition blockposition) {
        return this.a.getTileEntity(blockposition);
    }

    @Nullable
    public IBlockData getType(BlockPosition blockposition) {
        return this.a.getType(blockposition);
    }

    public Fluid b(BlockPosition blockposition) {
        return this.a.b(blockposition);
    }

    public int J() {
        return this.a.J();
    }

    @Nullable
    public IBlockData a(BlockPosition blockposition, IBlockData iblockdata, boolean flag) {
        return null;
    }

    public void a(BlockPosition blockposition, TileEntity tileentity) {}

    public void a(Entity entity) {}

    public void a(ChunkStatus chunkstatus) {}

    public ChunkSection[] getSections() {
        return this.a.getSections();
    }

    public int a(EnumSkyBlock enumskyblock, BlockPosition blockposition, boolean flag) {
        return this.a.a(enumskyblock, blockposition, flag);
    }

    public int a(BlockPosition blockposition, int i, boolean flag) {
        return this.a.a(blockposition, i, flag);
    }

    public boolean c(BlockPosition blockposition) {
        return this.a.c(blockposition);
    }

    public void a(HeightMap.Type heightmap_type, long[] along) {}

    private HeightMap.Type c(HeightMap.Type heightmap_type) {
        return heightmap_type == HeightMap.Type.WORLD_SURFACE_WG ? HeightMap.Type.WORLD_SURFACE : (heightmap_type == HeightMap.Type.OCEAN_FLOOR_WG ? HeightMap.Type.OCEAN_FLOOR : heightmap_type);
    }

    public int a(HeightMap.Type heightmap_type, int i, int j) {
        return this.a.a(this.c(heightmap_type), i, j);
    }

    public ChunkCoordIntPair getPos() {
        return this.a.getPos();
    }

    public void setLastSaved(long i) {}

    @Nullable
    public StructureStart a(String s) {
        return this.a.a(s);
    }

    public void a(String s, StructureStart structurestart) {}

    public Map<String, StructureStart> e() {
        return this.a.e();
    }

    public void a(Map<String, StructureStart> map) {}

    @Nullable
    public LongSet b(String s) {
        return this.a.b(s);
    }

    public void a(String s, long i) {}

    public Map<String, LongSet> f() {
        return this.a.f();
    }

    public void b(Map<String, LongSet> map) {}

    public BiomeBase[] getBiomeIndex() {
        return this.a.getBiomeIndex();
    }

    public void a(boolean flag) {}

    public boolean h() {
        return false;
    }

    public ChunkStatus i() {
        return this.a.i();
    }

    public void d(BlockPosition blockposition) {}

    public void a(EnumSkyBlock enumskyblock, boolean flag, BlockPosition blockposition, int i) {
        this.a.a(enumskyblock, flag, blockposition, i);
    }

    public void e(BlockPosition blockposition) {}

    public void a(NBTTagCompound nbttagcompound) {}

    @Nullable
    public NBTTagCompound g(BlockPosition blockposition) {
        return this.a.g(blockposition);
    }

    public void a(BiomeBase[] abiomebase) {}

    public void a(HeightMap.Type... aheightmap_type) {}

    public List<BlockPosition> j() {
        return this.a.j();
    }

    public ProtoChunkTickList<Block> n() {
        Predicate predicate = (block) -> {
            return block.getBlockData().isAir();
        };
        RegistryBlocks registryblocks = Block.REGISTRY;

        Block.REGISTRY.getClass();
        Function function = registryblocks::b;
        RegistryBlocks registryblocks1 = Block.REGISTRY;

        Block.REGISTRY.getClass();
        return new ProtoChunkTickList(predicate, function, registryblocks1::get, this.getPos());
    }

    public ProtoChunkTickList<FluidType> o() {
        Predicate predicate = (fluidtype) -> {
            return fluidtype == FluidTypes.a;
        };
        RegistryBlocks registryblocks = FluidType.c;

        FluidType.c.getClass();
        Function function = registryblocks::b;
        RegistryBlocks registryblocks1 = FluidType.c;

        FluidType.c.getClass();
        return new ProtoChunkTickList(predicate, function, registryblocks1::get, this.getPos());
    }

    public BitSet a(WorldGenStage.Features worldgenstage_features) {
        return this.a.a(worldgenstage_features);
    }

    public void b(boolean flag) {}

    public TickList l() {
        return this.o();
    }

    public TickList k() {
        return this.n();
    }
}
