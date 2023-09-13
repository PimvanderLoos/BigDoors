package net.minecraft.util;

public class ColorUtil {

    public ColorUtil() {}

    public static class a {

        public a() {}

        public static int a(int i) {
            return i >>> 24;
        }

        public static int b(int i) {
            return i >> 16 & 255;
        }

        public static int c(int i) {
            return i >> 8 & 255;
        }

        public static int d(int i) {
            return i & 255;
        }

        public static int a(int i, int j, int k, int l) {
            return i << 24 | j << 16 | k << 8 | l;
        }

        public static int a(int i, int j) {
            return a(a(i) * a(j) / 255, b(i) * b(j) / 255, c(i) * c(j) / 255, d(i) * d(j) / 255);
        }
    }
}
