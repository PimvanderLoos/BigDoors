package net.minecraft.server;

import java.util.function.Supplier;

public class ChunkGeneratorType<C extends GeneratorSettings, T extends ChunkGenerator<C>> implements ChunkGeneratorFactory<C, T> {

    public static final RegistryMaterials<MinecraftKey, ChunkGeneratorType<?, ?>> a = new RegistryMaterials();
    private final MinecraftKey g;
    private final ChunkGeneratorFactory<C, T> h;
    private final boolean i;
    private final ChunkGeneratorType.a<C> j;
    public static final ChunkGeneratorType<GeneratorSettingsOverworld, ChunkProviderGenerate> b = a("surface", ChunkProviderGenerate::new, ChunkGeneratorType.a.b, true);
    public static final ChunkGeneratorType<GeneratorSettingsNether, ChunkProviderHell> c = a("caves", ChunkProviderHell::new, ChunkGeneratorType.a.c, true);
    public static final ChunkGeneratorType<GeneratorSettingsEnd, ChunkProviderTheEnd> d = a("floating_islands", ChunkProviderTheEnd::new, ChunkGeneratorType.a.d, true);
    public static final ChunkGeneratorType<GeneratorSettingsDebug, ChunkProviderDebug> e = a("debug", ChunkProviderDebug::new, ChunkGeneratorType.a.e, false);
    public static final ChunkGeneratorType<GeneratorSettingsFlat, ChunkProviderFlat> f = a("flat", ChunkProviderFlat::new, ChunkGeneratorType.a.f, false);

    public ChunkGeneratorType(ChunkGeneratorFactory<C, T> chunkgeneratorfactory, boolean flag, ChunkGeneratorType.a<C> chunkgeneratortype_a, MinecraftKey minecraftkey) {
        this.h = chunkgeneratorfactory;
        this.i = flag;
        this.j = chunkgeneratortype_a;
        this.g = minecraftkey;
    }

    public static <C extends GeneratorSettings, T extends ChunkGenerator<C>> ChunkGeneratorType<C, T> a(String s, ChunkGeneratorFactory<C, T> chunkgeneratorfactory, ChunkGeneratorType.a<C> chunkgeneratortype_a, boolean flag) {
        MinecraftKey minecraftkey = new MinecraftKey(s);
        ChunkGeneratorType chunkgeneratortype = new ChunkGeneratorType(chunkgeneratorfactory, flag, chunkgeneratortype_a, minecraftkey);

        ChunkGeneratorType.a.a(minecraftkey, chunkgeneratortype);
        return chunkgeneratortype;
    }

    public T create(World world, WorldChunkManager worldchunkmanager, C c0) {
        return this.h.create(world, worldchunkmanager, c0);
    }

    public C a() {
        return this.j.a();
    }

    public MinecraftKey c() {
        return this.g;
    }

    static final class a<C extends GeneratorSettings> {

        public static final RegistryMaterials<MinecraftKey, ChunkGeneratorType.a<?>> a = new RegistryMaterials();
        private final Supplier<C> g;
        public static final ChunkGeneratorType.a<GeneratorSettingsOverworld> b = a("surface", GeneratorSettingsOverworld::new);
        public static final ChunkGeneratorType.a<GeneratorSettingsNether> c = a("caves", GeneratorSettingsNether::new);
        public static final ChunkGeneratorType.a<GeneratorSettingsEnd> d = a("floating_islands", GeneratorSettingsEnd::new);
        public static final ChunkGeneratorType.a<GeneratorSettingsDebug> e = a("debug", GeneratorSettingsDebug::new);
        public static final ChunkGeneratorType.a<GeneratorSettingsFlat> f = a("flat", GeneratorSettingsFlat::new);

        private a(Supplier<C> supplier) {
            this.g = supplier;
        }

        public static <C extends GeneratorSettings> ChunkGeneratorType.a<C> a(String s, Supplier<C> supplier) {
            ChunkGeneratorType.a chunkgeneratortype_a = new ChunkGeneratorType.a(supplier);

            ChunkGeneratorType.a.a.a(new MinecraftKey(s), chunkgeneratortype_a);
            return chunkgeneratortype_a;
        }

        public C a() {
            return (GeneratorSettings) this.g.get();
        }
    }
}
