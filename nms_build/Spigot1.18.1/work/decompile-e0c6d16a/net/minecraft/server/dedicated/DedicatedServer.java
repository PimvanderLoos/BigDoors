package net.minecraft.server.dedicated;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.datafixers.DataFixer;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Proxy;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.function.BooleanSupplier;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.minecraft.DefaultUncaughtExceptionHandler;
import net.minecraft.SharedConstants;
import net.minecraft.SystemReport;
import net.minecraft.SystemUtils;
import net.minecraft.ThreadNamedUncaughtExceptionHandler;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.IRegistryCustom;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.server.DataPackResources;
import net.minecraft.server.IMinecraftServer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerCommand;
import net.minecraft.server.gui.ServerGUI;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.server.level.progress.WorldLoadListenerFactory;
import net.minecraft.server.network.ITextFilter;
import net.minecraft.server.network.TextFilter;
import net.minecraft.server.packs.repository.ResourcePackRepository;
import net.minecraft.server.players.NameReferencingFileConverter;
import net.minecraft.server.players.UserCache;
import net.minecraft.server.rcon.RemoteControlCommandListener;
import net.minecraft.server.rcon.thread.RemoteControlListener;
import net.minecraft.server.rcon.thread.RemoteStatusListener;
import net.minecraft.util.MathHelper;
import net.minecraft.util.monitoring.jmx.MinecraftServerBeans;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.EnumGamemode;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.entity.TileEntitySkull;
import net.minecraft.world.level.storage.Convertable;
import net.minecraft.world.level.storage.SaveData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DedicatedServer extends MinecraftServer implements IMinecraftServer {

    static final Logger LOGGER = LogManager.getLogger();
    private static final int CONVERSION_RETRY_DELAY_MS = 5000;
    private static final int CONVERSION_RETRIES = 2;
    private static final Pattern SHA1 = Pattern.compile("^[a-fA-F0-9]{40}$");
    private final List<ServerCommand> consoleInput = Collections.synchronizedList(Lists.newArrayList());
    @Nullable
    private RemoteStatusListener queryThreadGs4;
    public final RemoteControlCommandListener rconConsoleSource;
    @Nullable
    private RemoteControlListener rconThread;
    public DedicatedServerSettings settings;
    @Nullable
    private ServerGUI gui;
    @Nullable
    private final TextFilter textFilterClient;
    @Nullable
    private final IChatBaseComponent resourcePackPrompt;

    public DedicatedServer(Thread thread, IRegistryCustom.Dimension iregistrycustom_dimension, Convertable.ConversionSession convertable_conversionsession, ResourcePackRepository resourcepackrepository, DataPackResources datapackresources, SaveData savedata, DedicatedServerSettings dedicatedserversettings, DataFixer datafixer, MinecraftSessionService minecraftsessionservice, GameProfileRepository gameprofilerepository, UserCache usercache, WorldLoadListenerFactory worldloadlistenerfactory) {
        super(thread, iregistrycustom_dimension, convertable_conversionsession, savedata, resourcepackrepository, Proxy.NO_PROXY, datafixer, datapackresources, minecraftsessionservice, gameprofilerepository, usercache, worldloadlistenerfactory);
        this.settings = dedicatedserversettings;
        this.rconConsoleSource = new RemoteControlCommandListener(this);
        this.textFilterClient = TextFilter.createFromConfig(dedicatedserversettings.getProperties().textFilteringConfig);
        this.resourcePackPrompt = parseResourcePackPrompt(dedicatedserversettings);
    }

    @Override
    public boolean initServer() throws IOException {
        Thread thread = new Thread("Server console handler") {
            public void run() {
                BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));

                String s;

                try {
                    while (!DedicatedServer.this.isStopped() && DedicatedServer.this.isRunning() && (s = bufferedreader.readLine()) != null) {
                        DedicatedServer.this.handleConsoleInput(s, DedicatedServer.this.createCommandSourceStack());
                    }
                } catch (IOException ioexception) {
                    DedicatedServer.LOGGER.error("Exception handling console input", ioexception);
                }

            }
        };

        thread.setDaemon(true);
        thread.setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(DedicatedServer.LOGGER));
        thread.start();
        DedicatedServer.LOGGER.info("Starting minecraft server version {}", SharedConstants.getCurrentVersion().getName());
        if (Runtime.getRuntime().maxMemory() / 1024L / 1024L < 512L) {
            DedicatedServer.LOGGER.warn("To start the server with more ram, launch it as \"java -Xmx1024M -Xms1024M -jar minecraft_server.jar\"");
        }

        DedicatedServer.LOGGER.info("Loading properties");
        DedicatedServerProperties dedicatedserverproperties = this.settings.getProperties();

        if (this.isSingleplayer()) {
            this.setLocalIp("127.0.0.1");
        } else {
            this.setUsesAuthentication(dedicatedserverproperties.onlineMode);
            this.setPreventProxyConnections(dedicatedserverproperties.preventProxyConnections);
            this.setLocalIp(dedicatedserverproperties.serverIp);
        }

        this.setPvpAllowed(dedicatedserverproperties.pvp);
        this.setFlightAllowed(dedicatedserverproperties.allowFlight);
        this.setResourcePack(dedicatedserverproperties.resourcePack, this.getPackHash());
        this.setMotd(dedicatedserverproperties.motd);
        super.setPlayerIdleTimeout((Integer) dedicatedserverproperties.playerIdleTimeout.get());
        this.setEnforceWhitelist(dedicatedserverproperties.enforceWhitelist);
        this.worldData.setGameType(dedicatedserverproperties.gamemode);
        DedicatedServer.LOGGER.info("Default game type: {}", dedicatedserverproperties.gamemode);
        InetAddress inetaddress = null;

        if (!this.getLocalIp().isEmpty()) {
            inetaddress = InetAddress.getByName(this.getLocalIp());
        }

        if (this.getPort() < 0) {
            this.setPort(dedicatedserverproperties.serverPort);
        }

        this.initializeKeyPair();
        DedicatedServer.LOGGER.info("Starting Minecraft server on {}:{}", this.getLocalIp().isEmpty() ? "*" : this.getLocalIp(), this.getPort());

        try {
            this.getConnection().startTcpServerListener(inetaddress, this.getPort());
        } catch (IOException ioexception) {
            DedicatedServer.LOGGER.warn("**** FAILED TO BIND TO PORT!");
            DedicatedServer.LOGGER.warn("The exception was: {}", ioexception.toString());
            DedicatedServer.LOGGER.warn("Perhaps a server is already running on that port?");
            return false;
        }

        if (!this.usesAuthentication()) {
            DedicatedServer.LOGGER.warn("**** SERVER IS RUNNING IN OFFLINE/INSECURE MODE!");
            DedicatedServer.LOGGER.warn("The server will make no attempt to authenticate usernames. Beware.");
            DedicatedServer.LOGGER.warn("While this makes the game possible to play without internet access, it also opens up the ability for hackers to connect with any username they choose.");
            DedicatedServer.LOGGER.warn("To change this, set \"online-mode\" to \"true\" in the server.properties file.");
        }

        if (this.convertOldUsers()) {
            this.getProfileCache().save();
        }

        if (!NameReferencingFileConverter.serverReadyAfterUserconversion(this)) {
            return false;
        } else {
            this.setPlayerList(new DedicatedPlayerList(this, this.registryHolder, this.playerDataStorage));
            long i = SystemUtils.getNanos();

            TileEntitySkull.setup(this.getProfileCache(), this.getSessionService(), this);
            UserCache.setUsesAuthentication(this.usesAuthentication());
            DedicatedServer.LOGGER.info("Preparing level \"{}\"", this.getLevelIdName());
            this.loadLevel();
            long j = SystemUtils.getNanos() - i;
            String s = String.format(Locale.ROOT, "%.3fs", (double) j / 1.0E9D);

            DedicatedServer.LOGGER.info("Done ({})! For help, type \"help\"", s);
            if (dedicatedserverproperties.announcePlayerAchievements != null) {
                ((GameRules.GameRuleBoolean) this.getGameRules().getRule(GameRules.RULE_ANNOUNCE_ADVANCEMENTS)).set(dedicatedserverproperties.announcePlayerAchievements, this);
            }

            if (dedicatedserverproperties.enableQuery) {
                DedicatedServer.LOGGER.info("Starting GS4 status listener");
                this.queryThreadGs4 = RemoteStatusListener.create(this);
            }

            if (dedicatedserverproperties.enableRcon) {
                DedicatedServer.LOGGER.info("Starting remote control listener");
                this.rconThread = RemoteControlListener.create(this);
            }

            if (this.getMaxTickLength() > 0L) {
                Thread thread1 = new Thread(new ThreadWatchdog(this));

                thread1.setUncaughtExceptionHandler(new ThreadNamedUncaughtExceptionHandler(DedicatedServer.LOGGER));
                thread1.setName("Server Watchdog");
                thread1.setDaemon(true);
                thread1.start();
            }

            Items.AIR.fillItemCategory(CreativeModeTab.TAB_SEARCH, NonNullList.create());
            if (dedicatedserverproperties.enableJmxMonitoring) {
                MinecraftServerBeans.registerJmxMonitoring(this);
                DedicatedServer.LOGGER.info("JMX monitoring enabled");
            }

            return true;
        }
    }

    @Override
    public boolean isSpawningAnimals() {
        return this.getProperties().spawnAnimals && super.isSpawningAnimals();
    }

    @Override
    public boolean isSpawningMonsters() {
        return this.settings.getProperties().spawnMonsters && super.isSpawningMonsters();
    }

    @Override
    public boolean areNpcsEnabled() {
        return this.settings.getProperties().spawnNpcs && super.areNpcsEnabled();
    }

    public String getPackHash() {
        DedicatedServerProperties dedicatedserverproperties = this.settings.getProperties();
        String s;

        if (!dedicatedserverproperties.resourcePackSha1.isEmpty()) {
            s = dedicatedserverproperties.resourcePackSha1;
            if (!Strings.isNullOrEmpty(dedicatedserverproperties.resourcePackHash)) {
                DedicatedServer.LOGGER.warn("resource-pack-hash is deprecated and found along side resource-pack-sha1. resource-pack-hash will be ignored.");
            }
        } else if (!Strings.isNullOrEmpty(dedicatedserverproperties.resourcePackHash)) {
            DedicatedServer.LOGGER.warn("resource-pack-hash is deprecated. Please use resource-pack-sha1 instead.");
            s = dedicatedserverproperties.resourcePackHash;
        } else {
            s = "";
        }

        if (!s.isEmpty() && !DedicatedServer.SHA1.matcher(s).matches()) {
            DedicatedServer.LOGGER.warn("Invalid sha1 for ressource-pack-sha1");
        }

        if (!dedicatedserverproperties.resourcePack.isEmpty() && s.isEmpty()) {
            DedicatedServer.LOGGER.warn("You specified a resource pack without providing a sha1 hash. Pack will be updated on the client only if you change the name of the pack.");
        }

        return s;
    }

    @Override
    public DedicatedServerProperties getProperties() {
        return this.settings.getProperties();
    }

    @Override
    public void forceDifficulty() {
        this.setDifficulty(this.getProperties().difficulty, true);
    }

    @Override
    public boolean isHardcore() {
        return this.getProperties().hardcore;
    }

    @Override
    public SystemReport fillServerSystemReport(SystemReport systemreport) {
        systemreport.setDetail("Is Modded", () -> {
            return this.getModdedStatus().fullDescription();
        });
        systemreport.setDetail("Type", () -> {
            return "Dedicated Server (map_server.txt)";
        });
        return systemreport;
    }

    @Override
    public void dumpServerProperties(Path path) throws IOException {
        DedicatedServerProperties dedicatedserverproperties = this.getProperties();
        BufferedWriter bufferedwriter = Files.newBufferedWriter(path);

        try {
            bufferedwriter.write(String.format("sync-chunk-writes=%s%n", dedicatedserverproperties.syncChunkWrites));
            bufferedwriter.write(String.format("gamemode=%s%n", dedicatedserverproperties.gamemode));
            bufferedwriter.write(String.format("spawn-monsters=%s%n", dedicatedserverproperties.spawnMonsters));
            bufferedwriter.write(String.format("entity-broadcast-range-percentage=%d%n", dedicatedserverproperties.entityBroadcastRangePercentage));
            bufferedwriter.write(String.format("max-world-size=%d%n", dedicatedserverproperties.maxWorldSize));
            bufferedwriter.write(String.format("spawn-npcs=%s%n", dedicatedserverproperties.spawnNpcs));
            bufferedwriter.write(String.format("view-distance=%d%n", dedicatedserverproperties.viewDistance));
            bufferedwriter.write(String.format("simulation-distance=%d%n", dedicatedserverproperties.simulationDistance));
            bufferedwriter.write(String.format("spawn-animals=%s%n", dedicatedserverproperties.spawnAnimals));
            bufferedwriter.write(String.format("generate-structures=%s%n", dedicatedserverproperties.getWorldGenSettings(this.registryHolder).generateFeatures()));
            bufferedwriter.write(String.format("use-native=%s%n", dedicatedserverproperties.useNativeTransport));
            bufferedwriter.write(String.format("rate-limit=%d%n", dedicatedserverproperties.rateLimitPacketsPerSecond));
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

    }

    @Override
    public void onServerExit() {
        if (this.textFilterClient != null) {
            this.textFilterClient.close();
        }

        if (this.gui != null) {
            this.gui.close();
        }

        if (this.rconThread != null) {
            this.rconThread.stop();
        }

        if (this.queryThreadGs4 != null) {
            this.queryThreadGs4.stop();
        }

    }

    @Override
    public void tickChildren(BooleanSupplier booleansupplier) {
        super.tickChildren(booleansupplier);
        this.handleConsoleInputs();
    }

    @Override
    public boolean isNetherEnabled() {
        return this.getProperties().allowNether;
    }

    public void handleConsoleInput(String s, CommandListenerWrapper commandlistenerwrapper) {
        this.consoleInput.add(new ServerCommand(s, commandlistenerwrapper));
    }

    public void handleConsoleInputs() {
        while (!this.consoleInput.isEmpty()) {
            ServerCommand servercommand = (ServerCommand) this.consoleInput.remove(0);

            this.getCommands().performCommand(servercommand.source, servercommand.msg);
        }

    }

    @Override
    public boolean isDedicatedServer() {
        return true;
    }

    @Override
    public int getRateLimitPacketsPerSecond() {
        return this.getProperties().rateLimitPacketsPerSecond;
    }

    @Override
    public boolean isEpollEnabled() {
        return this.getProperties().useNativeTransport;
    }

    @Override
    public DedicatedPlayerList getPlayerList() {
        return (DedicatedPlayerList) super.getPlayerList();
    }

    @Override
    public boolean isPublished() {
        return true;
    }

    @Override
    public String getServerIp() {
        return this.getLocalIp();
    }

    @Override
    public int getServerPort() {
        return this.getPort();
    }

    @Override
    public String getServerName() {
        return this.getMotd();
    }

    public void showGui() {
        if (this.gui == null) {
            this.gui = ServerGUI.showFrameFor(this);
        }

    }

    @Override
    public boolean hasGui() {
        return this.gui != null;
    }

    @Override
    public boolean isCommandBlockEnabled() {
        return this.getProperties().enableCommandBlock;
    }

    @Override
    public int getSpawnProtectionRadius() {
        return this.getProperties().spawnProtection;
    }

    @Override
    public boolean isUnderSpawnProtection(WorldServer worldserver, BlockPosition blockposition, EntityHuman entityhuman) {
        if (worldserver.dimension() != World.OVERWORLD) {
            return false;
        } else if (this.getPlayerList().getOps().isEmpty()) {
            return false;
        } else if (this.getPlayerList().isOp(entityhuman.getGameProfile())) {
            return false;
        } else if (this.getSpawnProtectionRadius() <= 0) {
            return false;
        } else {
            BlockPosition blockposition1 = worldserver.getSharedSpawnPos();
            int i = MathHelper.abs(blockposition.getX() - blockposition1.getX());
            int j = MathHelper.abs(blockposition.getZ() - blockposition1.getZ());
            int k = Math.max(i, j);

            return k <= this.getSpawnProtectionRadius();
        }
    }

    @Override
    public boolean repliesToStatus() {
        return this.getProperties().enableStatus;
    }

    @Override
    public boolean hidesOnlinePlayers() {
        return this.getProperties().hideOnlinePlayers;
    }

    @Override
    public int getOperatorUserPermissionLevel() {
        return this.getProperties().opPermissionLevel;
    }

    @Override
    public int getFunctionCompilationLevel() {
        return this.getProperties().functionPermissionLevel;
    }

    @Override
    public void setPlayerIdleTimeout(int i) {
        super.setPlayerIdleTimeout(i);
        this.settings.update((dedicatedserverproperties) -> {
            return (DedicatedServerProperties) dedicatedserverproperties.playerIdleTimeout.update(this.registryAccess(), i);
        });
    }

    @Override
    public boolean shouldRconBroadcast() {
        return this.getProperties().broadcastRconToOps;
    }

    @Override
    public boolean shouldInformAdmins() {
        return this.getProperties().broadcastConsoleToOps;
    }

    @Override
    public int getAbsoluteMaxWorldSize() {
        return this.getProperties().maxWorldSize;
    }

    @Override
    public int getCompressionThreshold() {
        return this.getProperties().networkCompressionThreshold;
    }

    protected boolean convertOldUsers() {
        boolean flag = false;

        int i;

        for (i = 0; !flag && i <= 2; ++i) {
            if (i > 0) {
                DedicatedServer.LOGGER.warn("Encountered a problem while converting the user banlist, retrying in a few seconds");
                this.waitForRetry();
            }

            flag = NameReferencingFileConverter.convertUserBanlist(this);
        }

        boolean flag1 = false;

        for (i = 0; !flag1 && i <= 2; ++i) {
            if (i > 0) {
                DedicatedServer.LOGGER.warn("Encountered a problem while converting the ip banlist, retrying in a few seconds");
                this.waitForRetry();
            }

            flag1 = NameReferencingFileConverter.convertIpBanlist(this);
        }

        boolean flag2 = false;

        for (i = 0; !flag2 && i <= 2; ++i) {
            if (i > 0) {
                DedicatedServer.LOGGER.warn("Encountered a problem while converting the op list, retrying in a few seconds");
                this.waitForRetry();
            }

            flag2 = NameReferencingFileConverter.convertOpsList(this);
        }

        boolean flag3 = false;

        for (i = 0; !flag3 && i <= 2; ++i) {
            if (i > 0) {
                DedicatedServer.LOGGER.warn("Encountered a problem while converting the whitelist, retrying in a few seconds");
                this.waitForRetry();
            }

            flag3 = NameReferencingFileConverter.convertWhiteList(this);
        }

        boolean flag4 = false;

        for (i = 0; !flag4 && i <= 2; ++i) {
            if (i > 0) {
                DedicatedServer.LOGGER.warn("Encountered a problem while converting the player save files, retrying in a few seconds");
                this.waitForRetry();
            }

            flag4 = NameReferencingFileConverter.convertPlayers(this);
        }

        return flag || flag1 || flag2 || flag3 || flag4;
    }

    private void waitForRetry() {
        try {
            Thread.sleep(5000L);
        } catch (InterruptedException interruptedexception) {
            ;
        }
    }

    public long getMaxTickLength() {
        return this.getProperties().maxTickTime;
    }

    @Override
    public String getPluginNames() {
        return "";
    }

    @Override
    public String runCommand(String s) {
        this.rconConsoleSource.prepareForCommand();
        this.executeBlocking(() -> {
            this.getCommands().performCommand(this.rconConsoleSource.createCommandSourceStack(), s);
        });
        return this.rconConsoleSource.getCommandResponse();
    }

    public void storeUsingWhiteList(boolean flag) {
        this.settings.update((dedicatedserverproperties) -> {
            return (DedicatedServerProperties) dedicatedserverproperties.whiteList.update(this.registryAccess(), flag);
        });
    }

    @Override
    public void stopServer() {
        super.stopServer();
        SystemUtils.shutdownExecutors();
        TileEntitySkull.clear();
    }

    @Override
    public boolean isSingleplayerOwner(GameProfile gameprofile) {
        return false;
    }

    @Override
    public int getScaledTrackingDistance(int i) {
        return this.getProperties().entityBroadcastRangePercentage * i / 100;
    }

    @Override
    public String getLevelIdName() {
        return this.storageSource.getLevelId();
    }

    @Override
    public boolean forceSynchronousWrites() {
        return this.settings.getProperties().syncChunkWrites;
    }

    @Override
    public ITextFilter createTextFilterForPlayer(EntityPlayer entityplayer) {
        return this.textFilterClient != null ? this.textFilterClient.createContext(entityplayer.getGameProfile()) : ITextFilter.DUMMY;
    }

    @Override
    public boolean isResourcePackRequired() {
        return this.settings.getProperties().requireResourcePack;
    }

    @Nullable
    @Override
    public EnumGamemode getForcedGameType() {
        return this.settings.getProperties().forceGameMode ? this.worldData.getGameType() : null;
    }

    @Nullable
    private static IChatBaseComponent parseResourcePackPrompt(DedicatedServerSettings dedicatedserversettings) {
        String s = dedicatedserversettings.getProperties().resourcePackPrompt;

        if (!Strings.isNullOrEmpty(s)) {
            try {
                return IChatBaseComponent.ChatSerializer.fromJson(s);
            } catch (Exception exception) {
                DedicatedServer.LOGGER.warn("Failed to parse resource pack prompt '{}'", s, exception);
            }
        }

        return null;
    }

    @Nullable
    @Override
    public IChatBaseComponent getResourcePackPrompt() {
        return this.resourcePackPrompt;
    }
}
