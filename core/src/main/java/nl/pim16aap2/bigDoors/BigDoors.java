package nl.pim16aap2.bigDoors;

import com.github.Anon8281.universalScheduler.UniversalScheduler;
import com.github.Anon8281.universalScheduler.scheduling.schedulers.TaskScheduler;
import nl.pim16aap2.bigDoors.GUI.GUI;
import nl.pim16aap2.bigDoors.NMS.FallingBlockFactory;
import nl.pim16aap2.bigDoors.NMS.FallingBlockFactoryProvider_V1_20_R1;
import nl.pim16aap2.bigDoors.NMS.FallingBlockFactoryProvider_V1_20_R2;
import nl.pim16aap2.bigDoors.NMS.FallingBlockFactoryProvider_V1_20_R3;
import nl.pim16aap2.bigDoors.NMS.FallingBlockFactoryProvider_V1_20_R4;
import nl.pim16aap2.bigDoors.NMS.FallingBlockFactoryProvider_V1_21_R1;
import nl.pim16aap2.bigDoors.NMS.FallingBlockFactoryProvider_V1_21_R2;
import nl.pim16aap2.bigDoors.NMS.FallingBlockFactoryProvider_V1_21_R3;
import nl.pim16aap2.bigDoors.NMS.FallingBlockFactoryProvider_V1_21_R4;
import nl.pim16aap2.bigDoors.NMS.FallingBlockFactoryProvider_V1_21_R5;
import nl.pim16aap2.bigDoors.NMS.FallingBlockFactoryProvider_V1_21_R6;
import nl.pim16aap2.bigDoors.NMS.FallingBlockFactory_V1_11_R1;
import nl.pim16aap2.bigDoors.NMS.FallingBlockFactory_V1_12_R1;
import nl.pim16aap2.bigDoors.NMS.FallingBlockFactory_V1_13_R1;
import nl.pim16aap2.bigDoors.NMS.FallingBlockFactory_V1_13_R1_5;
import nl.pim16aap2.bigDoors.NMS.FallingBlockFactory_V1_13_R2;
import nl.pim16aap2.bigDoors.NMS.FallingBlockFactory_V1_14_R1;
import nl.pim16aap2.bigDoors.NMS.FallingBlockFactory_V1_15_R1;
import nl.pim16aap2.bigDoors.NMS.FallingBlockFactory_V1_16_R1;
import nl.pim16aap2.bigDoors.NMS.FallingBlockFactory_V1_16_R2;
import nl.pim16aap2.bigDoors.NMS.FallingBlockFactory_V1_16_R3;
import nl.pim16aap2.bigDoors.NMS.FallingBlockFactory_V1_17_R1;
import nl.pim16aap2.bigDoors.NMS.FallingBlockFactory_V1_18_R1;
import nl.pim16aap2.bigDoors.NMS.FallingBlockFactory_V1_18_R2;
import nl.pim16aap2.bigDoors.NMS.FallingBlockFactory_V1_19_R1;
import nl.pim16aap2.bigDoors.NMS.FallingBlockFactory_V1_19_R1_1;
import nl.pim16aap2.bigDoors.NMS.FallingBlockFactory_V1_19_R2;
import nl.pim16aap2.bigDoors.NMS.FallingBlockFactory_V1_19_R3;
import nl.pim16aap2.bigDoors.codegeneration.FallbackGeneratorManager;
import nl.pim16aap2.bigDoors.compatibility.FakePlayerCreator;
import nl.pim16aap2.bigDoors.compatibility.ProtectionCompatManager;
import nl.pim16aap2.bigDoors.handlers.ChunkUnloadHandler;
import nl.pim16aap2.bigDoors.handlers.CommandHandler;
import nl.pim16aap2.bigDoors.handlers.EventHandlers;
import nl.pim16aap2.bigDoors.handlers.FailureCommandHandler;
import nl.pim16aap2.bigDoors.handlers.GUIHandler;
import nl.pim16aap2.bigDoors.handlers.LoginMessageHandler;
import nl.pim16aap2.bigDoors.handlers.LoginResourcePackHandler;
import nl.pim16aap2.bigDoors.handlers.RedstoneHandler;
import nl.pim16aap2.bigDoors.moveBlocks.BridgeOpener;
import nl.pim16aap2.bigDoors.moveBlocks.DoorOpener;
import nl.pim16aap2.bigDoors.moveBlocks.Opener;
import nl.pim16aap2.bigDoors.moveBlocks.PortcullisOpener;
import nl.pim16aap2.bigDoors.moveBlocks.SlidingDoorOpener;
import nl.pim16aap2.bigDoors.reflection.BukkitReflectionUtil;
import nl.pim16aap2.bigDoors.storage.sqlite.SQLiteJDBCDriverConnection;
import nl.pim16aap2.bigDoors.toolUsers.ToolUser;
import nl.pim16aap2.bigDoors.toolUsers.ToolVerifier;
import nl.pim16aap2.bigDoors.util.ChunkUtils.ChunkLoadMode;
import nl.pim16aap2.bigDoors.util.ChunkUtils.ChunkLoadResult;
import nl.pim16aap2.bigDoors.util.ConfigLoader;
import nl.pim16aap2.bigDoors.util.DoorOpenResult;
import nl.pim16aap2.bigDoors.util.DoorType;
import nl.pim16aap2.bigDoors.util.Messages;
import nl.pim16aap2.bigDoors.util.TimedCache;
import nl.pim16aap2.bigDoors.util.Util;
import nl.pim16aap2.bigDoors.waitForCommand.WaitForCommand;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.AdvancedPie;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.semver4j.Semver;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;


public class BigDoors extends JavaPlugin implements Listener
{
    public static final Semver SERVER_VERSION =
        Objects.requireNonNull(Semver.coerce(Bukkit.getServer().getBukkitVersion())).withClearedPreReleaseAndBuild();

    private static BigDoors instance;
    private static volatile TaskScheduler scheduler;
    public static final boolean DEVBUILD = false;
    private int buildNumber = -1;

    public static final int MINIMUMDOORDELAY = 15;

    private static final Set<String> BLACKLISTED_SERVERS = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
        "CatServer", "Mohist", "Magma", "Glowstone", "Akarin", "ArcLight")));

    private static final List<String> BLACKLISTED_PLUGINS = Collections.unmodifiableList(new ArrayList<>(Arrays.asList(
        "Geyser-Spigot", "ViaRewind")));

    private ToolVerifier tf;
    private SQLiteJDBCDriverConnection db;
    private FallingBlockFactory fabf;
    private ConfigLoader config;
    private String locale;
    private final MyLogger logger;
    private final File logFile;
    private Metrics metrics;
    private Messages messages;
    private Commander commander = null;
    private DoorOpener doorOpener;
    private BridgeOpener bridgeOpener;
    private CommandHandler commandHandler;
    private FailureCommandHandler failureCommandHandler;
    private SlidingDoorOpener slidingDoorOpener;
    private PortcullisOpener portcullisOpener;
    private @Nullable RedstoneHandler redstoneHandler;
    private boolean validVersion;
    private HashMap<UUID, ToolUser> toolUsers;
    private HashMap<UUID, GUI> playerGUIs;
    private HashMap<UUID, WaitForCommand> cmdWaiters;
    private FakePlayerCreator fakePlayerCreator;
    private AutoCloseScheduler autoCloseScheduler;
    private ProtectionCompatManager protCompatMan;
    private @Nullable LoginResourcePackHandler rPackHandler;
    private TimedCache<Long /* Chunk */, HashMap<Long /* Loc */, Long /* doorUID */>> pbCache = null;
    private VaultManager vaultManager;
    private UpdateManager updateManager;
    private volatile boolean schedulerIsRunning = false;
    private static final boolean IS_ON_FLATTENED_VERSION = SERVER_VERSION.isGreaterThanOrEqualTo(Semver.of(1, 13, 0));
    private boolean isEnabled = false;
    private final List<String> loginMessages = new ArrayList<>();
    private final WorldHeightManager worldHeightManager = new WorldHeightManager();

    public BigDoors()
    {
        instance = this;
        logFile = new File(getDataFolder(), "log.txt");
        logger = new MyLogger(this, logFile);
        initLegacyMaterials();
    }

    @Override
    public void onEnable()
    {
        scheduler = UniversalScheduler.getScheduler(this);
        try
        {
            onEnable0();
        }
        catch (Throwable t)
        {
            setDisabled("An unknown error occurred!");
            logger.logMessageToConsoleOnly("Failed to enable plugin: An unknown error occurred!");
            getMyLogger().logMessage(Level.SEVERE, Util.throwableToString(t));
        }
    }

    private void onEnable0()
        throws Exception
    {
        if (!schedulerIsRunning)
            getScheduler().runTask(() -> schedulerIsRunning = true);

        updateManager = new UpdateManager(this);
        buildNumber = readBuildNumber();
        overrideVersion();

        try
        {
            readConfigValues();
        }
        catch (Exception | ExceptionInInitializerError e)
        {
            setDisabled("Failed to read config file!");
            logger.logMessageToConsoleOnly("Failed to read config file. Plugin disabled!");
            getMyLogger().logMessage(Level.SEVERE, Util.throwableToString(e));
            return;
        }

        try
        {
            BukkitReflectionUtil.init();
        }
        catch (Exception | ExceptionInInitializerError e)
        {
            setDisabled("Failed to initialize BukkitReflectionUtil!");
            logger.logMessageToConsoleOnly("Failed to initialize BukkitReflectionUtil! Plugin disabled!");
            getMyLogger().logMessage(Level.SEVERE, Util.throwableToString(e));
            return;
        }

        updateManager.setEnabled(getConfigLoader().autoDLUpdate(), getConfigLoader().announceUpdateCheck());

        messages = new Messages(this);

        final Optional<String> disableReason = isCurrentEnvironmentInvalid();
        if (disableReason.isPresent())
        {
            if (!getConfigLoader().unsafeMode())
            {
                String error = "Running in an invalid environment: '"
                    + disableReason.get() +
                    "'. This can be bypassed in the config if you are feeling adventurous (unsafeMode).";
                setDisabled(error);
                logger.logMessage(error, true, true);
                return;
            }
            else if (config.unsafeModeNotification())
                loginMessages.add("You are trying to load this plugin in an unsupported environment: '"
                                      + disableReason.get() + "'. This may cause issues!");
        }

        logger.logMessageToLogFile("Starting BigDoors version: " + getDescription().getVersion());

        Bukkit.getPluginManager().registerEvents(new LoginMessageHandler(this), this);

        try
        {
            fabf = createFallingBlockFactory();
            validVersion = (fabf != null);
        }
        catch (Exception | ExceptionInInitializerError e)
        {
            logger.logMessageToConsoleOnly(
                "Failed to enable the plugin for Minecraft version '" + SERVER_VERSION + "'!");
            getMyLogger().logMessage(Level.SEVERE, Util.throwableToString(e));
            validVersion = false;
        }
        // Load the files for the correct version of Minecraft.
        if (!validVersion)
        {
            setDisabled(
                "This version of Minecraft is not supported. Is the plugin up-to-date?");
            logger.logMessage("Trying to load the plugin on an incompatible version of Minecraft! ('"
                                  + SERVER_VERSION + "'). This plugin will NOT be enabled!", true, true);
            logger.logMessage("If no update is available for this version, you could try to enable " +
                                  "__CODE GENERATION__ in the config.", true, true);
            logger.logMessage("Code generation may add support for this version, but be sure to read the " +
                                  "warning in the config before using it!", true, true);
            return;
        }
        fakePlayerCreator = new FakePlayerCreator(this);

        init();

        vaultManager = new VaultManager(this);
        autoCloseScheduler = new AutoCloseScheduler(this);

        Bukkit.getPluginManager().registerEvents(new EventHandlers(this), this);
        Bukkit.getPluginManager().registerEvents(new GUIHandler(this), this);
        Bukkit.getPluginManager().registerEvents(new ChunkUnloadHandler(this), this);

        // No need to put these in init, as they should not be reloaded.
        pbCache = new TimedCache<>(config.cacheTimeout());
        protCompatMan = new ProtectionCompatManager(this);
        Bukkit.getPluginManager().registerEvents(protCompatMan, this);
        db = new SQLiteJDBCDriverConnection(this, config.dbFile());
        commander = new Commander(this, db);
        doorOpener = new DoorOpener(this);
        bridgeOpener = new BridgeOpener(this);
        commandHandler = new CommandHandler(this);
        portcullisOpener = new PortcullisOpener(this);
        slidingDoorOpener = new SlidingDoorOpener(this);

        registerCommands(commandHandler);

        isEnabled = true;
    }

    public WorldHeightManager getWorldHeightManager()
    {
        return worldHeightManager;
    }

    /**
     * Checks if the Bukkit scheduler is running. This will be false until all plugins have been enabled.
     *
     * @return True if the scheduler is running.
     */
    public boolean isSchedulerRunning()
    {
        return schedulerIsRunning;
    }

    /**
     * Asserts that the scheduler is running.
     *
     * @throws IllegalStateException When the scheduler is not running.
     */
    public void assertSchedulerRunning()
        throws IllegalStateException
    {
        if (!BigDoors.get().isSchedulerRunning())
            throw new IllegalStateException("Scheduler is not running!");
    }

    /**
     * Checks if the current environment is invalid. This plugin should not attempt initialization in an invalid
     * environment.
     * <p>
     * Note that it doesn't check if the version is valid.
     *
     * @return The name of the invalid environment, if one could be found.
     */
    private Optional<String> isCurrentEnvironmentInvalid()
    {
        for (final String pluginName : BLACKLISTED_PLUGINS)
            if (getServer().getPluginManager().getPlugin(pluginName) != null)
                return Optional.of(pluginName);

        if (BigDoors.BLACKLISTED_SERVERS.contains(Bukkit.getName()))
            return Optional.of(Bukkit.getName());

        return Optional.empty();
    }

    @SuppressWarnings("SpellCheckingInspection")
    private void registerCommands(CommandExecutor commandExecutor)
    {
        getCommand("recalculatepowerblocks").setExecutor(commandExecutor);
        getCommand("setbypassprotections").setExecutor(commandExecutor);
        getCommand("killbigdoorsentities").setExecutor(commandExecutor);
        getCommand("inspectpowerblockloc").setExecutor(commandExecutor);
        getCommand("changepowerblockloc").setExecutor(commandExecutor);
        getCommand("setautoclosetime").setExecutor(commandExecutor);
        getCommand("newslidingdoor").setExecutor(commandExecutor);
        getCommand("setdoorrotation").setExecutor(commandExecutor);
        getCommand("setblockstomove").setExecutor(commandExecutor);
        getCommand("listplayerdoors").setExecutor(commandExecutor);
        getCommand("setnotification").setExecutor(commandExecutor);
        getCommand("newdrawbridge").setExecutor(commandExecutor);
        getCommand("newportcullis").setExecutor(commandExecutor);
        getCommand("toggledoor").setExecutor(commandExecutor);
        getCommand("pausedoors").setExecutor(commandExecutor);
        getCommand("closedoor").setExecutor(commandExecutor);
        getCommand("doordebug").setExecutor(commandExecutor);
        getCommand("listdoors").setExecutor(commandExecutor);
        getCommand("stopdoors").setExecutor(commandExecutor);
        getCommand("bdcancel").setExecutor(commandExecutor);
        getCommand("filldoor").setExecutor(commandExecutor);
        getCommand("doorinfo").setExecutor(commandExecutor);
        getCommand("opendoor").setExecutor(commandExecutor);
        getCommand("nameDoor").setExecutor(commandExecutor);
        getCommand("bigdoors").setExecutor(commandExecutor);
        getCommand("newdoor").setExecutor(commandExecutor);
        getCommand("deldoor").setExecutor(commandExecutor);
        getCommand("bdm").setExecutor(commandExecutor);
    }

    private void setDisabled(String reason)
    {
        try
        {
            this.isEnabled = false;
            HandlerList.unregisterAll((JavaPlugin) this);
            failureCommandHandler = new FailureCommandHandler("Plugin disabled: " + reason);
            registerCommands(failureCommandHandler);
        }
        catch (Exception e)
        {
            logger.logMessageToConsoleOnly("Failed to set disabled status!");
            getMyLogger().logMessage(Level.SEVERE, Util.throwableToString(e));
        }
    }

    private void init()
    {
        if (!validVersion)
            return;

        // Don't read the config if the plugin hasn't been enabled yet.
        // In other words: Skip reading the config on the first run (because it's
        // already read in onEnable).
        if (isEnabled)
            readConfigValues();

        Util.processConfig(getConfigLoader());
        messages.reloadMessages();
        toolUsers = new HashMap<>();
        playerGUIs = new HashMap<>();
        cmdWaiters = new HashMap<>();
        tf = new ToolVerifier(messages.getString("CREATOR.GENERAL.StickName"));

        if (config.enableRedstone())
        {
            redstoneHandler = new RedstoneHandler(this);
            Bukkit.getPluginManager().registerEvents(redstoneHandler, this);
        }

        if (config.resourcePackEnabled())
        {
            // If a resource pack was set for the current version of Minecraft, send that
            // pack to the client on login.
            rPackHandler = new LoginResourcePackHandler(this, config.resourcePack());
            Bukkit.getPluginManager().registerEvents(rPackHandler, this);
        }

        // Load stats collector if allowed, otherwise unload it if needed or simply
        // don't load it in the first place.
        if (config.allowStats())
        {
            logger.myLogger(Level.INFO, "Enabling stats! Thanks, it really helps!");
            setupMetrics();
        }
        else
        {
            // Y u do dis? :(
            metrics = null;
            logger
                .myLogger(Level.INFO,
                          "Stats disabled, not laoding stats :(... Please consider enabling it! I am a simple man, seeing higher user numbers helps me stay motivated!");
        }

        updateManager.setEnabled(getConfigLoader().autoDLUpdate(), getConfigLoader().announceUpdateCheck());

        if (commander != null)
            commander.setCanGo(true);
    }

    public static BigDoors get()
    {
        return instance;
    }

    public @NotNull ClassLoader getBigDoorsClassLoader()
    {
        return super.getClassLoader();
    }

    /**
     * For this plugin, there is a difference between being enabled and being enabled successfully.
     * <p>
     * This plugin will always try to enable itself, even if it won't function at all. This is done so that an
     * alternative command handler can be registered to inform users that the plugin is broken and admins WHY it failed
     * to enable properly.
     * <p>
     * So, if you inted to use this plugin, make sure to check this function to avoid running into issues.
     *
     * @return True if this plugin was enabled successfully.
     */
    @SuppressWarnings("unused")
    public boolean isEnabledSuccessfully()
    {
        return isEnabled;
    }

    /**
     * Gets the number of the current build. Higher number == newer build. This can be used to check for feature
     * support, for example. Released builds do not use branches, so if feature x is supported in build y, feature x
     * will also be supported in build y+1, unless intentionally removed.
     *
     * @return The id of the current build.
     */
    @SuppressWarnings("unused")
    public int getBuild()
    {
        return buildNumber;
    }

    public void onPlayerLogout(final Player player)
    {
        WaitForCommand cw = getCommandWaiter(player);
        if (cw != null)
            cw.abortSilently();

        playerGUIs.remove(player.getUniqueId());
        ToolUser tu = getToolUser(player);
        if (tu != null)
            tu.abortSilently();
    }

    public CompletableFuture<@Nullable String> canBreakBlock(UUID playerUUID, String playerName, Location loc)
    {
        assertSchedulerRunning();
        return protCompatMan.canBreakBlock(playerUUID, playerName, loc)
            .exceptionally(throwable -> Util.exceptionally(throwable, "ERROR"));
    }

    public CompletableFuture<@Nullable String> canBreakBlocksBetweenLocs(
        UUID playerUUID, String playerName, World world, Location loc1, Location loc2)
    {
        assertSchedulerRunning();
        return protCompatMan.canBreakBlocksBetweenLocs(playerUUID, playerName, world, loc1, loc2)
            .exceptionally(throwable -> Util.exceptionally(throwable, "ERROR"));
    }

    public ProtectionCompatManager getProtectionCompatManager()
    {
        return protCompatMan;
    }

    public void restart()
    {
        if (!validVersion)
            return;
        reloadConfig();

        onDisable();
        protCompatMan.restart();
        playerGUIs.forEach((key, value) -> value.close());
        playerGUIs.clear();

        if (redstoneHandler != null)
        {
            HandlerList.unregisterAll(redstoneHandler);
            redstoneHandler = null;
        }
        if (rPackHandler != null)
        {
            HandlerList.unregisterAll(rPackHandler);
            rPackHandler = null;
        }

        init();

        vaultManager.init();
        pbCache.reinit(config.cacheTimeout());
    }

    @Override
    public void onDisable()
    {
        if (!validVersion)
            return;

        closeGUIs();

        // Stop all toolUsers and take all BigDoor tools from players.
        commander.setCanGo(false);
        commander.stopMovers(true);

        Iterator<Entry<UUID, ToolUser>> it = toolUsers.entrySet().iterator();
        while (it.hasNext())
        {
            Entry<UUID, ToolUser> entry = it.next();
            entry.getValue().abort();
        }

        toolUsers.clear();
        cmdWaiters.clear();
    }

    private void closeGUIs()
    {
        Iterator<UUID> it = playerGUIs.keySet().iterator();
        while (it.hasNext())
        {
            UUID uuid = it.next();
            Player player = Bukkit.getPlayer(uuid);
            if (player == null)
                continue;
            player.closeInventory();
        }
        playerGUIs.clear();
    }

    private void setupMetrics()
    {
        if (metrics != null)
            return;

        metrics = new Metrics(this, 2887);

        metrics.addCustomChart(new AdvancedPie("doors_per_type", () ->
        {
            DoorType[] doorTypes = DoorType.values();
            Map<String, Integer> output = new HashMap<>(doorTypes.length - 1);
            Map<DoorType, Integer> stats = db.getDatabaseStatistics();
            for (DoorType type : doorTypes)
                output.put(DoorType.getFriendlyName(type), stats.getOrDefault(type, 0));
            return output;
        }));
    }

    public static TaskScheduler getScheduler() {
        return scheduler;
    }

    public String getLoginMessage()
    {
        final StringBuilder sb = new StringBuilder();
        if (updateManager.updateAvailable())
        {
            if (getConfigLoader().autoDLUpdate() && updateManager.hasUpdateBeenDownloaded())
                sb.append("[BigDoors] A new update (").append(updateManager.getNewestVersion())
                  .append(") has been downloaded! ").append("Restart your server to apply the update!\n");
            else if (updateManager.updateAvailable())
                sb.append("[BigDoors] A new update is available: ").append(updateManager.getNewestVersion())
                  .append("\n");
        }
        if (failureCommandHandler != null)
            sb.append("[BigDoors] ").append(failureCommandHandler.getError()).append("\n");
        loginMessages.forEach(str -> sb.append("[BigDoors] ").append(str).append("\n"));
        return sb.toString();
    }

    public UpdateManager getUpdateManager()
    {
        return updateManager;
    }

    public TimedCache<Long, HashMap<Long, Long>> getPBCache()
    {
        return pbCache;
    }

    public FallingBlockFactory getFABF()
    {
        return fabf;
    }

    public BigDoors getPlugin()
    {
        return this;
    }

    public AutoCloseScheduler getAutoCloseScheduler()
    {
        return autoCloseScheduler;
    }

    public FakePlayerCreator getFakePlayerCreator()
    {
        return fakePlayerCreator;
    }

    public Opener getDoorOpener(DoorType type)
    {
        switch (type)
        {
            case DOOR:
                return doorOpener;
            case DRAWBRIDGE:
                return bridgeOpener;
            case PORTCULLIS:
                return portcullisOpener;
            case SLIDINGDOOR:
                return slidingDoorOpener;
            default:
                return null;
        }
    }

    public ToolUser getToolUser(Player player)
    {
        return toolUsers.get(player.getUniqueId());
    }

    public void addToolUser(ToolUser toolUser)
    {
        toolUsers.put(toolUser.getPlayer().getUniqueId(), toolUser);
    }

    public void removeToolUser(ToolUser toolUser)
    {
        toolUsers.remove(toolUser.getPlayer().getUniqueId());
    }

    public GUI getGUIUser(Player player)
    {
        return playerGUIs.get(player.getUniqueId());
    }

    public void addGUIUser(GUI gui)
    {
        playerGUIs.put(gui.getPlayer().getUniqueId(), gui);
    }

    public void removeGUIUser(GUI gui)
    {
        playerGUIs.remove(gui.getPlayer().getUniqueId());
    }

    public WaitForCommand getCommandWaiter(Player player)
    {
        return cmdWaiters.get(player.getUniqueId());
    }

    public void addCommandWaiter(WaitForCommand cmdWaiter)
    {
        cmdWaiters.put(cmdWaiter.getPlayer().getUniqueId(), cmdWaiter);
    }

    public void removeCommandWaiter(WaitForCommand cmdWaiter)
    {
        cmdWaiters.remove(cmdWaiter.getPlayer().getUniqueId());
    }

    // Get the command Handler.
    public CommandHandler getCommandHandler()
    {
        return commandHandler;
    }

    // Get the commander (class executing commands).
    public Commander getCommander()
    {
        return commander;
    }

    // Get the logger.
    public MyLogger getMyLogger()
    {
        return logger;
    }

    // Get the messages.
    public Messages getMessages()
    {
        return messages;
    }

    // Returns the config handler.
    public ConfigLoader getConfigLoader()
    {
        return config;
    }

    public VaultManager getVaultManager()
    {
        return vaultManager;
    }

    // Get the ToolVerifier.
    public ToolVerifier getTF()
    {
        return tf;
    }

    public String getLocale()
    {
        return locale == null ? "en_US" : locale;
    }

    private void readConfigValues()
    {
        // Load the settings from the config file.
        config = new ConfigLoader(this);
        locale = config.languageFile();

        if (config.unsafeMode())
        {
            logger.warn("╔═══════════════════════════════════════════════════════╗");
            logger.warn("║                                                       ║");
            logger.warn("║                    !!  WARNING  !!                    ║");
            logger.warn("║                                                       ║");
            logger.warn("║                                                       ║");
            logger.warn("║            You have enabled \"unsafe mode\"!            ║");
            logger.warn("║                                                       ║");
            logger.warn("║   THIS IS NOT SUPPORTED! USE THIS AT YOUR OWN RISK!   ║");
            logger.warn("║                                                       ║");
            logger.warn("╚═══════════════════════════════════════════════════════╝");
        }
    }

    public static boolean isOnFlattenedVersion()
    {
        return IS_ON_FLATTENED_VERSION;
    }

    // Check + initialize for the correct version of Minecraft.
    private @Nullable FallingBlockFactory createFallingBlockFactory()
        throws Exception
    {
        if (config.forceCodeGeneration())
            return FallbackGeneratorManager.getFallingBlockFactory();

        if (SERVER_VERSION.isLowerThan(Semver.of(1, 11, 0)))
        {
            logger.severe("This version of Minecraft is not supported. Is the plugin up-to-date?");
            return null;
        }

        switch (SERVER_VERSION.getMinor())
        {
            case 11:
                return new FallingBlockFactory_V1_11_R1();

            case 12:
                return new FallingBlockFactory_V1_12_R1();

            case 13:
                switch (SERVER_VERSION.getPatch())
                {
                    case 0:
                        return new FallingBlockFactory_V1_13_R1();
                    case 1:
                        return new FallingBlockFactory_V1_13_R1_5();
                    case 2:
                        return new FallingBlockFactory_V1_13_R2();
                    default:
                        logger.severe("Unexpected patch version '" + SERVER_VERSION.getPatch() + "' for 1.13!");
                        return null;
                }

            case 14:
                return new FallingBlockFactory_V1_14_R1();

            case 15:
                return new FallingBlockFactory_V1_15_R1();

            case 16:
                switch (SERVER_VERSION.getPatch())
                {
                    case 0:
                    case 1:
                        return new FallingBlockFactory_V1_16_R1();
                    case 2:
                    case 3:
                        return new FallingBlockFactory_V1_16_R2();
                    case 4:
                    case 5:
                        return new FallingBlockFactory_V1_16_R3();
                    default:
                        logger.severe("Unexpected patch version '" + SERVER_VERSION.getPatch() + "' for 1.16!");
                        return null;
                }

            case 17:
                return new FallingBlockFactory_V1_17_R1();

            case 18:
                switch (SERVER_VERSION.getPatch())
                {
                    case 0:
                    case 1:
                        return new FallingBlockFactory_V1_18_R1();
                    case 2:
                        return new FallingBlockFactory_V1_18_R2();
                    default:
                        logger.severe("Unexpected patch version '" + SERVER_VERSION.getPatch() + "' for 1.18!");
                        return null;
                }

            case 19:
                switch (SERVER_VERSION.getPatch())
                {
                    case 0:
                        return new FallingBlockFactory_V1_19_R1();
                    case 1:
                    case 2:
                        return new FallingBlockFactory_V1_19_R1_1();
                    case 3:
                        return new FallingBlockFactory_V1_19_R2();
                    case 4:
                        return new FallingBlockFactory_V1_19_R3();
                    default:
                        logger.severe("Unexpected patch version '" + SERVER_VERSION.getPatch() + "' for 1.19!");
                        return null;
                }

            case 20:
                switch (SERVER_VERSION.getPatch())
                {
                    case 0:
                    case 1:
                        return FallingBlockFactoryProvider_V1_20_R1.getFactory();
                    case 2:
                        return FallingBlockFactoryProvider_V1_20_R2.getFactory();
                    case 3:
                    case 4:
                        return FallingBlockFactoryProvider_V1_20_R3.getFactory();
                    case 5:
                    case 6:
                        return FallingBlockFactoryProvider_V1_20_R4.getFactory();
                    default:
                        logger.severe("Unexpected patch version '" + SERVER_VERSION.getPatch() + "' for 1.20!");
                        return null;
                }

            case 21:
                switch (SERVER_VERSION.getPatch())
                {
                    case 0:
                    case 1:
                        return FallingBlockFactoryProvider_V1_21_R1.getFactory();
                    case 2: // Untested, but likely to work and unlikely to matter.
                    case 3:
                        return FallingBlockFactoryProvider_V1_21_R2.getFactory();
                    case 4:
                        return FallingBlockFactoryProvider_V1_21_R3.getFactory();
                    case 5:
                        return FallingBlockFactoryProvider_V1_21_R4.getFactory();
                    case 6:
                    case 7:
                    case 8:
                        return FallingBlockFactoryProvider_V1_21_R5.getFactory();
                    case 9:
                    case 10:
                        return FallingBlockFactoryProvider_V1_21_R6.getFactory();
                }

            default:
                logger.severe("Unsupported version of Minecraft: " + SERVER_VERSION);
                if (config.allowCodeGeneration())
                    return FallbackGeneratorManager.getFallingBlockFactory();
                return null;
        }
    }

    private int readBuildNumber()
    {
        try (BufferedReader reader = new BufferedReader(
            new InputStreamReader(Objects.requireNonNull(getClass().getResourceAsStream("/build.number")))))
        {
            for (int idx = 0; idx != 2; ++idx)
                reader.readLine();
            return Integer.parseInt(reader.readLine().replace("build.number=", ""));
        }
        catch (Exception e)
        {
            return -1;
        }
    }

    private void overrideVersion()
    {
        try
        {
            String version = getDescription().getVersion() + " (b" + buildNumber + ")";
            final Field field = PluginDescriptionFile.class.getDeclaredField("version");
            field.setAccessible(true);
            field.set(getDescription(), version);
        }
        catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e)
        {
            getMyLogger().logMessage(Util.throwableToString(e), true, false);
        }
    }

    /*
     * API Starts here.
     */

    /**
     * Checks if all chunks a door could interact with if it were to be toggled right now. Depending on how much is
     * known about the door (e.g. open direction, blocksToMove) and its type, the result can be more or less reliable.
     *
     * @param door The door for which to check which chunks it could interact with.
     * @return True if all chunks the door could interact with are currently loaded.
     */
    @SuppressWarnings("unused")
    public boolean areChunksLoadedForDoor(Door door)
    {
        final Opener opener = getDoorOpener(door.getType());
        return opener != null && opener.chunksLoaded(door, ChunkLoadMode.VERIFY_LOADED) == ChunkLoadResult.PASS;
    }

    /**
     * Attempts to toggle a door.
     *
     * @param door
     *     The door to toggle. If it is currently open, it will be closed. If it is currently closed, it will be opened.
     * @param time
     *     The amount of time the animation should (try to) take to complete. This will not be the actual amount of time
     *     the full toggle will take because 1) some parts of the animation are not included in this timing value and 2)
     *     There are some limits that cannot be exceeded. Setting this value to 0 will result in the default time value
     *     being used.
     * @param instantOpen
     *     True if the door should be toggled instantly, skipping the animation and moving the blocks to their final
     *     position immediately.
     * @return The result of the toggle attempt. Note that this is likely to be executed on the main thread, so you must
     * not use get or join on it, or you will end up with a deadlock!
     */
    @SuppressWarnings("unused")
    public CompletableFuture<DoorOpenResult> toggleDoorFuture(Door door, double time, boolean instantOpen)
    {
        final Opener opener = getDoorOpener(door.getType());
        return opener == null ?
               CompletableFuture.completedFuture(DoorOpenResult.TYPEDISABLED) :
               opener.openDoorFuture(door, time, instantOpen);
    }

    private CompletableFuture<Boolean> toggleDoorFuture0(Door door, double time, boolean instantOpen)
    {
        return toggleDoorFuture(door, time, instantOpen).thenApply(result -> result == DoorOpenResult.SUCCESS);
    }
    /**
     * Attempts to toggle a door.
     *
     * @param doorUID
     *     The UID of the door to toggle. If it is currently open, it will be closed. If it is currently closed, it will
     *     be opened.
     * @param instantOpen
     *     True if the door should be toggled instantly, skipping the animation and moving the blocks to their final
     *     position immediately.
     * @return The result of the toggle attempt. Note that this is likely to be executed on the main thread, so you must
     * not use get or join on it, or you will end up with a deadlock!
     */
    @SuppressWarnings("unused")
    public CompletableFuture<Boolean> toggleDoorFuture(long doorUID, boolean instantOpen)
    {
        final Door door = getCommander().getDoor(null, doorUID);
        return toggleDoorFuture0(door, 0.0, instantOpen);
    }

    /**
     * Attempts to toggle a door.
     *
     * @param doorUID
     *     The UID of the door to toggle. If it is currently open, it will be closed. If it is currently closed, it will
     *     be opened.
     * @param time
     *     The amount of time the animation should (try to) take to complete. This will not be the actual amount of time
     *     the full toggle will take because 1) some parts of the animation are not included in this timing value and 2)
     *     There are some limits that cannot be exceeded. Setting this value to 0 will result in the default time value
     *     being used.
     * @return The result of the toggle attempt. Note that this is likely to be executed on the main thread, so you must
     * not use get or join on it, or you will end up with a deadlock!
     */
    @SuppressWarnings("unused")
    public CompletableFuture<Boolean> toggleDoorFuture(long doorUID, double time)
    {
        return toggleDoorFuture0(getCommander().getDoor(null, doorUID), time, false);
    }

    /**
     * Attempts to toggle a door.
     *
     * @param doorUID
     *     The UID of the door to toggle. If it is currently open, it will be closed. If it is currently closed, it will
     *     be opened.
     * @return The result of the toggle attempt. Note that this is likely to be executed on the main thread, so you must
     * not use get or join on it, or you will end up with a deadlock!
     */
    @SuppressWarnings("unused")
    public CompletableFuture<Boolean> toggleDoorFuture(long doorUID)
    {
        return toggleDoorFuture0(getCommander().getDoor(null, doorUID), 0.0, false);
    }

    /**
     * @deprecated Use {@link #toggleDoorFuture(Door, double, boolean)} instead.
     * </p>
     * This method may return {@link DoorOpenResult#ERROR} even when the eventual result will be a success.
     */
    @Deprecated
    public DoorOpenResult toggleDoor(Door door, double time, boolean instantOpen)
    {
        Opener opener = getDoorOpener(door.getType());
        return opener == null ? DoorOpenResult.TYPEDISABLED :
               Opener.processFutureResult(opener.openDoorFuture(door, time, instantOpen));
    }

    /**
     * @deprecated Use {@link #toggleDoorFuture(long, boolean)} instead.
     * </p>
     * This method may return false even when the eventual result will be a success.
     */
    @Deprecated
    public boolean toggleDoor(long doorUID, boolean instantOpen)
    {
        final Door door = getCommander().getDoor(null, doorUID);
        return toggleDoor(door, 0.0, instantOpen) == DoorOpenResult.SUCCESS;
    }

    /**
     * @deprecated Use {@link #toggleDoorFuture(long, double)} instead.
     * </p>
     * This method may return false even when the eventual result will be a success.
     */
    @Deprecated
    public boolean toggleDoor(long doorUID, double time)
    {
        final Door door = getCommander().getDoor(null, doorUID);
        return toggleDoor(door, time, false) == DoorOpenResult.SUCCESS;
    }

    /**
     * @deprecated Use {@link #toggleDoorFuture(long)} instead.
     * </p>
     * This method may return false even when the eventual result will be a success.
     */
    @Deprecated
    public boolean toggleDoor(long doorUID)
    {
        final Door door = getCommander().getDoor(null, doorUID);
        return toggleDoor(door, 0.0, false) == DoorOpenResult.SUCCESS;
    }

    /**
     * Checks if a given entity is a BigDoors entity.
     *
     * @param entity The entity to check.
     * @return True if the entity is a BigDoors entity.
     *
     * @throws RuntimeException If the plugin is not (correctly) enabled.
     * You can use {@link #isEnabledSuccessfully()} to ensure the plugin is enabled correctly.
     */
    public boolean isBigDoorsEntity(@Nullable Entity entity)
    {
        if (fabf == null)
            throw new RuntimeException("Falling Block Factory is null! The plugin is likely not enabled!");
        return fabf.isBigDoorsEntity(entity);
    }

    // Check the open-status of a door.
    private boolean isOpen(Door door)
    {
        return door.isOpen();
    }

    // Check the open-status of a door from a doorUID.
    @SuppressWarnings("unused")
    public boolean isOpen(long doorUID)
    {
        final Door door = getCommander().getDoor(null, doorUID);
        return this.isOpen(door);
    }

    @SuppressWarnings("unused")
    public int getMinimumDoorDelay()
    {
        return MINIMUMDOORDELAY;
    }

    private void initLegacyMaterials()
    {
        try
        {
            Class.forName(BukkitReflectionUtil.CRAFT_BASE + "legacy.CraftLegacy");
        }
        catch (ClassNotFoundException e)
        {
            // ignore
        }
    }

    public RedstoneHandler getRedstoneHandler()
    {
        return redstoneHandler;
    }
}
