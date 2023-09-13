package net.minecraft.server.commands;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.arguments.ArgumentEntity;
import net.minecraft.commands.arguments.ArgumentInventorySlot;
import net.minecraft.commands.arguments.coordinates.ArgumentPosition;
import net.minecraft.commands.arguments.item.ArgumentItemStack;
import net.minecraft.core.BlockPosition;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.IInventory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.TileEntity;

public class CommandReplaceItem {

    public static final SimpleCommandExceptionType a = new SimpleCommandExceptionType(new ChatMessage("commands.replaceitem.block.failed"));
    public static final DynamicCommandExceptionType b = new DynamicCommandExceptionType((object) -> {
        return new ChatMessage("commands.replaceitem.slot.inapplicable", new Object[]{object});
    });
    public static final Dynamic2CommandExceptionType c = new Dynamic2CommandExceptionType((object, object1) -> {
        return new ChatMessage("commands.replaceitem.entity.failed", new Object[]{object, object1});
    });

    public static void a(CommandDispatcher<CommandListenerWrapper> commanddispatcher) {
        commanddispatcher.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("replaceitem").requires((commandlistenerwrapper) -> {
            return commandlistenerwrapper.hasPermission(2);
        })).then(net.minecraft.commands.CommandDispatcher.a("block").then(net.minecraft.commands.CommandDispatcher.a("pos", (ArgumentType) ArgumentPosition.a()).then(net.minecraft.commands.CommandDispatcher.a("slot", (ArgumentType) ArgumentInventorySlot.a()).then(((RequiredArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("item", (ArgumentType) ArgumentItemStack.a()).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), ArgumentPosition.a(commandcontext, "pos"), ArgumentInventorySlot.a(commandcontext, "slot"), ArgumentItemStack.a(commandcontext, "item").a(1, false));
        })).then(net.minecraft.commands.CommandDispatcher.a("count", (ArgumentType) IntegerArgumentType.integer(1, 64)).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), ArgumentPosition.a(commandcontext, "pos"), ArgumentInventorySlot.a(commandcontext, "slot"), ArgumentItemStack.a(commandcontext, "item").a(IntegerArgumentType.getInteger(commandcontext, "count"), true));
        }))))))).then(net.minecraft.commands.CommandDispatcher.a("entity").then(net.minecraft.commands.CommandDispatcher.a("targets", (ArgumentType) ArgumentEntity.multipleEntities()).then(net.minecraft.commands.CommandDispatcher.a("slot", (ArgumentType) ArgumentInventorySlot.a()).then(((RequiredArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("item", (ArgumentType) ArgumentItemStack.a()).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.b(commandcontext, "targets"), ArgumentInventorySlot.a(commandcontext, "slot"), ArgumentItemStack.a(commandcontext, "item").a(1, false));
        })).then(net.minecraft.commands.CommandDispatcher.a("count", (ArgumentType) IntegerArgumentType.integer(1, 64)).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.b(commandcontext, "targets"), ArgumentInventorySlot.a(commandcontext, "slot"), ArgumentItemStack.a(commandcontext, "item").a(IntegerArgumentType.getInteger(commandcontext, "count"), true));
        })))))));
    }

    private static int a(CommandListenerWrapper commandlistenerwrapper, BlockPosition blockposition, int i, ItemStack itemstack) throws CommandSyntaxException {
        TileEntity tileentity = commandlistenerwrapper.getWorld().getTileEntity(blockposition);

        if (!(tileentity instanceof IInventory)) {
            throw CommandReplaceItem.a.create();
        } else {
            IInventory iinventory = (IInventory) tileentity;

            if (i >= 0 && i < iinventory.getSize()) {
                iinventory.setItem(i, itemstack);
                commandlistenerwrapper.sendMessage(new ChatMessage("commands.replaceitem.block.success", new Object[]{blockposition.getX(), blockposition.getY(), blockposition.getZ(), itemstack.C()}), true);
                return 1;
            } else {
                throw CommandReplaceItem.b.create(i);
            }
        }
    }

    private static int a(CommandListenerWrapper commandlistenerwrapper, Collection<? extends Entity> collection, int i, ItemStack itemstack) throws CommandSyntaxException {
        List<Entity> list = Lists.newArrayListWithCapacity(collection.size());
        Iterator iterator = collection.iterator();

        while (iterator.hasNext()) {
            Entity entity = (Entity) iterator.next();

            if (entity instanceof EntityPlayer) {
                ((EntityPlayer) entity).defaultContainer.c();
            }

            if (entity.a_(i, itemstack.cloneItemStack())) {
                list.add(entity);
                if (entity instanceof EntityPlayer) {
                    ((EntityPlayer) entity).defaultContainer.c();
                }
            }
        }

        if (list.isEmpty()) {
            throw CommandReplaceItem.c.create(itemstack.C(), i);
        } else {
            if (list.size() == 1) {
                commandlistenerwrapper.sendMessage(new ChatMessage("commands.replaceitem.entity.success.single", new Object[]{((Entity) list.iterator().next()).getScoreboardDisplayName(), itemstack.C()}), true);
            } else {
                commandlistenerwrapper.sendMessage(new ChatMessage("commands.replaceitem.entity.success.multiple", new Object[]{list.size(), itemstack.C()}), true);
            }

            return list.size();
        }
    }
}
