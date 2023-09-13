package net.minecraft.server;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import org.apache.commons.io.IOUtils;

public class GameTestHarnessTestCommand {

    public static void a(com.mojang.brigadier.CommandDispatcher<CommandListenerWrapper> com_mojang_brigadier_commanddispatcher) {
        com_mojang_brigadier_commanddispatcher.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) ((LiteralArgumentBuilder) CommandDispatcher.a("test").then(CommandDispatcher.a("runthis").executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource());
        }))).then(CommandDispatcher.a("runthese").executes((commandcontext) -> {
            return b((CommandListenerWrapper) commandcontext.getSource());
        }))).then(CommandDispatcher.a("run").then(CommandDispatcher.a("testName", (ArgumentType) GameTestHarnessTestFunctionArgument.a()).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), GameTestHarnessTestFunctionArgument.a(commandcontext, "testName"));
        })))).then(((LiteralArgumentBuilder) CommandDispatcher.a("runall").executes((commandcontext) -> {
            return c((CommandListenerWrapper) commandcontext.getSource());
        })).then(CommandDispatcher.a("testClassName", (ArgumentType) GameTestHarnessTestClassArgument.a()).executes((commandcontext) -> {
            return b((CommandListenerWrapper) commandcontext.getSource(), GameTestHarnessTestClassArgument.a(commandcontext, "testClassName"));
        })))).then(CommandDispatcher.a("export").then(CommandDispatcher.a("testName", (ArgumentType) StringArgumentType.word()).executes((commandcontext) -> {
            return d((CommandListenerWrapper) commandcontext.getSource(), StringArgumentType.getString(commandcontext, "testName"));
        })))).then(CommandDispatcher.a("import").then(CommandDispatcher.a("testName", (ArgumentType) StringArgumentType.word()).executes((commandcontext) -> {
            return e((CommandListenerWrapper) commandcontext.getSource(), StringArgumentType.getString(commandcontext, "testName"));
        })))).then(((LiteralArgumentBuilder) CommandDispatcher.a("pos").executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), "pos");
        })).then(CommandDispatcher.a("var", (ArgumentType) StringArgumentType.word()).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), StringArgumentType.getString(commandcontext, "var"));
        })))).then(CommandDispatcher.a("create").then(((RequiredArgumentBuilder) CommandDispatcher.a("testName", (ArgumentType) StringArgumentType.word()).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), StringArgumentType.getString(commandcontext, "testName"), 5, 5, 5);
        })).then(((RequiredArgumentBuilder) CommandDispatcher.a("width", (ArgumentType) IntegerArgumentType.integer()).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), StringArgumentType.getString(commandcontext, "testName"), IntegerArgumentType.getInteger(commandcontext, "width"), IntegerArgumentType.getInteger(commandcontext, "width"), IntegerArgumentType.getInteger(commandcontext, "width"));
        })).then(CommandDispatcher.a("height", (ArgumentType) IntegerArgumentType.integer()).then(CommandDispatcher.a("depth", (ArgumentType) IntegerArgumentType.integer()).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), StringArgumentType.getString(commandcontext, "testName"), IntegerArgumentType.getInteger(commandcontext, "width"), IntegerArgumentType.getInteger(commandcontext, "height"), IntegerArgumentType.getInteger(commandcontext, "depth"));
        }))))))).then(((LiteralArgumentBuilder) CommandDispatcher.a("clearall").executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), 200);
        })).then(CommandDispatcher.a("radius", (ArgumentType) IntegerArgumentType.integer()).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), IntegerArgumentType.getInteger(commandcontext, "radius"));
        }))));
    }

    private static int a(CommandListenerWrapper commandlistenerwrapper, String s, int i, int j, int k) {
        if (i <= 32 && j <= 32 && k <= 32) {
            WorldServer worldserver = commandlistenerwrapper.getWorld();
            BlockPosition blockposition = new BlockPosition(commandlistenerwrapper.getPosition());
            BlockPosition blockposition1 = new BlockPosition(blockposition.getX(), commandlistenerwrapper.getWorld().getHighestBlockYAt(HeightMap.Type.WORLD_SURFACE, blockposition).getY(), blockposition.getZ() + 3);

            GameTestHarnessStructures.a(s.toLowerCase(), blockposition1, new BlockPosition(i, j, k), 2, worldserver);

            for (int l = 0; l < i; ++l) {
                for (int i1 = 0; i1 < k; ++i1) {
                    BlockPosition blockposition2 = new BlockPosition(blockposition1.getX() + l, blockposition1.getY() + 1, blockposition1.getZ() + i1);
                    Block block = Blocks.POLISHED_ANDESITE;
                    ArgumentTileLocation argumenttilelocation = new ArgumentTileLocation(block.getBlockData(), Collections.EMPTY_SET, (NBTTagCompound) null);

                    argumenttilelocation.a(worldserver, blockposition2, 2);
                }
            }

            GameTestHarnessStructures.a(blockposition1.b(1, 0, -1), worldserver);
            return 0;
        } else {
            throw new IllegalArgumentException("The structure must be less than 32 blocks big in each axis");
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
            IChatBaseComponent ichatbasecomponent = (new ChatComponentText(s1)).setChatModifier((new ChatModifier()).setBold(true).setColor(EnumChatFormat.GREEN).setChatHoverable(new ChatHoverable(ChatHoverable.EnumHoverAction.SHOW_TEXT, new ChatComponentText("Click to copy to clipboard"))).setChatClickable(new ChatClickable(ChatClickable.EnumClickAction.COPY_TO_CLIPBOARD, "final BlockPos " + s + " = new BlockPos(" + s1 + ");")));

            commandlistenerwrapper.sendMessage((new ChatComponentText("Position relative to " + s2 + ": ")).addSibling(ichatbasecomponent), false);
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
            c(commandlistenerwrapper, "Running " + collection.size() + " tests...");
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
        GameTestHarnessInfo gametestharnessinfo = new GameTestHarnessInfo(gametestharnesstestfunction, blockposition, worldserver);

        if (gametestharnesscollector != null) {
            gametestharnesscollector.a(gametestharnessinfo);
            gametestharnessinfo.a((GameTestHarnessListener) (new GameTestHarnessTestCommand.a(worldserver, gametestharnesscollector)));
        }

        a(gametestharnesstestfunction, worldserver);
        GameTestHarnessRunner.a(gametestharnessinfo, GameTestHarnessTicker.a);
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

    private static int a(CommandListenerWrapper commandlistenerwrapper, GameTestHarnessTestFunction gametestharnesstestfunction) {
        WorldServer worldserver = commandlistenerwrapper.getWorld();
        BlockPosition blockposition = new BlockPosition(commandlistenerwrapper.getPosition());
        BlockPosition blockposition1 = new BlockPosition(blockposition.getX(), commandlistenerwrapper.getWorld().getHighestBlockYAt(HeightMap.Type.WORLD_SURFACE, blockposition).getY(), blockposition.getZ() + 3);

        GameTestHarnessRunner.a(worldserver);
        a(gametestharnesstestfunction, worldserver);
        GameTestHarnessInfo gametestharnessinfo = new GameTestHarnessInfo(gametestharnesstestfunction, blockposition1, worldserver);

        GameTestHarnessRunner.a(gametestharnessinfo, GameTestHarnessTicker.a);
        return 1;
    }

    private static void a(GameTestHarnessTestFunction gametestharnesstestfunction, WorldServer worldserver) {
        Consumer<WorldServer> consumer = GameTestHarnessRegistry.c(gametestharnesstestfunction.e());

        if (consumer != null) {
            consumer.accept(worldserver);
        }

    }

    private static int c(CommandListenerWrapper commandlistenerwrapper) {
        GameTestHarnessRunner.a(commandlistenerwrapper.getWorld());
        a(commandlistenerwrapper, GameTestHarnessRegistry.a());
        return 1;
    }

    private static int b(CommandListenerWrapper commandlistenerwrapper, String s) {
        Collection<GameTestHarnessTestFunction> collection = GameTestHarnessRegistry.a(s);

        GameTestHarnessRunner.a(commandlistenerwrapper.getWorld());
        a(commandlistenerwrapper, collection);
        return 1;
    }

    private static void a(CommandListenerWrapper commandlistenerwrapper, Collection<GameTestHarnessTestFunction> collection) {
        BlockPosition blockposition = new BlockPosition(commandlistenerwrapper.getPosition());
        BlockPosition blockposition1 = new BlockPosition(blockposition.getX(), commandlistenerwrapper.getWorld().getHighestBlockYAt(HeightMap.Type.WORLD_SURFACE, blockposition).getY(), blockposition.getZ() + 3);
        WorldServer worldserver = commandlistenerwrapper.getWorld();

        c(commandlistenerwrapper, "Running " + collection.size() + " tests...");
        Collection<GameTestHarnessInfo> collection1 = GameTestHarnessRunner.b(collection, blockposition1, worldserver, GameTestHarnessTicker.a);
        GameTestHarnessCollector gametestharnesscollector = new GameTestHarnessCollector(collection1);

        gametestharnesscollector.a((GameTestHarnessListener) (new GameTestHarnessTestCommand.a(worldserver, gametestharnesscollector)));
    }

    private static void c(CommandListenerWrapper commandlistenerwrapper, String s) {
        commandlistenerwrapper.sendMessage(new ChatComponentText(s), false);
    }

    private static int d(CommandListenerWrapper commandlistenerwrapper, String s) {
        java.nio.file.Path java_nio_file_path = Paths.get(GameTestHarnessStructures.a);
        MinecraftKey minecraftkey = new MinecraftKey("minecraft", s);
        java.nio.file.Path java_nio_file_path1 = commandlistenerwrapper.getWorld().r().a(minecraftkey, ".nbt");
        java.nio.file.Path java_nio_file_path2 = DebugReportNBT.a(java_nio_file_path1, s, java_nio_file_path);

        if (java_nio_file_path2 == null) {
            c(commandlistenerwrapper, "Failed to export " + java_nio_file_path1);
            return 1;
        } else {
            try {
                Files.createDirectories(java_nio_file_path2.getParent());
            } catch (IOException ioexception) {
                c(commandlistenerwrapper, "Could not create folder " + java_nio_file_path2.getParent());
                ioexception.printStackTrace();
                return 1;
            }

            c(commandlistenerwrapper, "Exported to " + java_nio_file_path2.toAbsolutePath());
            return 0;
        }
    }

    private static int e(CommandListenerWrapper commandlistenerwrapper, String s) {
        java.nio.file.Path java_nio_file_path = Paths.get(GameTestHarnessStructures.a, s + ".snbt");
        MinecraftKey minecraftkey = new MinecraftKey("minecraft", s);
        java.nio.file.Path java_nio_file_path1 = commandlistenerwrapper.getWorld().r().a(minecraftkey, ".nbt");

        try {
            BufferedReader bufferedreader = Files.newBufferedReader(java_nio_file_path);
            String s1 = IOUtils.toString(bufferedreader);

            Files.createDirectories(java_nio_file_path1.getParent());
            OutputStream outputstream = Files.newOutputStream(java_nio_file_path1);

            NBTCompressedStreamTools.a(MojangsonParser.parse(s1), outputstream);
            c(commandlistenerwrapper, "Imported to " + java_nio_file_path1.toAbsolutePath());
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
            entityplayer.sendMessage(new ChatComponentText(enumchatformat + s));
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
