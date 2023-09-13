package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import java.util.Iterator;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.arguments.ArgumentEntity;
import net.minecraft.commands.arguments.ArgumentParticle;
import net.minecraft.commands.arguments.coordinates.ArgumentVec3;
import net.minecraft.core.IRegistry;
import net.minecraft.core.particles.ParticleParam;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.phys.Vec3D;

public class CommandParticle {

    private static final SimpleCommandExceptionType ERROR_FAILED = new SimpleCommandExceptionType(new ChatMessage("commands.particle.failed"));

    public CommandParticle() {}

    public static void register(CommandDispatcher<CommandListenerWrapper> commanddispatcher) {
        commanddispatcher.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.literal("particle").requires((commandlistenerwrapper) -> {
            return commandlistenerwrapper.hasPermission(2);
        })).then(((RequiredArgumentBuilder) net.minecraft.commands.CommandDispatcher.argument("name", ArgumentParticle.particle()).executes((commandcontext) -> {
            return sendParticles((CommandListenerWrapper) commandcontext.getSource(), ArgumentParticle.getParticle(commandcontext, "name"), ((CommandListenerWrapper) commandcontext.getSource()).getPosition(), Vec3D.ZERO, 0.0F, 0, false, ((CommandListenerWrapper) commandcontext.getSource()).getServer().getPlayerList().getPlayers());
        })).then(((RequiredArgumentBuilder) net.minecraft.commands.CommandDispatcher.argument("pos", ArgumentVec3.vec3()).executes((commandcontext) -> {
            return sendParticles((CommandListenerWrapper) commandcontext.getSource(), ArgumentParticle.getParticle(commandcontext, "name"), ArgumentVec3.getVec3(commandcontext, "pos"), Vec3D.ZERO, 0.0F, 0, false, ((CommandListenerWrapper) commandcontext.getSource()).getServer().getPlayerList().getPlayers());
        })).then(net.minecraft.commands.CommandDispatcher.argument("delta", ArgumentVec3.vec3(false)).then(net.minecraft.commands.CommandDispatcher.argument("speed", FloatArgumentType.floatArg(0.0F)).then(((RequiredArgumentBuilder) ((RequiredArgumentBuilder) net.minecraft.commands.CommandDispatcher.argument("count", IntegerArgumentType.integer(0)).executes((commandcontext) -> {
            return sendParticles((CommandListenerWrapper) commandcontext.getSource(), ArgumentParticle.getParticle(commandcontext, "name"), ArgumentVec3.getVec3(commandcontext, "pos"), ArgumentVec3.getVec3(commandcontext, "delta"), FloatArgumentType.getFloat(commandcontext, "speed"), IntegerArgumentType.getInteger(commandcontext, "count"), false, ((CommandListenerWrapper) commandcontext.getSource()).getServer().getPlayerList().getPlayers());
        })).then(((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.literal("force").executes((commandcontext) -> {
            return sendParticles((CommandListenerWrapper) commandcontext.getSource(), ArgumentParticle.getParticle(commandcontext, "name"), ArgumentVec3.getVec3(commandcontext, "pos"), ArgumentVec3.getVec3(commandcontext, "delta"), FloatArgumentType.getFloat(commandcontext, "speed"), IntegerArgumentType.getInteger(commandcontext, "count"), true, ((CommandListenerWrapper) commandcontext.getSource()).getServer().getPlayerList().getPlayers());
        })).then(net.minecraft.commands.CommandDispatcher.argument("viewers", ArgumentEntity.players()).executes((commandcontext) -> {
            return sendParticles((CommandListenerWrapper) commandcontext.getSource(), ArgumentParticle.getParticle(commandcontext, "name"), ArgumentVec3.getVec3(commandcontext, "pos"), ArgumentVec3.getVec3(commandcontext, "delta"), FloatArgumentType.getFloat(commandcontext, "speed"), IntegerArgumentType.getInteger(commandcontext, "count"), true, ArgumentEntity.getPlayers(commandcontext, "viewers"));
        })))).then(((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.literal("normal").executes((commandcontext) -> {
            return sendParticles((CommandListenerWrapper) commandcontext.getSource(), ArgumentParticle.getParticle(commandcontext, "name"), ArgumentVec3.getVec3(commandcontext, "pos"), ArgumentVec3.getVec3(commandcontext, "delta"), FloatArgumentType.getFloat(commandcontext, "speed"), IntegerArgumentType.getInteger(commandcontext, "count"), false, ((CommandListenerWrapper) commandcontext.getSource()).getServer().getPlayerList().getPlayers());
        })).then(net.minecraft.commands.CommandDispatcher.argument("viewers", ArgumentEntity.players()).executes((commandcontext) -> {
            return sendParticles((CommandListenerWrapper) commandcontext.getSource(), ArgumentParticle.getParticle(commandcontext, "name"), ArgumentVec3.getVec3(commandcontext, "pos"), ArgumentVec3.getVec3(commandcontext, "delta"), FloatArgumentType.getFloat(commandcontext, "speed"), IntegerArgumentType.getInteger(commandcontext, "count"), false, ArgumentEntity.getPlayers(commandcontext, "viewers"));
        })))))))));
    }

    private static int sendParticles(CommandListenerWrapper commandlistenerwrapper, ParticleParam particleparam, Vec3D vec3d, Vec3D vec3d1, float f, int i, boolean flag, Collection<EntityPlayer> collection) throws CommandSyntaxException {
        int j = 0;
        Iterator iterator = collection.iterator();

        while (iterator.hasNext()) {
            EntityPlayer entityplayer = (EntityPlayer) iterator.next();

            if (commandlistenerwrapper.getLevel().sendParticles(entityplayer, particleparam, flag, vec3d.x, vec3d.y, vec3d.z, i, vec3d1.x, vec3d1.y, vec3d1.z, (double) f)) {
                ++j;
            }
        }

        if (j == 0) {
            throw CommandParticle.ERROR_FAILED.create();
        } else {
            commandlistenerwrapper.sendSuccess(new ChatMessage("commands.particle.success", new Object[]{IRegistry.PARTICLE_TYPE.getKey(particleparam.getType()).toString()}), true);
            return j;
        }
    }
}
