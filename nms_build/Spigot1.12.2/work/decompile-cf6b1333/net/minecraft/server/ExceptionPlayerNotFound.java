package net.minecraft.server;

public class ExceptionPlayerNotFound extends CommandException {

    public ExceptionPlayerNotFound(String s) {
        super(s, new Object[0]);
    }

    public ExceptionPlayerNotFound(String s, Object... aobject) {
        super(s, aobject);
    }

    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
