package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
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

    private static final SimpleCommandExceptionType ERROR_FAILED = new SimpleCommandExceptionType(new ChatMessage("commands.setblock.failed"));

    public CommandSetBlock() {}

    public static void register(CommandDispatcher<CommandListenerWrapper> commanddispatcher) {
        commanddispatcher.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.literal("setblock").requires((commandlistenerwrapper) -> {
            return commandlistenerwrapper.hasPermission(2);
        })).then(net.minecraft.commands.CommandDispatcher.argument("pos", ArgumentPosition.blockPos()).then(((RequiredArgumentBuilder) ((RequiredArgumentBuilder) ((RequiredArgumentBuilder) net.minecraft.commands.CommandDispatcher.argument("block", ArgumentTile.block()).executes((commandcontext) -> {
            return setBlock((CommandListenerWrapper) commandcontext.getSource(), ArgumentPosition.getLoadedBlockPos(commandcontext, "pos"), ArgumentTile.getBlock(commandcontext, "block"), CommandSetBlock.Mode.REPLACE, (Predicate) null);
        })).then(net.minecraft.commands.CommandDispatcher.literal("destroy").executes((commandcontext) -> {
            return setBlock((CommandListenerWrapper) commandcontext.getSource(), ArgumentPosition.getLoadedBlockPos(commandcontext, "pos"), ArgumentTile.getBlock(commandcontext, "block"), CommandSetBlock.Mode.DESTROY, (Predicate) null);
        }))).then(net.minecraft.commands.CommandDispatcher.literal("keep").executes((commandcontext) -> {
            return setBlock((CommandListenerWrapper) commandcontext.getSource(), ArgumentPosition.getLoadedBlockPos(commandcontext, "pos"), ArgumentTile.getBlock(commandcontext, "block"), CommandSetBlock.Mode.REPLACE, (shapedetectorblock) -> {
                return shapedetectorblock.getLevel().isEmptyBlock(shapedetectorblock.getPos());
            });
        }))).then(net.minecraft.commands.CommandDispatcher.literal("replace").executes((commandcontext) -> {
            return setBlock((CommandListenerWrapper) commandcontext.getSource(), ArgumentPosition.getLoadedBlockPos(commandcontext, "pos"), ArgumentTile.getBlock(commandcontext, "block"), CommandSetBlock.Mode.REPLACE, (Predicate) null);
        })))));
    }

    private static int setBlock(CommandListenerWrapper commandlistenerwrapper, BlockPosition blockposition, ArgumentTileLocation argumenttilelocation, CommandSetBlock.Mode commandsetblock_mode, @Nullable Predicate<ShapeDetectorBlock> predicate) throws CommandSyntaxException {
        WorldServer worldserver = commandlistenerwrapper.getLevel();

        if (predicate != null && !predicate.test(new ShapeDetectorBlock(worldserver, blockposition, true))) {
            throw CommandSetBlock.ERROR_FAILED.create();
        } else {
            boolean flag;

            if (commandsetblock_mode == CommandSetBlock.Mode.DESTROY) {
                worldserver.destroyBlock(blockposition, true);
                flag = !argumenttilelocation.getState().isAir() || !worldserver.getBlockState(blockposition).isAir();
            } else {
                TileEntity tileentity = worldserver.getBlockEntity(blockposition);

                Clearable.tryClear(tileentity);
                flag = true;
            }

            if (flag && !argumenttilelocation.place(worldserver, blockposition, 2)) {
                throw CommandSetBlock.ERROR_FAILED.create();
            } else {
                worldserver.blockUpdated(blockposition, argumenttilelocation.getState().getBlock());
                commandlistenerwrapper.sendSuccess(new ChatMessage("commands.setblock.success", new Object[]{blockposition.getX(), blockposition.getY(), blockposition.getZ()}), true);
                return 1;
            }
        }
    }

    public static enum Mode {

        REPLACE, DESTROY;

        private Mode() {}
    }

    public interface Filter {

        @Nullable
        ArgumentTileLocation filter(StructureBoundingBox structureboundingbox, BlockPosition blockposition, ArgumentTileLocation argumenttilelocation, WorldServer worldserver);
    }
}
