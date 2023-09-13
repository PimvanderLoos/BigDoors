package net.minecraft.world.level;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportSystemDetails;
import net.minecraft.ReportedException;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.IRegistry;
import net.minecraft.core.SectionPosition;
import net.minecraft.core.particles.ParticleParam;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.PlayerChunk;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.tags.ITagRegistry;
import net.minecraft.util.MathHelper;
import net.minecraft.util.profiling.GameProfilerFiller;
import net.minecraft.world.DifficultyDamageScaler;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.boss.EntityComplexPart;
import net.minecraft.world.entity.boss.enderdragon.EntityEnderDragon;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingManager;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BlockFireAbstract;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.TickingBlockEntity;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.chunk.Chunk;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.IChunkAccess;
import net.minecraft.world.level.chunk.IChunkProvider;
import net.minecraft.world.level.dimension.DimensionManager;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.entity.LevelEntityGetter;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.levelgen.HeightMap;
import net.minecraft.world.level.lighting.LightEngine;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidTypes;
import net.minecraft.world.level.saveddata.maps.WorldMap;
import net.minecraft.world.level.storage.WorldData;
import net.minecraft.world.level.storage.WorldDataMutable;
import net.minecraft.world.phys.AxisAlignedBB;
import net.minecraft.world.scores.Scoreboard;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class World implements GeneratorAccess, AutoCloseable {

    protected static final Logger LOGGER = LogManager.getLogger();
    public static final Codec<ResourceKey<World>> RESOURCE_KEY_CODEC = MinecraftKey.CODEC.xmap(ResourceKey.elementKey(IRegistry.DIMENSION_REGISTRY), ResourceKey::location);
    public static final ResourceKey<World> OVERWORLD = ResourceKey.create(IRegistry.DIMENSION_REGISTRY, new MinecraftKey("overworld"));
    public static final ResourceKey<World> NETHER = ResourceKey.create(IRegistry.DIMENSION_REGISTRY, new MinecraftKey("the_nether"));
    public static final ResourceKey<World> END = ResourceKey.create(IRegistry.DIMENSION_REGISTRY, new MinecraftKey("the_end"));
    public static final int MAX_LEVEL_SIZE = 30000000;
    public static final int LONG_PARTICLE_CLIP_RANGE = 512;
    public static final int SHORT_PARTICLE_CLIP_RANGE = 32;
    private static final EnumDirection[] DIRECTIONS = EnumDirection.values();
    public static final int MAX_BRIGHTNESS = 15;
    public static final int TICKS_PER_DAY = 24000;
    public static final int MAX_ENTITY_SPAWN_Y = 20000000;
    public static final int MIN_ENTITY_SPAWN_Y = -20000000;
    protected final List<TickingBlockEntity> blockEntityTickers = Lists.newArrayList();
    private final List<TickingBlockEntity> pendingBlockEntityTickers = Lists.newArrayList();
    private boolean tickingBlockEntities;
    public final Thread thread;
    private final boolean isDebug;
    private int skyDarken;
    protected int randValue = (new Random()).nextInt();
    protected final int addend = 1013904223;
    protected float oRainLevel;
    public float rainLevel;
    protected float oThunderLevel;
    public float thunderLevel;
    public final Random random = new Random();
    private final DimensionManager dimensionType;
    public final WorldDataMutable levelData;
    private final Supplier<GameProfilerFiller> profiler;
    public final boolean isClientSide;
    private final WorldBorder worldBorder;
    private final BiomeManager biomeManager;
    private final ResourceKey<World> dimension;
    private long subTickCount;

    protected World(WorldDataMutable worlddatamutable, ResourceKey<World> resourcekey, final DimensionManager dimensionmanager, Supplier<GameProfilerFiller> supplier, boolean flag, boolean flag1, long i) {
        this.profiler = supplier;
        this.levelData = worlddatamutable;
        this.dimensionType = dimensionmanager;
        this.dimension = resourcekey;
        this.isClientSide = flag;
        if (dimensionmanager.coordinateScale() != 1.0D) {
            this.worldBorder = new WorldBorder() {
                @Override
                public double getCenterX() {
                    return super.getCenterX() / dimensionmanager.coordinateScale();
                }

                @Override
                public double getCenterZ() {
                    return super.getCenterZ() / dimensionmanager.coordinateScale();
                }
            };
        } else {
            this.worldBorder = new WorldBorder();
        }

        this.thread = Thread.currentThread();
        this.biomeManager = new BiomeManager(this, i);
        this.isDebug = flag1;
    }

    @Override
    public boolean isClientSide() {
        return this.isClientSide;
    }

    @Nullable
    @Override
    public MinecraftServer getServer() {
        return null;
    }

    public boolean isInWorldBounds(BlockPosition blockposition) {
        return !this.isOutsideBuildHeight(blockposition) && isInWorldBoundsHorizontal(blockposition);
    }

    public static boolean isInSpawnableBounds(BlockPosition blockposition) {
        return !isOutsideSpawnableHeight(blockposition.getY()) && isInWorldBoundsHorizontal(blockposition);
    }

    private static boolean isInWorldBoundsHorizontal(BlockPosition blockposition) {
        return blockposition.getX() >= -30000000 && blockposition.getZ() >= -30000000 && blockposition.getX() < 30000000 && blockposition.getZ() < 30000000;
    }

    private static boolean isOutsideSpawnableHeight(int i) {
        return i < -20000000 || i >= 20000000;
    }

    public Chunk getChunkAt(BlockPosition blockposition) {
        return this.getChunk(SectionPosition.blockToSectionCoord(blockposition.getX()), SectionPosition.blockToSectionCoord(blockposition.getZ()));
    }

    @Override
    public Chunk getChunk(int i, int j) {
        return (Chunk) this.getChunk(i, j, ChunkStatus.FULL);
    }

    @Nullable
    @Override
    public IChunkAccess getChunk(int i, int j, ChunkStatus chunkstatus, boolean flag) {
        IChunkAccess ichunkaccess = this.getChunkSource().getChunk(i, j, chunkstatus, flag);

        if (ichunkaccess == null && flag) {
            throw new IllegalStateException("Should always be able to create a chunk!");
        } else {
            return ichunkaccess;
        }
    }

    @Override
    public boolean setBlock(BlockPosition blockposition, IBlockData iblockdata, int i) {
        return this.setBlock(blockposition, iblockdata, i, 512);
    }

    @Override
    public boolean setBlock(BlockPosition blockposition, IBlockData iblockdata, int i, int j) {
        if (this.isOutsideBuildHeight(blockposition)) {
            return false;
        } else if (!this.isClientSide && this.isDebug()) {
            return false;
        } else {
            Chunk chunk = this.getChunkAt(blockposition);
            Block block = iblockdata.getBlock();
            IBlockData iblockdata1 = chunk.setBlockState(blockposition, iblockdata, (i & 64) != 0);

            if (iblockdata1 == null) {
                return false;
            } else {
                IBlockData iblockdata2 = this.getBlockState(blockposition);

                if ((i & 128) == 0 && iblockdata2 != iblockdata1 && (iblockdata2.getLightBlock(this, blockposition) != iblockdata1.getLightBlock(this, blockposition) || iblockdata2.getLightEmission() != iblockdata1.getLightEmission() || iblockdata2.useShapeForLightOcclusion() || iblockdata1.useShapeForLightOcclusion())) {
                    this.getProfiler().push("queueCheckLight");
                    this.getChunkSource().getLightEngine().checkBlock(blockposition);
                    this.getProfiler().pop();
                }

                if (iblockdata2 == iblockdata) {
                    if (iblockdata1 != iblockdata2) {
                        this.setBlocksDirty(blockposition, iblockdata1, iblockdata2);
                    }

                    if ((i & 2) != 0 && (!this.isClientSide || (i & 4) == 0) && (this.isClientSide || chunk.getFullStatus() != null && chunk.getFullStatus().isOrAfter(PlayerChunk.State.TICKING))) {
                        this.sendBlockUpdated(blockposition, iblockdata1, iblockdata, i);
                    }

                    if ((i & 1) != 0) {
                        this.blockUpdated(blockposition, iblockdata1.getBlock());
                        if (!this.isClientSide && iblockdata.hasAnalogOutputSignal()) {
                            this.updateNeighbourForOutputSignal(blockposition, block);
                        }
                    }

                    if ((i & 16) == 0 && j > 0) {
                        int k = i & -34;

                        iblockdata1.updateIndirectNeighbourShapes(this, blockposition, k, j - 1);
                        iblockdata.updateNeighbourShapes(this, blockposition, k, j - 1);
                        iblockdata.updateIndirectNeighbourShapes(this, blockposition, k, j - 1);
                    }

                    this.onBlockStateChange(blockposition, iblockdata1, iblockdata2);
                }

                return true;
            }
        }
    }

    public void onBlockStateChange(BlockPosition blockposition, IBlockData iblockdata, IBlockData iblockdata1) {}

    @Override
    public boolean removeBlock(BlockPosition blockposition, boolean flag) {
        Fluid fluid = this.getFluidState(blockposition);

        return this.setBlock(blockposition, fluid.createLegacyBlock(), 3 | (flag ? 64 : 0));
    }

    @Override
    public boolean destroyBlock(BlockPosition blockposition, boolean flag, @Nullable Entity entity, int i) {
        IBlockData iblockdata = this.getBlockState(blockposition);

        if (iblockdata.isAir()) {
            return false;
        } else {
            Fluid fluid = this.getFluidState(blockposition);

            if (!(iblockdata.getBlock() instanceof BlockFireAbstract)) {
                this.levelEvent(2001, blockposition, Block.getId(iblockdata));
            }

            if (flag) {
                TileEntity tileentity = iblockdata.hasBlockEntity() ? this.getBlockEntity(blockposition) : null;

                Block.dropResources(iblockdata, this, blockposition, tileentity, entity, ItemStack.EMPTY);
            }

            boolean flag1 = this.setBlock(blockposition, fluid.createLegacyBlock(), 3, i);

            if (flag1) {
                this.gameEvent(entity, GameEvent.BLOCK_DESTROY, blockposition);
            }

            return flag1;
        }
    }

    public void addDestroyBlockEffect(BlockPosition blockposition, IBlockData iblockdata) {}

    public boolean setBlockAndUpdate(BlockPosition blockposition, IBlockData iblockdata) {
        return this.setBlock(blockposition, iblockdata, 3);
    }

    public abstract void sendBlockUpdated(BlockPosition blockposition, IBlockData iblockdata, IBlockData iblockdata1, int i);

    public void setBlocksDirty(BlockPosition blockposition, IBlockData iblockdata, IBlockData iblockdata1) {}

    public void updateNeighborsAt(BlockPosition blockposition, Block block) {
        this.neighborChanged(blockposition.west(), block, blockposition);
        this.neighborChanged(blockposition.east(), block, blockposition);
        this.neighborChanged(blockposition.below(), block, blockposition);
        this.neighborChanged(blockposition.above(), block, blockposition);
        this.neighborChanged(blockposition.north(), block, blockposition);
        this.neighborChanged(blockposition.south(), block, blockposition);
    }

    public void updateNeighborsAtExceptFromFacing(BlockPosition blockposition, Block block, EnumDirection enumdirection) {
        if (enumdirection != EnumDirection.WEST) {
            this.neighborChanged(blockposition.west(), block, blockposition);
        }

        if (enumdirection != EnumDirection.EAST) {
            this.neighborChanged(blockposition.east(), block, blockposition);
        }

        if (enumdirection != EnumDirection.DOWN) {
            this.neighborChanged(blockposition.below(), block, blockposition);
        }

        if (enumdirection != EnumDirection.UP) {
            this.neighborChanged(blockposition.above(), block, blockposition);
        }

        if (enumdirection != EnumDirection.NORTH) {
            this.neighborChanged(blockposition.north(), block, blockposition);
        }

        if (enumdirection != EnumDirection.SOUTH) {
            this.neighborChanged(blockposition.south(), block, blockposition);
        }

    }

    public void neighborChanged(BlockPosition blockposition, Block block, BlockPosition blockposition1) {
        if (!this.isClientSide) {
            IBlockData iblockdata = this.getBlockState(blockposition);

            try {
                iblockdata.neighborChanged(this, blockposition, block, blockposition1, false);
            } catch (Throwable throwable) {
                CrashReport crashreport = CrashReport.forThrowable(throwable, "Exception while updating neighbours");
                CrashReportSystemDetails crashreportsystemdetails = crashreport.addCategory("Block being updated");

                crashreportsystemdetails.setDetail("Source block type", () -> {
                    try {
                        return String.format("ID #%s (%s // %s)", IRegistry.BLOCK.getKey(block), block.getDescriptionId(), block.getClass().getCanonicalName());
                    } catch (Throwable throwable1) {
                        return "ID #" + IRegistry.BLOCK.getKey(block);
                    }
                });
                CrashReportSystemDetails.populateBlockDetails(crashreportsystemdetails, this, blockposition, iblockdata);
                throw new ReportedException(crashreport);
            }
        }
    }

    @Override
    public int getHeight(HeightMap.Type heightmap_type, int i, int j) {
        int k;

        if (i >= -30000000 && j >= -30000000 && i < 30000000 && j < 30000000) {
            if (this.hasChunk(SectionPosition.blockToSectionCoord(i), SectionPosition.blockToSectionCoord(j))) {
                k = this.getChunk(SectionPosition.blockToSectionCoord(i), SectionPosition.blockToSectionCoord(j)).getHeight(heightmap_type, i & 15, j & 15) + 1;
            } else {
                k = this.getMinBuildHeight();
            }
        } else {
            k = this.getSeaLevel() + 1;
        }

        return k;
    }

    @Override
    public LightEngine getLightEngine() {
        return this.getChunkSource().getLightEngine();
    }

    @Override
    public IBlockData getBlockState(BlockPosition blockposition) {
        if (this.isOutsideBuildHeight(blockposition)) {
            return Blocks.VOID_AIR.defaultBlockState();
        } else {
            Chunk chunk = this.getChunk(SectionPosition.blockToSectionCoord(blockposition.getX()), SectionPosition.blockToSectionCoord(blockposition.getZ()));

            return chunk.getBlockState(blockposition);
        }
    }

    @Override
    public Fluid getFluidState(BlockPosition blockposition) {
        if (this.isOutsideBuildHeight(blockposition)) {
            return FluidTypes.EMPTY.defaultFluidState();
        } else {
            Chunk chunk = this.getChunkAt(blockposition);

            return chunk.getFluidState(blockposition);
        }
    }

    public boolean isDay() {
        return !this.dimensionType().hasFixedTime() && this.skyDarken < 4;
    }

    public boolean isNight() {
        return !this.dimensionType().hasFixedTime() && !this.isDay();
    }

    @Override
    public void playSound(@Nullable EntityHuman entityhuman, BlockPosition blockposition, SoundEffect soundeffect, SoundCategory soundcategory, float f, float f1) {
        this.playSound(entityhuman, (double) blockposition.getX() + 0.5D, (double) blockposition.getY() + 0.5D, (double) blockposition.getZ() + 0.5D, soundeffect, soundcategory, f, f1);
    }

    public abstract void playSound(@Nullable EntityHuman entityhuman, double d0, double d1, double d2, SoundEffect soundeffect, SoundCategory soundcategory, float f, float f1);

    public abstract void playSound(@Nullable EntityHuman entityhuman, Entity entity, SoundEffect soundeffect, SoundCategory soundcategory, float f, float f1);

    public void playLocalSound(double d0, double d1, double d2, SoundEffect soundeffect, SoundCategory soundcategory, float f, float f1, boolean flag) {}

    @Override
    public void addParticle(ParticleParam particleparam, double d0, double d1, double d2, double d3, double d4, double d5) {}

    public void addParticle(ParticleParam particleparam, boolean flag, double d0, double d1, double d2, double d3, double d4, double d5) {}

    public void addAlwaysVisibleParticle(ParticleParam particleparam, double d0, double d1, double d2, double d3, double d4, double d5) {}

    public void addAlwaysVisibleParticle(ParticleParam particleparam, boolean flag, double d0, double d1, double d2, double d3, double d4, double d5) {}

    public float getSunAngle(float f) {
        float f1 = this.getTimeOfDay(f);

        return f1 * 6.2831855F;
    }

    public void addBlockEntityTicker(TickingBlockEntity tickingblockentity) {
        (this.tickingBlockEntities ? this.pendingBlockEntityTickers : this.blockEntityTickers).add(tickingblockentity);
    }

    protected void tickBlockEntities() {
        GameProfilerFiller gameprofilerfiller = this.getProfiler();

        gameprofilerfiller.push("blockEntities");
        this.tickingBlockEntities = true;
        if (!this.pendingBlockEntityTickers.isEmpty()) {
            this.blockEntityTickers.addAll(this.pendingBlockEntityTickers);
            this.pendingBlockEntityTickers.clear();
        }

        Iterator iterator = this.blockEntityTickers.iterator();

        while (iterator.hasNext()) {
            TickingBlockEntity tickingblockentity = (TickingBlockEntity) iterator.next();

            if (tickingblockentity.isRemoved()) {
                iterator.remove();
            } else if (this.shouldTickBlocksAt(ChunkCoordIntPair.asLong(tickingblockentity.getPos()))) {
                tickingblockentity.tick();
            }
        }

        this.tickingBlockEntities = false;
        gameprofilerfiller.pop();
    }

    public <T extends Entity> void guardEntityTick(Consumer<T> consumer, T t0) {
        try {
            consumer.accept(t0);
        } catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.forThrowable(throwable, "Ticking entity");
            CrashReportSystemDetails crashreportsystemdetails = crashreport.addCategory("Entity being ticked");

            t0.fillCrashReportCategory(crashreportsystemdetails);
            throw new ReportedException(crashreport);
        }
    }

    public boolean shouldTickDeath(Entity entity) {
        return true;
    }

    public boolean shouldTickBlocksAt(long i) {
        return true;
    }

    public Explosion explode(@Nullable Entity entity, double d0, double d1, double d2, float f, Explosion.Effect explosion_effect) {
        return this.explode(entity, (DamageSource) null, (ExplosionDamageCalculator) null, d0, d1, d2, f, false, explosion_effect);
    }

    public Explosion explode(@Nullable Entity entity, double d0, double d1, double d2, float f, boolean flag, Explosion.Effect explosion_effect) {
        return this.explode(entity, (DamageSource) null, (ExplosionDamageCalculator) null, d0, d1, d2, f, flag, explosion_effect);
    }

    public Explosion explode(@Nullable Entity entity, @Nullable DamageSource damagesource, @Nullable ExplosionDamageCalculator explosiondamagecalculator, double d0, double d1, double d2, float f, boolean flag, Explosion.Effect explosion_effect) {
        Explosion explosion = new Explosion(this, entity, damagesource, explosiondamagecalculator, d0, d1, d2, f, flag, explosion_effect);

        explosion.explode();
        explosion.finalizeExplosion(true);
        return explosion;
    }

    public abstract String gatherChunkSourceStats();

    @Nullable
    @Override
    public TileEntity getBlockEntity(BlockPosition blockposition) {
        return this.isOutsideBuildHeight(blockposition) ? null : (!this.isClientSide && Thread.currentThread() != this.thread ? null : this.getChunkAt(blockposition).getBlockEntity(blockposition, Chunk.EnumTileEntityState.IMMEDIATE));
    }

    public void setBlockEntity(TileEntity tileentity) {
        BlockPosition blockposition = tileentity.getBlockPos();

        if (!this.isOutsideBuildHeight(blockposition)) {
            this.getChunkAt(blockposition).addAndRegisterBlockEntity(tileentity);
        }
    }

    public void removeBlockEntity(BlockPosition blockposition) {
        if (!this.isOutsideBuildHeight(blockposition)) {
            this.getChunkAt(blockposition).removeBlockEntity(blockposition);
        }
    }

    public boolean isLoaded(BlockPosition blockposition) {
        return this.isOutsideBuildHeight(blockposition) ? false : this.getChunkSource().hasChunk(SectionPosition.blockToSectionCoord(blockposition.getX()), SectionPosition.blockToSectionCoord(blockposition.getZ()));
    }

    public boolean loadedAndEntityCanStandOnFace(BlockPosition blockposition, Entity entity, EnumDirection enumdirection) {
        if (this.isOutsideBuildHeight(blockposition)) {
            return false;
        } else {
            IChunkAccess ichunkaccess = this.getChunk(SectionPosition.blockToSectionCoord(blockposition.getX()), SectionPosition.blockToSectionCoord(blockposition.getZ()), ChunkStatus.FULL, false);

            return ichunkaccess == null ? false : ichunkaccess.getBlockState(blockposition).entityCanStandOnFace(this, blockposition, entity, enumdirection);
        }
    }

    public boolean loadedAndEntityCanStandOn(BlockPosition blockposition, Entity entity) {
        return this.loadedAndEntityCanStandOnFace(blockposition, entity, EnumDirection.UP);
    }

    public void updateSkyBrightness() {
        double d0 = 1.0D - (double) (this.getRainLevel(1.0F) * 5.0F) / 16.0D;
        double d1 = 1.0D - (double) (this.getThunderLevel(1.0F) * 5.0F) / 16.0D;
        double d2 = 0.5D + 2.0D * MathHelper.clamp((double) MathHelper.cos(this.getTimeOfDay(1.0F) * 6.2831855F), -0.25D, 0.25D);

        this.skyDarken = (int) ((1.0D - d2 * d0 * d1) * 11.0D);
    }

    public void setSpawnSettings(boolean flag, boolean flag1) {
        this.getChunkSource().setSpawnSettings(flag, flag1);
    }

    protected void prepareWeather() {
        if (this.levelData.isRaining()) {
            this.rainLevel = 1.0F;
            if (this.levelData.isThundering()) {
                this.thunderLevel = 1.0F;
            }
        }

    }

    public void close() throws IOException {
        this.getChunkSource().close();
    }

    @Nullable
    @Override
    public IBlockAccess getChunkForCollisions(int i, int j) {
        return this.getChunk(i, j, ChunkStatus.FULL, false);
    }

    @Override
    public List<Entity> getEntities(@Nullable Entity entity, AxisAlignedBB axisalignedbb, Predicate<? super Entity> predicate) {
        this.getProfiler().incrementCounter("getEntities");
        List<Entity> list = Lists.newArrayList();

        this.getEntities().get(axisalignedbb, (entity1) -> {
            if (entity1 != entity && predicate.test(entity1)) {
                list.add(entity1);
            }

            if (entity1 instanceof EntityEnderDragon) {
                EntityComplexPart[] aentitycomplexpart = ((EntityEnderDragon) entity1).getSubEntities();
                int i = aentitycomplexpart.length;

                for (int j = 0; j < i; ++j) {
                    EntityComplexPart entitycomplexpart = aentitycomplexpart[j];

                    if (entity1 != entity && predicate.test(entitycomplexpart)) {
                        list.add(entitycomplexpart);
                    }
                }
            }

        });
        return list;
    }

    @Override
    public <T extends Entity> List<T> getEntities(EntityTypeTest<Entity, T> entitytypetest, AxisAlignedBB axisalignedbb, Predicate<? super T> predicate) {
        this.getProfiler().incrementCounter("getEntities");
        List<T> list = Lists.newArrayList();

        this.getEntities().get(entitytypetest, axisalignedbb, (entity) -> {
            if (predicate.test(entity)) {
                list.add(entity);
            }

            if (entity instanceof EntityEnderDragon) {
                EntityEnderDragon entityenderdragon = (EntityEnderDragon) entity;
                EntityComplexPart[] aentitycomplexpart = entityenderdragon.getSubEntities();
                int i = aentitycomplexpart.length;

                for (int j = 0; j < i; ++j) {
                    EntityComplexPart entitycomplexpart = aentitycomplexpart[j];
                    T t0 = (Entity) entitytypetest.tryCast(entitycomplexpart);

                    if (t0 != null && predicate.test(t0)) {
                        list.add(t0);
                    }
                }
            }

        });
        return list;
    }

    @Nullable
    public abstract Entity getEntity(int i);

    public void blockEntityChanged(BlockPosition blockposition) {
        if (this.hasChunkAt(blockposition)) {
            this.getChunkAt(blockposition).setUnsaved(true);
        }

    }

    @Override
    public int getSeaLevel() {
        return 63;
    }

    public int getDirectSignalTo(BlockPosition blockposition) {
        byte b0 = 0;
        int i = Math.max(b0, this.getDirectSignal(blockposition.below(), EnumDirection.DOWN));

        if (i >= 15) {
            return i;
        } else {
            i = Math.max(i, this.getDirectSignal(blockposition.above(), EnumDirection.UP));
            if (i >= 15) {
                return i;
            } else {
                i = Math.max(i, this.getDirectSignal(blockposition.north(), EnumDirection.NORTH));
                if (i >= 15) {
                    return i;
                } else {
                    i = Math.max(i, this.getDirectSignal(blockposition.south(), EnumDirection.SOUTH));
                    if (i >= 15) {
                        return i;
                    } else {
                        i = Math.max(i, this.getDirectSignal(blockposition.west(), EnumDirection.WEST));
                        if (i >= 15) {
                            return i;
                        } else {
                            i = Math.max(i, this.getDirectSignal(blockposition.east(), EnumDirection.EAST));
                            return i >= 15 ? i : i;
                        }
                    }
                }
            }
        }
    }

    public boolean hasSignal(BlockPosition blockposition, EnumDirection enumdirection) {
        return this.getSignal(blockposition, enumdirection) > 0;
    }

    public int getSignal(BlockPosition blockposition, EnumDirection enumdirection) {
        IBlockData iblockdata = this.getBlockState(blockposition);
        int i = iblockdata.getSignal(this, blockposition, enumdirection);

        return iblockdata.isRedstoneConductor(this, blockposition) ? Math.max(i, this.getDirectSignalTo(blockposition)) : i;
    }

    public boolean hasNeighborSignal(BlockPosition blockposition) {
        return this.getSignal(blockposition.below(), EnumDirection.DOWN) > 0 ? true : (this.getSignal(blockposition.above(), EnumDirection.UP) > 0 ? true : (this.getSignal(blockposition.north(), EnumDirection.NORTH) > 0 ? true : (this.getSignal(blockposition.south(), EnumDirection.SOUTH) > 0 ? true : (this.getSignal(blockposition.west(), EnumDirection.WEST) > 0 ? true : this.getSignal(blockposition.east(), EnumDirection.EAST) > 0))));
    }

    public int getBestNeighborSignal(BlockPosition blockposition) {
        int i = 0;
        EnumDirection[] aenumdirection = World.DIRECTIONS;
        int j = aenumdirection.length;

        for (int k = 0; k < j; ++k) {
            EnumDirection enumdirection = aenumdirection[k];
            int l = this.getSignal(blockposition.relative(enumdirection), enumdirection);

            if (l >= 15) {
                return 15;
            }

            if (l > i) {
                i = l;
            }
        }

        return i;
    }

    public void disconnect() {}

    public long getGameTime() {
        return this.levelData.getGameTime();
    }

    public long getDayTime() {
        return this.levelData.getDayTime();
    }

    public boolean mayInteract(EntityHuman entityhuman, BlockPosition blockposition) {
        return true;
    }

    public void broadcastEntityEvent(Entity entity, byte b0) {}

    public void blockEvent(BlockPosition blockposition, Block block, int i, int j) {
        this.getBlockState(blockposition).triggerEvent(this, blockposition, i, j);
    }

    @Override
    public WorldData getLevelData() {
        return this.levelData;
    }

    public GameRules getGameRules() {
        return this.levelData.getGameRules();
    }

    public float getThunderLevel(float f) {
        return MathHelper.lerp(f, this.oThunderLevel, this.thunderLevel) * this.getRainLevel(f);
    }

    public void setThunderLevel(float f) {
        float f1 = MathHelper.clamp(f, 0.0F, 1.0F);

        this.oThunderLevel = f1;
        this.thunderLevel = f1;
    }

    public float getRainLevel(float f) {
        return MathHelper.lerp(f, this.oRainLevel, this.rainLevel);
    }

    public void setRainLevel(float f) {
        float f1 = MathHelper.clamp(f, 0.0F, 1.0F);

        this.oRainLevel = f1;
        this.rainLevel = f1;
    }

    public boolean isThundering() {
        return this.dimensionType().hasSkyLight() && !this.dimensionType().hasCeiling() ? (double) this.getThunderLevel(1.0F) > 0.9D : false;
    }

    public boolean isRaining() {
        return (double) this.getRainLevel(1.0F) > 0.2D;
    }

    public boolean isRainingAt(BlockPosition blockposition) {
        if (!this.isRaining()) {
            return false;
        } else if (!this.canSeeSky(blockposition)) {
            return false;
        } else if (this.getHeightmapPos(HeightMap.Type.MOTION_BLOCKING, blockposition).getY() > blockposition.getY()) {
            return false;
        } else {
            BiomeBase biomebase = this.getBiome(blockposition);

            return biomebase.getPrecipitation() == BiomeBase.Precipitation.RAIN && biomebase.warmEnoughToRain(blockposition);
        }
    }

    public boolean isHumidAt(BlockPosition blockposition) {
        BiomeBase biomebase = this.getBiome(blockposition);

        return biomebase.isHumid();
    }

    @Nullable
    public abstract WorldMap getMapData(String s);

    public abstract void setMapData(String s, WorldMap worldmap);

    public abstract int getFreeMapId();

    public void globalLevelEvent(int i, BlockPosition blockposition, int j) {}

    public CrashReportSystemDetails fillReportDetails(CrashReport crashreport) {
        CrashReportSystemDetails crashreportsystemdetails = crashreport.addCategory("Affected level", 1);

        crashreportsystemdetails.setDetail("All players", () -> {
            int i = this.players().size();

            return i + " total; " + this.players();
        });
        IChunkProvider ichunkprovider = this.getChunkSource();

        Objects.requireNonNull(ichunkprovider);
        crashreportsystemdetails.setDetail("Chunk stats", ichunkprovider::gatherStats);
        crashreportsystemdetails.setDetail("Level dimension", () -> {
            return this.dimension().location().toString();
        });

        try {
            this.levelData.fillCrashReportCategory(crashreportsystemdetails, this);
        } catch (Throwable throwable) {
            crashreportsystemdetails.setDetailError("Level Data Unobtainable", throwable);
        }

        return crashreportsystemdetails;
    }

    public abstract void destroyBlockProgress(int i, BlockPosition blockposition, int j);

    public void createFireworks(double d0, double d1, double d2, double d3, double d4, double d5, @Nullable NBTTagCompound nbttagcompound) {}

    public abstract Scoreboard getScoreboard();

    public void updateNeighbourForOutputSignal(BlockPosition blockposition, Block block) {
        Iterator iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

        while (iterator.hasNext()) {
            EnumDirection enumdirection = (EnumDirection) iterator.next();
            BlockPosition blockposition1 = blockposition.relative(enumdirection);

            if (this.hasChunkAt(blockposition1)) {
                IBlockData iblockdata = this.getBlockState(blockposition1);

                if (iblockdata.is(Blocks.COMPARATOR)) {
                    iblockdata.neighborChanged(this, blockposition1, block, blockposition, false);
                } else if (iblockdata.isRedstoneConductor(this, blockposition1)) {
                    blockposition1 = blockposition1.relative(enumdirection);
                    iblockdata = this.getBlockState(blockposition1);
                    if (iblockdata.is(Blocks.COMPARATOR)) {
                        iblockdata.neighborChanged(this, blockposition1, block, blockposition, false);
                    }
                }
            }
        }

    }

    @Override
    public DifficultyDamageScaler getCurrentDifficultyAt(BlockPosition blockposition) {
        long i = 0L;
        float f = 0.0F;

        if (this.hasChunkAt(blockposition)) {
            f = this.getMoonBrightness();
            i = this.getChunkAt(blockposition).getInhabitedTime();
        }

        return new DifficultyDamageScaler(this.getDifficulty(), this.getDayTime(), i, f);
    }

    @Override
    public int getSkyDarken() {
        return this.skyDarken;
    }

    public void setSkyFlashTime(int i) {}

    @Override
    public WorldBorder getWorldBorder() {
        return this.worldBorder;
    }

    public void sendPacketToServer(Packet<?> packet) {
        throw new UnsupportedOperationException("Can't send packets to server unless you're on the client.");
    }

    @Override
    public DimensionManager dimensionType() {
        return this.dimensionType;
    }

    public ResourceKey<World> dimension() {
        return this.dimension;
    }

    @Override
    public Random getRandom() {
        return this.random;
    }

    @Override
    public boolean isStateAtPosition(BlockPosition blockposition, Predicate<IBlockData> predicate) {
        return predicate.test(this.getBlockState(blockposition));
    }

    @Override
    public boolean isFluidAtPosition(BlockPosition blockposition, Predicate<Fluid> predicate) {
        return predicate.test(this.getFluidState(blockposition));
    }

    public abstract CraftingManager getRecipeManager();

    public abstract ITagRegistry getTagManager();

    public BlockPosition getBlockRandomPos(int i, int j, int k, int l) {
        this.randValue = this.randValue * 3 + 1013904223;
        int i1 = this.randValue >> 2;

        return new BlockPosition(i + (i1 & 15), j + (i1 >> 16 & l), k + (i1 >> 8 & 15));
    }

    public boolean noSave() {
        return false;
    }

    public GameProfilerFiller getProfiler() {
        return (GameProfilerFiller) this.profiler.get();
    }

    public Supplier<GameProfilerFiller> getProfilerSupplier() {
        return this.profiler;
    }

    @Override
    public BiomeManager getBiomeManager() {
        return this.biomeManager;
    }

    public final boolean isDebug() {
        return this.isDebug;
    }

    public abstract LevelEntityGetter<Entity> getEntities();

    protected void postGameEventInRadius(@Nullable Entity entity, GameEvent gameevent, BlockPosition blockposition, int i) {
        int j = SectionPosition.blockToSectionCoord(blockposition.getX() - i);
        int k = SectionPosition.blockToSectionCoord(blockposition.getZ() - i);
        int l = SectionPosition.blockToSectionCoord(blockposition.getX() + i);
        int i1 = SectionPosition.blockToSectionCoord(blockposition.getZ() + i);
        int j1 = SectionPosition.blockToSectionCoord(blockposition.getY() - i);
        int k1 = SectionPosition.blockToSectionCoord(blockposition.getY() + i);

        for (int l1 = j; l1 <= l; ++l1) {
            for (int i2 = k; i2 <= i1; ++i2) {
                Chunk chunk = this.getChunkSource().getChunkNow(l1, i2);

                if (chunk != null) {
                    for (int j2 = j1; j2 <= k1; ++j2) {
                        chunk.getEventDispatcher(j2).post(gameevent, entity, blockposition);
                    }
                }
            }
        }

    }

    @Override
    public long nextSubTickCount() {
        return (long) (this.subTickCount++);
    }

    public boolean shouldDelayFallingBlockEntityRemoval(Entity.RemovalReason entity_removalreason) {
        return false;
    }
}
