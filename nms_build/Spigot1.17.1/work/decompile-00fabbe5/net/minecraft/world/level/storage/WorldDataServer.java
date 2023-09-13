package net.minecraft.world.level.storage;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.mojang.datafixers.DataFixer;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.Lifecycle;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.CrashReportSystemDetails;
import net.minecraft.SharedConstants;
import net.minecraft.SystemUtils;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.IRegistryCustom;
import net.minecraft.core.MinecraftSerializableUUID;
import net.minecraft.nbt.DynamicOpsNBT;
import net.minecraft.nbt.GameProfileSerializer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.resources.RegistryWriteOps;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.level.DataPackConfiguration;
import net.minecraft.world.level.EnumGamemode;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.WorldSettings;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.levelgen.GeneratorSettings;
import net.minecraft.world.level.timers.CustomFunctionCallbackTimerQueue;
import net.minecraft.world.level.timers.CustomFunctionCallbackTimers;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WorldDataServer implements IWorldDataServer, SaveData {

    private static final Logger LOGGER = LogManager.getLogger();
    protected static final String WORLD_GEN_SETTINGS = "WorldGenSettings";
    public WorldSettings settings;
    private final GeneratorSettings worldGenSettings;
    private final Lifecycle worldGenSettingsLifecycle;
    private int xSpawn;
    private int ySpawn;
    private int zSpawn;
    private float spawnAngle;
    private long gameTime;
    private long dayTime;
    @Nullable
    private final DataFixer fixerUpper;
    private final int playerDataVersion;
    private boolean upgradedPlayerTag;
    @Nullable
    private NBTTagCompound loadedPlayerTag;
    private final int version;
    private int clearWeatherTime;
    private boolean raining;
    private int rainTime;
    private boolean thundering;
    private int thunderTime;
    private boolean initialized;
    private boolean difficultyLocked;
    private WorldBorder.c worldBorder;
    private NBTTagCompound endDragonFightData;
    @Nullable
    private NBTTagCompound customBossEvents;
    private int wanderingTraderSpawnDelay;
    private int wanderingTraderSpawnChance;
    @Nullable
    private UUID wanderingTraderId;
    private final Set<String> knownServerBrands;
    private boolean wasModded;
    private final CustomFunctionCallbackTimerQueue<MinecraftServer> scheduledEvents;

    private WorldDataServer(@Nullable DataFixer datafixer, int i, @Nullable NBTTagCompound nbttagcompound, boolean flag, int j, int k, int l, float f, long i1, long j1, int k1, int l1, int i2, boolean flag1, int j2, boolean flag2, boolean flag3, boolean flag4, WorldBorder.c worldborder_c, int k2, int l2, @Nullable UUID uuid, Set<String> set, CustomFunctionCallbackTimerQueue<MinecraftServer> customfunctioncallbacktimerqueue, @Nullable NBTTagCompound nbttagcompound1, NBTTagCompound nbttagcompound2, WorldSettings worldsettings, GeneratorSettings generatorsettings, Lifecycle lifecycle) {
        this.fixerUpper = datafixer;
        this.wasModded = flag;
        this.xSpawn = j;
        this.ySpawn = k;
        this.zSpawn = l;
        this.spawnAngle = f;
        this.gameTime = i1;
        this.dayTime = j1;
        this.version = k1;
        this.clearWeatherTime = l1;
        this.rainTime = i2;
        this.raining = flag1;
        this.thunderTime = j2;
        this.thundering = flag2;
        this.initialized = flag3;
        this.difficultyLocked = flag4;
        this.worldBorder = worldborder_c;
        this.wanderingTraderSpawnDelay = k2;
        this.wanderingTraderSpawnChance = l2;
        this.wanderingTraderId = uuid;
        this.knownServerBrands = set;
        this.loadedPlayerTag = nbttagcompound;
        this.playerDataVersion = i;
        this.scheduledEvents = customfunctioncallbacktimerqueue;
        this.customBossEvents = nbttagcompound1;
        this.endDragonFightData = nbttagcompound2;
        this.settings = worldsettings;
        this.worldGenSettings = generatorsettings;
        this.worldGenSettingsLifecycle = lifecycle;
    }

    public WorldDataServer(WorldSettings worldsettings, GeneratorSettings generatorsettings, Lifecycle lifecycle) {
        this((DataFixer) null, SharedConstants.getGameVersion().getWorldVersion(), (NBTTagCompound) null, false, 0, 0, 0, 0.0F, 0L, 0L, 19133, 0, 0, false, 0, false, false, false, WorldBorder.DEFAULT_SETTINGS, 0, 0, (UUID) null, Sets.newLinkedHashSet(), new CustomFunctionCallbackTimerQueue<>(CustomFunctionCallbackTimers.SERVER_CALLBACKS), (NBTTagCompound) null, new NBTTagCompound(), worldsettings.h(), generatorsettings, lifecycle);
    }

    public static WorldDataServer a(Dynamic<NBTBase> dynamic, DataFixer datafixer, int i, @Nullable NBTTagCompound nbttagcompound, WorldSettings worldsettings, LevelVersion levelversion, GeneratorSettings generatorsettings, Lifecycle lifecycle) {
        long j = dynamic.get("Time").asLong(0L);
        NBTTagCompound nbttagcompound1 = (NBTTagCompound) dynamic.get("DragonFight").result().map(Dynamic::getValue).orElseGet(() -> {
            return (NBTBase) dynamic.get("DimensionData").get("1").get("DragonFight").orElseEmptyMap().getValue();
        });

        return new WorldDataServer(datafixer, i, nbttagcompound, dynamic.get("WasModded").asBoolean(false), dynamic.get("SpawnX").asInt(0), dynamic.get("SpawnY").asInt(0), dynamic.get("SpawnZ").asInt(0), dynamic.get("SpawnAngle").asFloat(0.0F), j, dynamic.get("DayTime").asLong(j), levelversion.a(), dynamic.get("clearWeatherTime").asInt(0), dynamic.get("rainTime").asInt(0), dynamic.get("raining").asBoolean(false), dynamic.get("thunderTime").asInt(0), dynamic.get("thundering").asBoolean(false), dynamic.get("initialized").asBoolean(true), dynamic.get("DifficultyLocked").asBoolean(false), WorldBorder.c.a(dynamic, WorldBorder.DEFAULT_SETTINGS), dynamic.get("WanderingTraderSpawnDelay").asInt(0), dynamic.get("WanderingTraderSpawnChance").asInt(0), (UUID) dynamic.get("WanderingTraderId").read(MinecraftSerializableUUID.CODEC).result().orElse((Object) null), (Set) dynamic.get("ServerBrands").asStream().flatMap((dynamic1) -> {
            return SystemUtils.a(dynamic1.asString().result());
        }).collect(Collectors.toCollection(Sets::newLinkedHashSet)), new CustomFunctionCallbackTimerQueue<>(CustomFunctionCallbackTimers.SERVER_CALLBACKS, dynamic.get("ScheduledEvents").asStream()), (NBTTagCompound) dynamic.get("CustomBossEvents").orElseEmptyMap().getValue(), nbttagcompound1, worldsettings, generatorsettings, lifecycle);
    }

    @Override
    public NBTTagCompound a(IRegistryCustom iregistrycustom, @Nullable NBTTagCompound nbttagcompound) {
        this.J();
        if (nbttagcompound == null) {
            nbttagcompound = this.loadedPlayerTag;
        }

        NBTTagCompound nbttagcompound1 = new NBTTagCompound();

        this.a(iregistrycustom, nbttagcompound1, nbttagcompound);
        return nbttagcompound1;
    }

    private void a(IRegistryCustom iregistrycustom, NBTTagCompound nbttagcompound, @Nullable NBTTagCompound nbttagcompound1) {
        NBTTagList nbttaglist = new NBTTagList();
        Stream stream = this.knownServerBrands.stream().map(NBTTagString::a);

        Objects.requireNonNull(nbttaglist);
        stream.forEach(nbttaglist::add);
        nbttagcompound.set("ServerBrands", nbttaglist);
        nbttagcompound.setBoolean("WasModded", this.wasModded);
        NBTTagCompound nbttagcompound2 = new NBTTagCompound();

        nbttagcompound2.setString("Name", SharedConstants.getGameVersion().getName());
        nbttagcompound2.setInt("Id", SharedConstants.getGameVersion().getWorldVersion());
        nbttagcompound2.setBoolean("Snapshot", !SharedConstants.getGameVersion().isStable());
        nbttagcompound.set("Version", nbttagcompound2);
        nbttagcompound.setInt("DataVersion", SharedConstants.getGameVersion().getWorldVersion());
        RegistryWriteOps<NBTBase> registrywriteops = RegistryWriteOps.a(DynamicOpsNBT.INSTANCE, iregistrycustom);
        DataResult dataresult = GeneratorSettings.CODEC.encodeStart(registrywriteops, this.worldGenSettings);
        Logger logger = WorldDataServer.LOGGER;

        Objects.requireNonNull(logger);
        dataresult.resultOrPartial(SystemUtils.a("WorldGenSettings: ", logger::error)).ifPresent((nbtbase) -> {
            nbttagcompound.set("WorldGenSettings", nbtbase);
        });
        nbttagcompound.setInt("GameType", this.settings.getGameType().getId());
        nbttagcompound.setInt("SpawnX", this.xSpawn);
        nbttagcompound.setInt("SpawnY", this.ySpawn);
        nbttagcompound.setInt("SpawnZ", this.zSpawn);
        nbttagcompound.setFloat("SpawnAngle", this.spawnAngle);
        nbttagcompound.setLong("Time", this.gameTime);
        nbttagcompound.setLong("DayTime", this.dayTime);
        nbttagcompound.setLong("LastPlayed", SystemUtils.getTimeMillis());
        nbttagcompound.setString("LevelName", this.settings.getLevelName());
        nbttagcompound.setInt("version", 19133);
        nbttagcompound.setInt("clearWeatherTime", this.clearWeatherTime);
        nbttagcompound.setInt("rainTime", this.rainTime);
        nbttagcompound.setBoolean("raining", this.raining);
        nbttagcompound.setInt("thunderTime", this.thunderTime);
        nbttagcompound.setBoolean("thundering", this.thundering);
        nbttagcompound.setBoolean("hardcore", this.settings.isHardcore());
        nbttagcompound.setBoolean("allowCommands", this.settings.e());
        nbttagcompound.setBoolean("initialized", this.initialized);
        this.worldBorder.a(nbttagcompound);
        nbttagcompound.setByte("Difficulty", (byte) this.settings.getDifficulty().a());
        nbttagcompound.setBoolean("DifficultyLocked", this.difficultyLocked);
        nbttagcompound.set("GameRules", this.settings.getGameRules().a());
        nbttagcompound.set("DragonFight", this.endDragonFightData);
        if (nbttagcompound1 != null) {
            nbttagcompound.set("Player", nbttagcompound1);
        }

        DataPackConfiguration.CODEC.encodeStart(DynamicOpsNBT.INSTANCE, this.settings.g()).result().ifPresent((nbtbase) -> {
            nbttagcompound.set("DataPacks", nbtbase);
        });
        if (this.customBossEvents != null) {
            nbttagcompound.set("CustomBossEvents", this.customBossEvents);
        }

        nbttagcompound.set("ScheduledEvents", this.scheduledEvents.b());
        nbttagcompound.setInt("WanderingTraderSpawnDelay", this.wanderingTraderSpawnDelay);
        nbttagcompound.setInt("WanderingTraderSpawnChance", this.wanderingTraderSpawnChance);
        if (this.wanderingTraderId != null) {
            nbttagcompound.a("WanderingTraderId", this.wanderingTraderId);
        }

    }

    @Override
    public int a() {
        return this.xSpawn;
    }

    @Override
    public int b() {
        return this.ySpawn;
    }

    @Override
    public int c() {
        return this.zSpawn;
    }

    @Override
    public float d() {
        return this.spawnAngle;
    }

    @Override
    public long getTime() {
        return this.gameTime;
    }

    @Override
    public long getDayTime() {
        return this.dayTime;
    }

    private void J() {
        if (!this.upgradedPlayerTag && this.loadedPlayerTag != null) {
            if (this.playerDataVersion < SharedConstants.getGameVersion().getWorldVersion()) {
                if (this.fixerUpper == null) {
                    throw (NullPointerException) SystemUtils.c((Throwable) (new NullPointerException("Fixer Upper not set inside LevelData, and the player tag is not upgraded.")));
                }

                this.loadedPlayerTag = GameProfileSerializer.a(this.fixerUpper, DataFixTypes.PLAYER, this.loadedPlayerTag, this.playerDataVersion);
            }

            this.upgradedPlayerTag = true;
        }
    }

    @Override
    public NBTTagCompound y() {
        this.J();
        return this.loadedPlayerTag;
    }

    @Override
    public void b(int i) {
        this.xSpawn = i;
    }

    @Override
    public void c(int i) {
        this.ySpawn = i;
    }

    @Override
    public void d(int i) {
        this.zSpawn = i;
    }

    @Override
    public void a(float f) {
        this.spawnAngle = f;
    }

    @Override
    public void setTime(long i) {
        this.gameTime = i;
    }

    @Override
    public void setDayTime(long i) {
        this.dayTime = i;
    }

    @Override
    public void setSpawn(BlockPosition blockposition, float f) {
        this.xSpawn = blockposition.getX();
        this.ySpawn = blockposition.getY();
        this.zSpawn = blockposition.getZ();
        this.spawnAngle = f;
    }

    @Override
    public String getName() {
        return this.settings.getLevelName();
    }

    @Override
    public int z() {
        return this.version;
    }

    @Override
    public int getClearWeatherTime() {
        return this.clearWeatherTime;
    }

    @Override
    public void setClearWeatherTime(int i) {
        this.clearWeatherTime = i;
    }

    @Override
    public boolean isThundering() {
        return this.thundering;
    }

    @Override
    public void setThundering(boolean flag) {
        this.thundering = flag;
    }

    @Override
    public int getThunderDuration() {
        return this.thunderTime;
    }

    @Override
    public void setThunderDuration(int i) {
        this.thunderTime = i;
    }

    @Override
    public boolean hasStorm() {
        return this.raining;
    }

    @Override
    public void setStorm(boolean flag) {
        this.raining = flag;
    }

    @Override
    public int getWeatherDuration() {
        return this.rainTime;
    }

    @Override
    public void setWeatherDuration(int i) {
        this.rainTime = i;
    }

    @Override
    public EnumGamemode getGameType() {
        return this.settings.getGameType();
    }

    @Override
    public void setGameType(EnumGamemode enumgamemode) {
        this.settings = this.settings.a(enumgamemode);
    }

    @Override
    public boolean isHardcore() {
        return this.settings.isHardcore();
    }

    @Override
    public boolean o() {
        return this.settings.e();
    }

    @Override
    public boolean p() {
        return this.initialized;
    }

    @Override
    public void c(boolean flag) {
        this.initialized = flag;
    }

    @Override
    public GameRules q() {
        return this.settings.getGameRules();
    }

    @Override
    public WorldBorder.c r() {
        return this.worldBorder;
    }

    @Override
    public void a(WorldBorder.c worldborder_c) {
        this.worldBorder = worldborder_c;
    }

    @Override
    public EnumDifficulty getDifficulty() {
        return this.settings.getDifficulty();
    }

    @Override
    public void setDifficulty(EnumDifficulty enumdifficulty) {
        this.settings = this.settings.a(enumdifficulty);
    }

    @Override
    public boolean isDifficultyLocked() {
        return this.difficultyLocked;
    }

    @Override
    public void d(boolean flag) {
        this.difficultyLocked = flag;
    }

    @Override
    public CustomFunctionCallbackTimerQueue<MinecraftServer> u() {
        return this.scheduledEvents;
    }

    @Override
    public void a(CrashReportSystemDetails crashreportsystemdetails, LevelHeightAccessor levelheightaccessor) {
        IWorldDataServer.super.a(crashreportsystemdetails, levelheightaccessor);
        SaveData.super.a(crashreportsystemdetails);
    }

    @Override
    public GeneratorSettings getGeneratorSettings() {
        return this.worldGenSettings;
    }

    @Override
    public Lifecycle B() {
        return this.worldGenSettingsLifecycle;
    }

    @Override
    public NBTTagCompound C() {
        return this.endDragonFightData;
    }

    @Override
    public void a(NBTTagCompound nbttagcompound) {
        this.endDragonFightData = nbttagcompound;
    }

    @Override
    public DataPackConfiguration D() {
        return this.settings.g();
    }

    @Override
    public void a(DataPackConfiguration datapackconfiguration) {
        this.settings = this.settings.a(datapackconfiguration);
    }

    @Nullable
    @Override
    public NBTTagCompound getCustomBossEvents() {
        return this.customBossEvents;
    }

    @Override
    public void setCustomBossEvents(@Nullable NBTTagCompound nbttagcompound) {
        this.customBossEvents = nbttagcompound;
    }

    @Override
    public int v() {
        return this.wanderingTraderSpawnDelay;
    }

    @Override
    public void g(int i) {
        this.wanderingTraderSpawnDelay = i;
    }

    @Override
    public int w() {
        return this.wanderingTraderSpawnChance;
    }

    @Override
    public void h(int i) {
        this.wanderingTraderSpawnChance = i;
    }

    @Nullable
    @Override
    public UUID x() {
        return this.wanderingTraderId;
    }

    @Override
    public void a(UUID uuid) {
        this.wanderingTraderId = uuid;
    }

    @Override
    public void a(String s, boolean flag) {
        this.knownServerBrands.add(s);
        this.wasModded |= flag;
    }

    @Override
    public boolean F() {
        return this.wasModded;
    }

    @Override
    public Set<String> G() {
        return ImmutableSet.copyOf(this.knownServerBrands);
    }

    @Override
    public IWorldDataServer H() {
        return this;
    }

    @Override
    public WorldSettings I() {
        return this.settings.h();
    }
}
