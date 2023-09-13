package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.arguments.ArgumentEntity;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.commands.arguments.coordinates.ArgumentVec3;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;

public class DamageCommand {

    private static final SimpleCommandExceptionType ERROR_INVULNERABLE = new SimpleCommandExceptionType(IChatBaseComponent.translatable("commands.damage.invulnerable"));

    public DamageCommand() {}

    public static void register(CommandDispatcher<CommandListenerWrapper> commanddispatcher, CommandBuildContext commandbuildcontext) {
        commanddispatcher.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.literal("damage").requires((commandlistenerwrapper) -> {
            return commandlistenerwrapper.hasPermission(2);
        })).then(net.minecraft.commands.CommandDispatcher.argument("target", ArgumentEntity.entity()).then(((RequiredArgumentBuilder) net.minecraft.commands.CommandDispatcher.argument("amount", FloatArgumentType.floatArg(0.0F)).executes((commandcontext) -> {
            return damage((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.getEntity(commandcontext, "target"), FloatArgumentType.getFloat(commandcontext, "amount"), ((CommandListenerWrapper) commandcontext.getSource()).getLevel().damageSources().generic());
        })).then(((RequiredArgumentBuilder) ((RequiredArgumentBuilder) net.minecraft.commands.CommandDispatcher.argument("damageType", ResourceArgument.resource(commandbuildcontext, Registries.DAMAGE_TYPE)).executes((commandcontext) -> {
            return damage((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.getEntity(commandcontext, "target"), FloatArgumentType.getFloat(commandcontext, "amount"), new DamageSource(ResourceArgument.getResource(commandcontext, "damageType", Registries.DAMAGE_TYPE)));
        })).then(net.minecraft.commands.CommandDispatcher.literal("at").then(net.minecraft.commands.CommandDispatcher.argument("location", ArgumentVec3.vec3()).executes((commandcontext) -> {
            return damage((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.getEntity(commandcontext, "target"), FloatArgumentType.getFloat(commandcontext, "amount"), new DamageSource(ResourceArgument.getResource(commandcontext, "damageType", Registries.DAMAGE_TYPE), ArgumentVec3.getVec3(commandcontext, "location")));
        })))).then(net.minecraft.commands.CommandDispatcher.literal("by").then(((RequiredArgumentBuilder) net.minecraft.commands.CommandDispatcher.argument("entity", ArgumentEntity.entity()).executes((commandcontext) -> {
            return damage((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.getEntity(commandcontext, "target"), FloatArgumentType.getFloat(commandcontext, "amount"), new DamageSource(ResourceArgument.getResource(commandcontext, "damageType", Registries.DAMAGE_TYPE), ArgumentEntity.getEntity(commandcontext, "entity")));
        })).then(net.minecraft.commands.CommandDispatcher.literal("from").then(net.minecraft.commands.CommandDispatcher.argument("cause", ArgumentEntity.entity()).executes((commandcontext) -> {
            return damage((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.getEntity(commandcontext, "target"), FloatArgumentType.getFloat(commandcontext, "amount"), new DamageSource(ResourceArgument.getResource(commandcontext, "damageType", Registries.DAMAGE_TYPE), ArgumentEntity.getEntity(commandcontext, "entity"), ArgumentEntity.getEntity(commandcontext, "cause")));
        })))))))));
    }

    private static int damage(CommandListenerWrapper commandlistenerwrapper, Entity entity, float f, DamageSource damagesource) throws CommandSyntaxException {
        if (entity.hurt(damagesource, f)) {
            commandlistenerwrapper.sendSuccess(IChatBaseComponent.translatable("commands.damage.success", f, entity.getDisplayName()), true);
            return 1;
        } else {
            throw DamageCommand.ERROR_INVULNERABLE.create();
        }
    }
}
