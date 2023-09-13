package net.minecraft.world.level.levelgen.synth;

import net.minecraft.util.MathHelper;

public class NoiseUtils {

    public NoiseUtils() {}

    public static double a(NoiseGeneratorNormal noisegeneratornormal, double d0, double d1, double d2, double d3, double d4) {
        double d5 = noisegeneratornormal.a(d0, d1, d2);

        return MathHelper.b(d5, -1.0D, 1.0D, d3, d4);
    }

    public static double a(double d0, double d1) {
        return d0 + Math.sin(3.141592653589793D * d0) * d1 / 3.141592653589793D;
    }
}
