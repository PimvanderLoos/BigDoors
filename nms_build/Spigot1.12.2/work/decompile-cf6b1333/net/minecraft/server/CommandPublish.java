package net.minecraft.server;

public class CommandPublish extends CommandAbstract {

    public CommandPublish() {}

    public String getCommand() {
        return "publish";
    }

    public String getUsage(ICommandListener icommandlistener) {
        return "commands.publish.usage";
    }

    public void execute(MinecraftServer minecraftserver, ICommandListener icommandlistener, String[] astring) throws CommandException {
        String s = minecraftserver.a(EnumGamemode.SURVIVAL, false);

        if (s != null) {
            a(icommandlistener, (ICommand) this, "commands.publish.started", new Object[] { s});
        } else {
            a(icommandlistener, (ICommand) this, "commands.publish.failed", new Object[0]);
        }

    }
}
