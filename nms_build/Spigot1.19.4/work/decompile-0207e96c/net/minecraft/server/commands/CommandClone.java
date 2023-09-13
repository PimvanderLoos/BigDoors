package net.minecraft.server.commands;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.arguments.ArgumentDimension;
import net.minecraft.commands.arguments.blocks.ArgumentBlockPredicate;
import net.minecraft.commands.arguments.coordinates.ArgumentPosition;
import net.minecraft.core.BlockPosition;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.Clearable;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.pattern.ShapeDetectorBlock;
import net.minecraft.world.level.levelgen.structure.StructureBoundingBox;

public class CommandClone {

    private static final SimpleCommandExceptionType ERROR_OVERLAP = new SimpleCommandExceptionType(IChatBaseComponent.translatable("commands.clone.overlap"));
    private static final Dynamic2CommandExceptionType ERROR_AREA_TOO_LARGE = new Dynamic2CommandExceptionType((object, object1) -> {
        return IChatBaseComponent.translatable("commands.clone.toobig", object, object1);
    });
    private static final SimpleCommandExceptionType ERROR_FAILED = new SimpleCommandExceptionType(IChatBaseComponent.translatable("commands.clone.failed"));
    public static final Predicate<ShapeDetectorBlock> FILTER_AIR = (shapedetectorblock) -> {
        return !shapedetectorblock.getState().isAir();
    };

    public CommandClone() {}

    public static void register(CommandDispatcher<CommandListenerWrapper> commanddispatcher, CommandBuildContext commandbuildcontext) {
        commanddispatcher.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.literal("clone").requires((commandlistenerwrapper) -> {
            return commandlistenerwrapper.hasPermission(2);
        })).then(beginEndDestinationAndModeSuffix(commandbuildcontext, (commandcontext) -> {
            return ((CommandListenerWrapper) commandcontext.getSource()).getLevel();
        }))).then(net.minecraft.commands.CommandDispatcher.literal("from").then(net.minecraft.commands.CommandDispatcher.argument("sourceDimension", ArgumentDimension.dimension()).then(beginEndDestinationAndModeSuffix(commandbuildcontext, (commandcontext) -> {
            return ArgumentDimension.getDimension(commandcontext, "sourceDimension");
        })))));
    }

    private static ArgumentBuilder<CommandListenerWrapper, ?> beginEndDestinationAndModeSuffix(CommandBuildContext commandbuildcontext, CommandClone.b<CommandContext<CommandListenerWrapper>, WorldServer> commandclone_b) {
        return net.minecraft.commands.CommandDispatcher.argument("begin", ArgumentPosition.blockPos()).then(((RequiredArgumentBuilder) net.minecraft.commands.CommandDispatcher.argument("end", ArgumentPosition.blockPos()).then(destinationAndModeSuffix(commandbuildcontext, commandclone_b, (commandcontext) -> {
            return ((CommandListenerWrapper) commandcontext.getSource()).getLevel();
        }))).then(net.minecraft.commands.CommandDispatcher.literal("to").then(net.minecraft.commands.CommandDispatcher.argument("targetDimension", ArgumentDimension.dimension()).then(destinationAndModeSuffix(commandbuildcontext, commandclone_b, (commandcontext) -> {
            return ArgumentDimension.getDimension(commandcontext, "targetDimension");
        })))));
    }

    private static CommandClone.c getLoadedDimensionAndPosition(CommandContext<CommandListenerWrapper> commandcontext, WorldServer worldserver, String s) throws CommandSyntaxException {
        BlockPosition blockposition = ArgumentPosition.getLoadedBlockPos(commandcontext, worldserver, s);

        return new CommandClone.c(worldserver, blockposition);
    }

    private static ArgumentBuilder<CommandListenerWrapper, ?> destinationAndModeSuffix(CommandBuildContext commandbuildcontext, CommandClone.b<CommandContext<CommandListenerWrapper>, WorldServer> commandclone_b, CommandClone.b<CommandContext<CommandListenerWrapper>, WorldServer> commandclone_b1) {
        CommandClone.b<CommandContext<CommandListenerWrapper>, CommandClone.c> commandclone_b2 = (commandcontext) -> {
            return getLoadedDimensionAndPosition(commandcontext, (WorldServer) commandclone_b.apply(commandcontext), "begin");
        };
        CommandClone.b<CommandContext<CommandListenerWrapper>, CommandClone.c> commandclone_b3 = (commandcontext) -> {
            return getLoadedDimensionAndPosition(commandcontext, (WorldServer) commandclone_b.apply(commandcontext), "end");
        };
        CommandClone.b<CommandContext<CommandListenerWrapper>, CommandClone.c> commandclone_b4 = (commandcontext) -> {
            return getLoadedDimensionAndPosition(commandcontext, (WorldServer) commandclone_b1.apply(commandcontext), "destination");
        };

        return ((RequiredArgumentBuilder) ((RequiredArgumentBuilder) ((RequiredArgumentBuilder) net.minecraft.commands.CommandDispatcher.argument("destination", ArgumentPosition.blockPos()).executes((commandcontext) -> {
            return clone((CommandListenerWrapper) commandcontext.getSource(), (CommandClone.c) commandclone_b2.apply(commandcontext), (CommandClone.c) commandclone_b3.apply(commandcontext), (CommandClone.c) commandclone_b4.apply(commandcontext), (shapedetectorblock) -> {
                return true;
            }, CommandClone.Mode.NORMAL);
        })).then(wrapWithCloneMode(commandclone_b2, commandclone_b3, commandclone_b4, (commandcontext) -> {
            return (shapedetectorblock) -> {
                return true;
            };
        }, net.minecraft.commands.CommandDispatcher.literal("replace").executes((commandcontext) -> {
            return clone((CommandListenerWrapper) commandcontext.getSource(), (CommandClone.c) commandclone_b2.apply(commandcontext), (CommandClone.c) commandclone_b3.apply(commandcontext), (CommandClone.c) commandclone_b4.apply(commandcontext), (shapedetectorblock) -> {
                return true;
            }, CommandClone.Mode.NORMAL);
        })))).then(wrapWithCloneMode(commandclone_b2, commandclone_b3, commandclone_b4, (commandcontext) -> {
            return CommandClone.FILTER_AIR;
        }, net.minecraft.commands.CommandDispatcher.literal("masked").executes((commandcontext) -> {
            return clone((CommandListenerWrapper) commandcontext.getSource(), (CommandClone.c) commandclone_b2.apply(commandcontext), (CommandClone.c) commandclone_b3.apply(commandcontext), (CommandClone.c) commandclone_b4.apply(commandcontext), CommandClone.FILTER_AIR, CommandClone.Mode.NORMAL);
        })))).then(net.minecraft.commands.CommandDispatcher.literal("filtered").then(wrapWithCloneMode(commandclone_b2, commandclone_b3, commandclone_b4, (commandcontext) -> {
            return ArgumentBlockPredicate.getBlockPredicate(commandcontext, "filter");
        }, net.minecraft.commands.CommandDispatcher.argument("filter", ArgumentBlockPredicate.blockPredicate(commandbuildcontext)).executes((commandcontext) -> {
            return clone((CommandListenerWrapper) commandcontext.getSource(), (CommandClone.c) commandclone_b2.apply(commandcontext), (CommandClone.c) commandclone_b3.apply(commandcontext), (CommandClone.c) commandclone_b4.apply(commandcontext), ArgumentBlockPredicate.getBlockPredicate(commandcontext, "filter"), CommandClone.Mode.NORMAL);
        }))));
    }

    private static ArgumentBuilder<CommandListenerWrapper, ?> wrapWithCloneMode(CommandClone.b<CommandContext<CommandListenerWrapper>, CommandClone.c> commandclone_b, CommandClone.b<CommandContext<CommandListenerWrapper>, CommandClone.c> commandclone_b1, CommandClone.b<CommandContext<CommandListenerWrapper>, CommandClone.c> commandclone_b2, CommandClone.b<CommandContext<CommandListenerWrapper>, Predicate<ShapeDetectorBlock>> commandclone_b3, ArgumentBuilder<CommandListenerWrapper, ?> argumentbuilder) {
        return argumentbuilder.then(net.minecraft.commands.CommandDispatcher.literal("force").executes((commandcontext) -> {
            return clone((CommandListenerWrapper) commandcontext.getSource(), (CommandClone.c) commandclone_b.apply(commandcontext), (CommandClone.c) commandclone_b1.apply(commandcontext), (CommandClone.c) commandclone_b2.apply(commandcontext), (Predicate) commandclone_b3.apply(commandcontext), CommandClone.Mode.FORCE);
        })).then(net.minecraft.commands.CommandDispatcher.literal("move").executes((commandcontext) -> {
            return clone((CommandListenerWrapper) commandcontext.getSource(), (CommandClone.c) commandclone_b.apply(commandcontext), (CommandClone.c) commandclone_b1.apply(commandcontext), (CommandClone.c) commandclone_b2.apply(commandcontext), (Predicate) commandclone_b3.apply(commandcontext), CommandClone.Mode.MOVE);
        })).then(net.minecraft.commands.CommandDispatcher.literal("normal").executes((commandcontext) -> {
            return clone((CommandListenerWrapper) commandcontext.getSource(), (CommandClone.c) commandclone_b.apply(commandcontext), (CommandClone.c) commandclone_b1.apply(commandcontext), (CommandClone.c) commandclone_b2.apply(commandcontext), (Predicate) commandclone_b3.apply(commandcontext), CommandClone.Mode.NORMAL);
        }));
    }

    private static int clone(CommandListenerWrapper commandlistenerwrapper, CommandClone.c commandclone_c, CommandClone.c commandclone_c1, CommandClone.c commandclone_c2, Predicate<ShapeDetectorBlock> predicate, CommandClone.Mode commandclone_mode) throws CommandSyntaxException {
        BlockPosition blockposition = commandclone_c.position();
        BlockPosition blockposition1 = commandclone_c1.position();
        StructureBoundingBox structureboundingbox = StructureBoundingBox.fromCorners(blockposition, blockposition1);
        BlockPosition blockposition2 = commandclone_c2.position();
        BlockPosition blockposition3 = blockposition2.offset(structureboundingbox.getLength());
        StructureBoundingBox structureboundingbox1 = StructureBoundingBox.fromCorners(blockposition2, blockposition3);
        WorldServer worldserver = commandclone_c.dimension();
        WorldServer worldserver1 = commandclone_c2.dimension();

        if (!commandclone_mode.canOverlap() && worldserver == worldserver1 && structureboundingbox1.intersects(structureboundingbox)) {
            throw CommandClone.ERROR_OVERLAP.create();
        } else {
            int i = structureboundingbox.getXSpan() * structureboundingbox.getYSpan() * structureboundingbox.getZSpan();
            int j = commandlistenerwrapper.getLevel().getGameRules().getInt(GameRules.RULE_COMMAND_MODIFICATION_BLOCK_LIMIT);

            if (i > j) {
                throw CommandClone.ERROR_AREA_TOO_LARGE.create(j, i);
            } else if (worldserver.hasChunksAt(blockposition, blockposition1) && worldserver1.hasChunksAt(blockposition2, blockposition3)) {
                List<CommandClone.CommandCloneStoredTileEntity> list = Lists.newArrayList();
                List<CommandClone.CommandCloneStoredTileEntity> list1 = Lists.newArrayList();
                List<CommandClone.CommandCloneStoredTileEntity> list2 = Lists.newArrayList();
                Deque<BlockPosition> deque = Lists.newLinkedList();
                BlockPosition blockposition4 = new BlockPosition(structureboundingbox1.minX() - structureboundingbox.minX(), structureboundingbox1.minY() - structureboundingbox.minY(), structureboundingbox1.minZ() - structureboundingbox.minZ());

                int k;

                for (int l = structureboundingbox.minZ(); l <= structureboundingbox.maxZ(); ++l) {
                    for (int i1 = structureboundingbox.minY(); i1 <= structureboundingbox.maxY(); ++i1) {
                        for (k = structureboundingbox.minX(); k <= structureboundingbox.maxX(); ++k) {
                            BlockPosition blockposition5 = new BlockPosition(k, i1, l);
                            BlockPosition blockposition6 = blockposition5.offset(blockposition4);
                            ShapeDetectorBlock shapedetectorblock = new ShapeDetectorBlock(worldserver, blockposition5, false);
                            IBlockData iblockdata = shapedetectorblock.getState();

                            if (predicate.test(shapedetectorblock)) {
                                TileEntity tileentity = worldserver.getBlockEntity(blockposition5);

                                if (tileentity != null) {
                                    NBTTagCompound nbttagcompound = tileentity.saveWithoutMetadata();

                                    list1.add(new CommandClone.CommandCloneStoredTileEntity(blockposition6, iblockdata, nbttagcompound));
                                    deque.addLast(blockposition5);
                                } else if (!iblockdata.isSolidRender(worldserver, blockposition5) && !iblockdata.isCollisionShapeFullBlock(worldserver, blockposition5)) {
                                    list2.add(new CommandClone.CommandCloneStoredTileEntity(blockposition6, iblockdata, (NBTTagCompound) null));
                                    deque.addFirst(blockposition5);
                                } else {
                                    list.add(new CommandClone.CommandCloneStoredTileEntity(blockposition6, iblockdata, (NBTTagCompound) null));
                                    deque.addLast(blockposition5);
                                }
                            }
                        }
                    }
                }

                if (commandclone_mode == CommandClone.Mode.MOVE) {
                    Iterator iterator = deque.iterator();

                    BlockPosition blockposition7;

                    while (iterator.hasNext()) {
                        blockposition7 = (BlockPosition) iterator.next();
                        TileEntity tileentity1 = worldserver.getBlockEntity(blockposition7);

                        Clearable.tryClear(tileentity1);
                        worldserver.setBlock(blockposition7, Blocks.BARRIER.defaultBlockState(), 2);
                    }

                    iterator = deque.iterator();

                    while (iterator.hasNext()) {
                        blockposition7 = (BlockPosition) iterator.next();
                        worldserver.setBlock(blockposition7, Blocks.AIR.defaultBlockState(), 3);
                    }
                }

                List<CommandClone.CommandCloneStoredTileEntity> list3 = Lists.newArrayList();

                list3.addAll(list);
                list3.addAll(list1);
                list3.addAll(list2);
                List<CommandClone.CommandCloneStoredTileEntity> list4 = Lists.reverse(list3);
                Iterator iterator1 = list4.iterator();

                while (iterator1.hasNext()) {
                    CommandClone.CommandCloneStoredTileEntity commandclone_commandclonestoredtileentity = (CommandClone.CommandCloneStoredTileEntity) iterator1.next();
                    TileEntity tileentity2 = worldserver1.getBlockEntity(commandclone_commandclonestoredtileentity.pos);

                    Clearable.tryClear(tileentity2);
                    worldserver1.setBlock(commandclone_commandclonestoredtileentity.pos, Blocks.BARRIER.defaultBlockState(), 2);
                }

                k = 0;
                Iterator iterator2 = list3.iterator();

                CommandClone.CommandCloneStoredTileEntity commandclone_commandclonestoredtileentity1;

                while (iterator2.hasNext()) {
                    commandclone_commandclonestoredtileentity1 = (CommandClone.CommandCloneStoredTileEntity) iterator2.next();
                    if (worldserver1.setBlock(commandclone_commandclonestoredtileentity1.pos, commandclone_commandclonestoredtileentity1.state, 2)) {
                        ++k;
                    }
                }

                for (iterator2 = list1.iterator(); iterator2.hasNext(); worldserver1.setBlock(commandclone_commandclonestoredtileentity1.pos, commandclone_commandclonestoredtileentity1.state, 2)) {
                    commandclone_commandclonestoredtileentity1 = (CommandClone.CommandCloneStoredTileEntity) iterator2.next();
                    TileEntity tileentity3 = worldserver1.getBlockEntity(commandclone_commandclonestoredtileentity1.pos);

                    if (commandclone_commandclonestoredtileentity1.tag != null && tileentity3 != null) {
                        tileentity3.load(commandclone_commandclonestoredtileentity1.tag);
                        tileentity3.setChanged();
                    }
                }

                iterator2 = list4.iterator();

                while (iterator2.hasNext()) {
                    commandclone_commandclonestoredtileentity1 = (CommandClone.CommandCloneStoredTileEntity) iterator2.next();
                    worldserver1.blockUpdated(commandclone_commandclonestoredtileentity1.pos, commandclone_commandclonestoredtileentity1.state.getBlock());
                }

                worldserver1.getBlockTicks().copyAreaFrom(worldserver.getBlockTicks(), structureboundingbox, blockposition4);
                if (k == 0) {
                    throw CommandClone.ERROR_FAILED.create();
                } else {
                    commandlistenerwrapper.sendSuccess(IChatBaseComponent.translatable("commands.clone.success", k), true);
                    return k;
                }
            } else {
                throw ArgumentPosition.ERROR_NOT_LOADED.create();
            }
        }
    }

    @FunctionalInterface
    interface b<T, R> {

        R apply(T t0) throws CommandSyntaxException;
    }

    private static record c(WorldServer dimension, BlockPosition position) {

    }

    private static enum Mode {

        FORCE(true), MOVE(true), NORMAL(false);

        private final boolean canOverlap;

        private Mode(boolean flag) {
            this.canOverlap = flag;
        }

        public boolean canOverlap() {
            return this.canOverlap;
        }
    }

    private static class CommandCloneStoredTileEntity {

        public final BlockPosition pos;
        public final IBlockData state;
        @Nullable
        public final NBTTagCompound tag;

        public CommandCloneStoredTileEntity(BlockPosition blockposition, IBlockData iblockdata, @Nullable NBTTagCompound nbttagcompound) {
            this.pos = blockposition;
            this.state = iblockdata;
            this.tag = nbttagcompound;
        }
    }
}
