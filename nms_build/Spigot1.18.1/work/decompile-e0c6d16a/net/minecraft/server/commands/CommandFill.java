package net.minecraft.server.commands;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.arguments.blocks.ArgumentBlockPredicate;
import net.minecraft.commands.arguments.blocks.ArgumentTile;
import net.minecraft.commands.arguments.blocks.ArgumentTileLocation;
import net.minecraft.commands.arguments.coordinates.ArgumentPosition;
import net.minecraft.core.BlockPosition;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.Clearable;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.state.pattern.ShapeDetectorBlock;
import net.minecraft.world.level.levelgen.structure.StructureBoundingBox;

public class CommandFill {

    private static final int MAX_FILL_AREA = 32768;
    private static final Dynamic2CommandExceptionType ERROR_AREA_TOO_LARGE = new Dynamic2CommandExceptionType((object, object1) -> {
        return new ChatMessage("commands.fill.toobig", new Object[]{object, object1});
    });
    static final ArgumentTileLocation HOLLOW_CORE = new ArgumentTileLocation(Blocks.AIR.defaultBlockState(), Collections.emptySet(), (NBTTagCompound) null);
    private static final SimpleCommandExceptionType ERROR_FAILED = new SimpleCommandExceptionType(new ChatMessage("commands.fill.failed"));

    public CommandFill() {}

    public static void register(CommandDispatcher<CommandListenerWrapper> commanddispatcher) {
        commanddispatcher.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.literal("fill").requires((commandlistenerwrapper) -> {
            return commandlistenerwrapper.hasPermission(2);
        })).then(net.minecraft.commands.CommandDispatcher.argument("from", ArgumentPosition.blockPos()).then(net.minecraft.commands.CommandDispatcher.argument("to", ArgumentPosition.blockPos()).then(((RequiredArgumentBuilder) ((RequiredArgumentBuilder) ((RequiredArgumentBuilder) ((RequiredArgumentBuilder) ((RequiredArgumentBuilder) net.minecraft.commands.CommandDispatcher.argument("block", ArgumentTile.block()).executes((commandcontext) -> {
            return fillBlocks((CommandListenerWrapper) commandcontext.getSource(), StructureBoundingBox.fromCorners(ArgumentPosition.getLoadedBlockPos(commandcontext, "from"), ArgumentPosition.getLoadedBlockPos(commandcontext, "to")), ArgumentTile.getBlock(commandcontext, "block"), CommandFill.Mode.REPLACE, (Predicate) null);
        })).then(((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.literal("replace").executes((commandcontext) -> {
            return fillBlocks((CommandListenerWrapper) commandcontext.getSource(), StructureBoundingBox.fromCorners(ArgumentPosition.getLoadedBlockPos(commandcontext, "from"), ArgumentPosition.getLoadedBlockPos(commandcontext, "to")), ArgumentTile.getBlock(commandcontext, "block"), CommandFill.Mode.REPLACE, (Predicate) null);
        })).then(net.minecraft.commands.CommandDispatcher.argument("filter", ArgumentBlockPredicate.blockPredicate()).executes((commandcontext) -> {
            return fillBlocks((CommandListenerWrapper) commandcontext.getSource(), StructureBoundingBox.fromCorners(ArgumentPosition.getLoadedBlockPos(commandcontext, "from"), ArgumentPosition.getLoadedBlockPos(commandcontext, "to")), ArgumentTile.getBlock(commandcontext, "block"), CommandFill.Mode.REPLACE, ArgumentBlockPredicate.getBlockPredicate(commandcontext, "filter"));
        })))).then(net.minecraft.commands.CommandDispatcher.literal("keep").executes((commandcontext) -> {
            return fillBlocks((CommandListenerWrapper) commandcontext.getSource(), StructureBoundingBox.fromCorners(ArgumentPosition.getLoadedBlockPos(commandcontext, "from"), ArgumentPosition.getLoadedBlockPos(commandcontext, "to")), ArgumentTile.getBlock(commandcontext, "block"), CommandFill.Mode.REPLACE, (shapedetectorblock) -> {
                return shapedetectorblock.getLevel().isEmptyBlock(shapedetectorblock.getPos());
            });
        }))).then(net.minecraft.commands.CommandDispatcher.literal("outline").executes((commandcontext) -> {
            return fillBlocks((CommandListenerWrapper) commandcontext.getSource(), StructureBoundingBox.fromCorners(ArgumentPosition.getLoadedBlockPos(commandcontext, "from"), ArgumentPosition.getLoadedBlockPos(commandcontext, "to")), ArgumentTile.getBlock(commandcontext, "block"), CommandFill.Mode.OUTLINE, (Predicate) null);
        }))).then(net.minecraft.commands.CommandDispatcher.literal("hollow").executes((commandcontext) -> {
            return fillBlocks((CommandListenerWrapper) commandcontext.getSource(), StructureBoundingBox.fromCorners(ArgumentPosition.getLoadedBlockPos(commandcontext, "from"), ArgumentPosition.getLoadedBlockPos(commandcontext, "to")), ArgumentTile.getBlock(commandcontext, "block"), CommandFill.Mode.HOLLOW, (Predicate) null);
        }))).then(net.minecraft.commands.CommandDispatcher.literal("destroy").executes((commandcontext) -> {
            return fillBlocks((CommandListenerWrapper) commandcontext.getSource(), StructureBoundingBox.fromCorners(ArgumentPosition.getLoadedBlockPos(commandcontext, "from"), ArgumentPosition.getLoadedBlockPos(commandcontext, "to")), ArgumentTile.getBlock(commandcontext, "block"), CommandFill.Mode.DESTROY, (Predicate) null);
        }))))));
    }

    private static int fillBlocks(CommandListenerWrapper commandlistenerwrapper, StructureBoundingBox structureboundingbox, ArgumentTileLocation argumenttilelocation, CommandFill.Mode commandfill_mode, @Nullable Predicate<ShapeDetectorBlock> predicate) throws CommandSyntaxException {
        int i = structureboundingbox.getXSpan() * structureboundingbox.getYSpan() * structureboundingbox.getZSpan();

        if (i > 32768) {
            throw CommandFill.ERROR_AREA_TOO_LARGE.create(32768, i);
        } else {
            List<BlockPosition> list = Lists.newArrayList();
            WorldServer worldserver = commandlistenerwrapper.getLevel();
            int j = 0;
            Iterator iterator = BlockPosition.betweenClosed(structureboundingbox.minX(), structureboundingbox.minY(), structureboundingbox.minZ(), structureboundingbox.maxX(), structureboundingbox.maxY(), structureboundingbox.maxZ()).iterator();

            BlockPosition blockposition;

            while (iterator.hasNext()) {
                blockposition = (BlockPosition) iterator.next();
                if (predicate == null || predicate.test(new ShapeDetectorBlock(worldserver, blockposition, true))) {
                    ArgumentTileLocation argumenttilelocation1 = commandfill_mode.filter.filter(structureboundingbox, blockposition, argumenttilelocation, worldserver);

                    if (argumenttilelocation1 != null) {
                        TileEntity tileentity = worldserver.getBlockEntity(blockposition);

                        Clearable.tryClear(tileentity);
                        if (argumenttilelocation1.place(worldserver, blockposition, 2)) {
                            list.add(blockposition.immutable());
                            ++j;
                        }
                    }
                }
            }

            iterator = list.iterator();

            while (iterator.hasNext()) {
                blockposition = (BlockPosition) iterator.next();
                Block block = worldserver.getBlockState(blockposition).getBlock();

                worldserver.blockUpdated(blockposition, block);
            }

            if (j == 0) {
                throw CommandFill.ERROR_FAILED.create();
            } else {
                commandlistenerwrapper.sendSuccess(new ChatMessage("commands.fill.success", new Object[]{j}), true);
                return j;
            }
        }
    }

    private static enum Mode {

        REPLACE((structureboundingbox, blockposition, argumenttilelocation, worldserver) -> {
            return argumenttilelocation;
        }), OUTLINE((structureboundingbox, blockposition, argumenttilelocation, worldserver) -> {
            return blockposition.getX() != structureboundingbox.minX() && blockposition.getX() != structureboundingbox.maxX() && blockposition.getY() != structureboundingbox.minY() && blockposition.getY() != structureboundingbox.maxY() && blockposition.getZ() != structureboundingbox.minZ() && blockposition.getZ() != structureboundingbox.maxZ() ? null : argumenttilelocation;
        }), HOLLOW((structureboundingbox, blockposition, argumenttilelocation, worldserver) -> {
            return blockposition.getX() != structureboundingbox.minX() && blockposition.getX() != structureboundingbox.maxX() && blockposition.getY() != structureboundingbox.minY() && blockposition.getY() != structureboundingbox.maxY() && blockposition.getZ() != structureboundingbox.minZ() && blockposition.getZ() != structureboundingbox.maxZ() ? CommandFill.HOLLOW_CORE : argumenttilelocation;
        }), DESTROY((structureboundingbox, blockposition, argumenttilelocation, worldserver) -> {
            worldserver.destroyBlock(blockposition, true);
            return argumenttilelocation;
        });

        public final CommandSetBlock.Filter filter;

        private Mode(CommandSetBlock.Filter commandsetblock_filter) {
            this.filter = commandsetblock_filter;
        }
    }
}
