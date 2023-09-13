package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.function.Predicate;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.arguments.ArgumentEntity;
import net.minecraft.commands.arguments.item.ArgumentItemPredicate;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.IInventory;
import net.minecraft.world.item.ItemStack;

public class CommandClear {

    private static final DynamicCommandExceptionType a = new DynamicCommandExceptionType((object) -> {
        return new ChatMessage("clear.failed.single", new Object[]{object});
    });
    private static final DynamicCommandExceptionType b = new DynamicCommandExceptionType((object) -> {
        return new ChatMessage("clear.failed.multiple", new Object[]{object});
    });

    public static void a(CommandDispatcher<CommandListenerWrapper> commanddispatcher) {
        commanddispatcher.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("clear").requires((commandlistenerwrapper) -> {
            return commandlistenerwrapper.hasPermission(2);
        })).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), Collections.singleton(((CommandListenerWrapper) commandcontext.getSource()).h()), (itemstack) -> {
                return true;
            }, -1);
        })).then(((RequiredArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("targets", (ArgumentType) ArgumentEntity.d()).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.f(commandcontext, "targets"), (itemstack) -> {
                return true;
            }, -1);
        })).then(((RequiredArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("item", (ArgumentType) ArgumentItemPredicate.a()).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.f(commandcontext, "targets"), ArgumentItemPredicate.a(commandcontext, "item"), -1);
        })).then(net.minecraft.commands.CommandDispatcher.a("maxCount", (ArgumentType) IntegerArgumentType.integer(0)).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.f(commandcontext, "targets"), ArgumentItemPredicate.a(commandcontext, "item"), IntegerArgumentType.getInteger(commandcontext, "maxCount"));
        })))));
    }

    private static int a(CommandListenerWrapper commandlistenerwrapper, Collection<EntityPlayer> collection, Predicate<ItemStack> predicate, int i) throws CommandSyntaxException {
        int j = 0;
        Iterator iterator = collection.iterator();

        while (iterator.hasNext()) {
            EntityPlayer entityplayer = (EntityPlayer) iterator.next();

            j += entityplayer.inventory.a(predicate, i, entityplayer.defaultContainer.j());
            entityplayer.activeContainer.c();
            entityplayer.defaultContainer.a((IInventory) entityplayer.inventory);
            entityplayer.broadcastCarriedItem();
        }

        if (j == 0) {
            if (collection.size() == 1) {
                throw CommandClear.a.create(((EntityPlayer) collection.iterator().next()).getDisplayName());
            } else {
                throw CommandClear.b.create(collection.size());
            }
        } else {
            if (i == 0) {
                if (collection.size() == 1) {
                    commandlistenerwrapper.sendMessage(new ChatMessage("commands.clear.test.single", new Object[]{j, ((EntityPlayer) collection.iterator().next()).getScoreboardDisplayName()}), true);
                } else {
                    commandlistenerwrapper.sendMessage(new ChatMessage("commands.clear.test.multiple", new Object[]{j, collection.size()}), true);
                }
            } else if (collection.size() == 1) {
                commandlistenerwrapper.sendMessage(new ChatMessage("commands.clear.success.single", new Object[]{j, ((EntityPlayer) collection.iterator().next()).getScoreboardDisplayName()}), true);
            } else {
                commandlistenerwrapper.sendMessage(new ChatMessage("commands.clear.success.multiple", new Object[]{j, collection.size()}), true);
            }

            return j;
        }
    }
}
