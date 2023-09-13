package net.minecraft.server.dedicated;

import com.google.gson.JsonObject;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;
import net.minecraft.core.IRegistryCustom;
import net.minecraft.util.ChatDeserializer;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.level.EnumGamemode;
import net.minecraft.world.level.levelgen.GeneratorSettings;

public class DedicatedServerProperties extends PropertyManager<DedicatedServerProperties> {

    public final boolean onlineMode = this.get("online-mode", true);
    public final boolean preventProxyConnections = this.get("prevent-proxy-connections", false);
    public final String serverIp = this.get("server-ip", "");
    public final boolean spawnAnimals = this.get("spawn-animals", true);
    public final boolean spawnNpcs = this.get("spawn-npcs", true);
    public final boolean pvp = this.get("pvp", true);
    public final boolean allowFlight = this.get("allow-flight", false);
    public final String resourcePack = this.get("resource-pack", "");
    public final boolean requireResourcePack = this.get("require-resource-pack", false);
    public final String resourcePackPrompt = this.get("resource-pack-prompt", "");
    public final String motd = this.get("motd", "A Minecraft Server");
    public final boolean forceGameMode = this.get("force-gamemode", false);
    public final boolean enforceWhitelist = this.get("enforce-whitelist", false);
    public final EnumDifficulty difficulty;
    public final EnumGamemode gamemode;
    public final String levelName;
    public final int serverPort;
    @Nullable
    public final Boolean announcePlayerAchievements;
    public final boolean enableQuery;
    public final int queryPort;
    public final boolean enableRcon;
    public final int rconPort;
    public final String rconPassword;
    @Nullable
    public final String resourcePackHash;
    public final String resourcePackSha1;
    public final boolean hardcore;
    public final boolean allowNether;
    public final boolean spawnMonsters;
    public final boolean useNativeTransport;
    public final boolean enableCommandBlock;
    public final int spawnProtection;
    public final int opPermissionLevel;
    public final int functionPermissionLevel;
    public final long maxTickTime;
    public final int rateLimitPacketsPerSecond;
    public final int viewDistance;
    public final int simulationDistance;
    public final int maxPlayers;
    public final int networkCompressionThreshold;
    public final boolean broadcastRconToOps;
    public final boolean broadcastConsoleToOps;
    public final int maxWorldSize;
    public final boolean syncChunkWrites;
    public final boolean enableJmxMonitoring;
    public final boolean enableStatus;
    public final boolean hideOnlinePlayers;
    public final int entityBroadcastRangePercentage;
    public final String textFilteringConfig;
    public final PropertyManager<DedicatedServerProperties>.EditableProperty<Integer> playerIdleTimeout;
    public final PropertyManager<DedicatedServerProperties>.EditableProperty<Boolean> whiteList;
    private final DedicatedServerProperties.a worldGenProperties;
    @Nullable
    private GeneratorSettings worldGenSettings;

    public DedicatedServerProperties(Properties properties) {
        super(properties);
        this.difficulty = (EnumDifficulty) this.get("difficulty", dispatchNumberOrString(EnumDifficulty::byId, EnumDifficulty::byName), EnumDifficulty::getKey, EnumDifficulty.EASY);
        this.gamemode = (EnumGamemode) this.get("gamemode", dispatchNumberOrString(EnumGamemode::byId, EnumGamemode::byName), EnumGamemode::getName, EnumGamemode.SURVIVAL);
        this.levelName = this.get("level-name", "world");
        this.serverPort = this.get("server-port", 25565);
        this.announcePlayerAchievements = this.getLegacyBoolean("announce-player-achievements");
        this.enableQuery = this.get("enable-query", false);
        this.queryPort = this.get("query.port", 25565);
        this.enableRcon = this.get("enable-rcon", false);
        this.rconPort = this.get("rcon.port", 25575);
        this.rconPassword = this.get("rcon.password", "");
        this.resourcePackHash = this.getLegacyString("resource-pack-hash");
        this.resourcePackSha1 = this.get("resource-pack-sha1", "");
        this.hardcore = this.get("hardcore", false);
        this.allowNether = this.get("allow-nether", true);
        this.spawnMonsters = this.get("spawn-monsters", true);
        this.useNativeTransport = this.get("use-native-transport", true);
        this.enableCommandBlock = this.get("enable-command-block", false);
        this.spawnProtection = this.get("spawn-protection", 16);
        this.opPermissionLevel = this.get("op-permission-level", 4);
        this.functionPermissionLevel = this.get("function-permission-level", 2);
        this.maxTickTime = this.get("max-tick-time", TimeUnit.MINUTES.toMillis(1L));
        this.rateLimitPacketsPerSecond = this.get("rate-limit", 0);
        this.viewDistance = this.get("view-distance", 10);
        this.simulationDistance = this.get("simulation-distance", 10);
        this.maxPlayers = this.get("max-players", 20);
        this.networkCompressionThreshold = this.get("network-compression-threshold", 256);
        this.broadcastRconToOps = this.get("broadcast-rcon-to-ops", true);
        this.broadcastConsoleToOps = this.get("broadcast-console-to-ops", true);
        this.maxWorldSize = this.get("max-world-size", (integer) -> {
            return MathHelper.clamp(integer, (int) 1, 29999984);
        }, 29999984);
        this.syncChunkWrites = this.get("sync-chunk-writes", true);
        this.enableJmxMonitoring = this.get("enable-jmx-monitoring", false);
        this.enableStatus = this.get("enable-status", true);
        this.hideOnlinePlayers = this.get("hide-online-players", false);
        this.entityBroadcastRangePercentage = this.get("entity-broadcast-range-percentage", (integer) -> {
            return MathHelper.clamp(integer, (int) 10, (int) 1000);
        }, 100);
        this.textFilteringConfig = this.get("text-filtering-config", "");
        this.playerIdleTimeout = this.getMutable("player-idle-timeout", 0);
        this.whiteList = this.getMutable("white-list", false);
        this.worldGenProperties = new DedicatedServerProperties.a(this.get("level-seed", ""), (JsonObject) this.get("generator-settings", (s) -> {
            return ChatDeserializer.parse(!s.isEmpty() ? s : "{}");
        }, new JsonObject()), this.get("generate-structures", true), (String) this.get("level-type", (s) -> {
            return s.toLowerCase(Locale.ROOT);
        }, "default"));
    }

    public static DedicatedServerProperties fromFile(Path path) {
        return new DedicatedServerProperties(loadFromFile(path));
    }

    @Override
    protected DedicatedServerProperties reload(IRegistryCustom iregistrycustom, Properties properties) {
        DedicatedServerProperties dedicatedserverproperties = new DedicatedServerProperties(properties);

        dedicatedserverproperties.getWorldGenSettings(iregistrycustom);
        return dedicatedserverproperties;
    }

    public GeneratorSettings getWorldGenSettings(IRegistryCustom iregistrycustom) {
        if (this.worldGenSettings == null) {
            this.worldGenSettings = GeneratorSettings.create(iregistrycustom, this.worldGenProperties);
        }

        return this.worldGenSettings;
    }

    public static record a(String a, JsonObject b, boolean c, String d) {

        private final String levelSeed;
        private final JsonObject generatorSettings;
        private final boolean generateStructures;
        private final String levelType;

        public a(String s, JsonObject jsonobject, boolean flag, String s1) {
            this.levelSeed = s;
            this.generatorSettings = jsonobject;
            this.generateStructures = flag;
            this.levelType = s1;
        }

        public String levelSeed() {
            return this.levelSeed;
        }

        public JsonObject generatorSettings() {
            return this.generatorSettings;
        }

        public boolean generateStructures() {
            return this.generateStructures;
        }

        public String levelType() {
            return this.levelType;
        }
    }
}
