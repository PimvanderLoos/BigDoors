package net.minecraft.server.commands;

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
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.arguments.ArgumentEnchantment;
import net.minecraft.commands.arguments.ArgumentEntity;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentManager;

public class CommandEnchant {

    private static final DynamicCommandExceptionType a = new DynamicCommandExceptionType((object) -> {
        return new ChatMessage("commands.enchant.failed.entity", new Object[]{object});
    });
    private static final DynamicCommandExceptionType b = new DynamicCommandExceptionType((object) -> {
        return new ChatMessage("commands.enchant.failed.itemless", new Object[]{object});
    });
    private static final DynamicCommandExceptionType c = new DynamicCommandExceptionType((object) -> {
        return new ChatMessage("commands.enchant.failed.incompatible", new Object[]{object});
    });
    private static final Dynamic2CommandExceptionType d = new Dynamic2CommandExceptionType((object, object1) -> {
        return new ChatMessage("commands.enchant.failed.level", new Object[]{object, object1});
    });
    private static final SimpleCommandExceptionType e = new SimpleCommandExceptionType(new ChatMessage("commands.enchant.failed"));

    public static void a(CommandDispatcher<CommandListenerWrapper> commanddispatcher) {
        commanddispatcher.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("enchant").requires((commandlistenerwrapper) -> {
            return commandlistenerwrapper.hasPermission(2);
        })).then(net.minecraft.commands.CommandDispatcher.a("targets", (ArgumentType) ArgumentEntity.multipleEntities()).then(((RequiredArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("enchantment", (ArgumentType) ArgumentEnchantment.a()).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.b(commandcontext, "targets"), ArgumentEnchantment.a(commandcontext, "enchantment"), 1);
        })).then(net.minecraft.commands.CommandDispatcher.a("level", (ArgumentType) IntegerArgumentType.integer(0)).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.b(commandcontext, "targets"), ArgumentEnchantment.a(commandcontext, "enchantment"), IntegerArgumentType.getInteger(commandcontext, "level"));
        })))));
    }

    private static int a(CommandListenerWrapper commandlistenerwrapper, Collection<? extends Entity> collection, Enchantment enchantment, int i) throws CommandSyntaxException {
        if (i > enchantment.getMaxLevel()) {
            throw CommandEnchant.d.create(i, enchantment.getMaxLevel());
        } else {
            int j = 0;
            Iterator iterator = collection.iterator();

            while (iterator.hasNext()) {
                Entity entity = (Entity) iterator.next();

                if (entity instanceof EntityLiving) {
                    EntityLiving entityliving = (EntityLiving) entity;
                    ItemStack itemstack = entityliving.getItemInMainHand();

                    if (!itemstack.isEmpty()) {
                        if (enchantment.canEnchant(itemstack) && EnchantmentManager.a((Collection) EnchantmentManager.a(itemstack).keySet(), enchantment)) {
                            itemstack.addEnchantment(enchantment, i);
                            ++j;
                        } else if (collection.size() == 1) {
                            throw CommandEnchant.c.create(itemstack.getItem().h(itemstack).getString());
                        }
                    } else if (collection.size() == 1) {
                        throw CommandEnchant.b.create(entityliving.getDisplayName().getString());
                    }
                } else if (collection.size() == 1) {
                    throw CommandEnchant.a.create(entity.getDisplayName().getString());
                }
            }

            if (j == 0) {
                throw CommandEnchant.e.create();
            } else {
                if (collection.size() == 1) {
                    commandlistenerwrapper.sendMessage(new ChatMessage("commands.enchant.success.single", new Object[]{enchantment.d(i), ((Entity) collection.iterator().next()).getScoreboardDisplayName()}), true);
                } else {
                    commandlistenerwrapper.sendMessage(new ChatMessage("commands.enchant.success.multiple", new Object[]{enchantment.d(i), collection.size()}), true);
                }

                return j;
            }
        }
    }
}
