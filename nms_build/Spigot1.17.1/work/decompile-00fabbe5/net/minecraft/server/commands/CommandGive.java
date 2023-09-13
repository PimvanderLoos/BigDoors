package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Collection;
import java.util.Iterator;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.arguments.ArgumentEntity;
import net.minecraft.commands.arguments.item.ArgumentItemStack;
import net.minecraft.commands.arguments.item.ArgumentPredicateItemStack;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.world.entity.item.EntityItem;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.ItemStack;

public class CommandGive {

    public static final int MAX_ALLOWED_ITEMSTACKS = 100;

    public CommandGive() {}

    public static void a(CommandDispatcher<CommandListenerWrapper> commanddispatcher) {
        commanddispatcher.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("give").requires((commandlistenerwrapper) -> {
            return commandlistenerwrapper.hasPermission(2);
        })).then(net.minecraft.commands.CommandDispatcher.a("targets", (ArgumentType) ArgumentEntity.d()).then(((RequiredArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("item", (ArgumentType) ArgumentItemStack.a()).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), ArgumentItemStack.a(commandcontext, "item"), ArgumentEntity.f(commandcontext, "targets"), 1);
        })).then(net.minecraft.commands.CommandDispatcher.a("count", (ArgumentType) IntegerArgumentType.integer(1)).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), ArgumentItemStack.a(commandcontext, "item"), ArgumentEntity.f(commandcontext, "targets"), IntegerArgumentType.getInteger(commandcontext, "count"));
        })))));
    }

    private static int a(CommandListenerWrapper commandlistenerwrapper, ArgumentPredicateItemStack argumentpredicateitemstack, Collection<EntityPlayer> collection, int i) throws CommandSyntaxException {
        int j = argumentpredicateitemstack.a().getMaxStackSize();
        int k = j * 100;

        if (i > k) {
            commandlistenerwrapper.sendFailureMessage(new ChatMessage("commands.give.failed.toomanyitems", new Object[]{k, argumentpredicateitemstack.a(i, false).G()}));
            return 0;
        } else {
            Iterator iterator = collection.iterator();

            while (iterator.hasNext()) {
                EntityPlayer entityplayer = (EntityPlayer) iterator.next();
                int l = i;

                while (l > 0) {
                    int i1 = Math.min(j, l);

                    l -= i1;
                    ItemStack itemstack = argumentpredicateitemstack.a(i1, false);
                    boolean flag = entityplayer.getInventory().pickup(itemstack);
                    EntityItem entityitem;

                    if (flag && itemstack.isEmpty()) {
                        itemstack.setCount(1);
                        entityitem = entityplayer.drop(itemstack, false);
                        if (entityitem != null) {
                            entityitem.t();
                        }

                        entityplayer.level.playSound((EntityHuman) null, entityplayer.locX(), entityplayer.locY(), entityplayer.locZ(), SoundEffects.ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F, ((entityplayer.getRandom().nextFloat() - entityplayer.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F);
                        entityplayer.containerMenu.d();
                    } else {
                        entityitem = entityplayer.drop(itemstack, false);
                        if (entityitem != null) {
                            entityitem.o();
                            entityitem.setOwner(entityplayer.getUniqueID());
                        }
                    }
                }
            }

            if (collection.size() == 1) {
                commandlistenerwrapper.sendMessage(new ChatMessage("commands.give.success.single", new Object[]{i, argumentpredicateitemstack.a(i, false).G(), ((EntityPlayer) collection.iterator().next()).getScoreboardDisplayName()}), true);
            } else {
                commandlistenerwrapper.sendMessage(new ChatMessage("commands.give.success.single", new Object[]{i, argumentpredicateitemstack.a(i, false).G(), collection.size()}), true);
            }

            return collection.size();
        }
    }
}
