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
import net.minecraft.world.level.biome.WorldChunkManagerMultiNoise;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.DimensionManager;
import net.minecraft.world.level.dimension.WorldDimension;
import net.minecraft.world.level.levelgen.flat.GeneratorSettingsFlat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GeneratorSettings {

    public static final Codec<GeneratorSettings> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(Codec.LONG.fieldOf("seed").stable().forGetter(GeneratorSettings::seed), Codec.BOOL.fieldOf("generate_features").orElse(true).stable().forGetter(GeneratorSettings::generateFeatures), Codec.BOOL.fieldOf("bonus_chest").orElse(false).stable().forGetter(GeneratorSettings::generateBonusChest), RegistryMaterials.dataPackCodec(IRegistry.LEVEL_STEM_REGISTRY, Lifecycle.stable(), WorldDimension.CODEC).xmap(WorldDimension::sortMap, Function.identity()).fieldOf("dimensions").forGetter(GeneratorSettings::dimensions), Codec.STRING.optionalFieldOf("legacy_custom_options").stable().forGetter((generatorsettings) -> {
            return generatorsettings.legacyCustomOptions;
        })).apply(instance, instance.stable(GeneratorSettings::new));
    }).comapFlatMap(GeneratorSettings::guardExperimental, Function.identity());
    private static final Logger LOGGER = LogManager.getLogger();
    private final long seed;
    private final boolean generateFeatures;
    private final boolean generateBonusChest;
    private final RegistryMaterials<WorldDimension> dimensions;
    private final Optional<String> legacyCustomOptions;

    private DataResult<GeneratorSettings> guardExperimental() {
        WorldDimension worlddimension = (WorldDimension) this.dimensions.get(WorldDimension.OVERWORLD);

        return worlddimension == null ? DataResult.error("Overworld settings missing") : (this.stable() ? DataResult.success(this, Lifecycle.stable()) : DataResult.success(this));
    }

    private boolean stable() {
        return WorldDimension.stable(this.seed, this.dimensions);
    }

    public GeneratorSettings(long i, boolean flag, boolean flag1, RegistryMaterials<WorldDimension> registrymaterials) {
        this(i, flag, flag1, registrymaterials, Optional.empty());
        WorldDimension worlddimension = (WorldDimension) registrymaterials.get(WorldDimension.OVERWORLD);

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

    public static GeneratorSettings demoSettings(IRegistryCustom iregistrycustom) {
        int i = "North Carolina".hashCode();

        return new GeneratorSettings((long) i, true, true, withOverworld(iregistrycustom.registryOrThrow(IRegistry.DIMENSION_TYPE_REGISTRY), DimensionManager.defaultDimensions(iregistrycustom, (long) i), makeDefaultOverworld(iregistrycustom, (long) i)));
    }

    public static GeneratorSettings makeDefault(IRegistryCustom iregistrycustom) {
        long i = (new Random()).nextLong();

        return new GeneratorSettings(i, true, false, withOverworld(iregistrycustom.registryOrThrow(IRegistry.DIMENSION_TYPE_REGISTRY), DimensionManager.defaultDimensions(iregistrycustom, i), makeDefaultOverworld(iregistrycustom, i)));
    }

    public static ChunkGeneratorAbstract makeDefaultOverworld(IRegistryCustom iregistrycustom, long i) {
        return makeDefaultOverworld(iregistrycustom, i, true);
    }

    public static ChunkGeneratorAbstract makeDefaultOverworld(IRegistryCustom iregistrycustom, long i, boolean flag) {
        return makeOverworld(iregistrycustom, i, GeneratorSettingBase.OVERWORLD, flag);
    }

    public static ChunkGeneratorAbstract makeOverworld(IRegistryCustom iregistrycustom, long i, ResourceKey<GeneratorSettingBase> resourcekey) {
        return makeOverworld(iregistrycustom, i, resourcekey, true);
    }

    public static ChunkGeneratorAbstract makeOverworld(IRegistryCustom iregistrycustom, long i, ResourceKey<GeneratorSettingBase> resourcekey, boolean flag) {
        return new ChunkGeneratorAbstract(iregistrycustom.registryOrThrow(IRegistry.NOISE_REGISTRY), WorldChunkManagerMultiNoise.a.OVERWORLD.biomeSource(iregistrycustom.registryOrThrow(IRegistry.BIOME_REGISTRY), flag), i, () -> {
            return (GeneratorSettingBase) iregistrycustom.registryOrThrow(IRegistry.NOISE_GENERATOR_SETTINGS_REGISTRY).getOrThrow(resourcekey);
        });
    }

    public long seed() {
        return this.seed;
    }

    public boolean generateFeatures() {
        return this.generateFeatures;
    }

    public boolean generateBonusChest() {
        return this.generateBonusChest;
    }

    public static RegistryMaterials<WorldDimension> withOverworld(IRegistry<DimensionManager> iregistry, RegistryMaterials<WorldDimension> registrymaterials, ChunkGenerator chunkgenerator) {
        WorldDimension worlddimension = (WorldDimension) registrymaterials.get(WorldDimension.OVERWORLD);
        Supplier<DimensionManager> supplier = () -> {
            return worlddimension == null ? (DimensionManager) iregistry.getOrThrow(DimensionManager.OVERWORLD_LOCATION) : worlddimension.type();
        };

        return withOverworld(registrymaterials, supplier, chunkgenerator);
    }

    public static RegistryMaterials<WorldDimension> withOverworld(RegistryMaterials<WorldDimension> registrymaterials, Supplier<DimensionManager> supplier, ChunkGenerator chunkgenerator) {
        RegistryMaterials<WorldDimension> registrymaterials1 = new RegistryMaterials<>(IRegistry.LEVEL_STEM_REGISTRY, Lifecycle.experimental());

        registrymaterials1.register(WorldDimension.OVERWORLD, (Object) (new WorldDimension(supplier, chunkgenerator)), Lifecycle.stable());
        Iterator iterator = registrymaterials.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry<ResourceKey<WorldDimension>, WorldDimension> entry = (Entry) iterator.next();
            ResourceKey<WorldDimension> resourcekey = (ResourceKey) entry.getKey();

            if (resourcekey != WorldDimension.OVERWORLD) {
                registrymaterials1.register(resourcekey, (Object) ((WorldDimension) entry.getValue()), registrymaterials.lifecycle((WorldDimension) entry.getValue()));
            }
        }

        return registrymaterials1;
    }

    public RegistryMaterials<WorldDimension> dimensions() {
        return this.dimensions;
    }

    public ChunkGenerator overworld() {
        WorldDimension worlddimension = (WorldDimension) this.dimensions.get(WorldDimension.OVERWORLD);

        if (worlddimension == null) {
            throw new IllegalStateException("Overworld settings missing");
        } else {
            return worlddimension.generator();
        }
    }

    public ImmutableSet<ResourceKey<World>> levels() {
        return (ImmutableSet) this.dimensions().entrySet().stream().map(Entry::getKey).map(GeneratorSettings::levelStemToLevel).collect(ImmutableSet.toImmutableSet());
    }

    public static ResourceKey<World> levelStemToLevel(ResourceKey<WorldDimension> resourcekey) {
        return ResourceKey.create(IRegistry.DIMENSION_REGISTRY, resourcekey.location());
    }

    public static ResourceKey<WorldDimension> levelToLevelStem(ResourceKey<World> resourcekey) {
        return ResourceKey.create(IRegistry.LEVEL_STEM_REGISTRY, resourcekey.location());
    }

    public boolean isDebug() {
        return this.overworld() instanceof ChunkProviderDebug;
    }

    public boolean isFlatWorld() {
        return this.overworld() instanceof ChunkProviderFlat;
    }

    public boolean isOldCustomizedWorld() {
        return this.legacyCustomOptions.isPresent();
    }

    public GeneratorSettings withBonusChest() {
        return new GeneratorSettings(this.seed, this.generateFeatures, true, this.dimensions, this.legacyCustomOptions);
    }

    public GeneratorSettings withFeaturesToggled() {
        return new GeneratorSettings(this.seed, !this.generateFeatures, this.generateBonusChest, this.dimensions);
    }

    public GeneratorSettings withBonusChestToggled() {
        return new GeneratorSettings(this.seed, this.generateFeatures, !this.generateBonusChest, this.dimensions);
    }

    public static GeneratorSettings create(IRegistryCustom iregistrycustom, Properties properties) {
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

        IRegistry<DimensionManager> iregistry = iregistrycustom.registryOrThrow(IRegistry.DIMENSION_TYPE_REGISTRY);
        IRegistry<BiomeBase> iregistry1 = iregistrycustom.registryOrThrow(IRegistry.BIOME_REGISTRY);
        RegistryMaterials<WorldDimension> registrymaterials = DimensionManager.defaultDimensions(iregistrycustom, i);
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
                JsonObject jsonobject = !s.isEmpty() ? ChatDeserializer.parse(s) : new JsonObject();
                Dynamic<JsonElement> dynamic = new Dynamic(JsonOps.INSTANCE, jsonobject);
                DataResult dataresult = GeneratorSettingsFlat.CODEC.parse(dynamic);
                Logger logger = GeneratorSettings.LOGGER;

                Objects.requireNonNull(logger);
                return new GeneratorSettings(i, flag, false, withOverworld(iregistry, registrymaterials, new ChunkProviderFlat((GeneratorSettingsFlat) dataresult.resultOrPartial(logger::error).orElseGet(() -> {
                    return GeneratorSettingsFlat.getDefault(iregistry1);
                }))));
            case 1:
                return new GeneratorSettings(i, flag, false, withOverworld(iregistry, registrymaterials, new ChunkProviderDebug(iregistry1)));
            case 2:
                return new GeneratorSettings(i, flag, false, withOverworld(iregistry, registrymaterials, makeOverworld(iregistrycustom, i, GeneratorSettingBase.AMPLIFIED)));
            case 3:
                return new GeneratorSettings(i, flag, false, withOverworld(iregistry, registrymaterials, makeOverworld(iregistrycustom, i, GeneratorSettingBase.LARGE_BIOMES)));
            default:
                return new GeneratorSettings(i, flag, false, withOverworld(iregistry, registrymaterials, makeDefaultOverworld(iregistrycustom, i)));
        }
    }

    public GeneratorSettings withSeed(boolean flag, OptionalLong optionallong) {
        long i = optionallong.orElse(this.seed);
        RegistryMaterials registrymaterials;

        if (optionallong.isPresent()) {
            registrymaterials = new RegistryMaterials<>(IRegistry.LEVEL_STEM_REGISTRY, Lifecycle.experimental());
            long j = optionallong.getAsLong();
            Iterator iterator = this.dimensions.entrySet().iterator();

            while (iterator.hasNext()) {
                Entry<ResourceKey<WorldDimension>, WorldDimension> entry = (Entry) iterator.next();
                ResourceKey<WorldDimension> resourcekey = (ResourceKey) entry.getKey();

                registrymaterials.register(resourcekey, (Object) (new WorldDimension(((WorldDimension) entry.getValue()).typeSupplier(), ((WorldDimension) entry.getValue()).generator().withSeed(j))), this.dimensions.lifecycle((WorldDimension) entry.getValue()));
            }
        } else {
            registrymaterials = this.dimensions;
        }

        GeneratorSettings generatorsettings;

        if (this.isDebug()) {
            generatorsettings = new GeneratorSettings(i, false, false, registrymaterials);
        } else {
            generatorsettings = new GeneratorSettings(i, this.generateFeatures(), this.generateBonusChest() && !flag, registrymaterials);
        }

        return generatorsettings;
    }
}
