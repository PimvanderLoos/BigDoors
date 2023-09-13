package net.minecraft.gametest.framework;

import com.google.common.collect.Lists;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.commands.arguments.blocks.ArgumentTileLocation;
import net.minecraft.core.BaseBlockPosition;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.IRegistry;
import net.minecraft.data.structures.DebugReportNBT;
import net.minecraft.data.structures.StructureUpdater;
import net.minecraft.nbt.GameProfileSerializer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.DispenserRegistry;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EnumBlockMirror;
import net.minecraft.world.level.block.EnumBlockRotation;
import net.minecraft.world.level.block.entity.TileEntityCommand;
import net.minecraft.world.level.block.entity.TileEntityStructure;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockPropertyStructureMode;
import net.minecraft.world.level.levelgen.flat.GeneratorSettingsFlat;
import net.minecraft.world.level.levelgen.structure.StructureBoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructure;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructureManager;
import net.minecraft.world.phys.AxisAlignedBB;
import net.minecraft.world.phys.Vec3D;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GameTestHarnessStructures {

    private static final Logger LOGGER = LogManager.getLogger();
    public static final String DEFAULT_TEST_STRUCTURES_DIR = "gameteststructures";
    public static String testStructuresDir = "gameteststructures";
    private static final int HOW_MANY_CHUNKS_TO_LOAD_IN_EACH_DIRECTION_OF_STRUCTURE = 4;

    public GameTestHarnessStructures() {}

    public static EnumBlockRotation a(int i) {
        switch (i) {
            case 0:
                return EnumBlockRotation.NONE;
            case 1:
                return EnumBlockRotation.CLOCKWISE_90;
            case 2:
                return EnumBlockRotation.CLOCKWISE_180;
            case 3:
                return EnumBlockRotation.COUNTERCLOCKWISE_90;
            default:
                throw new IllegalArgumentException("rotationSteps must be a value from 0-3. Got value " + i);
        }
    }

    public static int a(EnumBlockRotation enumblockrotation) {
        switch (enumblockrotation) {
            case NONE:
                return 0;
            case CLOCKWISE_90:
                return 1;
            case CLOCKWISE_180:
                return 2;
            case COUNTERCLOCKWISE_90:
                return 3;
            default:
                throw new IllegalArgumentException("Unknown rotation value, don't know how many steps it represents: " + enumblockrotation);
        }
    }

    public static void a(String[] astring) throws IOException {
        DispenserRegistry.init();
        Files.walk(Paths.get(GameTestHarnessStructures.testStructuresDir)).filter((path) -> {
            return path.toString().endsWith(".snbt");
        }).forEach((path) -> {
            try {
                String s = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
                NBTTagCompound nbttagcompound = GameProfileSerializer.a(s);
                NBTTagCompound nbttagcompound1 = StructureUpdater.b(path.toString(), nbttagcompound);

                DebugReportNBT.a(path, GameProfileSerializer.d(nbttagcompound1));
            } catch (IOException | CommandSyntaxException commandsyntaxexception) {
                GameTestHarnessStructures.LOGGER.error("Something went wrong upgrading: {}", path, commandsyntaxexception);
            }

        });
    }

    public static AxisAlignedBB a(TileEntityStructure tileentitystructure) {
        BlockPosition blockposition = tileentitystructure.getPosition();
        BlockPosition blockposition1 = blockposition.f(tileentitystructure.i().c(-1, -1, -1));
        BlockPosition blockposition2 = DefinedStructure.a(blockposition1, EnumBlockMirror.NONE, tileentitystructure.s(), blockposition);

        return new AxisAlignedBB(blockposition, blockposition2);
    }

    public static StructureBoundingBox b(TileEntityStructure tileentitystructure) {
        BlockPosition blockposition = tileentitystructure.getPosition();
        BlockPosition blockposition1 = blockposition.f(tileentitystructure.i().c(-1, -1, -1));
        BlockPosition blockposition2 = DefinedStructure.a(blockposition1, EnumBlockMirror.NONE, tileentitystructure.s(), blockposition);

        return StructureBoundingBox.a(blockposition, blockposition2);
    }

    public static void a(BlockPosition blockposition, BlockPosition blockposition1, EnumBlockRotation enumblockrotation, WorldServer worldserver) {
        BlockPosition blockposition2 = DefinedStructure.a(blockposition.f(blockposition1), EnumBlockMirror.NONE, enumblockrotation, blockposition);

        worldserver.setTypeUpdate(blockposition2, Blocks.COMMAND_BLOCK.getBlockData());
        TileEntityCommand tileentitycommand = (TileEntityCommand) worldserver.getTileEntity(blockposition2);

        tileentitycommand.getCommandBlock().setCommand("test runthis");
        BlockPosition blockposition3 = DefinedStructure.a(blockposition2.c(0, 0, -1), EnumBlockMirror.NONE, enumblockrotation, blockposition2);

        worldserver.setTypeUpdate(blockposition3, Blocks.STONE_BUTTON.getBlockData().a(enumblockrotation));
    }

    public static void a(String s, BlockPosition blockposition, BaseBlockPosition baseblockposition, EnumBlockRotation enumblockrotation, WorldServer worldserver) {
        StructureBoundingBox structureboundingbox = a(blockposition, baseblockposition, enumblockrotation);

        a(structureboundingbox, blockposition.getY(), worldserver);
        worldserver.setTypeUpdate(blockposition, Blocks.STRUCTURE_BLOCK.getBlockData());
        TileEntityStructure tileentitystructure = (TileEntityStructure) worldserver.getTileEntity(blockposition);

        tileentitystructure.a(false);
        tileentitystructure.a(new MinecraftKey(s));
        tileentitystructure.a(baseblockposition);
        tileentitystructure.setUsageMode(BlockPropertyStructureMode.SAVE);
        tileentitystructure.e(true);
    }

    public static TileEntityStructure a(String s, BlockPosition blockposition, EnumBlockRotation enumblockrotation, int i, WorldServer worldserver, boolean flag) {
        BaseBlockPosition baseblockposition = a(s, worldserver).a();
        StructureBoundingBox structureboundingbox = a(blockposition, baseblockposition, enumblockrotation);
        BlockPosition blockposition1;

        if (enumblockrotation == EnumBlockRotation.NONE) {
            blockposition1 = blockposition;
        } else if (enumblockrotation == EnumBlockRotation.CLOCKWISE_90) {
            blockposition1 = blockposition.c(baseblockposition.getZ() - 1, 0, 0);
        } else if (enumblockrotation == EnumBlockRotation.CLOCKWISE_180) {
            blockposition1 = blockposition.c(baseblockposition.getX() - 1, 0, baseblockposition.getZ() - 1);
        } else {
            if (enumblockrotation != EnumBlockRotation.COUNTERCLOCKWISE_90) {
                throw new IllegalArgumentException("Invalid rotation: " + enumblockrotation);
            }

            blockposition1 = blockposition.c(0, 0, baseblockposition.getX() - 1);
        }

        a(blockposition, worldserver);
        a(structureboundingbox, blockposition.getY(), worldserver);
        TileEntityStructure tileentitystructure = a(s, blockposition1, enumblockrotation, worldserver, flag);

        worldserver.getBlockTickList().a(structureboundingbox, true, false);
        worldserver.a(structureboundingbox);
        return tileentitystructure;
    }

    private static void a(BlockPosition blockposition, WorldServer worldserver) {
        ChunkCoordIntPair chunkcoordintpair = new ChunkCoordIntPair(blockposition);

        for (int i = -1; i < 4; ++i) {
            for (int j = -1; j < 4; ++j) {
                int k = chunkcoordintpair.x + i;
                int l = chunkcoordintpair.z + j;

                worldserver.setForceLoaded(k, l, true);
            }
        }

    }

    public static void a(StructureBoundingBox structureboundingbox, int i, WorldServer worldserver) {
        StructureBoundingBox structureboundingbox1 = new StructureBoundingBox(structureboundingbox.g() - 2, structureboundingbox.h() - 3, structureboundingbox.i() - 3, structureboundingbox.j() + 3, structureboundingbox.k() + 20, structureboundingbox.l() + 3);

        BlockPosition.a(structureboundingbox1).forEach((blockposition) -> {
            a(i, blockposition, worldserver);
        });
        worldserver.getBlockTickList().a(structureboundingbox1, true, false);
        worldserver.a(structureboundingbox1);
        AxisAlignedBB axisalignedbb = new AxisAlignedBB((double) structureboundingbox1.g(), (double) structureboundingbox1.h(), (double) structureboundingbox1.i(), (double) structureboundingbox1.j(), (double) structureboundingbox1.k(), (double) structureboundingbox1.l());
        List<Entity> list = worldserver.a(Entity.class, axisalignedbb, (entity) -> {
            return !(entity instanceof EntityHuman);
        });

        list.forEach(Entity::die);
    }

    public static StructureBoundingBox a(BlockPosition blockposition, BaseBlockPosition baseblockposition, EnumBlockRotation enumblockrotation) {
        BlockPosition blockposition1 = blockposition.f(baseblockposition).c(-1, -1, -1);
        BlockPosition blockposition2 = DefinedStructure.a(blockposition1, EnumBlockMirror.NONE, enumblockrotation, blockposition);
        StructureBoundingBox structureboundingbox = StructureBoundingBox.a(blockposition, blockposition2);
        int i = Math.min(structureboundingbox.g(), structureboundingbox.j());
        int j = Math.min(structureboundingbox.i(), structureboundingbox.l());

        return structureboundingbox.a(blockposition.getX() - i, 0, blockposition.getZ() - j);
    }

    public static Optional<BlockPosition> a(BlockPosition blockposition, int i, WorldServer worldserver) {
        return c(blockposition, i, worldserver).stream().filter((blockposition1) -> {
            return a(blockposition1, blockposition, worldserver);
        }).findFirst();
    }

    @Nullable
    public static BlockPosition b(BlockPosition blockposition, int i, WorldServer worldserver) {
        Comparator<BlockPosition> comparator = Comparator.comparingInt((blockposition1) -> {
            return blockposition1.k(blockposition);
        });
        Collection<BlockPosition> collection = c(blockposition, i, worldserver);
        Optional<BlockPosition> optional = collection.stream().min(comparator);

        return (BlockPosition) optional.orElse((Object) null);
    }

    public static Collection<BlockPosition> c(BlockPosition blockposition, int i, WorldServer worldserver) {
        Collection<BlockPosition> collection = Lists.newArrayList();
        AxisAlignedBB axisalignedbb = new AxisAlignedBB(blockposition);

        axisalignedbb = axisalignedbb.g((double) i);

        for (int j = (int) axisalignedbb.minX; j <= (int) axisalignedbb.maxX; ++j) {
            for (int k = (int) axisalignedbb.minY; k <= (int) axisalignedbb.maxY; ++k) {
                for (int l = (int) axisalignedbb.minZ; l <= (int) axisalignedbb.maxZ; ++l) {
                    BlockPosition blockposition1 = new BlockPosition(j, k, l);
                    IBlockData iblockdata = worldserver.getType(blockposition1);

                    if (iblockdata.a(Blocks.STRUCTURE_BLOCK)) {
                        collection.add(blockposition1);
                    }
                }
            }
        }

        return collection;
    }

    private static DefinedStructure a(String s, WorldServer worldserver) {
        DefinedStructureManager definedstructuremanager = worldserver.p();
        Optional<DefinedStructure> optional = definedstructuremanager.b(new MinecraftKey(s));

        if (optional.isPresent()) {
            return (DefinedStructure) optional.get();
        } else {
            String s1 = s + ".snbt";
            Path path = Paths.get(GameTestHarnessStructures.testStructuresDir, s1);
            NBTTagCompound nbttagcompound = a(path);

            if (nbttagcompound == null) {
                throw new RuntimeException("Could not find structure file " + path + ", and the structure is not available in the world structures either.");
            } else {
                return definedstructuremanager.a(nbttagcompound);
            }
        }
    }

    private static TileEntityStructure a(String s, BlockPosition blockposition, EnumBlockRotation enumblockrotation, WorldServer worldserver, boolean flag) {
        worldserver.setTypeUpdate(blockposition, Blocks.STRUCTURE_BLOCK.getBlockData());
        TileEntityStructure tileentitystructure = (TileEntityStructure) worldserver.getTileEntity(blockposition);

        tileentitystructure.setUsageMode(BlockPropertyStructureMode.LOAD);
        tileentitystructure.a(enumblockrotation);
        tileentitystructure.a(false);
        tileentitystructure.a(new MinecraftKey(s));
        tileentitystructure.a(worldserver, flag);
        if (tileentitystructure.i() != BaseBlockPosition.ZERO) {
            return tileentitystructure;
        } else {
            DefinedStructure definedstructure = a(s, worldserver);

            tileentitystructure.a(worldserver, flag, definedstructure);
            if (tileentitystructure.i() == BaseBlockPosition.ZERO) {
                throw new RuntimeException("Failed to load structure " + s);
            } else {
                return tileentitystructure;
            }
        }
    }

    @Nullable
    private static NBTTagCompound a(Path path) {
        try {
            BufferedReader bufferedreader = Files.newBufferedReader(path);
            String s = IOUtils.toString(bufferedreader);

            return GameProfileSerializer.a(s);
        } catch (IOException ioexception) {
            return null;
        } catch (CommandSyntaxException commandsyntaxexception) {
            throw new RuntimeException("Error while trying to load structure " + path, commandsyntaxexception);
        }
    }

    private static void a(int i, BlockPosition blockposition, WorldServer worldserver) {
        IBlockData iblockdata = null;
        GeneratorSettingsFlat generatorsettingsflat = GeneratorSettingsFlat.a(worldserver.t().d(IRegistry.BIOME_REGISTRY));

        if (generatorsettingsflat instanceof GeneratorSettingsFlat) {
            List<IBlockData> list = generatorsettingsflat.g();
            int j = blockposition.getY() - worldserver.getMinBuildHeight();

            if (blockposition.getY() < i && j > 0 && j <= list.size()) {
                iblockdata = (IBlockData) list.get(j - 1);
            }
        } else if (blockposition.getY() == i - 1) {
            iblockdata = worldserver.getBiome(blockposition).e().e().a();
        } else if (blockposition.getY() < i - 1) {
            iblockdata = worldserver.getBiome(blockposition).e().e().b();
        }

        if (iblockdata == null) {
            iblockdata = Blocks.AIR.getBlockData();
        }

        ArgumentTileLocation argumenttilelocation = new ArgumentTileLocation(iblockdata, Collections.emptySet(), (NBTTagCompound) null);

        argumenttilelocation.a(worldserver, blockposition, 2);
        worldserver.update(blockposition, iblockdata.getBlock());
    }

    private static boolean a(BlockPosition blockposition, BlockPosition blockposition1, WorldServer worldserver) {
        TileEntityStructure tileentitystructure = (TileEntityStructure) worldserver.getTileEntity(blockposition);
        AxisAlignedBB axisalignedbb = a(tileentitystructure).g(1.0D);

        return axisalignedbb.d(Vec3D.a((BaseBlockPosition) blockposition1));
    }
}
