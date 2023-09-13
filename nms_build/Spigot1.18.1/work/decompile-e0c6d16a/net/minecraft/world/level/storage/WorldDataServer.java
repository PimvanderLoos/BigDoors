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
        this((DataFixer) null, SharedConstants.getCurrentVersion().getWorldVersion(), (NBTTagCompound) null, false, 0, 0, 0, 0.0F, 0L, 0L, 19133, 0, 0, false, 0, false, false, false, WorldBorder.DEFAULT_SETTINGS, 0, 0, (UUID) null, Sets.newLinkedHashSet(), new CustomFunctionCallbackTimerQueue<>(CustomFunctionCallbackTimers.SERVER_CALLBACKS), (NBTTagCompound) null, new NBTTagCompound(), worldsettings.copy(), generatorsettings, lifecycle);
    }

    public static WorldDataServer parse(Dynamic<NBTBase> dynamic, DataFixer datafixer, int i, @Nullable NBTTagCompound nbttagcompound, WorldSettings worldsettings, LevelVersion levelversion, GeneratorSettings generatorsettings, Lifecycle lifecycle) {
        long j = dynamic.get("Time").asLong(0L);
        NBTTagCompound nbttagcompound1 = (NBTTagCompound) dynamic.get("DragonFight").result().map(Dynamic::getValue).orElseGet(() -> {
            return (NBTBase) dynamic.get("DimensionData").get("1").get("DragonFight").orElseEmptyMap().getValue();
        });

        return new WorldDataServer(datafixer, i, nbttagcompound, dynamic.get("WasModded").asBoolean(false), dynamic.get("SpawnX").asInt(0), dynamic.get("SpawnY").asInt(0), dynamic.get("SpawnZ").asInt(0), dynamic.get("SpawnAngle").asFloat(0.0F), j, dynamic.get("DayTime").asLong(j), levelversion.levelDataVersion(), dynamic.get("clearWeatherTime").asInt(0), dynamic.get("rainTime").asInt(0), dynamic.get("raining").asBoolean(false), dynamic.get("thunderTime").asInt(0), dynamic.get("thundering").asBoolean(false), dynamic.get("initialized").asBoolean(true), dynamic.get("DifficultyLocked").asBoolean(false), WorldBorder.c.read(dynamic, WorldBorder.DEFAULT_SETTINGS), dynamic.get("WanderingTraderSpawnDelay").asInt(0), dynamic.get("WanderingTraderSpawnChance").asInt(0), (UUID) dynamic.get("WanderingTraderId").read(MinecraftSerializableUUID.CODEC).result().orElse((Object) null), (Set) dynamic.get("ServerBrands").asStream().flatMap((dynamic1) -> {
            return SystemUtils.toStream(dynamic1.asString().result());
        }).collect(Collectors.toCollection(Sets::newLinkedHashSet)), new CustomFunctionCallbackTimerQueue<>(CustomFunctionCallbackTimers.SERVER_CALLBACKS, dynamic.get("ScheduledEvents").asStream()), (NBTTagCompound) dynamic.get("CustomBossEvents").orElseEmptyMap().getValue(), nbttagcompound1, worldsettings, generatorsettings, lifecycle);
    }

    @Override
    public NBTTagCompound createTag(IRegistryCustom iregistrycustom, @Nullable NBTTagCompound nbttagcompound) {
        this.updatePlayerTag();
        if (nbttagcompound == null) {
            nbttagcompound = this.loadedPlayerTag;
        }

        NBTTagCompound nbttagcompound1 = new NBTTagCompound();

        this.setTagData(iregistrycustom, nbttagcompound1, nbttagcompound);
        return nbttagcompound1;
    }

    private void setTagData(IRegistryCustom iregistrycustom, NBTTagCompound nbttagcompound, @Nullable NBTTagCompound nbttagcompound1) {
        NBTTagList nbttaglist = new NBTTagList();
        Stream stream = this.knownServerBrands.stream().map(NBTTagString::valueOf);

        Objects.requireNonNull(nbttaglist);
        stream.forEach(nbttaglist::add);
        nbttagcompound.put("ServerBrands", nbttaglist);
        nbttagcompound.putBoolean("WasModded", this.wasModded);
        NBTTagCompound nbttagcompound2 = new NBTTagCompound();

        nbttagcompound2.putString("Name", SharedConstants.getCurrentVersion().getName());
        nbttagcompound2.putInt("Id", SharedConstants.getCurrentVersion().getDataVersion().getVersion());
        nbttagcompound2.putBoolean("Snapshot", !SharedConstants.getCurrentVersion().isStable());
        nbttagcompound2.putString("Series", SharedConstants.getCurrentVersion().getDataVersion().getSeries());
        nbttagcompound.put("Version", nbttagcompound2);
        nbttagcompound.putInt("DataVersion", SharedConstants.getCurrentVersion().getWorldVersion());
        RegistryWriteOps<NBTBase> registrywriteops = RegistryWriteOps.create(DynamicOpsNBT.INSTANCE, iregistrycustom);
        DataResult dataresult = GeneratorSettings.CODEC.encodeStart(registrywriteops, this.worldGenSettings);
        Logger logger = WorldDataServer.LOGGER;

        Objects.requireNonNull(logger);
        dataresult.resultOrPartial(SystemUtils.prefix("WorldGenSettings: ", logger::error)).ifPresent((nbtbase) -> {
            nbttagcompound.put("WorldGenSettings", nbtbase);
        });
        nbttagcompound.putInt("GameType", this.settings.gameType().getId());
        nbttagcompound.putInt("SpawnX", this.xSpawn);
        nbttagcompound.putInt("SpawnY", this.ySpawn);
        nbttagcompound.putInt("SpawnZ", this.zSpawn);
        nbttagcompound.putFloat("SpawnAngle", this.spawnAngle);
        nbttagcompound.putLong("Time", this.gameTime);
        nbttagcompound.putLong("DayTime", this.dayTime);
        nbttagcompound.putLong("LastPlayed", SystemUtils.getEpochMillis());
        nbttagcompound.putString("LevelName", this.settings.levelName());
        nbttagcompound.putInt("version", 19133);
        nbttagcompound.putInt("clearWeatherTime", this.clearWeatherTime);
        nbttagcompound.putInt("rainTime", this.rainTime);
        nbttagcompound.putBoolean("raining", this.raining);
        nbttagcompound.putInt("thunderTime", this.thunderTime);
        nbttagcompound.putBoolean("thundering", this.thundering);
        nbttagcompound.putBoolean("hardcore", this.settings.hardcore());
        nbttagcompound.putBoolean("allowCommands", this.settings.allowCommands());
        nbttagcompound.putBoolean("initialized", this.initialized);
        this.worldBorder.write(nbttagcompound);
        nbttagcompound.putByte("Difficulty", (byte) this.settings.difficulty().getId());
        nbttagcompound.putBoolean("DifficultyLocked", this.difficultyLocked);
        nbttagcompound.put("GameRules", this.settings.gameRules().createTag());
        nbttagcompound.put("DragonFight", this.endDragonFightData);
        if (nbttagcompound1 != null) {
            nbttagcompound.put("Player", nbttagcompound1);
        }

        DataPackConfiguration.CODEC.encodeStart(DynamicOpsNBT.INSTANCE, this.settings.getDataPackConfig()).result().ifPresent((nbtbase) -> {
            nbttagcompound.put("DataPacks", nbtbase);
        });
        if (this.customBossEvents != null) {
            nbttagcompound.put("CustomBossEvents", this.customBossEvents);
        }

        nbttagcompound.put("ScheduledEvents", this.scheduledEvents.store());
        nbttagcompound.putInt("WanderingTraderSpawnDelay", this.wanderingTraderSpawnDelay);
        nbttagcompound.putInt("WanderingTraderSpawnChance", this.wanderingTraderSpawnChance);
        if (this.wanderingTraderId != null) {
            nbttagcompound.putUUID("WanderingTraderId", this.wanderingTraderId);
        }

    }

    @Override
    public int getXSpawn() {
        return this.xSpawn;
    }

    @Override
    public int getYSpawn() {
        return this.ySpawn;
    }

    @Override
    public int getZSpawn() {
        return this.zSpawn;
    }

    @Override
    public float getSpawnAngle() {
        return this.spawnAngle;
    }

    @Override
    public long getGameTime() {
        return this.gameTime;
    }

    @Override
    public long getDayTime() {
        return this.dayTime;
    }

    private void updatePlayerTag() {
        if (!this.upgradedPlayerTag && this.loadedPlayerTag != null) {
            if (this.playerDataVersion < SharedConstants.getCurrentVersion().getWorldVersion()) {
                if (this.fixerUpper == null) {
                    throw (NullPointerException) SystemUtils.pauseInIde(new NullPointerException("Fixer Upper not set inside LevelData, and the player tag is not upgraded."));
                }

                this.loadedPlayerTag = GameProfileSerializer.update(this.fixerUpper, DataFixTypes.PLAYER, this.loadedPlayerTag, this.playerDataVersion);
            }

            this.upgradedPlayerTag = true;
        }
    }

    @Override
    public NBTTagCompound getLoadedPlayerTag() {
        this.updatePlayerTag();
        return this.loadedPlayerTag;
    }

    @Override
    public void setXSpawn(int i) {
        this.xSpawn = i;
    }

    @Override
    public void setYSpawn(int i) {
        this.ySpawn = i;
    }

    @Override
    public void setZSpawn(int i) {
        this.zSpawn = i;
    }

    @Override
    public void setSpawnAngle(float f) {
        this.spawnAngle = f;
    }

    @Override
    public void setGameTime(long i) {
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
    public String getLevelName() {
        return this.settings.levelName();
    }

    @Override
    public int getVersion() {
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
    public int getThunderTime() {
        return this.thunderTime;
    }

    @Override
    public void setThunderTime(int i) {
        this.thunderTime = i;
    }

    @Override
    public boolean isRaining() {
        return this.raining;
    }

    @Override
    public void setRaining(boolean flag) {
        this.raining = flag;
    }

    @Override
    public int getRainTime() {
        return this.rainTime;
    }

    @Override
    public void setRainTime(int i) {
        this.rainTime = i;
    }

    @Override
    public EnumGamemode getGameType() {
        return this.settings.gameType();
    }

    @Override
    public void setGameType(EnumGamemode enumgamemode) {
        this.settings = this.settings.withGameType(enumgamemode);
    }

    @Override
    public boolean isHardcore() {
        return this.settings.hardcore();
    }

    @Override
    public boolean getAllowCommands() {
        return this.settings.allowCommands();
    }

    @Override
    public boolean isInitialized() {
        return this.initialized;
    }

    @Override
    public void setInitialized(boolean flag) {
        this.initialized = flag;
    }

    @Override
    public GameRules getGameRules() {
        return this.settings.gameRules();
    }

    @Override
    public WorldBorder.c getWorldBorder() {
        return this.worldBorder;
    }

    @Override
    public void setWorldBorder(WorldBorder.c worldborder_c) {
        this.worldBorder = worldborder_c;
    }

    @Override
    public EnumDifficulty getDifficulty() {
        return this.settings.difficulty();
    }

    @Override
    public void setDifficulty(EnumDifficulty enumdifficulty) {
        this.settings = this.settings.withDifficulty(enumdifficulty);
    }

    @Override
    public boolean isDifficultyLocked() {
        return this.difficultyLocked;
    }

    @Override
    public void setDifficultyLocked(boolean flag) {
        this.difficultyLocked = flag;
    }

    @Override
    public CustomFunctionCallbackTimerQueue<MinecraftServer> getScheduledEvents() {
        return this.scheduledEvents;
    }

    @Override
    public void fillCrashReportCategory(CrashReportSystemDetails crashreportsystemdetails, LevelHeightAccessor levelheightaccessor) {
        IWorldDataServer.super.fillCrashReportCategory(crashreportsystemdetails, levelheightaccessor);
        SaveData.super.fillCrashReportCategory(crashreportsystemdetails);
    }

    @Override
    public GeneratorSettings worldGenSettings() {
        return this.worldGenSettings;
    }

    @Override
    public Lifecycle worldGenSettingsLifecycle() {
        return this.worldGenSettingsLifecycle;
    }

    @Override
    public NBTTagCompound endDragonFightData() {
        return this.endDragonFightData;
    }

    @Override
    public void setEndDragonFightData(NBTTagCompound nbttagcompound) {
        this.endDragonFightData = nbttagcompound;
    }

    @Override
    public DataPackConfiguration getDataPackConfig() {
        return this.settings.getDataPackConfig();
    }

    @Override
    public void setDataPackConfig(DataPackConfiguration datapackconfiguration) {
        this.settings = this.settings.withDataPackConfig(datapackconfiguration);
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
    public int getWanderingTraderSpawnDelay() {
        return this.wanderingTraderSpawnDelay;
    }

    @Override
    public void setWanderingTraderSpawnDelay(int i) {
        this.wanderingTraderSpawnDelay = i;
    }

    @Override
    public int getWanderingTraderSpawnChance() {
        return this.wanderingTraderSpawnChance;
    }

    @Override
    public void setWanderingTraderSpawnChance(int i) {
        this.wanderingTraderSpawnChance = i;
    }

    @Nullable
    @Override
    public UUID getWanderingTraderId() {
        return this.wanderingTraderId;
    }

    @Override
    public void setWanderingTraderId(UUID uuid) {
        this.wanderingTraderId = uuid;
    }

    @Override
    public void setModdedInfo(String s, boolean flag) {
        this.knownServerBrands.add(s);
        this.wasModded |= flag;
    }

    @Override
    public boolean wasModded() {
        return this.wasModded;
    }

    @Override
    public Set<String> getKnownServerBrands() {
        return ImmutableSet.copyOf(this.knownServerBrands);
    }

    @Override
    public IWorldDataServer overworldData() {
        return this;
    }

    @Override
    public WorldSettings getLevelSettings() {
        return this.settings.copy();
    }
}
