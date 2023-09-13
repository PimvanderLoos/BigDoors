package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
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

    private static final SimpleCommandExceptionType a = new SimpleCommandExceptionType(new ChatMessage("commands.particle.failed"));

    public static void a(CommandDispatcher<CommandListenerWrapper> commanddispatcher) {
        commanddispatcher.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("particle").requires((commandlistenerwrapper) -> {
            return commandlistenerwrapper.hasPermission(2);
        })).then(((RequiredArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("name", (ArgumentType) ArgumentParticle.a()).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), ArgumentParticle.a(commandcontext, "name"), ((CommandListenerWrapper) commandcontext.getSource()).getPosition(), Vec3D.ORIGIN, 0.0F, 0, false, ((CommandListenerWrapper) commandcontext.getSource()).getServer().getPlayerList().getPlayers());
        })).then(((RequiredArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("pos", (ArgumentType) ArgumentVec3.a()).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), ArgumentParticle.a(commandcontext, "name"), ArgumentVec3.a(commandcontext, "pos"), Vec3D.ORIGIN, 0.0F, 0, false, ((CommandListenerWrapper) commandcontext.getSource()).getServer().getPlayerList().getPlayers());
        })).then(net.minecraft.commands.CommandDispatcher.a("delta", (ArgumentType) ArgumentVec3.a(false)).then(net.minecraft.commands.CommandDispatcher.a("speed", (ArgumentType) FloatArgumentType.floatArg(0.0F)).then(((RequiredArgumentBuilder) ((RequiredArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("count", (ArgumentType) IntegerArgumentType.integer(0)).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), ArgumentParticle.a(commandcontext, "name"), ArgumentVec3.a(commandcontext, "pos"), ArgumentVec3.a(commandcontext, "delta"), FloatArgumentType.getFloat(commandcontext, "speed"), IntegerArgumentType.getInteger(commandcontext, "count"), false, ((CommandListenerWrapper) commandcontext.getSource()).getServer().getPlayerList().getPlayers());
        })).then(((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("force").executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), ArgumentParticle.a(commandcontext, "name"), ArgumentVec3.a(commandcontext, "pos"), ArgumentVec3.a(commandcontext, "delta"), FloatArgumentType.getFloat(commandcontext, "speed"), IntegerArgumentType.getInteger(commandcontext, "count"), true, ((CommandListenerWrapper) commandcontext.getSource()).getServer().getPlayerList().getPlayers());
        })).then(net.minecraft.commands.CommandDispatcher.a("viewers", (ArgumentType) ArgumentEntity.d()).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), ArgumentParticle.a(commandcontext, "name"), ArgumentVec3.a(commandcontext, "pos"), ArgumentVec3.a(commandcontext, "delta"), FloatArgumentType.getFloat(commandcontext, "speed"), IntegerArgumentType.getInteger(commandcontext, "count"), true, ArgumentEntity.f(commandcontext, "viewers"));
        })))).then(((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("normal").executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), ArgumentParticle.a(commandcontext, "name"), ArgumentVec3.a(commandcontext, "pos"), ArgumentVec3.a(commandcontext, "delta"), FloatArgumentType.getFloat(commandcontext, "speed"), IntegerArgumentType.getInteger(commandcontext, "count"), false, ((CommandListenerWrapper) commandcontext.getSource()).getServer().getPlayerList().getPlayers());
        })).then(net.minecraft.commands.CommandDispatcher.a("viewers", (ArgumentType) ArgumentEntity.d()).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), ArgumentParticle.a(commandcontext, "name"), ArgumentVec3.a(commandcontext, "pos"), ArgumentVec3.a(commandcontext, "delta"), FloatArgumentType.getFloat(commandcontext, "speed"), IntegerArgumentType.getInteger(commandcontext, "count"), false, ArgumentEntity.f(commandcontext, "viewers"));
        })))))))));
    }

    private static int a(CommandListenerWrapper commandlistenerwrapper, ParticleParam particleparam, Vec3D vec3d, Vec3D vec3d1, float f, int i, boolean flag, Collection<EntityPlayer> collection) throws CommandSyntaxException {
        int j = 0;
        Iterator iterator = collection.iterator();

        while (iterator.hasNext()) {
            EntityPlayer entityplayer = (EntityPlayer) iterator.next();

            if (commandlistenerwrapper.getWorld().a(entityplayer, particleparam, flag, vec3d.x, vec3d.y, vec3d.z, i, vec3d1.x, vec3d1.y, vec3d1.z, (double) f)) {
                ++j;
            }
        }

        if (j == 0) {
            throw CommandParticle.a.create();
        } else {
            commandlistenerwrapper.sendMessage(new ChatMessage("commands.particle.success", new Object[]{IRegistry.PARTICLE_TYPE.getKey(particleparam.getParticle()).toString()}), true);
            return j;
        }
    }
}
