package net.minecraft.gametest.framework;

import com.google.common.collect.Lists;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
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
import net.minecraft.core.IRegistry;
import net.minecraft.nbt.MojangsonParser;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.resources.MinecraftKey;
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

public class GameTestHarnessStructures {

    public static String a = "gameteststructures";

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

    public static AxisAlignedBB a(TileEntityStructure tileentitystructure) {
        BlockPosition blockposition = tileentitystructure.getPosition();
        BlockPosition blockposition1 = blockposition.a((BaseBlockPosition) tileentitystructure.j().b(-1, -1, -1));
        BlockPosition blockposition2 = DefinedStructure.a(blockposition1, EnumBlockMirror.NONE, tileentitystructure.l(), blockposition);

        return new AxisAlignedBB(blockposition, blockposition2);
    }

    public static StructureBoundingBox b(TileEntityStructure tileentitystructure) {
        BlockPosition blockposition = tileentitystructure.getPosition();
        BlockPosition blockposition1 = blockposition.a((BaseBlockPosition) tileentitystructure.j().b(-1, -1, -1));
        BlockPosition blockposition2 = DefinedStructure.a(blockposition1, EnumBlockMirror.NONE, tileentitystructure.l(), blockposition);

        return new StructureBoundingBox(blockposition, blockposition2);
    }

    public static void a(BlockPosition blockposition, BlockPosition blockposition1, EnumBlockRotation enumblockrotation, WorldServer worldserver) {
        BlockPosition blockposition2 = DefinedStructure.a(blockposition.a((BaseBlockPosition) blockposition1), EnumBlockMirror.NONE, enumblockrotation, blockposition);

        worldserver.setTypeUpdate(blockposition2, Blocks.COMMAND_BLOCK.getBlockData());
        TileEntityCommand tileentitycommand = (TileEntityCommand) worldserver.getTileEntity(blockposition2);

        tileentitycommand.getCommandBlock().setCommand("test runthis");
        BlockPosition blockposition3 = DefinedStructure.a(blockposition2.b(0, 0, -1), EnumBlockMirror.NONE, enumblockrotation, blockposition2);

        worldserver.setTypeUpdate(blockposition3, Blocks.STONE_BUTTON.getBlockData().a(enumblockrotation));
    }

    public static void a(String s, BlockPosition blockposition, BlockPosition blockposition1, EnumBlockRotation enumblockrotation, WorldServer worldserver) {
        StructureBoundingBox structureboundingbox = a(blockposition, blockposition1, enumblockrotation);

        a(structureboundingbox, blockposition.getY(), worldserver);
        worldserver.setTypeUpdate(blockposition, Blocks.STRUCTURE_BLOCK.getBlockData());
        TileEntityStructure tileentitystructure = (TileEntityStructure) worldserver.getTileEntity(blockposition);

        tileentitystructure.a(false);
        tileentitystructure.a(new MinecraftKey(s));
        tileentitystructure.c(blockposition1);
        tileentitystructure.setUsageMode(BlockPropertyStructureMode.SAVE);
        tileentitystructure.e(true);
    }

    public static TileEntityStructure a(String s, BlockPosition blockposition, EnumBlockRotation enumblockrotation, int i, WorldServer worldserver, boolean flag) {
        BlockPosition blockposition1 = a(s, worldserver).a();
        StructureBoundingBox structureboundingbox = a(blockposition, blockposition1, enumblockrotation);
        BlockPosition blockposition2;

        if (enumblockrotation == EnumBlockRotation.NONE) {
            blockposition2 = blockposition;
        } else if (enumblockrotation == EnumBlockRotation.CLOCKWISE_90) {
            blockposition2 = blockposition.b(blockposition1.getZ() - 1, 0, 0);
        } else if (enumblockrotation == EnumBlockRotation.CLOCKWISE_180) {
            blockposition2 = blockposition.b(blockposition1.getX() - 1, 0, blockposition1.getZ() - 1);
        } else {
            if (enumblockrotation != EnumBlockRotation.COUNTERCLOCKWISE_90) {
                throw new IllegalArgumentException("Invalid rotation: " + enumblockrotation);
            }

            blockposition2 = blockposition.b(0, 0, blockposition1.getX() - 1);
        }

        a(blockposition, worldserver);
        a(structureboundingbox, blockposition.getY(), worldserver);
        TileEntityStructure tileentitystructure = a(s, blockposition2, enumblockrotation, worldserver, flag);

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
        StructureBoundingBox structureboundingbox1 = new StructureBoundingBox(structureboundingbox.a - 2, structureboundingbox.b - 3, structureboundingbox.c - 3, structureboundingbox.d + 3, structureboundingbox.e + 20, structureboundingbox.f + 3);

        BlockPosition.a(structureboundingbox1).forEach((blockposition) -> {
            a(i, blockposition, worldserver);
        });
        worldserver.getBlockTickList().a(structureboundingbox1, true, false);
        worldserver.a(structureboundingbox1);
        AxisAlignedBB axisalignedbb = new AxisAlignedBB((double) structureboundingbox1.a, (double) structureboundingbox1.b, (double) structureboundingbox1.c, (double) structureboundingbox1.d, (double) structureboundingbox1.e, (double) structureboundingbox1.f);
        List<Entity> list = worldserver.a(Entity.class, axisalignedbb, (entity) -> {
            return !(entity instanceof EntityHuman);
        });

        list.forEach(Entity::die);
    }

    public static StructureBoundingBox a(BlockPosition blockposition, BlockPosition blockposition1, EnumBlockRotation enumblockrotation) {
        BlockPosition blockposition2 = blockposition.a((BaseBlockPosition) blockposition1).b(-1, -1, -1);
        BlockPosition blockposition3 = DefinedStructure.a(blockposition2, EnumBlockMirror.NONE, enumblockrotation, blockposition);
        StructureBoundingBox structureboundingbox = StructureBoundingBox.a(blockposition.getX(), blockposition.getY(), blockposition.getZ(), blockposition3.getX(), blockposition3.getY(), blockposition3.getZ());
        int i = Math.min(structureboundingbox.a, structureboundingbox.d);
        int j = Math.min(structureboundingbox.c, structureboundingbox.f);
        BlockPosition blockposition4 = new BlockPosition(blockposition.getX() - i, 0, blockposition.getZ() - j);

        structureboundingbox.a(blockposition4);
        return structureboundingbox;
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
        DefinedStructureManager definedstructuremanager = worldserver.n();
        DefinedStructure definedstructure = definedstructuremanager.b(new MinecraftKey(s));

        if (definedstructure != null) {
            return definedstructure;
        } else {
            String s1 = s + ".snbt";
            Path path = Paths.get(GameTestHarnessStructures.a, s1);
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
        tileentitystructure.b(enumblockrotation);
        tileentitystructure.a(false);
        tileentitystructure.a(new MinecraftKey(s));
        tileentitystructure.a(worldserver, flag);
        if (tileentitystructure.j() != BlockPosition.ZERO) {
            return tileentitystructure;
        } else {
            DefinedStructure definedstructure = a(s, worldserver);

            tileentitystructure.a(worldserver, flag, definedstructure);
            if (tileentitystructure.j() == BlockPosition.ZERO) {
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

            return MojangsonParser.parse(s);
        } catch (IOException ioexception) {
            return null;
        } catch (CommandSyntaxException commandsyntaxexception) {
            throw new RuntimeException("Error while trying to load structure " + path, commandsyntaxexception);
        }
    }

    private static void a(int i, BlockPosition blockposition, WorldServer worldserver) {
        IBlockData iblockdata = null;
        GeneratorSettingsFlat generatorsettingsflat = GeneratorSettingsFlat.a((IRegistry) worldserver.r().b(IRegistry.ay));

        if (generatorsettingsflat instanceof GeneratorSettingsFlat) {
            IBlockData[] aiblockdata = generatorsettingsflat.g();

            if (blockposition.getY() < i && blockposition.getY() <= aiblockdata.length) {
                iblockdata = aiblockdata[blockposition.getY() - 1];
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
