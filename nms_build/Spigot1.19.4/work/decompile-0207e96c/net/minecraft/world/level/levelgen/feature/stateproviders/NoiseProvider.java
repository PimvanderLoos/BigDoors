package net.minecraft.world.level.levelgen.feature.stateproviders;

import com.mojang.datafixers.Products.P4;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import com.mojang.serialization.codecs.RecordCodecBuilder.Mu;
import java.util.List;
import net.minecraft.core.BlockPosition;
import net.minecraft.util.MathHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.levelgen.synth.NoiseGeneratorNormal;

public class NoiseProvider extends NoiseBasedStateProvider {

    public static final Codec<NoiseProvider> CODEC = RecordCodecBuilder.create((instance) -> {
        return noiseProviderCodec(instance).apply(instance, NoiseProvider::new);
    });
    protected final List<IBlockData> states;

    protected static <P extends NoiseProvider> P4<Mu<P>, Long, NoiseGeneratorNormal.a, Float, List<IBlockData>> noiseProviderCodec(Instance<P> instance) {
        return noiseCodec(instance).and(Codec.list(IBlockData.CODEC).fieldOf("states").forGetter((noiseprovider) -> {
            return noiseprovider.states;
        }));
    }

    public NoiseProvider(long i, NoiseGeneratorNormal.a noisegeneratornormal_a, float f, List<IBlockData> list) {
        super(i, noisegeneratornormal_a, f);
        this.states = list;
    }

    @Override
    protected WorldGenFeatureStateProviders<?> type() {
        return WorldGenFeatureStateProviders.NOISE_PROVIDER;
    }

    @Override
    public IBlockData getState(RandomSource randomsource, BlockPosition blockposition) {
        return this.getRandomState(this.states, blockposition, (double) this.scale);
    }

    protected IBlockData getRandomState(List<IBlockData> list, BlockPosition blockposition, double d0) {
        double d1 = this.getNoiseValue(blockposition, d0);

        return this.getRandomState(list, d1);
    }

    protected IBlockData getRandomState(List<IBlockData> list, double d0) {
        double d1 = MathHelper.clamp((1.0D + d0) / 2.0D, 0.0D, 0.9999D);

        return (IBlockData) list.get((int) (d1 * (double) list.size()));
    }
}
