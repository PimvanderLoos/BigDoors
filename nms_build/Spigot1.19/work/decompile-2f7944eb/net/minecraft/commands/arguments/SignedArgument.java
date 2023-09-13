package net.minecraft.commands.arguments;

import net.minecraft.network.chat.IChatBaseComponent;

public interface SignedArgument<T> extends PreviewedArgument<T> {

    IChatBaseComponent getPlainSignableComponent(T t0);
}
