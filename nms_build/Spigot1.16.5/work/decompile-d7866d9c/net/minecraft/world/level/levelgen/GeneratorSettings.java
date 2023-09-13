package net.minecraft.world.level.levelgen;

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
import net.minecraft.core.IRegistry;
import net.minecraft.core.IRegistryCustom;
import net.minecraft.core.RegistryMaterials;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ChatDeserializer;
import net.minecraft.world.level.World;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.biome.WorldChunkManagerOverworld;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.DimensionManager;
import net.minecraft.world.level.dimension.WorldDimension;
import net.minecraft.world.level.levelgen.flat.GeneratorSettingsFlat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GeneratorSettings {

    public static final Codec<GeneratorSettings> a = RecordCodecBuilder.create((instance) -> {
        return instance.group(Codec.LONG.fieldOf("seed").stable().forGetter(GeneratorSettings::getSeed), Codec.BOOL.fieldOf("generate_features").orElse(true).stable().forGetter(GeneratorSettings::shouldGenerateMapFeatures), Codec.BOOL.fieldOf("bonus_chest").orElse(false).stable().forGetter(GeneratorSettings::c), RegistryMaterials.b(IRegistry.M, Lifecycle.stable(), WorldDimension.a).xmap(WorldDimension::a, Function.identity()).fieldOf("dimensions").forGetter(GeneratorSettings::d), Codec.STRING.optionalFieldOf("legacy_custom_options").stable().forGetter((generatorsettings) -> {
            return generatorsettings.g;
        })).apply(instance, instance.stable(GeneratorSettings::new));
    }).comapFlatMap(GeneratorSettings::m, Function.identity());
    private static final Logger LOGGER = LogManager.getLogger();
    private final long seed;
    private final boolean d;
    private final boolean e;
    private final RegistryMaterials<WorldDimension> f;
    private final Optional<String> g;

    private DataResult<GeneratorSettings> m() {
        WorldDimension worlddimension = (WorldDimension) this.f.a(WorldDimension.OVERWORLD);

        return worlddimension == null ? DataResult.error("Overworld settings missing") : (this.n() ? DataResult.success(this, Lifecycle.stable()) : DataResult.success(this));
    }

    private boolean n() {
        return WorldDimension.a(this.seed, this.f);
    }

    public GeneratorSettings(long i, boolean flag, boolean flag1, RegistryMaterials<WorldDimension> registrymaterials) {
        this(i, flag, flag1, registrymaterials, Optional.empty());
        WorldDimension worlddimension = (WorldDimension) registrymaterials.a(WorldDimension.OVERWORLD);

        if (worlddimension == null) {
            throw new IllegalStateException("Overworld settings missing");
        }
    }

    private GeneratorSettings(long i, boolean flag, boolean flag1, RegistryMaterials<WorldDimension> registrymaterials, Optional<String> optional) {
        this.seed = i;
        this.d = flag;
        this.e = flag1;
        this.f = registrymaterials;
        this.g = optional;
    }

    public static GeneratorSettings a(IRegistryCustom iregistrycustom) {
        IRegistry<BiomeBase> iregistry = iregistrycustom.b(IRegistry.ay);
        int i = "North Carolina".hashCode();
        IRegistry<DimensionManager> iregistry1 = iregistrycustom.b(IRegistry.K);
        IRegistry<GeneratorSettingBase> iregistry2 = iregistrycustom.b(IRegistry.ar);

        return new GeneratorSettings((long) i, true, true, a((IRegistry) iregistry1, DimensionManager.a(iregistry1, iregistry, iregistry2, (long) i), (ChunkGenerator) a(iregistry, iregistry2, (long) i)));
    }

    public static GeneratorSettings a(IRegistry<DimensionManager> iregistry, IRegistry<BiomeBase> iregistry1, IRegistry<GeneratorSettingBase> iregistry2) {
        long i = (new Random()).nextLong();

        return new GeneratorSettings(i, true, false, a(iregistry, DimensionManager.a(iregistry, iregistry1, iregistry2, i), (ChunkGenerator) a(iregistry1, iregistry2, i)));
    }

    public static ChunkGeneratorAbstract a(IRegistry<BiomeBase> iregistry, IRegistry<GeneratorSettingBase> iregistry1, long i) {
        return new ChunkGeneratorAbstract(new WorldChunkManagerOverworld(i, false, false, iregistry), i, () -> {
            return (GeneratorSettingBase) iregistry1.d(GeneratorSettingBase.c);
        });
    }

    public long getSeed() {
        return this.seed;
    }

    public boolean shouldGenerateMapFeatures() {
        return this.d;
    }

    public boolean c() {
        return this.e;
    }

    public static RegistryMaterials<WorldDimension> a(IRegistry<DimensionManager> iregistry, RegistryMaterials<WorldDimension> registrymaterials, ChunkGenerator chunkgenerator) {
        WorldDimension worlddimension = (WorldDimension) registrymaterials.a(WorldDimension.OVERWORLD);
        Supplier<DimensionManager> supplier = () -> {
            return worlddimension == null ? (DimensionManager) iregistry.d(DimensionManager.OVERWORLD) : worlddimension.b();
        };

        return a(registrymaterials, supplier, chunkgenerator);
    }

    public static RegistryMaterials<WorldDimension> a(RegistryMaterials<WorldDimension> registrymaterials, Supplier<DimensionManager> supplier, ChunkGenerator chunkgenerator) {
        RegistryMaterials<WorldDimension> registrymaterials1 = new RegistryMaterials<>(IRegistry.M, Lifecycle.experimental());

        registrymaterials1.a(WorldDimension.OVERWORLD, (Object) (new WorldDimension(supplier, chunkgenerator)), Lifecycle.stable());
        Iterator iterator = registrymaterials.d().iterator();

        while (iterator.hasNext()) {
            Entry<ResourceKey<WorldDimension>, WorldDimension> entry = (Entry) iterator.next();
            ResourceKey<WorldDimension> resourcekey = (ResourceKey) entry.getKey();

            if (resourcekey != WorldDimension.OVERWORLD) {
                registrymaterials1.a(resourcekey, entry.getValue(), registrymaterials.d(entry.getValue()));
            }
        }

        return registrymaterials1;
    }

    public RegistryMaterials<WorldDimension> d() {
        return this.f;
    }

    public ChunkGenerator getChunkGenerator() {
        WorldDimension worlddimension = (WorldDimension) this.f.a(WorldDimension.OVERWORLD);

        if (worlddimension == null) {
            throw new IllegalStateException("Overworld settings missing");
        } else {
            return worlddimension.c();
        }
    }

    public ImmutableSet<ResourceKey<World>> f() {
        return (ImmutableSet) this.d().d().stream().map((entry) -> {
            return ResourceKey.a(IRegistry.L, ((ResourceKey) entry.getKey()).a());
        }).collect(ImmutableSet.toImmutableSet());
    }

    public boolean isDebugWorld() {
        return this.getChunkGenerator() instanceof ChunkProviderDebug;
    }

    public boolean isFlatWorld() {
        return this.getChunkGenerator() instanceof ChunkProviderFlat;
    }

    public GeneratorSettings j() {
        return new GeneratorSettings(this.seed, this.d, true, this.f, this.g);
    }

    public static GeneratorSettings a(IRegistryCustom iregistrycustom, Properties properties) {
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

        IRegistry<DimensionManager> iregistry = iregistrycustom.b(IRegistry.K);
        IRegistry<BiomeBase> iregistry1 = iregistrycustom.b(IRegistry.ay);
        IRegistry<GeneratorSettingBase> iregistry2 = iregistrycustom.b(IRegistry.ar);
        RegistryMaterials<WorldDimension> registrymaterials = DimensionManager.a(iregistry, iregistry1, iregistry2, i);
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
                return new GeneratorSettings(i, flag, false, a((IRegistry) iregistry, registrymaterials, (ChunkGenerator) (new ChunkProviderFlat((GeneratorSettingsFlat) dataresult.resultOrPartial(logger::error).orElseGet(() -> {
                    return GeneratorSettingsFlat.a(iregistry1);
                })))));
            case 1:
                return new GeneratorSettings(i, flag, false, a((IRegistry) iregistry, registrymaterials, (ChunkGenerator) (new ChunkProviderDebug(iregistry1))));
            case 2:
                return new GeneratorSettings(i, flag, false, a((IRegistry) iregistry, registrymaterials, (ChunkGenerator) (new ChunkGeneratorAbstract(new WorldChunkManagerOverworld(i, false, false, iregistry1), i, () -> {
                    return (GeneratorSettingBase) iregistry2.d(GeneratorSettingBase.d);
                }))));
            case 3:
                return new GeneratorSettings(i, flag, false, a((IRegistry) iregistry, registrymaterials, (ChunkGenerator) (new ChunkGeneratorAbstract(new WorldChunkManagerOverworld(i, false, true, iregistry1), i, () -> {
                    return (GeneratorSettingBase) iregistry2.d(GeneratorSettingBase.c);
                }))));
            default:
                return new GeneratorSettings(i, flag, false, a((IRegistry) iregistry, registrymaterials, (ChunkGenerator) a(iregistry1, iregistry2, i)));
        }
    }
}
