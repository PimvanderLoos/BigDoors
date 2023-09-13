package net.minecraft.gametest.framework;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
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
import net.minecraft.nbt.MojangsonParser;
import net.minecraft.nbt.NBTCompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.chat.ChatClickable;
import net.minecraft.network.chat.ChatComponentText;
import net.minecraft.network.chat.ChatHoverable;
import net.minecraft.network.chat.ChatModifier;
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

    public static void a(CommandDispatcher<CommandListenerWrapper> commanddispatcher) {
        commanddispatcher.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("test").then(net.minecraft.commands.CommandDispatcher.a("runthis").executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource());
        }))).then(net.minecraft.commands.CommandDispatcher.a("runthese").executes((commandcontext) -> {
            return b((CommandListenerWrapper) commandcontext.getSource());
        }))).then(((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("runfailed").executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), false, 0, 8);
        })).then(((RequiredArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("onlyRequiredTests", (ArgumentType) BoolArgumentType.bool()).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), BoolArgumentType.getBool(commandcontext, "onlyRequiredTests"), 0, 8);
        })).then(((RequiredArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("rotationSteps", (ArgumentType) IntegerArgumentType.integer()).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), BoolArgumentType.getBool(commandcontext, "onlyRequiredTests"), IntegerArgumentType.getInteger(commandcontext, "rotationSteps"), 8);
        })).then(net.minecraft.commands.CommandDispatcher.a("testsPerRow", (ArgumentType) IntegerArgumentType.integer()).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), BoolArgumentType.getBool(commandcontext, "onlyRequiredTests"), IntegerArgumentType.getInteger(commandcontext, "rotationSteps"), IntegerArgumentType.getInteger(commandcontext, "testsPerRow"));
        })))))).then(net.minecraft.commands.CommandDispatcher.a("run").then(((RequiredArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("testName", (ArgumentType) GameTestHarnessTestFunctionArgument.a()).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), GameTestHarnessTestFunctionArgument.a(commandcontext, "testName"), 0);
        })).then(net.minecraft.commands.CommandDispatcher.a("rotationSteps", (ArgumentType) IntegerArgumentType.integer()).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), GameTestHarnessTestFunctionArgument.a(commandcontext, "testName"), IntegerArgumentType.getInteger(commandcontext, "rotationSteps"));
        }))))).then(((LiteralArgumentBuilder) ((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("runall").executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), 0, 8);
        })).then(((RequiredArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("testClassName", (ArgumentType) GameTestHarnessTestClassArgument.a()).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), GameTestHarnessTestClassArgument.a(commandcontext, "testClassName"), 0, 8);
        })).then(((RequiredArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("rotationSteps", (ArgumentType) IntegerArgumentType.integer()).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), GameTestHarnessTestClassArgument.a(commandcontext, "testClassName"), IntegerArgumentType.getInteger(commandcontext, "rotationSteps"), 8);
        })).then(net.minecraft.commands.CommandDispatcher.a("testsPerRow", (ArgumentType) IntegerArgumentType.integer()).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), GameTestHarnessTestClassArgument.a(commandcontext, "testClassName"), IntegerArgumentType.getInteger(commandcontext, "rotationSteps"), IntegerArgumentType.getInteger(commandcontext, "testsPerRow"));
        }))))).then(((RequiredArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("rotationSteps", (ArgumentType) IntegerArgumentType.integer()).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), IntegerArgumentType.getInteger(commandcontext, "rotationSteps"), 8);
        })).then(net.minecraft.commands.CommandDispatcher.a("testsPerRow", (ArgumentType) IntegerArgumentType.integer()).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), IntegerArgumentType.getInteger(commandcontext, "rotationSteps"), IntegerArgumentType.getInteger(commandcontext, "testsPerRow"));
        }))))).then(net.minecraft.commands.CommandDispatcher.a("export").then(net.minecraft.commands.CommandDispatcher.a("testName", (ArgumentType) StringArgumentType.word()).executes((commandcontext) -> {
            return c((CommandListenerWrapper) commandcontext.getSource(), StringArgumentType.getString(commandcontext, "testName"));
        })))).then(net.minecraft.commands.CommandDispatcher.a("exportthis").executes((commandcontext) -> {
            return c((CommandListenerWrapper) commandcontext.getSource());
        }))).then(net.minecraft.commands.CommandDispatcher.a("import").then(net.minecraft.commands.CommandDispatcher.a("testName", (ArgumentType) StringArgumentType.word()).executes((commandcontext) -> {
            return d((CommandListenerWrapper) commandcontext.getSource(), StringArgumentType.getString(commandcontext, "testName"));
        })))).then(((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("pos").executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), "pos");
        })).then(net.minecraft.commands.CommandDispatcher.a("var", (ArgumentType) StringArgumentType.word()).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), StringArgumentType.getString(commandcontext, "var"));
        })))).then(net.minecraft.commands.CommandDispatcher.a("create").then(((RequiredArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("testName", (ArgumentType) StringArgumentType.word()).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), StringArgumentType.getString(commandcontext, "testName"), 5, 5, 5);
        })).then(((RequiredArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("width", (ArgumentType) IntegerArgumentType.integer()).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), StringArgumentType.getString(commandcontext, "testName"), IntegerArgumentType.getInteger(commandcontext, "width"), IntegerArgumentType.getInteger(commandcontext, "width"), IntegerArgumentType.getInteger(commandcontext, "width"));
        })).then(net.minecraft.commands.CommandDispatcher.a("height", (ArgumentType) IntegerArgumentType.integer()).then(net.minecraft.commands.CommandDispatcher.a("depth", (ArgumentType) IntegerArgumentType.integer()).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), StringArgumentType.getString(commandcontext, "testName"), IntegerArgumentType.getInteger(commandcontext, "width"), IntegerArgumentType.getInteger(commandcontext, "height"), IntegerArgumentType.getInteger(commandcontext, "depth"));
        }))))))).then(((LiteralArgumentBuilder) net.minecraft.commands.CommandDispatcher.a("clearall").executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), 200);
        })).then(net.minecraft.commands.CommandDispatcher.a("radius", (ArgumentType) IntegerArgumentType.integer()).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), IntegerArgumentType.getInteger(commandcontext, "radius"));
        }))));
    }

    private static int a(CommandListenerWrapper commandlistenerwrapper, String s, int i, int j, int k) {
        if (i <= 48 && j <= 48 && k <= 48) {
            WorldServer worldserver = commandlistenerwrapper.getWorld();
            BlockPosition blockposition = new BlockPosition(commandlistenerwrapper.getPosition());
            BlockPosition blockposition1 = new BlockPosition(blockposition.getX(), commandlistenerwrapper.getWorld().getHighestBlockYAt(HeightMap.Type.WORLD_SURFACE, blockposition).getY(), blockposition.getZ() + 3);

            GameTestHarnessStructures.a(s.toLowerCase(), blockposition1, new BlockPosition(i, j, k), EnumBlockRotation.NONE, worldserver);

            for (int l = 0; l < i; ++l) {
                for (int i1 = 0; i1 < k; ++i1) {
                    BlockPosition blockposition2 = new BlockPosition(blockposition1.getX() + l, blockposition1.getY() + 1, blockposition1.getZ() + i1);
                    Block block = Blocks.POLISHED_ANDESITE;
                    ArgumentTileLocation argumenttilelocation = new ArgumentTileLocation(block.getBlockData(), Collections.EMPTY_SET, (NBTTagCompound) null);

                    argumenttilelocation.a(worldserver, blockposition2, 2);
                }
            }

            GameTestHarnessStructures.a(blockposition1, new BlockPosition(1, 0, -1), EnumBlockRotation.NONE, worldserver);
            return 0;
        } else {
            throw new IllegalArgumentException("The structure must be less than 48 blocks big in each axis");
        }
    }

    private static int a(CommandListenerWrapper commandlistenerwrapper, String s) throws CommandSyntaxException {
        MovingObjectPositionBlock movingobjectpositionblock = (MovingObjectPositionBlock) commandlistenerwrapper.h().a(10.0D, 1.0F, false);
        BlockPosition blockposition = movingobjectpositionblock.getBlockPosition();
        WorldServer worldserver = commandlistenerwrapper.getWorld();
        Optional<BlockPosition> optional = GameTestHarnessStructures.a(blockposition, 15, worldserver);

        if (!optional.isPresent()) {
            optional = GameTestHarnessStructures.a(blockposition, 200, worldserver);
        }

        if (!optional.isPresent()) {
            commandlistenerwrapper.sendFailureMessage(new ChatComponentText("Can't find a structure block that contains the targeted pos " + blockposition));
            return 0;
        } else {
            TileEntityStructure tileentitystructure = (TileEntityStructure) worldserver.getTileEntity((BlockPosition) optional.get());
            BlockPosition blockposition1 = blockposition.b((BaseBlockPosition) optional.get());
            String s1 = blockposition1.getX() + ", " + blockposition1.getY() + ", " + blockposition1.getZ();
            String s2 = tileentitystructure.f();
            IChatMutableComponent ichatmutablecomponent = (new ChatComponentText(s1)).setChatModifier(ChatModifier.a.setBold(true).setColor(EnumChatFormat.GREEN).setChatHoverable(new ChatHoverable(ChatHoverable.EnumHoverAction.SHOW_TEXT, new ChatComponentText("Click to copy to clipboard"))).setChatClickable(new ChatClickable(ChatClickable.EnumClickAction.COPY_TO_CLIPBOARD, "final BlockPos " + s + " = new BlockPos(" + s1 + ");")));

            commandlistenerwrapper.sendMessage((new ChatComponentText("Position relative to " + s2 + ": ")).addSibling(ichatmutablecomponent), false);
            PacketDebug.a(worldserver, new BlockPosition(blockposition), s1, -2147418368, 10000);
            return 1;
        }
    }

    private static int a(CommandListenerWrapper commandlistenerwrapper) {
        BlockPosition blockposition = new BlockPosition(commandlistenerwrapper.getPosition());
        WorldServer worldserver = commandlistenerwrapper.getWorld();
        BlockPosition blockposition1 = GameTestHarnessStructures.b(blockposition, 15, worldserver);

        if (blockposition1 == null) {
            a(worldserver, "Couldn't find any structure block within 15 radius", EnumChatFormat.RED);
            return 0;
        } else {
            GameTestHarnessRunner.a(worldserver);
            a(worldserver, blockposition1, (GameTestHarnessCollector) null);
            return 1;
        }
    }

    private static int b(CommandListenerWrapper commandlistenerwrapper) {
        BlockPosition blockposition = new BlockPosition(commandlistenerwrapper.getPosition());
        WorldServer worldserver = commandlistenerwrapper.getWorld();
        Collection<BlockPosition> collection = GameTestHarnessStructures.c(blockposition, 200, worldserver);

        if (collection.isEmpty()) {
            a(worldserver, "Couldn't find any structure blocks within 200 block radius", EnumChatFormat.RED);
            return 1;
        } else {
            GameTestHarnessRunner.a(worldserver);
            b(commandlistenerwrapper, "Running " + collection.size() + " tests...");
            GameTestHarnessCollector gametestharnesscollector = new GameTestHarnessCollector();

            collection.forEach((blockposition1) -> {
                a(worldserver, blockposition1, gametestharnesscollector);
            });
            return 1;
        }
    }

    private static void a(WorldServer worldserver, BlockPosition blockposition, @Nullable GameTestHarnessCollector gametestharnesscollector) {
        TileEntityStructure tileentitystructure = (TileEntityStructure) worldserver.getTileEntity(blockposition);
        String s = tileentitystructure.f();
        GameTestHarnessTestFunction gametestharnesstestfunction = GameTestHarnessRegistry.e(s);
        GameTestHarnessInfo gametestharnessinfo = new GameTestHarnessInfo(gametestharnesstestfunction, tileentitystructure.l(), worldserver);

        if (gametestharnesscollector != null) {
            gametestharnesscollector.a(gametestharnessinfo);
            gametestharnessinfo.a((GameTestHarnessListener) (new GameTestHarnessTestCommand.a(worldserver, gametestharnesscollector)));
        }

        a(gametestharnesstestfunction, worldserver);
        AxisAlignedBB axisalignedbb = GameTestHarnessStructures.a(tileentitystructure);
        BlockPosition blockposition1 = new BlockPosition(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ);

        GameTestHarnessRunner.a(gametestharnessinfo, blockposition1, GameTestHarnessTicker.a);
    }

    private static void b(WorldServer worldserver, GameTestHarnessCollector gametestharnesscollector) {
        if (gametestharnesscollector.i()) {
            a(worldserver, "GameTest done! " + gametestharnesscollector.h() + " tests were run", EnumChatFormat.WHITE);
            if (gametestharnesscollector.d()) {
                a(worldserver, "" + gametestharnesscollector.a() + " required tests failed :(", EnumChatFormat.RED);
            } else {
                a(worldserver, "All required tests passed :)", EnumChatFormat.GREEN);
            }

            if (gametestharnesscollector.e()) {
                a(worldserver, "" + gametestharnesscollector.b() + " optional tests failed", EnumChatFormat.GRAY);
            }
        }

    }

    private static int a(CommandListenerWrapper commandlistenerwrapper, int i) {
        WorldServer worldserver = commandlistenerwrapper.getWorld();

        GameTestHarnessRunner.a(worldserver);
        BlockPosition blockposition = new BlockPosition(commandlistenerwrapper.getPosition().x, (double) commandlistenerwrapper.getWorld().getHighestBlockYAt(HeightMap.Type.WORLD_SURFACE, new BlockPosition(commandlistenerwrapper.getPosition())).getY(), commandlistenerwrapper.getPosition().z);

        GameTestHarnessRunner.a(worldserver, blockposition, GameTestHarnessTicker.a, MathHelper.clamp(i, 0, 1024));
        return 1;
    }

    private static int a(CommandListenerWrapper commandlistenerwrapper, GameTestHarnessTestFunction gametestharnesstestfunction, int i) {
        WorldServer worldserver = commandlistenerwrapper.getWorld();
        BlockPosition blockposition = new BlockPosition(commandlistenerwrapper.getPosition());
        int j = commandlistenerwrapper.getWorld().getHighestBlockYAt(HeightMap.Type.WORLD_SURFACE, blockposition).getY();
        BlockPosition blockposition1 = new BlockPosition(blockposition.getX(), j, blockposition.getZ() + 3);

        GameTestHarnessRunner.a(worldserver);
        a(gametestharnesstestfunction, worldserver);
        EnumBlockRotation enumblockrotation = GameTestHarnessStructures.a(i);
        GameTestHarnessInfo gametestharnessinfo = new GameTestHarnessInfo(gametestharnesstestfunction, enumblockrotation, worldserver);

        GameTestHarnessRunner.a(gametestharnessinfo, blockposition1, GameTestHarnessTicker.a);
        return 1;
    }

    private static void a(GameTestHarnessTestFunction gametestharnesstestfunction, WorldServer worldserver) {
        Consumer<WorldServer> consumer = GameTestHarnessRegistry.c(gametestharnesstestfunction.e());

        if (consumer != null) {
            consumer.accept(worldserver);
        }

    }

    private static int a(CommandListenerWrapper commandlistenerwrapper, int i, int j) {
        GameTestHarnessRunner.a(commandlistenerwrapper.getWorld());
        Collection<GameTestHarnessTestFunction> collection = GameTestHarnessRegistry.a();

        b(commandlistenerwrapper, "Running all " + collection.size() + " tests...");
        GameTestHarnessRegistry.d();
        a(commandlistenerwrapper, collection, i, j);
        return 1;
    }

    private static int a(CommandListenerWrapper commandlistenerwrapper, String s, int i, int j) {
        Collection<GameTestHarnessTestFunction> collection = GameTestHarnessRegistry.a(s);

        GameTestHarnessRunner.a(commandlistenerwrapper.getWorld());
        b(commandlistenerwrapper, "Running " + collection.size() + " tests from " + s + "...");
        GameTestHarnessRegistry.d();
        a(commandlistenerwrapper, collection, i, j);
        return 1;
    }

    private static int a(CommandListenerWrapper commandlistenerwrapper, boolean flag, int i, int j) {
        Collection collection;

        if (flag) {
            collection = (Collection) GameTestHarnessRegistry.c().stream().filter(GameTestHarnessTestFunction::d).collect(Collectors.toList());
        } else {
            collection = GameTestHarnessRegistry.c();
        }

        if (collection.isEmpty()) {
            b(commandlistenerwrapper, "No failed tests to rerun");
            return 0;
        } else {
            GameTestHarnessRunner.a(commandlistenerwrapper.getWorld());
            b(commandlistenerwrapper, "Rerunning " + collection.size() + " failed tests (" + (flag ? "only required tests" : "including optional tests") + ")");
            a(commandlistenerwrapper, collection, i, j);
            return 1;
        }
    }

    private static void a(CommandListenerWrapper commandlistenerwrapper, Collection<GameTestHarnessTestFunction> collection, int i, int j) {
        BlockPosition blockposition = new BlockPosition(commandlistenerwrapper.getPosition());
        BlockPosition blockposition1 = new BlockPosition(blockposition.getX(), commandlistenerwrapper.getWorld().getHighestBlockYAt(HeightMap.Type.WORLD_SURFACE, blockposition).getY(), blockposition.getZ() + 3);
        WorldServer worldserver = commandlistenerwrapper.getWorld();
        EnumBlockRotation enumblockrotation = GameTestHarnessStructures.a(i);
        Collection<GameTestHarnessInfo> collection1 = GameTestHarnessRunner.b(collection, blockposition1, enumblockrotation, worldserver, GameTestHarnessTicker.a, j);
        GameTestHarnessCollector gametestharnesscollector = new GameTestHarnessCollector(collection1);

        gametestharnesscollector.a((GameTestHarnessListener) (new GameTestHarnessTestCommand.a(worldserver, gametestharnesscollector)));
        gametestharnesscollector.a((gametestharnessinfo) -> {
            GameTestHarnessRegistry.a(gametestharnessinfo.u());
        });
    }

    private static void b(CommandListenerWrapper commandlistenerwrapper, String s) {
        commandlistenerwrapper.sendMessage(new ChatComponentText(s), false);
    }

    private static int c(CommandListenerWrapper commandlistenerwrapper) {
        BlockPosition blockposition = new BlockPosition(commandlistenerwrapper.getPosition());
        WorldServer worldserver = commandlistenerwrapper.getWorld();
        BlockPosition blockposition1 = GameTestHarnessStructures.b(blockposition, 15, worldserver);

        if (blockposition1 == null) {
            a(worldserver, "Couldn't find any structure block within 15 radius", EnumChatFormat.RED);
            return 0;
        } else {
            TileEntityStructure tileentitystructure = (TileEntityStructure) worldserver.getTileEntity(blockposition1);
            String s = tileentitystructure.f();

            return c(commandlistenerwrapper, s);
        }
    }

    private static int c(CommandListenerWrapper commandlistenerwrapper, String s) {
        Path path = Paths.get(GameTestHarnessStructures.a);
        MinecraftKey minecraftkey = new MinecraftKey("minecraft", s);
        Path path1 = commandlistenerwrapper.getWorld().n().a(minecraftkey, ".nbt");
        Path path2 = DebugReportNBT.a(path1, s, path);

        if (path2 == null) {
            b(commandlistenerwrapper, "Failed to export " + path1);
            return 1;
        } else {
            try {
                Files.createDirectories(path2.getParent());
            } catch (IOException ioexception) {
                b(commandlistenerwrapper, "Could not create folder " + path2.getParent());
                ioexception.printStackTrace();
                return 1;
            }

            b(commandlistenerwrapper, "Exported " + s + " to " + path2.toAbsolutePath());
            return 0;
        }
    }

    private static int d(CommandListenerWrapper commandlistenerwrapper, String s) {
        Path path = Paths.get(GameTestHarnessStructures.a, s + ".snbt");
        MinecraftKey minecraftkey = new MinecraftKey("minecraft", s);
        Path path1 = commandlistenerwrapper.getWorld().n().a(minecraftkey, ".nbt");

        try {
            BufferedReader bufferedreader = Files.newBufferedReader(path);
            String s1 = IOUtils.toString(bufferedreader);

            Files.createDirectories(path1.getParent());
            OutputStream outputstream = Files.newOutputStream(path1);
            Throwable throwable = null;

            try {
                NBTCompressedStreamTools.a(MojangsonParser.parse(s1), outputstream);
            } catch (Throwable throwable1) {
                throwable = throwable1;
                throw throwable1;
            } finally {
                if (outputstream != null) {
                    if (throwable != null) {
                        try {
                            outputstream.close();
                        } catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    } else {
                        outputstream.close();
                    }
                }

            }

            b(commandlistenerwrapper, "Imported to " + path1.toAbsolutePath());
            return 0;
        } catch (CommandSyntaxException | IOException ioexception) {
            System.err.println("Failed to load structure " + s);
            ioexception.printStackTrace();
            return 1;
        }
    }

    private static void a(WorldServer worldserver, String s, EnumChatFormat enumchatformat) {
        worldserver.a((entityplayer) -> {
            return true;
        }).forEach((entityplayer) -> {
            entityplayer.sendMessage(new ChatComponentText(enumchatformat + s), SystemUtils.b);
        });
    }

    static class a implements GameTestHarnessListener {

        private final WorldServer a;
        private final GameTestHarnessCollector b;

        public a(WorldServer worldserver, GameTestHarnessCollector gametestharnesscollector) {
            this.a = worldserver;
            this.b = gametestharnesscollector;
        }

        @Override
        public void a(GameTestHarnessInfo gametestharnessinfo) {}

        @Override
        public void c(GameTestHarnessInfo gametestharnessinfo) {
            GameTestHarnessTestCommand.b(this.a, this.b);
        }
    }
}
