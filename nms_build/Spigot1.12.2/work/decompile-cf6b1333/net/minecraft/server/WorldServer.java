package net.minecraft.server;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.ListenableFuture;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WorldServer extends World implements IAsyncTaskHandler {

    private static final Logger a = LogManager.getLogger();
    private final MinecraftServer server;
    public EntityTracker tracker;
    private final PlayerChunkMap manager;
    private final Set<NextTickListEntry> nextTickListHash = Sets.newHashSet();
    private final TreeSet<NextTickListEntry> nextTickList = new TreeSet();
    private final Map<UUID, Entity> entitiesByUUID = Maps.newHashMap();
    public boolean savingDisabled;
    private boolean Q;
    private int emptyTime;
    private final PortalTravelAgent portalTravelAgent;
    private final SpawnerCreature spawnerCreature = new SpawnerCreature();
    protected final VillageSiege siegeManager = new VillageSiege(this);
    private final WorldServer.BlockActionDataList[] U = new WorldServer.BlockActionDataList[] { new WorldServer.BlockActionDataList(null), new WorldServer.BlockActionDataList(null)};
    private int V;
    private final List<NextTickListEntry> W = Lists.newArrayList();

    public WorldServer(MinecraftServer minecraftserver, IDataManager idatamanager, WorldData worlddata, int i, MethodProfiler methodprofiler) {
        super(idatamanager, worlddata, DimensionManager.a(i).d(), methodprofiler, false);
        this.server = minecraftserver;
        this.tracker = new EntityTracker(this);
        this.manager = new PlayerChunkMap(this);
        this.worldProvider.a((World) this);
        this.chunkProvider = this.n();
        this.portalTravelAgent = new PortalTravelAgent(this);
        this.J();
        this.K();
        this.getWorldBorder().a(minecraftserver.aE());
    }

    public World b() {
        this.worldMaps = new PersistentCollection(this.dataManager);
        String s = PersistentVillage.a(this.worldProvider);
        PersistentVillage persistentvillage = (PersistentVillage) this.worldMaps.get(PersistentVillage.class, s);

        if (persistentvillage == null) {
            this.villages = new PersistentVillage(this);
            this.worldMaps.a(s, this.villages);
        } else {
            this.villages = persistentvillage;
            this.villages.a((World) this);
        }

        this.scoreboard = new ScoreboardServer(this.server);
        PersistentScoreboard persistentscoreboard = (PersistentScoreboard) this.worldMaps.get(PersistentScoreboard.class, "scoreboard");

        if (persistentscoreboard == null) {
            persistentscoreboard = new PersistentScoreboard();
            this.worldMaps.a("scoreboard", persistentscoreboard);
        }

        persistentscoreboard.a(this.scoreboard);
        ((ScoreboardServer) this.scoreboard).a((Runnable) (new RunnableSaveScoreboard(persistentscoreboard)));
        this.B = new LootTableRegistry(new File(new File(this.dataManager.getDirectory(), "data"), "loot_tables"));
        this.C = new AdvancementDataWorld(new File(new File(this.dataManager.getDirectory(), "data"), "advancements"));
        this.D = new CustomFunctionData(new File(new File(this.dataManager.getDirectory(), "data"), "functions"), this.server);
        this.getWorldBorder().setCenter(this.worldData.B(), this.worldData.C());
        this.getWorldBorder().setDamageAmount(this.worldData.H());
        this.getWorldBorder().setDamageBuffer(this.worldData.G());
        this.getWorldBorder().setWarningDistance(this.worldData.I());
        this.getWorldBorder().setWarningTime(this.worldData.J());
        if (this.worldData.E() > 0L) {
            this.getWorldBorder().transitionSizeBetween(this.worldData.D(), this.worldData.F(), this.worldData.E());
        } else {
            this.getWorldBorder().setSize(this.worldData.D());
        }

        return this;
    }

    public void doTick() {
        super.doTick();
        if (this.getWorldData().isHardcore() && this.getDifficulty() != EnumDifficulty.HARD) {
            this.getWorldData().setDifficulty(EnumDifficulty.HARD);
        }

        this.worldProvider.k().b();
        if (this.everyoneDeeplySleeping()) {
            if (this.getGameRules().getBoolean("doDaylightCycle")) {
                long i = this.worldData.getDayTime() + 24000L;

                this.worldData.setDayTime(i - i % 24000L);
            }

            this.f();
        }

        this.methodProfiler.a("mobSpawner");
        if (this.getGameRules().getBoolean("doMobSpawning") && this.worldData.getType() != WorldType.DEBUG_ALL_BLOCK_STATES) {
            this.spawnerCreature.a(this, this.allowMonsters, this.allowAnimals, this.worldData.getTime() % 400L == 0L);
        }

        this.methodProfiler.c("chunkSource");
        this.chunkProvider.unloadChunks();
        int j = this.a(1.0F);

        if (j != this.ah()) {
            this.c(j);
        }

        this.worldData.setTime(this.worldData.getTime() + 1L);
        if (this.getGameRules().getBoolean("doDaylightCycle")) {
            this.worldData.setDayTime(this.worldData.getDayTime() + 1L);
        }

        this.methodProfiler.c("tickPending");
        this.a(false);
        this.methodProfiler.c("tickBlocks");
        this.j();
        this.methodProfiler.c("chunkMap");
        this.manager.flush();
        this.methodProfiler.c("village");
        this.villages.tick();
        this.siegeManager.a();
        this.methodProfiler.c("portalForcer");
        this.portalTravelAgent.a(this.getTime());
        this.methodProfiler.b();
        this.aq();
    }

    @Nullable
    public BiomeBase.BiomeMeta a(EnumCreatureType enumcreaturetype, BlockPosition blockposition) {
        List list = this.getChunkProviderServer().a(enumcreaturetype, blockposition);

        return list != null && !list.isEmpty() ? (BiomeBase.BiomeMeta) WeightedRandom.a(this.random, list) : null;
    }

    public boolean a(EnumCreatureType enumcreaturetype, BiomeBase.BiomeMeta biomebase_biomemeta, BlockPosition blockposition) {
        List list = this.getChunkProviderServer().a(enumcreaturetype, blockposition);

        return list != null && !list.isEmpty() ? list.contains(biomebase_biomemeta) : false;
    }

    public void everyoneSleeping() {
        this.Q = false;
        if (!this.players.isEmpty()) {
            int i = 0;
            int j = 0;
            Iterator iterator = this.players.iterator();

            while (iterator.hasNext()) {
                EntityHuman entityhuman = (EntityHuman) iterator.next();

                if (entityhuman.isSpectator()) {
                    ++i;
                } else if (entityhuman.isSleeping()) {
                    ++j;
                }
            }

            this.Q = j > 0 && j >= this.players.size() - i;
        }

    }

    protected void f() {
        this.Q = false;
        List list = (List) this.players.stream().filter(EntityHuman::isSleeping).collect(Collectors.toList());
        Iterator iterator = list.iterator();

        while (iterator.hasNext()) {
            EntityHuman entityhuman = (EntityHuman) iterator.next();

            entityhuman.a(false, false, true);
        }

        if (this.getGameRules().getBoolean("doWeatherCycle")) {
            this.c();
        }

    }

    private void c() {
        this.worldData.setWeatherDuration(0);
        this.worldData.setStorm(false);
        this.worldData.setThunderDuration(0);
        this.worldData.setThundering(false);
    }

    public boolean everyoneDeeplySleeping() {
        if (this.Q && !this.isClientSide) {
            Iterator iterator = this.players.iterator();

            EntityHuman entityhuman;

            do {
                if (!iterator.hasNext()) {
                    return true;
                }

                entityhuman = (EntityHuman) iterator.next();
            } while (entityhuman.isSpectator() || entityhuman.isDeeplySleeping());

            return false;
        } else {
            return false;
        }
    }

    protected boolean isChunkLoaded(int i, int j, boolean flag) {
        return this.getChunkProviderServer().isLoaded(i, j);
    }

    protected void i() {
        this.methodProfiler.a("playerCheckLight");
        if (!this.players.isEmpty()) {
            int i = this.random.nextInt(this.players.size());
            EntityHuman entityhuman = (EntityHuman) this.players.get(i);
            int j = MathHelper.floor(entityhuman.locX) + this.random.nextInt(11) - 5;
            int k = MathHelper.floor(entityhuman.locY) + this.random.nextInt(11) - 5;
            int l = MathHelper.floor(entityhuman.locZ) + this.random.nextInt(11) - 5;

            this.w(new BlockPosition(j, k, l));
        }

        this.methodProfiler.b();
    }

    protected void j() {
        this.i();
        if (this.worldData.getType() == WorldType.DEBUG_ALL_BLOCK_STATES) {
            Iterator iterator = this.manager.b();

            while (iterator.hasNext()) {
                ((Chunk) iterator.next()).b(false);
            }

        } else {
            int i = this.getGameRules().c("randomTickSpeed");
            boolean flag = this.isRaining();
            boolean flag1 = this.X();

            this.methodProfiler.a("pollingChunks");

            for (Iterator iterator1 = this.manager.b(); iterator1.hasNext(); this.methodProfiler.b()) {
                this.methodProfiler.a("getChunk");
                Chunk chunk = (Chunk) iterator1.next();
                int j = chunk.locX * 16;
                int k = chunk.locZ * 16;

                this.methodProfiler.c("checkNextLight");
                chunk.n();
                this.methodProfiler.c("tickChunk");
                chunk.b(false);
                this.methodProfiler.c("thunder");
                int l;
                BlockPosition blockposition;

                if (flag && flag1 && this.random.nextInt(100000) == 0) {
                    this.l = this.l * 3 + 1013904223;
                    l = this.l >> 2;
                    blockposition = this.a(new BlockPosition(j + (l & 15), 0, k + (l >> 8 & 15)));
                    if (this.isRainingAt(blockposition)) {
                        DifficultyDamageScaler difficultydamagescaler = this.D(blockposition);

                        if (this.getGameRules().getBoolean("doMobSpawning") && this.random.nextDouble() < (double) difficultydamagescaler.b() * 0.01D) {
                            EntityHorseSkeleton entityhorseskeleton = new EntityHorseSkeleton(this);

                            entityhorseskeleton.p(true);
                            entityhorseskeleton.setAgeRaw(0);
                            entityhorseskeleton.setPosition((double) blockposition.getX(), (double) blockposition.getY(), (double) blockposition.getZ());
                            this.addEntity(entityhorseskeleton);
                            this.strikeLightning(new EntityLightning(this, (double) blockposition.getX(), (double) blockposition.getY(), (double) blockposition.getZ(), true));
                        } else {
                            this.strikeLightning(new EntityLightning(this, (double) blockposition.getX(), (double) blockposition.getY(), (double) blockposition.getZ(), false));
                        }
                    }
                }

                this.methodProfiler.c("iceandsnow");
                if (this.random.nextInt(16) == 0) {
                    this.l = this.l * 3 + 1013904223;
                    l = this.l >> 2;
                    blockposition = this.p(new BlockPosition(j + (l & 15), 0, k + (l >> 8 & 15)));
                    BlockPosition blockposition1 = blockposition.down();

                    if (this.v(blockposition1)) {
                        this.setTypeUpdate(blockposition1, Blocks.ICE.getBlockData());
                    }

                    if (flag && this.f(blockposition, true)) {
                        this.setTypeUpdate(blockposition, Blocks.SNOW_LAYER.getBlockData());
                    }

                    if (flag && this.getBiome(blockposition1).d()) {
                        this.getType(blockposition1).getBlock().h(this, blockposition1);
                    }
                }

                this.methodProfiler.c("tickBlocks");
                if (i > 0) {
                    ChunkSection[] achunksection = chunk.getSections();
                    int i1 = achunksection.length;

                    for (int j1 = 0; j1 < i1; ++j1) {
                        ChunkSection chunksection = achunksection[j1];

                        if (chunksection != Chunk.a && chunksection.shouldTick()) {
                            for (int k1 = 0; k1 < i; ++k1) {
                                this.l = this.l * 3 + 1013904223;
                                int l1 = this.l >> 2;
                                int i2 = l1 & 15;
                                int j2 = l1 >> 8 & 15;
                                int k2 = l1 >> 16 & 15;
                                IBlockData iblockdata = chunksection.getType(i2, k2, j2);
                                Block block = iblockdata.getBlock();

                                this.methodProfiler.a("randomTick");
                                if (block.isTicking()) {
                                    block.a((World) this, new BlockPosition(i2 + j, k2 + chunksection.getYPosition(), j2 + k), iblockdata, this.random);
                                }

                                this.methodProfiler.b();
                            }
                        }
                    }
                }
            }

            this.methodProfiler.b();
        }
    }

    protected BlockPosition a(BlockPosition blockposition) {
        BlockPosition blockposition1 = this.p(blockposition);
        AxisAlignedBB axisalignedbb = (new AxisAlignedBB(blockposition1, new BlockPosition(blockposition1.getX(), this.getHeight(), blockposition1.getZ()))).g(3.0D);
        List list = this.a(EntityLiving.class, axisalignedbb, new com.google.common.base.Predicate() {
            public boolean a(@Nullable EntityLiving entityliving) {
                return entityliving != null && entityliving.isAlive() && WorldServer.this.h(entityliving.getChunkCoordinates());
            }

            public boolean apply(@Nullable Object object) {
                return this.a((EntityLiving) object);
            }
        });

        if (!list.isEmpty()) {
            return ((EntityLiving) list.get(this.random.nextInt(list.size()))).getChunkCoordinates();
        } else {
            if (blockposition1.getY() == -1) {
                blockposition1 = blockposition1.up(2);
            }

            return blockposition1;
        }
    }

    public boolean a(BlockPosition blockposition, Block block) {
        NextTickListEntry nextticklistentry = new NextTickListEntry(blockposition, block);

        return this.W.contains(nextticklistentry);
    }

    public boolean b(BlockPosition blockposition, Block block) {
        NextTickListEntry nextticklistentry = new NextTickListEntry(blockposition, block);

        return this.nextTickListHash.contains(nextticklistentry);
    }

    public void a(BlockPosition blockposition, Block block, int i) {
        this.a(blockposition, block, i, 0);
    }

    public void a(BlockPosition blockposition, Block block, int i, int j) {
        Material material = block.getBlockData().getMaterial();

        if (this.d && material != Material.AIR) {
            if (block.r()) {
                if (this.areChunksLoadedBetween(blockposition.a(-8, -8, -8), blockposition.a(8, 8, 8))) {
                    IBlockData iblockdata = this.getType(blockposition);

                    if (iblockdata.getMaterial() != Material.AIR && iblockdata.getBlock() == block) {
                        iblockdata.getBlock().b((World) this, blockposition, iblockdata, this.random);
                    }
                }

                return;
            }

            i = 1;
        }

        NextTickListEntry nextticklistentry = new NextTickListEntry(blockposition, block);

        if (this.isLoaded(blockposition)) {
            if (material != Material.AIR) {
                nextticklistentry.a((long) i + this.worldData.getTime());
                nextticklistentry.a(j);
            }

            if (!this.nextTickListHash.contains(nextticklistentry)) {
                this.nextTickListHash.add(nextticklistentry);
                this.nextTickList.add(nextticklistentry);
            }
        }

    }

    public void b(BlockPosition blockposition, Block block, int i, int j) {
        NextTickListEntry nextticklistentry = new NextTickListEntry(blockposition, block);

        nextticklistentry.a(j);
        Material material = block.getBlockData().getMaterial();

        if (material != Material.AIR) {
            nextticklistentry.a((long) i + this.worldData.getTime());
        }

        if (!this.nextTickListHash.contains(nextticklistentry)) {
            this.nextTickListHash.add(nextticklistentry);
            this.nextTickList.add(nextticklistentry);
        }

    }

    public void tickEntities() {
        if (this.players.isEmpty()) {
            if (this.emptyTime++ >= 300) {
                return;
            }
        } else {
            this.m();
        }

        this.worldProvider.s();
        super.tickEntities();
    }

    protected void l() {
        super.l();
        this.methodProfiler.c("players");

        for (int i = 0; i < this.players.size(); ++i) {
            Entity entity = (Entity) this.players.get(i);
            Entity entity1 = entity.bJ();

            if (entity1 != null) {
                if (!entity1.dead && entity1.w(entity)) {
                    continue;
                }

                entity.stopRiding();
            }

            this.methodProfiler.a("tick");
            if (!entity.dead) {
                try {
                    this.h(entity);
                } catch (Throwable throwable) {
                    CrashReport crashreport = CrashReport.a(throwable, "Ticking player");
                    CrashReportSystemDetails crashreportsystemdetails = crashreport.a("Player being ticked");

                    entity.appendEntityCrashDetails(crashreportsystemdetails);
                    throw new ReportedException(crashreport);
                }
            }

            this.methodProfiler.b();
            this.methodProfiler.a("remove");
            if (entity.dead) {
                int j = entity.ab;
                int k = entity.ad;

                if (entity.aa && this.isChunkLoaded(j, k, true)) {
                    this.getChunkAt(j, k).b(entity);
                }

                this.entityList.remove(entity);
                this.c(entity);
            }

            this.methodProfiler.b();
        }

    }

    public void m() {
        this.emptyTime = 0;
    }

    public boolean a(boolean flag) {
        if (this.worldData.getType() == WorldType.DEBUG_ALL_BLOCK_STATES) {
            return false;
        } else {
            int i = this.nextTickList.size();

            if (i != this.nextTickListHash.size()) {
                throw new IllegalStateException("TickNextTick list out of synch");
            } else {
                if (i > 65536) {
                    i = 65536;
                }

                this.methodProfiler.a("cleaning");

                NextTickListEntry nextticklistentry;

                for (int j = 0; j < i; ++j) {
                    nextticklistentry = (NextTickListEntry) this.nextTickList.first();
                    if (!flag && nextticklistentry.b > this.worldData.getTime()) {
                        break;
                    }

                    this.nextTickList.remove(nextticklistentry);
                    this.nextTickListHash.remove(nextticklistentry);
                    this.W.add(nextticklistentry);
                }

                this.methodProfiler.b();
                this.methodProfiler.a("ticking");
                Iterator iterator = this.W.iterator();

                while (iterator.hasNext()) {
                    nextticklistentry = (NextTickListEntry) iterator.next();
                    iterator.remove();
                    boolean flag1 = false;

                    if (this.areChunksLoadedBetween(nextticklistentry.a.a(0, 0, 0), nextticklistentry.a.a(0, 0, 0))) {
                        IBlockData iblockdata = this.getType(nextticklistentry.a);

                        if (iblockdata.getMaterial() != Material.AIR && Block.a(iblockdata.getBlock(), nextticklistentry.a())) {
                            try {
                                iblockdata.getBlock().b((World) this, nextticklistentry.a, iblockdata, this.random);
                            } catch (Throwable throwable) {
                                CrashReport crashreport = CrashReport.a(throwable, "Exception while ticking a block");
                                CrashReportSystemDetails crashreportsystemdetails = crashreport.a("Block being ticked");

                                CrashReportSystemDetails.a(crashreportsystemdetails, nextticklistentry.a, iblockdata);
                                throw new ReportedException(crashreport);
                            }
                        }
                    } else {
                        this.a(nextticklistentry.a, nextticklistentry.a(), 0);
                    }
                }

                this.methodProfiler.b();
                this.W.clear();
                return !this.nextTickList.isEmpty();
            }
        }
    }

    @Nullable
    public List<NextTickListEntry> a(Chunk chunk, boolean flag) {
        ChunkCoordIntPair chunkcoordintpair = chunk.k();
        int i = (chunkcoordintpair.x << 4) - 2;
        int j = i + 16 + 2;
        int k = (chunkcoordintpair.z << 4) - 2;
        int l = k + 16 + 2;

        return this.a(new StructureBoundingBox(i, 0, k, j, 256, l), flag);
    }

    @Nullable
    public List<NextTickListEntry> a(StructureBoundingBox structureboundingbox, boolean flag) {
        ArrayList arraylist = null;

        for (int i = 0; i < 2; ++i) {
            Iterator iterator;

            if (i == 0) {
                iterator = this.nextTickList.iterator();
            } else {
                iterator = this.W.iterator();
            }

            while (iterator.hasNext()) {
                NextTickListEntry nextticklistentry = (NextTickListEntry) iterator.next();
                BlockPosition blockposition = nextticklistentry.a;

                if (blockposition.getX() >= structureboundingbox.a && blockposition.getX() < structureboundingbox.d && blockposition.getZ() >= structureboundingbox.c && blockposition.getZ() < structureboundingbox.f) {
                    if (flag) {
                        if (i == 0) {
                            this.nextTickListHash.remove(nextticklistentry);
                        }

                        iterator.remove();
                    }

                    if (arraylist == null) {
                        arraylist = Lists.newArrayList();
                    }

                    arraylist.add(nextticklistentry);
                }
            }
        }

        return arraylist;
    }

    public void entityJoinedWorld(Entity entity, boolean flag) {
        if (!this.getSpawnAnimals() && (entity instanceof EntityAnimal || entity instanceof EntityWaterAnimal)) {
            entity.die();
        }

        if (!this.getSpawnNPCs() && entity instanceof NPC) {
            entity.die();
        }

        super.entityJoinedWorld(entity, flag);
    }

    private boolean getSpawnNPCs() {
        return this.server.getSpawnNPCs();
    }

    private boolean getSpawnAnimals() {
        return this.server.getSpawnAnimals();
    }

    protected IChunkProvider n() {
        IChunkLoader ichunkloader = this.dataManager.createChunkLoader(this.worldProvider);

        return new ChunkProviderServer(this, ichunkloader, this.worldProvider.getChunkGenerator());
    }

    public boolean a(EntityHuman entityhuman, BlockPosition blockposition) {
        return !this.server.a(this, blockposition, entityhuman) && this.getWorldBorder().a(blockposition);
    }

    public void a(WorldSettings worldsettings) {
        if (!this.worldData.v()) {
            try {
                this.b(worldsettings);
                if (this.worldData.getType() == WorldType.DEBUG_ALL_BLOCK_STATES) {
                    this.ap();
                }

                super.a(worldsettings);
            } catch (Throwable throwable) {
                CrashReport crashreport = CrashReport.a(throwable, "Exception initializing level");

                try {
                    this.a(crashreport);
                } catch (Throwable throwable1) {
                    ;
                }

                throw new ReportedException(crashreport);
            }

            this.worldData.d(true);
        }

    }

    private void ap() {
        this.worldData.f(false);
        this.worldData.c(true);
        this.worldData.setStorm(false);
        this.worldData.setThundering(false);
        this.worldData.i(1000000000);
        this.worldData.setDayTime(6000L);
        this.worldData.setGameType(EnumGamemode.SPECTATOR);
        this.worldData.g(false);
        this.worldData.setDifficulty(EnumDifficulty.PEACEFUL);
        this.worldData.e(true);
        this.getGameRules().set("doDaylightCycle", "false");
    }

    private void b(WorldSettings worldsettings) {
        if (!this.worldProvider.e()) {
            this.worldData.setSpawn(BlockPosition.ZERO.up(this.worldProvider.getSeaLevel()));
        } else if (this.worldData.getType() == WorldType.DEBUG_ALL_BLOCK_STATES) {
            this.worldData.setSpawn(BlockPosition.ZERO.up());
        } else {
            this.isLoading = true;
            WorldChunkManager worldchunkmanager = this.worldProvider.k();
            List list = worldchunkmanager.a();
            Random random = new Random(this.getSeed());
            BlockPosition blockposition = worldchunkmanager.a(0, 0, 256, list, random);
            int i = 8;
            int j = this.worldProvider.getSeaLevel();
            int k = 8;

            if (blockposition != null) {
                i = blockposition.getX();
                k = blockposition.getZ();
            } else {
                WorldServer.a.warn("Unable to find spawn biome");
            }

            int l = 0;

            while (!this.worldProvider.canSpawn(i, k)) {
                i += random.nextInt(64) - random.nextInt(64);
                k += random.nextInt(64) - random.nextInt(64);
                ++l;
                if (l == 1000) {
                    break;
                }
            }

            this.worldData.setSpawn(new BlockPosition(i, j, k));
            this.isLoading = false;
            if (worldsettings.c()) {
                this.o();
            }

        }
    }

    protected void o() {
        WorldGenBonusChest worldgenbonuschest = new WorldGenBonusChest();

        for (int i = 0; i < 10; ++i) {
            int j = this.worldData.b() + this.random.nextInt(6) - this.random.nextInt(6);
            int k = this.worldData.d() + this.random.nextInt(6) - this.random.nextInt(6);
            BlockPosition blockposition = this.q(new BlockPosition(j, 0, k)).up();

            if (worldgenbonuschest.generate(this, this.random, blockposition)) {
                break;
            }
        }

    }

    @Nullable
    public BlockPosition getDimensionSpawn() {
        return this.worldProvider.h();
    }

    public void save(boolean flag, @Nullable IProgressUpdate iprogressupdate) throws ExceptionWorldConflict {
        ChunkProviderServer chunkproviderserver = this.getChunkProviderServer();

        if (chunkproviderserver.e()) {
            if (iprogressupdate != null) {
                iprogressupdate.a("Saving level");
            }

            this.a();
            if (iprogressupdate != null) {
                iprogressupdate.c("Saving chunks");
            }

            chunkproviderserver.a(flag);
            ArrayList arraylist = Lists.newArrayList(chunkproviderserver.a());
            Iterator iterator = arraylist.iterator();

            while (iterator.hasNext()) {
                Chunk chunk = (Chunk) iterator.next();

                if (chunk != null && !this.manager.a(chunk.locX, chunk.locZ)) {
                    chunkproviderserver.unload(chunk);
                }
            }

        }
    }

    public void flushSave() {
        ChunkProviderServer chunkproviderserver = this.getChunkProviderServer();

        if (chunkproviderserver.e()) {
            chunkproviderserver.c();
        }
    }

    protected void a() throws ExceptionWorldConflict {
        this.checkSession();
        WorldServer[] aworldserver = this.server.worldServer;
        int i = aworldserver.length;

        for (int j = 0; j < i; ++j) {
            WorldServer worldserver = aworldserver[j];

            if (worldserver instanceof SecondaryWorldServer) {
                ((SecondaryWorldServer) worldserver).c();
            }
        }

        this.worldData.a(this.getWorldBorder().getSize());
        this.worldData.d(this.getWorldBorder().getCenterX());
        this.worldData.c(this.getWorldBorder().getCenterZ());
        this.worldData.e(this.getWorldBorder().getDamageBuffer());
        this.worldData.f(this.getWorldBorder().getDamageAmount());
        this.worldData.j(this.getWorldBorder().getWarningDistance());
        this.worldData.k(this.getWorldBorder().getWarningTime());
        this.worldData.b(this.getWorldBorder().j());
        this.worldData.e(this.getWorldBorder().i());
        this.dataManager.saveWorldData(this.worldData, this.server.getPlayerList().t());
        this.worldMaps.a();
    }

    public boolean addEntity(Entity entity) {
        return this.j(entity) ? super.addEntity(entity) : false;
    }

    public void a(Collection<Entity> collection) {
        ArrayList arraylist = Lists.newArrayList(collection);
        Iterator iterator = arraylist.iterator();

        while (iterator.hasNext()) {
            Entity entity = (Entity) iterator.next();

            if (this.j(entity)) {
                this.entityList.add(entity);
                this.b(entity);
            }
        }

    }

    private boolean j(Entity entity) {
        if (entity.dead) {
            WorldServer.a.warn("Tried to add entity {} but it was marked as removed already", EntityTypes.a(entity));
            return false;
        } else {
            UUID uuid = entity.getUniqueID();

            if (this.entitiesByUUID.containsKey(uuid)) {
                Entity entity1 = (Entity) this.entitiesByUUID.get(uuid);

                if (this.f.contains(entity1)) {
                    this.f.remove(entity1);
                } else {
                    if (!(entity instanceof EntityHuman)) {
                        WorldServer.a.warn("Keeping entity {} that already exists with UUID {}", EntityTypes.a(entity1), uuid.toString());
                        return false;
                    }

                    WorldServer.a.warn("Force-added player with duplicate UUID {}", uuid.toString());
                }

                this.removeEntity(entity1);
            }

            return true;
        }
    }

    protected void b(Entity entity) {
        super.b(entity);
        this.entitiesById.a(entity.getId(), entity);
        this.entitiesByUUID.put(entity.getUniqueID(), entity);
        Entity[] aentity = entity.bb();

        if (aentity != null) {
            Entity[] aentity1 = aentity;
            int i = aentity.length;

            for (int j = 0; j < i; ++j) {
                Entity entity1 = aentity1[j];

                this.entitiesById.a(entity1.getId(), entity1);
            }
        }

    }

    protected void c(Entity entity) {
        super.c(entity);
        this.entitiesById.d(entity.getId());
        this.entitiesByUUID.remove(entity.getUniqueID());
        Entity[] aentity = entity.bb();

        if (aentity != null) {
            Entity[] aentity1 = aentity;
            int i = aentity.length;

            for (int j = 0; j < i; ++j) {
                Entity entity1 = aentity1[j];

                this.entitiesById.d(entity1.getId());
            }
        }

    }

    public boolean strikeLightning(Entity entity) {
        if (super.strikeLightning(entity)) {
            this.server.getPlayerList().sendPacketNearby((EntityHuman) null, entity.locX, entity.locY, entity.locZ, 512.0D, this.worldProvider.getDimensionManager().getDimensionID(), new PacketPlayOutSpawnEntityWeather(entity));
            return true;
        } else {
            return false;
        }
    }

    public void broadcastEntityEffect(Entity entity, byte b0) {
        this.getTracker().sendPacketToEntity(entity, new PacketPlayOutEntityStatus(entity, b0));
    }

    public ChunkProviderServer getChunkProviderServer() {
        return (ChunkProviderServer) super.getChunkProvider();
    }

    public Explosion createExplosion(@Nullable Entity entity, double d0, double d1, double d2, float f, boolean flag, boolean flag1) {
        Explosion explosion = new Explosion(this, entity, d0, d1, d2, f, flag, flag1);

        explosion.a();
        explosion.a(false);
        if (!flag1) {
            explosion.clearBlocks();
        }

        Iterator iterator = this.players.iterator();

        while (iterator.hasNext()) {
            EntityHuman entityhuman = (EntityHuman) iterator.next();

            if (entityhuman.d(d0, d1, d2) < 4096.0D) {
                ((EntityPlayer) entityhuman).playerConnection.sendPacket(new PacketPlayOutExplosion(d0, d1, d2, f, explosion.getBlocks(), (Vec3D) explosion.b().get(entityhuman)));
            }
        }

        return explosion;
    }

    public void playBlockAction(BlockPosition blockposition, Block block, int i, int j) {
        BlockActionData blockactiondata = new BlockActionData(blockposition, block, i, j);
        Iterator iterator = this.U[this.V].iterator();

        BlockActionData blockactiondata1;

        do {
            if (!iterator.hasNext()) {
                this.U[this.V].add(blockactiondata);
                return;
            }

            blockactiondata1 = (BlockActionData) iterator.next();
        } while (!blockactiondata1.equals(blockactiondata));

    }

    private void aq() {
        while (!this.U[this.V].isEmpty()) {
            int i = this.V;

            this.V ^= 1;
            Iterator iterator = this.U[i].iterator();

            while (iterator.hasNext()) {
                BlockActionData blockactiondata = (BlockActionData) iterator.next();

                if (this.a(blockactiondata)) {
                    this.server.getPlayerList().sendPacketNearby((EntityHuman) null, (double) blockactiondata.a().getX(), (double) blockactiondata.a().getY(), (double) blockactiondata.a().getZ(), 64.0D, this.worldProvider.getDimensionManager().getDimensionID(), new PacketPlayOutBlockAction(blockactiondata.a(), blockactiondata.d(), blockactiondata.b(), blockactiondata.c()));
                }
            }

            this.U[i].clear();
        }

    }

    private boolean a(BlockActionData blockactiondata) {
        IBlockData iblockdata = this.getType(blockactiondata.a());

        return iblockdata.getBlock() == blockactiondata.d() ? iblockdata.a(this, blockactiondata.a(), blockactiondata.b(), blockactiondata.c()) : false;
    }

    public void saveLevel() {
        this.dataManager.a();
    }

    protected void t() {
        boolean flag = this.isRaining();

        super.t();
        if (this.n != this.o) {
            this.server.getPlayerList().a((Packet) (new PacketPlayOutGameStateChange(7, this.o)), this.worldProvider.getDimensionManager().getDimensionID());
        }

        if (this.p != this.q) {
            this.server.getPlayerList().a((Packet) (new PacketPlayOutGameStateChange(8, this.q)), this.worldProvider.getDimensionManager().getDimensionID());
        }

        if (flag != this.isRaining()) {
            if (flag) {
                this.server.getPlayerList().sendAll(new PacketPlayOutGameStateChange(2, 0.0F));
            } else {
                this.server.getPlayerList().sendAll(new PacketPlayOutGameStateChange(1, 0.0F));
            }

            this.server.getPlayerList().sendAll(new PacketPlayOutGameStateChange(7, this.o));
            this.server.getPlayerList().sendAll(new PacketPlayOutGameStateChange(8, this.q));
        }

    }

    @Nullable
    public MinecraftServer getMinecraftServer() {
        return this.server;
    }

    public EntityTracker getTracker() {
        return this.tracker;
    }

    public PlayerChunkMap getPlayerChunkMap() {
        return this.manager;
    }

    public PortalTravelAgent getTravelAgent() {
        return this.portalTravelAgent;
    }

    public DefinedStructureManager y() {
        return this.dataManager.h();
    }

    public void a(EnumParticle enumparticle, double d0, double d1, double d2, int i, double d3, double d4, double d5, double d6, int... aint) {
        this.a(enumparticle, false, d0, d1, d2, i, d3, d4, d5, d6, aint);
    }

    public void a(EnumParticle enumparticle, boolean flag, double d0, double d1, double d2, int i, double d3, double d4, double d5, double d6, int... aint) {
        PacketPlayOutWorldParticles packetplayoutworldparticles = new PacketPlayOutWorldParticles(enumparticle, flag, (float) d0, (float) d1, (float) d2, (float) d3, (float) d4, (float) d5, (float) d6, i, aint);

        for (int j = 0; j < this.players.size(); ++j) {
            EntityPlayer entityplayer = (EntityPlayer) this.players.get(j);

            this.a(entityplayer, flag, d0, d1, d2, packetplayoutworldparticles);
        }

    }

    public void a(EntityPlayer entityplayer, EnumParticle enumparticle, boolean flag, double d0, double d1, double d2, int i, double d3, double d4, double d5, double d6, int... aint) {
        PacketPlayOutWorldParticles packetplayoutworldparticles = new PacketPlayOutWorldParticles(enumparticle, flag, (float) d0, (float) d1, (float) d2, (float) d3, (float) d4, (float) d5, (float) d6, i, aint);

        this.a(entityplayer, flag, d0, d1, d2, packetplayoutworldparticles);
    }

    private void a(EntityPlayer entityplayer, boolean flag, double d0, double d1, double d2, Packet<?> packet) {
        BlockPosition blockposition = entityplayer.getChunkCoordinates();
        double d3 = blockposition.distanceSquared(d0, d1, d2);

        if (d3 <= 1024.0D || flag && d3 <= 262144.0D) {
            entityplayer.playerConnection.sendPacket(packet);
        }

    }

    @Nullable
    public Entity getEntity(UUID uuid) {
        return (Entity) this.entitiesByUUID.get(uuid);
    }

    public ListenableFuture<Object> postToMainThread(Runnable runnable) {
        return this.server.postToMainThread(runnable);
    }

    public boolean isMainThread() {
        return this.server.isMainThread();
    }

    @Nullable
    public BlockPosition a(String s, BlockPosition blockposition, boolean flag) {
        return this.getChunkProviderServer().a(this, s, blockposition, flag);
    }

    public AdvancementDataWorld z() {
        return this.C;
    }

    public CustomFunctionData A() {
        return this.D;
    }

    public IChunkProvider getChunkProvider() {
        return this.getChunkProviderServer();
    }

    static class BlockActionDataList extends ArrayList<BlockActionData> {

        private BlockActionDataList() {}

        BlockActionDataList(Object object) {
            this();
        }
    }
}
