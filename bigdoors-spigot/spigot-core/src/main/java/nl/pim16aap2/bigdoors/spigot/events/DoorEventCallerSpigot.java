package nl.pim16aap2.bigdoors.spigot.events;

import lombok.extern.flogger.Flogger;
import nl.pim16aap2.bigdoors.api.IPExecutor;
import nl.pim16aap2.bigdoors.events.IBigDoorsEvent;
import nl.pim16aap2.bigdoors.events.IDoorEventCaller;
import org.bukkit.Bukkit;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.logging.Level;

/**
 * Represents an implementation of {@link IDoorEventCaller} for the Spigot platform.
 *
 * @author Pim
 */
@Singleton
@Flogger
public class DoorEventCallerSpigot implements IDoorEventCaller
{
    private final IPExecutor executor;

    @Inject
    public DoorEventCallerSpigot(IPExecutor executor)
    {
        this.executor = executor;
    }

    @Override
    public void callDoorEvent(IBigDoorsEvent bigDoorsEvent)
    {
        if (!(bigDoorsEvent instanceof BigDoorsSpigotEvent))
        {
            log.at(Level.SEVERE).withCause(new IllegalArgumentException(
                "Event " + bigDoorsEvent.getEventName() +
                    ", is not a Spigot event, but it was called on the Spigot platform!")).log();
            return;
        }

        // Async events can only be called asynchronously and Sync events can only be called from the main thread.
        final boolean isMainThread = executor.isMainThread(Thread.currentThread().getId());
        if (isMainThread && bigDoorsEvent.isAsynchronous())
            executor.runAsync(() -> Bukkit.getPluginManager().callEvent((BigDoorsSpigotEvent) bigDoorsEvent));
        else if ((!isMainThread) && (!bigDoorsEvent.isAsynchronous()))
            executor.runSync(() -> Bukkit.getPluginManager().callEvent((BigDoorsSpigotEvent) bigDoorsEvent));
        else
            Bukkit.getPluginManager().callEvent((BigDoorsSpigotEvent) bigDoorsEvent);
    }
}
