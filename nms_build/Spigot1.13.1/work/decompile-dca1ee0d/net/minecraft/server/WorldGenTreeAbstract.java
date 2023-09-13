package net.minecraft.server;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
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
        HashSet hashset = Sets.newHashSet();
        boolean flag = this.a(hashset, generatoraccess, random, blockposition);
        ArrayList arraylist = Lists.newArrayList();
        boolean flag1 = true;

        for (int i = 0; i < 6; ++i) {
            arraylist.add(Sets.newHashSet());
        }

        BlockPosition.b blockposition_b = BlockPosition.b.r();
        Throwable throwable = null;

        try {
            if (flag && !hashset.isEmpty()) {
                Iterator iterator = Lists.newArrayList(hashset).iterator();

                while (iterator.hasNext()) {
                    BlockPosition blockposition1 = (BlockPosition) iterator.next();
                    EnumDirection[] aenumdirection = EnumDirection.values();
                    int j = aenumdirection.length;

                    for (int k = 0; k < j; ++k) {
                        EnumDirection enumdirection = aenumdirection[k];

                        blockposition_b.j(blockposition1).d(enumdirection);
                        if (!hashset.contains(blockposition_b)) {
                            IBlockData iblockdata = generatoraccess.getType(blockposition_b);

                            if (iblockdata.b(BlockProperties.ab)) {
                                ((Set) arraylist.get(0)).add(blockposition_b.h());
                                this.b(generatoraccess, blockposition_b, (IBlockData) iblockdata.set(BlockProperties.ab, Integer.valueOf(1)));
                            }
                        }
                    }
                }
            }

            int l = 1;

            while (l < 6) {
                Set set = (Set) arraylist.get(l - 1);
                Set set1 = (Set) arraylist.get(l);
                Iterator iterator1 = set.iterator();

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

                            blockposition_b.j(blockposition2).d(enumdirection1);
                            if (!set.contains(blockposition_b) && !set1.contains(blockposition_b)) {
                                IBlockData iblockdata1 = generatoraccess.getType(blockposition_b);

                                if (iblockdata1.b(BlockProperties.ab)) {
                                    int k1 = ((Integer) iblockdata1.get(BlockProperties.ab)).intValue();

                                    if (k1 > l + 1) {
                                        IBlockData iblockdata2 = (IBlockData) iblockdata1.set(BlockProperties.ab, Integer.valueOf(l + 1));

                                        this.b(generatoraccess, blockposition_b, iblockdata2);
                                        set1.add(blockposition_b.h());
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
            if (blockposition_b != null) {
                if (throwable != null) {
                    try {
                        blockposition_b.close();
                    } catch (Throwable throwable2) {
                        throwable.addSuppressed(throwable2);
                    }
                } else {
                    blockposition_b.close();
                }
            }

        }

        return flag;
    }

    protected abstract boolean a(Set<BlockPosition> set, GeneratorAccess generatoraccess, Random random, BlockPosition blockposition);
}
