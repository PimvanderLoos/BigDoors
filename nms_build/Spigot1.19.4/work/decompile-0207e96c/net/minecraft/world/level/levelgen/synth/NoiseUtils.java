package net.minecraft.world.level.levelgen.synth;

import java.util.Locale;

public class NoiseUtils {

    public NoiseUtils() {}

    public static double biasTowardsExtreme(double d0, double d1) {
        return d0 + Math.sin(3.141592653589793D * d0) * d1 / 3.141592653589793D;
    }

    public static void parityNoiseOctaveConfigString(StringBuilder stringbuilder, double d0, double d1, double d2, byte[] abyte) {
        stringbuilder.append(String.format(Locale.ROOT, "xo=%.3f, yo=%.3f, zo=%.3f, p0=%d, p255=%d", (float) d0, (float) d1, (float) d2, abyte[0], abyte[255]));
    }

    public static void parityNoiseOctaveConfigString(StringBuilder stringbuilder, double d0, double d1, double d2, int[] aint) {
        stringbuilder.append(String.format(Locale.ROOT, "xo=%.3f, yo=%.3f, zo=%.3f, p0=%d, p255=%d", (float) d0, (float) d1, (float) d2, aint[0], aint[255]));
    }
}
