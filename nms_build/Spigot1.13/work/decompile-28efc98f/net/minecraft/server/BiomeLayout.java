package net.minecraft.server;

import java.util.function.Function;
import java.util.function.Supplier;

public class BiomeLayout<C extends BiomeLayoutConfiguration, T extends WorldChunkManager> {

    public static final RegistryMaterials<MinecraftKey, BiomeLayout<?, ?>> a = new RegistryMaterials();
    private final MinecraftKey f;
    private final Function<C, T> g;
    private final BiomeLayout.a<C> h;
    public static final BiomeLayout<BiomeLayoutCheckerboardConfiguration, WorldChunkManagerCheckerBoard> b = a("checkerboard", WorldChunkManagerCheckerBoard::new, BiomeLayout.a.b);
    public static final BiomeLayout<BiomeLayoutFixedConfiguration, WorldChunkManagerHell> c = a("fixed", WorldChunkManagerHell::new, BiomeLayout.a.c);
    public static final BiomeLayout<BiomeLayoutOverworldConfiguration, WorldChunkManagerOverworld> d = a("vanilla_layered", WorldChunkManagerOverworld::new, BiomeLayout.a.d);
    public static final BiomeLayout<BiomeLayoutTheEndConfiguration, WorldChunkManagerTheEnd> e = a("the_end", WorldChunkManagerTheEnd::new, BiomeLayout.a.e);

    public BiomeLayout(Function<C, T> function, BiomeLayout.a<C> biomelayout_a, MinecraftKey minecraftkey) {
        this.g = function;
        this.h = biomelayout_a;
        this.f = minecraftkey;
    }

    public static <C extends BiomeLayoutConfiguration, T extends WorldChunkManager> BiomeLayout<C, T> a(String s, Function<C, T> function, BiomeLayout.a<C> biomelayout_a) {
        MinecraftKey minecraftkey = new MinecraftKey(s);
        BiomeLayout biomelayout = new BiomeLayout(function, biomelayout_a, minecraftkey);

        BiomeLayout.a.a(minecraftkey, biomelayout);
        return biomelayout;
    }

    public T a(C c0) {
        return (WorldChunkManager) this.g.apply(c0);
    }

    public C a() {
        return this.h.a();
    }

    public MinecraftKey b() {
        return this.f;
    }

    static final class a<C extends BiomeLayoutConfiguration> {

        public static final RegistryMaterials<MinecraftKey, BiomeLayout.a<?>> a = new RegistryMaterials();
        private final Supplier<C> f;
        public static final BiomeLayout.a<BiomeLayoutCheckerboardConfiguration> b = a("checkerboard", BiomeLayoutCheckerboardConfiguration::new);
        public static final BiomeLayout.a<BiomeLayoutFixedConfiguration> c = a("fixed", BiomeLayoutFixedConfiguration::new);
        public static final BiomeLayout.a<BiomeLayoutOverworldConfiguration> d = a("vanilla_layered", BiomeLayoutOverworldConfiguration::new);
        public static final BiomeLayout.a<BiomeLayoutTheEndConfiguration> e = a("the_end", BiomeLayoutTheEndConfiguration::new);

        private a(Supplier<C> supplier) {
            this.f = supplier;
        }

        public static <C extends BiomeLayoutConfiguration> BiomeLayout.a<C> a(String s, Supplier<C> supplier) {
            BiomeLayout.a biomelayout_a = new BiomeLayout.a(supplier);

            BiomeLayout.a.a.a(new MinecraftKey(s), biomelayout_a);
            return biomelayout_a;
        }

        public C a() {
            return (BiomeLayoutConfiguration) this.f.get();
        }
    }
}
