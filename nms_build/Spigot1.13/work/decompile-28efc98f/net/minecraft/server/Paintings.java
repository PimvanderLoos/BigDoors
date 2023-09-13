package net.minecraft.server;

public class Paintings {

    public static final RegistryBlocks<MinecraftKey, Paintings> a = new RegistryBlocks(new MinecraftKey("kebab"));
    public static final Paintings b = a("kebab", 16, 16, 0, 0);
    public static final Paintings c = a("aztec", 16, 16, 16, 0);
    public static final Paintings d = a("alban", 16, 16, 32, 0);
    public static final Paintings e = a("aztec2", 16, 16, 48, 0);
    public static final Paintings f = a("bomb", 16, 16, 64, 0);
    public static final Paintings g = a("plant", 16, 16, 80, 0);
    public static final Paintings h = a("wasteland", 16, 16, 96, 0);
    public static final Paintings i = a("pool", 32, 16, 0, 32);
    public static final Paintings j = a("courbet", 32, 16, 32, 32);
    public static final Paintings k = a("sea", 32, 16, 64, 32);
    public static final Paintings l = a("sunset", 32, 16, 96, 32);
    public static final Paintings m = a("creebet", 32, 16, 128, 32);
    public static final Paintings n = a("wanderer", 16, 32, 0, 64);
    public static final Paintings o = a("graham", 16, 32, 16, 64);
    public static final Paintings p = a("match", 32, 32, 0, 128);
    public static final Paintings q = a("bust", 32, 32, 32, 128);
    public static final Paintings r = a("stage", 32, 32, 64, 128);
    public static final Paintings s = a("void", 32, 32, 96, 128);
    public static final Paintings t = a("skull_and_roses", 32, 32, 128, 128);
    public static final Paintings u = a("wither", 32, 32, 160, 128);
    public static final Paintings v = a("fighters", 64, 32, 0, 96);
    public static final Paintings w = a("pointer", 64, 64, 0, 192);
    public static final Paintings x = a("pigscene", 64, 64, 64, 192);
    public static final Paintings y = a("burning_skull", 64, 64, 128, 192);
    public static final Paintings z = a("skeleton", 64, 48, 192, 64);
    public static final Paintings A = a("donkey_kong", 64, 48, 192, 112);
    private final int B;
    private final int C;
    private final int D;
    private final int E;

    public static void a() {
        Paintings.a.a();
    }

    public Paintings(int i, int j, int k, int l) {
        this.B = i;
        this.C = j;
        this.D = k;
        this.E = l;
    }

    public int b() {
        return this.B;
    }

    public int c() {
        return this.C;
    }

    public static Paintings a(String s, int i, int j, int k, int l) {
        Paintings paintings = new Paintings(i, j, k, l);

        Paintings.a.a(new MinecraftKey(s), paintings);
        return paintings;
    }
}
