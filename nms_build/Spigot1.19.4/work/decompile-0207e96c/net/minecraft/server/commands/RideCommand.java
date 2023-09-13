package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.arguments.ArgumentEntity;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityTypes;

public class RideCommand {

    private static final DynamicCommandExceptionType ERROR_NOT_RIDING = new DynamicCommandExceptionType((object) -> {
        return IChatBaseComponent.translatable("commands.ride.not_riding", object);
    });
    private static final Dynamic2CommandExceptionType ERROR_ALREADY_RIDING = new Dynamic2CommandExceptionType((object, object1) -> {
        return IChatBaseComponent.translatable("commands.ride.already_riding", object, object1);
    });
    private static final Dynamic2CommandExceptionType ERROR_MOUNT_FAILED = new Dynamic2CommandExceptionType((object, object1) -> {
        return IChatBaseComponent.translatable("commands.ride.mount.failure.generic", object, object1);
    });
    private static final SimpleCommandExceptionType ERROR_MOUNTING_PLAYER = new SimpleCommandExceptionType(IChatBaseComponent.translatable("commands.ride.mount.failure.cant_ride_players"));
    private static final SimpleCommandExceptionType ERROR_MOUNTING_LOOP = new SimpleCommandExceptionType(IChatBaseComponent.translatable("commands.ride.mount.failure.loop"));
    private static final SimpleCommandExceptionType ERROR_WRONG_DIMENSION = new SimpleCommandExceptionType(IChatBaseComponent.translatable("commands.ride.mount.failure.wrong_dimension"));

    public RideCommand() {}

    public static void register(CommandDispatcher<CommandListenerWrapper> commanddispatcher) {
        commanddispatcher.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.literal("ride").requires((commandlistenerwrapper) -> {
            return commandlistenerwrapper.hasPermission(2);
        })).then(((RequiredArgumentBuilder) net.minecraft.commands.CommandDispatcher.argument("target", ArgumentEntity.entity()).then(net.minecraft.commands.CommandDispatcher.literal("mount").then(net.minecraft.commands.CommandDispatcher.argument("vehicle", ArgumentEntity.entity()).executes((commandcontext) -> {
            return mount((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.getEntity(commandcontext, "target"), ArgumentEntity.getEntity(commandcontext, "vehicle"));
        })))).then(net.minecraft.commands.CommandDispatcher.literal("dismount").executes((commandcontext) -> {
            return dismount((CommandListenerWrapper) commandcontext.getSource(), ArgumentEntity.getEntity(commandcontext, "target"));
        }))));
    }

    private static int mount(CommandListenerWrapper commandlistenerwrapper, Entity entity, Entity entity1) throws CommandSyntaxException {
        Entity entity2 = entity.getVehicle();

        if (entity2 != null) {
            throw RideCommand.ERROR_ALREADY_RIDING.create(entity.getDisplayName(), entity2.getDisplayName());
        } else if (entity1.getType() == EntityTypes.PLAYER) {
            throw RideCommand.ERROR_MOUNTING_PLAYER.create();
        } else if (entity.getSelfAndPassengers().anyMatch((entity3) -> {
            return entity3 == entity1;
        })) {
            throw RideCommand.ERROR_MOUNTING_LOOP.create();
        } else if (entity.getLevel() != entity1.getLevel()) {
            throw RideCommand.ERROR_WRONG_DIMENSION.create();
        } else if (!entity.startRiding(entity1, true)) {
            throw RideCommand.ERROR_MOUNT_FAILED.create(entity.getDisplayName(), entity1.getDisplayName());
        } else {
            commandlistenerwrapper.sendSuccess(IChatBaseComponent.translatable("commands.ride.mount.success", entity.getDisplayName(), entity1.getDisplayName()), true);
            return 1;
        }
    }

    private static int dismount(CommandListenerWrapper commandlistenerwrapper, Entity entity) throws CommandSyntaxException {
        Entity entity1 = entity.getVehicle();

        if (entity1 == null) {
            throw RideCommand.ERROR_NOT_RIDING.create(entity.getDisplayName());
        } else {
            entity.stopRiding();
            commandlistenerwrapper.sendSuccess(IChatBaseComponent.translatable("commands.ride.dismount.success", entity.getDisplayName(), entity1.getDisplayName()), true);
            return 1;
        }
    }
}
