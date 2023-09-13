package net.minecraft.world.level.biome;

import com.mojang.serialization.Codec;
import net.minecraft.core.IRegistry;

public class BiomeSources {

    public BiomeSources() {}

    public static Codec<? extends WorldChunkManager> bootstrap(IRegistry<Codec<? extends WorldChunkManager>> iregistry) {
        IRegistry.register(iregistry, "fixed", WorldChunkManagerHell.CODEC);
        IRegistry.register(iregistry, "multi_noise", WorldChunkManagerMultiNoise.CODEC);
        IRegistry.register(iregistry, "checkerboard", WorldChunkManagerCheckerBoard.CODEC);
        return (Codec) IRegistry.register(iregistry, "the_end", WorldChunkManagerTheEnd.CODEC);
    }
}
