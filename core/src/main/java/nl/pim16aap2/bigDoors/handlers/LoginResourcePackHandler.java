package nl.pim16aap2.bigDoors.handlers;

import nl.pim16aap2.bigDoors.BigDoors;
import nl.pim16aap2.bigDoors.util.ResourcePackDetails;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.Nullable;

public class LoginResourcePackHandler implements Listener
{
    private final BigDoors plugin;
    private final @Nullable ResourcePackDetails resourcePackDetails;

    public LoginResourcePackHandler(BigDoors plugin, @Nullable ResourcePackDetails resourcePackDetails)
    {
        this.plugin = plugin;
        this.resourcePackDetails = resourcePackDetails;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        if (resourcePackDetails == null)
        {
            plugin.getMyLogger().warn("No resource pack set! Please contact pim16aap2!");
            return;
        }

        if (resourcePackDetails.getHash().length != 20)
            event.getPlayer().setResourcePack(resourcePackDetails.getUrl());
        else
            event.getPlayer().setResourcePack(resourcePackDetails.getUrl(), resourcePackDetails.getHash());

    }
}
