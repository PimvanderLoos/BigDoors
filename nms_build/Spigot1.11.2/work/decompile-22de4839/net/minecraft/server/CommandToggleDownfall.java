package net.minecraft.server;

public class CommandToggleDownfall extends CommandAbstract {

    public CommandToggleDownfall() {}

    public String getCommand() {
        return "toggledownfall";
    }

    public int a() {
        return 2;
    }

    public String getUsage(ICommandListener icommandlistener) {
        return "commands.downfall.usage";
    }

    public void execute(MinecraftServer minecraftserver, ICommandListener icommandlistener, String[] astring) throws CommandException {
        this.a(minecraftserver);
        a(icommandlistener, (ICommand) this, "commands.downfall.success", new Object[0]);
    }

    protected void a(MinecraftServer minecraftserver) {
        WorldData worlddata = minecraftserver.worldServer[0].getWorldData();

        worlddata.setStorm(!worlddata.hasStorm());
    }
}
