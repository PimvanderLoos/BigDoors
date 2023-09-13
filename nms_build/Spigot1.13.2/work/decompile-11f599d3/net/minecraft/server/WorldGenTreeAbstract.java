package net.minecraft.server;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

public abstract class WorldGenTreeAbstract<T extends WorldGenFeatureConfiguration> extends WorldGenerator<T> {

    public WorldGenTreeAbstract(boolean flag) {
        super(flag);
    }

    protected boolean a(Block block) {
        IBlockData iblockdata = block.getBlockData();

        return iblockdata.isAir() || iblockdata.a(TagsBlock.LEAVES) || block == Blocks.GRASS_BLOCK || Block.d(block) || block.a(TagsBlock.LOGS) || block.a(TagsBlock.SAPLINGS) || block == Blocks.VINE;
    }

    protected void a(GeneratorAccess generatoraccess, BlockPosition blockposition) {
        if (!Block.d(generatoraccess.getType(blockposition).getBlock())) {
            this.a(generatoraccess, blockposition, Blocks.DIRT.getBlockData());
        }

    }

    protected void a(GeneratorAccess generatoraccess, BlockPosition blockposition, IBlockData iblockdata) {
        this.b(generatoraccess, blockposition, iblockdata);
    }

    protected final void a(Set<BlockPosition> set, GeneratorAccess generatoraccess, BlockPosition blockposition, IBlockData iblockdata) {
        this.b(generatoraccess, blockposition, iblockdata);
        if (TagsBlock.LOGS.isTagged(iblockdata.getBlock())) {
            set.add(blockposition.h());
        }

    }

    private void b(GeneratorAccess generatoraccess, BlockPosition blockposition, IBlockData iblockdata) {
        if (this.aG) {
            generatoraccess.setTypeAndData(blockposition, iblockdata, 19);
        } else {
            generatoraccess.setTypeAndData(blockposition, iblockdata, 18);
        }

    }

    public final boolean generate(GeneratorAccess generatoraccess, ChunkGenerator<? extends GeneratorSettings> chunkgenerator, Random random, BlockPosition blockposition, T t0) {
        Set<BlockPosition> set = Sets.newHashSet();
        boolean flag = this.a(set, generatoraccess, random, blockposition);
        List<Set<BlockPosition>> list = Lists.newArrayList();
        boolean flag1 = true;

        for (int i = 0; i < 6; ++i) {
            list.add(Sets.newHashSet());
        }

        BlockPosition.PooledBlockPosition blockposition_pooledblockposition = BlockPosition.PooledBlockPosition.r();
        Throwable throwable = null;

        try {
            if (flag && !set.isEmpty()) {
                Iterator iterator = Lists.newArrayList(set).iterator();

                while (iterator.hasNext()) {
                    BlockPosition blockposition1 = (BlockPosition) iterator.next();
                    EnumDirection[] aenumdirection = EnumDirection.values();
                    int j = aenumdirection.length;

                    for (int k = 0; k < j; ++k) {
                        EnumDirection enumdirection = aenumdirection[k];

                        blockposition_pooledblockposition.g(blockposition1).c(enumdirection);
                        if (!set.contains(blockposition_pooledblockposition)) {
                            IBlockData iblockdata = generatoraccess.getType(blockposition_pooledblockposition);

                            if (iblockdata.b(BlockProperties.ab)) {
                                ((Set) list.get(0)).add(blockposition_pooledblockposition.h());
                                this.b(generatoraccess, blockposition_pooledblockposition, (IBlockData) iblockdata.set(BlockProperties.ab, 1));
                            }
                        }
                    }
                }
            }

            int l = 1;

            while (l < 6) {
                Set<BlockPosition> set1 = (Set) list.get(l - 1);
                Set<BlockPosition> set2 = (Set) list.get(l);
                Iterator iterator1 = set1.iterator();

                label176:
                while (true) {
                    if (iterator1.hasNext()) {
                        BlockPosition blockposition2 = (BlockPosition) iterator1.next();
                        EnumDirection[] aenumdirection1 = EnumDirection.values();
                        int i1 = aenumdirection1.length;
                        int j1 = 0;

                        while (true) {
                            if (j1 >= i1) {
                                continue label176;
                            }

                            EnumDirection enumdirection1 = aenumdirection1[j1];

                            blockposition_pooledblockposition.g(blockposition2).c(enumdirection1);
                            if (!set1.contains(blockposition_pooledblockposition) && !set2.contains(blockposition_pooledblockposition)) {
                                IBlockData iblockdata1 = generatoraccess.getType(blockposition_pooledblockposition);

                                if (iblockdata1.b(BlockProperties.ab)) {
                                    int k1 = (Integer) iblockdata1.get(BlockProperties.ab);

                                    if (k1 > l + 1) {
                                        IBlockData iblockdata2 = (IBlockData) iblockdata1.set(BlockProperties.ab, l + 1);

                                        this.b(generatoraccess, blockposition_pooledblockposition, iblockdata2);
                                        set2.add(blockposition_pooledblockposition.h());
                                    }
                                }
                            }

                            ++j1;
                        }
                    }

                    ++l;
                    break;
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

        return flag;
    }

    protected abstract boolean a(Set<BlockPosition> set, GeneratorAccess generatoraccess, Random random, BlockPosition blockposition);
}
