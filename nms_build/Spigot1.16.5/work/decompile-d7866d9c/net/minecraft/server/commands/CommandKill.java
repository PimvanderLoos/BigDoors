package net.minecraft.server.commands;

import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.Collection;
import java.util.Iterator;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.arguments.ArgumentEntity;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.world.entity.Entity;

public class CommandKill {

    public static void a(CommandDispatcher<CommandListenerWrapper> commanddispatcher) {
        commanddispatcher.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("kill").requires((commandlistenerwrapper) -> {
            return commandlistenerwrapper.hasPermission(2);
        })).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), ImmutableList.of(((CommandListenerWrapper) commandcontext.getSource()).g()));
        })).then(net.minecraft.commands.CommandDispatcher.a("targets", (ArgumentType) ArgumentEntity.multipleEntities()).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.b(commandcontext, "targets"));
        })));
    }

    private static int a(CommandListenerWrapper commandlistenerwrapper, Collection<? extends Entity> collection) {
        Iterator iterator = collection.iterator();

        while (iterator.hasNext()) {
            Entity entity = (Entity) iterator.next();

            entity.killEntity();
        }

        if (collection.size() == 1) {
            commandlistenerwrapper.sendMessage(new ChatMessage("commands.kill.success.single", new Object[]{((Entity) collection.iterator().next()).getScoreboardDisplayName()}), true);
        } else {
            commandlistenerwrapper.sendMessage(new ChatMessage("commands.kill.success.multiple", new Object[]{collection.size()}), true);
        }

        return collection.size();
    }
}
