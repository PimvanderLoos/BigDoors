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
import java.util.OptionalLong;
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

    public static final Codec<GeneratorSettings> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(Codec.LONG.fieldOf("seed").stable().forGetter(GeneratorSettings::getSeed), Codec.BOOL.fieldOf("generate_features").orElse(true).stable().forGetter(GeneratorSettings::shouldGenerateMapFeatures), Codec.BOOL.fieldOf("bonus_chest").orElse(false).stable().forGetter(GeneratorSettings::c), RegistryMaterials.b(IRegistry.LEVEL_STEM_REGISTRY, Lifecycle.stable(), WorldDimension.CODEC).xmap(WorldDimension::a, Function.identity()).fieldOf("dimensions").forGetter(GeneratorSettings::d), Codec.STRING.optionalFieldOf("legacy_custom_options").stable().forGetter((generatorsettings) -> {
            return generatorsettings.legacyCustomOptions;
        })).apply(instance, instance.stable(GeneratorSettings::new));
    }).comapFlatMap(GeneratorSettings::m, Function.identity());
    private static final Logger LOGGER = LogManager.getLogger();
    private final long seed;
    private final boolean generateFeatures;
    private final boolean generateBonusChest;
    private final RegistryMaterials<WorldDimension> dimensions;
    private final Optional<String> legacyCustomOptions;

    private DataResult<GeneratorSettings> m() {
        WorldDimension worlddimension = (WorldDimension) this.dimensions.a(WorldDimension.OVERWORLD);

        return worlddimension == null ? DataResult.error("Overworld settings missing") : (this.n() ? DataResult.success(this, Lifecycle.stable()) : DataResult.success(this));
    }

    private boolean n() {
        return WorldDimension.a(this.seed, this.dimensions);
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
        this.generateFeatures = flag;
        this.generateBonusChest = flag1;
        this.dimensions = registrymaterials;
        this.legacyCustomOptions = optional;
    }

    public static GeneratorSettings a(IRegistryCustom iregistrycustom) {
        IRegistry<BiomeBase> iregistry = iregistrycustom.d(IRegistry.BIOME_REGISTRY);
        int i = "North Carolina".hashCode();
        IRegistry<DimensionManager> iregistry1 = iregistrycustom.d(IRegistry.DIMENSION_TYPE_REGISTRY);
        IRegistry<GeneratorSettingBase> iregistry2 = iregistrycustom.d(IRegistry.NOISE_GENERATOR_SETTINGS_REGISTRY);

        return new GeneratorSettings((long) i, true, true, a(iregistry1, DimensionManager.a(iregistry1, iregistry, iregistry2, (long) i), (ChunkGenerator) a(iregistry, iregistry2, (long) i)));
    }

    public static GeneratorSettings a(IRegistry<DimensionManager> iregistry, IRegistry<BiomeBase> iregistry1, IRegistry<GeneratorSettingBase> iregistry2) {
        long i = (new Random()).nextLong();

        return new GeneratorSettings(i, true, false, a(iregistry, DimensionManager.a(iregistry, iregistry1, iregistry2, i), (ChunkGenerator) a(iregistry1, iregistry2, i)));
    }

    public static ChunkGeneratorAbstract a(IRegistry<BiomeBase> iregistry, IRegistry<GeneratorSettingBase> iregistry1, long i) {
        return new ChunkGeneratorAbstract(new WorldChunkManagerOverworld(i, false, false, iregistry), i, () -> {
            return (GeneratorSettingBase) iregistry1.d(GeneratorSettingBase.OVERWORLD);
        });
    }

    public long getSeed() {
        return this.seed;
    }

    public boolean shouldGenerateMapFeatures() {
        return this.generateFeatures;
    }

    public boolean c() {
        return this.generateBonusChest;
    }

    public static RegistryMaterials<WorldDimension> a(IRegistry<DimensionManager> iregistry, RegistryMaterials<WorldDimension> registrymaterials, ChunkGenerator chunkgenerator) {
        WorldDimension worlddimension = (WorldDimension) registrymaterials.a(WorldDimension.OVERWORLD);
        Supplier<DimensionManager> supplier = () -> {
            return worlddimension == null ? (DimensionManager) iregistry.d(DimensionManager.OVERWORLD_LOCATION) : worlddimension.b();
        };

        return a(registrymaterials, supplier, chunkgenerator);
    }

    public static RegistryMaterials<WorldDimension> a(RegistryMaterials<WorldDimension> registrymaterials, Supplier<DimensionManager> supplier, ChunkGenerator chunkgenerator) {
        RegistryMaterials<WorldDimension> registrymaterials1 = new RegistryMaterials<>(IRegistry.LEVEL_STEM_REGISTRY, Lifecycle.experimental());

        registrymaterials1.a(WorldDimension.OVERWORLD, (Object) (new WorldDimension(supplier, chunkgenerator)), Lifecycle.stable());
        Iterator iterator = registrymaterials.d().iterator();

        while (iterator.hasNext()) {
            Entry<ResourceKey<WorldDimension>, WorldDimension> entry = (Entry) iterator.next();
            ResourceKey<WorldDimension> resourcekey = (ResourceKey) entry.getKey();

            if (resourcekey != WorldDimension.OVERWORLD) {
                registrymaterials1.a(resourcekey, (Object) ((WorldDimension) entry.getValue()), registrymaterials.d((Object) ((WorldDimension) entry.getValue())));
            }
        }

        return registrymaterials1;
    }

    public RegistryMaterials<WorldDimension> d() {
        return this.dimensions;
    }

    public ChunkGenerator getChunkGenerator() {
        WorldDimension worlddimension = (WorldDimension) this.dimensions.a(WorldDimension.OVERWORLD);

        if (worlddimension == null) {
            throw new IllegalStateException("Overworld settings missing");
        } else {
            return worlddimension.c();
        }
    }

    public ImmutableSet<ResourceKey<World>> f() {
        return (ImmutableSet) this.d().d().stream().map((entry) -> {
            return ResourceKey.a(IRegistry.DIMENSION_REGISTRY, ((ResourceKey) entry.getKey()).a());
        }).collect(ImmutableSet.toImmutableSet());
    }

    public boolean isDebugWorld() {
        return this.getChunkGenerator() instanceof ChunkProviderDebug;
    }

    public boolean isFlatWorld() {
        return this.getChunkGenerator() instanceof ChunkProviderFlat;
    }

    public boolean i() {
        return this.legacyCustomOptions.isPresent();
    }

    public GeneratorSettings j() {
        return new GeneratorSettings(this.seed, this.generateFeatures, true, this.dimensions, this.legacyCustomOptions);
    }

    public GeneratorSettings k() {
        return new GeneratorSettings(this.seed, !this.generateFeatures, this.generateBonusChest, this.dimensions);
    }

    public GeneratorSettings l() {
        return new GeneratorSettings(this.seed, this.generateFeatures, !this.generateBonusChest, this.dimensions);
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

        IRegistry<DimensionManager> iregistry = iregistrycustom.d(IRegistry.DIMENSION_TYPE_REGISTRY);
        IRegistry<BiomeBase> iregistry1 = iregistrycustom.d(IRegistry.BIOME_REGISTRY);
        IRegistry<GeneratorSettingBase> iregistry2 = iregistrycustom.d(IRegistry.NOISE_GENERATOR_SETTINGS_REGISTRY);
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
                DataResult dataresult = GeneratorSettingsFlat.CODEC.parse(dynamic);
                Logger logger = GeneratorSettings.LOGGER;

                Objects.requireNonNull(logger);
                return new GeneratorSettings(i, flag, false, a(iregistry, registrymaterials, (ChunkGenerator) (new ChunkProviderFlat((GeneratorSettingsFlat) dataresult.resultOrPartial(logger::error).orElseGet(() -> {
                    return GeneratorSettingsFlat.a(iregistry1);
                })))));
            case 1:
                return new GeneratorSettings(i, flag, false, a(iregistry, registrymaterials, (ChunkGenerator) (new ChunkProviderDebug(iregistry1))));
            case 2:
                return new GeneratorSettings(i, flag, false, a(iregistry, registrymaterials, (ChunkGenerator) (new ChunkGeneratorAbstract(new WorldChunkManagerOverworld(i, false, false, iregistry1), i, () -> {
                    return (GeneratorSettingBase) iregistry2.d(GeneratorSettingBase.AMPLIFIED);
                }))));
            case 3:
                return new GeneratorSettings(i, flag, false, a(iregistry, registrymaterials, (ChunkGenerator) (new ChunkGeneratorAbstract(new WorldChunkManagerOverworld(i, false, true, iregistry1), i, () -> {
                    return (GeneratorSettingBase) iregistry2.d(GeneratorSettingBase.OVERWORLD);
                }))));
            default:
                return new GeneratorSettings(i, flag, false, a(iregistry, registrymaterials, (ChunkGenerator) a(iregistry1, iregistry2, i)));
        }
    }

    public GeneratorSettings a(boolean flag, OptionalLong optionallong) {
        long i = optionallong.orElse(this.seed);
        RegistryMaterials registrymaterials;

        if (optionallong.isPresent()) {
            registrymaterials = new RegistryMaterials<>(IRegistry.LEVEL_STEM_REGISTRY, Lifecycle.experimental());
            long j = optionallong.getAsLong();
            Iterator iterator = this.dimensions.d().iterator();

            while (iterator.hasNext()) {
                Entry<ResourceKey<WorldDimension>, WorldDimension> entry = (Entry) iterator.next();
                ResourceKey<WorldDimension> resourcekey = (ResourceKey) entry.getKey();

                registrymaterials.a(resourcekey, (Object) (new WorldDimension(((WorldDimension) entry.getValue()).a(), ((WorldDimension) entry.getValue()).c().withSeed(j))), this.dimensions.d((Object) ((WorldDimension) entry.getValue())));
            }
        } else {
            registrymaterials = this.dimensions;
        }

        GeneratorSettings generatorsettings;

        if (this.isDebugWorld()) {
            generatorsettings = new GeneratorSettings(i, false, false, registrymaterials);
        } else {
            generatorsettings = new GeneratorSettings(i, this.shouldGenerateMapFeatures(), this.c() && !flag, registrymaterials);
        }

        return generatorsettings;
    }
}
