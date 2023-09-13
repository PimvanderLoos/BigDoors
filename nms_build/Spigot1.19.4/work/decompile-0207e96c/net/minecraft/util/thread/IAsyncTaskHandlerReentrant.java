package net.minecraft.util.thread;

public abstract class IAsyncTaskHandlerReentrant<R extends Runnable> extends IAsyncTaskHandler<R> {

    private int reentrantCount;

    public IAsyncTaskHandlerReentrant(String s) {
        super(s);
    }

    @Override
    public boolean scheduleExecutables() {
        return this.runningTask() || super.scheduleExecutables();
    }

    protected boolean runningTask() {
        return this.reentrantCount != 0;
    }

    @Override
    public void doRunTask(R r0) {
        ++this.reentrantCount;

        try {
            super.doRunTask(r0);
        } finally {
            --this.reentrantCount;
        }

    }
}
