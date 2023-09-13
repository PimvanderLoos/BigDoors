package net.minecraft.server;

import java.util.function.Supplier;

public class ChunkGeneratorType<C extends GeneratorSettings, T extends ChunkGenerator<C>> implements ChunkGeneratorFactory<C, T> {

    public static final ChunkGeneratorType<GeneratorSettingsOverworld, ChunkProviderGenerate> a = a("surface", ChunkProviderGenerate::new, GeneratorSettingsOverworld::new, true);
    public static final ChunkGeneratorType<GeneratorSettingsNether, ChunkProviderHell> b = a("caves", ChunkProviderHell::new, GeneratorSettingsNether::new, true);
    public static final ChunkGeneratorType<GeneratorSettingsEnd, ChunkProviderTheEnd> c = a("floating_islands", ChunkProviderTheEnd::new, GeneratorSettingsEnd::new, true);
    public static final ChunkGeneratorType<GeneratorSettingsDebug, ChunkProviderDebug> d = a("debug", ChunkProviderDebug::new, GeneratorSettingsDebug::new, false);
    public static final ChunkGeneratorType<GeneratorSettingsFlat, ChunkProviderFlat> e = a("flat", ChunkProviderFlat::new, GeneratorSettingsFlat::new, false);
    private final MinecraftKey f;
    private final ChunkGeneratorFactory<C, T> g;
    private final boolean h;
    private final Supplier<C> i;

    public static void a() {}

    public ChunkGeneratorType(ChunkGeneratorFactory<C, T> chunkgeneratorfactory, boolean flag, Supplier<C> supplier, MinecraftKey minecraftkey) {
        this.g = chunkgeneratorfactory;
        this.h = flag;
        this.i = supplier;
        this.f = minecraftkey;
    }

    public static <C extends GeneratorSettings, T extends ChunkGenerator<C>> ChunkGeneratorType<C, T> a(String s, ChunkGeneratorFactory<C, T> chunkgeneratorfactory, Supplier<C> supplier, boolean flag) {
        MinecraftKey minecraftkey = new MinecraftKey(s);
        ChunkGeneratorType<C, T> chunkgeneratortype = new ChunkGeneratorType<>(chunkgeneratorfactory, flag, supplier, minecraftkey);

        IRegistry.CHUNK_GENERATOR_TYPE.a(minecraftkey, (Object) chunkgeneratortype);
        return chunkgeneratortype;
    }

    public T create(World world, WorldChunkManager worldchunkmanager, C c0) {
        return this.g.create(world, worldchunkmanager, c0);
    }

    public C b() {
        return (GeneratorSettings) this.i.get();
    }

    public MinecraftKey d() {
        return this.f;
    }
}
