package net.minecraft.commands;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.network.chat.IChatBaseComponent;

public class CommandException extends RuntimeException {

    private final IChatBaseComponent a;

    public CommandException(IChatBaseComponent ichatbasecomponent) {
        super(ichatbasecomponent.getString(), (Throwable) null, CommandSyntaxException.ENABLE_COMMAND_STACK_TRACES, CommandSyntaxException.ENABLE_COMMAND_STACK_TRACES);
        this.a = ichatbasecomponent;
    }

    public IChatBaseComponent a() {
        return this.a;
    }
}
