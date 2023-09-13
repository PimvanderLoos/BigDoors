package net.minecraft.server.commands;

import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.Collection;
import java.util.Iterator;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.arguments.ArgumentEntity;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.world.entity.Entity;

public class CommandKill {

    public CommandKill() {}

    public static void register(CommandDispatcher<CommandListenerWrapper> commanddispatcher) {
        commanddispatcher.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.literal("kill").requires((commandlistenerwrapper) -> {
            return commandlistenerwrapper.hasPermission(2);
        })).executes((commandcontext) -> {
            return kill((CommandListenerWrapper) commandcontext.getSource(), ImmutableList.of(((CommandListenerWrapper) commandcontext.getSource()).getEntityOrException()));
        })).then(net.minecraft.commands.CommandDispatcher.argument("targets", ArgumentEntity.entities()).executes((commandcontext) -> {
            return kill((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.getEntities(commandcontext, "targets"));
        })));
    }

    private static int kill(CommandListenerWrapper commandlistenerwrapper, Collection<? extends Entity> collection) {
        Iterator iterator = collection.iterator();

        while (iterator.hasNext()) {
            Entity entity = (Entity) iterator.next();

            entity.kill();
        }

        if (collection.size() == 1) {
            commandlistenerwrapper.sendSuccess(new ChatMessage("commands.kill.success.single", new Object[]{((Entity) collection.iterator().next()).getDisplayName()}), true);
        } else {
            commandlistenerwrapper.sendSuccess(new ChatMessage("commands.kill.success.multiple", new Object[]{collection.size()}), true);
        }

        return collection.size();
    }
}
