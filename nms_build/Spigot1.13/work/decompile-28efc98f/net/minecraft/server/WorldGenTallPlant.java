package net.minecraft.server;

import java.util.Random;

public class WorldGenTallPlant extends WorldGenerator<WorldGenFeatureDoublePlantConfiguration> {

    public WorldGenTallPlant() {}

    public boolean a(GeneratorAccess generatoraccess, ChunkGenerator<? extends GeneratorSettings> chunkgenerator, Random random, BlockPosition blockposition, WorldGenFeatureDoublePlantConfiguration worldgenfeaturedoubleplantconfiguration) {
        boolean flag = false;

        for (int i = 0; i < 64; ++i) {
            BlockPosition blockposition1 = blockposition.a(random.nextInt(8) - random.nextInt(8), random.nextInt(4) - random.nextInt(4), random.nextInt(8) - random.nextInt(8));

            if (generatoraccess.isEmpty(blockposition1) && blockposition1.getY() < 254 && worldgenfeaturedoubleplantconfiguration.a.canPlace(generatoraccess, blockposition1)) {
                ((BlockTallPlant) worldgenfeaturedoubleplantconfiguration.a.getBlock()).a(generatoraccess, blockposition1, 2);
                flag = true;
            }
        }

        return flag;
    }
}
