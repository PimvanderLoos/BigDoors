package net.minecraft.world.level.levelgen.synth;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import it.unimi.dsi.fastutil.ints.IntBidirectionalIterator;
import it.unimi.dsi.fastutil.ints.IntRBTreeSet;
import it.unimi.dsi.fastutil.ints.IntSortedSet;
import java.util.List;
import java.util.function.LongFunction;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import net.minecraft.util.MathHelper;
import net.minecraft.world.level.levelgen.RandomSource;
import net.minecraft.world.level.levelgen.SeededRandom;

public class NoiseGeneratorOctaves implements NoiseGenerator {

    private static final int ROUND_OFF = 33554432;
    private final NoiseGeneratorPerlin[] noiseLevels;
    private final DoubleList amplitudes;
    private final double lowestFreqValueFactor;
    private final double lowestFreqInputFactor;

    public NoiseGeneratorOctaves(RandomSource randomsource, IntStream intstream) {
        this(randomsource, (List) intstream.boxed().collect(ImmutableList.toImmutableList()));
    }

    public NoiseGeneratorOctaves(RandomSource randomsource, List<Integer> list) {
        this(randomsource, (IntSortedSet) (new IntRBTreeSet(list)));
    }

    public static NoiseGeneratorOctaves a(RandomSource randomsource, int i, double... adouble) {
        return a(randomsource, i, (DoubleList) (new DoubleArrayList(adouble)));
    }

    public static NoiseGeneratorOctaves a(RandomSource randomsource, int i, DoubleList doublelist) {
        return new NoiseGeneratorOctaves(randomsource, Pair.of(i, doublelist));
    }

    private static Pair<Integer, DoubleList> a(IntSortedSet intsortedset) {
        if (intsortedset.isEmpty()) {
            throw new IllegalArgumentException("Need some octaves!");
        } else {
            int i = -intsortedset.firstInt();
            int j = intsortedset.lastInt();
            int k = i + j + 1;

            if (k < 1) {
                throw new IllegalArgumentException("Total number of octaves needs to be >= 1");
            } else {
                DoubleArrayList doublearraylist = new DoubleArrayList(new double[k]);
                IntBidirectionalIterator intbidirectionaliterator = intsortedset.iterator();

                while (intbidirectionaliterator.hasNext()) {
                    int l = intbidirectionaliterator.nextInt();

                    doublearraylist.set(l + i, 1.0D);
                }

                return Pair.of(-i, doublearraylist);
            }
        }
    }

    private NoiseGeneratorOctaves(RandomSource randomsource, IntSortedSet intsortedset) {
        this(randomsource, intsortedset, SeededRandom::new);
    }

    private NoiseGeneratorOctaves(RandomSource randomsource, IntSortedSet intsortedset, LongFunction<RandomSource> longfunction) {
        this(randomsource, a(intsortedset), longfunction);
    }

    protected NoiseGeneratorOctaves(RandomSource randomsource, Pair<Integer, DoubleList> pair) {
        this(randomsource, pair, SeededRandom::new);
    }

    protected NoiseGeneratorOctaves(RandomSource randomsource, Pair<Integer, DoubleList> pair, LongFunction<RandomSource> longfunction) {
        int i = (Integer) pair.getFirst();

        this.amplitudes = (DoubleList) pair.getSecond();
        NoiseGeneratorPerlin noisegeneratorperlin = new NoiseGeneratorPerlin(randomsource);
        int j = this.amplitudes.size();
        int k = -i;

        this.noiseLevels = new NoiseGeneratorPerlin[j];
        if (k >= 0 && k < j) {
            double d0 = this.amplitudes.getDouble(k);

            if (d0 != 0.0D) {
                this.noiseLevels[k] = noisegeneratorperlin;
            }
        }

        for (int l = k - 1; l >= 0; --l) {
            if (l < j) {
                double d1 = this.amplitudes.getDouble(l);

                if (d1 != 0.0D) {
                    this.noiseLevels[l] = new NoiseGeneratorPerlin(randomsource);
                } else {
                    a(randomsource);
                }
            } else {
                a(randomsource);
            }
        }

        if (k < j - 1) {
            throw new IllegalArgumentException("Positive octaves are temporarily disabled");
        } else {
            this.lowestFreqInputFactor = Math.pow(2.0D, (double) (-k));
            this.lowestFreqValueFactor = Math.pow(2.0D, (double) (j - 1)) / (Math.pow(2.0D, (double) j) - 1.0D);
        }
    }

    private static void a(RandomSource randomsource) {
        randomsource.a(262);
    }

    public double a(double d0, double d1, double d2) {
        return this.a(d0, d1, d2, 0.0D, 0.0D, false);
    }

    @Deprecated
    public double a(double d0, double d1, double d2, double d3, double d4, boolean flag) {
        double d5 = 0.0D;
        double d6 = this.lowestFreqInputFactor;
        double d7 = this.lowestFreqValueFactor;

        for (int i = 0; i < this.noiseLevels.length; ++i) {
            NoiseGeneratorPerlin noisegeneratorperlin = this.noiseLevels[i];

            if (noisegeneratorperlin != null) {
                double d8 = noisegeneratorperlin.a(a(d0 * d6), flag ? -noisegeneratorperlin.yo : a(d1 * d6), a(d2 * d6), d3 * d6, d4 * d6);

                d5 += this.amplitudes.getDouble(i) * d8 * d7;
            }

            d6 *= 2.0D;
            d7 /= 2.0D;
        }

        return d5;
    }

    @Nullable
    public NoiseGeneratorPerlin a(int i) {
        return this.noiseLevels[this.noiseLevels.length - 1 - i];
    }

    public static double a(double d0) {
        return d0 - (double) MathHelper.c(d0 / 3.3554432E7D + 0.5D) * 3.3554432E7D;
    }

    @Override
    public double a(double d0, double d1, double d2, double d3) {
        return this.a(d0, d1, 0.0D, d2, d3, false);
    }
}
