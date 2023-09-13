package net.minecraft.world.level;

public class GrassColor {

    private static int[] pixels = new int[65536];

    public GrassColor() {}

    public static void init(int[] aint) {
        GrassColor.pixels = aint;
    }

    public static int get(double d0, double d1) {
        d1 *= d0;
        int i = (int) ((1.0D - d0) * 255.0D);
        int j = (int) ((1.0D - d1) * 255.0D);
        int k = j << 8 | i;

        return k >= GrassColor.pixels.length ? -65281 : GrassColor.pixels[k];
    }

    public static int getDefaultColor() {
        return get(0.5D, 1.0D);
    }
}
