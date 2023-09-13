package net.minecraft.server;

import java.util.function.Function;

public class BiomeLayout<C extends BiomeLayoutConfiguration, T extends WorldChunkManager> {

    public static final BiomeLayout<BiomeLayoutCheckerboardConfiguration, WorldChunkManagerCheckerBoard> a = a("checkerboard", WorldChunkManagerCheckerBoard::new, BiomeLayoutCheckerboardConfiguration::new);
    public static final BiomeLayout<BiomeLayoutFixedConfiguration, WorldChunkManagerHell> b = a("fixed", WorldChunkManagerHell::new, BiomeLayoutFixedConfiguration::new);
    public static final BiomeLayout<BiomeLayoutOverworldConfiguration, WorldChunkManagerOverworld> c = a("vanilla_layered", WorldChunkManagerOverworld::new, BiomeLayoutOverworldConfiguration::new);
    public static final BiomeLayout<BiomeLayoutTheEndConfiguration, WorldChunkManagerTheEnd> d = a("the_end", WorldChunkManagerTheEnd::new, BiomeLayoutTheEndConfiguration::new);
    private final Function<C, T> e;
    private final Function<WorldData, C> f;

    private static <C extends BiomeLayoutConfiguration, T extends WorldChunkManager> BiomeLayout<C, T> a(String s, Function<C, T> function, Function<WorldData, C> function1) {
        return (BiomeLayout) IRegistry.a(IRegistry.BIOME_SOURCE_TYPE, s, (Object) (new BiomeLayout<>(function, function1)));
    }

    private BiomeLayout(Function<C, T> function, Function<WorldData, C> function1) {
        this.e = function;
        this.f = function1;
    }

    public T a(C c0) {
        return (WorldChunkManager) this.e.apply(c0);
    }

    public C a(WorldData worlddata) {
        return (BiomeLayoutConfiguration) this.f.apply(worlddata);
    }
}
