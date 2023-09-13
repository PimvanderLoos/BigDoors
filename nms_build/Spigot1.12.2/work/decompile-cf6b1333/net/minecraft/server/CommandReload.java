package net.minecraft.server;

public class CommandReload extends CommandAbstract {

    public CommandReload() {}

    public String getCommand() {
        return "reload";
    }

    public int a() {
        return 3;
    }

    public String getUsage(ICommandListener icommandlistener) {
        return "commands.reload.usage";
    }

    public void execute(MinecraftServer minecraftserver, ICommandListener icommandlistener, String[] astring) throws CommandException {
        if (astring.length > 0) {
            throw new ExceptionUsage("commands.reload.usage", new Object[0]);
        } else {
            minecraftserver.reload();
            a(icommandlistener, (ICommand) this, "commands.reload.success", new Object[0]);
        }
    }
}
