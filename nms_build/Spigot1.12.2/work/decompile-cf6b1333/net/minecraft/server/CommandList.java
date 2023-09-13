package net.minecraft.server;

public class CommandList extends CommandAbstract {

    public CommandList() {}

    public String getCommand() {
        return "list";
    }

    public int a() {
        return 0;
    }

    public String getUsage(ICommandListener icommandlistener) {
        return "commands.players.usage";
    }

    public void execute(MinecraftServer minecraftserver, ICommandListener icommandlistener, String[] astring) throws CommandException {
        int i = minecraftserver.H();

        icommandlistener.sendMessage(new ChatMessage("commands.players.list", new Object[] { Integer.valueOf(i), Integer.valueOf(minecraftserver.I())}));
        icommandlistener.sendMessage(new ChatComponentText(minecraftserver.getPlayerList().b(astring.length > 0 && "uuids".equalsIgnoreCase(astring[0]))));
        icommandlistener.a(CommandObjectiveExecutor.EnumCommandResult.QUERY_RESULT, i);
    }
}
