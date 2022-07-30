package nl.pim16aap2.bigDoors.compatibility;

import nl.pim16aap2.bigDoors.BigDoors;
import nl.pim16aap2.bigDoors.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
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
    private final ArrayList<IProtectionCompat> protectionCompats;
    private final FakePlayerCreator fakePlayerCreator;
    private final BigDoors plugin;

    /**
     * Constructor of {@link ProtectionCompatManager}.
     *
     * @param plugin The instance of {@link BigDoors}.
     */
    public ProtectionCompatManager(final BigDoors plugin)
    {
        this.plugin = plugin;
        registeredDefinitions = registerDefaultProtectionCompatDefinitions();
        fakePlayerCreator = plugin.getFakePlayerCreator();
        protectionCompats = new ArrayList<>();
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
     * Check if a player is allowed to bypass the compatibility checks. Players can
     * bypass the check if they are OP or if they have the
     * {@link ProtectionCompatManager#BYPASS_PERMISSION} permission node.
     *
     * @param player The {@link Player} to check the permissions for.
     * @return True if the player can bypass the checks.
     */
    private boolean canByPass(Player player)
    {
        if (player.isOp())
            return true;

        // offline players don't have permissions, so use Vault if that's the case.
        if (!player.hasMetadata(FakePlayerCreator.FAKE_PLAYER_METADATA))
            return player.hasPermission(BYPASS_PERMISSION);
        return offlinePlayerHasPermission(player, player.getWorld().getName());
    }

    /**
     * Circuitous way of checking if an offline player has a given permission in a
     * given world or not.
     *
     * @param oPlayer The offline player to check.
     * @param world   The world to check the permission in.
     * @return The if the player has the bypass permission node in this world.
     */
    private boolean offlinePlayerHasPermission(OfflinePlayer oPlayer, String world)
    {
        try
        {
            CompletableFuture<Boolean> future = CompletableFuture
                .supplyAsync(() -> plugin.getVaultManager().hasPermission(oPlayer, BYPASS_PERMISSION, world));

            return future.get();
        }
        catch (InterruptedException | ExecutionException e)
        {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Get an online player from a player {@link UUID} in a given world. If the
     * player with the given UUID is not online, a fake-online player is created.
     *
     * @param playerUUID The {@link UUID} of the player to get.
     * @param playerName The name of the player. Used in case the player isn't
     *                   online.
     * @param location   The {@link Location} of the player.
     * @return An online {@link Player}. Either fake or real.
     * @see FakePlayerCreator
     */
    private @Nullable Player getPlayer(UUID playerUUID, String playerName, Location location)
    {
        Player player = Bukkit.getPlayer(playerUUID);
        if (player == null)
            player = fakePlayerCreator.getFakePlayer(Bukkit.getOfflinePlayer(playerUUID), playerName, location);
        return player;
    }

    /**
     * Check if a player can break a block at a given location.
     *
     * @param playerUUID The {@link UUID} of the player to check for.
     * @param playerName The name of the player. Used in case the player isn't
     *                   online.
     * @param loc        The {@link Location} to check.
     * @return The name of the {@link IProtectionCompat} that objects, if any, or
     *         null if allowed by all compats.
     */
    public String canBreakBlock(UUID playerUUID, String playerName, Location loc)
    {
        if (protectionCompats.isEmpty())
            return null;

        final @Nullable Player fakePlayer = getPlayer(playerUUID, playerName, loc);
        if (fakePlayer == null)
            return "InvalidFakePlayer";

        if (canByPass(fakePlayer))
            return null;

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

    /**
     * Check if a player can break all blocks between two locations.
     *
     * @param playerUUID The {@link UUID} of the player to check for.
     * @param playerName The name of the player. Used in case the player isn't
     *                   online.
     * @param loc1       The start {@link Location} to check.
     * @param loc2       The end {@link Location} to check.
     * @return The name of the {@link IProtectionCompat} that objects, if any, or null if allowed by all compats.
     */
    public String canBreakBlocksBetweenLocs(UUID playerUUID, String playerName, World world, Location loc1,
                                            Location loc2)
    {
        if (protectionCompats.isEmpty())
            return null;

        final @Nullable Player fakePlayer = getPlayer(playerUUID, playerName, loc1);
        if (fakePlayer == null)
            return "InvalidFakePlayer";

        if (canByPass(fakePlayer))
            return null;
        if (protectionCompats.isEmpty())
            return null;

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

    /**
     * Check if an {@link IProtectionCompat} is already loaded.
     *
     * @param compatClass The class of the {@link IProtectionCompat} to check.
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
     * Add a {@link IProtectionCompat} to the list of loaded compats if it loaded
     * successfully.
     *
     * @param hook The compat to add.
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
     * @param event The event of the plugin that is loaded.
     */
    @EventHandler
    protected void onPluginEnable(final PluginEnableEvent event)
    {
        loadFromPluginName(event.getPlugin().getName());
    }

    /**
     * Load a compat for a plugin with a given name if allowed and possible.
     *
     * @param compatName The name of the plugin to load a compat for.
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
