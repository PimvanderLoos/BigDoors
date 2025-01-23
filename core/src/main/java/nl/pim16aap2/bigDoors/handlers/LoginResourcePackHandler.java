package nl.pim16aap2.bigDoors.handlers;

import nl.pim16aap2.bigDoors.BigDoors;
import nl.pim16aap2.bigDoors.util.ResourcePackDetails;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.UUID;
import java.util.function.BiConsumer;

public class LoginResourcePackHandler implements Listener
{
    private static final Method METHOD_ADD_RESOURCE_PACK = getMethodAddResourcePack();
    private static final UUID RESOURCE_PACK_ID = UUID.fromString("54e9104d-de52-4b46-b594-4ffc13a8c4af");

    private final BigDoors plugin;
    private final @Nullable ResourcePackDetails resourcePackDetails;
    private final BiConsumer<@NotNull Player, @NotNull ResourcePackDetails> resourcePackSender;

    public LoginResourcePackHandler(BigDoors plugin, @Nullable ResourcePackDetails resourcePackDetails)
    {
        this.plugin = plugin;
        this.resourcePackDetails = resourcePackDetails;
        this.resourcePackSender = METHOD_ADD_RESOURCE_PACK == null ? this::sendResourcePack : this::addResourcePack;

        System.out.println("Found method: " + METHOD_ADD_RESOURCE_PACK);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        if (resourcePackDetails == null)
        {
            plugin.getMyLogger().warn("No resource pack set! Please contact pim16aap2!");
            return;
        }

        resourcePackSender.accept(event.getPlayer(), resourcePackDetails);
    }

    private void addResourcePack(@NotNull Player player, @NotNull ResourcePackDetails resourcePackDetails)
    {
        try
        {
            Objects.requireNonNull(METHOD_ADD_RESOURCE_PACK).invoke(
                player,
                RESOURCE_PACK_ID,
                resourcePackDetails.getUrl(),
                resourcePackDetails.getHash(),
                "",
                false
            );
        }
        catch (Exception exception)
        {
            plugin.getMyLogger().log("Failed to add resource pack to player!", exception);
        }
    }

    private void sendResourcePack(@NotNull Player player, @NotNull ResourcePackDetails resourcePackDetails)
    {
        System.out.println("Sending resource pack to player: " + player.getName() + " with pack: " + resourcePackDetails.name());
        if (resourcePackDetails.getHash().length != 20)
            player.setResourcePack(resourcePackDetails.getUrl());
        else
            player.setResourcePack(resourcePackDetails.getUrl(), resourcePackDetails.getHash());
    }

    private static @Nullable Method getMethodAddResourcePack()
    {
        @Nullable Method ret = null;
        try
        {
            ret = Player.class.getMethod(
                "addResourcePack",
                UUID.class,
                String.class,
                byte[].class,
                String.class,
                boolean.class
            );
        }
        catch (NoSuchMethodException e)
        {
            // Ignore; This method does not exist on legacy versions.
        }
        return ret;
    }
}
