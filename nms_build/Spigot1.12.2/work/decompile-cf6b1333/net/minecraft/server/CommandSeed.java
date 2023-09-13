package net.minecraft.server;

public class CommandSeed extends CommandAbstract {

    public CommandSeed() {}

    public boolean canUse(MinecraftServer minecraftserver, ICommandListener icommandlistener) {
        return minecraftserver.R() || super.canUse(minecraftserver, icommandlistener);
    }

    public String getCommand() {
        return "seed";
    }

    public int a() {
        return 2;
    }

    public String getUsage(ICommandListener icommandlistener) {
        return "commands.seed.usage";
    }

    public void execute(MinecraftServer minecraftserver, ICommandListener icommandlistener, String[] astring) throws CommandException {
        Object object = icommandlistener instanceof EntityHuman ? ((EntityHuman) icommandlistener).world : minecraftserver.getWorldServer(0);

        icommandlistener.sendMessage(new ChatMessage("commands.seed.success", new Object[] { Long.valueOf(((World) object).getSeed())}));
    }
}
