package net.minecraft.util.thread;

public abstract class IAsyncTaskHandlerReentrant<R extends Runnable> extends IAsyncTaskHandler<R> {

    private int reentrantCount;

    public IAsyncTaskHandlerReentrant(String s) {
        super(s);
    }

    @Override
    public boolean isNotMainThread() {
        return this.isEntered() || super.isNotMainThread();
    }

    protected boolean isEntered() {
        return this.reentrantCount != 0;
    }

    @Override
    public void executeTask(R r0) {
        ++this.reentrantCount;

        try {
            super.executeTask(r0);
        } finally {
            --this.reentrantCount;
        }

    }
}
