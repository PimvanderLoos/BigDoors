package net.minecraft.world.level.chunk;

import com.mojang.serialization.Codec;
import net.minecraft.core.IRegistry;
import net.minecraft.world.level.levelgen.ChunkGeneratorAbstract;
import net.minecraft.world.level.levelgen.ChunkProviderDebug;
import net.minecraft.world.level.levelgen.ChunkProviderFlat;

public class ChunkGenerators {

    public ChunkGenerators() {}

    public static Codec<? extends ChunkGenerator> bootstrap(IRegistry<Codec<? extends ChunkGenerator>> iregistry) {
        IRegistry.register(iregistry, "noise", ChunkGeneratorAbstract.CODEC);
        IRegistry.register(iregistry, "flat", ChunkProviderFlat.CODEC);
        return (Codec) IRegistry.register(iregistry, "debug", ChunkProviderDebug.CODEC);
    }
}
