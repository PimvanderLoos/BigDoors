package net.minecraft.server;

public class ExceptionEntityNotFound extends CommandException {

    public ExceptionEntityNotFound(String s) {
        this("commands.generic.entity.notFound", new Object[] { s});
    }

    public ExceptionEntityNotFound(String s, Object... aobject) {
        super(s, aobject);
    }

    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
