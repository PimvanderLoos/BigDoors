package net.minecraft.world.level.levelgen.synth;

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.ints.IntRBTreeSet;
import it.unimi.dsi.fastutil.ints.IntSortedSet;
import java.util.List;
import java.util.stream.IntStream;
import net.minecraft.world.level.levelgen.SeededRandom;

public class NoiseGenerator3 implements NoiseGenerator {

    private final NoiseGenerator3Handler[] a;
    private final double b;
    private final double c;

    public NoiseGenerator3(SeededRandom seededrandom, IntStream intstream) {
        this(seededrandom, (List) intstream.boxed().collect(ImmutableList.toImmutableList()));
    }

    public NoiseGenerator3(SeededRandom seededrandom, List<Integer> list) {
        this(seededrandom, (IntSortedSet) (new IntRBTreeSet(list)));
    }

    private NoiseGenerator3(SeededRandom seededrandom, IntSortedSet intsortedset) {
        if (intsortedset.isEmpty()) {
            throw new IllegalArgumentException("Need some octaves!");
        } else {
            int i = -intsortedset.firstInt();
            int j = intsortedset.lastInt();
            int k = i + j + 1;

            if (k < 1) {
                throw new IllegalArgumentException("Total number of octaves needs to be >= 1");
            } else {
                NoiseGenerator3Handler noisegenerator3handler = new NoiseGenerator3Handler(seededrandom);
                int l = j;

                this.a = new NoiseGenerator3Handler[k];
                if (j >= 0 && j < k && intsortedset.contains(0)) {
                    this.a[j] = noisegenerator3handler;
                }

                for (int i1 = j + 1; i1 < k; ++i1) {
                    if (i1 >= 0 && intsortedset.contains(l - i1)) {
                        this.a[i1] = new NoiseGenerator3Handler(seededrandom);
                    } else {
                        seededrandom.a(262);
                    }
                }

                if (j > 0) {
                    long j1 = (long) (noisegenerator3handler.a(noisegenerator3handler.b, noisegenerator3handler.c, noisegenerator3handler.d) * 9.223372036854776E18D);
                    SeededRandom seededrandom1 = new SeededRandom(j1);

                    for (int k1 = l - 1; k1 >= 0; --k1) {
                        if (k1 < k && intsortedset.contains(l - k1)) {
                            this.a[k1] = new NoiseGenerator3Handler(seededrandom1);
                        } else {
                            seededrandom1.a(262);
                        }
                    }
                }

                this.c = Math.pow(2.0D, (double) j);
                this.b = 1.0D / (Math.pow(2.0D, (double) k) - 1.0D);
            }
        }
    }

    public double a(double d0, double d1, boolean flag) {
        double d2 = 0.0D;
        double d3 = this.c;
        double d4 = this.b;
        NoiseGenerator3Handler[] anoisegenerator3handler = this.a;
        int i = anoisegenerator3handler.length;

        for (int j = 0; j < i; ++j) {
            NoiseGenerator3Handler noisegenerator3handler = anoisegenerator3handler[j];

            if (noisegenerator3handler != null) {
                d2 += noisegenerator3handler.a(d0 * d3 + (flag ? noisegenerator3handler.b : 0.0D), d1 * d3 + (flag ? noisegenerator3handler.c : 0.0D)) * d4;
            }

            d3 /= 2.0D;
            d4 *= 2.0D;
        }

        return d2;
    }

    @Override
    public double a(double d0, double d1, double d2, double d3) {
        return this.a(d0, d1, true) * 0.55D;
    }
}
