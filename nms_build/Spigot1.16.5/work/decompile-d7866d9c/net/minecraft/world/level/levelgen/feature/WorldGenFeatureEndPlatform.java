package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureEmptyConfiguration;

public class WorldGenFeatureEndPlatform extends WorldGenerator<WorldGenFeatureEmptyConfiguration> {

    private static final BlockPosition a = new BlockPosition(8, 3, 8);
    private static final ChunkCoordIntPair ab = new ChunkCoordIntPair(WorldGenFeatureEndPlatform.a);

    public WorldGenFeatureEndPlatform(Codec<WorldGenFeatureEmptyConfiguration> codec) {
        super(codec);
    }

    private static int a(int i, int j, int k, int l) {
        return Math.max(Math.abs(i - k), Math.abs(j - l));
    }

    public boolean a(GeneratorAccessSeed generatoraccessseed, ChunkGenerator chunkgenerator, Random random, BlockPosition blockposition, WorldGenFeatureEmptyConfiguration worldgenfeatureemptyconfiguration) {
        ChunkCoordIntPair chunkcoordintpair = new ChunkCoordIntPair(blockposition);

        if (a(chunkcoordintpair.x, chunkcoordintpair.z, WorldGenFeatureEndPlatform.ab.x, WorldGenFeatureEndPlatform.ab.z) > 1) {
            return true;
        } else {
            BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();

            for (int i = chunkcoordintpair.e(); i <= chunkcoordintpair.g(); ++i) {
                for (int j = chunkcoordintpair.d(); j <= chunkcoordintpair.f(); ++j) {
                    if (a(WorldGenFeatureEndPlatform.a.getX(), WorldGenFeatureEndPlatform.a.getZ(), j, i) <= 16) {
                        blockposition_mutableblockposition.d(j, WorldGenFeatureEndPlatform.a.getY(), i);
                        if (blockposition_mutableblockposition.equals(WorldGenFeatureEndPlatform.a)) {
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
