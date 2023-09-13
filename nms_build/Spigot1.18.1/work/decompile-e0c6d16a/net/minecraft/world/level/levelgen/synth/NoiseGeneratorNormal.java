package net.minecraft.world.level.levelgen.synth;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import it.unimi.dsi.fastutil.doubles.DoubleListIterator;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.core.IRegistry;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.world.level.levelgen.RandomSource;

public class NoiseGeneratorNormal {

    private static final double INPUT_FACTOR = 1.0181268882175227D;
    private static final double TARGET_DEVIATION = 0.3333333333333333D;
    private final double valueFactor;
    private final NoiseGeneratorOctaves first;
    private final NoiseGeneratorOctaves second;

    /** @deprecated */
    @Deprecated
    public static NoiseGeneratorNormal createLegacyNetherBiome(RandomSource randomsource, NoiseGeneratorNormal.a noisegeneratornormal_a) {
        return new NoiseGeneratorNormal(randomsource, noisegeneratornormal_a.firstOctave(), noisegeneratornormal_a.amplitudes(), false);
    }

    public static NoiseGeneratorNormal create(RandomSource randomsource, int i, double... adouble) {
        return new NoiseGeneratorNormal(randomsource, i, new DoubleArrayList(adouble), true);
    }

    public static NoiseGeneratorNormal create(RandomSource randomsource, NoiseGeneratorNormal.a noisegeneratornormal_a) {
        return new NoiseGeneratorNormal(randomsource, noisegeneratornormal_a.firstOctave(), noisegeneratornormal_a.amplitudes(), true);
    }

    public static NoiseGeneratorNormal create(RandomSource randomsource, int i, DoubleList doublelist) {
        return new NoiseGeneratorNormal(randomsource, i, doublelist, true);
    }

    private NoiseGeneratorNormal(RandomSource randomsource, int i, DoubleList doublelist, boolean flag) {
        if (flag) {
            this.first = NoiseGeneratorOctaves.create(randomsource, i, doublelist);
            this.second = NoiseGeneratorOctaves.create(randomsource, i, doublelist);
        } else {
            this.first = NoiseGeneratorOctaves.createLegacyForLegacyNormalNoise(randomsource, i, doublelist);
            this.second = NoiseGeneratorOctaves.createLegacyForLegacyNormalNoise(randomsource, i, doublelist);
        }

        int j = Integer.MAX_VALUE;
        int k = Integer.MIN_VALUE;
        DoubleListIterator doublelistiterator = doublelist.iterator();

        while (doublelistiterator.hasNext()) {
            int l = doublelistiterator.nextIndex();
            double d0 = doublelistiterator.nextDouble();

            if (d0 != 0.0D) {
                j = Math.min(j, l);
                k = Math.max(k, l);
            }
        }

        this.valueFactor = 0.16666666666666666D / expectedDeviation(k - j);
    }

    private static double expectedDeviation(int i) {
        return 0.1D * (1.0D + 1.0D / (double) (i + 1));
    }

    public double getValue(double d0, double d1, double d2) {
        double d3 = d0 * 1.0181268882175227D;
        double d4 = d1 * 1.0181268882175227D;
        double d5 = d2 * 1.0181268882175227D;

        return (this.first.getValue(d0, d1, d2) + this.second.getValue(d3, d4, d5)) * this.valueFactor;
    }

    public NoiseGeneratorNormal.a parameters() {
        return new NoiseGeneratorNormal.a(this.first.firstOctave(), this.first.amplitudes());
    }

    @VisibleForTesting
    public void parityConfigString(StringBuilder stringbuilder) {
        stringbuilder.append("NormalNoise {");
        stringbuilder.append("first: ");
        this.first.parityConfigString(stringbuilder);
        stringbuilder.append(", second: ");
        this.second.parityConfigString(stringbuilder);
        stringbuilder.append("}");
    }

    public static class a {

        private final int firstOctave;
        private final DoubleList amplitudes;
        public static final Codec<NoiseGeneratorNormal.a> DIRECT_CODEC = RecordCodecBuilder.create((instance) -> {
            return instance.group(Codec.INT.fieldOf("firstOctave").forGetter(NoiseGeneratorNormal.a::firstOctave), Codec.DOUBLE.listOf().fieldOf("amplitudes").forGetter(NoiseGeneratorNormal.a::amplitudes)).apply(instance, NoiseGeneratorNormal.a::new);
        });
        public static final Codec<Supplier<NoiseGeneratorNormal.a>> CODEC = RegistryFileCodec.create(IRegistry.NOISE_REGISTRY, NoiseGeneratorNormal.a.DIRECT_CODEC);

        public a(int i, List<Double> list) {
            this.firstOctave = i;
            this.amplitudes = new DoubleArrayList(list);
        }

        public a(int i, double d0, double... adouble) {
            this.firstOctave = i;
            this.amplitudes = new DoubleArrayList(adouble);
            this.amplitudes.add(0, d0);
        }

        public int firstOctave() {
            return this.firstOctave;
        }

        public DoubleList amplitudes() {
            return this.amplitudes;
        }
    }
}
