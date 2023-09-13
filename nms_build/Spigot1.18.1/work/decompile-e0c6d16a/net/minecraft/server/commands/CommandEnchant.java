package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
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

    private static final DynamicCommandExceptionType ERROR_NOT_LIVING_ENTITY = new DynamicCommandExceptionType((object) -> {
        return new ChatMessage("commands.enchant.failed.entity", new Object[]{object});
    });
    private static final DynamicCommandExceptionType ERROR_NO_ITEM = new DynamicCommandExceptionType((object) -> {
        return new ChatMessage("commands.enchant.failed.itemless", new Object[]{object});
    });
    private static final DynamicCommandExceptionType ERROR_INCOMPATIBLE = new DynamicCommandExceptionType((object) -> {
        return new ChatMessage("commands.enchant.failed.incompatible", new Object[]{object});
    });
    private static final Dynamic2CommandExceptionType ERROR_LEVEL_TOO_HIGH = new Dynamic2CommandExceptionType((object, object1) -> {
        return new ChatMessage("commands.enchant.failed.level", new Object[]{object, object1});
    });
    private static final SimpleCommandExceptionType ERROR_NOTHING_HAPPENED = new SimpleCommandExceptionType(new ChatMessage("commands.enchant.failed"));

    public CommandEnchant() {}

    public static void register(CommandDispatcher<CommandListenerWrapper> commanddispatcher) {
        commanddispatcher.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.literal("enchant").requires((commandlistenerwrapper) -> {
            return commandlistenerwrapper.hasPermission(2);
        })).then(net.minecraft.commands.CommandDispatcher.argument("targets", ArgumentEntity.entities()).then(((RequiredArgumentBuilder) net.minecraft.commands.CommandDispatcher.argument("enchantment", ArgumentEnchantment.enchantment()).executes((commandcontext) -> {
            return enchant((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.getEntities(commandcontext, "targets"), ArgumentEnchantment.getEnchantment(commandcontext, "enchantment"), 1);
        })).then(net.minecraft.commands.CommandDispatcher.argument("level", IntegerArgumentType.integer(0)).executes((commandcontext) -> {
            return enchant((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.getEntities(commandcontext, "targets"), ArgumentEnchantment.getEnchantment(commandcontext, "enchantment"), IntegerArgumentType.getInteger(commandcontext, "level"));
        })))));
    }

    private static int enchant(CommandListenerWrapper commandlistenerwrapper, Collection<? extends Entity> collection, Enchantment enchantment, int i) throws CommandSyntaxException {
        if (i > enchantment.getMaxLevel()) {
            throw CommandEnchant.ERROR_LEVEL_TOO_HIGH.create(i, enchantment.getMaxLevel());
        } else {
            int j = 0;
            Iterator iterator = collection.iterator();

            while (iterator.hasNext()) {
                Entity entity = (Entity) iterator.next();

                if (entity instanceof EntityLiving) {
                    EntityLiving entityliving = (EntityLiving) entity;
                    ItemStack itemstack = entityliving.getMainHandItem();

                    if (!itemstack.isEmpty()) {
                        if (enchantment.canEnchant(itemstack) && EnchantmentManager.isEnchantmentCompatible(EnchantmentManager.getEnchantments(itemstack).keySet(), enchantment)) {
                            itemstack.enchant(enchantment, i);
                            ++j;
                        } else if (collection.size() == 1) {
                            throw CommandEnchant.ERROR_INCOMPATIBLE.create(itemstack.getItem().getName(itemstack).getString());
                        }
                    } else if (collection.size() == 1) {
                        throw CommandEnchant.ERROR_NO_ITEM.create(entityliving.getName().getString());
                    }
                } else if (collection.size() == 1) {
                    throw CommandEnchant.ERROR_NOT_LIVING_ENTITY.create(entity.getName().getString());
                }
            }

            if (j == 0) {
                throw CommandEnchant.ERROR_NOTHING_HAPPENED.create();
            } else {
                if (collection.size() == 1) {
                    commandlistenerwrapper.sendSuccess(new ChatMessage("commands.enchant.success.single", new Object[]{enchantment.getFullname(i), ((Entity) collection.iterator().next()).getDisplayName()}), true);
                } else {
                    commandlistenerwrapper.sendSuccess(new ChatMessage("commands.enchant.success.multiple", new Object[]{enchantment.getFullname(i), collection.size()}), true);
                }

                return j;
            }
        }
    }
}
