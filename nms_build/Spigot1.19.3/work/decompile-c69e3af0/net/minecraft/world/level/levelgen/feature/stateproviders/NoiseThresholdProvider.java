package net.minecraft.world.level.levelgen.feature.stateproviders;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.SystemUtils;
import net.minecraft.core.BlockPosition;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.levelgen.synth.NoiseGeneratorNormal;

public class NoiseThresholdProvider extends NoiseBasedStateProvider {

    public static final Codec<NoiseThresholdProvider> CODEC = RecordCodecBuilder.create((instance) -> {
        return noiseCodec(instance).and(instance.group(Codec.floatRange(-1.0F, 1.0F).fieldOf("threshold").forGetter((noisethresholdprovider) -> {
            return noisethresholdprovider.threshold;
        }), Codec.floatRange(0.0F, 1.0F).fieldOf("high_chance").forGetter((noisethresholdprovider) -> {
            return noisethresholdprovider.highChance;
        }), IBlockData.CODEC.fieldOf("default_state").forGetter((noisethresholdprovider) -> {
            return noisethresholdprovider.defaultState;
        }), Codec.list(IBlockData.CODEC).fieldOf("low_states").forGetter((noisethresholdprovider) -> {
            return noisethresholdprovider.lowStates;
        }), Codec.list(IBlockData.CODEC).fieldOf("high_states").forGetter((noisethresholdprovider) -> {
            return noisethresholdprovider.highStates;
        }))).apply(instance, NoiseThresholdProvider::new);
    });
    private final float threshold;
    private final float highChance;
    private final IBlockData defaultState;
    private final List<IBlockData> lowStates;
    private final List<IBlockData> highStates;

    public NoiseThresholdProvider(long i, NoiseGeneratorNormal.a noisegeneratornormal_a, float f, float f1, float f2, IBlockData iblockdata, List<IBlockData> list, List<IBlockData> list1) {
        super(i, noisegeneratornormal_a, f);
        this.threshold = f1;
        this.highChance = f2;
        this.defaultState = iblockdata;
        this.lowStates = list;
        this.highStates = list1;
    }

    @Override
    protected WorldGenFeatureStateProviders<?> type() {
        return WorldGenFeatureStateProviders.NOISE_THRESHOLD_PROVIDER;
    }

    @Override
    public IBlockData getState(RandomSource randomsource, BlockPosition blockposition) {
        double d0 = this.getNoiseValue(blockposition, (double) this.scale);

        return d0 < (double) this.threshold ? (IBlockData) SystemUtils.getRandom(this.lowStates, randomsource) : (randomsource.nextFloat() < this.highChance ? (IBlockData) SystemUtils.getRandom(this.highStates, randomsource) : this.defaultState);
    }
}
