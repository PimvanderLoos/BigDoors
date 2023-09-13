package net.minecraft.world.level.levelgen.synth;

import it.unimi.dsi.fastutil.ints.IntRBTreeSet;
import it.unimi.dsi.fastutil.ints.IntSortedSet;
import java.util.List;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.SeededRandom;

public class NoiseGenerator3 {

    private final NoiseGenerator3Handler[] noiseLevels;
    private final double highestFreqValueFactor;
    private final double highestFreqInputFactor;

    public NoiseGenerator3(RandomSource randomsource, List<Integer> list) {
        this(randomsource, (IntSortedSet) (new IntRBTreeSet(list)));
    }

    private NoiseGenerator3(RandomSource randomsource, IntSortedSet intsortedset) {
        if (intsortedset.isEmpty()) {
            throw new IllegalArgumentException("Need some octaves!");
        } else {
            int i = -intsortedset.firstInt();
            int j = intsortedset.lastInt();
            int k = i + j + 1;

            if (k < 1) {
                throw new IllegalArgumentException("Total number of octaves needs to be >= 1");
            } else {
                NoiseGenerator3Handler noisegenerator3handler = new NoiseGenerator3Handler(randomsource);
                int l = j;

                this.noiseLevels = new NoiseGenerator3Handler[k];
                if (j >= 0 && j < k && intsortedset.contains(0)) {
                    this.noiseLevels[j] = noisegenerator3handler;
                }

                for (int i1 = j + 1; i1 < k; ++i1) {
                    if (i1 >= 0 && intsortedset.contains(l - i1)) {
                        this.noiseLevels[i1] = new NoiseGenerator3Handler(randomsource);
                    } else {
                        randomsource.consumeCount(262);
                    }
                }

                if (j > 0) {
                    long j1 = (long) (noisegenerator3handler.getValue(noisegenerator3handler.xo, noisegenerator3handler.yo, noisegenerator3handler.zo) * 9.223372036854776E18D);
                    SeededRandom seededrandom = new SeededRandom(new LegacyRandomSource(j1));

                    for (int k1 = l - 1; k1 >= 0; --k1) {
                        if (k1 < k && intsortedset.contains(l - k1)) {
                            this.noiseLevels[k1] = new NoiseGenerator3Handler(seededrandom);
                        } else {
                            seededrandom.consumeCount(262);
                        }
                    }
                }

                this.highestFreqInputFactor = Math.pow(2.0D, (double) j);
                this.highestFreqValueFactor = 1.0D / (Math.pow(2.0D, (double) k) - 1.0D);
            }
        }
    }

    public double getValue(double d0, double d1, boolean flag) {
        double d2 = 0.0D;
        double d3 = this.highestFreqInputFactor;
        double d4 = this.highestFreqValueFactor;
        NoiseGenerator3Handler[] anoisegenerator3handler = this.noiseLevels;
        int i = anoisegenerator3handler.length;

        for (int j = 0; j < i; ++j) {
            NoiseGenerator3Handler noisegenerator3handler = anoisegenerator3handler[j];

            if (noisegenerator3handler != null) {
                d2 += noisegenerator3handler.getValue(d0 * d3 + (flag ? noisegenerator3handler.xo : 0.0D), d1 * d3 + (flag ? noisegenerator3handler.yo : 0.0D)) * d4;
            }

            d3 /= 2.0D;
            d4 *= 2.0D;
        }

        return d2;
    }
}
