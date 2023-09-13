package net.minecraft.server;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

public class CommandSpawnpoint {

    public static void a(com.mojang.brigadier.CommandDispatcher<CommandListenerWrapper> com_mojang_brigadier_commanddispatcher) {
        com_mojang_brigadier_commanddispatcher.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) CommandDispatcher.a("spawnpoint").requires((commandlistenerwrapper) -> {
            return commandlistenerwrapper.hasPermission(2);
        })).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), Collections.singleton(((CommandListenerWrapper) commandcontext.getSource()).h()), new BlockPosition(((CommandListenerWrapper) commandcontext.getSource()).getPosition()), 0.0F);
        })).then(((RequiredArgumentBuilder) CommandDispatcher.a("targets", (ArgumentType) ArgumentEntity.d()).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.f(commandcontext, "targets"), new BlockPosition(((CommandListenerWrapper) commandcontext.getSource()).getPosition()), 0.0F);
        })).then(((RequiredArgumentBuilder) CommandDispatcher.a("pos", (ArgumentType) ArgumentPosition.a()).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.f(commandcontext, "targets"), ArgumentPosition.b(commandcontext, "pos"), 0.0F);
        })).then(CommandDispatcher.a("angle", (ArgumentType) ArgumentAngle.a()).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.f(commandcontext, "targets"), ArgumentPosition.b(commandcontext, "pos"), ArgumentAngle.a(commandcontext, "angle"));
        })))));
    }

    private static int a(CommandListenerWrapper commandlistenerwrapper, Collection<EntityPlayer> collection, BlockPosition blockposition, float f) {
        ResourceKey<World> resourcekey = commandlistenerwrapper.getWorld().getDimensionKey();
        Iterator iterator = collection.iterator();

        while (iterator.hasNext()) {
            EntityPlayer entityplayer = (EntityPlayer) iterator.next();

            entityplayer.setRespawnPosition(resourcekey, blockposition, f, true, false);
        }

        String s = resourcekey.a().toString();

        if (collection.size() == 1) {
            commandlistenerwrapper.sendMessage(new ChatMessage("commands.spawnpoint.success.single", new Object[]{blockposition.getX(), blockposition.getY(), blockposition.getZ(), f, s, ((EntityPlayer) collection.iterator().next()).getScoreboardDisplayName()}), true);
        } else {
            commandlistenerwrapper.sendMessage(new ChatMessage("commands.spawnpoint.success.multiple", new Object[]{blockposition.getX(), blockposition.getY(), blockposition.getZ(), f, s, collection.size()}), true);
        }

        return collection.size();
    }
}
