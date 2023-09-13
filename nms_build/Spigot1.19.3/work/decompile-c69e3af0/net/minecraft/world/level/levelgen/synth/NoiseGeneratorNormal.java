package net.minecraft.world.level.levelgen.synth;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import it.unimi.dsi.fastutil.doubles.DoubleListIterator;
import java.util.List;
import net.minecraft.SystemUtils;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.util.RandomSource;

public class NoiseGeneratorNormal {

    private static final double INPUT_FACTOR = 1.0181268882175227D;
    private static final double TARGET_DEVIATION = 0.3333333333333333D;
    private final double valueFactor;
    private final NoiseGeneratorOctaves first;
    private final NoiseGeneratorOctaves second;
    private final double maxValue;
    private final NoiseGeneratorNormal.a parameters;

    /** @deprecated */
    @Deprecated
    public static NoiseGeneratorNormal createLegacyNetherBiome(RandomSource randomsource, NoiseGeneratorNormal.a noisegeneratornormal_a) {
        return new NoiseGeneratorNormal(randomsource, noisegeneratornormal_a, false);
    }

    public static NoiseGeneratorNormal create(RandomSource randomsource, int i, double... adouble) {
        return create(randomsource, new NoiseGeneratorNormal.a(i, new DoubleArrayList(adouble)));
    }

    public static NoiseGeneratorNormal create(RandomSource randomsource, NoiseGeneratorNormal.a noisegeneratornormal_a) {
        return new NoiseGeneratorNormal(randomsource, noisegeneratornormal_a, true);
    }

    private NoiseGeneratorNormal(RandomSource randomsource, NoiseGeneratorNormal.a noisegeneratornormal_a, boolean flag) {
        int i = noisegeneratornormal_a.firstOctave;
        DoubleList doublelist = noisegeneratornormal_a.amplitudes;

        this.parameters = noisegeneratornormal_a;
        if (flag) {
            this.first = NoiseGeneratorOctaves.create(randomsource, i, doublelist);
            this.second = NoiseGeneratorOctaves.create(randomsource, i, doublelist);
        } else {
            this.first = NoiseGeneratorOctaves.createLegacyForLegacyNetherBiome(randomsource, i, doublelist);
            this.second = NoiseGeneratorOctaves.createLegacyForLegacyNetherBiome(randomsource, i, doublelist);
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
        this.maxValue = (this.first.maxValue() + this.second.maxValue()) * this.valueFactor;
    }

    public double maxValue() {
        return this.maxValue;
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
        return this.parameters;
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

    public static record a(int firstOctave, DoubleList amplitudes) {

        public static final Codec<NoiseGeneratorNormal.a> DIRECT_CODEC = RecordCodecBuilder.create((instance) -> {
            return instance.group(Codec.INT.fieldOf("firstOctave").forGetter(NoiseGeneratorNormal.a::firstOctave), Codec.DOUBLE.listOf().fieldOf("amplitudes").forGetter(NoiseGeneratorNormal.a::amplitudes)).apply(instance, NoiseGeneratorNormal.a::new);
        });
        public static final Codec<Holder<NoiseGeneratorNormal.a>> CODEC = RegistryFileCodec.create(Registries.NOISE, NoiseGeneratorNormal.a.DIRECT_CODEC);

        public a(int i, List<Double> list) {
            this(i, (DoubleList) (new DoubleArrayList(list)));
        }

        public a(int i, double d0, double... adouble) {
            this(i, (DoubleList) SystemUtils.make(new DoubleArrayList(adouble), (doublearraylist) -> {
                doublearraylist.add(0, d0);
            }));
        }
    }
}
