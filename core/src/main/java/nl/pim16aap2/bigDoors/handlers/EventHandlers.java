package nl.pim16aap2.bigDoors.handlers;

import nl.pim16aap2.bigDoors.BigDoors;
import nl.pim16aap2.bigDoors.Door;
import nl.pim16aap2.bigDoors.NMS.CustomCraftFallingBlock;
import nl.pim16aap2.bigDoors.events.DoorEventTogglePrepare;
import nl.pim16aap2.bigDoors.events.DoorEventToggleStart;
import nl.pim16aap2.bigDoors.toolUsers.ToolUser;
import nl.pim16aap2.bigDoors.util.ILoggableDoor;
import org.bukkit.Bukkit;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityEnterBlockEvent;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.event.entity.EntityResurrectEvent;
import org.bukkit.event.entity.EntityTransformEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.Nullable;

import java.text.MessageFormat;

public class EventHandlers implements Listener
{
    private final BigDoors plugin;

    public EventHandlers(BigDoors plugin)
    {
        this.plugin = plugin;
    }

    // Selection event.
    @EventHandler
    public void onLeftClick(PlayerInteractEvent event)
    {
        if (event.getAction() == Action.LEFT_CLICK_BLOCK)
            if (plugin.getTF().isTool(event.getPlayer().getItemInHand()))
            {
                ToolUser tu = plugin.getToolUser(event.getPlayer());
                if (tu != null)
                {
                    tu.selector(event.getClickedBlock().getLocation());
                    event.setCancelled(true);
                    return;
                }
            }
    }

    @EventHandler
    public void onDoorTogglePrepare(DoorEventTogglePrepare event)
    {
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockChange(EntityChangeBlockEvent event)
    {
        if (!(event.getEntity() instanceof FallingBlock))
            return;

        plugin.getMyLogger().logMessageToLogFileForDoor(getDoorFromEvent(event), String.format(
            "Falling Block  [%s]: Changed block from '%s' to '%s'",
            event.getEntity().getUniqueId(),
            event.getBlock(),
            event.getTo()
        ));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDeath(EntityDeathEvent event)
    {
        handleGeneralEntityEvent(event);
    }

    private @Nullable ILoggableDoor getDoorFromEvent(EntityEvent event)
    {
        if (!(event.getEntity() instanceof CustomCraftFallingBlock))
            return null;

        return ((CustomCraftFallingBlock) event.getEntity()).getDoor();
    }

    private void handleGeneralEntityEvent(EntityEvent event)
    {
        if (!(event.getEntity() instanceof FallingBlock))
            return;

        plugin.getMyLogger().logMessageToLogFileForDoor(getDoorFromEvent(event), String.format(
            "Falling Block  [%s]: Received event: '%s'",
            event.getEntity().getUniqueId(),
            event.getEventName()
        ));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityTransform(EntityTransformEvent event)
    {
        if (!(event.getEntity() instanceof FallingBlock))
            return;

        plugin.getMyLogger().logMessageToLogFileForDoor(getDoorFromEvent(event), String.format(
            "Falling Block  [%s]: Transformed! Converted entity '%s', List: %s, Reason: '%s'",
            event.getEntity().getUniqueId(),
            event.getTransformedEntity(),
            event.getTransformedEntities(),
            event.getTransformReason()
        ));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityEnterBlock(EntityEnterBlockEvent event)
    {
        handleGeneralEntityEvent(event);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityResurrect(EntityResurrectEvent event)
    {
        handleGeneralEntityEvent(event);
    }



    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDoorToggleStart(DoorEventToggleStart event)
    {
        if (!plugin.getConfigLoader().allowNotifications())
            return;
        Door door = event.getDoor();
        if (!door.notificationEnabled())
            return;
        Player player = Bukkit.getPlayer(door.getPrimeOwner());
        if (player == null)
            return;
        if (!player.hasPermission("bigdoors.user.receivenotifications") && !player.isOp())
            return;

        player.sendMessage(MessageFormat.format(plugin.getMessages().getString("GENERAL.DoorToggleNotification"),
                                                door.toSimpleString()));
    }

    @EventHandler
    public void onLogin(PlayerLoginEvent event)
    {
        plugin.getCommander().updatePlayer(event.getPlayer());
    }

    @EventHandler
    public void onLogout(PlayerQuitEvent event)
    {
        plugin.getCommander().removePlayer(event.getPlayer());
        plugin.onPlayerLogout(event.getPlayer());
    }

    // Do not allow the player to drop the door creation tool.
    // If they aren't even a
    @EventHandler
    public void onItemDropEvent(PlayerDropItemEvent event)
    {
        if (plugin.getTF().isTool(event.getItemDrop().getItemStack()))
        {
            ToolUser tu = plugin.getToolUser(event.getPlayer());
            if (tu == null)
                event.getItemDrop().remove();
            else
                event.setCancelled(true);
        }
    }

    // Do not allow players to transfer selection tools to other inventories
    // such as chests.
    @EventHandler
    public void inventoryClickEvent(InventoryClickEvent event)
    {
        if (!plugin.getTF().isTool(event.getCurrentItem()))
            return;
        if (event.getAction().equals(InventoryAction.MOVE_TO_OTHER_INVENTORY) ||
            !event.getClickedInventory().getType().equals(InventoryType.PLAYER))
        {
            if (event.getWhoClicked() instanceof Player)
            {
                ToolUser tu = plugin.getToolUser((Player) event.getWhoClicked());
                if (tu == null)
                    event.getInventory().removeItem(event.getCurrentItem());
                else
                    event.setCancelled(true);
            }
            event.setCancelled(true);
        }
    }

    // Do not allow players to transfer selection tools to other inventories
    // such as chests.
    @EventHandler
    public void inventoryDragEvent(InventoryDragEvent event)
    {
        event.getNewItems().forEach((K, V) ->
        {
            if (plugin.getTF().isTool(V))
                event.setCancelled(true);
        });
    }

    // Do not allow moving selection tools
    // If a player is trying to move it, cancel the move if they need the tool
    // (i.e. they're a toolUser), or delete it if not. If they somehow managed to
    // put it in a hopper or something, just delete the tool.
    @EventHandler
    public void onItemMoved(InventoryMoveItemEvent event)
    {
        if (!plugin.getTF().isTool(event.getItem()))
            return;

        Inventory src = event.getSource();
        if (src instanceof PlayerInventory && ((PlayerInventory) src).getHolder() instanceof Player)
        {
            ToolUser tu = plugin.getToolUser((Player) ((PlayerInventory) src).getHolder());
            if (tu != null)
            {
                event.setCancelled(true);
                return;
            }
        }
        src.removeItem(event.getItem());
    }
}
