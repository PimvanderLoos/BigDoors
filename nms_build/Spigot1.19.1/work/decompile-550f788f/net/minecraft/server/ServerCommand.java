package net.minecraft.server;

import net.minecraft.commands.CommandListenerWrapper;

public class ServerCommand {

    public final String msg;
    public final CommandListenerWrapper source;

    public ServerCommand(String s, CommandListenerWrapper commandlistenerwrapper) {
        this.msg = s;
        this.source = commandlistenerwrapper;
    }
}
