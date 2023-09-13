package net.minecraft.world.level.levelgen.synth;

import java.util.stream.IntStream;
import net.minecraft.util.MathHelper;
import net.minecraft.world.level.levelgen.RandomSource;

public class BlendedNoise {

    private final NoiseGeneratorOctaves minLimitNoise;
    private final NoiseGeneratorOctaves maxLimitNoise;
    private final NoiseGeneratorOctaves mainNoise;

    public BlendedNoise(NoiseGeneratorOctaves noisegeneratoroctaves, NoiseGeneratorOctaves noisegeneratoroctaves1, NoiseGeneratorOctaves noisegeneratoroctaves2) {
        this.minLimitNoise = noisegeneratoroctaves;
        this.maxLimitNoise = noisegeneratoroctaves1;
        this.mainNoise = noisegeneratoroctaves2;
    }

    public BlendedNoise(RandomSource randomsource) {
        this(new NoiseGeneratorOctaves(randomsource, IntStream.rangeClosed(-15, 0)), new NoiseGeneratorOctaves(randomsource, IntStream.rangeClosed(-15, 0)), new NoiseGeneratorOctaves(randomsource, IntStream.rangeClosed(-7, 0)));
    }

    public double a(int i, int j, int k, double d0, double d1, double d2, double d3) {
        double d4 = 0.0D;
        double d5 = 0.0D;
        double d6 = 0.0D;
        boolean flag = true;
        double d7 = 1.0D;

        for (int l = 0; l < 8; ++l) {
            NoiseGeneratorPerlin noisegeneratorperlin = this.mainNoise.a(l);

            if (noisegeneratorperlin != null) {
                d6 += noisegeneratorperlin.a(NoiseGeneratorOctaves.a((double) i * d2 * d7), NoiseGeneratorOctaves.a((double) j * d3 * d7), NoiseGeneratorOctaves.a((double) k * d2 * d7), d3 * d7, (double) j * d3 * d7) / d7;
            }

            d7 /= 2.0D;
        }

        double d8 = (d6 / 10.0D + 1.0D) / 2.0D;
        boolean flag1 = d8 >= 1.0D;
        boolean flag2 = d8 <= 0.0D;

        d7 = 1.0D;

        for (int i1 = 0; i1 < 16; ++i1) {
            double d9 = NoiseGeneratorOctaves.a((double) i * d0 * d7);
            double d10 = NoiseGeneratorOctaves.a((double) j * d1 * d7);
            double d11 = NoiseGeneratorOctaves.a((double) k * d0 * d7);
            double d12 = d1 * d7;
            NoiseGeneratorPerlin noisegeneratorperlin1;

            if (!flag1) {
                noisegeneratorperlin1 = this.minLimitNoise.a(i1);
                if (noisegeneratorperlin1 != null) {
                    d4 += noisegeneratorperlin1.a(d9, d10, d11, d12, (double) j * d12) / d7;
                }
            }

            if (!flag2) {
                noisegeneratorperlin1 = this.maxLimitNoise.a(i1);
                if (noisegeneratorperlin1 != null) {
                    d5 += noisegeneratorperlin1.a(d9, d10, d11, d12, (double) j * d12) / d7;
                }
            }

            d7 /= 2.0D;
        }

        return MathHelper.b(d4 / 512.0D, d5 / 512.0D, d8);
    }
}
