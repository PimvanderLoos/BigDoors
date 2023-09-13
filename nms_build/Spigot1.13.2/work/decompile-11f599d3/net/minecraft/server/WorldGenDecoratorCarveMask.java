package net.minecraft.server;

import java.util.BitSet;
import java.util.Random;

public class WorldGenDecoratorCarveMask extends WorldGenDecorator<WorldGenDecoratorCarveMaskConfiguration> {

    public WorldGenDecoratorCarveMask() {}

    public <C extends WorldGenFeatureConfiguration> boolean a(GeneratorAccess generatoraccess, ChunkGenerator<? extends GeneratorSettings> chunkgenerator, Random random, BlockPosition blockposition, WorldGenDecoratorCarveMaskConfiguration worldgendecoratorcarvemaskconfiguration, WorldGenerator<C> worldgenerator, C c0) {
        IChunkAccess ichunkaccess = generatoraccess.y(blockposition);
        ChunkCoordIntPair chunkcoordintpair = ichunkaccess.getPos();
        BitSet bitset = ichunkaccess.a(worldgendecoratorcarvemaskconfiguration.a);

        for (int i = 0; i < bitset.length(); ++i) {
            if (bitset.get(i) && random.nextFloat() < worldgendecoratorcarvemaskconfiguration.b) {
                int j = i & 15;
                int k = i >> 4 & 15;
                int l = i >> 8;

                worldgenerator.generate(generatoraccess, chunkgenerator, random, new BlockPosition(chunkcoordintpair.d() + j, l, chunkcoordintpair.e() + k), c0);
            }
        }

        return true;
    }
}
