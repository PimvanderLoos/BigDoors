package net.minecraft.server;

import java.util.Random;

public class WorldGenDecoratorEndGateway extends WorldGenDecorator<WorldGenFeatureDecoratorEmptyConfiguration> {

    public WorldGenDecoratorEndGateway() {}

    public <C extends WorldGenFeatureConfiguration> boolean a(GeneratorAccess generatoraccess, ChunkGenerator<? extends GeneratorSettings> chunkgenerator, Random random, BlockPosition blockposition, WorldGenFeatureDecoratorEmptyConfiguration worldgenfeaturedecoratoremptyconfiguration, WorldGenerator<C> worldgenerator, C c0) {
        boolean flag = false;

        if (random.nextInt(700) == 0) {
            int i = random.nextInt(16);
            int j = random.nextInt(16);
            int k = generatoraccess.getHighestBlockYAt(HeightMap.Type.MOTION_BLOCKING, blockposition.a(i, 0, j)).getY();

            if (k > 0) {
                int l = k + 3 + random.nextInt(7);
                BlockPosition blockposition1 = blockposition.a(i, l, j);

                worldgenerator.generate(generatoraccess, chunkgenerator, random, blockposition1, c0);
                TileEntity tileentity = generatoraccess.getTileEntity(blockposition);

                if (tileentity instanceof TileEntityEndGateway) {
                    TileEntityEndGateway tileentityendgateway = (TileEntityEndGateway) tileentity;

                    tileentityendgateway.b(((ChunkProviderTheEnd) chunkgenerator).f());
                }
            }
        }

        return false;
    }
}
