package net.minecraft.server;

import java.util.function.Supplier;

public enum DimensionManager {

    OVERWORLD(0, "overworld", "", WorldProviderNormal::new), NETHER(-1, "the_nether", "_nether", WorldProviderHell::new), THE_END(1, "the_end", "_end", WorldProviderTheEnd::new);

    private final int d;
    private final String e;
    private final String f;
    private final Supplier<? extends WorldProvider> g;

    private DimensionManager(int i, String s, String s1, Supplier supplier) {
        this.d = i;
        this.e = s;
        this.f = s1;
        this.g = supplier;
    }

    public int getDimensionID() {
        return this.d;
    }

    public String b() {
        return this.e;
    }

    public String c() {
        return this.f;
    }

    public WorldProvider d() {
        return (WorldProvider) this.g.get();
    }

    public static DimensionManager a(int i) {
        DimensionManager[] adimensionmanager = values();
        int j = adimensionmanager.length;

        for (int k = 0; k < j; ++k) {
            DimensionManager dimensionmanager = adimensionmanager[k];

            if (dimensionmanager.getDimensionID() == i) {
                return dimensionmanager;
            }
        }

        throw new IllegalArgumentException("Invalid dimension id " + i);
    }

    public static DimensionManager a(String s) {
        DimensionManager[] adimensionmanager = values();
        int i = adimensionmanager.length;

        for (int j = 0; j < i; ++j) {
            DimensionManager dimensionmanager = adimensionmanager[j];

            if (dimensionmanager.b().equals(s)) {
                return dimensionmanager;
            }
        }

        throw new IllegalArgumentException("Invalid dimension " + s);
    }
}
