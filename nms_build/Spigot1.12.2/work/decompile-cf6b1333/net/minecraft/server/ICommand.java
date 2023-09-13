package net.minecraft.server;

import java.util.List;
import javax.annotation.Nullable;

public interface ICommand extends Comparable<ICommand> {

    String getCommand();

    String getUsage(ICommandListener icommandlistener);

    List<String> getAliases();

    void execute(MinecraftServer minecraftserver, ICommandListener icommandlistener, String[] astring) throws CommandException;

    boolean canUse(MinecraftServer minecraftserver, ICommandListener icommandlistener);

    List<String> tabComplete(MinecraftServer minecraftserver, ICommandListener icommandlistener, String[] astring, @Nullable BlockPosition blockposition);

    boolean isListStart(String[] astring, int i);
}
