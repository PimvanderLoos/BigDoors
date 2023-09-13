package net.minecraft.server;

import com.mojang.serialization.Codec;
import java.util.Random;

public class WorldGenBlockPlacerSimple extends WorldGenBlockPlacer {

    public static final Codec<WorldGenBlockPlacerSimple> b = Codec.unit(() -> {
        return WorldGenBlockPlacerSimple.c;
    });
    public static final WorldGenBlockPlacerSimple c = new WorldGenBlockPlacerSimple();

    public WorldGenBlockPlacerSimple() {}

    @Override
    protected WorldGenBlockPlacers<?> a() {
        return WorldGenBlockPlacers.a;
    }

    @Override
    public void a(GeneratorAccess generatoraccess, BlockPosition blockposition, IBlockData iblockdata, Random random) {
        generatoraccess.setTypeAndData(blockposition, iblockdata, 2);
    }
}
