package net.minecraft.server;

public class Paintings {

    public static final Paintings a = a("kebab", 16, 16, 0, 0);
    public static final Paintings b = a("aztec", 16, 16, 16, 0);
    public static final Paintings c = a("alban", 16, 16, 32, 0);
    public static final Paintings d = a("aztec2", 16, 16, 48, 0);
    public static final Paintings e = a("bomb", 16, 16, 64, 0);
    public static final Paintings f = a("plant", 16, 16, 80, 0);
    public static final Paintings g = a("wasteland", 16, 16, 96, 0);
    public static final Paintings h = a("pool", 32, 16, 0, 32);
    public static final Paintings i = a("courbet", 32, 16, 32, 32);
    public static final Paintings j = a("sea", 32, 16, 64, 32);
    public static final Paintings k = a("sunset", 32, 16, 96, 32);
    public static final Paintings l = a("creebet", 32, 16, 128, 32);
    public static final Paintings m = a("wanderer", 16, 32, 0, 64);
    public static final Paintings n = a("graham", 16, 32, 16, 64);
    public static final Paintings o = a("match", 32, 32, 0, 128);
    public static final Paintings p = a("bust", 32, 32, 32, 128);
    public static final Paintings q = a("stage", 32, 32, 64, 128);
    public static final Paintings r = a("void", 32, 32, 96, 128);
    public static final Paintings s = a("skull_and_roses", 32, 32, 128, 128);
    public static final Paintings t = a("wither", 32, 32, 160, 128);
    public static final Paintings u = a("fighters", 64, 32, 0, 96);
    public static final Paintings v = a("pointer", 64, 64, 0, 192);
    public static final Paintings w = a("pigscene", 64, 64, 64, 192);
    public static final Paintings x = a("burning_skull", 64, 64, 128, 192);
    public static final Paintings y = a("skeleton", 64, 48, 192, 64);
    public static final Paintings z = a("donkey_kong", 64, 48, 192, 112);
    private final int A;
    private final int B;
    private final int C;
    private final int D;

    public static void a() {}

    public Paintings(int i, int j, int k, int l) {
        this.A = i;
        this.B = j;
        this.C = k;
        this.D = l;
    }

    public int b() {
        return this.A;
    }

    public int c() {
        return this.B;
    }

    public static Paintings a(String s, int i, int j, int k, int l) {
        Paintings paintings = new Paintings(i, j, k, l);

        IRegistry.MOTIVE.a(new MinecraftKey(s), (Object) paintings);
        return paintings;
    }
}
