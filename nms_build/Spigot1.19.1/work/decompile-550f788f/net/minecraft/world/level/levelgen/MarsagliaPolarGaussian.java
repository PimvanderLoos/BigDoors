package net.minecraft.world.level.levelgen;

import net.minecraft.util.MathHelper;
import net.minecraft.util.RandomSource;

public class MarsagliaPolarGaussian {

    public final RandomSource randomSource;
    private double nextNextGaussian;
    private boolean haveNextNextGaussian;

    public MarsagliaPolarGaussian(RandomSource randomsource) {
        this.randomSource = randomsource;
    }

    public void reset() {
        this.haveNextNextGaussian = false;
    }

    public double nextGaussian() {
        if (this.haveNextNextGaussian) {
            this.haveNextNextGaussian = false;
            return this.nextNextGaussian;
        } else {
            double d0;
            double d1;
            double d2;

            do {
                do {
                    d0 = 2.0D * this.randomSource.nextDouble() - 1.0D;
                    d1 = 2.0D * this.randomSource.nextDouble() - 1.0D;
                    d2 = MathHelper.square(d0) + MathHelper.square(d1);
                } while (d2 >= 1.0D);
            } while (d2 == 0.0D);

            double d3 = Math.sqrt(-2.0D * Math.log(d2) / d2);

            this.nextNextGaussian = d1 * d3;
            this.haveNextNextGaussian = true;
            return d0 * d3;
        }
    }
}
