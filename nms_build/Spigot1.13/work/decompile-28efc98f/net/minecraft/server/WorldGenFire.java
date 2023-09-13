package net.minecraft.server;

import java.util.Random;

public class WorldGenFire extends WorldGenerator<WorldGenFeatureEmptyConfiguration> {

    public WorldGenFire() {}

    public boolean a(GeneratorAccess generatoraccess, ChunkGenerator<? extends GeneratorSettings> chunkgenerator, Random random, BlockPosition blockposition, WorldGenFeatureEmptyConfiguration worldgenfeatureemptyconfiguration) {
        for (int i = 0; i < 64; ++i) {
            BlockPosition blockposition1 = blockposition.a(random.nextInt(8) - random.nextInt(8), random.nextInt(4) - random.nextInt(4), random.nextInt(8) - random.nextInt(8));

            if (generatoraccess.isEmpty(blockposition1) && generatoraccess.getType(blockposition1.down()).getBlock() == Blocks.NETHERRACK) {
                generatoraccess.setTypeAndData(blockposition1, Blocks.FIRE.getBlockData(), 2);
            }
        }

        return true;
    }
}
