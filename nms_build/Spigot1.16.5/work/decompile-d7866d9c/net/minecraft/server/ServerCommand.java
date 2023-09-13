package net.minecraft.server;

import net.minecraft.commands.CommandListenerWrapper;

public class ServerCommand {

    public final String command;
    public final CommandListenerWrapper source;

    public ServerCommand(String s, CommandListenerWrapper commandlistenerwrapper) {
        this.command = s;
        this.source = commandlistenerwrapper;
    }
}
