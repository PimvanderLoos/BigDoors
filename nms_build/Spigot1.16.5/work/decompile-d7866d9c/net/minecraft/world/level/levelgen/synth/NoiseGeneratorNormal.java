package net.minecraft.world.level.levelgen.synth;

import it.unimi.dsi.fastutil.doubles.DoubleList;
import it.unimi.dsi.fastutil.doubles.DoubleListIterator;
import net.minecraft.world.level.levelgen.SeededRandom;

public class NoiseGeneratorNormal {

    private final double a;
    private final NoiseGeneratorOctaves b;
    private final NoiseGeneratorOctaves c;

    public static NoiseGeneratorNormal a(SeededRandom seededrandom, int i, DoubleList doublelist) {
        return new NoiseGeneratorNormal(seededrandom, i, doublelist);
    }

    private NoiseGeneratorNormal(SeededRandom seededrandom, int i, DoubleList doublelist) {
        this.b = NoiseGeneratorOctaves.a(seededrandom, i, doublelist);
        this.c = NoiseGeneratorOctaves.a(seededrandom, i, doublelist);
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

        this.a = 0.16666666666666666D / a(k - j);
    }

    private static double a(int i) {
        return 0.1D * (1.0D + 1.0D / (double) (i + 1));
    }

    public double a(double d0, double d1, double d2) {
        double d3 = d0 * 1.0181268882175227D;
        double d4 = d1 * 1.0181268882175227D;
        double d5 = d2 * 1.0181268882175227D;

        return (this.b.a(d0, d1, d2) + this.c.a(d3, d4, d5)) * this.a;
    }
}
