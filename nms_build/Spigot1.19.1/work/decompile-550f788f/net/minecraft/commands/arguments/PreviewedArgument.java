package net.minecraft.commands.arguments;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.ParsedArgument;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.network.chat.IChatBaseComponent;

public interface PreviewedArgument<T> extends ArgumentType<T> {

    @Nullable
    default CompletableFuture<IChatBaseComponent> resolvePreview(CommandListenerWrapper commandlistenerwrapper, ParsedArgument<CommandListenerWrapper, ?> parsedargument) throws CommandSyntaxException {
        return this.getValueType().isInstance(parsedargument.getResult()) ? this.resolvePreview(commandlistenerwrapper, this.getValueType().cast(parsedargument.getResult())) : null;
    }

    CompletableFuture<IChatBaseComponent> resolvePreview(CommandListenerWrapper commandlistenerwrapper, T t0) throws CommandSyntaxException;

    Class<T> getValueType();
}
