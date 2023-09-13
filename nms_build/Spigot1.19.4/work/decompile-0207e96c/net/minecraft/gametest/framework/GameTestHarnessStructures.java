package net.minecraft.gametest.framework;

import com.google.common.collect.Lists;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.logging.LogUtils;
import java.io.BufferedReader;
import java.io.IOException;
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
import net.minecraft.core.IRegistryCustom;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.CachedOutput;
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
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.phys.AxisAlignedBB;
import net.minecraft.world.phys.Vec3D;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;

public class GameTestHarnessStructures {

    private static final Logger LOGGER = LogUtils.getLogger();
    public static final String DEFAULT_TEST_STRUCTURES_DIR = "gameteststructures";
    public static String testStructuresDir = "gameteststructures";
    private static final int HOW_MANY_CHUNKS_TO_LOAD_IN_EACH_DIRECTION_OF_STRUCTURE = 4;

    public GameTestHarnessStructures() {}

    public static EnumBlockRotation getRotationForRotationSteps(int i) {
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

    public static int getRotationStepsForRotation(EnumBlockRotation enumblockrotation) {
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

    public static void main(String[] astring) throws IOException {
        DispenserRegistry.bootStrap();
        Files.walk(Paths.get(GameTestHarnessStructures.testStructuresDir)).filter((path) -> {
            return path.toString().endsWith(".snbt");
        }).forEach((path) -> {
            try {
                String s = Files.readString(path);
                NBTTagCompound nbttagcompound = GameProfileSerializer.snbtToStructure(s);
                NBTTagCompound nbttagcompound1 = StructureUpdater.update(path.toString(), nbttagcompound);

                DebugReportNBT.writeSnbt(CachedOutput.NO_CACHE, path, GameProfileSerializer.structureToSnbt(nbttagcompound1));
            } catch (IOException | CommandSyntaxException commandsyntaxexception) {
                GameTestHarnessStructures.LOGGER.error("Something went wrong upgrading: {}", path, commandsyntaxexception);
            }

        });
    }

    public static AxisAlignedBB getStructureBounds(TileEntityStructure tileentitystructure) {
        BlockPosition blockposition = tileentitystructure.getBlockPos();
        BlockPosition blockposition1 = blockposition.offset(tileentitystructure.getStructureSize().offset(-1, -1, -1));
        BlockPosition blockposition2 = DefinedStructure.transform(blockposition1, EnumBlockMirror.NONE, tileentitystructure.getRotation(), blockposition);

        return new AxisAlignedBB(blockposition, blockposition2);
    }

    public static StructureBoundingBox getStructureBoundingBox(TileEntityStructure tileentitystructure) {
        BlockPosition blockposition = tileentitystructure.getBlockPos();
        BlockPosition blockposition1 = blockposition.offset(tileentitystructure.getStructureSize().offset(-1, -1, -1));
        BlockPosition blockposition2 = DefinedStructure.transform(blockposition1, EnumBlockMirror.NONE, tileentitystructure.getRotation(), blockposition);

        return StructureBoundingBox.fromCorners(blockposition, blockposition2);
    }

    public static void addCommandBlockAndButtonToStartTest(BlockPosition blockposition, BlockPosition blockposition1, EnumBlockRotation enumblockrotation, WorldServer worldserver) {
        BlockPosition blockposition2 = DefinedStructure.transform(blockposition.offset(blockposition1), EnumBlockMirror.NONE, enumblockrotation, blockposition);

        worldserver.setBlockAndUpdate(blockposition2, Blocks.COMMAND_BLOCK.defaultBlockState());
        TileEntityCommand tileentitycommand = (TileEntityCommand) worldserver.getBlockEntity(blockposition2);

        tileentitycommand.getCommandBlock().setCommand("test runthis");
        BlockPosition blockposition3 = DefinedStructure.transform(blockposition2.offset(0, 0, -1), EnumBlockMirror.NONE, enumblockrotation, blockposition2);

        worldserver.setBlockAndUpdate(blockposition3, Blocks.STONE_BUTTON.defaultBlockState().rotate(enumblockrotation));
    }

    public static void createNewEmptyStructureBlock(String s, BlockPosition blockposition, BaseBlockPosition baseblockposition, EnumBlockRotation enumblockrotation, WorldServer worldserver) {
        StructureBoundingBox structureboundingbox = getStructureBoundingBox(blockposition, baseblockposition, enumblockrotation);

        clearSpaceForStructure(structureboundingbox, blockposition.getY(), worldserver);
        worldserver.setBlockAndUpdate(blockposition, Blocks.STRUCTURE_BLOCK.defaultBlockState());
        TileEntityStructure tileentitystructure = (TileEntityStructure) worldserver.getBlockEntity(blockposition);

        tileentitystructure.setIgnoreEntities(false);
        tileentitystructure.setStructureName(new MinecraftKey(s));
        tileentitystructure.setStructureSize(baseblockposition);
        tileentitystructure.setMode(BlockPropertyStructureMode.SAVE);
        tileentitystructure.setShowBoundingBox(true);
    }

    public static TileEntityStructure spawnStructure(String s, BlockPosition blockposition, EnumBlockRotation enumblockrotation, int i, WorldServer worldserver, boolean flag) {
        BaseBlockPosition baseblockposition = getStructureTemplate(s, worldserver).getSize();
        StructureBoundingBox structureboundingbox = getStructureBoundingBox(blockposition, baseblockposition, enumblockrotation);
        BlockPosition blockposition1;

        if (enumblockrotation == EnumBlockRotation.NONE) {
            blockposition1 = blockposition;
        } else if (enumblockrotation == EnumBlockRotation.CLOCKWISE_90) {
            blockposition1 = blockposition.offset(baseblockposition.getZ() - 1, 0, 0);
        } else if (enumblockrotation == EnumBlockRotation.CLOCKWISE_180) {
            blockposition1 = blockposition.offset(baseblockposition.getX() - 1, 0, baseblockposition.getZ() - 1);
        } else {
            if (enumblockrotation != EnumBlockRotation.COUNTERCLOCKWISE_90) {
                throw new IllegalArgumentException("Invalid rotation: " + enumblockrotation);
            }

            blockposition1 = blockposition.offset(0, 0, baseblockposition.getX() - 1);
        }

        forceLoadChunks(blockposition, worldserver);
        clearSpaceForStructure(structureboundingbox, blockposition.getY(), worldserver);
        TileEntityStructure tileentitystructure = createStructureBlock(s, blockposition1, enumblockrotation, worldserver, flag);

        worldserver.getBlockTicks().clearArea(structureboundingbox);
        worldserver.clearBlockEvents(structureboundingbox);
        return tileentitystructure;
    }

    private static void forceLoadChunks(BlockPosition blockposition, WorldServer worldserver) {
        ChunkCoordIntPair chunkcoordintpair = new ChunkCoordIntPair(blockposition);

        for (int i = -1; i < 4; ++i) {
            for (int j = -1; j < 4; ++j) {
                int k = chunkcoordintpair.x + i;
                int l = chunkcoordintpair.z + j;

                worldserver.setChunkForced(k, l, true);
            }
        }

    }

    public static void clearSpaceForStructure(StructureBoundingBox structureboundingbox, int i, WorldServer worldserver) {
        StructureBoundingBox structureboundingbox1 = new StructureBoundingBox(structureboundingbox.minX() - 2, structureboundingbox.minY() - 3, structureboundingbox.minZ() - 3, structureboundingbox.maxX() + 3, structureboundingbox.maxY() + 20, structureboundingbox.maxZ() + 3);

        BlockPosition.betweenClosedStream(structureboundingbox1).forEach((blockposition) -> {
            clearBlock(i, blockposition, worldserver);
        });
        worldserver.getBlockTicks().clearArea(structureboundingbox1);
        worldserver.clearBlockEvents(structureboundingbox1);
        AxisAlignedBB axisalignedbb = new AxisAlignedBB((double) structureboundingbox1.minX(), (double) structureboundingbox1.minY(), (double) structureboundingbox1.minZ(), (double) structureboundingbox1.maxX(), (double) structureboundingbox1.maxY(), (double) structureboundingbox1.maxZ());
        List<Entity> list = worldserver.getEntitiesOfClass(Entity.class, axisalignedbb, (entity) -> {
            return !(entity instanceof EntityHuman);
        });

        list.forEach(Entity::discard);
    }

    public static StructureBoundingBox getStructureBoundingBox(BlockPosition blockposition, BaseBlockPosition baseblockposition, EnumBlockRotation enumblockrotation) {
        BlockPosition blockposition1 = blockposition.offset(baseblockposition).offset(-1, -1, -1);
        BlockPosition blockposition2 = DefinedStructure.transform(blockposition1, EnumBlockMirror.NONE, enumblockrotation, blockposition);
        StructureBoundingBox structureboundingbox = StructureBoundingBox.fromCorners(blockposition, blockposition2);
        int i = Math.min(structureboundingbox.minX(), structureboundingbox.maxX());
        int j = Math.min(structureboundingbox.minZ(), structureboundingbox.maxZ());

        return structureboundingbox.move(blockposition.getX() - i, 0, blockposition.getZ() - j);
    }

    public static Optional<BlockPosition> findStructureBlockContainingPos(BlockPosition blockposition, int i, WorldServer worldserver) {
        return findStructureBlocks(blockposition, i, worldserver).stream().filter((blockposition1) -> {
            return doesStructureContain(blockposition1, blockposition, worldserver);
        }).findFirst();
    }

    @Nullable
    public static BlockPosition findNearestStructureBlock(BlockPosition blockposition, int i, WorldServer worldserver) {
        Comparator<BlockPosition> comparator = Comparator.comparingInt((blockposition1) -> {
            return blockposition1.distManhattan(blockposition);
        });
        Collection<BlockPosition> collection = findStructureBlocks(blockposition, i, worldserver);
        Optional<BlockPosition> optional = collection.stream().min(comparator);

        return (BlockPosition) optional.orElse((Object) null);
    }

    public static Collection<BlockPosition> findStructureBlocks(BlockPosition blockposition, int i, WorldServer worldserver) {
        Collection<BlockPosition> collection = Lists.newArrayList();
        AxisAlignedBB axisalignedbb = new AxisAlignedBB(blockposition);

        axisalignedbb = axisalignedbb.inflate((double) i);

        for (int j = (int) axisalignedbb.minX; j <= (int) axisalignedbb.maxX; ++j) {
            for (int k = (int) axisalignedbb.minY; k <= (int) axisalignedbb.maxY; ++k) {
                for (int l = (int) axisalignedbb.minZ; l <= (int) axisalignedbb.maxZ; ++l) {
                    BlockPosition blockposition1 = new BlockPosition(j, k, l);
                    IBlockData iblockdata = worldserver.getBlockState(blockposition1);

                    if (iblockdata.is(Blocks.STRUCTURE_BLOCK)) {
                        collection.add(blockposition1);
                    }
                }
            }
        }

        return collection;
    }

    private static DefinedStructure getStructureTemplate(String s, WorldServer worldserver) {
        StructureTemplateManager structuretemplatemanager = worldserver.getStructureManager();
        Optional<DefinedStructure> optional = structuretemplatemanager.get(new MinecraftKey(s));

        if (optional.isPresent()) {
            return (DefinedStructure) optional.get();
        } else {
            String s1 = s + ".snbt";
            Path path = Paths.get(GameTestHarnessStructures.testStructuresDir, s1);
            NBTTagCompound nbttagcompound = tryLoadStructure(path);

            if (nbttagcompound == null) {
                throw new RuntimeException("Could not find structure file " + path + ", and the structure is not available in the world structures either.");
            } else {
                return structuretemplatemanager.readStructure(nbttagcompound);
            }
        }
    }

    private static TileEntityStructure createStructureBlock(String s, BlockPosition blockposition, EnumBlockRotation enumblockrotation, WorldServer worldserver, boolean flag) {
        worldserver.setBlockAndUpdate(blockposition, Blocks.STRUCTURE_BLOCK.defaultBlockState());
        TileEntityStructure tileentitystructure = (TileEntityStructure) worldserver.getBlockEntity(blockposition);

        tileentitystructure.setMode(BlockPropertyStructureMode.LOAD);
        tileentitystructure.setRotation(enumblockrotation);
        tileentitystructure.setIgnoreEntities(false);
        tileentitystructure.setStructureName(new MinecraftKey(s));
        tileentitystructure.loadStructure(worldserver, flag);
        if (tileentitystructure.getStructureSize() != BaseBlockPosition.ZERO) {
            return tileentitystructure;
        } else {
            DefinedStructure definedstructure = getStructureTemplate(s, worldserver);

            tileentitystructure.loadStructure(worldserver, flag, definedstructure);
            if (tileentitystructure.getStructureSize() == BaseBlockPosition.ZERO) {
                throw new RuntimeException("Failed to load structure " + s);
            } else {
                return tileentitystructure;
            }
        }
    }

    @Nullable
    private static NBTTagCompound tryLoadStructure(Path path) {
        try {
            BufferedReader bufferedreader = Files.newBufferedReader(path);
            String s = IOUtils.toString(bufferedreader);

            return GameProfileSerializer.snbtToStructure(s);
        } catch (IOException ioexception) {
            return null;
        } catch (CommandSyntaxException commandsyntaxexception) {
            throw new RuntimeException("Error while trying to load structure " + path, commandsyntaxexception);
        }
    }

    private static void clearBlock(int i, BlockPosition blockposition, WorldServer worldserver) {
        IBlockData iblockdata = null;
        IRegistryCustom iregistrycustom = worldserver.registryAccess();
        GeneratorSettingsFlat generatorsettingsflat = GeneratorSettingsFlat.getDefault(iregistrycustom.lookupOrThrow(Registries.BIOME), iregistrycustom.lookupOrThrow(Registries.STRUCTURE_SET), iregistrycustom.lookupOrThrow(Registries.PLACED_FEATURE));
        List<IBlockData> list = generatorsettingsflat.getLayers();
        int j = blockposition.getY() - worldserver.getMinBuildHeight();

        if (blockposition.getY() < i && j > 0 && j <= list.size()) {
            iblockdata = (IBlockData) list.get(j - 1);
        }

        if (iblockdata == null) {
            iblockdata = Blocks.AIR.defaultBlockState();
        }

        ArgumentTileLocation argumenttilelocation = new ArgumentTileLocation(iblockdata, Collections.emptySet(), (NBTTagCompound) null);

        argumenttilelocation.place(worldserver, blockposition, 2);
        worldserver.blockUpdated(blockposition, iblockdata.getBlock());
    }

    private static boolean doesStructureContain(BlockPosition blockposition, BlockPosition blockposition1, WorldServer worldserver) {
        TileEntityStructure tileentitystructure = (TileEntityStructure) worldserver.getBlockEntity(blockposition);
        AxisAlignedBB axisalignedbb = getStructureBounds(tileentitystructure).inflate(1.0D);

        return axisalignedbb.contains(Vec3D.atCenterOf(blockposition1));
    }
}
