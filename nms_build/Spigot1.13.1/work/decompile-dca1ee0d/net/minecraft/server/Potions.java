package net.minecraft.server;

import com.google.common.collect.Sets;
import java.util.Set;

public class Potions {

    private static final Set<PotionRegistry> Q;
    public static final PotionRegistry EMPTY;
    public static final PotionRegistry b;
    public static final PotionRegistry c;
    public static final PotionRegistry d;
    public static final PotionRegistry e;
    public static final PotionRegistry f;
    public static final PotionRegistry g;
    public static final PotionRegistry h;
    public static final PotionRegistry i;
    public static final PotionRegistry j;
    public static final PotionRegistry k;
    public static final PotionRegistry l;
    public static final PotionRegistry m;
    public static final PotionRegistry n;
    public static final PotionRegistry o;
    public static final PotionRegistry p;
    public static final PotionRegistry q;
    public static final PotionRegistry r;
    public static final PotionRegistry s;
    public static final PotionRegistry t;
    public static final PotionRegistry u;
    public static final PotionRegistry v;
    public static final PotionRegistry w;
    public static final PotionRegistry x;
    public static final PotionRegistry y;
    public static final PotionRegistry z;
    public static final PotionRegistry A;
    public static final PotionRegistry B;
    public static final PotionRegistry C;
    public static final PotionRegistry D;
    public static final PotionRegistry E;
    public static final PotionRegistry F;
    public static final PotionRegistry G;
    public static final PotionRegistry H;
    public static final PotionRegistry I;
    public static final PotionRegistry J;
    public static final PotionRegistry K;
    public static final PotionRegistry L;
    public static final PotionRegistry M;
    public static final PotionRegistry N;
    public static final PotionRegistry O;
    public static final PotionRegistry P;

    private static PotionRegistry a(String s) {
        PotionRegistry potionregistry = (PotionRegistry) IRegistry.POTION.getOrDefault(new MinecraftKey(s));

        if (!Potions.Q.add(potionregistry)) {
            throw new IllegalStateException("Invalid Potion requested: " + s);
        } else {
            return potionregistry;
        }
    }

    static {
        if (!DispenserRegistry.a()) {
            throw new RuntimeException("Accessed Potions before Bootstrap!");
        } else {
            Q = Sets.newHashSet(new PotionRegistry[] { (PotionRegistry) null});
            EMPTY = a("empty");
            b = a("water");
            c = a("mundane");
            d = a("thick");
            e = a("awkward");
            f = a("night_vision");
            g = a("long_night_vision");
            h = a("invisibility");
            i = a("long_invisibility");
            j = a("leaping");
            k = a("long_leaping");
            l = a("strong_leaping");
            m = a("fire_resistance");
            n = a("long_fire_resistance");
            o = a("swiftness");
            p = a("long_swiftness");
            q = a("strong_swiftness");
            r = a("slowness");
            s = a("long_slowness");
            t = a("strong_slowness");
            u = a("turtle_master");
            v = a("long_turtle_master");
            w = a("strong_turtle_master");
            x = a("water_breathing");
            y = a("long_water_breathing");
            z = a("healing");
            A = a("strong_healing");
            B = a("harming");
            C = a("strong_harming");
            D = a("poison");
            E = a("long_poison");
            F = a("strong_poison");
            G = a("regeneration");
            H = a("long_regeneration");
            I = a("strong_regeneration");
            J = a("strength");
            K = a("long_strength");
            L = a("strong_strength");
            M = a("weakness");
            N = a("long_weakness");
            O = a("slow_falling");
            P = a("long_slow_falling");
            Potions.Q.clear();
        }
    }
}
