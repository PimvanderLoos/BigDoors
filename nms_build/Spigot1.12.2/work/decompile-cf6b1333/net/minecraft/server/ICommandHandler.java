package net.minecraft.server;

import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

public interface ICommandHandler {

    int a(ICommandListener icommandlistener, String s);

    List<String> a(ICommandListener icommandlistener, String s, @Nullable BlockPosition blockposition);

    List<ICommand> a(ICommandListener icommandlistener);

    Map<String, ICommand> getCommands();
}
