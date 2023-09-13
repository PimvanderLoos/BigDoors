package net.minecraft.server;

public class ExceptionInvalidBlockState extends CommandException {

    public ExceptionInvalidBlockState() {
        this("commands.generic.blockstate.invalid", new Object[0]);
    }

    public ExceptionInvalidBlockState(String s, Object... aobject) {
        super(s, aobject);
    }

    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
