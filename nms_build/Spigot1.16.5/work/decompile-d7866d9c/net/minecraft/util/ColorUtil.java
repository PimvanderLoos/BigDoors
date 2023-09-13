package net.minecraft.util;

public class ColorUtil {
    public static class a {

        public static int b(int i) {
            return i >> 16 & 255;
        }

        public static int c(int i) {
            return i >> 8 & 255;
        }

        public static int d(int i) {
            return i & 255;
        }
    }
}
