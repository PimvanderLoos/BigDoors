package net.minecraft.server;

import com.google.common.collect.Lists;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import org.apache.commons.io.IOUtils;

public class GameTestHarnessStructures {

    public static String a = "gameteststructures";

    public static AxisAlignedBB a(TileEntityStructure tileentitystructure) {
        BlockPosition blockposition = tileentitystructure.getPosition().a((BaseBlockPosition) tileentitystructure.h());

        return new AxisAlignedBB(blockposition, blockposition.a((BaseBlockPosition) tileentitystructure.j()));
    }

    public static void a(BlockPosition blockposition, WorldServer worldserver) {
        worldserver.setTypeUpdate(blockposition, Blocks.COMMAND_BLOCK.getBlockData());
        TileEntityCommand tileentitycommand = (TileEntityCommand) worldserver.getTileEntity(blockposition);

        tileentitycommand.getCommandBlock().setCommand("test runthis");
        worldserver.setTypeUpdate(blockposition.b(0, 0, -1), Blocks.STONE_BUTTON.getBlockData());
    }

    public static void a(String s, BlockPosition blockposition, BlockPosition blockposition1, int i, WorldServer worldserver) {
        StructureBoundingBox structureboundingbox = a(blockposition, blockposition1, i);

        a(structureboundingbox, blockposition.getY(), worldserver);
        worldserver.setTypeUpdate(blockposition, Blocks.STRUCTURE_BLOCK.getBlockData());
        TileEntityStructure tileentitystructure = (TileEntityStructure) worldserver.getTileEntity(blockposition);

        tileentitystructure.a(false);
        tileentitystructure.a(new MinecraftKey(s));
        tileentitystructure.c(blockposition1);
        tileentitystructure.setUsageMode(BlockPropertyStructureMode.SAVE);
        tileentitystructure.f(true);
    }

    public static TileEntityStructure a(String s, BlockPosition blockposition, int i, WorldServer worldserver, boolean flag) {
        StructureBoundingBox structureboundingbox = a(blockposition, a(s, worldserver).a(), i);

        b(blockposition, worldserver);
        a(structureboundingbox, blockposition.getY(), worldserver);
        TileEntityStructure tileentitystructure = a(s, blockposition, worldserver, flag);

        worldserver.getBlockTickList().a(structureboundingbox, true, false);
        worldserver.a(structureboundingbox);
        return tileentitystructure;
    }

    private static void b(BlockPosition blockposition, WorldServer worldserver) {
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
        BlockPosition.a(structureboundingbox).forEach((blockposition) -> {
            a(i, blockposition, worldserver);
        });
        worldserver.getBlockTickList().a(structureboundingbox, true, false);
        worldserver.a(structureboundingbox);
        AxisAlignedBB axisalignedbb = new AxisAlignedBB((double) structureboundingbox.a, (double) structureboundingbox.b, (double) structureboundingbox.c, (double) structureboundingbox.d, (double) structureboundingbox.e, (double) structureboundingbox.f);
        List<Entity> list = worldserver.a(Entity.class, axisalignedbb, (entity) -> {
            return !(entity instanceof EntityHuman);
        });

        list.forEach(Entity::die);
    }

    public static StructureBoundingBox a(BlockPosition blockposition, BlockPosition blockposition1, int i) {
        BlockPosition blockposition2 = blockposition.b(-i, -3, -i);
        BlockPosition blockposition3 = blockposition.a((BaseBlockPosition) blockposition1).b(i - 1, 30, i - 1);

        return StructureBoundingBox.a(blockposition2.getX(), blockposition2.getY(), blockposition2.getZ(), blockposition3.getX(), blockposition3.getY(), blockposition3.getZ());
    }

    public static Optional<BlockPosition> a(BlockPosition blockposition, int i, WorldServer worldserver) {
        return c(blockposition, i, worldserver).stream().filter((blockposition1) -> {
            return a(blockposition1, blockposition, worldserver);
        }).findFirst();
    }

    @Nullable
    public static BlockPosition b(BlockPosition blockposition, int i, WorldServer worldserver) {
        Comparator<BlockPosition> comparator = Comparator.comparingInt((blockposition1) -> {
            return blockposition1.n(blockposition);
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

                    if (iblockdata.getBlock() == Blocks.STRUCTURE_BLOCK) {
                        collection.add(blockposition1);
                    }
                }
            }
        }

        return collection;
    }

    private static DefinedStructure a(String s, WorldServer worldserver) {
        DefinedStructureManager definedstructuremanager = worldserver.r();
        DefinedStructure definedstructure = definedstructuremanager.b(new MinecraftKey(s));

        if (definedstructure != null) {
            return definedstructure;
        } else {
            String s1 = s + ".snbt";
            java.nio.file.Path java_nio_file_path = Paths.get(GameTestHarnessStructures.a, s1);
            NBTTagCompound nbttagcompound = a(java_nio_file_path);

            if (nbttagcompound == null) {
                throw new RuntimeException("Could not find structure file " + java_nio_file_path + ", and the structure is not available in the world structures either.");
            } else {
                return definedstructuremanager.a(nbttagcompound);
            }
        }
    }

    private static TileEntityStructure a(String s, BlockPosition blockposition, WorldServer worldserver, boolean flag) {
        worldserver.setTypeUpdate(blockposition, Blocks.STRUCTURE_BLOCK.getBlockData());
        TileEntityStructure tileentitystructure = (TileEntityStructure) worldserver.getTileEntity(blockposition);

        tileentitystructure.setUsageMode(BlockPropertyStructureMode.LOAD);
        tileentitystructure.a(false);
        tileentitystructure.a(new MinecraftKey(s));
        tileentitystructure.c(flag);
        if (tileentitystructure.j() != BlockPosition.ZERO) {
            return tileentitystructure;
        } else {
            DefinedStructure definedstructure = a(s, worldserver);

            tileentitystructure.a(flag, definedstructure);
            if (tileentitystructure.j() == BlockPosition.ZERO) {
                throw new RuntimeException("Failed to load structure " + s);
            } else {
                return tileentitystructure;
            }
        }
    }

    @Nullable
    private static NBTTagCompound a(java.nio.file.Path java_nio_file_path) {
        try {
            BufferedReader bufferedreader = Files.newBufferedReader(java_nio_file_path);
            String s = IOUtils.toString(bufferedreader);

            return MojangsonParser.parse(s);
        } catch (IOException ioexception) {
            return null;
        } catch (CommandSyntaxException commandsyntaxexception) {
            throw new RuntimeException("Error while trying to load structure " + java_nio_file_path, commandsyntaxexception);
        }
    }

    private static void a(int i, BlockPosition blockposition, WorldServer worldserver) {
        GeneratorSettingsDefault generatorsettingsdefault = worldserver.getChunkProvider().getChunkGenerator().getSettings();
        IBlockData iblockdata;

        if (generatorsettingsdefault instanceof GeneratorSettingsFlat) {
            IBlockData[] aiblockdata = ((GeneratorSettingsFlat) generatorsettingsdefault).C();

            if (blockposition.getY() < i) {
                iblockdata = aiblockdata[blockposition.getY() - 1];
            } else {
                iblockdata = Blocks.AIR.getBlockData();
            }
        } else if (blockposition.getY() == i - 1) {
            iblockdata = worldserver.getBiome(blockposition).s().a();
        } else if (blockposition.getY() < i - 1) {
            iblockdata = worldserver.getBiome(blockposition).s().b();
        } else {
            iblockdata = Blocks.AIR.getBlockData();
        }

        ArgumentTileLocation argumenttilelocation = new ArgumentTileLocation(iblockdata, Collections.emptySet(), (NBTTagCompound) null);

        argumenttilelocation.a(worldserver, blockposition, 2);
        worldserver.update(blockposition, iblockdata.getBlock());
    }

    private static boolean a(BlockPosition blockposition, BlockPosition blockposition1, WorldServer worldserver) {
        TileEntityStructure tileentitystructure = (TileEntityStructure) worldserver.getTileEntity(blockposition);
        AxisAlignedBB axisalignedbb = a(tileentitystructure);

        return axisalignedbb.c(new Vec3D(blockposition1));
    }
}
