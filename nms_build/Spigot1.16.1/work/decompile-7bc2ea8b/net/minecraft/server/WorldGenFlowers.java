package net.minecraft.server;

import com.mojang.serialization.Codec;
import java.util.Random;

public abstract class WorldGenFlowers<U extends WorldGenFeatureConfiguration> extends WorldGenerator<U> {

    public WorldGenFlowers(Codec<U> codec) {
        super(codec);
    }

    @Override
    public boolean generate(GeneratorAccessSeed generatoraccessseed, StructureManager structuremanager, ChunkGenerator chunkgenerator, Random random, BlockPosition blockposition, U u0) {
        IBlockData iblockdata = this.b(random, blockposition, u0);
        int i = 0;

        for (int j = 0; j < this.a(u0); ++j) {
            BlockPosition blockposition1 = this.a(random, blockposition, u0);

            if (generatoraccessseed.isEmpty(blockposition1) && blockposition1.getY() < 255 && iblockdata.canPlace(generatoraccessseed, blockposition1) && this.a((GeneratorAccess) generatoraccessseed, blockposition1, u0)) {
                generatoraccessseed.setTypeAndData(blockposition1, iblockdata, 2);
                ++i;
            }
        }

        return i > 0;
    }

    public abstract boolean a(GeneratorAccess generatoraccess, BlockPosition blockposition, U u0);

    public abstract int a(U u0);

    public abstract BlockPosition a(Random random, BlockPosition blockposition, U u0);

    public abstract IBlockData b(Random random, BlockPosition blockposition, U u0);
}
