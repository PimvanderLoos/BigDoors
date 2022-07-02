package nl.pim16aap2.bigdoors.spigot.managers;

import lombok.extern.flogger.Flogger;
import nl.pim16aap2.bigdoors.api.restartable.Restartable;
import nl.pim16aap2.bigdoors.api.restartable.RestartableHolder;
import nl.pim16aap2.bigdoors.data.cache.timed.TimedCache;
import nl.pim16aap2.bigdoors.spigot.config.ConfigLoaderSpigot;
import nl.pim16aap2.bigdoors.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.time.Duration;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Represents a manager of player heads with the texture of a certain player.
 *
 * @author Pim
 */
@Singleton @Flogger
public final class HeadManager extends Restartable
{
    private static final ItemStack FALLBACK = new ItemStack(Material.PLAYER_HEAD);

    /**
     * Timed cache of player heads.
     * <p>
     * Key: The player's UUID.
     * <p>
     * Value: The player's head as item.
     */
    private TimedCache<UUID, ItemStack> headMap;
    private final ConfigLoaderSpigot config;

    /**
     * Constructs a new {@link HeadManager}.
     *
     * @param holder
     *     The {@link RestartableHolder} that manages this object.
     * @param config
     *     The BigDoors configuration.
     */
    @Inject
    public HeadManager(RestartableHolder holder, ConfigLoaderSpigot config)
    {
        super(holder);
        this.config = config;
        headMap = TimedCache.emptyCache();
    }

    /**
     * Requests the ItemStack of a head with the texture of the player's head. This is done asynchronously because it
     * can take quite a bit of time.
     *
     * @param uuid
     *     The {@link UUID} of the player whose head to get.
     * @param displayName
     *     The display name to give assign to the {@link ItemStack}.
     * @return The ItemStack of a head with the texture of the player's head if possible.
     */
    @SuppressWarnings("unused")
    public CompletableFuture<ItemStack> getPlayerHead(UUID uuid, String displayName)
    {
        final Optional<ItemStack> head = headMap.get(uuid);
        return head.map(CompletableFuture::completedFuture)
                   .orElseGet(() -> CompletableFuture
                       .supplyAsync(() -> Bukkit.getOfflinePlayer(uuid))
                       .thenApplyAsync(offlinePlayer -> headMap.put(uuid, createItemStack(offlinePlayer, displayName)))
                       .exceptionally(t -> Util.exceptionally(t, FALLBACK)));

    }

    private ItemStack createItemStack(OfflinePlayer offlinePlayer, String displayName)
    {
        final ItemStack skull = new ItemStack(Material.PLAYER_HEAD, 1);
        final SkullMeta sMeta = Util.requireNonNull((SkullMeta) skull.getItemMeta(), "SkullMeta");
        sMeta.setOwningPlayer(offlinePlayer);
        sMeta.setDisplayName(displayName);
        skull.setItemMeta(sMeta);
        return skull;
    }

    @Override
    public void initialize()
    {
        headMap = TimedCache.<UUID, ItemStack>builder()
                            .duration(Duration.ofMinutes(config.headCacheTimeout()))
                            .cleanup(Duration.ofMinutes(Math.max(1, config.headCacheTimeout())))
                            .softReference(true).build();
    }

    @Override
    public void shutDown()
    {
        headMap.shutDown();
        headMap = TimedCache.emptyCache();
    }
}
