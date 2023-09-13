package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import java.util.Iterator;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.arguments.ArgumentEntity;
import net.minecraft.commands.arguments.ArgumentMinecraftKeyRegistered;
import net.minecraft.commands.arguments.coordinates.ArgumentVec3;
import net.minecraft.commands.synchronization.CompletionProviders;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.network.protocol.game.PacketPlayOutCustomSoundEffect;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.util.MathHelper;
import net.minecraft.world.phys.Vec3D;

public class CommandPlaySound {

    private static final SimpleCommandExceptionType a = new SimpleCommandExceptionType(new ChatMessage("commands.playsound.failed"));

    public static void a(CommandDispatcher<CommandListenerWrapper> commanddispatcher) {
        RequiredArgumentBuilder<CommandListenerWrapper, MinecraftKey> requiredargumentbuilder = net.minecraft.commands.CommandDispatcher.a("sound", (ArgumentType) ArgumentMinecraftKeyRegistered.a()).suggests(CompletionProviders.c);
        SoundCategory[] asoundcategory = SoundCategory.values();
        int i = asoundcategory.length;

        for (int j = 0; j < i; ++j) {
            SoundCategory soundcategory = asoundcategory[j];

            requiredargumentbuilder.then(a(soundcategory));
        }

        commanddispatcher.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("playsound").requires((commandlistenerwrapper) -> {
            return commandlistenerwrapper.hasPermission(2);
        })).then(requiredargumentbuilder));
    }

    private static LiteralArgumentBuilder<CommandListenerWrapper> a(SoundCategory soundcategory) {
        return (LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.a(soundcategory.a()).then(((RequiredArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("targets", (ArgumentType) ArgumentEntity.d()).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.f(commandcontext, "targets"), ArgumentMinecraftKeyRegistered.e(commandcontext, "sound"), soundcategory, ((CommandListenerWrapper) commandcontext.getSource()).getPosition(), 1.0F, 1.0F, 0.0F);
        })).then(((RequiredArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("pos", (ArgumentType) ArgumentVec3.a()).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.f(commandcontext, "targets"), ArgumentMinecraftKeyRegistered.e(commandcontext, "sound"), soundcategory, ArgumentVec3.a(commandcontext, "pos"), 1.0F, 1.0F, 0.0F);
        })).then(((RequiredArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("volume", (ArgumentType) FloatArgumentType.floatArg(0.0F)).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.f(commandcontext, "targets"), ArgumentMinecraftKeyRegistered.e(commandcontext, "sound"), soundcategory, ArgumentVec3.a(commandcontext, "pos"), (Float) commandcontext.getArgument("volume", Float.class), 1.0F, 0.0F);
        })).then(((RequiredArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("pitch", (ArgumentType) FloatArgumentType.floatArg(0.0F, 2.0F)).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.f(commandcontext, "targets"), ArgumentMinecraftKeyRegistered.e(commandcontext, "sound"), soundcategory, ArgumentVec3.a(commandcontext, "pos"), (Float) commandcontext.getArgument("volume", Float.class), (Float) commandcontext.getArgument("pitch", Float.class), 0.0F);
        })).then(net.minecraft.commands.CommandDispatcher.a("minVolume", (ArgumentType) FloatArgumentType.floatArg(0.0F, 1.0F)).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.f(commandcontext, "targets"), ArgumentMinecraftKeyRegistered.e(commandcontext, "sound"), soundcategory, ArgumentVec3.a(commandcontext, "pos"), (Float) commandcontext.getArgument("volume", Float.class), (Float) commandcontext.getArgument("pitch", Float.class), (Float) commandcontext.getArgument("minVolume", Float.class));
        }))))));
    }

    private static int a(CommandListenerWrapper commandlistenerwrapper, Collection<EntityPlayer> collection, MinecraftKey minecraftkey, SoundCategory soundcategory, Vec3D vec3d, float f, float f1, float f2) throws CommandSyntaxException {
        double d0 = Math.pow(f > 1.0F ? (double) (f * 16.0F) : 16.0D, 2.0D);
        int i = 0;
        Iterator iterator = collection.iterator();

        while (iterator.hasNext()) {
            EntityPlayer entityplayer = (EntityPlayer) iterator.next();
            double d1 = vec3d.x - entityplayer.locX();
            double d2 = vec3d.y - entityplayer.locY();
            double d3 = vec3d.z - entityplayer.locZ();
            double d4 = d1 * d1 + d2 * d2 + d3 * d3;
            Vec3D vec3d1 = vec3d;
            float f3 = f;

            if (d4 > d0) {
                if (f2 <= 0.0F) {
                    continue;
                }

                double d5 = (double) MathHelper.sqrt(d4);

                vec3d1 = new Vec3D(entityplayer.locX() + d1 / d5 * 2.0D, entityplayer.locY() + d2 / d5 * 2.0D, entityplayer.locZ() + d3 / d5 * 2.0D);
                f3 = f2;
            }

            entityplayer.playerConnection.sendPacket(new PacketPlayOutCustomSoundEffect(minecraftkey, soundcategory, vec3d1, f3, f1));
            ++i;
        }

        if (i == 0) {
            throw CommandPlaySound.a.create();
        } else {
            if (collection.size() == 1) {
                commandlistenerwrapper.sendMessage(new ChatMessage("commands.playsound.success.single", new Object[]{minecraftkey, ((EntityPlayer) collection.iterator().next()).getScoreboardDisplayName()}), true);
            } else {
                commandlistenerwrapper.sendMessage(new ChatMessage("commands.playsound.success.multiple", new Object[]{minecraftkey, collection.size()}), true);
            }

            return i;
        }
    }
}
