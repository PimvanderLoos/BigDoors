package net.minecraft.server;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.datafixers.Dynamic;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;

public abstract class WorldGenTreeAbstract<T extends WorldGenFeatureTreeConfiguration> extends WorldGenerator<T> {

    public WorldGenTreeAbstract(Function<Dynamic<?>, ? extends T> function) {
        super(function);
    }

    protected static boolean a(VirtualLevelReadable virtuallevelreadable, BlockPosition blockposition) {
        return virtuallevelreadable.a(blockposition, (iblockdata) -> {
            Block block = iblockdata.getBlock();

            return iblockdata.isAir() || iblockdata.a(TagsBlock.LEAVES) || b(block) || block.a(TagsBlock.LOGS) || block.a(TagsBlock.SAPLINGS) || block == Blocks.VINE;
        });
    }

    public static boolean b(VirtualLevelReadable virtuallevelreadable, BlockPosition blockposition) {
        return virtuallevelreadable.a(blockposition, IBlockData::isAir);
    }

    protected static boolean c(VirtualLevelReadable virtuallevelreadable, BlockPosition blockposition) {
        return virtuallevelreadable.a(blockposition, (iblockdata) -> {
            Block block = iblockdata.getBlock();

            return b(block) && block != Blocks.GRASS_BLOCK && block != Blocks.MYCELIUM;
        });
    }

    protected static boolean d(VirtualLevelReadable virtuallevelreadable, BlockPosition blockposition) {
        return virtuallevelreadable.a(blockposition, (iblockdata) -> {
            return iblockdata.getBlock() == Blocks.VINE;
        });
    }

    public static boolean e(VirtualLevelReadable virtuallevelreadable, BlockPosition blockposition) {
        return virtuallevelreadable.a(blockposition, (iblockdata) -> {
            return iblockdata.getBlock() == Blocks.WATER;
        });
    }

    public static boolean f(VirtualLevelReadable virtuallevelreadable, BlockPosition blockposition) {
        return virtuallevelreadable.a(blockposition, (iblockdata) -> {
            return iblockdata.isAir() || iblockdata.a(TagsBlock.LEAVES);
        });
    }

    public static boolean g(VirtualLevelReadable virtuallevelreadable, BlockPosition blockposition) {
        return virtuallevelreadable.a(blockposition, (iblockdata) -> {
            return b(iblockdata.getBlock());
        });
    }

    protected static boolean h(VirtualLevelReadable virtuallevelreadable, BlockPosition blockposition) {
        return virtuallevelreadable.a(blockposition, (iblockdata) -> {
            Block block = iblockdata.getBlock();

            return b(block) || block == Blocks.FARMLAND;
        });
    }

    public static boolean i(VirtualLevelReadable virtuallevelreadable, BlockPosition blockposition) {
        return virtuallevelreadable.a(blockposition, (iblockdata) -> {
            Material material = iblockdata.getMaterial();

            return material == Material.REPLACEABLE_PLANT;
        });
    }

    protected void a(VirtualLevelWritable virtuallevelwritable, BlockPosition blockposition) {
        if (!c(virtuallevelwritable, blockposition)) {
            this.a(virtuallevelwritable, blockposition, Blocks.DIRT.getBlockData());
        }

    }

    protected boolean a(VirtualLevelWritable virtuallevelwritable, Random random, BlockPosition blockposition, Set<BlockPosition> set, StructureBoundingBox structureboundingbox, WorldGenFeatureTreeConfiguration worldgenfeaturetreeconfiguration) {
        if (!f(virtuallevelwritable, blockposition) && !i(virtuallevelwritable, blockposition) && !e(virtuallevelwritable, blockposition)) {
            return false;
        } else {
            this.a((IWorldWriter) virtuallevelwritable, blockposition, worldgenfeaturetreeconfiguration.m.a(random, blockposition), structureboundingbox);
            set.add(blockposition.immutableCopy());
            return true;
        }
    }

    protected boolean b(VirtualLevelWritable virtuallevelwritable, Random random, BlockPosition blockposition, Set<BlockPosition> set, StructureBoundingBox structureboundingbox, WorldGenFeatureTreeConfiguration worldgenfeaturetreeconfiguration) {
        if (!f(virtuallevelwritable, blockposition) && !i(virtuallevelwritable, blockposition) && !e(virtuallevelwritable, blockposition)) {
            return false;
        } else {
            this.a((IWorldWriter) virtuallevelwritable, blockposition, worldgenfeaturetreeconfiguration.n.a(random, blockposition), structureboundingbox);
            set.add(blockposition.immutableCopy());
            return true;
        }
    }

    @Override
    protected void a(IWorldWriter iworldwriter, BlockPosition blockposition, IBlockData iblockdata) {
        this.b(iworldwriter, blockposition, iblockdata);
    }

    protected final void a(IWorldWriter iworldwriter, BlockPosition blockposition, IBlockData iblockdata, StructureBoundingBox structureboundingbox) {
        this.b(iworldwriter, blockposition, iblockdata);
        structureboundingbox.c(new StructureBoundingBox(blockposition, blockposition));
    }

    private void b(IWorldWriter iworldwriter, BlockPosition blockposition, IBlockData iblockdata) {
        iworldwriter.setTypeAndData(blockposition, iblockdata, 19);
    }

    public final boolean a(GeneratorAccess generatoraccess, ChunkGenerator<? extends GeneratorSettingsDefault> chunkgenerator, Random random, BlockPosition blockposition, T t0) {
        Set<BlockPosition> set = Sets.newHashSet();
        Set<BlockPosition> set1 = Sets.newHashSet();
        Set<BlockPosition> set2 = Sets.newHashSet();
        StructureBoundingBox structureboundingbox = StructureBoundingBox.a();
        boolean flag = this.a((VirtualLevelWritable) generatoraccess, random, blockposition, (Set) set, set1, structureboundingbox, t0);

        if (structureboundingbox.a <= structureboundingbox.d && flag && !set.isEmpty()) {
            if (!t0.o.isEmpty()) {
                List<BlockPosition> list = Lists.newArrayList(set);
                List<BlockPosition> list1 = Lists.newArrayList(set1);

                list.sort(Comparator.comparingInt(BaseBlockPosition::getY));
                list1.sort(Comparator.comparingInt(BaseBlockPosition::getY));
                t0.o.forEach((worldgenfeaturetree) -> {
                    worldgenfeaturetree.a(generatoraccess, random, list, list1, set2, structureboundingbox);
                });
            }

            VoxelShapeDiscrete voxelshapediscrete = this.a(generatoraccess, structureboundingbox, (Set) set, (Set) set2);

            DefinedStructure.a(generatoraccess, 3, voxelshapediscrete, structureboundingbox.a, structureboundingbox.b, structureboundingbox.c);
            return true;
        } else {
            return false;
        }
    }

    private VoxelShapeDiscrete a(GeneratorAccess generatoraccess, StructureBoundingBox structureboundingbox, Set<BlockPosition> set, Set<BlockPosition> set1) {
        List<Set<BlockPosition>> list = Lists.newArrayList();
        VoxelShapeBitSet voxelshapebitset = new VoxelShapeBitSet(structureboundingbox.c(), structureboundingbox.d(), structureboundingbox.e());
        boolean flag = true;

        for (int i = 0; i < 6; ++i) {
            list.add(Sets.newHashSet());
        }

        BlockPosition.PooledBlockPosition blockposition_pooledblockposition = BlockPosition.PooledBlockPosition.r();
        Throwable throwable = null;

        try {
            Iterator iterator = Lists.newArrayList(set1).iterator();

            BlockPosition blockposition;

            while (iterator.hasNext()) {
                blockposition = (BlockPosition) iterator.next();
                if (structureboundingbox.b((BaseBlockPosition) blockposition)) {
                    voxelshapebitset.a(blockposition.getX() - structureboundingbox.a, blockposition.getY() - structureboundingbox.b, blockposition.getZ() - structureboundingbox.c, true, true);
                }
            }

            iterator = Lists.newArrayList(set).iterator();

            while (iterator.hasNext()) {
                blockposition = (BlockPosition) iterator.next();
                if (structureboundingbox.b((BaseBlockPosition) blockposition)) {
                    voxelshapebitset.a(blockposition.getX() - structureboundingbox.a, blockposition.getY() - structureboundingbox.b, blockposition.getZ() - structureboundingbox.c, true, true);
                }

                EnumDirection[] aenumdirection = EnumDirection.values();
                int j = aenumdirection.length;

                for (int k = 0; k < j; ++k) {
                    EnumDirection enumdirection = aenumdirection[k];

                    blockposition_pooledblockposition.g(blockposition).c(enumdirection);
                    if (!set.contains(blockposition_pooledblockposition)) {
                        IBlockData iblockdata = generatoraccess.getType(blockposition_pooledblockposition);

                        if (iblockdata.b((IBlockState) BlockProperties.ah)) {
                            ((Set) list.get(0)).add(blockposition_pooledblockposition.immutableCopy());
                            this.b(generatoraccess, blockposition_pooledblockposition, (IBlockData) iblockdata.set(BlockProperties.ah, 1));
                            if (structureboundingbox.b((BaseBlockPosition) blockposition_pooledblockposition)) {
                                voxelshapebitset.a(blockposition_pooledblockposition.getX() - structureboundingbox.a, blockposition_pooledblockposition.getY() - structureboundingbox.b, blockposition_pooledblockposition.getZ() - structureboundingbox.c, true, true);
                            }
                        }
                    }
                }
            }

            for (int l = 1; l < 6; ++l) {
                Set<BlockPosition> set2 = (Set) list.get(l - 1);
                Set<BlockPosition> set3 = (Set) list.get(l);
                Iterator iterator1 = set2.iterator();

                while (iterator1.hasNext()) {
                    BlockPosition blockposition1 = (BlockPosition) iterator1.next();

                    if (structureboundingbox.b((BaseBlockPosition) blockposition1)) {
                        voxelshapebitset.a(blockposition1.getX() - structureboundingbox.a, blockposition1.getY() - structureboundingbox.b, blockposition1.getZ() - structureboundingbox.c, true, true);
                    }

                    EnumDirection[] aenumdirection1 = EnumDirection.values();
                    int i1 = aenumdirection1.length;

                    for (int j1 = 0; j1 < i1; ++j1) {
                        EnumDirection enumdirection1 = aenumdirection1[j1];

                        blockposition_pooledblockposition.g(blockposition1).c(enumdirection1);
                        if (!set2.contains(blockposition_pooledblockposition) && !set3.contains(blockposition_pooledblockposition)) {
                            IBlockData iblockdata1 = generatoraccess.getType(blockposition_pooledblockposition);

                            if (iblockdata1.b((IBlockState) BlockProperties.ah)) {
                                int k1 = (Integer) iblockdata1.get(BlockProperties.ah);

                                if (k1 > l + 1) {
                                    IBlockData iblockdata2 = (IBlockData) iblockdata1.set(BlockProperties.ah, l + 1);

                                    this.b(generatoraccess, blockposition_pooledblockposition, iblockdata2);
                                    if (structureboundingbox.b((BaseBlockPosition) blockposition_pooledblockposition)) {
                                        voxelshapebitset.a(blockposition_pooledblockposition.getX() - structureboundingbox.a, blockposition_pooledblockposition.getY() - structureboundingbox.b, blockposition_pooledblockposition.getZ() - structureboundingbox.c, true, true);
                                    }

                                    set3.add(blockposition_pooledblockposition.immutableCopy());
                                }
                            }
                        }
                    }
                }
            }
        } catch (Throwable throwable1) {
            throwable = throwable1;
            throw throwable1;
        } finally {
            if (blockposition_pooledblockposition != null) {
                if (throwable != null) {
                    try {
                        blockposition_pooledblockposition.close();
                    } catch (Throwable throwable2) {
                        throwable.addSuppressed(throwable2);
                    }
                } else {
                    blockposition_pooledblockposition.close();
                }
            }

        }

        return voxelshapebitset;
    }

    protected abstract boolean a(VirtualLevelWritable virtuallevelwritable, Random random, BlockPosition blockposition, Set<BlockPosition> set, Set<BlockPosition> set1, StructureBoundingBox structureboundingbox, T t0);
}
