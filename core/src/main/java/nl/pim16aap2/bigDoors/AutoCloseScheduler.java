package nl.pim16aap2.bigDoors;

import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AutoCloseScheduler
{
    private final BigDoors plugin;
    private final Map<Long, BukkitTask> timers;

    private volatile boolean autoCloseAllowed = true;

    public AutoCloseScheduler(final BigDoors plugin)
    {
        this.plugin = plugin;
        timers = new ConcurrentHashMap<>();
    }

    /**
     * Cancels all autoCloseTimers.
     * <p>
     * Note that this does not prevent any new timers from being scheduled. Use {@link #autoCloseAllowed(boolean)} for that.
     */
    public void cancelAllTimers()
    {
        plugin.getMyLogger().logMessageToLogFile("Cancelling all autoCloseTimers.");
        for (Map.Entry<Long, BukkitTask> entry : timers.entrySet())
        {
            plugin.getMyLogger().logMessageToLogFile(String.format(
                "[%3d - %-12s] Cancelling auto close timer with task id %d",
                entry.getKey(), "___________", entry.getValue().getTaskId()
            ));
            entry.getValue().cancel();
        }
    }

    public boolean isDoorWaiting(long doorUID)
    {
        return timers.containsKey(doorUID);
    }

    private void deleteTimer(long doorUID)
    {
        if (timers.containsKey(doorUID))
        {
            timers.get(doorUID).cancel();
            timers.remove(doorUID);
        }
    }

    public void cancelTimer(long doorUID)
    {
        deleteTimer(doorUID);
    }

    public void scheduleAutoClose(Door door, double time, boolean instantOpen)
    {
        if (!autoCloseAllowed())
            return;

        if (door.getAutoClose() < 0 || !door.isOpen())
            return;

        plugin.getMyLogger().logMessageToLogFile(String.format(
            "[%3d - %-12s] Scheduling autoCloseTimer with time: %f, instantOpen: %b",
            door.getDoorUID(), door.getType(), time, instantOpen
        ));

        if (door.getAutoClose() > BigDoors.get().getConfigLoader().maxAutoCloseTimer())
        {
            BigDoors.get().getMyLogger()
                .warn("Aborted autoCloseTimer for door: " + door.getDoorUID() + ", because it's autoCloseTimer ("
                    + door.getAutoClose() + ") exceeds the global limit ("
                    + BigDoors.get().getConfigLoader().maxAutoCloseTimer() + ").");
            BigDoors.get().getMyLogger()
                .warn("Either change the door's autoCloseTimer or increase the global limit in the config.");
            return;
        }

        // First delete any old timers that might still be running.
        deleteTimer(door.getDoorUID());
        int delay = Math.max(plugin.getMinimumDoorDelay(), door.getAutoClose() * 20);

        timers.put(door.getDoorUID(), new BukkitRunnable()
        {
            @Override
            public void run()
            {
                if (door.isOpen())
                {
                    if (plugin.getCommander().isDoorBusy(door.getDoorUID()))
                    {
                        deleteTimer(door.getDoorUID());
                        cancel();
                    }
                    else
                        plugin.getDoorOpener(door.getType())
                            .openDoorFuture(plugin.getCommander().getDoor(null, door.getDoorUID()), time, instantOpen, false);
                }
                deleteTimer(door.getDoorUID());
            }
        }.runTaskLater(plugin, delay));
    }

    /**
     * Sets whether autoCloseTimers are allowed to be scheduled.
     * <p>
     * Note that this does not cancel any currently running autoCloseTimers. Use {@link #cancelAllTimers()} for that.
     *
     * @param autoCloseAllowed True if autoCloseTimers should be allowed to be scheduled, false otherwise.
     */
    public void autoCloseAllowed(boolean autoCloseAllowed)
    {
        plugin.getMyLogger().logMessageToLogFile("Setting autoCloseAllowed to " + autoCloseAllowed + ".");
        this.autoCloseAllowed = autoCloseAllowed;
    }

    /**
     * Returns whether autoCloseTimers are allowed to be scheduled.
     *
     * @return True if autoCloseTimers are allowed to be scheduled, false otherwise.
     */
    public boolean autoCloseAllowed()
    {
        return autoCloseAllowed;
    }
}
