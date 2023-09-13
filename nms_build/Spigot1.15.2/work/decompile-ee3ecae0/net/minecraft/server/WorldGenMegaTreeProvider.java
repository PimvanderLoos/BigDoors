package net.minecraft.server;

import java.util.Random;
import javax.annotation.Nullable;

public abstract class WorldGenMegaTreeProvider extends WorldGenTreeProvider {

    public WorldGenMegaTreeProvider() {}

    @Override
    public boolean a(GeneratorAccess generatoraccess, ChunkGenerator<?> chunkgenerator, BlockPosition blockposition, IBlockData iblockdata, Random random) {
        for (int i = 0; i >= -1; --i) {
            for (int j = 0; j >= -1; --j) {
                if (a(iblockdata, generatoraccess, blockposition, i, j)) {
                    return this.a(generatoraccess, chunkgenerator, blockposition, iblockdata, random, i, j);
                }
            }
        }

        return super.a(generatoraccess, chunkgenerator, blockposition, iblockdata, random);
    }

    @Nullable
    protected abstract WorldGenFeatureConfigured<WorldGenMegaTreeConfiguration, ?> a(Random random);

    public boolean a(GeneratorAccess generatoraccess, ChunkGenerator<?> chunkgenerator, BlockPosition blockposition, IBlockData iblockdata, Random random, int i, int j) {
        WorldGenFeatureConfigured<WorldGenMegaTreeConfiguration, ?> worldgenfeatureconfigured = this.a(random);

        if (worldgenfeatureconfigured == null) {
            return false;
        } else {
            IBlockData iblockdata1 = Blocks.AIR.getBlockData();

            generatoraccess.setTypeAndData(blockposition.b(i, 0, j), iblockdata1, 4);
            generatoraccess.setTypeAndData(blockposition.b(i + 1, 0, j), iblockdata1, 4);
            generatoraccess.setTypeAndData(blockposition.b(i, 0, j + 1), iblockdata1, 4);
            generatoraccess.setTypeAndData(blockposition.b(i + 1, 0, j + 1), iblockdata1, 4);
            if (worldgenfeatureconfigured.a(generatoraccess, chunkgenerator, random, blockposition.b(i, 0, j))) {
                return true;
            } else {
                generatoraccess.setTypeAndData(blockposition.b(i, 0, j), iblockdata, 4);
                generatoraccess.setTypeAndData(blockposition.b(i + 1, 0, j), iblockdata, 4);
                generatoraccess.setTypeAndData(blockposition.b(i, 0, j + 1), iblockdata, 4);
                generatoraccess.setTypeAndData(blockposition.b(i + 1, 0, j + 1), iblockdata, 4);
                return false;
            }
        }
    }

    public static boolean a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, int i, int j) {
        Block block = iblockdata.getBlock();

        return block == iblockaccess.getType(blockposition.b(i, 0, j)).getBlock() && block == iblockaccess.getType(blockposition.b(i + 1, 0, j)).getBlock() && block == iblockaccess.getType(blockposition.b(i, 0, j + 1)).getBlock() && block == iblockaccess.getType(blockposition.b(i + 1, 0, j + 1)).getBlock();
    }
}
