package nl.pim16aap2.bigDoors.util;

import com.github.Anon8281.universalScheduler.scheduling.tasks.MyScheduledTask;

public abstract class Abortable
{
    private MyScheduledTask universalTask;

    public abstract void abort(boolean onDisable);

    protected void cancelTask()
    {
        universalTask.cancel();
    }

    public void abort()
    {
        abort(false);
    }

    public void setTask(MyScheduledTask task)
    {
        universalTask = task;
    }
}
