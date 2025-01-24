package nl.pim16aap2.bigDoors.util;

import com.github.Anon8281.universalScheduler.scheduling.tasks.MyScheduledTask;

public abstract class Abortable
{
    private volatile MyScheduledTask universalTask;

    public abstract void abort(boolean onDisable);

    protected void cancelTask()
    {
        final MyScheduledTask universalTask0 = this.universalTask;
        if (universalTask0 != null)
            universalTask0.cancel();
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
