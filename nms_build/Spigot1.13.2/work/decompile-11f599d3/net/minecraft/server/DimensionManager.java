package net.minecraft.server;

import java.io.File;
import java.util.function.Supplier;
import javax.annotation.Nullable;

public class DimensionManager {

    public static final DimensionManager OVERWORLD = a("overworld", new DimensionManager(1, "", "", WorldProviderNormal::new));
    public static final DimensionManager NETHER = a("the_nether", new DimensionManager(0, "_nether", "DIM-1", WorldProviderHell::new));
    public static final DimensionManager THE_END = a("the_end", new DimensionManager(2, "_end", "DIM1", WorldProviderTheEnd::new));
    private final int d;
    private final String e;
    private final String f;
    private final Supplier<? extends WorldProvider> g;

    public static void a() {}

    private static DimensionManager a(String s, DimensionManager dimensionmanager) {
        IRegistry.DIMENSION_TYPE.a(dimensionmanager.d, new MinecraftKey(s), dimensionmanager);
        return dimensionmanager;
    }

    public DimensionManager(int i, String s, String s1, Supplier<? extends WorldProvider> supplier) {
        this.d = i;
        this.e = s;
        this.f = s1;
        this.g = supplier;
    }

    public static Iterable<DimensionManager> b() {
        return IRegistry.DIMENSION_TYPE;
    }

    public int getDimensionID() {
        return this.d + -1;
    }

    public String d() {
        return this.e;
    }

    public File a(File file) {
        return this.f.isEmpty() ? file : new File(file, this.f);
    }

    public WorldProvider e() {
        return (WorldProvider) this.g.get();
    }

    public String toString() {
        return a(this).toString();
    }

    @Nullable
    public static DimensionManager a(int i) {
        return (DimensionManager) IRegistry.DIMENSION_TYPE.fromId(i - -1);
    }

    @Nullable
    public static DimensionManager a(MinecraftKey minecraftkey) {
        return (DimensionManager) IRegistry.DIMENSION_TYPE.get(minecraftkey);
    }

    @Nullable
    public static MinecraftKey a(DimensionManager dimensionmanager) {
        return IRegistry.DIMENSION_TYPE.getKey(dimensionmanager);
    }
}
