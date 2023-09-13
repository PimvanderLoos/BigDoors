package net.minecraft.server.level;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.mojang.datafixers.DataFixer;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.LongSets;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
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
import net.minecraft.core.BaseBlockPosition;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.IPosition;
import net.minecraft.core.IRegistry;
import net.minecraft.core.IRegistryCustom;
import net.minecraft.core.RegistryBlocks;
import net.minecraft.core.SectionPosition;
import net.minecraft.core.particles.ParticleParam;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddVibrationSignalPacket;
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
import net.minecraft.tags.ITagRegistry;
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
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.MobSpawner;
import net.minecraft.world.level.NextTickListEntry;
import net.minecraft.world.level.SpawnerCreature;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.TickListServer;
import net.minecraft.world.level.World;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.TickingBlockEntity;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.chunk.Chunk;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkSection;
import net.minecraft.world.level.chunk.storage.EntityStorage;
import net.minecraft.world.level.dimension.DimensionManager;
import net.minecraft.world.level.dimension.end.EnderDragonBattle;
import net.minecraft.world.level.entity.EntityAccess;
import net.minecraft.world.level.entity.EntityPersistentStorage;
import net.minecraft.world.level.entity.EntityTickList;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.entity.LevelCallback;
import net.minecraft.world.level.entity.LevelEntityGetter;
import net.minecraft.world.level.entity.PersistentEntitySectionManager;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEventListenerRegistrar;
import net.minecraft.world.level.gameevent.vibrations.VibrationPath;
import net.minecraft.world.level.levelgen.HeightMap;
import net.minecraft.world.level.levelgen.feature.StructureGenerator;
import net.minecraft.world.level.levelgen.structure.StructureBoundingBox;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureManager;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidType;
import net.minecraft.world.level.material.FluidTypes;
import net.minecraft.world.level.portal.PortalTravelAgent;
import net.minecraft.world.level.saveddata.PersistentBase;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WorldServer extends World implements GeneratorAccessSeed {

    public static final BlockPosition END_SPAWN_POINT = new BlockPosition(100, 50, 0);
    private static final Logger LOGGER = LogManager.getLogger();
    private static final int EMPTY_TIME_NO_TICK = 300;
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
    private final TickListServer<FluidType> liquidTicks;
    final Set<EntityInsentient> navigatingMobs;
    protected final PersistentRaid raids;
    private final ObjectLinkedOpenHashSet<BlockActionData> blockEvents;
    private boolean handlingTick;
    private final List<MobSpawner> customSpawners;
    @Nullable
    private final EnderDragonBattle dragonFight;
    final Int2ObjectMap<EntityComplexPart> dragonParts;
    private final StructureManager structureFeatureManager;
    private final boolean tickTime;

    public WorldServer(MinecraftServer minecraftserver, Executor executor, Convertable.ConversionSession convertable_conversionsession, IWorldDataServer iworlddataserver, ResourceKey<World> resourcekey, DimensionManager dimensionmanager, WorldLoadListener worldloadlistener, ChunkGenerator chunkgenerator, boolean flag, long i, List<MobSpawner> list, boolean flag1) {
        Objects.requireNonNull(minecraftserver);
        super(iworlddataserver, resourcekey, dimensionmanager, minecraftserver::getMethodProfiler, false, flag, i);
        this.players = Lists.newArrayList();
        this.entityTickList = new EntityTickList();
        Predicate predicate = (block) -> {
            return block == null || block.getBlockData().isAir();
        };
        RegistryBlocks registryblocks = IRegistry.BLOCK;

        Objects.requireNonNull(registryblocks);
        this.blockTicks = new TickListServer<>(this, predicate, registryblocks::getKey, this::b);
        predicate = (fluidtype) -> {
            return fluidtype == null || fluidtype == FluidTypes.EMPTY;
        };
        registryblocks = IRegistry.FLUID;
        Objects.requireNonNull(registryblocks);
        this.liquidTicks = new TickListServer<>(this, predicate, registryblocks::getKey, this::a);
        this.navigatingMobs = new ObjectOpenHashSet();
        this.blockEvents = new ObjectLinkedOpenHashSet();
        this.dragonParts = new Int2ObjectOpenHashMap();
        this.tickTime = flag1;
        this.server = minecraftserver;
        this.customSpawners = list;
        this.serverLevelData = iworlddataserver;
        boolean flag2 = minecraftserver.isSyncChunkWrites();
        DataFixer datafixer = minecraftserver.getDataFixer();
        EntityPersistentStorage<Entity> entitypersistentstorage = new EntityStorage(this, new File(convertable_conversionsession.a(resourcekey), "entities"), datafixer, flag2, minecraftserver);

        this.entityManager = new PersistentEntitySectionManager<>(Entity.class, new WorldServer.a(), entitypersistentstorage);
        DefinedStructureManager definedstructuremanager = minecraftserver.getDefinedStructureManager();
        int j = minecraftserver.getPlayerList().getViewDistance();
        PersistentEntitySectionManager persistententitysectionmanager = this.entityManager;

        Objects.requireNonNull(this.entityManager);
        this.chunkSource = new ChunkProviderServer(this, convertable_conversionsession, datafixer, definedstructuremanager, executor, chunkgenerator, j, flag2, worldloadlistener, persistententitysectionmanager::a, () -> {
            return minecraftserver.E().getWorldPersistentData();
        });
        this.portalForcer = new PortalTravelAgent(this);
        this.S();
        this.T();
        this.getWorldBorder().a(minecraftserver.as());
        this.raids = (PersistentRaid) this.getWorldPersistentData().a((nbttagcompound) -> {
            return PersistentRaid.a(this, nbttagcompound);
        }, () -> {
            return new PersistentRaid(this);
        }, PersistentRaid.a(this.getDimensionManager()));
        if (!minecraftserver.isEmbeddedServer()) {
            iworlddataserver.setGameType(minecraftserver.getGamemode());
        }

        this.structureFeatureManager = new StructureManager(this, minecraftserver.getSaveData().getGeneratorSettings());
        if (this.getDimensionManager().isCreateDragonBattle()) {
            this.dragonFight = new EnderDragonBattle(this, minecraftserver.getSaveData().getGeneratorSettings().getSeed(), minecraftserver.getSaveData().C());
        } else {
            this.dragonFight = null;
        }

        this.sleepStatus = new SleepStatus();
    }

    public void a(int i, int j, boolean flag, boolean flag1) {
        this.serverLevelData.setClearWeatherTime(i);
        this.serverLevelData.setWeatherDuration(j);
        this.serverLevelData.setThunderDuration(j);
        this.serverLevelData.setStorm(flag);
        this.serverLevelData.setThundering(flag1);
    }

    @Override
    public BiomeBase a(int i, int j, int k) {
        return this.getChunkProvider().getChunkGenerator().getWorldChunkManager().getBiome(i, j, k);
    }

    public StructureManager getStructureManager() {
        return this.structureFeatureManager;
    }

    public void doTick(BooleanSupplier booleansupplier) {
        GameProfilerFiller gameprofilerfiller = this.getMethodProfiler();

        this.handlingTick = true;
        gameprofilerfiller.enter("world border");
        this.getWorldBorder().s();
        gameprofilerfiller.exitEnter("weather");
        boolean flag = this.isRaining();
        int i;

        if (this.getDimensionManager().hasSkyLight()) {
            if (this.getGameRules().getBoolean(GameRules.RULE_WEATHER_CYCLE)) {
                i = this.serverLevelData.getClearWeatherTime();
                int j = this.serverLevelData.getThunderDuration();
                int k = this.serverLevelData.getWeatherDuration();
                boolean flag1 = this.levelData.isThundering();
                boolean flag2 = this.levelData.hasStorm();

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
                        j = this.random.nextInt(12000) + 3600;
                    } else {
                        j = this.random.nextInt(168000) + 12000;
                    }

                    if (k > 0) {
                        --k;
                        if (k == 0) {
                            flag2 = !flag2;
                        }
                    } else if (flag2) {
                        k = this.random.nextInt(12000) + 12000;
                    } else {
                        k = this.random.nextInt(168000) + 12000;
                    }
                }

                this.serverLevelData.setThunderDuration(j);
                this.serverLevelData.setWeatherDuration(k);
                this.serverLevelData.setClearWeatherTime(i);
                this.serverLevelData.setThundering(flag1);
                this.serverLevelData.setStorm(flag2);
            }

            this.oThunderLevel = this.thunderLevel;
            if (this.levelData.isThundering()) {
                this.thunderLevel = (float) ((double) this.thunderLevel + 0.01D);
            } else {
                this.thunderLevel = (float) ((double) this.thunderLevel - 0.01D);
            }

            this.thunderLevel = MathHelper.a(this.thunderLevel, 0.0F, 1.0F);
            this.oRainLevel = this.rainLevel;
            if (this.levelData.hasStorm()) {
                this.rainLevel = (float) ((double) this.rainLevel + 0.01D);
            } else {
                this.rainLevel = (float) ((double) this.rainLevel - 0.01D);
            }

            this.rainLevel = MathHelper.a(this.rainLevel, 0.0F, 1.0F);
        }

        if (this.oRainLevel != this.rainLevel) {
            this.server.getPlayerList().a((Packet) (new PacketPlayOutGameStateChange(PacketPlayOutGameStateChange.RAIN_LEVEL_CHANGE, this.rainLevel)), this.getDimensionKey());
        }

        if (this.oThunderLevel != this.thunderLevel) {
            this.server.getPlayerList().a((Packet) (new PacketPlayOutGameStateChange(PacketPlayOutGameStateChange.THUNDER_LEVEL_CHANGE, this.thunderLevel)), this.getDimensionKey());
        }

        if (flag != this.isRaining()) {
            if (flag) {
                this.server.getPlayerList().sendAll(new PacketPlayOutGameStateChange(PacketPlayOutGameStateChange.STOP_RAINING, 0.0F));
            } else {
                this.server.getPlayerList().sendAll(new PacketPlayOutGameStateChange(PacketPlayOutGameStateChange.START_RAINING, 0.0F));
            }

            this.server.getPlayerList().sendAll(new PacketPlayOutGameStateChange(PacketPlayOutGameStateChange.RAIN_LEVEL_CHANGE, this.rainLevel));
            this.server.getPlayerList().sendAll(new PacketPlayOutGameStateChange(PacketPlayOutGameStateChange.THUNDER_LEVEL_CHANGE, this.thunderLevel));
        }

        i = this.getGameRules().getInt(GameRules.RULE_PLAYERS_SLEEPING_PERCENTAGE);
        if (this.sleepStatus.a(i) && this.sleepStatus.a(i, this.players)) {
            if (this.getGameRules().getBoolean(GameRules.RULE_DAYLIGHT)) {
                long l = this.levelData.getDayTime() + 24000L;

                this.setDayTime(l - l % 24000L);
            }

            this.wakeupPlayers();
            if (this.getGameRules().getBoolean(GameRules.RULE_WEATHER_CYCLE)) {
                this.clearWeather();
            }
        }

        this.S();
        this.b();
        gameprofilerfiller.exitEnter("tickPending");
        if (!this.isDebugWorld()) {
            this.blockTicks.b();
            this.liquidTicks.b();
        }

        gameprofilerfiller.exitEnter("raid");
        this.raids.a();
        gameprofilerfiller.exitEnter("chunkSource");
        this.getChunkProvider().tick(booleansupplier);
        gameprofilerfiller.exitEnter("blockEvents");
        this.aq();
        this.handlingTick = false;
        gameprofilerfiller.exit();
        boolean flag3 = !this.players.isEmpty() || !this.getForceLoadedChunks().isEmpty();

        if (flag3) {
            this.resetEmptyTime();
        }

        if (flag3 || this.emptyTime++ < 300) {
            gameprofilerfiller.enter("entities");
            if (this.dragonFight != null) {
                gameprofilerfiller.enter("dragonFight");
                this.dragonFight.b();
                gameprofilerfiller.exit();
            }

            this.entityTickList.a((entity) -> {
                if (!entity.isRemoved()) {
                    if (this.i(entity)) {
                        entity.die();
                    } else {
                        gameprofilerfiller.enter("checkDespawn");
                        entity.checkDespawn();
                        gameprofilerfiller.exit();
                        Entity entity1 = entity.getVehicle();

                        if (entity1 != null) {
                            if (!entity1.isRemoved() && entity1.u(entity)) {
                                return;
                            }

                            entity.stopRiding();
                        }

                        gameprofilerfiller.enter("tick");
                        this.a(this::entityJoinedWorld, entity);
                        gameprofilerfiller.exit();
                    }
                }
            });
            gameprofilerfiller.exit();
            this.tickBlockEntities();
        }

        gameprofilerfiller.enter("entityManagement");
        this.entityManager.tick();
        gameprofilerfiller.exit();
    }

    protected void b() {
        if (this.tickTime) {
            long i = this.levelData.getTime() + 1L;

            this.serverLevelData.setTime(i);
            this.serverLevelData.u().a(this.server, i);
            if (this.levelData.q().getBoolean(GameRules.RULE_DAYLIGHT)) {
                this.setDayTime(this.levelData.getDayTime() + 1L);
            }

        }
    }

    public void setDayTime(long i) {
        this.serverLevelData.setDayTime(i);
    }

    public void doMobSpawning(boolean flag, boolean flag1) {
        Iterator iterator = this.customSpawners.iterator();

        while (iterator.hasNext()) {
            MobSpawner mobspawner = (MobSpawner) iterator.next();

            mobspawner.a(this, flag, flag1);
        }

    }

    private boolean i(Entity entity) {
        return !this.server.getSpawnAnimals() && (entity instanceof EntityAnimal || entity instanceof EntityWaterAnimal) ? true : !this.server.getSpawnNPCs() && entity instanceof NPC;
    }

    private void wakeupPlayers() {
        this.sleepStatus.a();
        ((List) this.players.stream().filter(EntityLiving::isSleeping).collect(Collectors.toList())).forEach((entityplayer) -> {
            entityplayer.wakeup(false, false);
        });
    }

    public void a(Chunk chunk, int i) {
        ChunkCoordIntPair chunkcoordintpair = chunk.getPos();
        boolean flag = this.isRaining();
        int j = chunkcoordintpair.d();
        int k = chunkcoordintpair.e();
        GameProfilerFiller gameprofilerfiller = this.getMethodProfiler();

        gameprofilerfiller.enter("thunder");
        BlockPosition blockposition;

        if (flag && this.Y() && this.random.nextInt(100000) == 0) {
            blockposition = this.a(this.a(j, 0, k, 15));
            if (this.isRainingAt(blockposition)) {
                DifficultyDamageScaler difficultydamagescaler = this.getDamageScaler(blockposition);
                boolean flag1 = this.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING) && this.random.nextDouble() < (double) difficultydamagescaler.b() * 0.01D && !this.getType(blockposition.down()).a(Blocks.LIGHTNING_ROD);

                if (flag1) {
                    EntityHorseSkeleton entityhorseskeleton = (EntityHorseSkeleton) EntityTypes.SKELETON_HORSE.a((World) this);

                    entityhorseskeleton.v(true);
                    entityhorseskeleton.setAgeRaw(0);
                    entityhorseskeleton.setPosition((double) blockposition.getX(), (double) blockposition.getY(), (double) blockposition.getZ());
                    this.addEntity(entityhorseskeleton);
                }

                EntityLightning entitylightning = (EntityLightning) EntityTypes.LIGHTNING_BOLT.a((World) this);

                entitylightning.d(Vec3D.c((BaseBlockPosition) blockposition));
                entitylightning.setEffect(flag1);
                this.addEntity(entitylightning);
            }
        }

        gameprofilerfiller.exitEnter("iceandsnow");
        if (this.random.nextInt(16) == 0) {
            blockposition = this.getHighestBlockYAt(HeightMap.Type.MOTION_BLOCKING, this.a(j, 0, k, 15));
            BlockPosition blockposition1 = blockposition.down();
            BiomeBase biomebase = this.getBiome(blockposition);

            if (biomebase.a((IWorldReader) this, blockposition1)) {
                this.setTypeUpdate(blockposition1, Blocks.ICE.getBlockData());
            }

            if (flag) {
                if (biomebase.b(this, blockposition)) {
                    this.setTypeUpdate(blockposition, Blocks.SNOW.getBlockData());
                }

                IBlockData iblockdata = this.getType(blockposition1);
                BiomeBase.Precipitation biomebase_precipitation = this.getBiome(blockposition).c();

                if (biomebase_precipitation == BiomeBase.Precipitation.RAIN && biomebase.b(blockposition1)) {
                    biomebase_precipitation = BiomeBase.Precipitation.SNOW;
                }

                iblockdata.getBlock().a(iblockdata, (World) this, blockposition1, biomebase_precipitation);
            }
        }

        gameprofilerfiller.exitEnter("tickBlocks");
        if (i > 0) {
            ChunkSection[] achunksection = chunk.getSections();
            int l = achunksection.length;

            for (int i1 = 0; i1 < l; ++i1) {
                ChunkSection chunksection = achunksection[i1];

                if (chunksection != Chunk.EMPTY_SECTION && chunksection.d()) {
                    int j1 = chunksection.getYPosition();

                    for (int k1 = 0; k1 < i; ++k1) {
                        BlockPosition blockposition2 = this.a(j, j1, k, 15);

                        gameprofilerfiller.enter("randomTick");
                        IBlockData iblockdata1 = chunksection.getType(blockposition2.getX() - j, blockposition2.getY() - j1, blockposition2.getZ() - k);

                        if (iblockdata1.isTicking()) {
                            iblockdata1.b(this, blockposition2, this.random);
                        }

                        Fluid fluid = iblockdata1.getFluid();

                        if (fluid.f()) {
                            fluid.b(this, blockposition2, this.random);
                        }

                        gameprofilerfiller.exit();
                    }
                }
            }
        }

        gameprofilerfiller.exit();
    }

    private Optional<BlockPosition> E(BlockPosition blockposition) {
        Optional<BlockPosition> optional = this.A().d((villageplacetype) -> {
            return villageplacetype == VillagePlaceType.LIGHTNING_ROD;
        }, (blockposition1) -> {
            return blockposition1.getY() == this.getLevel().a(HeightMap.Type.WORLD_SURFACE, blockposition1.getX(), blockposition1.getZ()) - 1;
        }, blockposition, 128, VillagePlace.Occupancy.ANY);

        return optional.map((blockposition1) -> {
            return blockposition1.up(1);
        });
    }

    protected BlockPosition a(BlockPosition blockposition) {
        BlockPosition blockposition1 = this.getHighestBlockYAt(HeightMap.Type.MOTION_BLOCKING, blockposition);
        Optional<BlockPosition> optional = this.E(blockposition1);

        if (optional.isPresent()) {
            return (BlockPosition) optional.get();
        } else {
            AxisAlignedBB axisalignedbb = (new AxisAlignedBB(blockposition1, new BlockPosition(blockposition1.getX(), this.getMaxBuildHeight(), blockposition1.getZ()))).g(3.0D);
            List<EntityLiving> list = this.a(EntityLiving.class, axisalignedbb, (entityliving) -> {
                return entityliving != null && entityliving.isAlive() && this.g(entityliving.getChunkCoordinates());
            });

            if (!list.isEmpty()) {
                return ((EntityLiving) list.get(this.random.nextInt(list.size()))).getChunkCoordinates();
            } else {
                if (blockposition1.getY() == this.getMinBuildHeight() - 1) {
                    blockposition1 = blockposition1.up(2);
                }

                return blockposition1;
            }
        }
    }

    public boolean c() {
        return this.handlingTick;
    }

    public boolean d() {
        return this.getGameRules().getInt(GameRules.RULE_PLAYERS_SLEEPING_PERCENTAGE) <= 100;
    }

    private void an() {
        if (this.d()) {
            if (!this.getMinecraftServer().isEmbeddedServer() || this.getMinecraftServer().o()) {
                int i = this.getGameRules().getInt(GameRules.RULE_PLAYERS_SLEEPING_PERCENTAGE);
                ChatMessage chatmessage;

                if (this.sleepStatus.a(i)) {
                    chatmessage = new ChatMessage("sleep.skipping_night");
                } else {
                    chatmessage = new ChatMessage("sleep.players_sleeping", new Object[]{this.sleepStatus.b(), this.sleepStatus.b(i)});
                }

                Iterator iterator = this.players.iterator();

                while (iterator.hasNext()) {
                    EntityPlayer entityplayer = (EntityPlayer) iterator.next();

                    entityplayer.a((IChatBaseComponent) chatmessage, true);
                }

            }
        }
    }

    public void everyoneSleeping() {
        if (!this.players.isEmpty() && this.sleepStatus.a(this.players)) {
            this.an();
        }

    }

    @Override
    public ScoreboardServer getScoreboard() {
        return this.server.getScoreboard();
    }

    private void clearWeather() {
        this.serverLevelData.setWeatherDuration(0);
        this.serverLevelData.setStorm(false);
        this.serverLevelData.setThunderDuration(0);
        this.serverLevelData.setThundering(false);
    }

    public void resetEmptyTime() {
        this.emptyTime = 0;
    }

    private void a(NextTickListEntry<FluidType> nextticklistentry) {
        Fluid fluid = this.getFluid(nextticklistentry.pos);

        if (fluid.getType() == nextticklistentry.b()) {
            fluid.a((World) this, nextticklistentry.pos);
        }

    }

    private void b(NextTickListEntry<Block> nextticklistentry) {
        IBlockData iblockdata = this.getType(nextticklistentry.pos);

        if (iblockdata.a((Block) nextticklistentry.b())) {
            iblockdata.a(this, nextticklistentry.pos, this.random);
        }

    }

    public void entityJoinedWorld(Entity entity) {
        entity.aZ();
        GameProfilerFiller gameprofilerfiller = this.getMethodProfiler();

        ++entity.tickCount;
        this.getMethodProfiler().a(() -> {
            return IRegistry.ENTITY_TYPE.getKey(entity.getEntityType()).toString();
        });
        gameprofilerfiller.c("tickNonPassenger");
        entity.tick();
        this.getMethodProfiler().exit();
        Iterator iterator = entity.getPassengers().iterator();

        while (iterator.hasNext()) {
            Entity entity1 = (Entity) iterator.next();

            this.a(entity, entity1);
        }

    }

    private void a(Entity entity, Entity entity1) {
        if (!entity1.isRemoved() && entity1.getVehicle() == entity) {
            if (entity1 instanceof EntityHuman || this.entityTickList.c(entity1)) {
                entity1.aZ();
                ++entity1.tickCount;
                GameProfilerFiller gameprofilerfiller = this.getMethodProfiler();

                gameprofilerfiller.a(() -> {
                    return IRegistry.ENTITY_TYPE.getKey(entity1.getEntityType()).toString();
                });
                gameprofilerfiller.c("tickPassenger");
                entity1.passengerTick();
                gameprofilerfiller.exit();
                Iterator iterator = entity1.getPassengers().iterator();

                while (iterator.hasNext()) {
                    Entity entity2 = (Entity) iterator.next();

                    this.a(entity1, entity2);
                }

            }
        } else {
            entity1.stopRiding();
        }
    }

    @Override
    public boolean a(EntityHuman entityhuman, BlockPosition blockposition) {
        return !this.server.a(this, blockposition, entityhuman) && this.getWorldBorder().a(blockposition);
    }

    public void save(@Nullable IProgressUpdate iprogressupdate, boolean flag, boolean flag1) {
        ChunkProviderServer chunkproviderserver = this.getChunkProvider();

        if (!flag1) {
            if (iprogressupdate != null) {
                iprogressupdate.a(new ChatMessage("menu.savingLevel"));
            }

            this.ap();
            if (iprogressupdate != null) {
                iprogressupdate.c(new ChatMessage("menu.savingChunks"));
            }

            chunkproviderserver.save(flag);
            if (flag) {
                this.entityManager.c();
            } else {
                this.entityManager.b();
            }

        }
    }

    private void ap() {
        if (this.dragonFight != null) {
            this.server.getSaveData().a(this.dragonFight.a());
        }

        this.getChunkProvider().getWorldPersistentData().a();
    }

    public <T extends Entity> List<? extends T> a(EntityTypeTest<Entity, T> entitytypetest, Predicate<? super T> predicate) {
        List<T> list = Lists.newArrayList();

        this.getEntities().a(entitytypetest, (entity) -> {
            if (predicate.test(entity)) {
                list.add(entity);
            }

        });
        return list;
    }

    public List<? extends EntityEnderDragon> h() {
        return this.a((EntityTypeTest) EntityTypes.ENDER_DRAGON, EntityLiving::isAlive);
    }

    public List<EntityPlayer> a(Predicate<? super EntityPlayer> predicate) {
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
    public EntityPlayer i() {
        List<EntityPlayer> list = this.a(EntityLiving::isAlive);

        return list.isEmpty() ? null : (EntityPlayer) list.get(this.random.nextInt(list.size()));
    }

    @Override
    public boolean addEntity(Entity entity) {
        return this.addEntity0(entity);
    }

    public boolean addEntitySerialized(Entity entity) {
        return this.addEntity0(entity);
    }

    public void addEntityTeleport(Entity entity) {
        this.addEntity0(entity);
    }

    public void addPlayerCommand(EntityPlayer entityplayer) {
        this.addPlayer0(entityplayer);
    }

    public void addPlayerPortal(EntityPlayer entityplayer) {
        this.addPlayer0(entityplayer);
    }

    public void addPlayerJoin(EntityPlayer entityplayer) {
        this.addPlayer0(entityplayer);
    }

    public void addPlayerRespawn(EntityPlayer entityplayer) {
        this.addPlayer0(entityplayer);
    }

    private void addPlayer0(EntityPlayer entityplayer) {
        Entity entity = (Entity) this.getEntities().a(entityplayer.getUniqueID());

        if (entity != null) {
            WorldServer.LOGGER.warn("Force-added player with duplicate UUID {}", entityplayer.getUniqueID().toString());
            entity.decouple();
            this.a((EntityPlayer) entity, Entity.RemovalReason.DISCARDED);
        }

        this.entityManager.a((EntityAccess) entityplayer);
    }

    private boolean addEntity0(Entity entity) {
        if (entity.isRemoved()) {
            WorldServer.LOGGER.warn("Tried to add entity {} but it was marked as removed already", EntityTypes.getName(entity.getEntityType()));
            return false;
        } else {
            return this.entityManager.a((EntityAccess) entity);
        }
    }

    public boolean addAllEntitiesSafely(Entity entity) {
        Stream stream = entity.recursiveStream().map(Entity::getUniqueID);
        PersistentEntitySectionManager persistententitysectionmanager = this.entityManager;

        Objects.requireNonNull(this.entityManager);
        if (stream.anyMatch(persistententitysectionmanager::a)) {
            return false;
        } else {
            this.addAllEntities(entity);
            return true;
        }
    }

    public void unloadChunk(Chunk chunk) {
        chunk.C();
    }

    public void a(EntityPlayer entityplayer, Entity.RemovalReason entity_removalreason) {
        entityplayer.a(entity_removalreason);
    }

    @Override
    public void a(int i, BlockPosition blockposition, int j) {
        Iterator iterator = this.server.getPlayerList().getPlayers().iterator();

        while (iterator.hasNext()) {
            EntityPlayer entityplayer = (EntityPlayer) iterator.next();

            if (entityplayer != null && entityplayer.level == this && entityplayer.getId() != i) {
                double d0 = (double) blockposition.getX() - entityplayer.locX();
                double d1 = (double) blockposition.getY() - entityplayer.locY();
                double d2 = (double) blockposition.getZ() - entityplayer.locZ();

                if (d0 * d0 + d1 * d1 + d2 * d2 < 1024.0D) {
                    entityplayer.connection.sendPacket(new PacketPlayOutBlockBreakAnimation(i, blockposition, j));
                }
            }
        }

    }

    @Override
    public void playSound(@Nullable EntityHuman entityhuman, double d0, double d1, double d2, SoundEffect soundeffect, SoundCategory soundcategory, float f, float f1) {
        this.server.getPlayerList().sendPacketNearby(entityhuman, d0, d1, d2, f > 1.0F ? (double) (16.0F * f) : 16.0D, this.getDimensionKey(), new PacketPlayOutNamedSoundEffect(soundeffect, soundcategory, d0, d1, d2, f, f1));
    }

    @Override
    public void playSound(@Nullable EntityHuman entityhuman, Entity entity, SoundEffect soundeffect, SoundCategory soundcategory, float f, float f1) {
        this.server.getPlayerList().sendPacketNearby(entityhuman, entity.locX(), entity.locY(), entity.locZ(), f > 1.0F ? (double) (16.0F * f) : 16.0D, this.getDimensionKey(), new PacketPlayOutEntitySound(soundeffect, soundcategory, entity, f, f1));
    }

    @Override
    public void b(int i, BlockPosition blockposition, int j) {
        this.server.getPlayerList().sendAll(new PacketPlayOutWorldEvent(i, blockposition, j, true));
    }

    @Override
    public void a(@Nullable EntityHuman entityhuman, int i, BlockPosition blockposition, int j) {
        this.server.getPlayerList().sendPacketNearby(entityhuman, (double) blockposition.getX(), (double) blockposition.getY(), (double) blockposition.getZ(), 64.0D, this.getDimensionKey(), new PacketPlayOutWorldEvent(i, blockposition, j, false));
    }

    @Override
    public int getLogicalHeight() {
        return this.getDimensionManager().getLogicalHeight();
    }

    @Override
    public void a(@Nullable Entity entity, GameEvent gameevent, BlockPosition blockposition) {
        this.a(entity, gameevent, blockposition, gameevent.b());
    }

    @Override
    public void notify(BlockPosition blockposition, IBlockData iblockdata, IBlockData iblockdata1, int i) {
        this.getChunkProvider().flagDirty(blockposition);
        VoxelShape voxelshape = iblockdata.getCollisionShape(this, blockposition);
        VoxelShape voxelshape1 = iblockdata1.getCollisionShape(this, blockposition);

        if (VoxelShapes.c(voxelshape, voxelshape1, OperatorBoolean.NOT_SAME)) {
            Iterator iterator = this.navigatingMobs.iterator();

            while (iterator.hasNext()) {
                EntityInsentient entityinsentient = (EntityInsentient) iterator.next();
                NavigationAbstract navigationabstract = entityinsentient.getNavigation();

                if (!navigationabstract.i()) {
                    navigationabstract.b(blockposition);
                }
            }

        }
    }

    @Override
    public void broadcastEntityEffect(Entity entity, byte b0) {
        this.getChunkProvider().broadcastIncludingSelf(entity, new PacketPlayOutEntityStatus(entity, b0));
    }

    @Override
    public ChunkProviderServer getChunkProvider() {
        return this.chunkSource;
    }

    @Override
    public Explosion createExplosion(@Nullable Entity entity, @Nullable DamageSource damagesource, @Nullable ExplosionDamageCalculator explosiondamagecalculator, double d0, double d1, double d2, float f, boolean flag, Explosion.Effect explosion_effect) {
        Explosion explosion = new Explosion(this, entity, damagesource, explosiondamagecalculator, d0, d1, d2, f, flag, explosion_effect);

        explosion.a();
        explosion.a(false);
        if (explosion_effect == Explosion.Effect.NONE) {
            explosion.clearBlocks();
        }

        Iterator iterator = this.players.iterator();

        while (iterator.hasNext()) {
            EntityPlayer entityplayer = (EntityPlayer) iterator.next();

            if (entityplayer.h(d0, d1, d2) < 4096.0D) {
                entityplayer.connection.sendPacket(new PacketPlayOutExplosion(d0, d1, d2, f, explosion.getBlocks(), (Vec3D) explosion.c().get(entityplayer)));
            }
        }

        return explosion;
    }

    @Override
    public void playBlockAction(BlockPosition blockposition, Block block, int i, int j) {
        this.blockEvents.add(new BlockActionData(blockposition, block, i, j));
    }

    private void aq() {
        while (!this.blockEvents.isEmpty()) {
            BlockActionData blockactiondata = (BlockActionData) this.blockEvents.removeFirst();

            if (this.a(blockactiondata)) {
                this.server.getPlayerList().sendPacketNearby((EntityHuman) null, (double) blockactiondata.a().getX(), (double) blockactiondata.a().getY(), (double) blockactiondata.a().getZ(), 64.0D, this.getDimensionKey(), new PacketPlayOutBlockAction(blockactiondata.a(), blockactiondata.b(), blockactiondata.c(), blockactiondata.d()));
            }
        }

    }

    private boolean a(BlockActionData blockactiondata) {
        IBlockData iblockdata = this.getType(blockactiondata.a());

        return iblockdata.a(blockactiondata.b()) ? iblockdata.a((World) this, blockactiondata.a(), blockactiondata.c(), blockactiondata.d()) : false;
    }

    @Override
    public TickListServer<Block> getBlockTickList() {
        return this.blockTicks;
    }

    @Override
    public TickListServer<FluidType> getFluidTickList() {
        return this.liquidTicks;
    }

    @Nonnull
    @Override
    public MinecraftServer getMinecraftServer() {
        return this.server;
    }

    public PortalTravelAgent getTravelAgent() {
        return this.portalForcer;
    }

    public DefinedStructureManager p() {
        return this.server.getDefinedStructureManager();
    }

    public void a(VibrationPath vibrationpath) {
        BlockPosition blockposition = vibrationpath.b();
        ClientboundAddVibrationSignalPacket clientboundaddvibrationsignalpacket = new ClientboundAddVibrationSignalPacket(vibrationpath);

        this.players.forEach((entityplayer) -> {
            this.a(entityplayer, false, (double) blockposition.getX(), (double) blockposition.getY(), (double) blockposition.getZ(), clientboundaddvibrationsignalpacket);
        });
    }

    public <T extends ParticleParam> int a(T t0, double d0, double d1, double d2, int i, double d3, double d4, double d5, double d6) {
        PacketPlayOutWorldParticles packetplayoutworldparticles = new PacketPlayOutWorldParticles(t0, false, d0, d1, d2, (float) d3, (float) d4, (float) d5, (float) d6, i);
        int j = 0;

        for (int k = 0; k < this.players.size(); ++k) {
            EntityPlayer entityplayer = (EntityPlayer) this.players.get(k);

            if (this.a(entityplayer, false, d0, d1, d2, packetplayoutworldparticles)) {
                ++j;
            }
        }

        return j;
    }

    public <T extends ParticleParam> boolean a(EntityPlayer entityplayer, T t0, boolean flag, double d0, double d1, double d2, int i, double d3, double d4, double d5, double d6) {
        Packet<?> packet = new PacketPlayOutWorldParticles(t0, flag, d0, d1, d2, (float) d3, (float) d4, (float) d5, (float) d6, i);

        return this.a(entityplayer, flag, d0, d1, d2, packet);
    }

    private boolean a(EntityPlayer entityplayer, boolean flag, double d0, double d1, double d2, Packet<?> packet) {
        if (entityplayer.getWorldServer() != this) {
            return false;
        } else {
            BlockPosition blockposition = entityplayer.getChunkCoordinates();

            if (blockposition.a((IPosition) (new Vec3D(d0, d1, d2)), flag ? 512.0D : 32.0D)) {
                entityplayer.connection.sendPacket(packet);
                return true;
            } else {
                return false;
            }
        }
    }

    @Nullable
    @Override
    public Entity getEntity(int i) {
        return (Entity) this.getEntities().a(i);
    }

    @Deprecated
    @Nullable
    public Entity b(int i) {
        Entity entity = (Entity) this.getEntities().a(i);

        return entity != null ? entity : (Entity) this.dragonParts.get(i);
    }

    @Nullable
    public Entity getEntity(UUID uuid) {
        return (Entity) this.getEntities().a(uuid);
    }

    @Nullable
    public BlockPosition a(StructureGenerator<?> structuregenerator, BlockPosition blockposition, int i, boolean flag) {
        return !this.server.getSaveData().getGeneratorSettings().shouldGenerateMapFeatures() ? null : this.getChunkProvider().getChunkGenerator().findNearestMapFeature(this, structuregenerator, blockposition, i, flag);
    }

    @Nullable
    public BlockPosition a(BiomeBase biomebase, BlockPosition blockposition, int i, int j) {
        return this.getChunkProvider().getChunkGenerator().getWorldChunkManager().a(blockposition.getX(), blockposition.getY(), blockposition.getZ(), i, j, (biomebase1) -> {
            return biomebase1 == biomebase;
        }, this.random, true);
    }

    @Override
    public CraftingManager getCraftingManager() {
        return this.server.getCraftingManager();
    }

    @Override
    public ITagRegistry r() {
        return this.server.getTagRegistry();
    }

    @Override
    public boolean isSavingDisabled() {
        return this.noSave;
    }

    @Override
    public IRegistryCustom t() {
        return this.server.getCustomRegistry();
    }

    public WorldPersistentData getWorldPersistentData() {
        return this.getChunkProvider().getWorldPersistentData();
    }

    @Nullable
    @Override
    public WorldMap a(String s) {
        return (WorldMap) this.getMinecraftServer().E().getWorldPersistentData().a(WorldMap::b, s);
    }

    @Override
    public void a(String s, WorldMap worldmap) {
        this.getMinecraftServer().E().getWorldPersistentData().a(s, (PersistentBase) worldmap);
    }

    @Override
    public int getWorldMapCount() {
        return ((PersistentIdCounts) this.getMinecraftServer().E().getWorldPersistentData().a(PersistentIdCounts::b, PersistentIdCounts::new, "idcounts")).a();
    }

    public void a(BlockPosition blockposition, float f) {
        ChunkCoordIntPair chunkcoordintpair = new ChunkCoordIntPair(new BlockPosition(this.levelData.a(), 0, this.levelData.c()));

        this.levelData.setSpawn(blockposition, f);
        this.getChunkProvider().removeTicket(TicketType.START, chunkcoordintpair, 11, Unit.INSTANCE);
        this.getChunkProvider().addTicket(TicketType.START, new ChunkCoordIntPair(blockposition), 11, Unit.INSTANCE);
        this.getMinecraftServer().getPlayerList().sendAll(new PacketPlayOutSpawnPosition(blockposition, f));
    }

    public BlockPosition getSpawn() {
        BlockPosition blockposition = new BlockPosition(this.levelData.a(), this.levelData.b(), this.levelData.c());

        if (!this.getWorldBorder().a(blockposition)) {
            blockposition = this.getHighestBlockYAt(HeightMap.Type.MOTION_BLOCKING, new BlockPosition(this.getWorldBorder().getCenterX(), 0.0D, this.getWorldBorder().getCenterZ()));
        }

        return blockposition;
    }

    public float x() {
        return this.levelData.d();
    }

    public LongSet getForceLoadedChunks() {
        ForcedChunk forcedchunk = (ForcedChunk) this.getWorldPersistentData().a(ForcedChunk::b, "chunks");

        return (LongSet) (forcedchunk != null ? LongSets.unmodifiable(forcedchunk.a()) : LongSets.EMPTY_SET);
    }

    public boolean setForceLoaded(int i, int j, boolean flag) {
        ForcedChunk forcedchunk = (ForcedChunk) this.getWorldPersistentData().a(ForcedChunk::b, ForcedChunk::new, "chunks");
        ChunkCoordIntPair chunkcoordintpair = new ChunkCoordIntPair(i, j);
        long k = chunkcoordintpair.pair();
        boolean flag1;

        if (flag) {
            flag1 = forcedchunk.a().add(k);
            if (flag1) {
                this.getChunkAt(i, j);
            }
        } else {
            flag1 = forcedchunk.a().remove(k);
        }

        forcedchunk.a(flag1);
        if (flag1) {
            this.getChunkProvider().a(chunkcoordintpair, flag);
        }

        return flag1;
    }

    @Override
    public List<EntityPlayer> getPlayers() {
        return this.players;
    }

    @Override
    public void a(BlockPosition blockposition, IBlockData iblockdata, IBlockData iblockdata1) {
        Optional<VillagePlaceType> optional = VillagePlaceType.b(iblockdata);
        Optional<VillagePlaceType> optional1 = VillagePlaceType.b(iblockdata1);

        if (!Objects.equals(optional, optional1)) {
            BlockPosition blockposition1 = blockposition.immutableCopy();

            optional.ifPresent((villageplacetype) -> {
                this.getMinecraftServer().execute(() -> {
                    this.A().a(blockposition1);
                    PacketDebug.b(this, blockposition1);
                });
            });
            optional1.ifPresent((villageplacetype) -> {
                this.getMinecraftServer().execute(() -> {
                    this.A().a(blockposition1, villageplacetype);
                    PacketDebug.a(this, blockposition1);
                });
            });
        }
    }

    public VillagePlace A() {
        return this.getChunkProvider().j();
    }

    public boolean b(BlockPosition blockposition) {
        return this.a(blockposition, 1);
    }

    public boolean a(SectionPosition sectionposition) {
        return this.b(sectionposition.q());
    }

    public boolean a(BlockPosition blockposition, int i) {
        return i > 6 ? false : this.b(SectionPosition.a(blockposition)) <= i;
    }

    public int b(SectionPosition sectionposition) {
        return this.A().a(sectionposition);
    }

    public PersistentRaid getPersistentRaid() {
        return this.raids;
    }

    @Nullable
    public Raid c(BlockPosition blockposition) {
        return this.raids.getNearbyRaid(blockposition, 9216);
    }

    public boolean d(BlockPosition blockposition) {
        return this.c(blockposition) != null;
    }

    public void a(ReputationEvent reputationevent, Entity entity, ReputationHandler reputationhandler) {
        reputationhandler.a(reputationevent, entity);
    }

    public void a(Path path) throws IOException {
        PlayerChunkMap playerchunkmap = this.getChunkProvider().chunkMap;
        BufferedWriter bufferedwriter = Files.newBufferedWriter(path.resolve("stats.txt"));

        try {
            bufferedwriter.write(String.format("spawning_chunks: %d\n", playerchunkmap.e().b()));
            SpawnerCreature.d spawnercreature_d = this.getChunkProvider().k();

            if (spawnercreature_d != null) {
                ObjectIterator objectiterator = spawnercreature_d.b().object2IntEntrySet().iterator();

                while (objectiterator.hasNext()) {
                    Entry<EnumCreatureType> entry = (Entry) objectiterator.next();

                    bufferedwriter.write(String.format("spawn_count.%s: %d\n", ((EnumCreatureType) entry.getKey()).a(), entry.getIntValue()));
                }
            }

            bufferedwriter.write(String.format("entities: %s\n", this.entityManager.e()));
            bufferedwriter.write(String.format("block_entity_tickers: %d\n", this.blockEntityTickers.size()));
            bufferedwriter.write(String.format("block_ticks: %d\n", this.getBlockTickList().a()));
            bufferedwriter.write(String.format("fluid_ticks: %d\n", this.getFluidTickList().a()));
            bufferedwriter.write("distance_manager: " + playerchunkmap.e().c() + "\n");
            bufferedwriter.write(String.format("pending_tasks: %d\n", this.getChunkProvider().f()));
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

        this.a(crashreport);
        BufferedWriter bufferedwriter1 = Files.newBufferedWriter(path.resolve("example_crash.txt"));

        try {
            bufferedwriter1.write(crashreport.e());
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
            playerchunkmap.a((Writer) bufferedwriter2);
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
            this.entityManager.a((Writer) bufferedwriter3);
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
            a((Writer) bufferedwriter4, this.getEntities().a());
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
            this.a((Writer) bufferedwriter5);
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

    private static void a(Writer writer, Iterable<Entity> iterable) throws IOException {
        CSVWriter csvwriter = CSVWriter.a().a("x").a("y").a("z").a("uuid").a("type").a("alive").a("display_name").a("custom_name").a(writer);
        Iterator iterator = iterable.iterator();

        while (iterator.hasNext()) {
            Entity entity = (Entity) iterator.next();
            IChatBaseComponent ichatbasecomponent = entity.getCustomName();
            IChatBaseComponent ichatbasecomponent1 = entity.getScoreboardDisplayName();

            csvwriter.a(entity.locX(), entity.locY(), entity.locZ(), entity.getUniqueID(), IRegistry.ENTITY_TYPE.getKey(entity.getEntityType()), entity.isAlive(), ichatbasecomponent1.getString(), ichatbasecomponent != null ? ichatbasecomponent.getString() : null);
        }

    }

    private void a(Writer writer) throws IOException {
        CSVWriter csvwriter = CSVWriter.a().a("x").a("y").a("z").a("type").a(writer);
        Iterator iterator = this.blockEntityTickers.iterator();

        while (iterator.hasNext()) {
            TickingBlockEntity tickingblockentity = (TickingBlockEntity) iterator.next();
            BlockPosition blockposition = tickingblockentity.c();

            csvwriter.a(blockposition.getX(), blockposition.getY(), blockposition.getZ(), tickingblockentity.d());
        }

    }

    @VisibleForTesting
    public void a(StructureBoundingBox structureboundingbox) {
        this.blockEvents.removeIf((blockactiondata) -> {
            return structureboundingbox.b((BaseBlockPosition) blockactiondata.a());
        });
    }

    @Override
    public void update(BlockPosition blockposition, Block block) {
        if (!this.isDebugWorld()) {
            this.applyPhysics(blockposition, block);
        }

    }

    @Override
    public float a(EnumDirection enumdirection, boolean flag) {
        return 1.0F;
    }

    public Iterable<Entity> C() {
        return this.getEntities().a();
    }

    public String toString() {
        return "ServerLevel[" + this.serverLevelData.getName() + "]";
    }

    public boolean isFlatWorld() {
        return this.server.getSaveData().getGeneratorSettings().isFlatWorld();
    }

    @Override
    public long getSeed() {
        return this.server.getSaveData().getGeneratorSettings().getSeed();
    }

    @Nullable
    public EnderDragonBattle getDragonBattle() {
        return this.dragonFight;
    }

    @Override
    public Stream<? extends StructureStart<?>> a(SectionPosition sectionposition, StructureGenerator<?> structuregenerator) {
        return this.getStructureManager().a(sectionposition, structuregenerator);
    }

    @Override
    public WorldServer getLevel() {
        return this;
    }

    @VisibleForTesting
    public String H() {
        return String.format("players: %s, entities: %s [%s], block_entities: %d [%s], block_ticks: %d, fluid_ticks: %d, chunk_source: %s", this.players.size(), this.entityManager.e(), a(this.entityManager.d().a(), (entity) -> {
            return IRegistry.ENTITY_TYPE.getKey(entity.getEntityType()).toString();
        }), this.blockEntityTickers.size(), a((Iterable) this.blockEntityTickers, TickingBlockEntity::d), this.getBlockTickList().a(), this.getFluidTickList().a(), this.J());
    }

    private static <T> String a(Iterable<T> iterable, Function<T, String> function) {
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

    public static void a(WorldServer worldserver) {
        BlockPosition blockposition = WorldServer.END_SPAWN_POINT;
        int i = blockposition.getX();
        int j = blockposition.getY() - 2;
        int k = blockposition.getZ();

        BlockPosition.b(i - 2, j + 1, k - 2, i + 2, j + 3, k + 2).forEach((blockposition1) -> {
            worldserver.setTypeUpdate(blockposition1, Blocks.AIR.getBlockData());
        });
        BlockPosition.b(i - 2, j, k - 2, i + 2, j, k + 2).forEach((blockposition1) -> {
            worldserver.setTypeUpdate(blockposition1, Blocks.OBSIDIAN.getBlockData());
        });
    }

    @Override
    public LevelEntityGetter<Entity> getEntities() {
        return this.entityManager.d();
    }

    public void a(Stream<Entity> stream) {
        this.entityManager.a(stream);
    }

    public void b(Stream<Entity> stream) {
        this.entityManager.b(stream);
    }

    @Override
    public void close() throws IOException {
        super.close();
        this.entityManager.close();
    }

    @Override
    public String J() {
        String s = this.chunkSource.getName();

        return "Chunks[S] W: " + s + " E: " + this.entityManager.e();
    }

    public boolean b(long i) {
        return this.entityManager.a(i);
    }

    public boolean e(BlockPosition blockposition) {
        long i = ChunkCoordIntPair.a(blockposition);

        return this.chunkSource.a(i) && this.b(i);
    }

    public boolean f(BlockPosition blockposition) {
        return this.entityManager.a(blockposition);
    }

    public boolean a(ChunkCoordIntPair chunkcoordintpair) {
        return this.entityManager.a(chunkcoordintpair);
    }

    private final class a implements LevelCallback<Entity> {

        a() {}

        public void f(Entity entity) {}

        public void e(Entity entity) {
            WorldServer.this.getScoreboard().a(entity);
        }

        public void d(Entity entity) {
            WorldServer.this.entityTickList.a(entity);
        }

        public void c(Entity entity) {
            WorldServer.this.entityTickList.b(entity);
        }

        public void b(Entity entity) {
            WorldServer.this.getChunkProvider().addEntity(entity);
            if (entity instanceof EntityPlayer) {
                WorldServer.this.players.add((EntityPlayer) entity);
                WorldServer.this.everyoneSleeping();
            }

            if (entity instanceof EntityInsentient) {
                WorldServer.this.navigatingMobs.add((EntityInsentient) entity);
            }

            if (entity instanceof EntityEnderDragon) {
                EntityComplexPart[] aentitycomplexpart = ((EntityEnderDragon) entity).t();
                int i = aentitycomplexpart.length;

                for (int j = 0; j < i; ++j) {
                    EntityComplexPart entitycomplexpart = aentitycomplexpart[j];

                    WorldServer.this.dragonParts.put(entitycomplexpart.getId(), entitycomplexpart);
                }
            }

        }

        public void a(Entity entity) {
            WorldServer.this.getChunkProvider().removeEntity(entity);
            if (entity instanceof EntityPlayer) {
                EntityPlayer entityplayer = (EntityPlayer) entity;

                WorldServer.this.players.remove(entityplayer);
                WorldServer.this.everyoneSleeping();
            }

            if (entity instanceof EntityInsentient) {
                WorldServer.this.navigatingMobs.remove(entity);
            }

            if (entity instanceof EntityEnderDragon) {
                EntityComplexPart[] aentitycomplexpart = ((EntityEnderDragon) entity).t();
                int i = aentitycomplexpart.length;

                for (int j = 0; j < i; ++j) {
                    EntityComplexPart entitycomplexpart = aentitycomplexpart[j];

                    WorldServer.this.dragonParts.remove(entitycomplexpart.getId());
                }
            }

            GameEventListenerRegistrar gameeventlistenerregistrar = entity.bQ();

            if (gameeventlistenerregistrar != null) {
                gameeventlistenerregistrar.a(entity.level);
            }

        }
    }
}
