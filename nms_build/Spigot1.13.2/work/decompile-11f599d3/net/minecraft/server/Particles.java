package net.minecraft.server;

public class Particles {

    public static final ParticleType a;
    public static final ParticleType b;
    public static final ParticleType c;
    public static final Particle<ParticleParamBlock> d;
    public static final ParticleType e;
    public static final ParticleType f;
    public static final ParticleType g;
    public static final ParticleType h;
    public static final ParticleType i;
    public static final ParticleType j;
    public static final ParticleType k;
    public static final ParticleType l;
    public static final Particle<ParticleParamRedstone> m;
    public static final ParticleType n;
    public static final ParticleType o;
    public static final ParticleType p;
    public static final ParticleType q;
    public static final ParticleType r;
    public static final ParticleType s;
    public static final ParticleType t;
    public static final ParticleType u;
    public static final Particle<ParticleParamBlock> v;
    public static final ParticleType w;
    public static final ParticleType x;
    public static final ParticleType y;
    public static final ParticleType z;
    public static final ParticleType A;
    public static final ParticleType B;
    public static final Particle<ParticleParamItem> C;
    public static final ParticleType D;
    public static final ParticleType E;
    public static final ParticleType F;
    public static final ParticleType G;
    public static final ParticleType H;
    public static final ParticleType I;
    public static final ParticleType J;
    public static final ParticleType K;
    public static final ParticleType L;
    public static final ParticleType M;
    public static final ParticleType N;
    public static final ParticleType O;
    public static final ParticleType P;
    public static final ParticleType Q;
    public static final ParticleType R;
    public static final ParticleType S;
    public static final ParticleType T;
    public static final ParticleType U;
    public static final ParticleType V;
    public static final ParticleType W;
    public static final ParticleType X;

    private static <T extends Particle<?>> T a(String s) {
        T t0 = (Particle) IRegistry.PARTICLE_TYPE.get(new MinecraftKey(s));

        if (t0 == null) {
            throw new IllegalStateException("Invalid or unknown particle type: " + s);
        } else {
            return t0;
        }
    }

    static {
        if (!DispenserRegistry.a()) {
            throw new RuntimeException("Accessed particles before Bootstrap!");
        } else {
            a = (ParticleType) a("ambient_entity_effect");
            b = (ParticleType) a("angry_villager");
            c = (ParticleType) a("barrier");
            d = a("block");
            e = (ParticleType) a("bubble");
            f = (ParticleType) a("bubble_column_up");
            g = (ParticleType) a("cloud");
            h = (ParticleType) a("crit");
            i = (ParticleType) a("damage_indicator");
            j = (ParticleType) a("dragon_breath");
            k = (ParticleType) a("dripping_lava");
            l = (ParticleType) a("dripping_water");
            m = a("dust");
            n = (ParticleType) a("effect");
            o = (ParticleType) a("elder_guardian");
            p = (ParticleType) a("enchanted_hit");
            q = (ParticleType) a("enchant");
            r = (ParticleType) a("end_rod");
            s = (ParticleType) a("entity_effect");
            t = (ParticleType) a("explosion_emitter");
            u = (ParticleType) a("explosion");
            v = a("falling_dust");
            w = (ParticleType) a("firework");
            x = (ParticleType) a("fishing");
            y = (ParticleType) a("flame");
            z = (ParticleType) a("happy_villager");
            A = (ParticleType) a("heart");
            B = (ParticleType) a("instant_effect");
            C = a("item");
            D = (ParticleType) a("item_slime");
            E = (ParticleType) a("item_snowball");
            F = (ParticleType) a("large_smoke");
            G = (ParticleType) a("lava");
            H = (ParticleType) a("mycelium");
            I = (ParticleType) a("note");
            J = (ParticleType) a("poof");
            K = (ParticleType) a("portal");
            L = (ParticleType) a("rain");
            M = (ParticleType) a("smoke");
            N = (ParticleType) a("spit");
            O = (ParticleType) a("sweep_attack");
            P = (ParticleType) a("totem_of_undying");
            Q = (ParticleType) a("underwater");
            R = (ParticleType) a("splash");
            S = (ParticleType) a("witch");
            T = (ParticleType) a("bubble_pop");
            U = (ParticleType) a("current_down");
            V = (ParticleType) a("squid_ink");
            W = (ParticleType) a("nautilus");
            X = (ParticleType) a("dolphin");
        }
    }
}
