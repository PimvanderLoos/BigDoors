package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.arguments.blocks.ArgumentTile;
import net.minecraft.commands.arguments.blocks.ArgumentTileLocation;
import net.minecraft.commands.arguments.coordinates.ArgumentPosition;
import net.minecraft.core.BlockPosition;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.Clearable;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.state.pattern.ShapeDetectorBlock;
import net.minecraft.world.level.levelgen.structure.StructureBoundingBox;

public class CommandSetBlock {

    private static final SimpleCommandExceptionType a = new SimpleCommandExceptionType(new ChatMessage("commands.setblock.failed"));

    public static void a(CommandDispatcher<CommandListenerWrapper> commanddispatcher) {
        commanddispatcher.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("setblock").requires((commandlistenerwrapper) -> {
            return commandlistenerwrapper.hasPermission(2);
        })).then(net.minecraft.commands.CommandDispatcher.a("pos", (ArgumentType) ArgumentPosition.a()).then(((RequiredArgumentBuilder) ((RequiredArgumentBuilder) ((RequiredArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("block", (ArgumentType) ArgumentTile.a()).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), ArgumentPosition.a(commandcontext, "pos"), ArgumentTile.a(commandcontext, "block"), CommandSetBlock.Mode.REPLACE, (Predicate) null);
        })).then(net.minecraft.commands.CommandDispatcher.a("destroy").executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), ArgumentPosition.a(commandcontext, "pos"), ArgumentTile.a(commandcontext, "block"), CommandSetBlock.Mode.DESTROY, (Predicate) null);
        }))).then(net.minecraft.commands.CommandDispatcher.a("keep").executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), ArgumentPosition.a(commandcontext, "pos"), ArgumentTile.a(commandcontext, "block"), CommandSetBlock.Mode.REPLACE, (shapedetectorblock) -> {
                return shapedetectorblock.c().isEmpty(shapedetectorblock.getPosition());
            });
        }))).then(net.minecraft.commands.CommandDispatcher.a("replace").executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), ArgumentPosition.a(commandcontext, "pos"), ArgumentTile.a(commandcontext, "block"), CommandSetBlock.Mode.REPLACE, (Predicate) null);
        })))));
    }

    private static int a(CommandListenerWrapper commandlistenerwrapper, BlockPosition blockposition, ArgumentTileLocation argumenttilelocation, CommandSetBlock.Mode commandsetblock_mode, @Nullable Predicate<ShapeDetectorBlock> predicate) throws CommandSyntaxException {
        WorldServer worldserver = commandlistenerwrapper.getWorld();

        if (predicate != null && !predicate.test(new ShapeDetectorBlock(worldserver, blockposition, true))) {
            throw CommandSetBlock.a.create();
        } else {
            boolean flag;

            if (commandsetblock_mode == CommandSetBlock.Mode.DESTROY) {
                worldserver.b(blockposition, true);
                flag = !argumenttilelocation.a().isAir() || !worldserver.getType(blockposition).isAir();
            } else {
                TileEntity tileentity = worldserver.getTileEntity(blockposition);

                Clearable.a(tileentity);
                flag = true;
            }

            if (flag && !argumenttilelocation.a(worldserver, blockposition, 2)) {
                throw CommandSetBlock.a.create();
            } else {
                worldserver.update(blockposition, argumenttilelocation.a().getBlock());
                commandlistenerwrapper.sendMessage(new ChatMessage("commands.setblock.success", new Object[]{blockposition.getX(), blockposition.getY(), blockposition.getZ()}), true);
                return 1;
            }
        }
    }

    public interface Filter {

        @Nullable
        ArgumentTileLocation filter(StructureBoundingBox structureboundingbox, BlockPosition blockposition, ArgumentTileLocation argumenttilelocation, WorldServer worldserver);
    }

    public static enum Mode {

        REPLACE, DESTROY;

        private Mode() {}
    }
}
