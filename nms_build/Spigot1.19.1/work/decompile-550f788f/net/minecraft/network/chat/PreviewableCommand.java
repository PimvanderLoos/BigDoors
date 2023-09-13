package net.minecraft.network.chat;

import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.context.ParsedArgument;
import com.mojang.brigadier.context.ParsedCommandNode;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.commands.arguments.PreviewedArgument;

public record PreviewableCommand<S> (List<PreviewableCommand.a<S>> arguments) {

    public static <S> PreviewableCommand<S> of(ParseResults<S> parseresults) {
        CommandContextBuilder<S> commandcontextbuilder = parseresults.getContext();
        CommandContextBuilder<S> commandcontextbuilder1 = commandcontextbuilder;

        CommandContextBuilder commandcontextbuilder2;
        List list;

        for (list = collectArguments(commandcontextbuilder); (commandcontextbuilder2 = commandcontextbuilder1.getChild()) != null; commandcontextbuilder1 = commandcontextbuilder2) {
            boolean flag = commandcontextbuilder2.getRootNode() != commandcontextbuilder.getRootNode();

            if (!flag) {
                break;
            }

            list.addAll(collectArguments(commandcontextbuilder2));
        }

        return new PreviewableCommand<>(list);
    }

    private static <S> List<PreviewableCommand.a<S>> collectArguments(CommandContextBuilder<S> commandcontextbuilder) {
        List<PreviewableCommand.a<S>> list = new ArrayList();
        Iterator iterator = commandcontextbuilder.getNodes().iterator();

        while (iterator.hasNext()) {
            ParsedCommandNode<S> parsedcommandnode = (ParsedCommandNode) iterator.next();
            CommandNode commandnode = parsedcommandnode.getNode();

            if (commandnode instanceof ArgumentCommandNode) {
                ArgumentCommandNode<S, ?> argumentcommandnode = (ArgumentCommandNode) commandnode;
                ArgumentType argumenttype = argumentcommandnode.getType();

                if (argumenttype instanceof PreviewedArgument) {
                    PreviewedArgument<?> previewedargument = (PreviewedArgument) argumenttype;
                    ParsedArgument<S, ?> parsedargument = (ParsedArgument) commandcontextbuilder.getArguments().get(argumentcommandnode.getName());

                    if (parsedargument != null) {
                        list.add(new PreviewableCommand.a<>(argumentcommandnode, parsedargument, previewedargument));
                    }
                }
            }
        }

        return list;
    }

    public boolean isPreviewed(CommandNode<?> commandnode) {
        Iterator iterator = this.arguments.iterator();

        PreviewableCommand.a previewablecommand_a;

        do {
            if (!iterator.hasNext()) {
                return false;
            }

            previewablecommand_a = (PreviewableCommand.a) iterator.next();
        } while (previewablecommand_a.node() != commandnode);

        return true;
    }

    public static record a<S> (ArgumentCommandNode<S, ?> node, ParsedArgument<S, ?> parsedValue, PreviewedArgument<?> previewType) {

        public String name() {
            return this.node.getName();
        }
    }
}
