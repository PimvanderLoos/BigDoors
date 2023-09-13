package net.minecraft.server;

import java.util.Random;

public abstract class WorldGenBlockPlacer implements MinecraftSerializable {

    protected final WorldGenBlockPlacers<?> a;

    protected WorldGenBlockPlacer(WorldGenBlockPlacers<?> worldgenblockplacers) {
        this.a = worldgenblockplacers;
    }

    public abstract void a(GeneratorAccess generatoraccess, BlockPosition blockposition, IBlockData iblockdata, Random random);
}
