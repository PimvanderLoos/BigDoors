package net.minecraft.server;

import java.util.Iterator;
import java.util.Random;
import javax.annotation.Nullable;

public abstract class WorldGenTreeProvider {

    public WorldGenTreeProvider() {}

    @Nullable
    protected abstract WorldGenFeatureConfigured<WorldGenFeatureSmallTreeConfigurationConfiguration, ?> a(Random random, boolean flag);

    public boolean a(GeneratorAccess generatoraccess, ChunkGenerator<?> chunkgenerator, BlockPosition blockposition, IBlockData iblockdata, Random random) {
        WorldGenFeatureConfigured<WorldGenFeatureSmallTreeConfigurationConfiguration, ?> worldgenfeatureconfigured = this.a(random, this.a(generatoraccess, blockposition));

        if (worldgenfeatureconfigured == null) {
            return false;
        } else {
            generatoraccess.setTypeAndData(blockposition, Blocks.AIR.getBlockData(), 4);
            ((WorldGenFeatureSmallTreeConfigurationConfiguration) worldgenfeatureconfigured.c).a();
            if (worldgenfeatureconfigured.a(generatoraccess, chunkgenerator, random, blockposition)) {
                return true;
            } else {
                generatoraccess.setTypeAndData(blockposition, iblockdata, 4);
                return false;
            }
        }
    }

    private boolean a(GeneratorAccess generatoraccess, BlockPosition blockposition) {
        Iterator iterator = BlockPosition.MutableBlockPosition.a(blockposition.down().north(2).west(2), blockposition.up().south(2).east(2)).iterator();

        BlockPosition blockposition1;

        do {
            if (!iterator.hasNext()) {
                return false;
            }

            blockposition1 = (BlockPosition) iterator.next();
        } while (!generatoraccess.getType(blockposition1).a(TagsBlock.FLOWERS));

        return true;
    }
}
