package nl.pim16aap2.bigdoors.spigot.factories.pplayerfactory;

import nl.pim16aap2.bigdoors.api.IPPlayer;
import nl.pim16aap2.bigdoors.api.PPlayerData;
import nl.pim16aap2.bigdoors.api.factories.IPPlayerFactory;
import nl.pim16aap2.bigdoors.managers.DatabaseManager;
import nl.pim16aap2.bigdoors.spigot.util.implementations.OfflinePPlayerSpigot;
import nl.pim16aap2.bigdoors.spigot.util.implementations.PPlayerSpigot;
import nl.pim16aap2.bigdoors.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * Represents an implementation of {@link IPPlayerFactory} for the Spigot platform.
 *
 * @author Pim
 */
@Singleton
public class PPlayerFactorySpigot implements IPPlayerFactory
{
    private final DatabaseManager databaseManager;

    @Inject
    public PPlayerFactorySpigot(DatabaseManager databaseManager)
    {
        this.databaseManager = databaseManager;
    }

    @Override
    public CompletableFuture<IPPlayer> create(PPlayerData playerData)
    {
        final @Nullable Player onlinePlayer = Bukkit.getPlayer(playerData.getUUID());
        if (onlinePlayer != null)
            CompletableFuture.completedFuture(new PPlayerSpigot(onlinePlayer));
        return OfflinePPlayerSpigot.of(playerData).thenApply(Function.identity());
    }

    @Override
    public @Nullable IPPlayer wrapOnlinePlayer(UUID uuid)
    {
        final @Nullable Player player = Bukkit.getPlayer(uuid);
        return player == null ? null : new PPlayerSpigot(player);
    }

    @Override
    public CompletableFuture<Optional<IPPlayer>> create(UUID uuid)
    {
        final @Nullable Player player = Bukkit.getPlayer(uuid);
        if (player != null)
            return CompletableFuture.completedFuture(Optional.of(new PPlayerSpigot(player)));

        return databaseManager.getPlayerData(uuid).thenCompose(
            playerData ->
            {
                final CompletableFuture<Optional<IPPlayer>> result;
                if (playerData.isPresent())
                    result = OfflinePPlayerSpigot.of(playerData.get()).thenCompose(
                        offlinePlayer -> CompletableFuture.completedFuture(Optional.of(offlinePlayer)));
                else
                    result = CompletableFuture.completedFuture(Optional.empty());
                return result;
            }
        ).exceptionally(Util::exceptionallyOptional);
    }
}
