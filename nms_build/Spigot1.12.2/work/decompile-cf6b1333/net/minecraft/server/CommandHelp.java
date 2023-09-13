package net.minecraft.server;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;

public class CommandHelp extends CommandAbstract {

    private static final String[] a = new String[] { "Yolo", "Ask for help on twitter", "/deop @p", "Scoreboard deleted, commands blocked", "Contact helpdesk for help", "/testfornoob @p", "/trigger warning", "Oh my god, it\'s full of stats", "/kill @p[name=!Searge]", "Have you tried turning it off and on again?", "Sorry, no help today"};
    private final Random b = new Random();

    public CommandHelp() {}

    public String getCommand() {
        return "help";
    }

    public int a() {
        return 0;
    }

    public String getUsage(ICommandListener icommandlistener) {
        return "commands.help.usage";
    }

    public List<String> getAliases() {
        return Arrays.asList(new String[] { "?"});
    }

    public void execute(MinecraftServer minecraftserver, ICommandListener icommandlistener, String[] astring) throws CommandException {
        if (icommandlistener instanceof CommandBlockListenerAbstract) {
            icommandlistener.sendMessage((new ChatComponentText("Searge says: ")).a(CommandHelp.a[this.b.nextInt(CommandHelp.a.length) % CommandHelp.a.length]));
        } else {
            List list = this.a(icommandlistener, minecraftserver);
            boolean flag = true;
            int i = (list.size() - 1) / 7;
            boolean flag1 = false;

            int j;

            try {
                j = astring.length == 0 ? 0 : a(astring[0], 1, i + 1) - 1;
            } catch (ExceptionInvalidNumber exceptioninvalidnumber) {
                Map map = this.a(minecraftserver);
                ICommand icommand = (ICommand) map.get(astring[0]);

                if (icommand != null) {
                    throw new ExceptionUsage(icommand.getUsage(icommandlistener), new Object[0]);
                }

                if (MathHelper.a(astring[0], -1) == -1 && MathHelper.a(astring[0], -2) == -2) {
                    throw new ExceptionUnknownCommand();
                }

                throw exceptioninvalidnumber;
            }

            int k = Math.min((j + 1) * 7, list.size());
            ChatMessage chatmessage = new ChatMessage("commands.help.header", new Object[] { Integer.valueOf(j + 1), Integer.valueOf(i + 1)});

            chatmessage.getChatModifier().setColor(EnumChatFormat.DARK_GREEN);
            icommandlistener.sendMessage(chatmessage);

            for (int l = j * 7; l < k; ++l) {
                ICommand icommand1 = (ICommand) list.get(l);
                ChatMessage chatmessage1 = new ChatMessage(icommand1.getUsage(icommandlistener), new Object[0]);

                chatmessage1.getChatModifier().setChatClickable(new ChatClickable(ChatClickable.EnumClickAction.SUGGEST_COMMAND, "/" + icommand1.getCommand() + " "));
                icommandlistener.sendMessage(chatmessage1);
            }

            if (j == 0) {
                ChatMessage chatmessage2 = new ChatMessage("commands.help.footer", new Object[0]);

                chatmessage2.getChatModifier().setColor(EnumChatFormat.GREEN);
                icommandlistener.sendMessage(chatmessage2);
            }

        }
    }

    protected List<ICommand> a(ICommandListener icommandlistener, MinecraftServer minecraftserver) {
        List list = minecraftserver.getCommandHandler().a(icommandlistener);

        Collections.sort(list);
        return list;
    }

    protected Map<String, ICommand> a(MinecraftServer minecraftserver) {
        return minecraftserver.getCommandHandler().getCommands();
    }

    public List<String> tabComplete(MinecraftServer minecraftserver, ICommandListener icommandlistener, String[] astring, @Nullable BlockPosition blockposition) {
        if (astring.length == 1) {
            Set set = this.a(minecraftserver).keySet();

            return a(astring, (String[]) set.toArray(new String[set.size()]));
        } else {
            return Collections.emptyList();
        }
    }
}
