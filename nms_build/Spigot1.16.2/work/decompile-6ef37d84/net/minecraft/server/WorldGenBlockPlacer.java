package net.minecraft.server;

import com.mojang.serialization.Codec;
import java.util.Random;

public abstract class WorldGenBlockPlacer {

    public static final Codec<WorldGenBlockPlacer> a = IRegistry.BLOCK_PLACER_TYPE.dispatch(WorldGenBlockPlacer::a, WorldGenBlockPlacers::a);

    public WorldGenBlockPlacer() {}

    public abstract void a(GeneratorAccess generatoraccess, BlockPosition blockposition, IBlockData iblockdata, Random random);

    protected abstract WorldGenBlockPlacers<?> a();
}
