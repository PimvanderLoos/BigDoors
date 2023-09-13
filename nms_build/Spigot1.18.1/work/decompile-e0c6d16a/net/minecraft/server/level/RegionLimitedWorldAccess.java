package net.minecraft.server.level;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Predicate;
import java.util.function.Supplier;
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
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.StructureManager;
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
import net.minecraft.world.ticks.LevelTickAccess;
import net.minecraft.world.ticks.TickListWorldGen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RegionLimitedWorldAccess implements GeneratorAccessSeed {

    private static final Logger LOGGER = LogManager.getLogger();
    private final List<IChunkAccess> cache;
    private final IChunkAccess center;
    private final int size;
    private final WorldServer level;
    private final long seed;
    private final WorldData levelData;
    private final Random random;
    private final DimensionManager dimensionType;
    private final TickListWorldGen<Block> blockTicks = new TickListWorldGen<>((blockposition) -> {
        return this.getChunk(blockposition).getBlockTicks();
    });
    private final TickListWorldGen<FluidType> fluidTicks = new TickListWorldGen<>((blockposition) -> {
        return this.getChunk(blockposition).getFluidTicks();
    });
    private final BiomeManager biomeManager;
    private final ChunkCoordIntPair firstPos;
    private final ChunkCoordIntPair lastPos;
    private final StructureManager structureFeatureManager;
    private final ChunkStatus generatingStatus;
    private final int writeRadiusCutoff;
    @Nullable
    private Supplier<String> currentlyGenerating;
    private final AtomicLong subTickCount = new AtomicLong();

    public RegionLimitedWorldAccess(WorldServer worldserver, List<IChunkAccess> list, ChunkStatus chunkstatus, int i) {
        this.generatingStatus = chunkstatus;
        this.writeRadiusCutoff = i;
        int j = MathHelper.floor(Math.sqrt((double) list.size()));

        if (j * j != list.size()) {
            throw (IllegalStateException) SystemUtils.pauseInIde(new IllegalStateException("Cache size is not a square."));
        } else {
            this.cache = list;
            this.center = (IChunkAccess) list.get(list.size() / 2);
            this.size = j;
            this.level = worldserver;
            this.seed = worldserver.getSeed();
            this.levelData = worldserver.getLevelData();
            this.random = worldserver.getRandom();
            this.dimensionType = worldserver.dimensionType();
            this.biomeManager = new BiomeManager(this, BiomeManager.obfuscateSeed(this.seed));
            this.firstPos = ((IChunkAccess) list.get(0)).getPos();
            this.lastPos = ((IChunkAccess) list.get(list.size() - 1)).getPos();
            this.structureFeatureManager = worldserver.structureFeatureManager().forWorldGenRegion(this);
        }
    }

    public ChunkCoordIntPair getCenter() {
        return this.center.getPos();
    }

    @Override
    public void setCurrentlyGenerating(@Nullable Supplier<String> supplier) {
        this.currentlyGenerating = supplier;
    }

    @Override
    public IChunkAccess getChunk(int i, int j) {
        return this.getChunk(i, j, ChunkStatus.EMPTY);
    }

    @Nullable
    @Override
    public IChunkAccess getChunk(int i, int j, ChunkStatus chunkstatus, boolean flag) {
        IChunkAccess ichunkaccess;

        if (this.hasChunk(i, j)) {
            int k = i - this.firstPos.x;
            int l = j - this.firstPos.z;

            ichunkaccess = (IChunkAccess) this.cache.get(k + l * this.size);
            if (ichunkaccess.getStatus().isOrAfter(chunkstatus)) {
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
                throw (RuntimeException) SystemUtils.pauseInIde(new RuntimeException(String.format("Chunk is not of correct status. Expecting %s, got %s | %s %s", chunkstatus, ichunkaccess.getStatus(), i, j)));
            } else {
                throw (RuntimeException) SystemUtils.pauseInIde(new RuntimeException(String.format("We are asking a region for a chunk out of bound | %s %s", i, j)));
            }
        }
    }

    @Override
    public boolean hasChunk(int i, int j) {
        return i >= this.firstPos.x && i <= this.lastPos.x && j >= this.firstPos.z && j <= this.lastPos.z;
    }

    @Override
    public IBlockData getBlockState(BlockPosition blockposition) {
        return this.getChunk(SectionPosition.blockToSectionCoord(blockposition.getX()), SectionPosition.blockToSectionCoord(blockposition.getZ())).getBlockState(blockposition);
    }

    @Override
    public Fluid getFluidState(BlockPosition blockposition) {
        return this.getChunk(blockposition).getFluidState(blockposition);
    }

    @Nullable
    @Override
    public EntityHuman getNearestPlayer(double d0, double d1, double d2, double d3, Predicate<Entity> predicate) {
        return null;
    }

    @Override
    public int getSkyDarken() {
        return 0;
    }

    @Override
    public BiomeManager getBiomeManager() {
        return this.biomeManager;
    }

    @Override
    public BiomeBase getUncachedNoiseBiome(int i, int j, int k) {
        return this.level.getUncachedNoiseBiome(i, j, k);
    }

    @Override
    public float getShade(EnumDirection enumdirection, boolean flag) {
        return 1.0F;
    }

    @Override
    public LightEngine getLightEngine() {
        return this.level.getLightEngine();
    }

    @Override
    public boolean destroyBlock(BlockPosition blockposition, boolean flag, @Nullable Entity entity, int i) {
        IBlockData iblockdata = this.getBlockState(blockposition);

        if (iblockdata.isAir()) {
            return false;
        } else {
            if (flag) {
                TileEntity tileentity = iblockdata.hasBlockEntity() ? this.getBlockEntity(blockposition) : null;

                Block.dropResources(iblockdata, this.level, blockposition, tileentity, entity, ItemStack.EMPTY);
            }

            return this.setBlock(blockposition, Blocks.AIR.defaultBlockState(), 3, i);
        }
    }

    @Nullable
    @Override
    public TileEntity getBlockEntity(BlockPosition blockposition) {
        IChunkAccess ichunkaccess = this.getChunk(blockposition);
        TileEntity tileentity = ichunkaccess.getBlockEntity(blockposition);

        if (tileentity != null) {
            return tileentity;
        } else {
            NBTTagCompound nbttagcompound = ichunkaccess.getBlockEntityNbt(blockposition);
            IBlockData iblockdata = ichunkaccess.getBlockState(blockposition);

            if (nbttagcompound != null) {
                if ("DUMMY".equals(nbttagcompound.getString("id"))) {
                    if (!iblockdata.hasBlockEntity()) {
                        return null;
                    }

                    tileentity = ((ITileEntity) iblockdata.getBlock()).newBlockEntity(blockposition, iblockdata);
                } else {
                    tileentity = TileEntity.loadStatic(blockposition, iblockdata, nbttagcompound);
                }

                if (tileentity != null) {
                    ichunkaccess.setBlockEntity(tileentity);
                    return tileentity;
                }
            }

            if (iblockdata.hasBlockEntity()) {
                RegionLimitedWorldAccess.LOGGER.warn("Tried to access a block entity before it was created. {}", blockposition);
            }

            return null;
        }
    }

    @Override
    public boolean ensureCanWrite(BlockPosition blockposition) {
        int i = SectionPosition.blockToSectionCoord(blockposition.getX());
        int j = SectionPosition.blockToSectionCoord(blockposition.getZ());
        ChunkCoordIntPair chunkcoordintpair = this.getCenter();
        int k = Math.abs(chunkcoordintpair.x - i);
        int l = Math.abs(chunkcoordintpair.z - j);

        if (k <= this.writeRadiusCutoff && l <= this.writeRadiusCutoff) {
            if (this.center.isUpgrading()) {
                LevelHeightAccessor levelheightaccessor = this.center.getHeightAccessorForGeneration();

                if (blockposition.getY() < levelheightaccessor.getMinBuildHeight() || blockposition.getY() >= levelheightaccessor.getMaxBuildHeight()) {
                    return false;
                }
            }

            return true;
        } else {
            SystemUtils.logAndPauseIfInIde("Detected setBlock in a far chunk [" + i + ", " + j + "], pos: " + blockposition + ", status: " + this.generatingStatus + (this.currentlyGenerating == null ? "" : ", currently generating: " + (String) this.currentlyGenerating.get()));
            return false;
        }
    }

    @Override
    public boolean setBlock(BlockPosition blockposition, IBlockData iblockdata, int i, int j) {
        if (!this.ensureCanWrite(blockposition)) {
            return false;
        } else {
            IChunkAccess ichunkaccess = this.getChunk(blockposition);
            IBlockData iblockdata1 = ichunkaccess.setBlockState(blockposition, iblockdata, false);

            if (iblockdata1 != null) {
                this.level.onBlockStateChange(blockposition, iblockdata1, iblockdata);
            }

            if (iblockdata.hasBlockEntity()) {
                if (ichunkaccess.getStatus().getChunkType() == ChunkStatus.Type.LEVELCHUNK) {
                    TileEntity tileentity = ((ITileEntity) iblockdata.getBlock()).newBlockEntity(blockposition, iblockdata);

                    if (tileentity != null) {
                        ichunkaccess.setBlockEntity(tileentity);
                    } else {
                        ichunkaccess.removeBlockEntity(blockposition);
                    }
                } else {
                    NBTTagCompound nbttagcompound = new NBTTagCompound();

                    nbttagcompound.putInt("x", blockposition.getX());
                    nbttagcompound.putInt("y", blockposition.getY());
                    nbttagcompound.putInt("z", blockposition.getZ());
                    nbttagcompound.putString("id", "DUMMY");
                    ichunkaccess.setBlockEntityNbt(nbttagcompound);
                }
            } else if (iblockdata1 != null && iblockdata1.hasBlockEntity()) {
                ichunkaccess.removeBlockEntity(blockposition);
            }

            if (iblockdata.hasPostProcess(this, blockposition)) {
                this.markPosForPostprocessing(blockposition);
            }

            return true;
        }
    }

    private void markPosForPostprocessing(BlockPosition blockposition) {
        this.getChunk(blockposition).markPosForPostprocessing(blockposition);
    }

    @Override
    public boolean addFreshEntity(Entity entity) {
        int i = SectionPosition.blockToSectionCoord(entity.getBlockX());
        int j = SectionPosition.blockToSectionCoord(entity.getBlockZ());

        this.getChunk(i, j).addEntity(entity);
        return true;
    }

    @Override
    public boolean removeBlock(BlockPosition blockposition, boolean flag) {
        return this.setBlock(blockposition, Blocks.AIR.defaultBlockState(), 3);
    }

    @Override
    public WorldBorder getWorldBorder() {
        return this.level.getWorldBorder();
    }

    @Override
    public boolean isClientSide() {
        return false;
    }

    /** @deprecated */
    @Deprecated
    @Override
    public WorldServer getLevel() {
        return this.level;
    }

    @Override
    public IRegistryCustom registryAccess() {
        return this.level.registryAccess();
    }

    @Override
    public WorldData getLevelData() {
        return this.levelData;
    }

    @Override
    public DifficultyDamageScaler getCurrentDifficultyAt(BlockPosition blockposition) {
        if (!this.hasChunk(SectionPosition.blockToSectionCoord(blockposition.getX()), SectionPosition.blockToSectionCoord(blockposition.getZ()))) {
            throw new RuntimeException("We are asking a region for a chunk out of bound");
        } else {
            return new DifficultyDamageScaler(this.level.getDifficulty(), this.level.getDayTime(), 0L, this.level.getMoonBrightness());
        }
    }

    @Nullable
    @Override
    public MinecraftServer getServer() {
        return this.level.getServer();
    }

    @Override
    public IChunkProvider getChunkSource() {
        return this.level.getChunkSource();
    }

    @Override
    public long getSeed() {
        return this.seed;
    }

    @Override
    public LevelTickAccess<Block> getBlockTicks() {
        return this.blockTicks;
    }

    @Override
    public LevelTickAccess<FluidType> getFluidTicks() {
        return this.fluidTicks;
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
    public int getHeight(HeightMap.Type heightmap_type, int i, int j) {
        return this.getChunk(SectionPosition.blockToSectionCoord(i), SectionPosition.blockToSectionCoord(j)).getHeight(heightmap_type, i & 15, j & 15) + 1;
    }

    @Override
    public void playSound(@Nullable EntityHuman entityhuman, BlockPosition blockposition, SoundEffect soundeffect, SoundCategory soundcategory, float f, float f1) {}

    @Override
    public void addParticle(ParticleParam particleparam, double d0, double d1, double d2, double d3, double d4, double d5) {}

    @Override
    public void levelEvent(@Nullable EntityHuman entityhuman, int i, BlockPosition blockposition, int j) {}

    @Override
    public void gameEvent(@Nullable Entity entity, GameEvent gameevent, BlockPosition blockposition) {}

    @Override
    public DimensionManager dimensionType() {
        return this.dimensionType;
    }

    @Override
    public boolean isStateAtPosition(BlockPosition blockposition, Predicate<IBlockData> predicate) {
        return predicate.test(this.getBlockState(blockposition));
    }

    @Override
    public boolean isFluidAtPosition(BlockPosition blockposition, Predicate<Fluid> predicate) {
        return predicate.test(this.getFluidState(blockposition));
    }

    @Override
    public <T extends Entity> List<T> getEntities(EntityTypeTest<Entity, T> entitytypetest, AxisAlignedBB axisalignedbb, Predicate<? super T> predicate) {
        return Collections.emptyList();
    }

    @Override
    public List<Entity> getEntities(@Nullable Entity entity, AxisAlignedBB axisalignedbb, @Nullable Predicate<? super Entity> predicate) {
        return Collections.emptyList();
    }

    @Override
    public List<EntityHuman> players() {
        return Collections.emptyList();
    }

    @Override
    public List<? extends StructureStart<?>> startsForFeature(SectionPosition sectionposition, StructureGenerator<?> structuregenerator) {
        return this.structureFeatureManager.startsForFeature(sectionposition, structuregenerator);
    }

    @Override
    public int getMinBuildHeight() {
        return this.level.getMinBuildHeight();
    }

    @Override
    public int getHeight() {
        return this.level.getHeight();
    }

    @Override
    public long nextSubTickCount() {
        return this.subTickCount.getAndIncrement();
    }
}
