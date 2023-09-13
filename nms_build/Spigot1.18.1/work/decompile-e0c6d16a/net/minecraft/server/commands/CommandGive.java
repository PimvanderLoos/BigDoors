package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
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

    public static void register(CommandDispatcher<CommandListenerWrapper> commanddispatcher) {
        commanddispatcher.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.literal("give").requires((commandlistenerwrapper) -> {
            return commandlistenerwrapper.hasPermission(2);
        })).then(net.minecraft.commands.CommandDispatcher.argument("targets", ArgumentEntity.players()).then(((RequiredArgumentBuilder) net.minecraft.commands.CommandDispatcher.argument("item", ArgumentItemStack.item()).executes((commandcontext) -> {
            return giveItem((CommandListenerWrapper) commandcontext.getSource(), ArgumentItemStack.getItem(commandcontext, "item"), ArgumentEntity.getPlayers(commandcontext, "targets"), 1);
        })).then(net.minecraft.commands.CommandDispatcher.argument("count", IntegerArgumentType.integer(1)).executes((commandcontext) -> {
            return giveItem((CommandListenerWrapper) commandcontext.getSource(), ArgumentItemStack.getItem(commandcontext, "item"), ArgumentEntity.getPlayers(commandcontext, "targets"), IntegerArgumentType.getInteger(commandcontext, "count"));
        })))));
    }

    private static int giveItem(CommandListenerWrapper commandlistenerwrapper, ArgumentPredicateItemStack argumentpredicateitemstack, Collection<EntityPlayer> collection, int i) throws CommandSyntaxException {
        int j = argumentpredicateitemstack.getItem().getMaxStackSize();
        int k = j * 100;

        if (i > k) {
            commandlistenerwrapper.sendFailure(new ChatMessage("commands.give.failed.toomanyitems", new Object[]{k, argumentpredicateitemstack.createItemStack(i, false).getDisplayName()}));
            return 0;
        } else {
            Iterator iterator = collection.iterator();

            while (iterator.hasNext()) {
                EntityPlayer entityplayer = (EntityPlayer) iterator.next();
                int l = i;

                while (l > 0) {
                    int i1 = Math.min(j, l);

                    l -= i1;
                    ItemStack itemstack = argumentpredicateitemstack.createItemStack(i1, false);
                    boolean flag = entityplayer.getInventory().add(itemstack);
                    EntityItem entityitem;

                    if (flag && itemstack.isEmpty()) {
                        itemstack.setCount(1);
                        entityitem = entityplayer.drop(itemstack, false);
                        if (entityitem != null) {
                            entityitem.makeFakeItem();
                        }

                        entityplayer.level.playSound((EntityHuman) null, entityplayer.getX(), entityplayer.getY(), entityplayer.getZ(), SoundEffects.ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F, ((entityplayer.getRandom().nextFloat() - entityplayer.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F);
                        entityplayer.containerMenu.broadcastChanges();
                    } else {
                        entityitem = entityplayer.drop(itemstack, false);
                        if (entityitem != null) {
                            entityitem.setNoPickUpDelay();
                            entityitem.setOwner(entityplayer.getUUID());
                        }
                    }
                }
            }

            if (collection.size() == 1) {
                commandlistenerwrapper.sendSuccess(new ChatMessage("commands.give.success.single", new Object[]{i, argumentpredicateitemstack.createItemStack(i, false).getDisplayName(), ((EntityPlayer) collection.iterator().next()).getDisplayName()}), true);
            } else {
                commandlistenerwrapper.sendSuccess(new ChatMessage("commands.give.success.single", new Object[]{i, argumentpredicateitemstack.createItemStack(i, false).getDisplayName(), collection.size()}), true);
            }

            return collection.size();
        }
    }
}
