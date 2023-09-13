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

    private static int a(int i, int j, int k, int l) {
        return Math.max(Math.abs(i - k), Math.abs(j - l));
    }

    @Override
    public boolean generate(FeaturePlaceContext<WorldGenFeatureEmptyConfiguration> featureplacecontext) {
        GeneratorAccessSeed generatoraccessseed = featureplacecontext.a();
        ChunkCoordIntPair chunkcoordintpair = new ChunkCoordIntPair(featureplacecontext.d());

        if (a(chunkcoordintpair.x, chunkcoordintpair.z, WorldGenFeatureEndPlatform.PLATFORM_ORIGIN_CHUNK.x, WorldGenFeatureEndPlatform.PLATFORM_ORIGIN_CHUNK.z) > 1) {
            return true;
        } else {
            BlockPosition blockposition = WorldGenFeatureEndPlatform.PLATFORM_OFFSET.h(featureplacecontext.d().getY() + WorldGenFeatureEndPlatform.PLATFORM_OFFSET.getY());
            BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();

            for (int i = chunkcoordintpair.e(); i <= chunkcoordintpair.g(); ++i) {
                for (int j = chunkcoordintpair.d(); j <= chunkcoordintpair.f(); ++j) {
                    if (a(blockposition.getX(), blockposition.getZ(), j, i) <= 16) {
                        blockposition_mutableblockposition.d(j, blockposition.getY(), i);
                        if (blockposition_mutableblockposition.equals(blockposition)) {
                            generatoraccessseed.setTypeAndData(blockposition_mutableblockposition, Blocks.COBBLESTONE.getBlockData(), 2);
                        } else {
                            generatoraccessseed.setTypeAndData(blockposition_mutableblockposition, Blocks.STONE.getBlockData(), 2);
                        }
                    }
                }
            }

            return true;
        }
    }
}
