package nl.pim16aap2.bigDoors.util;

import com.github.Anon8281.universalScheduler.scheduling.tasks.MyScheduledTask;

public abstract class Abortable
{
    private MyScheduledTask bukkitTask;

    public abstract void abort(boolean onDisable);

    protected void cancelTask()
    {
        bukkitTask.cancel();
    }

    public void abort()
    {
        abort(false);
    }

    public void setTask(MyScheduledTask task)
    {
        bukkitTask = task;
    }
}
