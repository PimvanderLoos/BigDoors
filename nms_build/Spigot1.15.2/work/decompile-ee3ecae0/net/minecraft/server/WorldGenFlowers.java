package net.minecraft.server;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;

public abstract class WorldGenFlowers<U extends WorldGenFeatureConfiguration> extends WorldGenerator<U> {

    public WorldGenFlowers(Function<Dynamic<?>, ? extends U> function) {
        super(function);
    }

    @Override
    public boolean generate(GeneratorAccess generatoraccess, ChunkGenerator<? extends GeneratorSettingsDefault> chunkgenerator, Random random, BlockPosition blockposition, U u0) {
        IBlockData iblockdata = this.b(random, blockposition, u0);
        int i = 0;

        for (int j = 0; j < this.a(u0); ++j) {
            BlockPosition blockposition1 = this.a(random, blockposition, u0);

            if (generatoraccess.isEmpty(blockposition1) && blockposition1.getY() < 255 && iblockdata.canPlace(generatoraccess, blockposition1) && this.a(generatoraccess, blockposition1, u0)) {
                generatoraccess.setTypeAndData(blockposition1, iblockdata, 2);
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
