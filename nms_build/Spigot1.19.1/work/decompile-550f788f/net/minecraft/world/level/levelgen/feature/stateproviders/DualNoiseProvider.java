package net.minecraft.world.level.levelgen.feature.stateproviders;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.core.BlockPosition;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.InclusiveRange;
import net.minecraft.util.MathHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.SeededRandom;
import net.minecraft.world.level.levelgen.synth.NoiseGeneratorNormal;

public class DualNoiseProvider extends NoiseProvider {

    public static final Codec<DualNoiseProvider> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(InclusiveRange.codec(Codec.INT, 1, 64).fieldOf("variety").forGetter((dualnoiseprovider) -> {
            return dualnoiseprovider.variety;
        }), NoiseGeneratorNormal.a.DIRECT_CODEC.fieldOf("slow_noise").forGetter((dualnoiseprovider) -> {
            return dualnoiseprovider.slowNoiseParameters;
        }), ExtraCodecs.POSITIVE_FLOAT.fieldOf("slow_scale").forGetter((dualnoiseprovider) -> {
            return dualnoiseprovider.slowScale;
        })).and(noiseProviderCodec(instance)).apply(instance, DualNoiseProvider::new);
    });
    private final InclusiveRange<Integer> variety;
    private final NoiseGeneratorNormal.a slowNoiseParameters;
    private final float slowScale;
    private final NoiseGeneratorNormal slowNoise;

    public DualNoiseProvider(InclusiveRange<Integer> inclusiverange, NoiseGeneratorNormal.a noisegeneratornormal_a, float f, long i, NoiseGeneratorNormal.a noisegeneratornormal_a1, float f1, List<IBlockData> list) {
        super(i, noisegeneratornormal_a1, f1, list);
        this.variety = inclusiverange;
        this.slowNoiseParameters = noisegeneratornormal_a;
        this.slowScale = f;
        this.slowNoise = NoiseGeneratorNormal.create(new SeededRandom(new LegacyRandomSource(i)), noisegeneratornormal_a);
    }

    @Override
    protected WorldGenFeatureStateProviders<?> type() {
        return WorldGenFeatureStateProviders.DUAL_NOISE_PROVIDER;
    }

    @Override
    public IBlockData getState(RandomSource randomsource, BlockPosition blockposition) {
        double d0 = this.getSlowNoiseValue(blockposition);
        int i = (int) MathHelper.clampedMap(d0, -1.0D, 1.0D, (double) (Integer) this.variety.minInclusive(), (double) ((Integer) this.variety.maxInclusive() + 1));
        List<IBlockData> list = Lists.newArrayListWithCapacity(i);

        for (int j = 0; j < i; ++j) {
            list.add(this.getRandomState(this.states, this.getSlowNoiseValue(blockposition.offset(j * '\ud511', 0, j * '\u85ba'))));
        }

        return this.getRandomState(list, blockposition, (double) this.scale);
    }

    protected double getSlowNoiseValue(BlockPosition blockposition) {
        return this.slowNoise.getValue((double) ((float) blockposition.getX() * this.slowScale), (double) ((float) blockposition.getY() * this.slowScale), (double) ((float) blockposition.getZ() * this.slowScale));
    }
}
