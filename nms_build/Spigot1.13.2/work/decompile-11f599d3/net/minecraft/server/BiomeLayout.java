package net.minecraft.server;

import java.util.function.Function;
import java.util.function.Supplier;

public class BiomeLayout<C extends BiomeLayoutConfiguration, T extends WorldChunkManager> {

    public static final BiomeLayout<BiomeLayoutCheckerboardConfiguration, WorldChunkManagerCheckerBoard> a = a("checkerboard", WorldChunkManagerCheckerBoard::new, BiomeLayoutCheckerboardConfiguration::new);
    public static final BiomeLayout<BiomeLayoutFixedConfiguration, WorldChunkManagerHell> b = a("fixed", WorldChunkManagerHell::new, BiomeLayoutFixedConfiguration::new);
    public static final BiomeLayout<BiomeLayoutOverworldConfiguration, WorldChunkManagerOverworld> c = a("vanilla_layered", WorldChunkManagerOverworld::new, BiomeLayoutOverworldConfiguration::new);
    public static final BiomeLayout<BiomeLayoutTheEndConfiguration, WorldChunkManagerTheEnd> d = a("the_end", WorldChunkManagerTheEnd::new, BiomeLayoutTheEndConfiguration::new);
    private final MinecraftKey e;
    private final Function<C, T> f;
    private final Supplier<C> g;

    public static void a() {}

    public BiomeLayout(Function<C, T> function, Supplier<C> supplier, MinecraftKey minecraftkey) {
        this.f = function;
        this.g = supplier;
        this.e = minecraftkey;
    }

    public static <C extends BiomeLayoutConfiguration, T extends WorldChunkManager> BiomeLayout<C, T> a(String s, Function<C, T> function, Supplier<C> supplier) {
        MinecraftKey minecraftkey = new MinecraftKey(s);
        BiomeLayout<C, T> biomelayout = new BiomeLayout<>(function, supplier, minecraftkey);

        IRegistry.BIOME_SOURCE_TYPE.a(minecraftkey, (Object) biomelayout);
        return biomelayout;
    }

    public T a(C c0) {
        return (WorldChunkManager) this.f.apply(c0);
    }

    public C b() {
        return (BiomeLayoutConfiguration) this.g.get();
    }

    public MinecraftKey c() {
        return this.e;
    }
}
