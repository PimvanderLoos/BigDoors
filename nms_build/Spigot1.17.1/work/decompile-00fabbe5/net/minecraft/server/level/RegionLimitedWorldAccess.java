package net.minecraft.server.level;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.SystemUtils;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.IRegistryCustom;
import net.minecraft.core.SectionPosition;
import net.minecraft.core.particles.ParticleParam;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.util.MathHelper;
import net.minecraft.world.DifficultyDamageScaler;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.TickList;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ITileEntity;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.IChunkAccess;
import net.minecraft.world.level.chunk.IChunkProvider;
import net.minecraft.world.level.dimension.DimensionManager;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.levelgen.HeightMap;
import net.minecraft.world.level.levelgen.feature.StructureGenerator;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.lighting.LightEngine;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidType;
import net.minecraft.world.level.storage.WorldData;
import net.minecraft.world.phys.AxisAlignedBB;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RegionLimitedWorldAccess implements GeneratorAccessSeed {

    private static final Logger LOGGER = LogManager.getLogger();
    private final List<IChunkAccess> cache;
    private final ChunkCoordIntPair center;
    private final int size;
    private final WorldServer level;
    private final long seed;
    private final WorldData levelData;
    private final Random random;
    private final DimensionManager dimensionType;
    private final TickList<Block> blockTicks = new TickListWorldGen<>((blockposition) -> {
        return this.A(blockposition).o();
    });
    private final TickList<FluidType> liquidTicks = new TickListWorldGen<>((blockposition) -> {
        return this.A(blockposition).p();
    });
    private final BiomeManager biomeManager;
    private final ChunkCoordIntPair firstPos;
    private final ChunkCoordIntPair lastPos;
    private final StructureManager structureFeatureManager;
    private final ChunkStatus generatingStatus;
    private final int writeRadiusCutoff;
    @Nullable
    private Supplier<String> currentlyGenerating;

    public RegionLimitedWorldAccess(WorldServer worldserver, List<IChunkAccess> list, ChunkStatus chunkstatus, int i) {
        this.generatingStatus = chunkstatus;
        this.writeRadiusCutoff = i;
        int j = MathHelper.floor(Math.sqrt((double) list.size()));

        if (j * j != list.size()) {
            throw (IllegalStateException) SystemUtils.c((Throwable) (new IllegalStateException("Cache size is not a square.")));
        } else {
            ChunkCoordIntPair chunkcoordintpair = ((IChunkAccess) list.get(list.size() / 2)).getPos();

            this.cache = list;
            this.center = chunkcoordintpair;
            this.size = j;
            this.level = worldserver;
            this.seed = worldserver.getSeed();
            this.levelData = worldserver.getWorldData();
            this.random = worldserver.getRandom();
            this.dimensionType = worldserver.getDimensionManager();
            this.biomeManager = new BiomeManager(this, BiomeManager.a(this.seed), worldserver.getDimensionManager().getGenLayerZoomer());
            this.firstPos = ((IChunkAccess) list.get(0)).getPos();
            this.lastPos = ((IChunkAccess) list.get(list.size() - 1)).getPos();
            this.structureFeatureManager = worldserver.getStructureManager().a(this);
        }
    }

    public ChunkCoordIntPair a() {
        return this.center;
    }

    public void a(@Nullable Supplier<String> supplier) {
        this.currentlyGenerating = supplier;
    }

    @Override
    public IChunkAccess getChunkAt(int i, int j) {
        return this.getChunkAt(i, j, ChunkStatus.EMPTY);
    }

    @Nullable
    @Override
    public IChunkAccess getChunkAt(int i, int j, ChunkStatus chunkstatus, boolean flag) {
        IChunkAccess ichunkaccess;

        if (this.isChunkLoaded(i, j)) {
            int k = i - this.firstPos.x;
            int l = j - this.firstPos.z;

            ichunkaccess = (IChunkAccess) this.cache.get(k + l * this.size);
            if (ichunkaccess.getChunkStatus().b(chunkstatus)) {
                return ichunkaccess;
            }
        } else {
            ichunkaccess = null;
        }

        if (!flag) {
            return null;
        } else {
            RegionLimitedWorldAccess.LOGGER.error("Requested chunk : {} {}", i, j);
            RegionLimitedWorldAccess.LOGGER.error("Region bounds : {} {} | {} {}", this.firstPos.x, this.firstPos.z, this.lastPos.x, this.lastPos.z);
            if (ichunkaccess != null) {
                throw (RuntimeException) SystemUtils.c((Throwable) (new RuntimeException(String.format("Chunk is not of correct status. Expecting %s, got %s | %s %s", chunkstatus, ichunkaccess.getChunkStatus(), i, j))));
            } else {
                throw (RuntimeException) SystemUtils.c((Throwable) (new RuntimeException(String.format("We are asking a region for a chunk out of bound | %s %s", i, j))));
            }
        }
    }

    @Override
    public boolean isChunkLoaded(int i, int j) {
        return i >= this.firstPos.x && i <= this.lastPos.x && j >= this.firstPos.z && j <= this.lastPos.z;
    }

    @Override
    public IBlockData getType(BlockPosition blockposition) {
        return this.getChunkAt(SectionPosition.a(blockposition.getX()), SectionPosition.a(blockposition.getZ())).getType(blockposition);
    }

    @Override
    public Fluid getFluid(BlockPosition blockposition) {
        return this.A(blockposition).getFluid(blockposition);
    }

    @Nullable
    @Override
    public EntityHuman a(double d0, double d1, double d2, double d3, Predicate<Entity> predicate) {
        return null;
    }

    @Override
    public int n_() {
        return 0;
    }

    @Override
    public BiomeManager r_() {
        return this.biomeManager;
    }

    @Override
    public BiomeBase a(int i, int j, int k) {
        return this.level.a(i, j, k);
    }

    @Override
    public float a(EnumDirection enumdirection, boolean flag) {
        return 1.0F;
    }

    @Override
    public LightEngine k_() {
        return this.level.k_();
    }

    @Override
    public boolean a(BlockPosition blockposition, boolean flag, @Nullable Entity entity, int i) {
        IBlockData iblockdata = this.getType(blockposition);

        if (iblockdata.isAir()) {
            return false;
        } else {
            if (flag) {
                TileEntity tileentity = iblockdata.isTileEntity() ? this.getTileEntity(blockposition) : null;

                Block.dropItems(iblockdata, this.level, blockposition, tileentity, entity, ItemStack.EMPTY);
            }

            return this.a(blockposition, Blocks.AIR.getBlockData(), 3, i);
        }
    }

    @Nullable
    @Override
    public TileEntity getTileEntity(BlockPosition blockposition) {
        IChunkAccess ichunkaccess = this.A(blockposition);
        TileEntity tileentity = ichunkaccess.getTileEntity(blockposition);

        if (tileentity != null) {
            return tileentity;
        } else {
            NBTTagCompound nbttagcompound = ichunkaccess.f(blockposition);
            IBlockData iblockdata = ichunkaccess.getType(blockposition);

            if (nbttagcompound != null) {
                if ("DUMMY".equals(nbttagcompound.getString("id"))) {
                    if (!iblockdata.isTileEntity()) {
                        return null;
                    }

                    tileentity = ((ITileEntity) iblockdata.getBlock()).createTile(blockposition, iblockdata);
                } else {
                    tileentity = TileEntity.create(blockposition, iblockdata, nbttagcompound);
                }

                if (tileentity != null) {
                    ichunkaccess.setTileEntity(tileentity);
                    return tileentity;
                }
            }

            if (iblockdata.isTileEntity()) {
                RegionLimitedWorldAccess.LOGGER.warn("Tried to access a block entity before it was created. {}", blockposition);
            }

            return null;
        }
    }

    @Override
    public boolean e_(BlockPosition blockposition) {
        int i = SectionPosition.a(blockposition.getX());
        int j = SectionPosition.a(blockposition.getZ());
        int k = Math.abs(this.center.x - i);
        int l = Math.abs(this.center.z - j);

        if (k <= this.writeRadiusCutoff && l <= this.writeRadiusCutoff) {
            return true;
        } else {
            SystemUtils.a("Detected setBlock in a far chunk [" + i + ", " + j + "], pos: " + blockposition + ", status: " + this.generatingStatus + (this.currentlyGenerating == null ? "" : ", currently generating: " + (String) this.currentlyGenerating.get()));
            return false;
        }
    }

    @Override
    public boolean a(BlockPosition blockposition, IBlockData iblockdata, int i, int j) {
        if (!this.e_(blockposition)) {
            return false;
        } else {
            IChunkAccess ichunkaccess = this.A(blockposition);
            IBlockData iblockdata1 = ichunkaccess.setType(blockposition, iblockdata, false);

            if (iblockdata1 != null) {
                this.level.a(blockposition, iblockdata1, iblockdata);
            }

            if (iblockdata.isTileEntity()) {
                if (ichunkaccess.getChunkStatus().getType() == ChunkStatus.Type.LEVELCHUNK) {
                    TileEntity tileentity = ((ITileEntity) iblockdata.getBlock()).createTile(blockposition, iblockdata);

                    if (tileentity != null) {
                        ichunkaccess.setTileEntity(tileentity);
                    } else {
                        ichunkaccess.removeTileEntity(blockposition);
                    }
                } else {
                    NBTTagCompound nbttagcompound = new NBTTagCompound();

                    nbttagcompound.setInt("x", blockposition.getX());
                    nbttagcompound.setInt("y", blockposition.getY());
                    nbttagcompound.setInt("z", blockposition.getZ());
                    nbttagcompound.setString("id", "DUMMY");
                    ichunkaccess.a(nbttagcompound);
                }
            } else if (iblockdata1 != null && iblockdata1.isTileEntity()) {
                ichunkaccess.removeTileEntity(blockposition);
            }

            if (iblockdata.q(this, blockposition)) {
                this.f(blockposition);
            }

            return true;
        }
    }

    private void f(BlockPosition blockposition) {
        this.A(blockposition).e(blockposition);
    }

    @Override
    public boolean addEntity(Entity entity) {
        int i = SectionPosition.a(entity.cW());
        int j = SectionPosition.a(entity.dc());

        this.getChunkAt(i, j).a(entity);
        return true;
    }

    @Override
    public boolean a(BlockPosition blockposition, boolean flag) {
        return this.setTypeAndData(blockposition, Blocks.AIR.getBlockData(), 3);
    }

    @Override
    public WorldBorder getWorldBorder() {
        return this.level.getWorldBorder();
    }

    @Override
    public boolean isClientSide() {
        return false;
    }

    @Deprecated
    @Override
    public WorldServer getLevel() {
        return this.level;
    }

    @Override
    public IRegistryCustom t() {
        return this.level.t();
    }

    @Override
    public WorldData getWorldData() {
        return this.levelData;
    }

    @Override
    public DifficultyDamageScaler getDamageScaler(BlockPosition blockposition) {
        if (!this.isChunkLoaded(SectionPosition.a(blockposition.getX()), SectionPosition.a(blockposition.getZ()))) {
            throw new RuntimeException("We are asking a region for a chunk out of bound");
        } else {
            return new DifficultyDamageScaler(this.level.getDifficulty(), this.level.getDayTime(), 0L, this.level.ak());
        }
    }

    @Nullable
    @Override
    public MinecraftServer getMinecraftServer() {
        return this.level.getMinecraftServer();
    }

    @Override
    public IChunkProvider getChunkProvider() {
        return this.level.getChunkProvider();
    }

    @Override
    public long getSeed() {
        return this.seed;
    }

    @Override
    public TickList<Block> getBlockTickList() {
        return this.blockTicks;
    }

    @Override
    public TickList<FluidType> getFluidTickList() {
        return this.liquidTicks;
    }

    @Override
    public int getSeaLevel() {
        return this.level.getSeaLevel();
    }

    @Override
    public Random getRandom() {
        return this.random;
    }

    @Override
    public int a(HeightMap.Type heightmap_type, int i, int j) {
        return this.getChunkAt(SectionPosition.a(i), SectionPosition.a(j)).getHighestBlock(heightmap_type, i & 15, j & 15) + 1;
    }

    @Override
    public void playSound(@Nullable EntityHuman entityhuman, BlockPosition blockposition, SoundEffect soundeffect, SoundCategory soundcategory, float f, float f1) {}

    @Override
    public void addParticle(ParticleParam particleparam, double d0, double d1, double d2, double d3, double d4, double d5) {}

    @Override
    public void a(@Nullable EntityHuman entityhuman, int i, BlockPosition blockposition, int j) {}

    @Override
    public void a(@Nullable Entity entity, GameEvent gameevent, BlockPosition blockposition) {}

    @Override
    public DimensionManager getDimensionManager() {
        return this.dimensionType;
    }

    @Override
    public boolean a(BlockPosition blockposition, Predicate<IBlockData> predicate) {
        return predicate.test(this.getType(blockposition));
    }

    @Override
    public boolean b(BlockPosition blockposition, Predicate<Fluid> predicate) {
        return predicate.test(this.getFluid(blockposition));
    }

    @Override
    public <T extends Entity> List<T> a(EntityTypeTest<Entity, T> entitytypetest, AxisAlignedBB axisalignedbb, Predicate<? super T> predicate) {
        return Collections.emptyList();
    }

    @Override
    public List<Entity> getEntities(@Nullable Entity entity, AxisAlignedBB axisalignedbb, @Nullable Predicate<? super Entity> predicate) {
        return Collections.emptyList();
    }

    @Override
    public List<EntityHuman> getPlayers() {
        return Collections.emptyList();
    }

    @Override
    public Stream<? extends StructureStart<?>> a(SectionPosition sectionposition, StructureGenerator<?> structuregenerator) {
        return this.structureFeatureManager.a(sectionposition, structuregenerator);
    }

    @Override
    public int getMinBuildHeight() {
        return this.level.getMinBuildHeight();
    }

    @Override
    public int getHeight() {
        return this.level.getHeight();
    }
}
