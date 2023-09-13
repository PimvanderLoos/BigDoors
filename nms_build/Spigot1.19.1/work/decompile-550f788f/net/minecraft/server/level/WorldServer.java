package net.minecraft.server.level;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.LongSets;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.SystemUtils;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.IRegistry;
import net.minecraft.core.IRegistryCustom;
import net.minecraft.core.SectionPosition;
import net.minecraft.core.particles.ParticleParam;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.chat.IChatMutableComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.PacketDebug;
import net.minecraft.network.protocol.game.PacketPlayOutBlockAction;
import net.minecraft.network.protocol.game.PacketPlayOutBlockBreakAnimation;
import net.minecraft.network.protocol.game.PacketPlayOutEntitySound;
import net.minecraft.network.protocol.game.PacketPlayOutEntityStatus;
import net.minecraft.network.protocol.game.PacketPlayOutExplosion;
import net.minecraft.network.protocol.game.PacketPlayOutGameStateChange;
import net.minecraft.network.protocol.game.PacketPlayOutNamedSoundEffect;
import net.minecraft.network.protocol.game.PacketPlayOutSpawnPosition;
import net.minecraft.network.protocol.game.PacketPlayOutWorldEvent;
import net.minecraft.network.protocol.game.PacketPlayOutWorldParticles;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ScoreboardServer;
import net.minecraft.server.level.progress.WorldLoadListener;
import net.minecraft.server.players.SleepStatus;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.tags.TagKey;
import net.minecraft.util.CSVWriter;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Unit;
import net.minecraft.util.profiling.GameProfilerFiller;
import net.minecraft.world.DifficultyDamageScaler;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityLightning;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumCreatureType;
import net.minecraft.world.entity.ReputationHandler;
import net.minecraft.world.entity.ai.navigation.NavigationAbstract;
import net.minecraft.world.entity.ai.village.ReputationEvent;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.entity.ai.village.poi.VillagePlace;
import net.minecraft.world.entity.ai.village.poi.VillagePlaceType;
import net.minecraft.world.entity.animal.EntityAnimal;
import net.minecraft.world.entity.animal.EntityWaterAnimal;
import net.minecraft.world.entity.animal.horse.EntityHorseSkeleton;
import net.minecraft.world.entity.boss.EntityComplexPart;
import net.minecraft.world.entity.boss.enderdragon.EntityEnderDragon;
import net.minecraft.world.entity.npc.NPC;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.entity.raid.PersistentRaid;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.item.crafting.CraftingManager;
import net.minecraft.world.level.BlockActionData;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.ForcedChunk;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.MobSpawner;
import net.minecraft.world.level.SpawnerCreature;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.World;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.TickingBlockEntity;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.chunk.Chunk;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkSection;
import net.minecraft.world.level.chunk.IChunkAccess;
import net.minecraft.world.level.chunk.storage.EntityStorage;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.WorldDimension;
import net.minecraft.world.level.dimension.end.EnderDragonBattle;
import net.minecraft.world.level.entity.EntityPersistentStorage;
import net.minecraft.world.level.entity.EntityTickList;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.entity.LevelCallback;
import net.minecraft.world.level.entity.LevelEntityGetter;
import net.minecraft.world.level.entity.PersistentEntitySectionManager;
import net.minecraft.world.level.gameevent.DynamicGameEventListener;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.level.levelgen.HeightMap;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureBoundingBox;
import net.minecraft.world.level.levelgen.structure.StructureCheck;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidType;
import net.minecraft.world.level.portal.PortalTravelAgent;
import net.minecraft.world.level.saveddata.maps.PersistentIdCounts;
import net.minecraft.world.level.saveddata.maps.WorldMap;
import net.minecraft.world.level.storage.Convertable;
import net.minecraft.world.level.storage.IWorldDataServer;
import net.minecraft.world.level.storage.WorldPersistentData;
import net.minecraft.world.phys.AxisAlignedBB;
import net.minecraft.world.phys.Vec3D;
import net.minecraft.world.phys.shapes.OperatorBoolean;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.VoxelShapes;
import net.minecraft.world.ticks.TickListServer;
import org.slf4j.Logger;

public class WorldServer extends World implements GeneratorAccessSeed {

    public static final BlockPosition END_SPAWN_POINT = new BlockPosition(100, 50, 0);
    private static final int MIN_RAIN_DELAY_TIME = 12000;
    private static final int MAX_RAIN_DELAY_TIME = 180000;
    private static final int MIN_RAIN_TIME = 12000;
    private static final int MAX_RAIN_TIME = 24000;
    private static final int MIN_THUNDER_DELAY_TIME = 12000;
    private static final int MAX_THUNDER_DELAY_TIME = 180000;
    private static final int MIN_THUNDER_TIME = 3600;
    private static final int MAX_THUNDER_TIME = 15600;
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int EMPTY_TIME_NO_TICK = 300;
    private static final int MAX_SCHEDULED_TICKS_PER_TICK = 65536;
    final List<EntityPlayer> players;
    private final ChunkProviderServer chunkSource;
    private final MinecraftServer server;
    public final IWorldDataServer serverLevelData;
    final EntityTickList entityTickList;
    public final PersistentEntitySectionManager<Entity> entityManager;
    public boolean noSave;
    private final SleepStatus sleepStatus;
    private int emptyTime;
    private final PortalTravelAgent portalForcer;
    private final TickListServer<Block> blockTicks;
    private final TickListServer<FluidType> fluidTicks;
    final Set<EntityInsentient> navigatingMobs;
    volatile boolean isUpdatingNavigations;
    protected final PersistentRaid raids;
    private final ObjectLinkedOpenHashSet<BlockActionData> blockEvents;
    private final List<BlockActionData> blockEventsToReschedule;
    private List<GameEvent.b> gameEventMessages;
    private boolean handlingTick;
    private final List<MobSpawner> customSpawners;
    @Nullable
    private final EnderDragonBattle dragonFight;
    final Int2ObjectMap<EntityComplexPart> dragonParts;
    private final StructureManager structureManager;
    private final StructureCheck structureCheck;
    private final boolean tickTime;

    public WorldServer(MinecraftServer minecraftserver, Executor executor, Convertable.ConversionSession convertable_conversionsession, IWorldDataServer iworlddataserver, ResourceKey<World> resourcekey, WorldDimension worlddimension, WorldLoadListener worldloadlistener, boolean flag, long i, List<MobSpawner> list, boolean flag1) {
        Holder holder = worlddimension.typeHolder();

        Objects.requireNonNull(minecraftserver);
        super(iworlddataserver, resourcekey, holder, minecraftserver::getProfiler, false, flag, i, minecraftserver.getMaxChainedNeighborUpdates());
        this.players = Lists.newArrayList();
        this.entityTickList = new EntityTickList();
        this.blockTicks = new TickListServer<>(this::isPositionTickingWithEntitiesLoaded, this.getProfilerSupplier());
        this.fluidTicks = new TickListServer<>(this::isPositionTickingWithEntitiesLoaded, this.getProfilerSupplier());
        this.navigatingMobs = new ObjectOpenHashSet();
        this.blockEvents = new ObjectLinkedOpenHashSet();
        this.blockEventsToReschedule = new ArrayList(64);
        this.gameEventMessages = new ArrayList();
        this.dragonParts = new Int2ObjectOpenHashMap();
        this.tickTime = flag1;
        this.server = minecraftserver;
        this.customSpawners = list;
        this.serverLevelData = iworlddataserver;
        ChunkGenerator chunkgenerator = worlddimension.generator();
        boolean flag2 = minecraftserver.forceSynchronousWrites();
        DataFixer datafixer = minecraftserver.getFixerUpper();
        EntityPersistentStorage<Entity> entitypersistentstorage = new EntityStorage(this, convertable_conversionsession.getDimensionPath(resourcekey).resolve("entities"), datafixer, flag2, minecraftserver);

        this.entityManager = new PersistentEntitySectionManager<>(Entity.class, new WorldServer.a(), entitypersistentstorage);
        StructureTemplateManager structuretemplatemanager = minecraftserver.getStructureManager();
        int j = minecraftserver.getPlayerList().getViewDistance();
        int k = minecraftserver.getPlayerList().getSimulationDistance();
        PersistentEntitySectionManager persistententitysectionmanager = this.entityManager;

        Objects.requireNonNull(this.entityManager);
        this.chunkSource = new ChunkProviderServer(this, convertable_conversionsession, datafixer, structuretemplatemanager, executor, chunkgenerator, j, k, flag2, worldloadlistener, persistententitysectionmanager::updateChunkStatus, () -> {
            return minecraftserver.overworld().getDataStorage();
        });
        chunkgenerator.ensureStructuresGenerated(this.chunkSource.randomState());
        this.portalForcer = new PortalTravelAgent(this);
        this.updateSkyBrightness();
        this.prepareWeather();
        this.getWorldBorder().setAbsoluteMaxSize(minecraftserver.getAbsoluteMaxWorldSize());
        this.raids = (PersistentRaid) this.getDataStorage().computeIfAbsent((nbttagcompound) -> {
            return PersistentRaid.load(this, nbttagcompound);
        }, () -> {
            return new PersistentRaid(this);
        }, PersistentRaid.getFileId(this.dimensionTypeRegistration()));
        if (!minecraftserver.isSingleplayer()) {
            iworlddataserver.setGameType(minecraftserver.getDefaultGameType());
        }

        long l = minecraftserver.getWorldData().worldGenSettings().seed();

        this.structureCheck = new StructureCheck(this.chunkSource.chunkScanner(), this.registryAccess(), minecraftserver.getStructureManager(), resourcekey, chunkgenerator, this.chunkSource.randomState(), this, chunkgenerator.getBiomeSource(), l, datafixer);
        this.structureManager = new StructureManager(this, minecraftserver.getWorldData().worldGenSettings(), this.structureCheck);
        if (this.dimension() == World.END && this.dimensionTypeRegistration().is(BuiltinDimensionTypes.END)) {
            this.dragonFight = new EnderDragonBattle(this, l, minecraftserver.getWorldData().endDragonFightData());
        } else {
            this.dragonFight = null;
        }

        this.sleepStatus = new SleepStatus();
    }

    public void setWeatherParameters(int i, int j, boolean flag, boolean flag1) {
        this.serverLevelData.setClearWeatherTime(i);
        this.serverLevelData.setRainTime(j);
        this.serverLevelData.setThunderTime(j);
        this.serverLevelData.setRaining(flag);
        this.serverLevelData.setThundering(flag1);
    }

    @Override
    public Holder<BiomeBase> getUncachedNoiseBiome(int i, int j, int k) {
        return this.getChunkSource().getGenerator().getBiomeSource().getNoiseBiome(i, j, k, this.getChunkSource().randomState().sampler());
    }

    public StructureManager structureManager() {
        return this.structureManager;
    }

    public void tick(BooleanSupplier booleansupplier) {
        GameProfilerFiller gameprofilerfiller = this.getProfiler();

        this.handlingTick = true;
        gameprofilerfiller.push("world border");
        this.getWorldBorder().tick();
        gameprofilerfiller.popPush("weather");
        this.advanceWeatherCycle();
        int i = this.getGameRules().getInt(GameRules.RULE_PLAYERS_SLEEPING_PERCENTAGE);
        long j;

        if (this.sleepStatus.areEnoughSleeping(i) && this.sleepStatus.areEnoughDeepSleeping(i, this.players)) {
            if (this.getGameRules().getBoolean(GameRules.RULE_DAYLIGHT)) {
                j = this.levelData.getDayTime() + 24000L;
                this.setDayTime(j - j % 24000L);
            }

            this.wakeUpAllPlayers();
            if (this.getGameRules().getBoolean(GameRules.RULE_WEATHER_CYCLE) && this.isRaining()) {
                this.resetWeatherCycle();
            }
        }

        this.updateSkyBrightness();
        this.tickTime();
        gameprofilerfiller.popPush("tickPending");
        if (!this.isDebug()) {
            j = this.getGameTime();
            gameprofilerfiller.push("blockTicks");
            this.blockTicks.tick(j, 65536, this::tickBlock);
            gameprofilerfiller.popPush("fluidTicks");
            this.fluidTicks.tick(j, 65536, this::tickFluid);
            gameprofilerfiller.pop();
        }

        gameprofilerfiller.popPush("raid");
        this.raids.tick();
        gameprofilerfiller.popPush("chunkSource");
        this.getChunkSource().tick(booleansupplier, true);
        gameprofilerfiller.popPush("blockEvents");
        this.runBlockEvents();
        this.handlingTick = false;
        gameprofilerfiller.pop();
        boolean flag = !this.players.isEmpty() || !this.getForcedChunks().isEmpty();

        if (flag) {
            this.resetEmptyTime();
        }

        if (flag || this.emptyTime++ < 300) {
            gameprofilerfiller.push("entities");
            if (this.dragonFight != null) {
                gameprofilerfiller.push("dragonFight");
                this.dragonFight.tick();
                gameprofilerfiller.pop();
            }

            this.entityTickList.forEach((entity) -> {
                if (!entity.isRemoved()) {
                    if (this.shouldDiscardEntity(entity)) {
                        entity.discard();
                    } else {
                        gameprofilerfiller.push("checkDespawn");
                        entity.checkDespawn();
                        gameprofilerfiller.pop();
                        if (this.chunkSource.chunkMap.getDistanceManager().inEntityTickingRange(entity.chunkPosition().toLong())) {
                            Entity entity1 = entity.getVehicle();

                            if (entity1 != null) {
                                if (!entity1.isRemoved() && entity1.hasPassenger(entity)) {
                                    return;
                                }

                                entity.stopRiding();
                            }

                            gameprofilerfiller.push("tick");
                            this.guardEntityTick(this::tickNonPassenger, entity);
                            gameprofilerfiller.pop();
                        }
                    }
                }
            });
            gameprofilerfiller.pop();
            this.tickBlockEntities();
        }

        gameprofilerfiller.push("entityManagement");
        this.entityManager.tick();
        gameprofilerfiller.popPush("gameEvents");
        this.sendGameEvents();
        gameprofilerfiller.pop();
    }

    @Override
    public boolean shouldTickBlocksAt(long i) {
        return this.chunkSource.chunkMap.getDistanceManager().inBlockTickingRange(i);
    }

    protected void tickTime() {
        if (this.tickTime) {
            long i = this.levelData.getGameTime() + 1L;

            this.serverLevelData.setGameTime(i);
            this.serverLevelData.getScheduledEvents().tick(this.server, i);
            if (this.levelData.getGameRules().getBoolean(GameRules.RULE_DAYLIGHT)) {
                this.setDayTime(this.levelData.getDayTime() + 1L);
            }

        }
    }

    public void setDayTime(long i) {
        this.serverLevelData.setDayTime(i);
    }

    public void tickCustomSpawners(boolean flag, boolean flag1) {
        Iterator iterator = this.customSpawners.iterator();

        while (iterator.hasNext()) {
            MobSpawner mobspawner = (MobSpawner) iterator.next();

            mobspawner.tick(this, flag, flag1);
        }

    }

    private boolean shouldDiscardEntity(Entity entity) {
        return !this.server.isSpawningAnimals() && (entity instanceof EntityAnimal || entity instanceof EntityWaterAnimal) ? true : !this.server.areNpcsEnabled() && entity instanceof NPC;
    }

    private void wakeUpAllPlayers() {
        this.sleepStatus.removeAllSleepers();
        ((List) this.players.stream().filter(EntityLiving::isSleeping).collect(Collectors.toList())).forEach((entityplayer) -> {
            entityplayer.stopSleepInBed(false, false);
        });
    }

    public void tickChunk(Chunk chunk, int i) {
        ChunkCoordIntPair chunkcoordintpair = chunk.getPos();
        boolean flag = this.isRaining();
        int j = chunkcoordintpair.getMinBlockX();
        int k = chunkcoordintpair.getMinBlockZ();
        GameProfilerFiller gameprofilerfiller = this.getProfiler();

        gameprofilerfiller.push("thunder");
        BlockPosition blockposition;

        if (flag && this.isThundering() && this.random.nextInt(100000) == 0) {
            blockposition = this.findLightningTargetAround(this.getBlockRandomPos(j, 0, k, 15));
            if (this.isRainingAt(blockposition)) {
                DifficultyDamageScaler difficultydamagescaler = this.getCurrentDifficultyAt(blockposition);
                boolean flag1 = this.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING) && this.random.nextDouble() < (double) difficultydamagescaler.getEffectiveDifficulty() * 0.01D && !this.getBlockState(blockposition.below()).is(Blocks.LIGHTNING_ROD);

                if (flag1) {
                    EntityHorseSkeleton entityhorseskeleton = (EntityHorseSkeleton) EntityTypes.SKELETON_HORSE.create(this);

                    entityhorseskeleton.setTrap(true);
                    entityhorseskeleton.setAge(0);
                    entityhorseskeleton.setPos((double) blockposition.getX(), (double) blockposition.getY(), (double) blockposition.getZ());
                    this.addFreshEntity(entityhorseskeleton);
                }

                EntityLightning entitylightning = (EntityLightning) EntityTypes.LIGHTNING_BOLT.create(this);

                entitylightning.moveTo(Vec3D.atBottomCenterOf(blockposition));
                entitylightning.setVisualOnly(flag1);
                this.addFreshEntity(entitylightning);
            }
        }

        gameprofilerfiller.popPush("iceandsnow");
        if (this.random.nextInt(16) == 0) {
            blockposition = this.getHeightmapPos(HeightMap.Type.MOTION_BLOCKING, this.getBlockRandomPos(j, 0, k, 15));
            BlockPosition blockposition1 = blockposition.below();
            BiomeBase biomebase = (BiomeBase) this.getBiome(blockposition).value();

            if (biomebase.shouldFreeze(this, blockposition1)) {
                this.setBlockAndUpdate(blockposition1, Blocks.ICE.defaultBlockState());
            }

            if (flag) {
                if (biomebase.shouldSnow(this, blockposition)) {
                    this.setBlockAndUpdate(blockposition, Blocks.SNOW.defaultBlockState());
                }

                IBlockData iblockdata = this.getBlockState(blockposition1);
                BiomeBase.Precipitation biomebase_precipitation = biomebase.getPrecipitation();

                if (biomebase_precipitation == BiomeBase.Precipitation.RAIN && biomebase.coldEnoughToSnow(blockposition1)) {
                    biomebase_precipitation = BiomeBase.Precipitation.SNOW;
                }

                iblockdata.getBlock().handlePrecipitation(iblockdata, this, blockposition1, biomebase_precipitation);
            }
        }

        gameprofilerfiller.popPush("tickBlocks");
        if (i > 0) {
            ChunkSection[] achunksection = chunk.getSections();
            int l = achunksection.length;

            for (int i1 = 0; i1 < l; ++i1) {
                ChunkSection chunksection = achunksection[i1];

                if (chunksection.isRandomlyTicking()) {
                    int j1 = chunksection.bottomBlockY();

                    for (int k1 = 0; k1 < i; ++k1) {
                        BlockPosition blockposition2 = this.getBlockRandomPos(j, j1, k, 15);

                        gameprofilerfiller.push("randomTick");
                        IBlockData iblockdata1 = chunksection.getBlockState(blockposition2.getX() - j, blockposition2.getY() - j1, blockposition2.getZ() - k);

                        if (iblockdata1.isRandomlyTicking()) {
                            iblockdata1.randomTick(this, blockposition2, this.random);
                        }

                        Fluid fluid = iblockdata1.getFluidState();

                        if (fluid.isRandomlyTicking()) {
                            fluid.randomTick(this, blockposition2, this.random);
                        }

                        gameprofilerfiller.pop();
                    }
                }
            }
        }

        gameprofilerfiller.pop();
    }

    private Optional<BlockPosition> findLightningRod(BlockPosition blockposition) {
        Optional<BlockPosition> optional = this.getPoiManager().findClosest((holder) -> {
            return holder.is(PoiTypes.LIGHTNING_ROD);
        }, (blockposition1) -> {
            return blockposition1.getY() == this.getHeight(HeightMap.Type.WORLD_SURFACE, blockposition1.getX(), blockposition1.getZ()) - 1;
        }, blockposition, 128, VillagePlace.Occupancy.ANY);

        return optional.map((blockposition1) -> {
            return blockposition1.above(1);
        });
    }

    protected BlockPosition findLightningTargetAround(BlockPosition blockposition) {
        BlockPosition blockposition1 = this.getHeightmapPos(HeightMap.Type.MOTION_BLOCKING, blockposition);
        Optional<BlockPosition> optional = this.findLightningRod(blockposition1);

        if (optional.isPresent()) {
            return (BlockPosition) optional.get();
        } else {
            AxisAlignedBB axisalignedbb = (new AxisAlignedBB(blockposition1, new BlockPosition(blockposition1.getX(), this.getMaxBuildHeight(), blockposition1.getZ()))).inflate(3.0D);
            List<EntityLiving> list = this.getEntitiesOfClass(EntityLiving.class, axisalignedbb, (entityliving) -> {
                return entityliving != null && entityliving.isAlive() && this.canSeeSky(entityliving.blockPosition());
            });

            if (!list.isEmpty()) {
                return ((EntityLiving) list.get(this.random.nextInt(list.size()))).blockPosition();
            } else {
                if (blockposition1.getY() == this.getMinBuildHeight() - 1) {
                    blockposition1 = blockposition1.above(2);
                }

                return blockposition1;
            }
        }
    }

    public boolean isHandlingTick() {
        return this.handlingTick;
    }

    public boolean canSleepThroughNights() {
        return this.getGameRules().getInt(GameRules.RULE_PLAYERS_SLEEPING_PERCENTAGE) <= 100;
    }

    private void announceSleepStatus() {
        if (this.canSleepThroughNights()) {
            if (!this.getServer().isSingleplayer() || this.getServer().isPublished()) {
                int i = this.getGameRules().getInt(GameRules.RULE_PLAYERS_SLEEPING_PERCENTAGE);
                IChatMutableComponent ichatmutablecomponent;

                if (this.sleepStatus.areEnoughSleeping(i)) {
                    ichatmutablecomponent = IChatBaseComponent.translatable("sleep.skipping_night");
                } else {
                    ichatmutablecomponent = IChatBaseComponent.translatable("sleep.players_sleeping", this.sleepStatus.amountSleeping(), this.sleepStatus.sleepersNeeded(i));
                }

                Iterator iterator = this.players.iterator();

                while (iterator.hasNext()) {
                    EntityPlayer entityplayer = (EntityPlayer) iterator.next();

                    entityplayer.displayClientMessage(ichatmutablecomponent, true);
                }

            }
        }
    }

    public void updateSleepingPlayerList() {
        if (!this.players.isEmpty() && this.sleepStatus.update(this.players)) {
            this.announceSleepStatus();
        }

    }

    @Override
    public ScoreboardServer getScoreboard() {
        return this.server.getScoreboard();
    }

    private void advanceWeatherCycle() {
        boolean flag = this.isRaining();

        if (this.dimensionType().hasSkyLight()) {
            if (this.getGameRules().getBoolean(GameRules.RULE_WEATHER_CYCLE)) {
                int i = this.serverLevelData.getClearWeatherTime();
                int j = this.serverLevelData.getThunderTime();
                int k = this.serverLevelData.getRainTime();
                boolean flag1 = this.levelData.isThundering();
                boolean flag2 = this.levelData.isRaining();

                if (i > 0) {
                    --i;
                    j = flag1 ? 0 : 1;
                    k = flag2 ? 0 : 1;
                    flag1 = false;
                    flag2 = false;
                } else {
                    if (j > 0) {
                        --j;
                        if (j == 0) {
                            flag1 = !flag1;
                        }
                    } else if (flag1) {
                        j = MathHelper.randomBetweenInclusive(this.random, 3600, 15600);
                    } else {
                        j = MathHelper.randomBetweenInclusive(this.random, 12000, 180000);
                    }

                    if (k > 0) {
                        --k;
                        if (k == 0) {
                            flag2 = !flag2;
                        }
                    } else if (flag2) {
                        k = MathHelper.randomBetweenInclusive(this.random, 12000, 24000);
                    } else {
                        k = MathHelper.randomBetweenInclusive(this.random, 12000, 180000);
                    }
                }

                this.serverLevelData.setThunderTime(j);
                this.serverLevelData.setRainTime(k);
                this.serverLevelData.setClearWeatherTime(i);
                this.serverLevelData.setThundering(flag1);
                this.serverLevelData.setRaining(flag2);
            }

            this.oThunderLevel = this.thunderLevel;
            if (this.levelData.isThundering()) {
                this.thunderLevel += 0.01F;
            } else {
                this.thunderLevel -= 0.01F;
            }

            this.thunderLevel = MathHelper.clamp(this.thunderLevel, 0.0F, 1.0F);
            this.oRainLevel = this.rainLevel;
            if (this.levelData.isRaining()) {
                this.rainLevel += 0.01F;
            } else {
                this.rainLevel -= 0.01F;
            }

            this.rainLevel = MathHelper.clamp(this.rainLevel, 0.0F, 1.0F);
        }

        if (this.oRainLevel != this.rainLevel) {
            this.server.getPlayerList().broadcastAll(new PacketPlayOutGameStateChange(PacketPlayOutGameStateChange.RAIN_LEVEL_CHANGE, this.rainLevel), this.dimension());
        }

        if (this.oThunderLevel != this.thunderLevel) {
            this.server.getPlayerList().broadcastAll(new PacketPlayOutGameStateChange(PacketPlayOutGameStateChange.THUNDER_LEVEL_CHANGE, this.thunderLevel), this.dimension());
        }

        if (flag != this.isRaining()) {
            if (flag) {
                this.server.getPlayerList().broadcastAll(new PacketPlayOutGameStateChange(PacketPlayOutGameStateChange.STOP_RAINING, 0.0F));
            } else {
                this.server.getPlayerList().broadcastAll(new PacketPlayOutGameStateChange(PacketPlayOutGameStateChange.START_RAINING, 0.0F));
            }

            this.server.getPlayerList().broadcastAll(new PacketPlayOutGameStateChange(PacketPlayOutGameStateChange.RAIN_LEVEL_CHANGE, this.rainLevel));
            this.server.getPlayerList().broadcastAll(new PacketPlayOutGameStateChange(PacketPlayOutGameStateChange.THUNDER_LEVEL_CHANGE, this.thunderLevel));
        }

    }

    private void resetWeatherCycle() {
        this.serverLevelData.setRainTime(0);
        this.serverLevelData.setRaining(false);
        this.serverLevelData.setThunderTime(0);
        this.serverLevelData.setThundering(false);
    }

    public void resetEmptyTime() {
        this.emptyTime = 0;
    }

    private void tickFluid(BlockPosition blockposition, FluidType fluidtype) {
        Fluid fluid = this.getFluidState(blockposition);

        if (fluid.is(fluidtype)) {
            fluid.tick(this, blockposition);
        }

    }

    private void tickBlock(BlockPosition blockposition, Block block) {
        IBlockData iblockdata = this.getBlockState(blockposition);

        if (iblockdata.is(block)) {
            iblockdata.tick(this, blockposition, this.random);
        }

    }

    public void tickNonPassenger(Entity entity) {
        entity.setOldPosAndRot();
        GameProfilerFiller gameprofilerfiller = this.getProfiler();

        ++entity.tickCount;
        this.getProfiler().push(() -> {
            return IRegistry.ENTITY_TYPE.getKey(entity.getType()).toString();
        });
        gameprofilerfiller.incrementCounter("tickNonPassenger");
        entity.tick();
        this.getProfiler().pop();
        Iterator iterator = entity.getPassengers().iterator();

        while (iterator.hasNext()) {
            Entity entity1 = (Entity) iterator.next();

            this.tickPassenger(entity, entity1);
        }

    }

    private void tickPassenger(Entity entity, Entity entity1) {
        if (!entity1.isRemoved() && entity1.getVehicle() == entity) {
            if (entity1 instanceof EntityHuman || this.entityTickList.contains(entity1)) {
                entity1.setOldPosAndRot();
                ++entity1.tickCount;
                GameProfilerFiller gameprofilerfiller = this.getProfiler();

                gameprofilerfiller.push(() -> {
                    return IRegistry.ENTITY_TYPE.getKey(entity1.getType()).toString();
                });
                gameprofilerfiller.incrementCounter("tickPassenger");
                entity1.rideTick();
                gameprofilerfiller.pop();
                Iterator iterator = entity1.getPassengers().iterator();

                while (iterator.hasNext()) {
                    Entity entity2 = (Entity) iterator.next();

                    this.tickPassenger(entity1, entity2);
                }

            }
        } else {
            entity1.stopRiding();
        }
    }

    @Override
    public boolean mayInteract(EntityHuman entityhuman, BlockPosition blockposition) {
        return !this.server.isUnderSpawnProtection(this, blockposition, entityhuman) && this.getWorldBorder().isWithinBounds(blockposition);
    }

    public void save(@Nullable IProgressUpdate iprogressupdate, boolean flag, boolean flag1) {
        ChunkProviderServer chunkproviderserver = this.getChunkSource();

        if (!flag1) {
            if (iprogressupdate != null) {
                iprogressupdate.progressStartNoAbort(IChatBaseComponent.translatable("menu.savingLevel"));
            }

            this.saveLevelData();
            if (iprogressupdate != null) {
                iprogressupdate.progressStage(IChatBaseComponent.translatable("menu.savingChunks"));
            }

            chunkproviderserver.save(flag);
            if (flag) {
                this.entityManager.saveAll();
            } else {
                this.entityManager.autoSave();
            }

        }
    }

    private void saveLevelData() {
        if (this.dragonFight != null) {
            this.server.getWorldData().setEndDragonFightData(this.dragonFight.saveData());
        }

        this.getChunkSource().getDataStorage().save();
    }

    public <T extends Entity> List<? extends T> getEntities(EntityTypeTest<Entity, T> entitytypetest, Predicate<? super T> predicate) {
        List<T> list = Lists.newArrayList();

        this.getEntities().get(entitytypetest, (entity) -> {
            if (predicate.test(entity)) {
                list.add(entity);
            }

        });
        return list;
    }

    public List<? extends EntityEnderDragon> getDragons() {
        return this.getEntities((EntityTypeTest) EntityTypes.ENDER_DRAGON, EntityLiving::isAlive);
    }

    public List<EntityPlayer> getPlayers(Predicate<? super EntityPlayer> predicate) {
        List<EntityPlayer> list = Lists.newArrayList();
        Iterator iterator = this.players.iterator();

        while (iterator.hasNext()) {
            EntityPlayer entityplayer = (EntityPlayer) iterator.next();

            if (predicate.test(entityplayer)) {
                list.add(entityplayer);
            }
        }

        return list;
    }

    @Nullable
    public EntityPlayer getRandomPlayer() {
        List<EntityPlayer> list = this.getPlayers(EntityLiving::isAlive);

        return list.isEmpty() ? null : (EntityPlayer) list.get(this.random.nextInt(list.size()));
    }

    @Override
    public boolean addFreshEntity(Entity entity) {
        return this.addEntity(entity);
    }

    public boolean addWithUUID(Entity entity) {
        return this.addEntity(entity);
    }

    public void addDuringTeleport(Entity entity) {
        this.addEntity(entity);
    }

    public void addDuringCommandTeleport(EntityPlayer entityplayer) {
        this.addPlayer(entityplayer);
    }

    public void addDuringPortalTeleport(EntityPlayer entityplayer) {
        this.addPlayer(entityplayer);
    }

    public void addNewPlayer(EntityPlayer entityplayer) {
        this.addPlayer(entityplayer);
    }

    public void addRespawnedPlayer(EntityPlayer entityplayer) {
        this.addPlayer(entityplayer);
    }

    private void addPlayer(EntityPlayer entityplayer) {
        Entity entity = (Entity) this.getEntities().get(entityplayer.getUUID());

        if (entity != null) {
            WorldServer.LOGGER.warn("Force-added player with duplicate UUID {}", entityplayer.getUUID().toString());
            entity.unRide();
            this.removePlayerImmediately((EntityPlayer) entity, Entity.RemovalReason.DISCARDED);
        }

        this.entityManager.addNewEntity(entityplayer);
    }

    private boolean addEntity(Entity entity) {
        if (entity.isRemoved()) {
            WorldServer.LOGGER.warn("Tried to add entity {} but it was marked as removed already", EntityTypes.getKey(entity.getType()));
            return false;
        } else {
            return this.entityManager.addNewEntity(entity);
        }
    }

    public boolean tryAddFreshEntityWithPassengers(Entity entity) {
        Stream stream = entity.getSelfAndPassengers().map(Entity::getUUID);
        PersistentEntitySectionManager persistententitysectionmanager = this.entityManager;

        Objects.requireNonNull(this.entityManager);
        if (stream.anyMatch(persistententitysectionmanager::isLoaded)) {
            return false;
        } else {
            this.addFreshEntityWithPassengers(entity);
            return true;
        }
    }

    public void unload(Chunk chunk) {
        chunk.clearAllBlockEntities();
        chunk.unregisterTickContainerFromLevel(this);
    }

    public void removePlayerImmediately(EntityPlayer entityplayer, Entity.RemovalReason entity_removalreason) {
        entityplayer.remove(entity_removalreason);
    }

    @Override
    public void destroyBlockProgress(int i, BlockPosition blockposition, int j) {
        Iterator iterator = this.server.getPlayerList().getPlayers().iterator();

        while (iterator.hasNext()) {
            EntityPlayer entityplayer = (EntityPlayer) iterator.next();

            if (entityplayer != null && entityplayer.level == this && entityplayer.getId() != i) {
                double d0 = (double) blockposition.getX() - entityplayer.getX();
                double d1 = (double) blockposition.getY() - entityplayer.getY();
                double d2 = (double) blockposition.getZ() - entityplayer.getZ();

                if (d0 * d0 + d1 * d1 + d2 * d2 < 1024.0D) {
                    entityplayer.connection.send(new PacketPlayOutBlockBreakAnimation(i, blockposition, j));
                }
            }
        }

    }

    @Override
    public void playSeededSound(@Nullable EntityHuman entityhuman, double d0, double d1, double d2, SoundEffect soundeffect, SoundCategory soundcategory, float f, float f1, long i) {
        this.server.getPlayerList().broadcast(entityhuman, d0, d1, d2, (double) soundeffect.getRange(f), this.dimension(), new PacketPlayOutNamedSoundEffect(soundeffect, soundcategory, d0, d1, d2, f, f1, i));
    }

    @Override
    public void playSeededSound(@Nullable EntityHuman entityhuman, Entity entity, SoundEffect soundeffect, SoundCategory soundcategory, float f, float f1, long i) {
        this.server.getPlayerList().broadcast(entityhuman, entity.getX(), entity.getY(), entity.getZ(), (double) soundeffect.getRange(f), this.dimension(), new PacketPlayOutEntitySound(soundeffect, soundcategory, entity, f, f1, i));
    }

    @Override
    public void globalLevelEvent(int i, BlockPosition blockposition, int j) {
        this.server.getPlayerList().broadcastAll(new PacketPlayOutWorldEvent(i, blockposition, j, true));
    }

    @Override
    public void levelEvent(@Nullable EntityHuman entityhuman, int i, BlockPosition blockposition, int j) {
        this.server.getPlayerList().broadcast(entityhuman, (double) blockposition.getX(), (double) blockposition.getY(), (double) blockposition.getZ(), 64.0D, this.dimension(), new PacketPlayOutWorldEvent(i, blockposition, j, false));
    }

    public int getLogicalHeight() {
        return this.dimensionType().logicalHeight();
    }

    @Override
    public void gameEvent(GameEvent gameevent, Vec3D vec3d, GameEvent.a gameevent_a) {
        int i = gameevent.getNotificationRadius();
        BlockPosition blockposition = new BlockPosition(vec3d);
        int j = SectionPosition.blockToSectionCoord(blockposition.getX() - i);
        int k = SectionPosition.blockToSectionCoord(blockposition.getY() - i);
        int l = SectionPosition.blockToSectionCoord(blockposition.getZ() - i);
        int i1 = SectionPosition.blockToSectionCoord(blockposition.getX() + i);
        int j1 = SectionPosition.blockToSectionCoord(blockposition.getY() + i);
        int k1 = SectionPosition.blockToSectionCoord(blockposition.getZ() + i);
        List<GameEvent.b> list = new ArrayList();
        boolean flag = false;

        for (int l1 = j; l1 <= i1; ++l1) {
            for (int i2 = l; i2 <= k1; ++i2) {
                Chunk chunk = this.getChunkSource().getChunkNow(l1, i2);

                if (chunk != null) {
                    for (int j2 = k; j2 <= j1; ++j2) {
                        flag |= chunk.getEventDispatcher(j2).walkListeners(gameevent, vec3d, gameevent_a, (gameeventlistener, vec3d1) -> {
                            (gameeventlistener.handleEventsImmediately() ? list : this.gameEventMessages).add(new GameEvent.b(gameevent, vec3d, gameevent_a, gameeventlistener, vec3d1));
                        });
                    }
                }
            }
        }

        if (!list.isEmpty()) {
            this.handleGameEventMessagesInQueue(list);
        }

        if (flag) {
            PacketDebug.sendGameEventInfo(this, gameevent, vec3d);
        }

    }

    private void sendGameEvents() {
        if (!this.gameEventMessages.isEmpty()) {
            List<GameEvent.b> list = this.gameEventMessages;

            this.gameEventMessages = new ArrayList();
            this.handleGameEventMessagesInQueue(list);
        }
    }

    private void handleGameEventMessagesInQueue(List<GameEvent.b> list) {
        Collections.sort(list);
        Iterator iterator = list.iterator();

        while (iterator.hasNext()) {
            GameEvent.b gameevent_b = (GameEvent.b) iterator.next();
            GameEventListener gameeventlistener = gameevent_b.recipient();

            gameeventlistener.handleGameEvent(this, gameevent_b);
        }

    }

    @Override
    public void sendBlockUpdated(BlockPosition blockposition, IBlockData iblockdata, IBlockData iblockdata1, int i) {
        if (this.isUpdatingNavigations) {
            String s = "recursive call to sendBlockUpdated";

            SystemUtils.logAndPauseIfInIde("recursive call to sendBlockUpdated", new IllegalStateException("recursive call to sendBlockUpdated"));
        }

        this.getChunkSource().blockChanged(blockposition);
        VoxelShape voxelshape = iblockdata.getCollisionShape(this, blockposition);
        VoxelShape voxelshape1 = iblockdata1.getCollisionShape(this, blockposition);

        if (VoxelShapes.joinIsNotEmpty(voxelshape, voxelshape1, OperatorBoolean.NOT_SAME)) {
            List<NavigationAbstract> list = new ObjectArrayList();
            Iterator iterator = this.navigatingMobs.iterator();

            while (iterator.hasNext()) {
                EntityInsentient entityinsentient = (EntityInsentient) iterator.next();
                NavigationAbstract navigationabstract = entityinsentient.getNavigation();

                if (navigationabstract.shouldRecomputePath(blockposition)) {
                    list.add(navigationabstract);
                }
            }

            try {
                this.isUpdatingNavigations = true;
                iterator = list.iterator();

                while (iterator.hasNext()) {
                    NavigationAbstract navigationabstract1 = (NavigationAbstract) iterator.next();

                    navigationabstract1.recomputePath();
                }
            } finally {
                this.isUpdatingNavigations = false;
            }

        }
    }

    @Override
    public void updateNeighborsAt(BlockPosition blockposition, Block block) {
        this.neighborUpdater.updateNeighborsAtExceptFromFacing(blockposition, block, (EnumDirection) null);
    }

    @Override
    public void updateNeighborsAtExceptFromFacing(BlockPosition blockposition, Block block, EnumDirection enumdirection) {
        this.neighborUpdater.updateNeighborsAtExceptFromFacing(blockposition, block, enumdirection);
    }

    @Override
    public void neighborChanged(BlockPosition blockposition, Block block, BlockPosition blockposition1) {
        this.neighborUpdater.neighborChanged(blockposition, block, blockposition1);
    }

    @Override
    public void neighborChanged(IBlockData iblockdata, BlockPosition blockposition, Block block, BlockPosition blockposition1, boolean flag) {
        this.neighborUpdater.neighborChanged(iblockdata, blockposition, block, blockposition1, flag);
    }

    @Override
    public void broadcastEntityEvent(Entity entity, byte b0) {
        this.getChunkSource().broadcastAndSend(entity, new PacketPlayOutEntityStatus(entity, b0));
    }

    @Override
    public ChunkProviderServer getChunkSource() {
        return this.chunkSource;
    }

    @Override
    public Explosion explode(@Nullable Entity entity, @Nullable DamageSource damagesource, @Nullable ExplosionDamageCalculator explosiondamagecalculator, double d0, double d1, double d2, float f, boolean flag, Explosion.Effect explosion_effect) {
        Explosion explosion = new Explosion(this, entity, damagesource, explosiondamagecalculator, d0, d1, d2, f, flag, explosion_effect);

        explosion.explode();
        explosion.finalizeExplosion(false);
        if (explosion_effect == Explosion.Effect.NONE) {
            explosion.clearToBlow();
        }

        Iterator iterator = this.players.iterator();

        while (iterator.hasNext()) {
            EntityPlayer entityplayer = (EntityPlayer) iterator.next();

            if (entityplayer.distanceToSqr(d0, d1, d2) < 4096.0D) {
                entityplayer.connection.send(new PacketPlayOutExplosion(d0, d1, d2, f, explosion.getToBlow(), (Vec3D) explosion.getHitPlayers().get(entityplayer)));
            }
        }

        return explosion;
    }

    @Override
    public void blockEvent(BlockPosition blockposition, Block block, int i, int j) {
        this.blockEvents.add(new BlockActionData(blockposition, block, i, j));
    }

    private void runBlockEvents() {
        this.blockEventsToReschedule.clear();

        while (!this.blockEvents.isEmpty()) {
            BlockActionData blockactiondata = (BlockActionData) this.blockEvents.removeFirst();

            if (this.shouldTickBlocksAt(blockactiondata.pos())) {
                if (this.doBlockEvent(blockactiondata)) {
                    this.server.getPlayerList().broadcast((EntityHuman) null, (double) blockactiondata.pos().getX(), (double) blockactiondata.pos().getY(), (double) blockactiondata.pos().getZ(), 64.0D, this.dimension(), new PacketPlayOutBlockAction(blockactiondata.pos(), blockactiondata.block(), blockactiondata.paramA(), blockactiondata.paramB()));
                }
            } else {
                this.blockEventsToReschedule.add(blockactiondata);
            }
        }

        this.blockEvents.addAll(this.blockEventsToReschedule);
    }

    private boolean doBlockEvent(BlockActionData blockactiondata) {
        IBlockData iblockdata = this.getBlockState(blockactiondata.pos());

        return iblockdata.is(blockactiondata.block()) ? iblockdata.triggerEvent(this, blockactiondata.pos(), blockactiondata.paramA(), blockactiondata.paramB()) : false;
    }

    @Override
    public TickListServer<Block> getBlockTicks() {
        return this.blockTicks;
    }

    @Override
    public TickListServer<FluidType> getFluidTicks() {
        return this.fluidTicks;
    }

    @Nonnull
    @Override
    public MinecraftServer getServer() {
        return this.server;
    }

    public PortalTravelAgent getPortalForcer() {
        return this.portalForcer;
    }

    public StructureTemplateManager getStructureManager() {
        return this.server.getStructureManager();
    }

    public <T extends ParticleParam> int sendParticles(T t0, double d0, double d1, double d2, int i, double d3, double d4, double d5, double d6) {
        PacketPlayOutWorldParticles packetplayoutworldparticles = new PacketPlayOutWorldParticles(t0, false, d0, d1, d2, (float) d3, (float) d4, (float) d5, (float) d6, i);
        int j = 0;

        for (int k = 0; k < this.players.size(); ++k) {
            EntityPlayer entityplayer = (EntityPlayer) this.players.get(k);

            if (this.sendParticles(entityplayer, false, d0, d1, d2, packetplayoutworldparticles)) {
                ++j;
            }
        }

        return j;
    }

    public <T extends ParticleParam> boolean sendParticles(EntityPlayer entityplayer, T t0, boolean flag, double d0, double d1, double d2, int i, double d3, double d4, double d5, double d6) {
        Packet<?> packet = new PacketPlayOutWorldParticles(t0, flag, d0, d1, d2, (float) d3, (float) d4, (float) d5, (float) d6, i);

        return this.sendParticles(entityplayer, flag, d0, d1, d2, packet);
    }

    private boolean sendParticles(EntityPlayer entityplayer, boolean flag, double d0, double d1, double d2, Packet<?> packet) {
        if (entityplayer.getLevel() != this) {
            return false;
        } else {
            BlockPosition blockposition = entityplayer.blockPosition();

            if (blockposition.closerToCenterThan(new Vec3D(d0, d1, d2), flag ? 512.0D : 32.0D)) {
                entityplayer.connection.send(packet);
                return true;
            } else {
                return false;
            }
        }
    }

    @Nullable
    @Override
    public Entity getEntity(int i) {
        return (Entity) this.getEntities().get(i);
    }

    /** @deprecated */
    @Deprecated
    @Nullable
    public Entity getEntityOrPart(int i) {
        Entity entity = (Entity) this.getEntities().get(i);

        return entity != null ? entity : (Entity) this.dragonParts.get(i);
    }

    @Nullable
    public Entity getEntity(UUID uuid) {
        return (Entity) this.getEntities().get(uuid);
    }

    @Nullable
    public BlockPosition findNearestMapStructure(TagKey<Structure> tagkey, BlockPosition blockposition, int i, boolean flag) {
        if (!this.server.getWorldData().worldGenSettings().generateStructures()) {
            return null;
        } else {
            Optional<HolderSet.Named<Structure>> optional = this.registryAccess().registryOrThrow(IRegistry.STRUCTURE_REGISTRY).getTag(tagkey);

            if (optional.isEmpty()) {
                return null;
            } else {
                Pair<BlockPosition, Holder<Structure>> pair = this.getChunkSource().getGenerator().findNearestMapStructure(this, (HolderSet) optional.get(), blockposition, i, flag);

                return pair != null ? (BlockPosition) pair.getFirst() : null;
            }
        }
    }

    @Nullable
    public Pair<BlockPosition, Holder<BiomeBase>> findClosestBiome3d(Predicate<Holder<BiomeBase>> predicate, BlockPosition blockposition, int i, int j, int k) {
        return this.getChunkSource().getGenerator().getBiomeSource().findClosestBiome3d(blockposition, i, j, k, predicate, this.getChunkSource().randomState().sampler(), this);
    }

    @Override
    public CraftingManager getRecipeManager() {
        return this.server.getRecipeManager();
    }

    @Override
    public boolean noSave() {
        return this.noSave;
    }

    @Override
    public IRegistryCustom registryAccess() {
        return this.server.registryAccess();
    }

    public WorldPersistentData getDataStorage() {
        return this.getChunkSource().getDataStorage();
    }

    @Nullable
    @Override
    public WorldMap getMapData(String s) {
        return (WorldMap) this.getServer().overworld().getDataStorage().get(WorldMap::load, s);
    }

    @Override
    public void setMapData(String s, WorldMap worldmap) {
        this.getServer().overworld().getDataStorage().set(s, worldmap);
    }

    @Override
    public int getFreeMapId() {
        return ((PersistentIdCounts) this.getServer().overworld().getDataStorage().computeIfAbsent(PersistentIdCounts::load, PersistentIdCounts::new, "idcounts")).getFreeAuxValueForMap();
    }

    public void setDefaultSpawnPos(BlockPosition blockposition, float f) {
        ChunkCoordIntPair chunkcoordintpair = new ChunkCoordIntPair(new BlockPosition(this.levelData.getXSpawn(), 0, this.levelData.getZSpawn()));

        this.levelData.setSpawn(blockposition, f);
        this.getChunkSource().removeRegionTicket(TicketType.START, chunkcoordintpair, 11, Unit.INSTANCE);
        this.getChunkSource().addRegionTicket(TicketType.START, new ChunkCoordIntPair(blockposition), 11, Unit.INSTANCE);
        this.getServer().getPlayerList().broadcastAll(new PacketPlayOutSpawnPosition(blockposition, f));
    }

    public LongSet getForcedChunks() {
        ForcedChunk forcedchunk = (ForcedChunk) this.getDataStorage().get(ForcedChunk::load, "chunks");

        return (LongSet) (forcedchunk != null ? LongSets.unmodifiable(forcedchunk.getChunks()) : LongSets.EMPTY_SET);
    }

    public boolean setChunkForced(int i, int j, boolean flag) {
        ForcedChunk forcedchunk = (ForcedChunk) this.getDataStorage().computeIfAbsent(ForcedChunk::load, ForcedChunk::new, "chunks");
        ChunkCoordIntPair chunkcoordintpair = new ChunkCoordIntPair(i, j);
        long k = chunkcoordintpair.toLong();
        boolean flag1;

        if (flag) {
            flag1 = forcedchunk.getChunks().add(k);
            if (flag1) {
                this.getChunk(i, j);
            }
        } else {
            flag1 = forcedchunk.getChunks().remove(k);
        }

        forcedchunk.setDirty(flag1);
        if (flag1) {
            this.getChunkSource().updateChunkForced(chunkcoordintpair, flag);
        }

        return flag1;
    }

    @Override
    public List<EntityPlayer> players() {
        return this.players;
    }

    @Override
    public void onBlockStateChange(BlockPosition blockposition, IBlockData iblockdata, IBlockData iblockdata1) {
        Optional<Holder<VillagePlaceType>> optional = PoiTypes.forState(iblockdata);
        Optional<Holder<VillagePlaceType>> optional1 = PoiTypes.forState(iblockdata1);

        if (!Objects.equals(optional, optional1)) {
            BlockPosition blockposition1 = blockposition.immutable();

            optional.ifPresent((holder) -> {
                this.getServer().execute(() -> {
                    this.getPoiManager().remove(blockposition1);
                    PacketDebug.sendPoiRemovedPacket(this, blockposition1);
                });
            });
            optional1.ifPresent((holder) -> {
                this.getServer().execute(() -> {
                    this.getPoiManager().add(blockposition1, holder);
                    PacketDebug.sendPoiAddedPacket(this, blockposition1);
                });
            });
        }
    }

    public VillagePlace getPoiManager() {
        return this.getChunkSource().getPoiManager();
    }

    public boolean isVillage(BlockPosition blockposition) {
        return this.isCloseToVillage(blockposition, 1);
    }

    public boolean isVillage(SectionPosition sectionposition) {
        return this.isVillage(sectionposition.center());
    }

    public boolean isCloseToVillage(BlockPosition blockposition, int i) {
        return i > 6 ? false : this.sectionsToVillage(SectionPosition.of(blockposition)) <= i;
    }

    public int sectionsToVillage(SectionPosition sectionposition) {
        return this.getPoiManager().sectionsToVillage(sectionposition);
    }

    public PersistentRaid getRaids() {
        return this.raids;
    }

    @Nullable
    public Raid getRaidAt(BlockPosition blockposition) {
        return this.raids.getNearbyRaid(blockposition, 9216);
    }

    public boolean isRaided(BlockPosition blockposition) {
        return this.getRaidAt(blockposition) != null;
    }

    public void onReputationEvent(ReputationEvent reputationevent, Entity entity, ReputationHandler reputationhandler) {
        reputationhandler.onReputationEventFrom(reputationevent, entity);
    }

    public void saveDebugReport(Path path) throws IOException {
        PlayerChunkMap playerchunkmap = this.getChunkSource().chunkMap;
        BufferedWriter bufferedwriter = Files.newBufferedWriter(path.resolve("stats.txt"));

        try {
            bufferedwriter.write(String.format(Locale.ROOT, "spawning_chunks: %d\n", playerchunkmap.getDistanceManager().getNaturalSpawnChunkCount()));
            SpawnerCreature.d spawnercreature_d = this.getChunkSource().getLastSpawnState();

            if (spawnercreature_d != null) {
                ObjectIterator objectiterator = spawnercreature_d.getMobCategoryCounts().object2IntEntrySet().iterator();

                while (objectiterator.hasNext()) {
                    Entry<EnumCreatureType> entry = (Entry) objectiterator.next();

                    bufferedwriter.write(String.format(Locale.ROOT, "spawn_count.%s: %d\n", ((EnumCreatureType) entry.getKey()).getName(), entry.getIntValue()));
                }
            }

            bufferedwriter.write(String.format(Locale.ROOT, "entities: %s\n", this.entityManager.gatherStats()));
            bufferedwriter.write(String.format(Locale.ROOT, "block_entity_tickers: %d\n", this.blockEntityTickers.size()));
            bufferedwriter.write(String.format(Locale.ROOT, "block_ticks: %d\n", this.getBlockTicks().count()));
            bufferedwriter.write(String.format(Locale.ROOT, "fluid_ticks: %d\n", this.getFluidTicks().count()));
            bufferedwriter.write("distance_manager: " + playerchunkmap.getDistanceManager().getDebugStatus() + "\n");
            bufferedwriter.write(String.format(Locale.ROOT, "pending_tasks: %d\n", this.getChunkSource().getPendingTasksCount()));
        } catch (Throwable throwable) {
            if (bufferedwriter != null) {
                try {
                    bufferedwriter.close();
                } catch (Throwable throwable1) {
                    throwable.addSuppressed(throwable1);
                }
            }

            throw throwable;
        }

        if (bufferedwriter != null) {
            bufferedwriter.close();
        }

        CrashReport crashreport = new CrashReport("Level dump", new Exception("dummy"));

        this.fillReportDetails(crashreport);
        BufferedWriter bufferedwriter1 = Files.newBufferedWriter(path.resolve("example_crash.txt"));

        try {
            bufferedwriter1.write(crashreport.getFriendlyReport());
        } catch (Throwable throwable2) {
            if (bufferedwriter1 != null) {
                try {
                    bufferedwriter1.close();
                } catch (Throwable throwable3) {
                    throwable2.addSuppressed(throwable3);
                }
            }

            throw throwable2;
        }

        if (bufferedwriter1 != null) {
            bufferedwriter1.close();
        }

        Path path1 = path.resolve("chunks.csv");
        BufferedWriter bufferedwriter2 = Files.newBufferedWriter(path1);

        try {
            playerchunkmap.dumpChunks(bufferedwriter2);
        } catch (Throwable throwable4) {
            if (bufferedwriter2 != null) {
                try {
                    bufferedwriter2.close();
                } catch (Throwable throwable5) {
                    throwable4.addSuppressed(throwable5);
                }
            }

            throw throwable4;
        }

        if (bufferedwriter2 != null) {
            bufferedwriter2.close();
        }

        Path path2 = path.resolve("entity_chunks.csv");
        BufferedWriter bufferedwriter3 = Files.newBufferedWriter(path2);

        try {
            this.entityManager.dumpSections(bufferedwriter3);
        } catch (Throwable throwable6) {
            if (bufferedwriter3 != null) {
                try {
                    bufferedwriter3.close();
                } catch (Throwable throwable7) {
                    throwable6.addSuppressed(throwable7);
                }
            }

            throw throwable6;
        }

        if (bufferedwriter3 != null) {
            bufferedwriter3.close();
        }

        Path path3 = path.resolve("entities.csv");
        BufferedWriter bufferedwriter4 = Files.newBufferedWriter(path3);

        try {
            dumpEntities(bufferedwriter4, this.getEntities().getAll());
        } catch (Throwable throwable8) {
            if (bufferedwriter4 != null) {
                try {
                    bufferedwriter4.close();
                } catch (Throwable throwable9) {
                    throwable8.addSuppressed(throwable9);
                }
            }

            throw throwable8;
        }

        if (bufferedwriter4 != null) {
            bufferedwriter4.close();
        }

        Path path4 = path.resolve("block_entities.csv");
        BufferedWriter bufferedwriter5 = Files.newBufferedWriter(path4);

        try {
            this.dumpBlockEntityTickers(bufferedwriter5);
        } catch (Throwable throwable10) {
            if (bufferedwriter5 != null) {
                try {
                    bufferedwriter5.close();
                } catch (Throwable throwable11) {
                    throwable10.addSuppressed(throwable11);
                }
            }

            throw throwable10;
        }

        if (bufferedwriter5 != null) {
            bufferedwriter5.close();
        }

    }

    private static void dumpEntities(Writer writer, Iterable<Entity> iterable) throws IOException {
        CSVWriter csvwriter = CSVWriter.builder().addColumn("x").addColumn("y").addColumn("z").addColumn("uuid").addColumn("type").addColumn("alive").addColumn("display_name").addColumn("custom_name").build(writer);
        Iterator iterator = iterable.iterator();

        while (iterator.hasNext()) {
            Entity entity = (Entity) iterator.next();
            IChatBaseComponent ichatbasecomponent = entity.getCustomName();
            IChatBaseComponent ichatbasecomponent1 = entity.getDisplayName();

            csvwriter.writeRow(entity.getX(), entity.getY(), entity.getZ(), entity.getUUID(), IRegistry.ENTITY_TYPE.getKey(entity.getType()), entity.isAlive(), ichatbasecomponent1.getString(), ichatbasecomponent != null ? ichatbasecomponent.getString() : null);
        }

    }

    private void dumpBlockEntityTickers(Writer writer) throws IOException {
        CSVWriter csvwriter = CSVWriter.builder().addColumn("x").addColumn("y").addColumn("z").addColumn("type").build(writer);
        Iterator iterator = this.blockEntityTickers.iterator();

        while (iterator.hasNext()) {
            TickingBlockEntity tickingblockentity = (TickingBlockEntity) iterator.next();
            BlockPosition blockposition = tickingblockentity.getPos();

            csvwriter.writeRow(blockposition.getX(), blockposition.getY(), blockposition.getZ(), tickingblockentity.getType());
        }

    }

    @VisibleForTesting
    public void clearBlockEvents(StructureBoundingBox structureboundingbox) {
        this.blockEvents.removeIf((blockactiondata) -> {
            return structureboundingbox.isInside(blockactiondata.pos());
        });
    }

    @Override
    public void blockUpdated(BlockPosition blockposition, Block block) {
        if (!this.isDebug()) {
            this.updateNeighborsAt(blockposition, block);
        }

    }

    @Override
    public float getShade(EnumDirection enumdirection, boolean flag) {
        return 1.0F;
    }

    public Iterable<Entity> getAllEntities() {
        return this.getEntities().getAll();
    }

    public String toString() {
        return "ServerLevel[" + this.serverLevelData.getLevelName() + "]";
    }

    public boolean isFlat() {
        return this.server.getWorldData().worldGenSettings().isFlatWorld();
    }

    @Override
    public long getSeed() {
        return this.server.getWorldData().worldGenSettings().seed();
    }

    @Nullable
    public EnderDragonBattle dragonFight() {
        return this.dragonFight;
    }

    @Override
    public WorldServer getLevel() {
        return this;
    }

    @VisibleForTesting
    public String getWatchdogStats() {
        return String.format(Locale.ROOT, "players: %s, entities: %s [%s], block_entities: %d [%s], block_ticks: %d, fluid_ticks: %d, chunk_source: %s", this.players.size(), this.entityManager.gatherStats(), getTypeCount(this.entityManager.getEntityGetter().getAll(), (entity) -> {
            return IRegistry.ENTITY_TYPE.getKey(entity.getType()).toString();
        }), this.blockEntityTickers.size(), getTypeCount(this.blockEntityTickers, TickingBlockEntity::getType), this.getBlockTicks().count(), this.getFluidTicks().count(), this.gatherChunkSourceStats());
    }

    private static <T> String getTypeCount(Iterable<T> iterable, Function<T, String> function) {
        try {
            Object2IntOpenHashMap<String> object2intopenhashmap = new Object2IntOpenHashMap();
            Iterator iterator = iterable.iterator();

            while (iterator.hasNext()) {
                T t0 = iterator.next();
                String s = (String) function.apply(t0);

                object2intopenhashmap.addTo(s, 1);
            }

            return (String) object2intopenhashmap.object2IntEntrySet().stream().sorted(Comparator.comparing(Entry::getIntValue).reversed()).limit(5L).map((entry) -> {
                String s1 = (String) entry.getKey();

                return s1 + ":" + entry.getIntValue();
            }).collect(Collectors.joining(","));
        } catch (Exception exception) {
            return "";
        }
    }

    public static void makeObsidianPlatform(WorldServer worldserver) {
        BlockPosition blockposition = WorldServer.END_SPAWN_POINT;
        int i = blockposition.getX();
        int j = blockposition.getY() - 2;
        int k = blockposition.getZ();

        BlockPosition.betweenClosed(i - 2, j + 1, k - 2, i + 2, j + 3, k + 2).forEach((blockposition1) -> {
            worldserver.setBlockAndUpdate(blockposition1, Blocks.AIR.defaultBlockState());
        });
        BlockPosition.betweenClosed(i - 2, j, k - 2, i + 2, j, k + 2).forEach((blockposition1) -> {
            worldserver.setBlockAndUpdate(blockposition1, Blocks.OBSIDIAN.defaultBlockState());
        });
    }

    @Override
    public LevelEntityGetter<Entity> getEntities() {
        return this.entityManager.getEntityGetter();
    }

    public void addLegacyChunkEntities(Stream<Entity> stream) {
        this.entityManager.addLegacyChunkEntities(stream);
    }

    public void addWorldGenChunkEntities(Stream<Entity> stream) {
        this.entityManager.addWorldGenChunkEntities(stream);
    }

    public void startTickingChunk(Chunk chunk) {
        chunk.unpackTicks(this.getLevelData().getGameTime());
    }

    public void onStructureStartsAvailable(IChunkAccess ichunkaccess) {
        this.server.execute(() -> {
            this.structureCheck.onStructureLoad(ichunkaccess.getPos(), ichunkaccess.getAllStarts());
        });
    }

    @Override
    public void close() throws IOException {
        super.close();
        this.entityManager.close();
    }

    @Override
    public String gatherChunkSourceStats() {
        String s = this.chunkSource.gatherStats();

        return "Chunks[S] W: " + s + " E: " + this.entityManager.gatherStats();
    }

    public boolean areEntitiesLoaded(long i) {
        return this.entityManager.areEntitiesLoaded(i);
    }

    private boolean isPositionTickingWithEntitiesLoaded(long i) {
        return this.areEntitiesLoaded(i) && this.chunkSource.isPositionTicking(i);
    }

    public boolean isPositionEntityTicking(BlockPosition blockposition) {
        return this.entityManager.canPositionTick(blockposition) && this.chunkSource.chunkMap.getDistanceManager().inEntityTickingRange(ChunkCoordIntPair.asLong(blockposition));
    }

    public boolean isNaturalSpawningAllowed(BlockPosition blockposition) {
        return this.entityManager.canPositionTick(blockposition);
    }

    public boolean isNaturalSpawningAllowed(ChunkCoordIntPair chunkcoordintpair) {
        return this.entityManager.canPositionTick(chunkcoordintpair);
    }

    private final class a implements LevelCallback<Entity> {

        a() {}

        public void onCreated(Entity entity) {}

        public void onDestroyed(Entity entity) {
            WorldServer.this.getScoreboard().entityRemoved(entity);
        }

        public void onTickingStart(Entity entity) {
            WorldServer.this.entityTickList.add(entity);
        }

        public void onTickingEnd(Entity entity) {
            WorldServer.this.entityTickList.remove(entity);
        }

        public void onTrackingStart(Entity entity) {
            WorldServer.this.getChunkSource().addEntity(entity);
            if (entity instanceof EntityPlayer) {
                EntityPlayer entityplayer = (EntityPlayer) entity;

                WorldServer.this.players.add(entityplayer);
                WorldServer.this.updateSleepingPlayerList();
            }

            if (entity instanceof EntityInsentient) {
                EntityInsentient entityinsentient = (EntityInsentient) entity;

                if (WorldServer.this.isUpdatingNavigations) {
                    String s = "onTrackingStart called during navigation iteration";

                    SystemUtils.logAndPauseIfInIde("onTrackingStart called during navigation iteration", new IllegalStateException("onTrackingStart called during navigation iteration"));
                }

                WorldServer.this.navigatingMobs.add(entityinsentient);
            }

            if (entity instanceof EntityEnderDragon) {
                EntityEnderDragon entityenderdragon = (EntityEnderDragon) entity;
                EntityComplexPart[] aentitycomplexpart = entityenderdragon.getSubEntities();
                int i = aentitycomplexpart.length;

                for (int j = 0; j < i; ++j) {
                    EntityComplexPart entitycomplexpart = aentitycomplexpart[j];

                    WorldServer.this.dragonParts.put(entitycomplexpart.getId(), entitycomplexpart);
                }
            }

            entity.updateDynamicGameEventListener(DynamicGameEventListener::add);
        }

        public void onTrackingEnd(Entity entity) {
            WorldServer.this.getChunkSource().removeEntity(entity);
            if (entity instanceof EntityPlayer) {
                EntityPlayer entityplayer = (EntityPlayer) entity;

                WorldServer.this.players.remove(entityplayer);
                WorldServer.this.updateSleepingPlayerList();
            }

            if (entity instanceof EntityInsentient) {
                EntityInsentient entityinsentient = (EntityInsentient) entity;

                if (WorldServer.this.isUpdatingNavigations) {
                    String s = "onTrackingStart called during navigation iteration";

                    SystemUtils.logAndPauseIfInIde("onTrackingStart called during navigation iteration", new IllegalStateException("onTrackingStart called during navigation iteration"));
                }

                WorldServer.this.navigatingMobs.remove(entityinsentient);
            }

            if (entity instanceof EntityEnderDragon) {
                EntityEnderDragon entityenderdragon = (EntityEnderDragon) entity;
                EntityComplexPart[] aentitycomplexpart = entityenderdragon.getSubEntities();
                int i = aentitycomplexpart.length;

                for (int j = 0; j < i; ++j) {
                    EntityComplexPart entitycomplexpart = aentitycomplexpart[j];

                    WorldServer.this.dragonParts.remove(entitycomplexpart.getId());
                }
            }

            entity.updateDynamicGameEventListener(DynamicGameEventListener::remove);
        }

        public void onSectionChange(Entity entity) {
            entity.updateDynamicGameEventListener(DynamicGameEventListener::move);
        }
    }
}
