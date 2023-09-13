package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BlockPosition;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsBlock;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureBlockPileConfiguration;

public class WorldGenFeatureNetherForestVegetation extends WorldGenerator<WorldGenFeatureBlockPileConfiguration> {

    public WorldGenFeatureNetherForestVegetation(Codec<WorldGenFeatureBlockPileConfiguration> codec) {
        super(codec);
    }

    public boolean a(GeneratorAccessSeed generatoraccessseed, ChunkGenerator chunkgenerator, Random random, BlockPosition blockposition, WorldGenFeatureBlockPileConfiguration worldgenfeatureblockpileconfiguration) {
        return a(generatoraccessseed, random, blockposition, worldgenfeatureblockpileconfiguration, 8, 4);
    }

    public static boolean a(GeneratorAccess generatoraccess, Random random, BlockPosition blockposition, WorldGenFeatureBlockPileConfiguration worldgenfeatureblockpileconfiguration, int i, int j) {
        Block block = generatoraccess.getType(blockposition.down()).getBlock();

        if (!block.a((Tag) TagsBlock.NYLIUM)) {
            return false;
        } else {
            int k = blockposition.getY();

            if (k >= 1 && k + 1 < 256) {
                int l = 0;

                for (int i1 = 0; i1 < i * i; ++i1) {
                    BlockPosition blockposition1 = blockposition.b(random.nextInt(i) - random.nextInt(i), random.nextInt(j) - random.nextInt(j), random.nextInt(i) - random.nextInt(i));
                    IBlockData iblockdata = worldgenfeatureblockpileconfiguration.b.a(random, blockposition1);

                    if (generatoraccess.isEmpty(blockposition1) && blockposition1.getY() > 0 && iblockdata.canPlace(generatoraccess, blockposition1)) {
                        generatoraccess.setTypeAndData(blockposition1, iblockdata, 2);
                        ++l;
                    }
                }

                return l > 0;
            } else {
                return false;
            }
        }
    }
}
