package net.minecraft.server;

import com.google.common.collect.Sets;
import java.util.Set;

public class Potions {

    private static final Set<PotionRegistry> K;
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

    private static PotionRegistry a(String s) {
        PotionRegistry potionregistry = (PotionRegistry) PotionRegistry.a.get(new MinecraftKey(s));

        if (!Potions.K.add(potionregistry)) {
            throw new IllegalStateException("Invalid Potion requested: " + s);
        } else {
            return potionregistry;
        }
    }

    static {
        if (!DispenserRegistry.a()) {
            throw new RuntimeException("Accessed Potions before Bootstrap!");
        } else {
            K = Sets.newHashSet();
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
            t = a("water_breathing");
            u = a("long_water_breathing");
            v = a("healing");
            w = a("strong_healing");
            x = a("harming");
            y = a("strong_harming");
            z = a("poison");
            A = a("long_poison");
            B = a("strong_poison");
            C = a("regeneration");
            D = a("long_regeneration");
            E = a("strong_regeneration");
            F = a("strength");
            G = a("long_strength");
            H = a("strong_strength");
            I = a("weakness");
            J = a("long_weakness");
            Potions.K.clear();
        }
    }
}
