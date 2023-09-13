package net.minecraft.world.level.levelgen.synth;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import it.unimi.dsi.fastutil.ints.IntBidirectionalIterator;
import it.unimi.dsi.fastutil.ints.IntRBTreeSet;
import it.unimi.dsi.fastutil.ints.IntSortedSet;
import java.util.List;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import net.minecraft.util.MathHelper;
import net.minecraft.world.level.levelgen.SeededRandom;

public class NoiseGeneratorOctaves implements NoiseGenerator {

    private final NoiseGeneratorPerlin[] a;
    private final DoubleList b;
    private final double c;
    private final double d;

    public NoiseGeneratorOctaves(SeededRandom seededrandom, IntStream intstream) {
        this(seededrandom, (List) intstream.boxed().collect(ImmutableList.toImmutableList()));
    }

    public NoiseGeneratorOctaves(SeededRandom seededrandom, List<Integer> list) {
        this(seededrandom, (IntSortedSet) (new IntRBTreeSet(list)));
    }

    public static NoiseGeneratorOctaves a(SeededRandom seededrandom, int i, DoubleList doublelist) {
        return new NoiseGeneratorOctaves(seededrandom, Pair.of(i, doublelist));
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

    private NoiseGeneratorOctaves(SeededRandom seededrandom, IntSortedSet intsortedset) {
        this(seededrandom, a(intsortedset));
    }

    private NoiseGeneratorOctaves(SeededRandom seededrandom, Pair<Integer, DoubleList> pair) {
        int i = (Integer) pair.getFirst();

        this.b = (DoubleList) pair.getSecond();
        NoiseGeneratorPerlin noisegeneratorperlin = new NoiseGeneratorPerlin(seededrandom);
        int j = this.b.size();
        int k = -i;

        this.a = new NoiseGeneratorPerlin[j];
        if (k >= 0 && k < j) {
            double d0 = this.b.getDouble(k);

            if (d0 != 0.0D) {
                this.a[k] = noisegeneratorperlin;
            }
        }

        for (int l = k - 1; l >= 0; --l) {
            if (l < j) {
                double d1 = this.b.getDouble(l);

                if (d1 != 0.0D) {
                    this.a[l] = new NoiseGeneratorPerlin(seededrandom);
                } else {
                    seededrandom.a(262);
                }
            } else {
                seededrandom.a(262);
            }
        }

        if (k < j - 1) {
            long i1 = (long) (noisegeneratorperlin.a(0.0D, 0.0D, 0.0D, 0.0D, 0.0D) * 9.223372036854776E18D);
            SeededRandom seededrandom1 = new SeededRandom(i1);

            for (int j1 = k + 1; j1 < j; ++j1) {
                if (j1 >= 0) {
                    double d2 = this.b.getDouble(j1);

                    if (d2 != 0.0D) {
                        this.a[j1] = new NoiseGeneratorPerlin(seededrandom1);
                    } else {
                        seededrandom1.a(262);
                    }
                } else {
                    seededrandom1.a(262);
                }
            }
        }

        this.d = Math.pow(2.0D, (double) (-k));
        this.c = Math.pow(2.0D, (double) (j - 1)) / (Math.pow(2.0D, (double) j) - 1.0D);
    }

    public double a(double d0, double d1, double d2) {
        return this.a(d0, d1, d2, 0.0D, 0.0D, false);
    }

    public double a(double d0, double d1, double d2, double d3, double d4, boolean flag) {
        double d5 = 0.0D;
        double d6 = this.d;
        double d7 = this.c;

        for (int i = 0; i < this.a.length; ++i) {
            NoiseGeneratorPerlin noisegeneratorperlin = this.a[i];

            if (noisegeneratorperlin != null) {
                d5 += this.b.getDouble(i) * noisegeneratorperlin.a(a(d0 * d6), flag ? -noisegeneratorperlin.b : a(d1 * d6), a(d2 * d6), d3 * d6, d4 * d6) * d7;
            }

            d6 *= 2.0D;
            d7 /= 2.0D;
        }

        return d5;
    }

    @Nullable
    public NoiseGeneratorPerlin a(int i) {
        return this.a[this.a.length - 1 - i];
    }

    public static double a(double d0) {
        return d0 - (double) MathHelper.d(d0 / 3.3554432E7D + 0.5D) * 3.3554432E7D;
    }

    @Override
    public double a(double d0, double d1, double d2, double d3) {
        return this.a(d0, d1, 0.0D, d2, d3, false);
    }
}
