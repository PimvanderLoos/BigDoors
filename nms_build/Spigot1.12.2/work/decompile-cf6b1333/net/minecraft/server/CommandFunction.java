package net.minecraft.server;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;

public class CommandFunction extends CommandAbstract {

    public CommandFunction() {}

    public String getCommand() {
        return "function";
    }

    public int a() {
        return 2;
    }

    public String getUsage(ICommandListener icommandlistener) {
        return "commands.function.usage";
    }

    public void execute(MinecraftServer minecraftserver, ICommandListener icommandlistener, String[] astring) throws CommandException {
        if (astring.length != 1 && astring.length != 3) {
            throw new ExceptionUsage("commands.function.usage", new Object[0]);
        } else {
            MinecraftKey minecraftkey = new MinecraftKey(astring[0]);
            CustomFunction customfunction = minecraftserver.aL().a(minecraftkey);

            if (customfunction == null) {
                throw new CommandException("commands.function.unknown", new Object[] { minecraftkey});
            } else {
                if (astring.length == 3) {
                    String s = astring[1];
                    boolean flag;

                    if ("if".equals(s)) {
                        flag = true;
                    } else {
                        if (!"unless".equals(s)) {
                            throw new ExceptionUsage("commands.function.usage", new Object[0]);
                        }

                        flag = false;
                    }

                    boolean flag1 = false;

                    try {
                        flag1 = !d(minecraftserver, icommandlistener, astring[2]).isEmpty();
                    } catch (ExceptionEntityNotFound exceptionentitynotfound) {
                        ;
                    }

                    if (flag != flag1) {
                        throw new CommandException("commands.function.skipped", new Object[] { minecraftkey});
                    }
                }

                int i = minecraftserver.aL().a(customfunction, CommandListenerWrapper.a(icommandlistener).i().a(2).a(false));

                a(icommandlistener, (ICommand) this, "commands.function.success", new Object[] { minecraftkey, Integer.valueOf(i)});
            }
        }
    }

    public List<String> tabComplete(MinecraftServer minecraftserver, ICommandListener icommandlistener, String[] astring, @Nullable BlockPosition blockposition) {
        return astring.length == 1 ? a(astring, (Collection) minecraftserver.aL().d().keySet()) : (astring.length == 2 ? a(astring, new String[] { "if", "unless"}) : (astring.length == 3 ? a(astring, minecraftserver.getPlayers()) : Collections.emptyList()));
    }
}
