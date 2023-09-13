package net.minecraft.server.commands;

import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import java.util.Iterator;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.arguments.ArgumentEntity;
import net.minecraft.commands.arguments.ArgumentMobEffect;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;

public class CommandEffect {

    private static final SimpleCommandExceptionType ERROR_GIVE_FAILED = new SimpleCommandExceptionType(new ChatMessage("commands.effect.give.failed"));
    private static final SimpleCommandExceptionType ERROR_CLEAR_EVERYTHING_FAILED = new SimpleCommandExceptionType(new ChatMessage("commands.effect.clear.everything.failed"));
    private static final SimpleCommandExceptionType ERROR_CLEAR_SPECIFIC_FAILED = new SimpleCommandExceptionType(new ChatMessage("commands.effect.clear.specific.failed"));

    public CommandEffect() {}

    public static void register(CommandDispatcher<CommandListenerWrapper> commanddispatcher) {
        commanddispatcher.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.literal("effect").requires((commandlistenerwrapper) -> {
            return commandlistenerwrapper.hasPermission(2);
        })).then(((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.literal("clear").executes((commandcontext) -> {
            return clearEffects((CommandListenerWrapper) commandcontext.getSource(), ImmutableList.of(((CommandListenerWrapper) commandcontext.getSource()).getEntityOrException()));
        })).then(((RequiredArgumentBuilder) net.minecraft.commands.CommandDispatcher.argument("targets", ArgumentEntity.entities()).executes((commandcontext) -> {
            return clearEffects((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.getEntities(commandcontext, "targets"));
        })).then(net.minecraft.commands.CommandDispatcher.argument("effect", ArgumentMobEffect.effect()).executes((commandcontext) -> {
            return clearEffect((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.getEntities(commandcontext, "targets"), ArgumentMobEffect.getEffect(commandcontext, "effect"));
        }))))).then(net.minecraft.commands.CommandDispatcher.literal("give").then(net.minecraft.commands.CommandDispatcher.argument("targets", ArgumentEntity.entities()).then(((RequiredArgumentBuilder) net.minecraft.commands.CommandDispatcher.argument("effect", ArgumentMobEffect.effect()).executes((commandcontext) -> {
            return giveEffect((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.getEntities(commandcontext, "targets"), ArgumentMobEffect.getEffect(commandcontext, "effect"), (Integer) null, 0, true);
        })).then(((RequiredArgumentBuilder) net.minecraft.commands.CommandDispatcher.argument("seconds", IntegerArgumentType.integer(1, 1000000)).executes((commandcontext) -> {
            return giveEffect((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.getEntities(commandcontext, "targets"), ArgumentMobEffect.getEffect(commandcontext, "effect"), IntegerArgumentType.getInteger(commandcontext, "seconds"), 0, true);
        })).then(((RequiredArgumentBuilder) net.minecraft.commands.CommandDispatcher.argument("amplifier", IntegerArgumentType.integer(0, 255)).executes((commandcontext) -> {
            return giveEffect((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.getEntities(commandcontext, "targets"), ArgumentMobEffect.getEffect(commandcontext, "effect"), IntegerArgumentType.getInteger(commandcontext, "seconds"), IntegerArgumentType.getInteger(commandcontext, "amplifier"), true);
        })).then(net.minecraft.commands.CommandDispatcher.argument("hideParticles", BoolArgumentType.bool()).executes((commandcontext) -> {
            return giveEffect((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.getEntities(commandcontext, "targets"), ArgumentMobEffect.getEffect(commandcontext, "effect"), IntegerArgumentType.getInteger(commandcontext, "seconds"), IntegerArgumentType.getInteger(commandcontext, "amplifier"), !BoolArgumentType.getBool(commandcontext, "hideParticles"));
        }))))))));
    }

    private static int giveEffect(CommandListenerWrapper commandlistenerwrapper, Collection<? extends Entity> collection, MobEffectList mobeffectlist, @Nullable Integer integer, int i, boolean flag) throws CommandSyntaxException {
        int j = 0;
        int k;

        if (integer != null) {
            if (mobeffectlist.isInstantenous()) {
                k = integer;
            } else {
                k = integer * 20;
            }
        } else if (mobeffectlist.isInstantenous()) {
            k = 1;
        } else {
            k = 600;
        }

        Iterator iterator = collection.iterator();

        while (iterator.hasNext()) {
            Entity entity = (Entity) iterator.next();

            if (entity instanceof EntityLiving) {
                MobEffect mobeffect = new MobEffect(mobeffectlist, k, i, false, flag);

                if (((EntityLiving) entity).addEffect(mobeffect, commandlistenerwrapper.getEntity())) {
                    ++j;
                }
            }
        }

        if (j == 0) {
            throw CommandEffect.ERROR_GIVE_FAILED.create();
        } else {
            if (collection.size() == 1) {
                commandlistenerwrapper.sendSuccess(new ChatMessage("commands.effect.give.success.single", new Object[]{mobeffectlist.getDisplayName(), ((Entity) collection.iterator().next()).getDisplayName(), k / 20}), true);
            } else {
                commandlistenerwrapper.sendSuccess(new ChatMessage("commands.effect.give.success.multiple", new Object[]{mobeffectlist.getDisplayName(), collection.size(), k / 20}), true);
            }

            return j;
        }
    }

    private static int clearEffects(CommandListenerWrapper commandlistenerwrapper, Collection<? extends Entity> collection) throws CommandSyntaxException {
        int i = 0;
        Iterator iterator = collection.iterator();

        while (iterator.hasNext()) {
            Entity entity = (Entity) iterator.next();

            if (entity instanceof EntityLiving && ((EntityLiving) entity).removeAllEffects()) {
                ++i;
            }
        }

        if (i == 0) {
            throw CommandEffect.ERROR_CLEAR_EVERYTHING_FAILED.create();
        } else {
            if (collection.size() == 1) {
                commandlistenerwrapper.sendSuccess(new ChatMessage("commands.effect.clear.everything.success.single", new Object[]{((Entity) collection.iterator().next()).getDisplayName()}), true);
            } else {
                commandlistenerwrapper.sendSuccess(new ChatMessage("commands.effect.clear.everything.success.multiple", new Object[]{collection.size()}), true);
            }

            return i;
        }
    }

    private static int clearEffect(CommandListenerWrapper commandlistenerwrapper, Collection<? extends Entity> collection, MobEffectList mobeffectlist) throws CommandSyntaxException {
        int i = 0;
        Iterator iterator = collection.iterator();

        while (iterator.hasNext()) {
            Entity entity = (Entity) iterator.next();

            if (entity instanceof EntityLiving && ((EntityLiving) entity).removeEffect(mobeffectlist)) {
                ++i;
            }
        }

        if (i == 0) {
            throw CommandEffect.ERROR_CLEAR_SPECIFIC_FAILED.create();
        } else {
            if (collection.size() == 1) {
                commandlistenerwrapper.sendSuccess(new ChatMessage("commands.effect.clear.specific.success.single", new Object[]{mobeffectlist.getDisplayName(), ((Entity) collection.iterator().next()).getDisplayName()}), true);
            } else {
                commandlistenerwrapper.sendSuccess(new ChatMessage("commands.effect.clear.specific.success.multiple", new Object[]{mobeffectlist.getDisplayName(), collection.size()}), true);
            }

            return i;
        }
    }
}
