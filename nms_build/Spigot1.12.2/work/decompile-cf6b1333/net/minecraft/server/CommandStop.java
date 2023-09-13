package net.minecraft.server;

public class CommandStop extends CommandAbstract {

    public CommandStop() {}

    public String getCommand() {
        return "stop";
    }

    public String getUsage(ICommandListener icommandlistener) {
        return "commands.stop.usage";
    }

    public void execute(MinecraftServer minecraftserver, ICommandListener icommandlistener, String[] astring) throws CommandException {
        if (minecraftserver.worldServer != null) {
            a(icommandlistener, (ICommand) this, "commands.stop.start", new Object[0]);
        }

        minecraftserver.safeShutdown();
    }
}
