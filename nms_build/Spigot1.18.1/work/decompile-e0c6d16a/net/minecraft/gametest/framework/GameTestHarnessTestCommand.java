package net.minecraft.gametest.framework;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.EnumChatFormat;
import net.minecraft.SystemUtils;
import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.commands.arguments.blocks.ArgumentTileLocation;
import net.minecraft.core.BaseBlockPosition;
import net.minecraft.core.BlockPosition;
import net.minecraft.data.structures.DebugReportNBT;
import net.minecraft.nbt.GameProfileSerializer;
import net.minecraft.nbt.NBTCompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.chat.ChatClickable;
import net.minecraft.network.chat.ChatComponentText;
import net.minecraft.network.chat.ChatHoverable;
import net.minecraft.network.chat.ChatModifier;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.chat.IChatMutableComponent;
import net.minecraft.network.protocol.game.PacketDebug;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.level.WorldServer;
import net.minecraft.util.MathHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EnumBlockRotation;
import net.minecraft.world.level.block.entity.TileEntityStructure;
import net.minecraft.world.level.levelgen.HeightMap;
import net.minecraft.world.phys.AxisAlignedBB;
import net.minecraft.world.phys.MovingObjectPositionBlock;
import org.apache.commons.io.IOUtils;

public class GameTestHarnessTestCommand {

    private static final int DEFAULT_CLEAR_RADIUS = 200;
    private static final int MAX_CLEAR_RADIUS = 1024;
    private static final int STRUCTURE_BLOCK_NEARBY_SEARCH_RADIUS = 15;
    private static final int STRUCTURE_BLOCK_FULL_SEARCH_RADIUS = 200;
    private static final int TEST_POS_Z_OFFSET_FROM_PLAYER = 3;
    private static final int SHOW_POS_DURATION_MS = 10000;
    private static final int DEFAULT_X_SIZE = 5;
    private static final int DEFAULT_Y_SIZE = 5;
    private static final int DEFAULT_Z_SIZE = 5;

    public GameTestHarnessTestCommand() {}

    public static void register(CommandDispatcher<CommandListenerWrapper> commanddispatcher) {
        commanddispatcher.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.literal("test").then(net.minecraft.commands.CommandDispatcher.literal("runthis").executes((commandcontext) -> {
            return runNearbyTest((CommandListenerWrapper) commandcontext.getSource());
        }))).then(net.minecraft.commands.CommandDispatcher.literal("runthese").executes((commandcontext) -> {
            return runAllNearbyTests((CommandListenerWrapper) commandcontext.getSource());
        }))).then(((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.literal("runfailed").executes((commandcontext) -> {
            return runLastFailedTests((CommandListenerWrapper) commandcontext.getSource(), false, 0, 8);
        })).then(((RequiredArgumentBuilder) net.minecraft.commands.CommandDispatcher.argument("onlyRequiredTests", BoolArgumentType.bool()).executes((commandcontext) -> {
            return runLastFailedTests((CommandListenerWrapper) commandcontext.getSource(), BoolArgumentType.getBool(commandcontext, "onlyRequiredTests"), 0, 8);
        })).then(((RequiredArgumentBuilder) net.minecraft.commands.CommandDispatcher.argument("rotationSteps", IntegerArgumentType.integer()).executes((commandcontext) -> {
            return runLastFailedTests((CommandListenerWrapper) commandcontext.getSource(), BoolArgumentType.getBool(commandcontext, "onlyRequiredTests"), IntegerArgumentType.getInteger(commandcontext, "rotationSteps"), 8);
        })).then(net.minecraft.commands.CommandDispatcher.argument("testsPerRow", IntegerArgumentType.integer()).executes((commandcontext) -> {
            return runLastFailedTests((CommandListenerWrapper) commandcontext.getSource(), BoolArgumentType.getBool(commandcontext, "onlyRequiredTests"), IntegerArgumentType.getInteger(commandcontext, "rotationSteps"), IntegerArgumentType.getInteger(commandcontext, "testsPerRow"));
        })))))).then(net.minecraft.commands.CommandDispatcher.literal("run").then(((RequiredArgumentBuilder) net.minecraft.commands.CommandDispatcher.argument("testName", GameTestHarnessTestFunctionArgument.testFunctionArgument()).executes((commandcontext) -> {
            return runTest((CommandListenerWrapper) commandcontext.getSource(), GameTestHarnessTestFunctionArgument.getTestFunction(commandcontext, "testName"), 0);
        })).then(net.minecraft.commands.CommandDispatcher.argument("rotationSteps", IntegerArgumentType.integer()).executes((commandcontext) -> {
            return runTest((CommandListenerWrapper) commandcontext.getSource(), GameTestHarnessTestFunctionArgument.getTestFunction(commandcontext, "testName"), IntegerArgumentType.getInteger(commandcontext, "rotationSteps"));
        }))))).then(((LiteralArgumentBuilder) ((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.literal("runall").executes((commandcontext) -> {
            return runAllTests((CommandListenerWrapper) commandcontext.getSource(), 0, 8);
        })).then(((RequiredArgumentBuilder) net.minecraft.commands.CommandDispatcher.argument("testClassName", GameTestHarnessTestClassArgument.testClassName()).executes((commandcontext) -> {
            return runAllTestsInClass((CommandListenerWrapper) commandcontext.getSource(), GameTestHarnessTestClassArgument.getTestClassName(commandcontext, "testClassName"), 0, 8);
        })).then(((RequiredArgumentBuilder) net.minecraft.commands.CommandDispatcher.argument("rotationSteps", IntegerArgumentType.integer()).executes((commandcontext) -> {
            return runAllTestsInClass((CommandListenerWrapper) commandcontext.getSource(), GameTestHarnessTestClassArgument.getTestClassName(commandcontext, "testClassName"), IntegerArgumentType.getInteger(commandcontext, "rotationSteps"), 8);
        })).then(net.minecraft.commands.CommandDispatcher.argument("testsPerRow", IntegerArgumentType.integer()).executes((commandcontext) -> {
            return runAllTestsInClass((CommandListenerWrapper) commandcontext.getSource(), GameTestHarnessTestClassArgument.getTestClassName(commandcontext, "testClassName"), IntegerArgumentType.getInteger(commandcontext, "rotationSteps"), IntegerArgumentType.getInteger(commandcontext, "testsPerRow"));
        }))))).then(((RequiredArgumentBuilder) net.minecraft.commands.CommandDispatcher.argument("rotationSteps", IntegerArgumentType.integer()).executes((commandcontext) -> {
            return runAllTests((CommandListenerWrapper) commandcontext.getSource(), IntegerArgumentType.getInteger(commandcontext, "rotationSteps"), 8);
        })).then(net.minecraft.commands.CommandDispatcher.argument("testsPerRow", IntegerArgumentType.integer()).executes((commandcontext) -> {
            return runAllTests((CommandListenerWrapper) commandcontext.getSource(), IntegerArgumentType.getInteger(commandcontext, "rotationSteps"), IntegerArgumentType.getInteger(commandcontext, "testsPerRow"));
        }))))).then(net.minecraft.commands.CommandDispatcher.literal("export").then(net.minecraft.commands.CommandDispatcher.argument("testName", StringArgumentType.word()).executes((commandcontext) -> {
            return exportTestStructure((CommandListenerWrapper) commandcontext.getSource(), StringArgumentType.getString(commandcontext, "testName"));
        })))).then(net.minecraft.commands.CommandDispatcher.literal("exportthis").executes((commandcontext) -> {
            return exportNearestTestStructure((CommandListenerWrapper) commandcontext.getSource());
        }))).then(net.minecraft.commands.CommandDispatcher.literal("import").then(net.minecraft.commands.CommandDispatcher.argument("testName", StringArgumentType.word()).executes((commandcontext) -> {
            return importTestStructure((CommandListenerWrapper) commandcontext.getSource(), StringArgumentType.getString(commandcontext, "testName"));
        })))).then(((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.literal("pos").executes((commandcontext) -> {
            return showPos((CommandListenerWrapper) commandcontext.getSource(), "pos");
        })).then(net.minecraft.commands.CommandDispatcher.argument("var", StringArgumentType.word()).executes((commandcontext) -> {
            return showPos((CommandListenerWrapper) commandcontext.getSource(), StringArgumentType.getString(commandcontext, "var"));
        })))).then(net.minecraft.commands.CommandDispatcher.literal("create").then(((RequiredArgumentBuilder) net.minecraft.commands.CommandDispatcher.argument("testName", StringArgumentType.word()).executes((commandcontext) -> {
            return createNewStructure((CommandListenerWrapper) commandcontext.getSource(), StringArgumentType.getString(commandcontext, "testName"), 5, 5, 5);
        })).then(((RequiredArgumentBuilder) net.minecraft.commands.CommandDispatcher.argument("width", IntegerArgumentType.integer()).executes((commandcontext) -> {
            return createNewStructure((CommandListenerWrapper) commandcontext.getSource(), StringArgumentType.getString(commandcontext, "testName"), IntegerArgumentType.getInteger(commandcontext, "width"), IntegerArgumentType.getInteger(commandcontext, "width"), IntegerArgumentType.getInteger(commandcontext, "width"));
        })).then(net.minecraft.commands.CommandDispatcher.argument("height", IntegerArgumentType.integer()).then(net.minecraft.commands.CommandDispatcher.argument("depth", IntegerArgumentType.integer()).executes((commandcontext) -> {
            return createNewStructure((CommandListenerWrapper) commandcontext.getSource(), StringArgumentType.getString(commandcontext, "testName"), IntegerArgumentType.getInteger(commandcontext, "width"), IntegerArgumentType.getInteger(commandcontext, "height"), IntegerArgumentType.getInteger(commandcontext, "depth"));
        }))))))).then(((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.literal("clearall").executes((commandcontext) -> {
            return clearAllTests((CommandListenerWrapper) commandcontext.getSource(), 200);
        })).then(net.minecraft.commands.CommandDispatcher.argument("radius", IntegerArgumentType.integer()).executes((commandcontext) -> {
            return clearAllTests((CommandListenerWrapper) commandcontext.getSource(), IntegerArgumentType.getInteger(commandcontext, "radius"));
        }))));
    }

    private static int createNewStructure(CommandListenerWrapper commandlistenerwrapper, String s, int i, int j, int k) {
        if (i <= 48 && j <= 48 && k <= 48) {
            WorldServer worldserver = commandlistenerwrapper.getLevel();
            BlockPosition blockposition = new BlockPosition(commandlistenerwrapper.getPosition());
            BlockPosition blockposition1 = new BlockPosition(blockposition.getX(), commandlistenerwrapper.getLevel().getHeightmapPos(HeightMap.Type.WORLD_SURFACE, blockposition).getY(), blockposition.getZ() + 3);

            GameTestHarnessStructures.createNewEmptyStructureBlock(s.toLowerCase(), blockposition1, new BaseBlockPosition(i, j, k), EnumBlockRotation.NONE, worldserver);

            for (int l = 0; l < i; ++l) {
                for (int i1 = 0; i1 < k; ++i1) {
                    BlockPosition blockposition2 = new BlockPosition(blockposition1.getX() + l, blockposition1.getY() + 1, blockposition1.getZ() + i1);
                    Block block = Blocks.POLISHED_ANDESITE;
                    ArgumentTileLocation argumenttilelocation = new ArgumentTileLocation(block.defaultBlockState(), Collections.emptySet(), (NBTTagCompound) null);

                    argumenttilelocation.place(worldserver, blockposition2, 2);
                }
            }

            GameTestHarnessStructures.addCommandBlockAndButtonToStartTest(blockposition1, new BlockPosition(1, 0, -1), EnumBlockRotation.NONE, worldserver);
            return 0;
        } else {
            throw new IllegalArgumentException("The structure must be less than 48 blocks big in each axis");
        }
    }

    private static int showPos(CommandListenerWrapper commandlistenerwrapper, String s) throws CommandSyntaxException {
        MovingObjectPositionBlock movingobjectpositionblock = (MovingObjectPositionBlock) commandlistenerwrapper.getPlayerOrException().pick(10.0D, 1.0F, false);
        BlockPosition blockposition = movingobjectpositionblock.getBlockPos();
        WorldServer worldserver = commandlistenerwrapper.getLevel();
        Optional<BlockPosition> optional = GameTestHarnessStructures.findStructureBlockContainingPos(blockposition, 15, worldserver);

        if (!optional.isPresent()) {
            optional = GameTestHarnessStructures.findStructureBlockContainingPos(blockposition, 200, worldserver);
        }

        if (!optional.isPresent()) {
            commandlistenerwrapper.sendFailure(new ChatComponentText("Can't find a structure block that contains the targeted pos " + blockposition));
            return 0;
        } else {
            TileEntityStructure tileentitystructure = (TileEntityStructure) worldserver.getBlockEntity((BlockPosition) optional.get());
            BlockPosition blockposition1 = blockposition.subtract((BaseBlockPosition) optional.get());
            int i = blockposition1.getX();
            String s1 = i + ", " + blockposition1.getY() + ", " + blockposition1.getZ();
            String s2 = tileentitystructure.getStructurePath();
            IChatMutableComponent ichatmutablecomponent = (new ChatComponentText(s1)).setStyle(ChatModifier.EMPTY.withBold(true).withColor(EnumChatFormat.GREEN).withHoverEvent(new ChatHoverable(ChatHoverable.EnumHoverAction.SHOW_TEXT, new ChatComponentText("Click to copy to clipboard"))).withClickEvent(new ChatClickable(ChatClickable.EnumClickAction.COPY_TO_CLIPBOARD, "final BlockPos " + s + " = new BlockPos(" + s1 + ");")));

            commandlistenerwrapper.sendSuccess((new ChatComponentText("Position relative to " + s2 + ": ")).append((IChatBaseComponent) ichatmutablecomponent), false);
            PacketDebug.sendGameTestAddMarker(worldserver, new BlockPosition(blockposition), s1, -2147418368, 10000);
            return 1;
        }
    }

    private static int runNearbyTest(CommandListenerWrapper commandlistenerwrapper) {
        BlockPosition blockposition = new BlockPosition(commandlistenerwrapper.getPosition());
        WorldServer worldserver = commandlistenerwrapper.getLevel();
        BlockPosition blockposition1 = GameTestHarnessStructures.findNearestStructureBlock(blockposition, 15, worldserver);

        if (blockposition1 == null) {
            say(worldserver, "Couldn't find any structure block within 15 radius", EnumChatFormat.RED);
            return 0;
        } else {
            GameTestHarnessRunner.clearMarkers(worldserver);
            runTest(worldserver, blockposition1, (GameTestHarnessCollector) null);
            return 1;
        }
    }

    private static int runAllNearbyTests(CommandListenerWrapper commandlistenerwrapper) {
        BlockPosition blockposition = new BlockPosition(commandlistenerwrapper.getPosition());
        WorldServer worldserver = commandlistenerwrapper.getLevel();
        Collection<BlockPosition> collection = GameTestHarnessStructures.findStructureBlocks(blockposition, 200, worldserver);

        if (collection.isEmpty()) {
            say(worldserver, "Couldn't find any structure blocks within 200 block radius", EnumChatFormat.RED);
            return 1;
        } else {
            GameTestHarnessRunner.clearMarkers(worldserver);
            say(commandlistenerwrapper, "Running " + collection.size() + " tests...");
            GameTestHarnessCollector gametestharnesscollector = new GameTestHarnessCollector();

            collection.forEach((blockposition1) -> {
                runTest(worldserver, blockposition1, gametestharnesscollector);
            });
            return 1;
        }
    }

    private static void runTest(WorldServer worldserver, BlockPosition blockposition, @Nullable GameTestHarnessCollector gametestharnesscollector) {
        TileEntityStructure tileentitystructure = (TileEntityStructure) worldserver.getBlockEntity(blockposition);
        String s = tileentitystructure.getStructurePath();
        GameTestHarnessTestFunction gametestharnesstestfunction = GameTestHarnessRegistry.getTestFunction(s);
        GameTestHarnessInfo gametestharnessinfo = new GameTestHarnessInfo(gametestharnesstestfunction, tileentitystructure.getRotation(), worldserver);

        if (gametestharnesscollector != null) {
            gametestharnesscollector.addTestToTrack(gametestharnessinfo);
            gametestharnessinfo.addListener(new GameTestHarnessTestCommand.a(worldserver, gametestharnesscollector));
        }

        runTestPreparation(gametestharnesstestfunction, worldserver);
        AxisAlignedBB axisalignedbb = GameTestHarnessStructures.getStructureBounds(tileentitystructure);
        BlockPosition blockposition1 = new BlockPosition(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ);

        GameTestHarnessRunner.runTest(gametestharnessinfo, blockposition1, GameTestHarnessTicker.SINGLETON);
    }

    static void showTestSummaryIfAllDone(WorldServer worldserver, GameTestHarnessCollector gametestharnesscollector) {
        if (gametestharnesscollector.isDone()) {
            say(worldserver, "GameTest done! " + gametestharnesscollector.getTotalCount() + " tests were run", EnumChatFormat.WHITE);
            if (gametestharnesscollector.hasFailedRequired()) {
                say(worldserver, gametestharnesscollector.getFailedRequiredCount() + " required tests failed :(", EnumChatFormat.RED);
            } else {
                say(worldserver, "All required tests passed :)", EnumChatFormat.GREEN);
            }

            if (gametestharnesscollector.hasFailedOptional()) {
                say(worldserver, gametestharnesscollector.getFailedOptionalCount() + " optional tests failed", EnumChatFormat.GRAY);
            }
        }

    }

    private static int clearAllTests(CommandListenerWrapper commandlistenerwrapper, int i) {
        WorldServer worldserver = commandlistenerwrapper.getLevel();

        GameTestHarnessRunner.clearMarkers(worldserver);
        BlockPosition blockposition = new BlockPosition(commandlistenerwrapper.getPosition().x, (double) commandlistenerwrapper.getLevel().getHeightmapPos(HeightMap.Type.WORLD_SURFACE, new BlockPosition(commandlistenerwrapper.getPosition())).getY(), commandlistenerwrapper.getPosition().z);

        GameTestHarnessRunner.clearAllTests(worldserver, blockposition, GameTestHarnessTicker.SINGLETON, MathHelper.clamp(i, (int) 0, (int) 1024));
        return 1;
    }

    private static int runTest(CommandListenerWrapper commandlistenerwrapper, GameTestHarnessTestFunction gametestharnesstestfunction, int i) {
        WorldServer worldserver = commandlistenerwrapper.getLevel();
        BlockPosition blockposition = new BlockPosition(commandlistenerwrapper.getPosition());
        int j = commandlistenerwrapper.getLevel().getHeightmapPos(HeightMap.Type.WORLD_SURFACE, blockposition).getY();
        BlockPosition blockposition1 = new BlockPosition(blockposition.getX(), j, blockposition.getZ() + 3);

        GameTestHarnessRunner.clearMarkers(worldserver);
        runTestPreparation(gametestharnesstestfunction, worldserver);
        EnumBlockRotation enumblockrotation = GameTestHarnessStructures.getRotationForRotationSteps(i);
        GameTestHarnessInfo gametestharnessinfo = new GameTestHarnessInfo(gametestharnesstestfunction, enumblockrotation, worldserver);

        GameTestHarnessRunner.runTest(gametestharnessinfo, blockposition1, GameTestHarnessTicker.SINGLETON);
        return 1;
    }

    private static void runTestPreparation(GameTestHarnessTestFunction gametestharnesstestfunction, WorldServer worldserver) {
        Consumer<WorldServer> consumer = GameTestHarnessRegistry.getBeforeBatchFunction(gametestharnesstestfunction.getBatchName());

        if (consumer != null) {
            consumer.accept(worldserver);
        }

    }

    private static int runAllTests(CommandListenerWrapper commandlistenerwrapper, int i, int j) {
        GameTestHarnessRunner.clearMarkers(commandlistenerwrapper.getLevel());
        Collection<GameTestHarnessTestFunction> collection = GameTestHarnessRegistry.getAllTestFunctions();

        say(commandlistenerwrapper, "Running all " + collection.size() + " tests...");
        GameTestHarnessRegistry.forgetFailedTests();
        runTests(commandlistenerwrapper, collection, i, j);
        return 1;
    }

    private static int runAllTestsInClass(CommandListenerWrapper commandlistenerwrapper, String s, int i, int j) {
        Collection<GameTestHarnessTestFunction> collection = GameTestHarnessRegistry.getTestFunctionsForClassName(s);

        GameTestHarnessRunner.clearMarkers(commandlistenerwrapper.getLevel());
        int k = collection.size();

        say(commandlistenerwrapper, "Running " + k + " tests from " + s + "...");
        GameTestHarnessRegistry.forgetFailedTests();
        runTests(commandlistenerwrapper, collection, i, j);
        return 1;
    }

    private static int runLastFailedTests(CommandListenerWrapper commandlistenerwrapper, boolean flag, int i, int j) {
        Collection collection;

        if (flag) {
            collection = (Collection) GameTestHarnessRegistry.getLastFailedTests().stream().filter(GameTestHarnessTestFunction::isRequired).collect(Collectors.toList());
        } else {
            collection = GameTestHarnessRegistry.getLastFailedTests();
        }

        if (collection.isEmpty()) {
            say(commandlistenerwrapper, "No failed tests to rerun");
            return 0;
        } else {
            GameTestHarnessRunner.clearMarkers(commandlistenerwrapper.getLevel());
            int k = collection.size();

            say(commandlistenerwrapper, "Rerunning " + k + " failed tests (" + (flag ? "only required tests" : "including optional tests") + ")");
            runTests(commandlistenerwrapper, collection, i, j);
            return 1;
        }
    }

    private static void runTests(CommandListenerWrapper commandlistenerwrapper, Collection<GameTestHarnessTestFunction> collection, int i, int j) {
        BlockPosition blockposition = new BlockPosition(commandlistenerwrapper.getPosition());
        BlockPosition blockposition1 = new BlockPosition(blockposition.getX(), commandlistenerwrapper.getLevel().getHeightmapPos(HeightMap.Type.WORLD_SURFACE, blockposition).getY(), blockposition.getZ() + 3);
        WorldServer worldserver = commandlistenerwrapper.getLevel();
        EnumBlockRotation enumblockrotation = GameTestHarnessStructures.getRotationForRotationSteps(i);
        Collection<GameTestHarnessInfo> collection1 = GameTestHarnessRunner.runTests(collection, blockposition1, enumblockrotation, worldserver, GameTestHarnessTicker.SINGLETON, j);
        GameTestHarnessCollector gametestharnesscollector = new GameTestHarnessCollector(collection1);

        gametestharnesscollector.addListener(new GameTestHarnessTestCommand.a(worldserver, gametestharnesscollector));
        gametestharnesscollector.addFailureListener((gametestharnessinfo) -> {
            GameTestHarnessRegistry.rememberFailedTest(gametestharnessinfo.getTestFunction());
        });
    }

    private static void say(CommandListenerWrapper commandlistenerwrapper, String s) {
        commandlistenerwrapper.sendSuccess(new ChatComponentText(s), false);
    }

    private static int exportNearestTestStructure(CommandListenerWrapper commandlistenerwrapper) {
        BlockPosition blockposition = new BlockPosition(commandlistenerwrapper.getPosition());
        WorldServer worldserver = commandlistenerwrapper.getLevel();
        BlockPosition blockposition1 = GameTestHarnessStructures.findNearestStructureBlock(blockposition, 15, worldserver);

        if (blockposition1 == null) {
            say(worldserver, "Couldn't find any structure block within 15 radius", EnumChatFormat.RED);
            return 0;
        } else {
            TileEntityStructure tileentitystructure = (TileEntityStructure) worldserver.getBlockEntity(blockposition1);
            String s = tileentitystructure.getStructurePath();

            return exportTestStructure(commandlistenerwrapper, s);
        }
    }

    private static int exportTestStructure(CommandListenerWrapper commandlistenerwrapper, String s) {
        Path path = Paths.get(GameTestHarnessStructures.testStructuresDir);
        MinecraftKey minecraftkey = new MinecraftKey("minecraft", s);
        Path path1 = commandlistenerwrapper.getLevel().getStructureManager().createPathToStructure(minecraftkey, ".nbt");
        Path path2 = DebugReportNBT.convertStructure(path1, s, path);

        if (path2 == null) {
            say(commandlistenerwrapper, "Failed to export " + path1);
            return 1;
        } else {
            try {
                Files.createDirectories(path2.getParent());
            } catch (IOException ioexception) {
                say(commandlistenerwrapper, "Could not create folder " + path2.getParent());
                ioexception.printStackTrace();
                return 1;
            }

            say(commandlistenerwrapper, "Exported " + s + " to " + path2.toAbsolutePath());
            return 0;
        }
    }

    private static int importTestStructure(CommandListenerWrapper commandlistenerwrapper, String s) {
        Path path = Paths.get(GameTestHarnessStructures.testStructuresDir, s + ".snbt");
        MinecraftKey minecraftkey = new MinecraftKey("minecraft", s);
        Path path1 = commandlistenerwrapper.getLevel().getStructureManager().createPathToStructure(minecraftkey, ".nbt");

        try {
            BufferedReader bufferedreader = Files.newBufferedReader(path);
            String s1 = IOUtils.toString(bufferedreader);

            Files.createDirectories(path1.getParent());
            OutputStream outputstream = Files.newOutputStream(path1);

            try {
                NBTCompressedStreamTools.writeCompressed(GameProfileSerializer.snbtToStructure(s1), outputstream);
            } catch (Throwable throwable) {
                if (outputstream != null) {
                    try {
                        outputstream.close();
                    } catch (Throwable throwable1) {
                        throwable.addSuppressed(throwable1);
                    }
                }

                throw throwable;
            }

            if (outputstream != null) {
                outputstream.close();
            }

            say(commandlistenerwrapper, "Imported to " + path1.toAbsolutePath());
            return 0;
        } catch (CommandSyntaxException | IOException ioexception) {
            System.err.println("Failed to load structure " + s);
            ioexception.printStackTrace();
            return 1;
        }
    }

    private static void say(WorldServer worldserver, String s, EnumChatFormat enumchatformat) {
        worldserver.getPlayers((entityplayer) -> {
            return true;
        }).forEach((entityplayer) -> {
            entityplayer.sendMessage(new ChatComponentText(enumchatformat + s), SystemUtils.NIL_UUID);
        });
    }

    private static class a implements GameTestHarnessListener {

        private final WorldServer level;
        private final GameTestHarnessCollector tracker;

        public a(WorldServer worldserver, GameTestHarnessCollector gametestharnesscollector) {
            this.level = worldserver;
            this.tracker = gametestharnesscollector;
        }

        @Override
        public void testStructureLoaded(GameTestHarnessInfo gametestharnessinfo) {}

        @Override
        public void testPassed(GameTestHarnessInfo gametestharnessinfo) {
            GameTestHarnessTestCommand.showTestSummaryIfAllDone(this.level, this.tracker);
        }

        @Override
        public void testFailed(GameTestHarnessInfo gametestharnessinfo) {
            GameTestHarnessTestCommand.showTestSummaryIfAllDone(this.level, this.tracker);
        }
    }
}
