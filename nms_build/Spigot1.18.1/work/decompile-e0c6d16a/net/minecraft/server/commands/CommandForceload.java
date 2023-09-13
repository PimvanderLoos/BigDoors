package net.minecraft.server.commands;

import com.google.common.base.Joiner;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.arguments.coordinates.ArgumentPosition;
import net.minecraft.commands.arguments.coordinates.ArgumentVec2I;
import net.minecraft.core.SectionPosition;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.BlockPosition2D;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.World;

public class CommandForceload {

    private static final int MAX_CHUNK_LIMIT = 256;
    private static final Dynamic2CommandExceptionType ERROR_TOO_MANY_CHUNKS = new Dynamic2CommandExceptionType((object, object1) -> {
        return new ChatMessage("commands.forceload.toobig", new Object[]{object, object1});
    });
    private static final Dynamic2CommandExceptionType ERROR_NOT_TICKING = new Dynamic2CommandExceptionType((object, object1) -> {
        return new ChatMessage("commands.forceload.query.failure", new Object[]{object, object1});
    });
    private static final SimpleCommandExceptionType ERROR_ALL_ADDED = new SimpleCommandExceptionType(new ChatMessage("commands.forceload.added.failure"));
    private static final SimpleCommandExceptionType ERROR_NONE_REMOVED = new SimpleCommandExceptionType(new ChatMessage("commands.forceload.removed.failure"));

    public CommandForceload() {}

    public static void register(CommandDispatcher<CommandListenerWrapper> commanddispatcher) {
        commanddispatcher.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.literal("forceload").requires((commandlistenerwrapper) -> {
            return commandlistenerwrapper.hasPermission(2);
        })).then(net.minecraft.commands.CommandDispatcher.literal("add").then(((RequiredArgumentBuilder) net.minecraft.commands.CommandDispatcher.argument("from", ArgumentVec2I.columnPos()).executes((commandcontext) -> {
            return changeForceLoad((CommandListenerWrapper) commandcontext.getSource(), ArgumentVec2I.getColumnPos(commandcontext, "from"), ArgumentVec2I.getColumnPos(commandcontext, "from"), true);
        })).then(net.minecraft.commands.CommandDispatcher.argument("to", ArgumentVec2I.columnPos()).executes((commandcontext) -> {
            return changeForceLoad((CommandListenerWrapper) commandcontext.getSource(), ArgumentVec2I.getColumnPos(commandcontext, "from"), ArgumentVec2I.getColumnPos(commandcontext, "to"), true);
        }))))).then(((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.literal("remove").then(((RequiredArgumentBuilder) net.minecraft.commands.CommandDispatcher.argument("from", ArgumentVec2I.columnPos()).executes((commandcontext) -> {
            return changeForceLoad((CommandListenerWrapper) commandcontext.getSource(), ArgumentVec2I.getColumnPos(commandcontext, "from"), ArgumentVec2I.getColumnPos(commandcontext, "from"), false);
        })).then(net.minecraft.commands.CommandDispatcher.argument("to", ArgumentVec2I.columnPos()).executes((commandcontext) -> {
            return changeForceLoad((CommandListenerWrapper) commandcontext.getSource(), ArgumentVec2I.getColumnPos(commandcontext, "from"), ArgumentVec2I.getColumnPos(commandcontext, "to"), false);
        })))).then(net.minecraft.commands.CommandDispatcher.literal("all").executes((commandcontext) -> {
            return removeAll((CommandListenerWrapper) commandcontext.getSource());
        })))).then(((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.literal("query").executes((commandcontext) -> {
            return listForceLoad((CommandListenerWrapper) commandcontext.getSource());
        })).then(net.minecraft.commands.CommandDispatcher.argument("pos", ArgumentVec2I.columnPos()).executes((commandcontext) -> {
            return queryForceLoad((CommandListenerWrapper) commandcontext.getSource(), ArgumentVec2I.getColumnPos(commandcontext, "pos"));
        }))));
    }

    private static int queryForceLoad(CommandListenerWrapper commandlistenerwrapper, BlockPosition2D blockposition2d) throws CommandSyntaxException {
        ChunkCoordIntPair chunkcoordintpair = new ChunkCoordIntPair(SectionPosition.blockToSectionCoord(blockposition2d.x), SectionPosition.blockToSectionCoord(blockposition2d.z));
        WorldServer worldserver = commandlistenerwrapper.getLevel();
        ResourceKey<World> resourcekey = worldserver.dimension();
        boolean flag = worldserver.getForcedChunks().contains(chunkcoordintpair.toLong());

        if (flag) {
            commandlistenerwrapper.sendSuccess(new ChatMessage("commands.forceload.query.success", new Object[]{chunkcoordintpair, resourcekey.location()}), false);
            return 1;
        } else {
            throw CommandForceload.ERROR_NOT_TICKING.create(chunkcoordintpair, resourcekey.location());
        }
    }

    private static int listForceLoad(CommandListenerWrapper commandlistenerwrapper) {
        WorldServer worldserver = commandlistenerwrapper.getLevel();
        ResourceKey<World> resourcekey = worldserver.dimension();
        LongSet longset = worldserver.getForcedChunks();
        int i = longset.size();

        if (i > 0) {
            String s = Joiner.on(", ").join(longset.stream().sorted().map(ChunkCoordIntPair::new).map(ChunkCoordIntPair::toString).iterator());

            if (i == 1) {
                commandlistenerwrapper.sendSuccess(new ChatMessage("commands.forceload.list.single", new Object[]{resourcekey.location(), s}), false);
            } else {
                commandlistenerwrapper.sendSuccess(new ChatMessage("commands.forceload.list.multiple", new Object[]{i, resourcekey.location(), s}), false);
            }
        } else {
            commandlistenerwrapper.sendFailure(new ChatMessage("commands.forceload.added.none", new Object[]{resourcekey.location()}));
        }

        return i;
    }

    private static int removeAll(CommandListenerWrapper commandlistenerwrapper) {
        WorldServer worldserver = commandlistenerwrapper.getLevel();
        ResourceKey<World> resourcekey = worldserver.dimension();
        LongSet longset = worldserver.getForcedChunks();

        longset.forEach((i) -> {
            worldserver.setChunkForced(ChunkCoordIntPair.getX(i), ChunkCoordIntPair.getZ(i), false);
        });
        commandlistenerwrapper.sendSuccess(new ChatMessage("commands.forceload.removed.all", new Object[]{resourcekey.location()}), true);
        return 0;
    }

    private static int changeForceLoad(CommandListenerWrapper commandlistenerwrapper, BlockPosition2D blockposition2d, BlockPosition2D blockposition2d1, boolean flag) throws CommandSyntaxException {
        int i = Math.min(blockposition2d.x, blockposition2d1.x);
        int j = Math.min(blockposition2d.z, blockposition2d1.z);
        int k = Math.max(blockposition2d.x, blockposition2d1.x);
        int l = Math.max(blockposition2d.z, blockposition2d1.z);

        if (i >= -30000000 && j >= -30000000 && k < 30000000 && l < 30000000) {
            int i1 = SectionPosition.blockToSectionCoord(i);
            int j1 = SectionPosition.blockToSectionCoord(j);
            int k1 = SectionPosition.blockToSectionCoord(k);
            int l1 = SectionPosition.blockToSectionCoord(l);
            long i2 = ((long) (k1 - i1) + 1L) * ((long) (l1 - j1) + 1L);

            if (i2 > 256L) {
                throw CommandForceload.ERROR_TOO_MANY_CHUNKS.create(256, i2);
            } else {
                WorldServer worldserver = commandlistenerwrapper.getLevel();
                ResourceKey<World> resourcekey = worldserver.dimension();
                ChunkCoordIntPair chunkcoordintpair = null;
                int j2 = 0;

                for (int k2 = i1; k2 <= k1; ++k2) {
                    for (int l2 = j1; l2 <= l1; ++l2) {
                        boolean flag1 = worldserver.setChunkForced(k2, l2, flag);

                        if (flag1) {
                            ++j2;
                            if (chunkcoordintpair == null) {
                                chunkcoordintpair = new ChunkCoordIntPair(k2, l2);
                            }
                        }
                    }
                }

                if (j2 == 0) {
                    throw (flag ? CommandForceload.ERROR_ALL_ADDED : CommandForceload.ERROR_NONE_REMOVED).create();
                } else {
                    if (j2 == 1) {
                        commandlistenerwrapper.sendSuccess(new ChatMessage("commands.forceload." + (flag ? "added" : "removed") + ".single", new Object[]{chunkcoordintpair, resourcekey.location()}), true);
                    } else {
                        ChunkCoordIntPair chunkcoordintpair1 = new ChunkCoordIntPair(i1, j1);
                        ChunkCoordIntPair chunkcoordintpair2 = new ChunkCoordIntPair(k1, l1);

                        commandlistenerwrapper.sendSuccess(new ChatMessage("commands.forceload." + (flag ? "added" : "removed") + ".multiple", new Object[]{j2, resourcekey.location(), chunkcoordintpair1, chunkcoordintpair2}), true);
                    }

                    return j2;
                }
            }
        } else {
            throw ArgumentPosition.ERROR_OUT_OF_WORLD.create();
        }
    }
}
