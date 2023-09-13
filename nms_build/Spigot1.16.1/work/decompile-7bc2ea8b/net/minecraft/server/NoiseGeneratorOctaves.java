package net.minecraft.server;

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.ints.IntRBTreeSet;
import it.unimi.dsi.fastutil.ints.IntSortedSet;
import java.util.List;
import java.util.stream.IntStream;
import javax.annotation.Nullable;

public class NoiseGeneratorOctaves implements NoiseGenerator {

    private final NoiseGeneratorPerlin[] a;
    private final double b;
    private final double c;

    public NoiseGeneratorOctaves(SeededRandom seededrandom, IntStream intstream) {
        this(seededrandom, (List) intstream.boxed().collect(ImmutableList.toImmutableList()));
    }

    public NoiseGeneratorOctaves(SeededRandom seededrandom, List<Integer> list) {
        this(seededrandom, (IntSortedSet) (new IntRBTreeSet(list)));
    }

    private NoiseGeneratorOctaves(SeededRandom seededrandom, IntSortedSet intsortedset) {
        if (intsortedset.isEmpty()) {
            throw new IllegalArgumentException("Need some octaves!");
        } else {
            int i = -intsortedset.firstInt();
            int j = intsortedset.lastInt();
            int k = i + j + 1;

            if (k < 1) {
                throw new IllegalArgumentException("Total number of octaves needs to be >= 1");
            } else {
                NoiseGeneratorPerlin noisegeneratorperlin = new NoiseGeneratorPerlin(seededrandom);
                int l = j;

                this.a = new NoiseGeneratorPerlin[k];
                if (j >= 0 && j < k && intsortedset.contains(0)) {
                    this.a[j] = noisegeneratorperlin;
                }

                for (int i1 = j + 1; i1 < k; ++i1) {
                    if (i1 >= 0 && intsortedset.contains(l - i1)) {
                        this.a[i1] = new NoiseGeneratorPerlin(seededrandom);
                    } else {
                        seededrandom.a(262);
                    }
                }

                if (j > 0) {
                    long j1 = (long) (noisegeneratorperlin.a(0.0D, 0.0D, 0.0D, 0.0D, 0.0D) * 9.223372036854776E18D);
                    SeededRandom seededrandom1 = new SeededRandom(j1);

                    for (int k1 = l - 1; k1 >= 0; --k1) {
                        if (k1 < k && intsortedset.contains(l - k1)) {
                            this.a[k1] = new NoiseGeneratorPerlin(seededrandom1);
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

    public double a(double d0, double d1, double d2) {
        return this.a(d0, d1, d2, 0.0D, 0.0D, false);
    }

    public double a(double d0, double d1, double d2, double d3, double d4, boolean flag) {
        double d5 = 0.0D;
        double d6 = this.c;
        double d7 = this.b;
        NoiseGeneratorPerlin[] anoisegeneratorperlin = this.a;
        int i = anoisegeneratorperlin.length;

        for (int j = 0; j < i; ++j) {
            NoiseGeneratorPerlin noisegeneratorperlin = anoisegeneratorperlin[j];

            if (noisegeneratorperlin != null) {
                d5 += noisegeneratorperlin.a(a(d0 * d6), flag ? -noisegeneratorperlin.b : a(d1 * d6), a(d2 * d6), d3 * d6, d4 * d6) * d7;
            }

            d6 /= 2.0D;
            d7 *= 2.0D;
        }

        return d5;
    }

    @Nullable
    public NoiseGeneratorPerlin a(int i) {
        return this.a[i];
    }

    public static double a(double d0) {
        return d0 - (double) MathHelper.d(d0 / 3.3554432E7D + 0.5D) * 3.3554432E7D;
    }

    @Override
    public double a(double d0, double d1, double d2, double d3) {
        return this.a(d0, d1, 0.0D, d2, d3, false);
    }
}
