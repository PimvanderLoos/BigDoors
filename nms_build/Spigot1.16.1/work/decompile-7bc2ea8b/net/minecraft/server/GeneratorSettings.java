package net.minecraft.server;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Supplier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GeneratorSettings {

    public static final Codec<GeneratorSettings> a = RecordCodecBuilder.create((instance) -> {
        return instance.group(Codec.LONG.fieldOf("seed").stable().forGetter(GeneratorSettings::getSeed), Codec.BOOL.fieldOf("generate_features").withDefault(true).stable().forGetter(GeneratorSettings::shouldGenerateMapFeatures), Codec.BOOL.fieldOf("bonus_chest").withDefault(false).stable().forGetter(GeneratorSettings::d), RegistryMaterials.b(IRegistry.af, Lifecycle.stable(), WorldDimension.a).xmap(WorldDimension::a, Function.identity()).fieldOf("dimensions").forGetter(GeneratorSettings::e), Codec.STRING.optionalFieldOf("legacy_custom_options").stable().forGetter((generatorsettings) -> {
            return generatorsettings.i;
        })).apply(instance, instance.stable(GeneratorSettings::new));
    }).comapFlatMap(GeneratorSettings::n, Function.identity());
    private static final Logger LOGGER = LogManager.getLogger();
    private static final int d = "North Carolina".hashCode();
    public static final GeneratorSettings b = new GeneratorSettings((long) GeneratorSettings.d, true, true, a(DimensionManager.a((long) GeneratorSettings.d), a((long) GeneratorSettings.d)));
    private final long seed;
    private final boolean f;
    private final boolean g;
    private final RegistryMaterials<WorldDimension> h;
    private final Optional<String> i;

    private DataResult<GeneratorSettings> n() {
        return this.o() ? DataResult.success(this, Lifecycle.stable()) : DataResult.success(this);
    }

    private boolean o() {
        return WorldDimension.a(this.seed, this.h);
    }

    public GeneratorSettings(long i, boolean flag, boolean flag1, RegistryMaterials<WorldDimension> registrymaterials) {
        this(i, flag, flag1, registrymaterials, Optional.empty());
    }

    private GeneratorSettings(long i, boolean flag, boolean flag1, RegistryMaterials<WorldDimension> registrymaterials, Optional<String> optional) {
        this.seed = i;
        this.f = flag;
        this.g = flag1;
        this.h = registrymaterials;
        this.i = optional;
    }

    public static GeneratorSettings a() {
        long i = (new Random()).nextLong();

        return new GeneratorSettings(i, true, false, a(DimensionManager.a(i), a(i)));
    }

    public static ChunkGeneratorAbstract a(long i) {
        return new ChunkGeneratorAbstract(new WorldChunkManagerOverworld(i, false, false), i, GeneratorSettingBase.a.b.b());
    }

    public long getSeed() {
        return this.seed;
    }

    public boolean shouldGenerateMapFeatures() {
        return this.f;
    }

    public boolean d() {
        return this.g;
    }

    public static RegistryMaterials<WorldDimension> a(RegistryMaterials<WorldDimension> registrymaterials, ChunkGenerator chunkgenerator) {
        WorldDimension worlddimension = (WorldDimension) registrymaterials.a(WorldDimension.OVERWORLD);
        Supplier<DimensionManager> supplier = () -> {
            return worlddimension == null ? DimensionManager.a() : worlddimension.b();
        };

        return a(registrymaterials, supplier, chunkgenerator);
    }

    public static RegistryMaterials<WorldDimension> a(RegistryMaterials<WorldDimension> registrymaterials, Supplier<DimensionManager> supplier, ChunkGenerator chunkgenerator) {
        RegistryMaterials<WorldDimension> registrymaterials1 = new RegistryMaterials<>(IRegistry.af, Lifecycle.experimental());

        registrymaterials1.a(WorldDimension.OVERWORLD, (Object) (new WorldDimension(supplier, chunkgenerator)));
        registrymaterials1.d(WorldDimension.OVERWORLD);
        Iterator iterator = registrymaterials.c().iterator();

        while (iterator.hasNext()) {
            Entry<ResourceKey<WorldDimension>, WorldDimension> entry = (Entry) iterator.next();
            ResourceKey<WorldDimension> resourcekey = (ResourceKey) entry.getKey();

            if (resourcekey != WorldDimension.OVERWORLD) {
                registrymaterials1.a(resourcekey, entry.getValue());
                if (registrymaterials.c(resourcekey)) {
                    registrymaterials1.d(resourcekey);
                }
            }
        }

        return registrymaterials1;
    }

    public RegistryMaterials<WorldDimension> e() {
        return this.h;
    }

    public ChunkGenerator getChunkGenerator() {
        WorldDimension worlddimension = (WorldDimension) this.h.a(WorldDimension.OVERWORLD);

        return (ChunkGenerator) (worlddimension == null ? a((new Random()).nextLong()) : worlddimension.c());
    }

    public ImmutableSet<ResourceKey<World>> g() {
        return (ImmutableSet) this.e().c().stream().map((entry) -> {
            return ResourceKey.a(IRegistry.ae, ((ResourceKey) entry.getKey()).a());
        }).collect(ImmutableSet.toImmutableSet());
    }

    public boolean isDebugWorld() {
        return this.getChunkGenerator() instanceof ChunkProviderDebug;
    }

    public boolean isFlatWorld() {
        return this.getChunkGenerator() instanceof ChunkProviderFlat;
    }

    public GeneratorSettings k() {
        return new GeneratorSettings(this.seed, this.f, true, this.h, this.i);
    }

    public static GeneratorSettings a(Properties properties) {
        String s = (String) MoreObjects.firstNonNull((String) properties.get("generator-settings"), "");

        properties.put("generator-settings", s);
        String s1 = (String) MoreObjects.firstNonNull((String) properties.get("level-seed"), "");

        properties.put("level-seed", s1);
        String s2 = (String) properties.get("generate-structures");
        boolean flag = s2 == null || Boolean.parseBoolean(s2);

        properties.put("generate-structures", Objects.toString(flag));
        String s3 = (String) properties.get("level-type");
        String s4 = (String) Optional.ofNullable(s3).map((s5) -> {
            return s5.toLowerCase(Locale.ROOT);
        }).orElse("default");

        properties.put("level-type", s4);
        long i = (new Random()).nextLong();

        if (!s1.isEmpty()) {
            try {
                long j = Long.parseLong(s1);

                if (j != 0L) {
                    i = j;
                }
            } catch (NumberFormatException numberformatexception) {
                i = (long) s1.hashCode();
            }
        }

        RegistryMaterials<WorldDimension> registrymaterials = DimensionManager.a(i);
        byte b0 = -1;

        switch (s4.hashCode()) {
            case -1100099890:
                if (s4.equals("largebiomes")) {
                    b0 = 3;
                }
                break;
            case 3145593:
                if (s4.equals("flat")) {
                    b0 = 0;
                }
                break;
            case 1045526590:
                if (s4.equals("debug_all_block_states")) {
                    b0 = 1;
                }
                break;
            case 1271599715:
                if (s4.equals("amplified")) {
                    b0 = 2;
                }
        }

        switch (b0) {
            case 0:
                JsonObject jsonobject = !s.isEmpty() ? ChatDeserializer.a(s) : new JsonObject();
                Dynamic<JsonElement> dynamic = new Dynamic(JsonOps.INSTANCE, jsonobject);
                DataResult dataresult = GeneratorSettingsFlat.a.parse(dynamic);
                Logger logger = GeneratorSettings.LOGGER;

                logger.getClass();
                return new GeneratorSettings(i, flag, false, a(registrymaterials, new ChunkProviderFlat((GeneratorSettingsFlat) dataresult.resultOrPartial(logger::error).orElseGet(GeneratorSettingsFlat::i))));
            case 1:
                return new GeneratorSettings(i, flag, false, a(registrymaterials, ChunkProviderDebug.d));
            case 2:
                return new GeneratorSettings(i, flag, false, a(registrymaterials, new ChunkGeneratorAbstract(new WorldChunkManagerOverworld(i, false, false), i, GeneratorSettingBase.a.c.b())));
            case 3:
                return new GeneratorSettings(i, flag, false, a(registrymaterials, new ChunkGeneratorAbstract(new WorldChunkManagerOverworld(i, false, true), i, GeneratorSettingBase.a.b.b())));
            default:
                return new GeneratorSettings(i, flag, false, a(registrymaterials, a(i)));
        }
    }
}
