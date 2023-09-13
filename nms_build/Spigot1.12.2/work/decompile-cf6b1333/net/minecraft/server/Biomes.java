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

    private static BiomeBase a(String s) {
        BiomeBase biomebase = (BiomeBase) BiomeBase.REGISTRY_ID.get(new MinecraftKey(s));

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
            e = a("extreme_hills");
            f = a("forest");
            g = a("taiga");
            h = a("swampland");
            i = a("river");
            j = a("hell");
            k = a("sky");
            l = a("frozen_ocean");
            m = a("frozen_river");
            n = a("ice_flats");
            o = a("ice_mountains");
            p = a("mushroom_island");
            q = a("mushroom_island_shore");
            r = a("beaches");
            s = a("desert_hills");
            t = a("forest_hills");
            u = a("taiga_hills");
            v = a("smaller_extreme_hills");
            w = a("jungle");
            x = a("jungle_hills");
            y = a("jungle_edge");
            z = a("deep_ocean");
            A = a("stone_beach");
            B = a("cold_beach");
            C = a("birch_forest");
            D = a("birch_forest_hills");
            E = a("roofed_forest");
            F = a("taiga_cold");
            G = a("taiga_cold_hills");
            H = a("redwood_taiga");
            I = a("redwood_taiga_hills");
            J = a("extreme_hills_with_trees");
            K = a("savanna");
            L = a("savanna_rock");
            M = a("mesa");
            N = a("mesa_rock");
            O = a("mesa_clear_rock");
            P = a("void");
            Q = a("mutated_plains");
            R = a("mutated_desert");
            S = a("mutated_extreme_hills");
            T = a("mutated_forest");
            U = a("mutated_taiga");
            V = a("mutated_swampland");
            W = a("mutated_ice_flats");
            X = a("mutated_jungle");
            Y = a("mutated_jungle_edge");
            Z = a("mutated_birch_forest");
            aa = a("mutated_birch_forest_hills");
            ab = a("mutated_roofed_forest");
            ac = a("mutated_taiga_cold");
            ad = a("mutated_redwood_taiga");
            ae = a("mutated_redwood_taiga_hills");
            af = a("mutated_extreme_hills_with_trees");
            ag = a("mutated_savanna");
            ah = a("mutated_savanna_rock");
            ai = a("mutated_mesa");
            aj = a("mutated_mesa_rock");
            ak = a("mutated_mesa_clear_rock");
        }
    }
}
