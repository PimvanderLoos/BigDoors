package net.minecraft.server;

import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.shorts.ShortList;
import it.unimi.dsi.fastutil.shorts.ShortListIterator;
import java.util.BitSet;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Chunk implements IChunkAccess {

    private static final Logger e = LogManager.getLogger();
    public static final ChunkSection a = null;
    private final ChunkSection[] sections;
    private final BiomeBase[] g;
    private final boolean[] h;
    private final Map<BlockPosition, NBTTagCompound> i;
    private boolean j;
    public final World world;
    public final Map<HeightMap.Type, HeightMap> heightMap;
    public final int locX;
    public final int locZ;
    private boolean m;
    private final ChunkConverter n;
    public final Map<BlockPosition, TileEntity> tileEntities;
    public final EntitySlice<Entity>[] entitySlices;
    private final Map<String, StructureStart> q;
    private final Map<String, LongSet> r;
    private final ShortList[] s;
    private final TickList<Block> t;
    private final TickList<FluidType> u;
    private boolean v;
    private boolean w;
    private long lastSaved;
    private boolean y;
    private int z;
    private long A;
    private int B;
    private final ConcurrentLinkedQueue<BlockPosition> C;
    public boolean d;
    private ChunkStatus D;
    private int E;
    private final AtomicInteger F;

    public Chunk(World world, int i, int j, BiomeBase[] abiomebase, ChunkConverter chunkconverter, TickList<Block> ticklist, TickList<FluidType> ticklist1, long k) {
        this.sections = new ChunkSection[16];
        this.h = new boolean[256];
        this.i = Maps.newHashMap();
        this.heightMap = Maps.newHashMap();
        this.tileEntities = Maps.newHashMap();
        this.q = Maps.newHashMap();
        this.r = Maps.newHashMap();
        this.s = new ShortList[16];
        this.B = 4096;
        this.C = Queues.newConcurrentLinkedQueue();
        this.D = ChunkStatus.EMPTY;
        this.F = new AtomicInteger();
        this.entitySlices = (EntitySlice[]) (new EntitySlice[16]);
        this.world = world;
        this.locX = i;
        this.locZ = j;
        this.n = chunkconverter;
        HeightMap.Type[] aheightmap_type = HeightMap.Type.values();
        int l = aheightmap_type.length;

        for (int i1 = 0; i1 < l; ++i1) {
            HeightMap.Type heightmap_type = aheightmap_type[i1];

            if (heightmap_type.c() == HeightMap.Use.LIVE_WORLD) {
                this.heightMap.put(heightmap_type, new HeightMap(this, heightmap_type));
            }
        }

        for (int j1 = 0; j1 < this.entitySlices.length; ++j1) {
            this.entitySlices[j1] = new EntitySlice(Entity.class);
        }

        this.g = abiomebase;
        this.t = ticklist;
        this.u = ticklist1;
        this.A = k;
    }

    public Chunk(World world, ProtoChunk protochunk, int i, int j) {
        this(world, i, j, protochunk.getBiomeIndex(), protochunk.v(), protochunk.n(), protochunk.o(), protochunk.m());

        int k;

        for (k = 0; k < this.sections.length; ++k) {
            this.sections[k] = protochunk.getSections()[k];
        }

        Iterator iterator = protochunk.s().iterator();

        while (iterator.hasNext()) {
            NBTTagCompound nbttagcompound = (NBTTagCompound) iterator.next();

            ChunkRegionLoader.a(nbttagcompound, world, this);
        }

        iterator = protochunk.r().values().iterator();

        while (iterator.hasNext()) {
            TileEntity tileentity = (TileEntity) iterator.next();

            this.a(tileentity);
        }

        this.i.putAll(protochunk.w());

        for (k = 0; k < protochunk.u().length; ++k) {
            this.s[k] = protochunk.u()[k];
        }

        this.a(protochunk.e());
        this.b(protochunk.f());
        iterator = protochunk.t().iterator();

        while (iterator.hasNext()) {
            HeightMap.Type heightmap_type = (HeightMap.Type) iterator.next();

            if (heightmap_type.c() == HeightMap.Use.LIVE_WORLD) {
                ((HeightMap) this.heightMap.computeIfAbsent(heightmap_type, (heightmap_type) -> {
                    return new HeightMap(this, heightmap_type);
                })).a(protochunk.b(heightmap_type).b());
            }
        }

        this.y = true;
        this.a(ChunkStatus.FULLCHUNK);
    }

    public Set<BlockPosition> t() {
        HashSet hashset = Sets.newHashSet(this.i.keySet());

        hashset.addAll(this.tileEntities.keySet());
        return hashset;
    }

    public boolean a(int i, int j) {
        return i == this.locX && j == this.locZ;
    }

    public ChunkSection[] getSections() {
        return this.sections;
    }

    public void initLighting() {
        int i = this.b();

        this.z = Integer.MAX_VALUE;
        Iterator iterator = this.heightMap.values().iterator();

        while (iterator.hasNext()) {
            HeightMap heightmap = (HeightMap) iterator.next();

            heightmap.a();
        }

        for (int j = 0; j < 16; ++j) {
            for (int k = 0; k < 16; ++k) {
                if (this.world.worldProvider.g()) {
                    int l = 15;
                    int i1 = i + 16 - 1;

                    do {
                        int j1 = this.d(j, i1, k);

                        if (j1 == 0 && l != 15) {
                            j1 = 1;
                        }

                        l -= j1;
                        if (l > 0) {
                            ChunkSection chunksection = this.sections[i1 >> 4];

                            if (chunksection != Chunk.a) {
                                chunksection.a(j, i1 & 15, k, l);
                                this.world.m(new BlockPosition((this.locX << 4) + j, i1, (this.locZ << 4) + k));
                            }
                        }

                        --i1;
                    } while (i1 > 0 && l > 0);
                }
            }
        }

        this.y = true;
    }

    private void c(int i, int j) {
        this.h[i + j * 16] = true;
        this.m = true;
    }

    private void g(boolean flag) {
        this.world.methodProfiler.a("recheckGaps");
        if (this.world.areChunksLoaded(new BlockPosition(this.locX * 16 + 8, 0, this.locZ * 16 + 8), 16)) {
            for (int i = 0; i < 16; ++i) {
                for (int j = 0; j < 16; ++j) {
                    if (this.h[i + j * 16]) {
                        this.h[i + j * 16] = false;
                        int k = this.a(HeightMap.Type.LIGHT_BLOCKING, i, j);
                        int l = this.locX * 16 + i;
                        int i1 = this.locZ * 16 + j;
                        int j1 = Integer.MAX_VALUE;

                        Iterator iterator;
                        EnumDirection enumdirection;

                        for (iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator(); iterator.hasNext(); j1 = Math.min(j1, this.world.f(l + enumdirection.getAdjacentX(), i1 + enumdirection.getAdjacentZ()))) {
                            enumdirection = (EnumDirection) iterator.next();
                        }

                        this.c(l, i1, j1);
                        iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

                        while (iterator.hasNext()) {
                            enumdirection = (EnumDirection) iterator.next();
                            this.c(l + enumdirection.getAdjacentX(), i1 + enumdirection.getAdjacentZ(), k);
                        }

                        if (flag) {
                            this.world.methodProfiler.e();
                            return;
                        }
                    }
                }
            }

            this.m = false;
        }

        this.world.methodProfiler.e();
    }

    private void c(int i, int j, int k) {
        int l = this.world.getHighestBlockYAt(HeightMap.Type.MOTION_BLOCKING, new BlockPosition(i, 0, j)).getY();

        if (l > k) {
            this.a(i, j, k, l + 1);
        } else if (l < k) {
            this.a(i, j, l, k + 1);
        }

    }

    private void a(int i, int j, int k, int l) {
        if (l > k && this.world.areChunksLoaded(new BlockPosition(i, 0, j), 16)) {
            for (int i1 = k; i1 < l; ++i1) {
                this.world.c(EnumSkyBlock.SKY, new BlockPosition(i, i1, j));
            }

            this.y = true;
        }

    }

    private void a(int i, int j, int k, IBlockData iblockdata) {
        HeightMap heightmap = (HeightMap) this.heightMap.get(HeightMap.Type.LIGHT_BLOCKING);
        int l = heightmap.a(i & 15, k & 15) & 255;

        if (heightmap.a(i, j, k, iblockdata)) {
            int i1 = heightmap.a(i & 15, k & 15);
            int j1 = this.locX * 16 + i;
            int k1 = this.locZ * 16 + k;

            this.world.a(j1, k1, i1, l);
            int l1;
            int i2;
            int j2;

            if (this.world.worldProvider.g()) {
                l1 = Math.min(l, i1);
                i2 = Math.max(l, i1);
                j2 = i1 < l ? 15 : 0;

                int k2;

                for (k2 = l1; k2 < i2; ++k2) {
                    ChunkSection chunksection = this.sections[k2 >> 4];

                    if (chunksection != Chunk.a) {
                        chunksection.a(i, k2 & 15, k, j2);
                        this.world.m(new BlockPosition((this.locX << 4) + i, k2, (this.locZ << 4) + k));
                    }
                }

                k2 = 15;

                while (i1 > 0 && k2 > 0) {
                    --i1;
                    int l2 = this.d(i, i1, k);

                    l2 = l2 == 0 ? 1 : l2;
                    k2 -= l2;
                    k2 = Math.max(0, k2);
                    ChunkSection chunksection1 = this.sections[i1 >> 4];

                    if (chunksection1 != Chunk.a) {
                        chunksection1.a(i, i1 & 15, k, k2);
                    }
                }
            }

            if (i1 < this.z) {
                this.z = i1;
            }

            if (this.world.worldProvider.g()) {
                l1 = heightmap.a(i & 15, k & 15);
                i2 = Math.min(l, l1);
                j2 = Math.max(l, l1);
                Iterator iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

                while (iterator.hasNext()) {
                    EnumDirection enumdirection = (EnumDirection) iterator.next();

                    this.a(j1 + enumdirection.getAdjacentX(), k1 + enumdirection.getAdjacentZ(), i2, j2);
                }

                this.a(j1, k1, i2, j2);
            }

            this.y = true;
        }
    }

    private int d(int i, int j, int k) {
        return this.getBlockData(i, j, k).b(this.world, new BlockPosition(i, j, k));
    }

    public IBlockData getType(BlockPosition blockposition) {
        return this.getBlockData(blockposition.getX(), blockposition.getY(), blockposition.getZ());
    }

    public IBlockData getBlockData(int i, int j, int k) {
        if (this.world.R() == WorldType.DEBUG_ALL_BLOCK_STATES) {
            IBlockData iblockdata = null;

            if (j == 60) {
                iblockdata = Blocks.BARRIER.getBlockData();
            }

            if (j == 70) {
                iblockdata = ChunkProviderDebug.b(i, k);
            }

            return iblockdata == null ? Blocks.AIR.getBlockData() : iblockdata;
        } else {
            try {
                if (j >= 0 && j >> 4 < this.sections.length) {
                    ChunkSection chunksection = this.sections[j >> 4];

                    if (chunksection != Chunk.a) {
                        return chunksection.getType(i & 15, j & 15, k & 15);
                    }
                }

                return Blocks.AIR.getBlockData();
            } catch (Throwable throwable) {
                CrashReport crashreport = CrashReport.a(throwable, "Getting block state");
                CrashReportSystemDetails crashreportsystemdetails = crashreport.a("Block being got");

                crashreportsystemdetails.a("Location", () -> {
                    return CrashReportSystemDetails.a(i, j, k);
                });
                throw new ReportedException(crashreport);
            }
        }
    }

    public Fluid b(BlockPosition blockposition) {
        return this.b(blockposition.getX(), blockposition.getY(), blockposition.getZ());
    }

    public Fluid b(int i, int j, int k) {
        try {
            if (j >= 0 && j >> 4 < this.sections.length) {
                ChunkSection chunksection = this.sections[j >> 4];

                if (chunksection != Chunk.a) {
                    return chunksection.b(i & 15, j & 15, k & 15);
                }
            }

            return FluidTypes.a.i();
        } catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.a(throwable, "Getting fluid state");
            CrashReportSystemDetails crashreportsystemdetails = crashreport.a("Block being got");

            crashreportsystemdetails.a("Location", () -> {
                return CrashReportSystemDetails.a(i, j, k);
            });
            throw new ReportedException(crashreport);
        }
    }

    @Nullable
    public IBlockData a(BlockPosition blockposition, IBlockData iblockdata, boolean flag) {
        int i = blockposition.getX() & 15;
        int j = blockposition.getY();
        int k = blockposition.getZ() & 15;
        int l = ((HeightMap) this.heightMap.get(HeightMap.Type.LIGHT_BLOCKING)).a(i, k);
        IBlockData iblockdata1 = this.getType(blockposition);

        if (iblockdata1 == iblockdata) {
            return null;
        } else {
            Block block = iblockdata.getBlock();
            Block block1 = iblockdata1.getBlock();
            ChunkSection chunksection = this.sections[j >> 4];
            boolean flag1 = false;

            if (chunksection == Chunk.a) {
                if (iblockdata.isAir()) {
                    return null;
                }

                chunksection = new ChunkSection(j >> 4 << 4, this.world.worldProvider.g());
                this.sections[j >> 4] = chunksection;
                flag1 = j >= l;
            }

            chunksection.setType(i, j & 15, k, iblockdata);
            ((HeightMap) this.heightMap.get(HeightMap.Type.MOTION_BLOCKING)).a(i, j, k, iblockdata);
            ((HeightMap) this.heightMap.get(HeightMap.Type.MOTION_BLOCKING_NO_LEAVES)).a(i, j, k, iblockdata);
            ((HeightMap) this.heightMap.get(HeightMap.Type.OCEAN_FLOOR)).a(i, j, k, iblockdata);
            ((HeightMap) this.heightMap.get(HeightMap.Type.WORLD_SURFACE)).a(i, j, k, iblockdata);
            if (!this.world.isClientSide) {
                iblockdata1.remove(this.world, blockposition, iblockdata, flag);
            } else if (block1 != block && block1 instanceof ITileEntity) {
                this.world.n(blockposition);
            }

            if (chunksection.getType(i, j & 15, k).getBlock() != block) {
                return null;
            } else {
                if (flag1) {
                    this.initLighting();
                } else {
                    int i1 = iblockdata.b(this.world, blockposition);
                    int j1 = iblockdata1.b(this.world, blockposition);

                    this.a(i, j, k, iblockdata);
                    if (i1 != j1 && (i1 < j1 || this.getBrightness(EnumSkyBlock.SKY, blockposition) > 0 || this.getBrightness(EnumSkyBlock.BLOCK, blockposition) > 0)) {
                        this.c(i, k);
                    }
                }

                TileEntity tileentity;

                if (block1 instanceof ITileEntity) {
                    tileentity = this.a(blockposition, Chunk.EnumTileEntityState.CHECK);
                    if (tileentity != null) {
                        tileentity.invalidateBlockCache();
                    }
                }

                if (!this.world.isClientSide) {
                    iblockdata.onPlace(this.world, blockposition, iblockdata1);
                }

                if (block instanceof ITileEntity) {
                    tileentity = this.a(blockposition, Chunk.EnumTileEntityState.CHECK);
                    if (tileentity == null) {
                        tileentity = ((ITileEntity) block).a(this.world);
                        this.world.setTileEntity(blockposition, tileentity);
                    } else {
                        tileentity.invalidateBlockCache();
                    }
                }

                this.y = true;
                return iblockdata1;
            }
        }
    }

    public int getBrightness(EnumSkyBlock enumskyblock, BlockPosition blockposition) {
        return this.a(enumskyblock, blockposition, this.world.o().g());
    }

    public int a(EnumSkyBlock enumskyblock, BlockPosition blockposition, boolean flag) {
        int i = blockposition.getX() & 15;
        int j = blockposition.getY();
        int k = blockposition.getZ() & 15;
        int l = j >> 4;

        if (l >= 0 && l <= this.sections.length - 1) {
            ChunkSection chunksection = this.sections[l];

            return chunksection == Chunk.a ? (this.c(blockposition) ? enumskyblock.c : 0) : (enumskyblock == EnumSkyBlock.SKY ? (!flag ? 0 : chunksection.c(i, j & 15, k)) : (enumskyblock == EnumSkyBlock.BLOCK ? chunksection.d(i, j & 15, k) : enumskyblock.c));
        } else {
            return (enumskyblock != EnumSkyBlock.SKY || !flag) && enumskyblock != EnumSkyBlock.BLOCK ? 0 : enumskyblock.c;
        }
    }

    public void a(EnumSkyBlock enumskyblock, BlockPosition blockposition, int i) {
        this.a(enumskyblock, this.world.o().g(), blockposition, i);
    }

    public void a(EnumSkyBlock enumskyblock, boolean flag, BlockPosition blockposition, int i) {
        int j = blockposition.getX() & 15;
        int k = blockposition.getY();
        int l = blockposition.getZ() & 15;
        int i1 = k >> 4;

        if (i1 < 16 && i1 >= 0) {
            ChunkSection chunksection = this.sections[i1];

            if (chunksection == Chunk.a) {
                if (i == enumskyblock.c) {
                    return;
                }

                chunksection = new ChunkSection(i1 << 4, flag);
                this.sections[i1] = chunksection;
                this.initLighting();
            }

            if (enumskyblock == EnumSkyBlock.SKY) {
                if (this.world.worldProvider.g()) {
                    chunksection.a(j, k & 15, l, i);
                }
            } else if (enumskyblock == EnumSkyBlock.BLOCK) {
                chunksection.b(j, k & 15, l, i);
            }

            this.y = true;
        }
    }

    public int a(BlockPosition blockposition, int i) {
        return this.a(blockposition, i, this.world.o().g());
    }

    public int a(BlockPosition blockposition, int i, boolean flag) {
        int j = blockposition.getX() & 15;
        int k = blockposition.getY();
        int l = blockposition.getZ() & 15;
        int i1 = k >> 4;

        if (i1 >= 0 && i1 <= this.sections.length - 1) {
            ChunkSection chunksection = this.sections[i1];

            if (chunksection == Chunk.a) {
                return flag && i < EnumSkyBlock.SKY.c ? EnumSkyBlock.SKY.c - i : 0;
            } else {
                int j1 = flag ? chunksection.c(j, k & 15, l) : 0;

                j1 -= i;
                int k1 = chunksection.d(j, k & 15, l);

                if (k1 > j1) {
                    j1 = k1;
                }

                return j1;
            }
        } else {
            return 0;
        }
    }

    public void a(Entity entity) {
        this.w = true;
        int i = MathHelper.floor(entity.locX / 16.0D);
        int j = MathHelper.floor(entity.locZ / 16.0D);

        if (i != this.locX || j != this.locZ) {
            Chunk.e.warn("Wrong location! ({}, {}) should be ({}, {}), {}", Integer.valueOf(i), Integer.valueOf(j), Integer.valueOf(this.locX), Integer.valueOf(this.locZ), entity);
            entity.die();
        }

        int k = MathHelper.floor(entity.locY / 16.0D);

        if (k < 0) {
            k = 0;
        }

        if (k >= this.entitySlices.length) {
            k = this.entitySlices.length - 1;
        }

        entity.inChunk = true;
        entity.ae = this.locX;
        entity.af = k;
        entity.ag = this.locZ;
        this.entitySlices[k].add(entity);
    }

    public void a(HeightMap.Type heightmap_type, long[] along) {
        ((HeightMap) this.heightMap.get(heightmap_type)).a(along);
    }

    public void b(Entity entity) {
        this.a(entity, entity.af);
    }

    public void a(Entity entity, int i) {
        if (i < 0) {
            i = 0;
        }

        if (i >= this.entitySlices.length) {
            i = this.entitySlices.length - 1;
        }

        this.entitySlices[i].remove(entity);
    }

    public boolean c(BlockPosition blockposition) {
        int i = blockposition.getX() & 15;
        int j = blockposition.getY();
        int k = blockposition.getZ() & 15;

        return j >= ((HeightMap) this.heightMap.get(HeightMap.Type.LIGHT_BLOCKING)).a(i, k);
    }

    public int a(HeightMap.Type heightmap_type, int i, int j) {
        return ((HeightMap) this.heightMap.get(heightmap_type)).a(i & 15, j & 15) - 1;
    }

    @Nullable
    private TileEntity j(BlockPosition blockposition) {
        IBlockData iblockdata = this.getType(blockposition);
        Block block = iblockdata.getBlock();

        return !block.isTileEntity() ? null : ((ITileEntity) block).a(this.world);
    }

    @Nullable
    public TileEntity getTileEntity(BlockPosition blockposition) {
        return this.a(blockposition, Chunk.EnumTileEntityState.CHECK);
    }

    @Nullable
    public TileEntity a(BlockPosition blockposition, Chunk.EnumTileEntityState chunk_enumtileentitystate) {
        TileEntity tileentity = (TileEntity) this.tileEntities.get(blockposition);

        if (tileentity == null) {
            if (chunk_enumtileentitystate == Chunk.EnumTileEntityState.IMMEDIATE) {
                tileentity = this.j(blockposition);
                this.world.setTileEntity(blockposition, tileentity);
            } else if (chunk_enumtileentitystate == Chunk.EnumTileEntityState.QUEUED) {
                this.C.add(blockposition);
            }
        } else if (tileentity.x()) {
            this.tileEntities.remove(blockposition);
            return null;
        }

        return tileentity;
    }

    public void a(TileEntity tileentity) {
        this.a(tileentity.getPosition(), tileentity);
        if (this.j) {
            this.world.a(tileentity);
        }

    }

    public void a(BlockPosition blockposition, TileEntity tileentity) {
        tileentity.setWorld(this.world);
        tileentity.setPosition(blockposition);
        if (this.getType(blockposition).getBlock() instanceof ITileEntity) {
            if (this.tileEntities.containsKey(blockposition)) {
                ((TileEntity) this.tileEntities.get(blockposition)).y();
            }

            tileentity.z();
            this.tileEntities.put(blockposition, tileentity);
        }
    }

    public void a(NBTTagCompound nbttagcompound) {
        this.i.put(new BlockPosition(nbttagcompound.getInt("x"), nbttagcompound.getInt("y"), nbttagcompound.getInt("z")), nbttagcompound);
    }

    public void d(BlockPosition blockposition) {
        if (this.j) {
            TileEntity tileentity = (TileEntity) this.tileEntities.remove(blockposition);

            if (tileentity != null) {
                tileentity.y();
            }
        }

    }

    public void addEntities() {
        this.j = true;
        this.world.b(this.tileEntities.values());
        EntitySlice[] aentityslice = this.entitySlices;
        int i = aentityslice.length;

        for (int j = 0; j < i; ++j) {
            EntitySlice entityslice = aentityslice[j];

            this.world.a((Collection) entityslice);
        }

    }

    public void removeEntities() {
        this.j = false;
        Iterator iterator = this.tileEntities.values().iterator();

        while (iterator.hasNext()) {
            TileEntity tileentity = (TileEntity) iterator.next();

            this.world.b(tileentity);
        }

        EntitySlice[] aentityslice = this.entitySlices;
        int i = aentityslice.length;

        for (int j = 0; j < i; ++j) {
            EntitySlice entityslice = aentityslice[j];

            this.world.c((Collection) entityslice);
        }

    }

    public void markDirty() {
        this.y = true;
    }

    public void a(@Nullable Entity entity, AxisAlignedBB axisalignedbb, List<Entity> list, Predicate<? super Entity> predicate) {
        int i = MathHelper.floor((axisalignedbb.b - 2.0D) / 16.0D);
        int j = MathHelper.floor((axisalignedbb.e + 2.0D) / 16.0D);

        i = MathHelper.clamp(i, 0, this.entitySlices.length - 1);
        j = MathHelper.clamp(j, 0, this.entitySlices.length - 1);

        for (int k = i; k <= j; ++k) {
            if (!this.entitySlices[k].isEmpty()) {
                Iterator iterator = this.entitySlices[k].iterator();

                while (iterator.hasNext()) {
                    Entity entity1 = (Entity) iterator.next();

                    if (entity1.getBoundingBox().c(axisalignedbb) && entity1 != entity) {
                        if (predicate == null || predicate.test(entity1)) {
                            list.add(entity1);
                        }

                        Entity[] aentity = entity1.bi();

                        if (aentity != null) {
                            Entity[] aentity1 = aentity;
                            int l = aentity.length;

                            for (int i1 = 0; i1 < l; ++i1) {
                                Entity entity2 = aentity1[i1];

                                if (entity2 != entity && entity2.getBoundingBox().c(axisalignedbb) && (predicate == null || predicate.test(entity2))) {
                                    list.add(entity2);
                                }
                            }
                        }
                    }
                }
            }
        }

    }

    public <T extends Entity> void a(Class<? extends T> oclass, AxisAlignedBB axisalignedbb, List<T> list, @Nullable Predicate<? super T> predicate) {
        int i = MathHelper.floor((axisalignedbb.b - 2.0D) / 16.0D);
        int j = MathHelper.floor((axisalignedbb.e + 2.0D) / 16.0D);

        i = MathHelper.clamp(i, 0, this.entitySlices.length - 1);
        j = MathHelper.clamp(j, 0, this.entitySlices.length - 1);

        for (int k = i; k <= j; ++k) {
            Iterator iterator = this.entitySlices[k].c(oclass).iterator();

            while (iterator.hasNext()) {
                Entity entity = (Entity) iterator.next();

                if (entity.getBoundingBox().c(axisalignedbb) && (predicate == null || predicate.test(entity))) {
                    list.add(entity);
                }
            }
        }

    }

    public boolean c(boolean flag) {
        if (flag) {
            if (this.w && this.world.getTime() != this.lastSaved || this.y) {
                return true;
            }
        } else if (this.w && this.world.getTime() >= this.lastSaved + 600L) {
            return true;
        }

        return this.y;
    }

    public boolean isEmpty() {
        return false;
    }

    public void d(boolean flag) {
        if (this.m && this.world.worldProvider.g() && !flag) {
            this.g(this.world.isClientSide);
        }

        this.v = true;

        while (!this.C.isEmpty()) {
            BlockPosition blockposition = (BlockPosition) this.C.poll();

            if (this.a(blockposition, Chunk.EnumTileEntityState.CHECK) == null && this.getType(blockposition).getBlock().isTileEntity()) {
                TileEntity tileentity = this.j(blockposition);

                this.world.setTileEntity(blockposition, tileentity);
                this.world.a(blockposition, blockposition);
            }
        }

    }

    public boolean isReady() {
        return this.D.a(ChunkStatus.POSTPROCESSED);
    }

    public boolean v() {
        return this.v;
    }

    public ChunkCoordIntPair getPos() {
        return new ChunkCoordIntPair(this.locX, this.locZ);
    }

    public boolean b(int i, int j) {
        if (i < 0) {
            i = 0;
        }

        if (j >= 256) {
            j = 255;
        }

        for (int k = i; k <= j; k += 16) {
            ChunkSection chunksection = this.sections[k >> 4];

            if (chunksection != Chunk.a && !chunksection.a()) {
                return false;
            }
        }

        return true;
    }

    public void a(ChunkSection[] achunksection) {
        if (this.sections.length != achunksection.length) {
            Chunk.e.warn("Could not set level chunk sections, array length is {} instead of {}", Integer.valueOf(achunksection.length), Integer.valueOf(this.sections.length));
        } else {
            System.arraycopy(achunksection, 0, this.sections, 0, this.sections.length);
        }
    }

    public BiomeBase getBiome(BlockPosition blockposition) {
        int i = blockposition.getX() & 15;
        int j = blockposition.getZ() & 15;

        return this.g[j << 4 | i];
    }

    public BiomeBase[] getBiomeIndex() {
        return this.g;
    }

    public void x() {
        if (this.B < 4096) {
            BlockPosition blockposition = new BlockPosition(this.locX << 4, 0, this.locZ << 4);

            for (int i = 0; i < 8; ++i) {
                if (this.B >= 4096) {
                    return;
                }

                int j = this.B % 16;
                int k = this.B / 16 % 16;
                int l = this.B / 256;

                ++this.B;

                for (int i1 = 0; i1 < 16; ++i1) {
                    BlockPosition blockposition1 = blockposition.a(k, (j << 4) + i1, l);
                    boolean flag = i1 == 0 || i1 == 15 || k == 0 || k == 15 || l == 0 || l == 15;

                    if (this.sections[j] == Chunk.a && flag || this.sections[j] != Chunk.a && this.sections[j].getType(k, i1, l).isAir()) {
                        EnumDirection[] aenumdirection = EnumDirection.values();
                        int j1 = aenumdirection.length;

                        for (int k1 = 0; k1 < j1; ++k1) {
                            EnumDirection enumdirection = aenumdirection[k1];
                            BlockPosition blockposition2 = blockposition1.shift(enumdirection);

                            if (this.world.getType(blockposition2).e() > 0) {
                                this.world.r(blockposition2);
                            }
                        }

                        this.world.r(blockposition1);
                    }
                }
            }

        }
    }

    public boolean y() {
        return this.j;
    }

    public World getWorld() {
        return this.world;
    }

    public Set<HeightMap.Type> A() {
        return this.heightMap.keySet();
    }

    public HeightMap b(HeightMap.Type heightmap_type) {
        return (HeightMap) this.heightMap.get(heightmap_type);
    }

    public Map<BlockPosition, TileEntity> getTileEntities() {
        return this.tileEntities;
    }

    public EntitySlice<Entity>[] getEntitySlices() {
        return this.entitySlices;
    }

    public NBTTagCompound g(BlockPosition blockposition) {
        return (NBTTagCompound) this.i.get(blockposition);
    }

    public TickList<Block> k() {
        return this.t;
    }

    public TickList<FluidType> l() {
        return this.u;
    }

    public BitSet a(WorldGenStage.Features worldgenstage_features) {
        throw new RuntimeException("Not yet implemented");
    }

    public void a(boolean flag) {
        this.y = flag;
    }

    public void f(boolean flag) {
        this.w = flag;
    }

    public void setLastSaved(long i) {
        this.lastSaved = i;
    }

    @Nullable
    public StructureStart a(String s) {
        return (StructureStart) this.q.get(s);
    }

    public void a(String s, StructureStart structurestart) {
        this.q.put(s, structurestart);
    }

    public Map<String, StructureStart> e() {
        return this.q;
    }

    public void a(Map<String, StructureStart> map) {
        this.q.clear();
        this.q.putAll(map);
    }

    @Nullable
    public LongSet b(String s) {
        return (LongSet) this.r.computeIfAbsent(s, (s) -> {
            return new LongOpenHashSet();
        });
    }

    public void a(String s, long i) {
        ((LongSet) this.r.computeIfAbsent(s, (s) -> {
            return new LongOpenHashSet();
        })).add(i);
    }

    public Map<String, LongSet> f() {
        return this.r;
    }

    public void b(Map<String, LongSet> map) {
        this.r.clear();
        this.r.putAll(map);
    }

    public int D() {
        return this.z;
    }

    public long m() {
        return this.A;
    }

    public void b(long i) {
        this.A = i;
    }

    public void E() {
        this.n.a(this);
        if (!this.D.a(ChunkStatus.POSTPROCESSED) && this.E == 8) {
            ChunkCoordIntPair chunkcoordintpair = this.getPos();

            for (int i = 0; i < this.s.length; ++i) {
                if (this.s[i] != null) {
                    ShortListIterator shortlistiterator = this.s[i].iterator();

                    while (shortlistiterator.hasNext()) {
                        Short oshort = (Short) shortlistiterator.next();
                        BlockPosition blockposition = ProtoChunk.a(oshort.shortValue(), i, chunkcoordintpair);
                        IBlockData iblockdata = this.world.getType(blockposition);
                        IBlockData iblockdata1 = Block.b(iblockdata, this.world, blockposition);

                        this.world.setTypeAndData(blockposition, iblockdata1, 20);
                    }

                    this.s[i].clear();
                }
            }

            if (this.t instanceof ProtoChunkTickList) {
                ((ProtoChunkTickList) this.t).a(this.world.I(), (blockposition) -> {
                    return this.world.getType(blockposition).getBlock();
                });
            }

            if (this.u instanceof ProtoChunkTickList) {
                ((ProtoChunkTickList) this.u).a(this.world.H(), (blockposition) -> {
                    return this.world.b(blockposition).c();
                });
            }

            Iterator iterator = this.i.entrySet().iterator();

            while (iterator.hasNext()) {
                Entry entry = (Entry) iterator.next();
                BlockPosition blockposition1 = (BlockPosition) entry.getKey();
                NBTTagCompound nbttagcompound = (NBTTagCompound) entry.getValue();

                if (this.getTileEntity(blockposition1) == null) {
                    TileEntity tileentity;

                    if ("DUMMY".equals(nbttagcompound.getString("id"))) {
                        Block block = this.getType(blockposition1).getBlock();

                        if (block instanceof ITileEntity) {
                            tileentity = ((ITileEntity) block).a(this.world);
                        } else {
                            tileentity = null;
                            Chunk.e.warn("Tried to load a DUMMY block entity @ {} but found not tile entity block {} at location", blockposition1, this.getType(blockposition1));
                        }
                    } else {
                        tileentity = TileEntity.create(nbttagcompound);
                    }

                    if (tileentity != null) {
                        tileentity.setPosition(blockposition1);
                        this.a(tileentity);
                    } else {
                        Chunk.e.warn("Tried to load a block entity for block {} but failed at location {}", this.getType(blockposition1), blockposition1);
                    }
                }
            }

            this.i.clear();
            this.a(ChunkStatus.POSTPROCESSED);
        }
    }

    public ChunkConverter F() {
        return this.n;
    }

    public ShortList[] G() {
        return this.s;
    }

    public void a(short short0, int i) {
        ProtoChunk.a(this.s, i).add(short0);
    }

    public ChunkStatus i() {
        return this.D;
    }

    public void a(ChunkStatus chunkstatus) {
        this.D = chunkstatus;
    }

    public void c(String s) {
        this.a(ChunkStatus.a(s));
    }

    public void H() {
        ++this.E;
        if (this.E > 8) {
            throw new RuntimeException("Error while adding chunk to cache. Too many neighbors");
        } else {
            if (this.K()) {
                ((IAsyncTaskHandler) this.world).postToMainThread(this::E);
            }

        }
    }

    public void I() {
        --this.E;
        if (this.E < 0) {
            throw new RuntimeException("Error while removing chunk from cache. Not enough neighbors");
        }
    }

    public boolean K() {
        return this.E == 8;
    }

    public static enum EnumTileEntityState {

        IMMEDIATE, QUEUED, CHECK;

        private EnumTileEntityState() {}
    }
}
