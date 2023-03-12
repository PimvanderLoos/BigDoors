package nl.pim16aap2.bigDoors.compatibility;

import nl.pim16aap2.bigDoors.BigDoors;
import nl.pim16aap2.bigDoors.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;
import java.util.logging.Level;

/**
 * Class that manages all objects of {@link IProtectionCompat}.
 *
 * @author Pim
 */
public class ProtectionCompatManager implements Listener
{
    private static final String BYPASS_PERMISSION = "bigdoors.admin.bypasscompat";

    private final Map<String, IProtectionCompatDefinition> registeredDefinitions;
    private final List<IProtectionCompat> protectionCompats;
    private final FakePlayerCreator fakePlayerCreator;
    private final BigDoors plugin;

    /**
     * Constructor of {@link ProtectionCompatManager}.
     *
     * @param plugin
     *     The instance of {@link BigDoors}.
     */
    public ProtectionCompatManager(final BigDoors plugin)
    {
        this.plugin = plugin;
        registeredDefinitions = registerDefaultProtectionCompatDefinitions();
        fakePlayerCreator = plugin.getFakePlayerCreator();
        protectionCompats = new CopyOnWriteArrayList<>();
        restart();
    }

    /**
     * {@inheritDoc}
     * <p>
     * Reinitialize all protection compats.
     */
    public void restart()
    {
        protectionCompats.clear();
        for (Plugin p : plugin.getServer().getPluginManager().getPlugins())
            loadFromPluginName(p.getName());
    }

    /**
     * Check if a player is allowed to bypass the compatibility checks. Players can bypass the check if they are OP or
     * if they have the {@link ProtectionCompatManager#BYPASS_PERMISSION} permission node.
     *
     * @param player
     *     The {@link Player} to check the permissions for.
     * @return True if the player can bypass the checks.
     */
    private CompletableFuture<Boolean> canBypass(Player player)
    {
        if (player.isOp())
            return CompletableFuture.completedFuture(true);

        // Real players do not have the FAKE_PLAYER tag, so we can use the real permissions check for them.
        if (!player.hasMetadata(FakePlayerCreator.FAKE_PLAYER_METADATA))
            return CompletableFuture.completedFuture(player.hasPermission(BYPASS_PERMISSION));

        // offline players don't have permissions, so use Vault to read those.
        return plugin.getVaultManager().hasPermission(player, BYPASS_PERMISSION, player.getWorld().getName());
    }

    /**
     * Get an online player from a player {@link UUID} in a given world. If the player with the given UUID is not
     * online, a fake-online player is created.
     *
     * @param playerUUID
     *     The {@link UUID} of the player to get.
     * @param playerName
     *     The name of the player. Used in case the player isn't online.
     * @param world
     *     The {@link World} the player is in.
     * @return An online {@link Player}. Either fake or real.
     *
     * @see FakePlayerCreator
     */
    private @Nullable Player getPlayer(UUID playerUUID, String playerName, World world)
    {
        final Player player = Bukkit.getPlayer(playerUUID);
        if (player != null)
            return player;

        return fakePlayerCreator.getFakePlayer(Bukkit.getOfflinePlayer(playerUUID), playerName, world);
    }

    // Needs to be called from the main thread!
    private @Nullable String canBreakBlockSync(Player fakePlayer, Location loc)
    {
        for (IProtectionCompat compat : protectionCompats)
            try
            {
                if (!compat.canBreakBlock(fakePlayer, loc))
                    return compat.getName();
            }
            catch (Exception e)
            {
                plugin.getMyLogger()
                      .warn("Failed to use \"" + compat.getName() + "\"! Please send this error to pim16aap2:");
                e.printStackTrace();
                plugin.getMyLogger().logMessageToLogFile(compat.getName() + "\n" + Util.throwableToString(e));
            }
        return null;
    }

    // Needs to be called from the main thread!
    private @Nullable String canBreakBlocksBetweenLocsSync(Player fakePlayer, World world, Location loc1, Location loc2)
    {
        loc1 = loc1.clone();
        loc2 = loc2.clone();

        loc1.setWorld(world);
        loc2.setWorld(world);

        for (IProtectionCompat compat : protectionCompats)
        {
            try
            {
                if (!compat.canBreakBlocksBetweenLocs(fakePlayer, loc1, loc2))
                    return compat.getName();
            }
            catch (Exception e)
            {
                plugin.getMyLogger()
                      .warn("Failed to use \"" + compat.getName() + "\"! Please send this error to pim16aap2:");
                e.printStackTrace();
                plugin.getMyLogger().logMessageToLogFile(Util.throwableToString(e));
            }
        }
        return null;
    }

    private CompletableFuture<@Nullable String> checkForPlayer(Player fakePlayer, Callable<@Nullable String> callable)
    {
        return canBypass(fakePlayer).thenApplyAsync(
            canBypass ->
            {
                if (canBypass)
                    return null;

                try
                {
                    return Bukkit
                        .getScheduler()
                        .callSyncMethod(plugin, callable)
                        .get(1, TimeUnit.SECONDS);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                }
                catch (TimeoutException e)
                {
                    plugin.getMyLogger().log("Timed out checking permissions for offline player: " + fakePlayer, e);
                }
                catch (Exception e)
                {
                    plugin.getMyLogger().log("Failed to check permissions for offline player: " + fakePlayer, e);
                }
                return "ERROR";
            });
    }

    public int registeredCompatsCount()
    {
        return protectionCompats.size();
    }

    /**
     * Checks something for a player. This can be used to, for example, check if a (fake) player has access to a
     * location.
     *
     * @param playerUUID
     *     The UUID of the player.
     * @param playerName
     *     The name of the player.
     * @param world
     *     The world in which to check the function.
     * @param function
     *     A function that accept a player and returns a String or null. The player argument may either be a real player
     *     or a fake player created by {@link #fakePlayerCreator}.
     * @return The future result of the function. If no hooks are registered with {@link #protectionCompats} the future
     * will always contain 'null'. If at least one hook fails the test for the provided user, the name of that hook will
     * be returned.
     */
    private CompletableFuture<@Nullable String> checkForPlayer(
        UUID playerUUID, String playerName, World world, Function<Player, @Nullable String> function)
    {
        if (protectionCompats.isEmpty())
            return CompletableFuture.completedFuture(null);

        final @Nullable Player fakePlayer = getPlayer(playerUUID, playerName, world);
        if (fakePlayer == null)
            return CompletableFuture.completedFuture("InvalidFakePlayer");

        return checkForPlayer(fakePlayer, () -> function.apply(fakePlayer));
    }

    /**
     * Check if a player can break a block at a given location.
     *
     * @param playerUUID
     *     The {@link UUID} of the player to check for.
     * @param playerName
     *     The name of the player. Used in case the player isn't online.
     * @param loc
     *     The {@link Location} to check.
     * @return The name of the {@link IProtectionCompat} that objects, if any, or null if allowed by all compats.
     */
    public CompletableFuture<@Nullable String> canBreakBlock(UUID playerUUID, String playerName, Location loc)
    {
        return checkForPlayer(
            playerUUID, playerName, loc.getWorld(), fakePlayer -> canBreakBlockSync(fakePlayer, loc));
    }

    /**
     * Check if a player can break all blocks between two locations.
     *
     * @param playerUUID
     *     The {@link UUID} of the player to check for.
     * @param playerName
     *     The name of the player. Used in case the player isn't online.
     * @param loc1
     *     The start {@link Location} to check.
     * @param loc2
     *     The end {@link Location} to check.
     * @return The name of the {@link IProtectionCompat} that objects, if any, or null if allowed by all compats.
     */
    public CompletableFuture<@Nullable String> canBreakBlocksBetweenLocs(
        UUID playerUUID, String playerName, World world, Location loc1, Location loc2)
    {
        return checkForPlayer(
            playerUUID, playerName, world, fakePlayer -> canBreakBlocksBetweenLocsSync(fakePlayer, world, loc1, loc2));
    }

    /**
     * Check if an {@link IProtectionCompat} is already loaded.
     *
     * @param compatClass
     *     The class of the {@link IProtectionCompat} to check.
     * @return True if the compat has already been loaded.
     */
    private boolean protectionAlreadyLoaded(Class<? extends IProtectionCompat> compatClass)
    {
        for (IProtectionCompat compat : protectionCompats)
            if (compat.getClass().equals(compatClass))
                return true;
        return false;
    }

    /**
     * Add a {@link IProtectionCompat} to the list of loaded compats if it loaded successfully.
     *
     * @param hook
     *     The compat to add.
     */
    private void addProtectionCompat(IProtectionCompat hook)
    {
        if (hook.success())
        {
            protectionCompats.add(hook);
            plugin.getMyLogger().logMessage(Level.INFO, "Successfully hooked into \"" + hook.getName() + "\"!");
        }
        else
            plugin.getMyLogger().logMessage(Level.INFO, "Failed to hook into \"" + hook.getName() + "\"!");
    }

    /**
     * Load a compat for the plugin enabled in the event if needed.
     *
     * @param event
     *     The event of the plugin that is loaded.
     */
    @EventHandler
    protected void onPluginEnable(final PluginEnableEvent event)
    {
        loadFromPluginName(event.getPlugin().getName());
    }

    /**
     * Load a compat for a plugin with a given name if allowed and possible.
     *
     * @param compatName
     *     The name of the plugin to load a compat for.
     */
    private void loadFromPluginName(final String compatName)
    {
        final @Nullable IProtectionCompatDefinition compatDefinition = registeredDefinitions.get(compatName);
        if (compatDefinition == null)
            return;

        if (!plugin.getConfigLoader().isHookEnabled(compatDefinition))
            return;

        @Nullable String version = null;
        try
        {
            version = plugin.getServer().getPluginManager()
                            .getPlugin(compatDefinition.getName()).getDescription().getVersion();

            final Class<? extends IProtectionCompat> compatClass =
                Objects.requireNonNull(compatDefinition.getClass(version), "Compat class cannot be null!");

            // No need to load compats twice.
            if (protectionAlreadyLoaded(compatClass))
                return;

            final HookContext hookContext = new HookContext(plugin, compatDefinition, plugin.getVaultManager());

            addProtectionCompat(compatClass.getConstructor(HookContext.class).newInstance(hookContext));
        }
        catch (NoClassDefFoundError | Exception e)
        {
            plugin.getMyLogger().logMessageToConsole("Failed to initialize the \"" + compatName + "\"" +
                                                         " (version \"" + version + "\") compatibility hook!");
            plugin.getMyLogger().logMessageToConsole("Now resuming normal startup with the \"" + compatName + "\"" +
                                                         " Compatibility Hook disabled!");
            plugin.getMyLogger().logMessage(Util.throwableToString(e), true, true);
        }
    }

    private Map<String, IProtectionCompatDefinition> registerDefaultProtectionCompatDefinitions()
    {
        final Map<String, IProtectionCompatDefinition> ret =
            new HashMap<>(ProtectionCompatDefinition.DEFAULT_COMPAT_DEFINITIONS.size());
        for (IProtectionCompatDefinition compatDefinition : ProtectionCompatDefinition.DEFAULT_COMPAT_DEFINITIONS)
            ret.put(compatDefinition.getName(), compatDefinition);
        return ret;
    }

    @SuppressWarnings("unused")
    public void registerProtectionCompatDefinition(IProtectionCompatDefinition compatDefinition)
    {
        registeredDefinitions.put(compatDefinition.getName(), compatDefinition);
    }
}
