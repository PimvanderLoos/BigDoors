package net.minecraft.server;

public final class CancelledPacketHandleException extends RuntimeException {

    public static final CancelledPacketHandleException RUNNING_ON_DIFFERENT_THREAD = new CancelledPacketHandleException();

    private CancelledPacketHandleException() {
        this.setStackTrace(new StackTraceElement[0]);
    }

    public synchronized Throwable fillInStackTrace() {
        this.setStackTrace(new StackTraceElement[0]);
        return this;
    }
}
