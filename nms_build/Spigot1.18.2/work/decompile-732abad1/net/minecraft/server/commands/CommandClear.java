package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
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
import net.minecraft.world.item.ItemStack;

public class CommandClear {

    private static final DynamicCommandExceptionType ERROR_SINGLE = new DynamicCommandExceptionType((object) -> {
        return new ChatMessage("clear.failed.single", new Object[]{object});
    });
    private static final DynamicCommandExceptionType ERROR_MULTIPLE = new DynamicCommandExceptionType((object) -> {
        return new ChatMessage("clear.failed.multiple", new Object[]{object});
    });

    public CommandClear() {}

    public static void register(CommandDispatcher<CommandListenerWrapper> commanddispatcher) {
        commanddispatcher.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.literal("clear").requires((commandlistenerwrapper) -> {
            return commandlistenerwrapper.hasPermission(2);
        })).executes((commandcontext) -> {
            return clearInventory((CommandListenerWrapper) commandcontext.getSource(), Collections.singleton(((CommandListenerWrapper) commandcontext.getSource()).getPlayerOrException()), (itemstack) -> {
                return true;
            }, -1);
        })).then(((RequiredArgumentBuilder) net.minecraft.commands.CommandDispatcher.argument("targets", ArgumentEntity.players()).executes((commandcontext) -> {
            return clearInventory((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.getPlayers(commandcontext, "targets"), (itemstack) -> {
                return true;
            }, -1);
        })).then(((RequiredArgumentBuilder) net.minecraft.commands.CommandDispatcher.argument("item", ArgumentItemPredicate.itemPredicate()).executes((commandcontext) -> {
            return clearInventory((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.getPlayers(commandcontext, "targets"), ArgumentItemPredicate.getItemPredicate(commandcontext, "item"), -1);
        })).then(net.minecraft.commands.CommandDispatcher.argument("maxCount", IntegerArgumentType.integer(0)).executes((commandcontext) -> {
            return clearInventory((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.getPlayers(commandcontext, "targets"), ArgumentItemPredicate.getItemPredicate(commandcontext, "item"), IntegerArgumentType.getInteger(commandcontext, "maxCount"));
        })))));
    }

    private static int clearInventory(CommandListenerWrapper commandlistenerwrapper, Collection<EntityPlayer> collection, Predicate<ItemStack> predicate, int i) throws CommandSyntaxException {
        int j = 0;
        Iterator iterator = collection.iterator();

        while (iterator.hasNext()) {
            EntityPlayer entityplayer = (EntityPlayer) iterator.next();

            j += entityplayer.getInventory().clearOrCountMatchingItems(predicate, i, entityplayer.inventoryMenu.getCraftSlots());
            entityplayer.containerMenu.broadcastChanges();
            entityplayer.inventoryMenu.slotsChanged(entityplayer.getInventory());
        }

        if (j == 0) {
            if (collection.size() == 1) {
                throw CommandClear.ERROR_SINGLE.create(((EntityPlayer) collection.iterator().next()).getName());
            } else {
                throw CommandClear.ERROR_MULTIPLE.create(collection.size());
            }
        } else {
            if (i == 0) {
                if (collection.size() == 1) {
                    commandlistenerwrapper.sendSuccess(new ChatMessage("commands.clear.test.single", new Object[]{j, ((EntityPlayer) collection.iterator().next()).getDisplayName()}), true);
                } else {
                    commandlistenerwrapper.sendSuccess(new ChatMessage("commands.clear.test.multiple", new Object[]{j, collection.size()}), true);
                }
            } else if (collection.size() == 1) {
                commandlistenerwrapper.sendSuccess(new ChatMessage("commands.clear.success.single", new Object[]{j, ((EntityPlayer) collection.iterator().next()).getDisplayName()}), true);
            } else {
                commandlistenerwrapper.sendSuccess(new ChatMessage("commands.clear.success.multiple", new Object[]{j, collection.size()}), true);
            }

            return j;
        }
    }
}
