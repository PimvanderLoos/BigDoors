package net.minecraft.server;

public abstract class Biomes {

    public static final BiomeBase a;
    public static final BiomeBase b;
    public static final BiomeBase c;
    public static final BiomeBase d;
    public static final BiomeBase e;
    public static final BiomeBase f;
    public static final BiomeBase g;
    public static final BiomeBase h;
    public static final BiomeBase i;
    public static final BiomeBase j;
    public static final BiomeBase k;
    public static final BiomeBase l;
    public static final BiomeBase m;
    public static final BiomeBase n;
    public static final BiomeBase o;
    public static final BiomeBase p;
    public static final BiomeBase q;
    public static final BiomeBase r;
    public static final BiomeBase s;
    public static final BiomeBase t;
    public static final BiomeBase u;
    public static final BiomeBase v;
    public static final BiomeBase w;
    public static final BiomeBase x;
    public static final BiomeBase y;
    public static final BiomeBase z;
    public static final BiomeBase A;
    public static final BiomeBase B;
    public static final BiomeBase C;
    public static final BiomeBase D;
    public static final BiomeBase E;
    public static final BiomeBase F;
    public static final BiomeBase G;
    public static final BiomeBase H;
    public static final BiomeBase I;
    public static final BiomeBase J;
    public static final BiomeBase K;
    public static final BiomeBase L;
    public static final BiomeBase M;
    public static final BiomeBase N;
    public static final BiomeBase O;
    public static final BiomeBase P;
    public static final BiomeBase Q;
    public static final BiomeBase R;
    public static final BiomeBase S;
    public static final BiomeBase T;
    public static final BiomeBase U;
    public static final BiomeBase V;
    public static final BiomeBase W;
    public static final BiomeBase X;
    public static final BiomeBase Y;
    public static final BiomeBase Z;
    public static final BiomeBase aa;
    public static final BiomeBase ab;
    public static final BiomeBase ac;
    public static final BiomeBase ad;
    public static final BiomeBase ae;
    public static final BiomeBase af;
    public static final BiomeBase ag;
    public static final BiomeBase ah;
    public static final BiomeBase ai;
    public static final BiomeBase aj;
    public static final BiomeBase ak;
    public static final BiomeBase al;
    public static final BiomeBase am;
    public static final BiomeBase an;
    public static final BiomeBase ao;
    public static final BiomeBase ap;
    public static final BiomeBase aq;
    public static final BiomeBase ar;
    public static final BiomeBase as;
    public static final BiomeBase at;
    public static final BiomeBase au;
    public static final BiomeBase av;

    private static BiomeBase a(String s) {
        BiomeBase biomebase = (BiomeBase) IRegistry.BIOME.get(new MinecraftKey(s));

        if (biomebase == null) {
            throw new IllegalStateException("Invalid Biome requested: " + s);
        } else {
            return biomebase;
        }
    }

    static {
        if (!DispenserRegistry.a()) {
            throw new RuntimeException("Accessed Biomes before Bootstrap!");
        } else {
            a = a("ocean");
            b = Biomes.a;
            c = a("plains");
            d = a("desert");
            e = a("mountains");
            f = a("forest");
            g = a("taiga");
            h = a("swamp");
            i = a("river");
            j = a("nether");
            k = a("the_end");
            l = a("frozen_ocean");
            m = a("frozen_river");
            n = a("snowy_tundra");
            o = a("snowy_mountains");
            p = a("mushroom_fields");
            q = a("mushroom_field_shore");
            r = a("beach");
            s = a("desert_hills");
            t = a("wooded_hills");
            u = a("taiga_hills");
            v = a("mountain_edge");
            w = a("jungle");
            x = a("jungle_hills");
            y = a("jungle_edge");
            z = a("deep_ocean");
            A = a("stone_shore");
            B = a("snowy_beach");
            C = a("birch_forest");
            D = a("birch_forest_hills");
            E = a("dark_forest");
            F = a("snowy_taiga");
            G = a("snowy_taiga_hills");
            H = a("giant_tree_taiga");
            I = a("giant_tree_taiga_hills");
            J = a("wooded_mountains");
            K = a("savanna");
            L = a("savanna_plateau");
            M = a("badlands");
            N = a("wooded_badlands_plateau");
            O = a("badlands_plateau");
            P = a("small_end_islands");
            Q = a("end_midlands");
            R = a("end_highlands");
            S = a("end_barrens");
            T = a("warm_ocean");
            U = a("lukewarm_ocean");
            V = a("cold_ocean");
            W = a("deep_warm_ocean");
            X = a("deep_lukewarm_ocean");
            Y = a("deep_cold_ocean");
            Z = a("deep_frozen_ocean");
            aa = a("the_void");
            ab = a("sunflower_plains");
            ac = a("desert_lakes");
            ad = a("gravelly_mountains");
            ae = a("flower_forest");
            af = a("taiga_mountains");
            ag = a("swamp_hills");
            ah = a("ice_spikes");
            ai = a("modified_jungle");
            aj = a("modified_jungle_edge");
            ak = a("tall_birch_forest");
            al = a("tall_birch_hills");
            am = a("dark_forest_hills");
            an = a("snowy_taiga_mountains");
            ao = a("giant_spruce_taiga");
            ap = a("giant_spruce_taiga_hills");
            aq = a("modified_gravelly_mountains");
            ar = a("shattered_savanna");
            as = a("shattered_savanna_plateau");
            at = a("eroded_badlands");
            au = a("modified_wooded_badlands_plateau");
            av = a("modified_badlands_plateau");
        }
    }
}
