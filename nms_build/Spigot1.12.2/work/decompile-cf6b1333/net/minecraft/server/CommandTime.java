package net.minecraft.server;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;

public class CommandTime extends CommandAbstract {

    public CommandTime() {}

    public String getCommand() {
        return "time";
    }

    public int a() {
        return 2;
    }

    public String getUsage(ICommandListener icommandlistener) {
        return "commands.time.usage";
    }

    public void execute(MinecraftServer minecraftserver, ICommandListener icommandlistener, String[] astring) throws CommandException {
        if (astring.length > 1) {
            int i;

            if ("set".equals(astring[0])) {
                if ("day".equals(astring[1])) {
                    i = 1000;
                } else if ("night".equals(astring[1])) {
                    i = 13000;
                } else {
                    i = a(astring[1], 0);
                }

                this.a(minecraftserver, i);
                a(icommandlistener, (ICommand) this, "commands.time.set", new Object[] { Integer.valueOf(i)});
                return;
            }

            if ("add".equals(astring[0])) {
                i = a(astring[1], 0);
                this.b(minecraftserver, i);
                a(icommandlistener, (ICommand) this, "commands.time.added", new Object[] { Integer.valueOf(i)});
                return;
            }

            if ("query".equals(astring[0])) {
                if ("daytime".equals(astring[1])) {
                    i = (int) (icommandlistener.getWorld().getDayTime() % 24000L);
                    icommandlistener.a(CommandObjectiveExecutor.EnumCommandResult.QUERY_RESULT, i);
                    a(icommandlistener, (ICommand) this, "commands.time.query", new Object[] { Integer.valueOf(i)});
                    return;
                }

                if ("day".equals(astring[1])) {
                    i = (int) (icommandlistener.getWorld().getDayTime() / 24000L % 2147483647L);
                    icommandlistener.a(CommandObjectiveExecutor.EnumCommandResult.QUERY_RESULT, i);
                    a(icommandlistener, (ICommand) this, "commands.time.query", new Object[] { Integer.valueOf(i)});
                    return;
                }

                if ("gametime".equals(astring[1])) {
                    i = (int) (icommandlistener.getWorld().getTime() % 2147483647L);
                    icommandlistener.a(CommandObjectiveExecutor.EnumCommandResult.QUERY_RESULT, i);
                    a(icommandlistener, (ICommand) this, "commands.time.query", new Object[] { Integer.valueOf(i)});
                    return;
                }
            }
        }

        throw new ExceptionUsage("commands.time.usage", new Object[0]);
    }

    public List<String> tabComplete(MinecraftServer minecraftserver, ICommandListener icommandlistener, String[] astring, @Nullable BlockPosition blockposition) {
        return astring.length == 1 ? a(astring, new String[] { "set", "add", "query"}) : (astring.length == 2 && "set".equals(astring[0]) ? a(astring, new String[] { "day", "night"}) : (astring.length == 2 && "query".equals(astring[0]) ? a(astring, new String[] { "daytime", "gametime", "day"}) : Collections.emptyList()));
    }

    protected void a(MinecraftServer minecraftserver, int i) {
        for (int j = 0; j < minecraftserver.worldServer.length; ++j) {
            minecraftserver.worldServer[j].setDayTime((long) i);
        }

    }

    protected void b(MinecraftServer minecraftserver, int i) {
        for (int j = 0; j < minecraftserver.worldServer.length; ++j) {
            WorldServer worldserver = minecraftserver.worldServer[j];

            worldserver.setDayTime(worldserver.getDayTime() + (long) i);
        }

    }
}
