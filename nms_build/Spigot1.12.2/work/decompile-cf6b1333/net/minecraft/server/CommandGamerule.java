package net.minecraft.server;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;

public class CommandGamerule extends CommandAbstract {

    public CommandGamerule() {}

    public String getCommand() {
        return "gamerule";
    }

    public int a() {
        return 2;
    }

    public String getUsage(ICommandListener icommandlistener) {
        return "commands.gamerule.usage";
    }

    public void execute(MinecraftServer minecraftserver, ICommandListener icommandlistener, String[] astring) throws CommandException {
        GameRules gamerules = this.a(minecraftserver);
        String s = astring.length > 0 ? astring[0] : "";
        String s1 = astring.length > 1 ? a(astring, 1) : "";

        switch (astring.length) {
        case 0:
            icommandlistener.sendMessage(new ChatComponentText(a((Object[]) gamerules.getGameRules())));
            break;

        case 1:
            if (!gamerules.contains(s)) {
                throw new CommandException("commands.gamerule.norule", new Object[] { s});
            }

            String s2 = gamerules.get(s);

            icommandlistener.sendMessage((new ChatComponentText(s)).a(" = ").a(s2));
            icommandlistener.a(CommandObjectiveExecutor.EnumCommandResult.QUERY_RESULT, gamerules.c(s));
            break;

        default:
            if (gamerules.a(s, GameRules.EnumGameRuleType.BOOLEAN_VALUE) && !"true".equals(s1) && !"false".equals(s1)) {
                throw new CommandException("commands.generic.boolean.invalid", new Object[] { s1});
            }

            gamerules.set(s, s1);
            a(gamerules, s, minecraftserver);
            a(icommandlistener, (ICommand) this, "commands.gamerule.success", new Object[] { s, s1});
        }

    }

    public static void a(GameRules gamerules, String s, MinecraftServer minecraftserver) {
        if ("reducedDebugInfo".equals(s)) {
            int i = gamerules.getBoolean(s) ? 22 : 23;
            Iterator iterator = minecraftserver.getPlayerList().v().iterator();

            while (iterator.hasNext()) {
                EntityPlayer entityplayer = (EntityPlayer) iterator.next();

                entityplayer.playerConnection.sendPacket(new PacketPlayOutEntityStatus(entityplayer, (byte) i));
            }
        }

    }

    public List<String> tabComplete(MinecraftServer minecraftserver, ICommandListener icommandlistener, String[] astring, @Nullable BlockPosition blockposition) {
        if (astring.length == 1) {
            return a(astring, this.a(minecraftserver).getGameRules());
        } else {
            if (astring.length == 2) {
                GameRules gamerules = this.a(minecraftserver);

                if (gamerules.a(astring[0], GameRules.EnumGameRuleType.BOOLEAN_VALUE)) {
                    return a(astring, new String[] { "true", "false"});
                }

                if (gamerules.a(astring[0], GameRules.EnumGameRuleType.FUNCTION)) {
                    return a(astring, (Collection) minecraftserver.aL().d().keySet());
                }
            }

            return Collections.emptyList();
        }
    }

    private GameRules a(MinecraftServer minecraftserver) {
        return minecraftserver.getWorldServer(0).getGameRules();
    }
}
