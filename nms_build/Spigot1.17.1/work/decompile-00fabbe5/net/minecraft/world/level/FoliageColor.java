package net.minecraft.world.level;

public class FoliageColor {

    private static int[] pixels = new int[65536];

    public FoliageColor() {}

    public static void a(int[] aint) {
        FoliageColor.pixels = aint;
    }

    public static int a(double d0, double d1) {
        d1 *= d0;
        int i = (int) ((1.0D - d0) * 255.0D);
        int j = (int) ((1.0D - d1) * 255.0D);
        int k = j << 8 | i;

        return k >= FoliageColor.pixels.length ? c() : FoliageColor.pixels[k];
    }

    public static int a() {
        return 6396257;
    }

    public static int b() {
        return 8431445;
    }

    public static int c() {
        return 4764952;
    }
}
