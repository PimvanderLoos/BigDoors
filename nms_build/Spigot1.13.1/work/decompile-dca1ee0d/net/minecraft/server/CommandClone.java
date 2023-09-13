package net.minecraft.server;

import com.google.common.collect.Lists;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType.Function;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;

public class CommandClone {

    private static final SimpleCommandExceptionType b = new SimpleCommandExceptionType(new ChatMessage("commands.clone.overlap", new Object[0]));
    private static final Dynamic2CommandExceptionType c = new Dynamic2CommandExceptionType((object, object1) -> {
        return new ChatMessage("commands.clone.toobig", new Object[] { object, object1});
    });
    private static final SimpleCommandExceptionType d = new SimpleCommandExceptionType(new ChatMessage("commands.clone.failed", new Object[0]));
    public static final Predicate<ShapeDetectorBlock> a = (shapedetectorblock) -> {
        return !shapedetectorblock.a().isAir();
    };

    public static void a(com.mojang.brigadier.CommandDispatcher<CommandListenerWrapper> com_mojang_brigadier_commanddispatcher) {
        com_mojang_brigadier_commanddispatcher.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) CommandDispatcher.a("clone").requires((commandlistenerwrapper) -> {
            return commandlistenerwrapper.hasPermission(2);
        })).then(CommandDispatcher.a("begin", (ArgumentType) ArgumentPosition.a()).then(CommandDispatcher.a("end", (ArgumentType) ArgumentPosition.a()).then(((RequiredArgumentBuilder) ((RequiredArgumentBuilder) ((RequiredArgumentBuilder) CommandDispatcher.a("destination", (ArgumentType) ArgumentPosition.a()).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), ArgumentPosition.a(commandcontext, "begin"), ArgumentPosition.a(commandcontext, "end"), ArgumentPosition.a(commandcontext, "destination"), (shapedetectorblock) -> {
                return true;
            }, CommandClone.Mode.NORMAL);
        })).then(((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) CommandDispatcher.a("replace").executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), ArgumentPosition.a(commandcontext, "begin"), ArgumentPosition.a(commandcontext, "end"), ArgumentPosition.a(commandcontext, "destination"), (shapedetectorblock) -> {
                return true;
            }, CommandClone.Mode.NORMAL);
        })).then(CommandDispatcher.a("force").executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), ArgumentPosition.a(commandcontext, "begin"), ArgumentPosition.a(commandcontext, "end"), ArgumentPosition.a(commandcontext, "destination"), (shapedetectorblock) -> {
                return true;
            }, CommandClone.Mode.FORCE);
        }))).then(CommandDispatcher.a("move").executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), ArgumentPosition.a(commandcontext, "begin"), ArgumentPosition.a(commandcontext, "end"), ArgumentPosition.a(commandcontext, "destination"), (shapedetectorblock) -> {
                return true;
            }, CommandClone.Mode.MOVE);
        }))).then(CommandDispatcher.a("normal").executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), ArgumentPosition.a(commandcontext, "begin"), ArgumentPosition.a(commandcontext, "end"), ArgumentPosition.a(commandcontext, "destination"), (shapedetectorblock) -> {
                return true;
            }, CommandClone.Mode.NORMAL);
        })))).then(((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) CommandDispatcher.a("masked").executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), ArgumentPosition.a(commandcontext, "begin"), ArgumentPosition.a(commandcontext, "end"), ArgumentPosition.a(commandcontext, "destination"), CommandClone.a, CommandClone.Mode.NORMAL);
        })).then(CommandDispatcher.a("force").executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), ArgumentPosition.a(commandcontext, "begin"), ArgumentPosition.a(commandcontext, "end"), ArgumentPosition.a(commandcontext, "destination"), CommandClone.a, CommandClone.Mode.FORCE);
        }))).then(CommandDispatcher.a("move").executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), ArgumentPosition.a(commandcontext, "begin"), ArgumentPosition.a(commandcontext, "end"), ArgumentPosition.a(commandcontext, "destination"), CommandClone.a, CommandClone.Mode.MOVE);
        }))).then(CommandDispatcher.a("normal").executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), ArgumentPosition.a(commandcontext, "begin"), ArgumentPosition.a(commandcontext, "end"), ArgumentPosition.a(commandcontext, "destination"), CommandClone.a, CommandClone.Mode.NORMAL);
        })))).then(CommandDispatcher.a("filtered").then(((RequiredArgumentBuilder) ((RequiredArgumentBuilder) ((RequiredArgumentBuilder) CommandDispatcher.a("filter", (ArgumentType) ArgumentBlockPredicate.a()).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), ArgumentPosition.a(commandcontext, "begin"), ArgumentPosition.a(commandcontext, "end"), ArgumentPosition.a(commandcontext, "destination"), ArgumentBlockPredicate.a(commandcontext, "filter"), CommandClone.Mode.NORMAL);
        })).then(CommandDispatcher.a("force").executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), ArgumentPosition.a(commandcontext, "begin"), ArgumentPosition.a(commandcontext, "end"), ArgumentPosition.a(commandcontext, "destination"), ArgumentBlockPredicate.a(commandcontext, "filter"), CommandClone.Mode.FORCE);
        }))).then(CommandDispatcher.a("move").executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), ArgumentPosition.a(commandcontext, "begin"), ArgumentPosition.a(commandcontext, "end"), ArgumentPosition.a(commandcontext, "destination"), ArgumentBlockPredicate.a(commandcontext, "filter"), CommandClone.Mode.MOVE);
        }))).then(CommandDispatcher.a("normal").executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), ArgumentPosition.a(commandcontext, "begin"), ArgumentPosition.a(commandcontext, "end"), ArgumentPosition.a(commandcontext, "destination"), ArgumentBlockPredicate.a(commandcontext, "filter"), CommandClone.Mode.NORMAL);
        }))))))));
    }

    private static int a(CommandListenerWrapper commandlistenerwrapper, BlockPosition blockposition, BlockPosition blockposition1, BlockPosition blockposition2, Predicate<ShapeDetectorBlock> predicate, CommandClone.Mode commandclone_mode) throws CommandSyntaxException {
        StructureBoundingBox structureboundingbox = new StructureBoundingBox(blockposition, blockposition1);
        StructureBoundingBox structureboundingbox1 = new StructureBoundingBox(blockposition2, blockposition2.a(structureboundingbox.b()));

        if (!commandclone_mode.a() && structureboundingbox1.a(structureboundingbox)) {
            throw CommandClone.b.create();
        } else {
            int i = structureboundingbox.c() * structureboundingbox.d() * structureboundingbox.e();

            if (i > '\u8000') {
                throw CommandClone.c.create(Integer.valueOf('\u8000'), Integer.valueOf(i));
            } else {
                WorldServer worldserver = commandlistenerwrapper.getWorld();

                if (worldserver.a(structureboundingbox) && worldserver.a(structureboundingbox1)) {
                    ArrayList arraylist = Lists.newArrayList();
                    ArrayList arraylist1 = Lists.newArrayList();
                    ArrayList arraylist2 = Lists.newArrayList();
                    LinkedList linkedlist = Lists.newLinkedList();
                    BlockPosition blockposition3 = new BlockPosition(structureboundingbox1.a - structureboundingbox.a, structureboundingbox1.b - structureboundingbox.b, structureboundingbox1.c - structureboundingbox.c);

                    int j;

                    for (int k = structureboundingbox.c; k <= structureboundingbox.f; ++k) {
                        for (int l = structureboundingbox.b; l <= structureboundingbox.e; ++l) {
                            for (j = structureboundingbox.a; j <= structureboundingbox.d; ++j) {
                                BlockPosition blockposition4 = new BlockPosition(j, l, k);
                                BlockPosition blockposition5 = blockposition4.a((BaseBlockPosition) blockposition3);
                                ShapeDetectorBlock shapedetectorblock = new ShapeDetectorBlock(worldserver, blockposition4, false);
                                IBlockData iblockdata = shapedetectorblock.a();

                                if (predicate.test(shapedetectorblock)) {
                                    TileEntity tileentity = worldserver.getTileEntity(blockposition4);

                                    if (tileentity != null) {
                                        NBTTagCompound nbttagcompound = tileentity.save(new NBTTagCompound());

                                        arraylist1.add(new CommandClone.CommandCloneStoredTileEntity(blockposition5, iblockdata, nbttagcompound));
                                        linkedlist.addLast(blockposition4);
                                    } else if (!iblockdata.f(worldserver, blockposition4) && !iblockdata.g()) {
                                        arraylist2.add(new CommandClone.CommandCloneStoredTileEntity(blockposition5, iblockdata, (NBTTagCompound) null));
                                        linkedlist.addFirst(blockposition4);
                                    } else {
                                        arraylist.add(new CommandClone.CommandCloneStoredTileEntity(blockposition5, iblockdata, (NBTTagCompound) null));
                                        linkedlist.addLast(blockposition4);
                                    }
                                }
                            }
                        }
                    }

                    if (commandclone_mode == CommandClone.Mode.MOVE) {
                        Iterator iterator;
                        BlockPosition blockposition6;

                        for (iterator = linkedlist.iterator(); iterator.hasNext(); worldserver.setTypeAndData(blockposition6, Blocks.BARRIER.getBlockData(), 2)) {
                            blockposition6 = (BlockPosition) iterator.next();
                            TileEntity tileentity1 = worldserver.getTileEntity(blockposition6);

                            if (tileentity1 instanceof IInventory) {
                                ((IInventory) tileentity1).clear();
                            }
                        }

                        iterator = linkedlist.iterator();

                        while (iterator.hasNext()) {
                            blockposition6 = (BlockPosition) iterator.next();
                            worldserver.setTypeAndData(blockposition6, Blocks.AIR.getBlockData(), 3);
                        }
                    }

                    ArrayList arraylist3 = Lists.newArrayList();

                    arraylist3.addAll(arraylist);
                    arraylist3.addAll(arraylist1);
                    arraylist3.addAll(arraylist2);
                    List list = Lists.reverse(arraylist3);

                    CommandClone.CommandCloneStoredTileEntity commandclone_commandclonestoredtileentity;

                    for (Iterator iterator1 = list.iterator(); iterator1.hasNext(); worldserver.setTypeAndData(commandclone_commandclonestoredtileentity.a, Blocks.BARRIER.getBlockData(), 2)) {
                        commandclone_commandclonestoredtileentity = (CommandClone.CommandCloneStoredTileEntity) iterator1.next();
                        TileEntity tileentity2 = worldserver.getTileEntity(commandclone_commandclonestoredtileentity.a);

                        if (tileentity2 instanceof IInventory) {
                            ((IInventory) tileentity2).clear();
                        }
                    }

                    j = 0;
                    Iterator iterator2 = arraylist3.iterator();

                    CommandClone.CommandCloneStoredTileEntity commandclone_commandclonestoredtileentity1;

                    while (iterator2.hasNext()) {
                        commandclone_commandclonestoredtileentity1 = (CommandClone.CommandCloneStoredTileEntity) iterator2.next();
                        if (worldserver.setTypeAndData(commandclone_commandclonestoredtileentity1.a, commandclone_commandclonestoredtileentity1.b, 2)) {
                            ++j;
                        }
                    }

                    for (iterator2 = arraylist1.iterator(); iterator2.hasNext(); worldserver.setTypeAndData(commandclone_commandclonestoredtileentity1.a, commandclone_commandclonestoredtileentity1.b, 2)) {
                        commandclone_commandclonestoredtileentity1 = (CommandClone.CommandCloneStoredTileEntity) iterator2.next();
                        TileEntity tileentity3 = worldserver.getTileEntity(commandclone_commandclonestoredtileentity1.a);

                        if (commandclone_commandclonestoredtileentity1.c != null && tileentity3 != null) {
                            commandclone_commandclonestoredtileentity1.c.setInt("x", commandclone_commandclonestoredtileentity1.a.getX());
                            commandclone_commandclonestoredtileentity1.c.setInt("y", commandclone_commandclonestoredtileentity1.a.getY());
                            commandclone_commandclonestoredtileentity1.c.setInt("z", commandclone_commandclonestoredtileentity1.a.getZ());
                            tileentity3.load(commandclone_commandclonestoredtileentity1.c);
                            tileentity3.update();
                        }
                    }

                    iterator2 = list.iterator();

                    while (iterator2.hasNext()) {
                        commandclone_commandclonestoredtileentity1 = (CommandClone.CommandCloneStoredTileEntity) iterator2.next();
                        worldserver.update(commandclone_commandclonestoredtileentity1.a, commandclone_commandclonestoredtileentity1.b.getBlock());
                    }

                    worldserver.x().a(structureboundingbox, blockposition3);
                    if (j == 0) {
                        throw CommandClone.d.create();
                    } else {
                        commandlistenerwrapper.sendMessage(new ChatMessage("commands.clone.success", new Object[] { Integer.valueOf(j)}), true);
                        return j;
                    }
                } else {
                    throw ArgumentPosition.a.create();
                }
            }
        }
    }

    static class CommandCloneStoredTileEntity {

        public final BlockPosition a;
        public final IBlockData b;
        @Nullable
        public final NBTTagCompound c;

        public CommandCloneStoredTileEntity(BlockPosition blockposition, IBlockData iblockdata, @Nullable NBTTagCompound nbttagcompound) {
            this.a = blockposition;
            this.b = iblockdata;
            this.c = nbttagcompound;
        }
    }

    static enum Mode {

        FORCE(true), MOVE(true), NORMAL(false);

        private final boolean d;

        private Mode(boolean flag) {
            this.d = flag;
        }

        public boolean a() {
            return this.d;
        }
    }
}
