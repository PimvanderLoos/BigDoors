package net.minecraft.server;

import com.mojang.serialization.Codec;
import java.util.Random;

public class WorldGenBlockPlacerDoublePlant extends WorldGenBlockPlacer {

    public static final Codec<WorldGenBlockPlacerDoublePlant> b = Codec.unit(() -> {
        return WorldGenBlockPlacerDoublePlant.c;
    });
    public static final WorldGenBlockPlacerDoublePlant c = new WorldGenBlockPlacerDoublePlant();

    public WorldGenBlockPlacerDoublePlant() {}

    @Override
    protected WorldGenBlockPlacers<?> a() {
        return WorldGenBlockPlacers.b;
    }

    @Override
    public void a(GeneratorAccess generatoraccess, BlockPosition blockposition, IBlockData iblockdata, Random random) {
        ((BlockTallPlant) iblockdata.getBlock()).a(generatoraccess, blockposition, 2);
    }
}
