package net.minecraft.server;

import java.util.Iterator;
import java.util.Random;

public abstract class WorldGenFeatureCoral extends WorldGenerator<WorldGenFeatureEmptyConfiguration> {

    public WorldGenFeatureCoral() {}

    public boolean a(GeneratorAccess generatoraccess, ChunkGenerator<? extends GeneratorSettings> chunkgenerator, Random random, BlockPosition blockposition, WorldGenFeatureEmptyConfiguration worldgenfeatureemptyconfiguration) {
        IBlockData iblockdata = ((Block) TagsBlock.A.a(random)).getBlockData();

        return this.a(generatoraccess, random, blockposition, iblockdata);
    }

    protected abstract boolean a(GeneratorAccess generatoraccess, Random random, BlockPosition blockposition, IBlockData iblockdata);

    protected boolean b(GeneratorAccess generatoraccess, Random random, BlockPosition blockposition, IBlockData iblockdata) {
        BlockPosition blockposition1 = blockposition.up();
        IBlockData iblockdata1 = generatoraccess.getType(blockposition);

        if ((iblockdata1.getBlock() == Blocks.WATER || iblockdata1.a(TagsBlock.B)) && generatoraccess.getType(blockposition1).getBlock() == Blocks.WATER) {
            generatoraccess.setTypeAndData(blockposition, iblockdata, 3);
            if (random.nextFloat() < 0.25F) {
                generatoraccess.setTypeAndData(blockposition1, ((Block) TagsBlock.B.a(random)).getBlockData(), 2);
            } else if (random.nextFloat() < 0.05F) {
                generatoraccess.setTypeAndData(blockposition1, (IBlockData) Blocks.SEA_PICKLE.getBlockData().set(BlockSeaPickle.a, Integer.valueOf(random.nextInt(4) + 1)), 2);
            }

            Iterator iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

            while (iterator.hasNext()) {
                EnumDirection enumdirection = (EnumDirection) iterator.next();

                if (random.nextFloat() < 0.2F) {
                    BlockPosition blockposition2 = blockposition.shift(enumdirection);

                    if (generatoraccess.getType(blockposition2).getBlock() == Blocks.WATER) {
                        IBlockData iblockdata2 = (IBlockData) ((Block) TagsBlock.C.a(random)).getBlockData().set(BlockCoralFanWallAbstract.b, enumdirection);

                        generatoraccess.setTypeAndData(blockposition2, iblockdata2, 2);
                    }
                }
            }

            return true;
        } else {
            return false;
        }
    }
}
