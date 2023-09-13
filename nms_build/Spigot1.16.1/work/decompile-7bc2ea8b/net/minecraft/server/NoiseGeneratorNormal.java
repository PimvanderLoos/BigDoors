package net.minecraft.server;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.stream.IntStream;

public class NoiseGeneratorNormal {

    private final double a;
    private final NoiseGeneratorOctaves b;
    private final NoiseGeneratorOctaves c;

    public NoiseGeneratorNormal(SeededRandom seededrandom, IntStream intstream) {
        this(seededrandom, (List) intstream.boxed().collect(ImmutableList.toImmutableList()));
    }

    public NoiseGeneratorNormal(SeededRandom seededrandom, List<Integer> list) {
        this.b = new NoiseGeneratorOctaves(seededrandom, list);
        this.c = new NoiseGeneratorOctaves(seededrandom, list);
        int i = (Integer) list.stream().min(Integer::compareTo).orElse(0);
        int j = (Integer) list.stream().max(Integer::compareTo).orElse(0);

        this.a = 0.16666666666666666D / a(j - i);
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
