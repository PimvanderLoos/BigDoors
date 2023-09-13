package net.minecraft.commands.arguments;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.context.ParsedArgument;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.network.chat.IChatBaseComponent;

public interface PreviewedArgument<T> extends ArgumentType<T> {

    @Nullable
    static CompletableFuture<IChatBaseComponent> resolvePreviewed(ArgumentCommandNode<?, ?> argumentcommandnode, CommandContextBuilder<CommandListenerWrapper> commandcontextbuilder) throws CommandSyntaxException {
        ArgumentType argumenttype = argumentcommandnode.getType();

        if (argumenttype instanceof PreviewedArgument) {
            PreviewedArgument<?> previewedargument = (PreviewedArgument) argumenttype;

            return previewedargument.resolvePreview(commandcontextbuilder, argumentcommandnode.getName());
        } else {
            return null;
        }
    }

    static boolean isPreviewed(CommandNode<?> commandnode) {
        boolean flag;

        if (commandnode instanceof ArgumentCommandNode) {
            ArgumentCommandNode<?, ?> argumentcommandnode = (ArgumentCommandNode) commandnode;

            if (argumentcommandnode.getType() instanceof PreviewedArgument) {
                flag = true;
                return flag;
            }
        }

        flag = false;
        return flag;
    }

    @Nullable
    default CompletableFuture<IChatBaseComponent> resolvePreview(CommandContextBuilder<CommandListenerWrapper> commandcontextbuilder, String s) throws CommandSyntaxException {
        ParsedArgument<CommandListenerWrapper, ?> parsedargument = (ParsedArgument) commandcontextbuilder.getArguments().get(s);

        return parsedargument != null && this.getValueType().isInstance(parsedargument.getResult()) ? this.resolvePreview((CommandListenerWrapper) commandcontextbuilder.getSource(), this.getValueType().cast(parsedargument.getResult())) : null;
    }

    CompletableFuture<IChatBaseComponent> resolvePreview(CommandListenerWrapper commandlistenerwrapper, T t0) throws CommandSyntaxException;

    Class<T> getValueType();
}
