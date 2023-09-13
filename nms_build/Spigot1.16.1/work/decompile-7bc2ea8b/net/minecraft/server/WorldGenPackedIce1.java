package net.minecraft.server;

import com.mojang.serialization.Codec;
import java.util.Random;

public class WorldGenPackedIce1 extends WorldGenerator<WorldGenFeatureRadiusConfiguration> {

    private final Block a;

    public WorldGenPackedIce1(Codec<WorldGenFeatureRadiusConfiguration> codec) {
        super(codec);
        this.a = Blocks.PACKED_ICE;
    }

    public boolean a(GeneratorAccessSeed generatoraccessseed, StructureManager structuremanager, ChunkGenerator chunkgenerator, Random random, BlockPosition blockposition, WorldGenFeatureRadiusConfiguration worldgenfeatureradiusconfiguration) {
        while (generatoraccessseed.isEmpty(blockposition) && blockposition.getY() > 2) {
            blockposition = blockposition.down();
        }

        if (!generatoraccessseed.getType(blockposition).a(Blocks.SNOW_BLOCK)) {
            return false;
        } else {
            int i = random.nextInt(worldgenfeatureradiusconfiguration.b) + 2;
            boolean flag = true;

            for (int j = blockposition.getX() - i; j <= blockposition.getX() + i; ++j) {
                for (int k = blockposition.getZ() - i; k <= blockposition.getZ() + i; ++k) {
                    int l = j - blockposition.getX();
                    int i1 = k - blockposition.getZ();

                    if (l * l + i1 * i1 <= i * i) {
                        for (int j1 = blockposition.getY() - 1; j1 <= blockposition.getY() + 1; ++j1) {
                            BlockPosition blockposition1 = new BlockPosition(j, j1, k);
                            Block block = generatoraccessseed.getType(blockposition1).getBlock();

                            if (b(block) || block == Blocks.SNOW_BLOCK || block == Blocks.ICE) {
                                generatoraccessseed.setTypeAndData(blockposition1, this.a.getBlockData(), 2);
                            }
                        }
                    }
                }
            }

            return true;
        }
    }
}
