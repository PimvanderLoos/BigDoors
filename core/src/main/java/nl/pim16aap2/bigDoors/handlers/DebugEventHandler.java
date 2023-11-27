package nl.pim16aap2.bigDoors.handlers;

import nl.pim16aap2.bigDoors.BigDoors;
import nl.pim16aap2.bigDoors.NMS.CustomCraftFallingBlock;
import nl.pim16aap2.bigDoors.util.ILoggableDoor;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityEnterBlockEvent;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.event.entity.EntityTransformEvent;
import org.jetbrains.annotations.Nullable;

/**
 * Handles events that are used for debugging purposes.
 */
public class DebugEventHandler implements Listener
{
    private final BigDoors plugin;

    public DebugEventHandler(BigDoors plugin)
    {
        this.plugin = plugin;
    }

    // Fired when a falling block is spawned (sand -> air) or when a falling block lands (air -> sand).
    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockChange(EntityChangeBlockEvent event)
    {
        if (event.getEntityType() != EntityType.FALLING_BLOCK)
            return;

        logGeneralFallingBlockEvent(event, String.format(
            "Changed block from '%s' to '%s'",
            event.getBlock(),
            event.getTo()
        ));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityDeath(EntityDeathEvent event)
    {
        if (event.getEntityType() != EntityType.FALLING_BLOCK)
            return;

        logGeneralFallingBlockEvent(event);
    }

    // Not sure what this event is for, but we might as well log it in case it's relevant.
    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityTransform(EntityTransformEvent event)
    {
        if (event.getEntityType() != EntityType.FALLING_BLOCK)
            return;

        logGeneralFallingBlockEvent(event, String.format(
            "Converted entity '%s', List: %s, Reason: '%s'",
            event.getTransformedEntity(),
            event.getTransformedEntities(),
            event.getTransformReason()
        ));
    }

    // Again, not sure what it does, but logging it anyway.
    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityEnterBlock(EntityEnterBlockEvent event)
    {
        if (event.getEntityType() != EntityType.FALLING_BLOCK)
            return;

        logGeneralFallingBlockEvent(event, String.format(
            "Entered block '%s'",
            event.getBlock()
        ));
    }

    /**
     * Attempts to extract the {@link ILoggableDoor} from the given {@link EntityEvent} if possible.
     *
     * @param event The {@link EntityEvent} to extract the {@link ILoggableDoor} from.
     * @return The {@link ILoggableDoor} if it could be extracted, null otherwise.
     */
    private @Nullable ILoggableDoor getDoorFromEvent(EntityEvent event)
    {
        if (!(event.getEntity() instanceof CustomCraftFallingBlock))
            return null;

        return ((CustomCraftFallingBlock) event.getEntity()).getDoor();
    }

    /**
     * Log a general {@link EntityEvent} for a {@link FallingBlock}.
     * <p>
     * See {@link #logGeneralFallingBlockEvent(EntityEvent, String)} for more information.
     *
     * @param event The {@link EntityEvent} to handle.
     */
    private void logGeneralFallingBlockEvent(EntityEvent event)
    {
        logGeneralFallingBlockEvent(event, null);
    }

    /**
     * Logs a general {@link EntityEvent} for a {@link FallingBlock}.
     * <p>
     * If the given {@link EntityEvent} is not for a {@link FallingBlock}, this method will do nothing.
     * <p>
     * The method will log some general information about the event, such as the event name, the entity's UUID, and
     * whether the event was cancelled. Any additional information can be provided through the {@code message} parameter.
     *
     * @param event The {@link EntityEvent} to handle.
     * @param message The message that provides additional information about the event.
     */
    private void logGeneralFallingBlockEvent(EntityEvent event, @Nullable String message)
    {
        if (!(event.getEntity() instanceof FallingBlock))
            return;

        final boolean isCancelled = event instanceof Cancellable && ((Cancellable) event).isCancelled();

        final Location loc = event.getEntity().getLocation();

        plugin.getMyLogger().logMessageToLogFileForDoor(getDoorFromEvent(event), String.format(
            "Falling Block  [%s] [%s] at [%d, %d, %d]: Received event: '%s' (cancelled: %b)%s",
            event.getEntity().getUniqueId(),
            ((FallingBlock) event.getEntity()).getBlockData(),
            loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(),
            event.getEventName(),
            isCancelled,
            message == null ? "" : (": " + message)
        ));
    }
}
