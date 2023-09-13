package net.minecraft.world.level.levelgen.synth;

import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import it.unimi.dsi.fastutil.doubles.DoubleListIterator;
import net.minecraft.world.level.levelgen.RandomSource;

public class NoiseGeneratorNormal {

    private static final double INPUT_FACTOR = 1.0181268882175227D;
    private static final double TARGET_DEVIATION = 0.3333333333333333D;
    private final double valueFactor;
    private final NoiseGeneratorOctaves first;
    private final NoiseGeneratorOctaves second;

    public static NoiseGeneratorNormal a(RandomSource randomsource, int i, double... adouble) {
        return new NoiseGeneratorNormal(randomsource, i, new DoubleArrayList(adouble));
    }

    public static NoiseGeneratorNormal a(RandomSource randomsource, int i, DoubleList doublelist) {
        return new NoiseGeneratorNormal(randomsource, i, doublelist);
    }

    private NoiseGeneratorNormal(RandomSource randomsource, int i, DoubleList doublelist) {
        this.first = NoiseGeneratorOctaves.a(randomsource, i, doublelist);
        this.second = NoiseGeneratorOctaves.a(randomsource, i, doublelist);
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

        this.valueFactor = 0.16666666666666666D / a(k - j);
    }

    private static double a(int i) {
        return 0.1D * (1.0D + 1.0D / (double) (i + 1));
    }

    public double a(double d0, double d1, double d2) {
        double d3 = d0 * 1.0181268882175227D;
        double d4 = d1 * 1.0181268882175227D;
        double d5 = d2 * 1.0181268882175227D;

        return (this.first.a(d0, d1, d2) + this.second.a(d3, d4, d5)) * this.valueFactor;
    }
}
