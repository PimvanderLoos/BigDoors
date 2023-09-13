package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureEmptyConfiguration;

public class WorldGenFeatureEndPlatform extends WorldGenerator<WorldGenFeatureEmptyConfiguration> {

    private static final BlockPosition PLATFORM_OFFSET = new BlockPosition(8, 3, 8);
    private static final ChunkCoordIntPair PLATFORM_ORIGIN_CHUNK = new ChunkCoordIntPair(WorldGenFeatureEndPlatform.PLATFORM_OFFSET);
    private static final int PLATFORM_RADIUS = 16;
    private static final int PLATFORM_RADIUS_CHUNKS = 1;

    public WorldGenFeatureEndPlatform(Codec<WorldGenFeatureEmptyConfiguration> codec) {
        super(codec);
    }

    private static int checkerboardDistance(int i, int j, int k, int l) {
        return Math.max(Math.abs(i - k), Math.abs(j - l));
    }

    @Override
    public boolean place(FeaturePlaceContext<WorldGenFeatureEmptyConfiguration> featureplacecontext) {
        GeneratorAccessSeed generatoraccessseed = featureplacecontext.level();
        ChunkCoordIntPair chunkcoordintpair = new ChunkCoordIntPair(featureplacecontext.origin());

        if (checkerboardDistance(chunkcoordintpair.x, chunkcoordintpair.z, WorldGenFeatureEndPlatform.PLATFORM_ORIGIN_CHUNK.x, WorldGenFeatureEndPlatform.PLATFORM_ORIGIN_CHUNK.z) > 1) {
            return true;
        } else {
            BlockPosition blockposition = WorldGenFeatureEndPlatform.PLATFORM_OFFSET.atY(featureplacecontext.origin().getY() + WorldGenFeatureEndPlatform.PLATFORM_OFFSET.getY());
            BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();

            for (int i = chunkcoordintpair.getMinBlockZ(); i <= chunkcoordintpair.getMaxBlockZ(); ++i) {
                for (int j = chunkcoordintpair.getMinBlockX(); j <= chunkcoordintpair.getMaxBlockX(); ++j) {
                    if (checkerboardDistance(blockposition.getX(), blockposition.getZ(), j, i) <= 16) {
                        blockposition_mutableblockposition.set(j, blockposition.getY(), i);
                        if (blockposition_mutableblockposition.equals(blockposition)) {
                            generatoraccessseed.setBlock(blockposition_mutableblockposition, Blocks.COBBLESTONE.defaultBlockState(), 2);
                        } else {
                            generatoraccessseed.setBlock(blockposition_mutableblockposition, Blocks.STONE.defaultBlockState(), 2);
                        }
                    }
                }
            }

            return true;
        }
    }
}
