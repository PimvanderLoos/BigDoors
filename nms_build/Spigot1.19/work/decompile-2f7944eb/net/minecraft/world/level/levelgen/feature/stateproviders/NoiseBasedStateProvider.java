package net.minecraft.world.level.levelgen.feature.stateproviders;

import com.mojang.datafixers.Products.P3;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import com.mojang.serialization.codecs.RecordCodecBuilder.Mu;
import net.minecraft.core.BlockPosition;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.SeededRandom;
import net.minecraft.world.level.levelgen.synth.NoiseGeneratorNormal;

public abstract class NoiseBasedStateProvider extends WorldGenFeatureStateProvider {

    protected final long seed;
    protected final NoiseGeneratorNormal.a parameters;
    protected final float scale;
    protected final NoiseGeneratorNormal noise;

    protected static <P extends NoiseBasedStateProvider> P3<Mu<P>, Long, NoiseGeneratorNormal.a, Float> noiseCodec(Instance<P> instance) {
        return instance.group(Codec.LONG.fieldOf("seed").forGetter((noisebasedstateprovider) -> {
            return noisebasedstateprovider.seed;
        }), NoiseGeneratorNormal.a.DIRECT_CODEC.fieldOf("noise").forGetter((noisebasedstateprovider) -> {
            return noisebasedstateprovider.parameters;
        }), ExtraCodecs.POSITIVE_FLOAT.fieldOf("scale").forGetter((noisebasedstateprovider) -> {
            return noisebasedstateprovider.scale;
        }));
    }

    protected NoiseBasedStateProvider(long i, NoiseGeneratorNormal.a noisegeneratornormal_a, float f) {
        this.seed = i;
        this.parameters = noisegeneratornormal_a;
        this.scale = f;
        this.noise = NoiseGeneratorNormal.create(new SeededRandom(new LegacyRandomSource(i)), noisegeneratornormal_a);
    }

    protected double getNoiseValue(BlockPosition blockposition, double d0) {
        return this.noise.getValue((double) blockposition.getX() * d0, (double) blockposition.getY() * d0, (double) blockposition.getZ() * d0);
    }
}
