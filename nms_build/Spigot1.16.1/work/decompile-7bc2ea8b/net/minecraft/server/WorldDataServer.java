package net.minecraft.server;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.mojang.datafixers.DataFixer;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.Lifecycle;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WorldDataServer implements IWorldDataServer, SaveData {

    private static final Logger LOGGER = LogManager.getLogger();
    private WorldSettings b;
    private final GeneratorSettings c;
    private final Lifecycle d;
    private int e;
    private int f;
    private int g;
    private long h;
    private long i;
    @Nullable
    private final DataFixer j;
    private final int k;
    private boolean l;
    @Nullable
    private NBTTagCompound m;
    private final int n;
    private int clearWeatherTime;
    private boolean raining;
    private int rainTime;
    private boolean thundering;
    private int thunderTime;
    private boolean t;
    private boolean u;
    private WorldBorder.c v;
    private NBTTagCompound w;
    @Nullable
    private NBTTagCompound customBossEvents;
    private int y;
    private int z;
    @Nullable
    private UUID A;
    private final Set<String> B;
    private boolean C;
    private final CustomFunctionCallbackTimerQueue<MinecraftServer> D;

    private WorldDataServer(@Nullable DataFixer datafixer, int i, @Nullable NBTTagCompound nbttagcompound, boolean flag, int j, int k, int l, long i1, long j1, int k1, int l1, int i2, boolean flag1, int j2, boolean flag2, boolean flag3, boolean flag4, WorldBorder.c worldborder_c, int k2, int l2, @Nullable UUID uuid, LinkedHashSet<String> linkedhashset, CustomFunctionCallbackTimerQueue<MinecraftServer> customfunctioncallbacktimerqueue, @Nullable NBTTagCompound nbttagcompound1, NBTTagCompound nbttagcompound2, WorldSettings worldsettings, GeneratorSettings generatorsettings, Lifecycle lifecycle) {
        this.j = datafixer;
        this.C = flag;
        this.e = j;
        this.f = k;
        this.g = l;
        this.h = i1;
        this.i = j1;
        this.n = k1;
        this.clearWeatherTime = l1;
        this.rainTime = i2;
        this.raining = flag1;
        this.thunderTime = j2;
        this.thundering = flag2;
        this.t = flag3;
        this.u = flag4;
        this.v = worldborder_c;
        this.y = k2;
        this.z = l2;
        this.A = uuid;
        this.B = linkedhashset;
        this.m = nbttagcompound;
        this.k = i;
        this.D = customfunctioncallbacktimerqueue;
        this.customBossEvents = nbttagcompound1;
        this.w = nbttagcompound2;
        this.b = worldsettings;
        this.c = generatorsettings;
        this.d = lifecycle;
    }

    public WorldDataServer(WorldSettings worldsettings, GeneratorSettings generatorsettings, Lifecycle lifecycle) {
        this((DataFixer) null, SharedConstants.getGameVersion().getWorldVersion(), (NBTTagCompound) null, false, 0, 0, 0, 0L, 0L, 19133, 0, 0, false, 0, false, false, false, WorldBorder.b, 0, 0, (UUID) null, Sets.newLinkedHashSet(), new CustomFunctionCallbackTimerQueue<>(CustomFunctionCallbackTimers.a), (NBTTagCompound) null, new NBTTagCompound(), worldsettings.h(), generatorsettings, lifecycle);
    }

    public static WorldDataServer a(Dynamic<NBTBase> dynamic, DataFixer datafixer, int i, @Nullable NBTTagCompound nbttagcompound, WorldSettings worldsettings, LevelVersion levelversion, GeneratorSettings generatorsettings, Lifecycle lifecycle) {
        long j = dynamic.get("Time").asLong(0L);
        NBTTagCompound nbttagcompound1 = (NBTTagCompound) dynamic.get("DragonFight").result().map(Dynamic::getValue).orElseGet(() -> {
            return (NBTBase) dynamic.get("DimensionData").get("1").get("DragonFight").orElseEmptyMap().getValue();
        });

        return new WorldDataServer(datafixer, i, nbttagcompound, dynamic.get("WasModded").asBoolean(false), dynamic.get("SpawnX").asInt(0), dynamic.get("SpawnY").asInt(0), dynamic.get("SpawnZ").asInt(0), j, dynamic.get("DayTime").asLong(j), levelversion.a(), dynamic.get("clearWeatherTime").asInt(0), dynamic.get("rainTime").asInt(0), dynamic.get("raining").asBoolean(false), dynamic.get("thunderTime").asInt(0), dynamic.get("thundering").asBoolean(false), dynamic.get("initialized").asBoolean(true), dynamic.get("DifficultyLocked").asBoolean(false), WorldBorder.c.a(dynamic, WorldBorder.b), dynamic.get("WanderingTraderSpawnDelay").asInt(0), dynamic.get("WanderingTraderSpawnChance").asInt(0), (UUID) dynamic.get("WanderingTraderId").read(MinecraftSerializableUUID.a).result().orElse((Object) null), (LinkedHashSet) dynamic.get("ServerBrands").asStream().flatMap((dynamic1) -> {
            return SystemUtils.a(dynamic1.asString().result());
        }).collect(Collectors.toCollection(Sets::newLinkedHashSet)), new CustomFunctionCallbackTimerQueue<>(CustomFunctionCallbackTimers.a, dynamic.get("ScheduledEvents").asStream()), (NBTTagCompound) dynamic.get("CustomBossEvents").orElseEmptyMap().getValue(), nbttagcompound1, worldsettings, generatorsettings, lifecycle);
    }

    @Override
    public NBTTagCompound a(IRegistryCustom iregistrycustom, @Nullable NBTTagCompound nbttagcompound) {
        this.I();
        if (nbttagcompound == null) {
            nbttagcompound = this.m;
        }

        NBTTagCompound nbttagcompound1 = new NBTTagCompound();

        this.a(iregistrycustom, nbttagcompound1, nbttagcompound);
        return nbttagcompound1;
    }

    private void a(IRegistryCustom iregistrycustom, NBTTagCompound nbttagcompound, @Nullable NBTTagCompound nbttagcompound1) {
        NBTTagList nbttaglist = new NBTTagList();

        this.B.stream().map(NBTTagString::a).forEach(nbttaglist::add);
        nbttagcompound.set("ServerBrands", nbttaglist);
        nbttagcompound.setBoolean("WasModded", this.C);
        NBTTagCompound nbttagcompound2 = new NBTTagCompound();

        nbttagcompound2.setString("Name", SharedConstants.getGameVersion().getName());
        nbttagcompound2.setInt("Id", SharedConstants.getGameVersion().getWorldVersion());
        nbttagcompound2.setBoolean("Snapshot", !SharedConstants.getGameVersion().isStable());
        nbttagcompound.set("Version", nbttagcompound2);
        nbttagcompound.setInt("DataVersion", SharedConstants.getGameVersion().getWorldVersion());
        RegistryWriteOps<NBTBase> registrywriteops = RegistryWriteOps.a(DynamicOpsNBT.a, iregistrycustom);
        DataResult dataresult = GeneratorSettings.a.encodeStart(registrywriteops, this.c);
        Logger logger = WorldDataServer.LOGGER;

        logger.getClass();
        dataresult.resultOrPartial(SystemUtils.a("WorldGenSettings: ", logger::error)).ifPresent((nbtbase) -> {
            nbttagcompound.set("WorldGenSettings", nbtbase);
        });
        nbttagcompound.setInt("GameType", this.b.getGameType().getId());
        nbttagcompound.setInt("SpawnX", this.e);
        nbttagcompound.setInt("SpawnY", this.f);
        nbttagcompound.setInt("SpawnZ", this.g);
        nbttagcompound.setLong("Time", this.h);
        nbttagcompound.setLong("DayTime", this.i);
        nbttagcompound.setLong("LastPlayed", SystemUtils.getTimeMillis());
        nbttagcompound.setString("LevelName", this.b.getLevelName());
        nbttagcompound.setInt("version", 19133);
        nbttagcompound.setInt("clearWeatherTime", this.clearWeatherTime);
        nbttagcompound.setInt("rainTime", this.rainTime);
        nbttagcompound.setBoolean("raining", this.raining);
        nbttagcompound.setInt("thunderTime", this.thunderTime);
        nbttagcompound.setBoolean("thundering", this.thundering);
        nbttagcompound.setBoolean("hardcore", this.b.isHardcore());
        nbttagcompound.setBoolean("allowCommands", this.b.e());
        nbttagcompound.setBoolean("initialized", this.t);
        this.v.a(nbttagcompound);
        nbttagcompound.setByte("Difficulty", (byte) this.b.getDifficulty().a());
        nbttagcompound.setBoolean("DifficultyLocked", this.u);
        nbttagcompound.set("GameRules", this.b.getGameRules().a());
        nbttagcompound.set("DragonFight", this.w);
        if (nbttagcompound1 != null) {
            nbttagcompound.set("Player", nbttagcompound1);
        }

        DataPackConfiguration.b.encodeStart(DynamicOpsNBT.a, this.b.g()).result().ifPresent((nbtbase) -> {
            nbttagcompound.set("DataPacks", nbtbase);
        });
        if (this.customBossEvents != null) {
            nbttagcompound.set("CustomBossEvents", this.customBossEvents);
        }

        nbttagcompound.set("ScheduledEvents", this.D.b());
        nbttagcompound.setInt("WanderingTraderSpawnDelay", this.y);
        nbttagcompound.setInt("WanderingTraderSpawnChance", this.z);
        if (this.A != null) {
            nbttagcompound.a("WanderingTraderId", this.A);
        }

    }

    @Override
    public int a() {
        return this.e;
    }

    @Override
    public int b() {
        return this.f;
    }

    @Override
    public int c() {
        return this.g;
    }

    @Override
    public long getTime() {
        return this.h;
    }

    @Override
    public long getDayTime() {
        return this.i;
    }

    private void I() {
        if (!this.l && this.m != null) {
            if (this.k < SharedConstants.getGameVersion().getWorldVersion()) {
                if (this.j == null) {
                    throw (NullPointerException) SystemUtils.c(new NullPointerException("Fixer Upper not set inside LevelData, and the player tag is not upgraded."));
                }

                this.m = GameProfileSerializer.a(this.j, DataFixTypes.PLAYER, this.m, this.k);
            }

            this.l = true;
        }
    }

    @Override
    public NBTTagCompound x() {
        this.I();
        return this.m;
    }

    @Override
    public void b(int i) {
        this.e = i;
    }

    @Override
    public void c(int i) {
        this.f = i;
    }

    @Override
    public void d(int i) {
        this.g = i;
    }

    @Override
    public void setTime(long i) {
        this.h = i;
    }

    @Override
    public void setDayTime(long i) {
        this.i = i;
    }

    @Override
    public void setSpawn(BlockPosition blockposition) {
        this.e = blockposition.getX();
        this.f = blockposition.getY();
        this.g = blockposition.getZ();
    }

    @Override
    public String getName() {
        return this.b.getLevelName();
    }

    @Override
    public int y() {
        return this.n;
    }

    @Override
    public int g() {
        return this.clearWeatherTime;
    }

    @Override
    public void a(int i) {
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
        return this.b.getGameType();
    }

    @Override
    public void setGameType(EnumGamemode enumgamemode) {
        this.b = this.b.a(enumgamemode);
    }

    @Override
    public boolean isHardcore() {
        return this.b.isHardcore();
    }

    @Override
    public boolean n() {
        return this.b.e();
    }

    @Override
    public boolean o() {
        return this.t;
    }

    @Override
    public void c(boolean flag) {
        this.t = flag;
    }

    @Override
    public GameRules p() {
        return this.b.getGameRules();
    }

    @Override
    public WorldBorder.c q() {
        return this.v;
    }

    @Override
    public void a(WorldBorder.c worldborder_c) {
        this.v = worldborder_c;
    }

    @Override
    public EnumDifficulty getDifficulty() {
        return this.b.getDifficulty();
    }

    @Override
    public void setDifficulty(EnumDifficulty enumdifficulty) {
        this.b = this.b.a(enumdifficulty);
    }

    @Override
    public boolean isDifficultyLocked() {
        return this.u;
    }

    @Override
    public void d(boolean flag) {
        this.u = flag;
    }

    @Override
    public CustomFunctionCallbackTimerQueue<MinecraftServer> t() {
        return this.D;
    }

    @Override
    public void a(CrashReportSystemDetails crashreportsystemdetails) {
        IWorldDataServer.super.a(crashreportsystemdetails);
        SaveData.super.a(crashreportsystemdetails);
    }

    @Override
    public GeneratorSettings getGeneratorSettings() {
        return this.c;
    }

    @Override
    public NBTTagCompound B() {
        return this.w;
    }

    @Override
    public void a(NBTTagCompound nbttagcompound) {
        this.w = nbttagcompound;
    }

    @Override
    public DataPackConfiguration C() {
        return this.b.g();
    }

    @Override
    public void a(DataPackConfiguration datapackconfiguration) {
        this.b = this.b.a(datapackconfiguration);
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
    public int u() {
        return this.y;
    }

    @Override
    public void g(int i) {
        this.y = i;
    }

    @Override
    public int v() {
        return this.z;
    }

    @Override
    public void h(int i) {
        this.z = i;
    }

    @Override
    public void a(UUID uuid) {
        this.A = uuid;
    }

    @Override
    public void a(String s, boolean flag) {
        this.B.add(s);
        this.C |= flag;
    }

    @Override
    public boolean E() {
        return this.C;
    }

    @Override
    public Set<String> F() {
        return ImmutableSet.copyOf(this.B);
    }

    @Override
    public IWorldDataServer G() {
        return this;
    }
}
