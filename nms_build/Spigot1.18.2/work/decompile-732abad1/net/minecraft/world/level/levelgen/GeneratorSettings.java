package net.minecraft.world.level.levelgen;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonElement;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.core.Holder;
import net.minecraft.core.IRegistry;
import net.minecraft.core.IRegistryCustom;
import net.minecraft.core.IRegistryWritable;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.RegistryMaterials;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.dedicated.DedicatedServerProperties;
import net.minecraft.world.level.World;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.biome.WorldChunkManagerMultiNoise;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.DimensionManager;
import net.minecraft.world.level.dimension.WorldDimension;
import net.minecraft.world.level.levelgen.flat.GeneratorSettingsFlat;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.synth.NoiseGeneratorNormal;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

public class GeneratorSettings {

    public static final Codec<GeneratorSettings> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(Codec.LONG.fieldOf("seed").stable().forGetter(GeneratorSettings::seed), Codec.BOOL.fieldOf("generate_features").orElse(true).stable().forGetter(GeneratorSettings::generateFeatures), Codec.BOOL.fieldOf("bonus_chest").orElse(false).stable().forGetter(GeneratorSettings::generateBonusChest), RegistryCodecs.dataPackAwareCodec(IRegistry.LEVEL_STEM_REGISTRY, Lifecycle.stable(), WorldDimension.CODEC).xmap(WorldDimension::sortMap, Function.identity()).fieldOf("dimensions").forGetter(GeneratorSettings::dimensions), Codec.STRING.optionalFieldOf("legacy_custom_options").stable().forGetter((generatorsettings) -> {
            return generatorsettings.legacyCustomOptions;
        })).apply(instance, instance.stable(GeneratorSettings::new));
    }).comapFlatMap(GeneratorSettings::guardExperimental, Function.identity());
    private static final Logger LOGGER = LogUtils.getLogger();
    private final long seed;
    private final boolean generateFeatures;
    private final boolean generateBonusChest;
    private final IRegistry<WorldDimension> dimensions;
    private final Optional<String> legacyCustomOptions;

    private DataResult<GeneratorSettings> guardExperimental() {
        WorldDimension worlddimension = (WorldDimension) this.dimensions.get(WorldDimension.OVERWORLD);

        return worlddimension == null ? DataResult.error("Overworld settings missing") : (this.stable() ? DataResult.success(this, Lifecycle.stable()) : DataResult.success(this));
    }

    private boolean stable() {
        return WorldDimension.stable(this.seed, this.dimensions);
    }

    public GeneratorSettings(long i, boolean flag, boolean flag1, IRegistry<WorldDimension> iregistry) {
        this(i, flag, flag1, iregistry, Optional.empty());
        WorldDimension worlddimension = (WorldDimension) iregistry.get(WorldDimension.OVERWORLD);

        if (worlddimension == null) {
            throw new IllegalStateException("Overworld settings missing");
        }
    }

    private GeneratorSettings(long i, boolean flag, boolean flag1, IRegistry<WorldDimension> iregistry, Optional<String> optional) {
        this.seed = i;
        this.generateFeatures = flag;
        this.generateBonusChest = flag1;
        this.dimensions = iregistry;
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
        IRegistry<BiomeBase> iregistry = iregistrycustom.registryOrThrow(IRegistry.BIOME_REGISTRY);
        IRegistry<StructureSet> iregistry1 = iregistrycustom.registryOrThrow(IRegistry.STRUCTURE_SET_REGISTRY);
        IRegistry<GeneratorSettingBase> iregistry2 = iregistrycustom.registryOrThrow(IRegistry.NOISE_GENERATOR_SETTINGS_REGISTRY);
        IRegistry<NoiseGeneratorNormal.a> iregistry3 = iregistrycustom.registryOrThrow(IRegistry.NOISE_REGISTRY);

        return new ChunkGeneratorAbstract(iregistry1, iregistry3, WorldChunkManagerMultiNoise.a.OVERWORLD.biomeSource(iregistry, flag), i, iregistry2.getOrCreateHolder(resourcekey));
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

    public static IRegistry<WorldDimension> withOverworld(IRegistry<DimensionManager> iregistry, IRegistry<WorldDimension> iregistry1, ChunkGenerator chunkgenerator) {
        WorldDimension worlddimension = (WorldDimension) iregistry1.get(WorldDimension.OVERWORLD);
        Holder<DimensionManager> holder = worlddimension == null ? iregistry.getOrCreateHolder(DimensionManager.OVERWORLD_LOCATION) : worlddimension.typeHolder();

        return withOverworld(iregistry1, holder, chunkgenerator);
    }

    public static IRegistry<WorldDimension> withOverworld(IRegistry<WorldDimension> iregistry, Holder<DimensionManager> holder, ChunkGenerator chunkgenerator) {
        IRegistryWritable<WorldDimension> iregistrywritable = new RegistryMaterials<>(IRegistry.LEVEL_STEM_REGISTRY, Lifecycle.experimental(), (Function) null);

        iregistrywritable.register(WorldDimension.OVERWORLD, (Object) (new WorldDimension(holder, chunkgenerator)), Lifecycle.stable());
        Iterator iterator = iregistry.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry<ResourceKey<WorldDimension>, WorldDimension> entry = (Entry) iterator.next();
            ResourceKey<WorldDimension> resourcekey = (ResourceKey) entry.getKey();

            if (resourcekey != WorldDimension.OVERWORLD) {
                iregistrywritable.register(resourcekey, (Object) ((WorldDimension) entry.getValue()), iregistry.lifecycle((WorldDimension) entry.getValue()));
            }
        }

        return iregistrywritable;
    }

    public IRegistry<WorldDimension> dimensions() {
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

    public static GeneratorSettings create(IRegistryCustom iregistrycustom, DedicatedServerProperties.a dedicatedserverproperties_a) {
        long i = parseSeed(dedicatedserverproperties_a.levelSeed()).orElse((new Random()).nextLong());
        IRegistry<DimensionManager> iregistry = iregistrycustom.registryOrThrow(IRegistry.DIMENSION_TYPE_REGISTRY);
        IRegistry<BiomeBase> iregistry1 = iregistrycustom.registryOrThrow(IRegistry.BIOME_REGISTRY);
        IRegistry<StructureSet> iregistry2 = iregistrycustom.registryOrThrow(IRegistry.STRUCTURE_SET_REGISTRY);
        IRegistry<WorldDimension> iregistry3 = DimensionManager.defaultDimensions(iregistrycustom, i);
        String s = dedicatedserverproperties_a.levelType();
        byte b0 = -1;

        switch (s.hashCode()) {
            case -1100099890:
                if (s.equals("largebiomes")) {
                    b0 = 3;
                }
                break;
            case 3145593:
                if (s.equals("flat")) {
                    b0 = 0;
                }
                break;
            case 1045526590:
                if (s.equals("debug_all_block_states")) {
                    b0 = 1;
                }
                break;
            case 1271599715:
                if (s.equals("amplified")) {
                    b0 = 2;
                }
        }

        switch (b0) {
            case 0:
                Dynamic<JsonElement> dynamic = new Dynamic(JsonOps.INSTANCE, dedicatedserverproperties_a.generatorSettings());
                boolean flag = dedicatedserverproperties_a.generateStructures();
                DataResult dataresult = GeneratorSettingsFlat.CODEC.parse(dynamic);
                Logger logger = GeneratorSettings.LOGGER;

                Objects.requireNonNull(logger);
                return new GeneratorSettings(i, flag, false, withOverworld(iregistry, iregistry3, new ChunkProviderFlat(iregistry2, (GeneratorSettingsFlat) dataresult.resultOrPartial(logger::error).orElseGet(() -> {
                    return GeneratorSettingsFlat.getDefault(iregistry1, iregistry2);
                }))));
            case 1:
                return new GeneratorSettings(i, dedicatedserverproperties_a.generateStructures(), false, withOverworld(iregistry, iregistry3, new ChunkProviderDebug(iregistry2, iregistry1)));
            case 2:
                return new GeneratorSettings(i, dedicatedserverproperties_a.generateStructures(), false, withOverworld(iregistry, iregistry3, makeOverworld(iregistrycustom, i, GeneratorSettingBase.AMPLIFIED)));
            case 3:
                return new GeneratorSettings(i, dedicatedserverproperties_a.generateStructures(), false, withOverworld(iregistry, iregistry3, makeOverworld(iregistrycustom, i, GeneratorSettingBase.LARGE_BIOMES)));
            default:
                return new GeneratorSettings(i, dedicatedserverproperties_a.generateStructures(), false, withOverworld(iregistry, iregistry3, makeDefaultOverworld(iregistrycustom, i)));
        }
    }

    public GeneratorSettings withSeed(boolean flag, OptionalLong optionallong) {
        long i = optionallong.orElse(this.seed);
        Object object;

        if (optionallong.isPresent()) {
            IRegistryWritable<WorldDimension> iregistrywritable = new RegistryMaterials<>(IRegistry.LEVEL_STEM_REGISTRY, Lifecycle.experimental(), (Function) null);
            long j = optionallong.getAsLong();
            Iterator iterator = this.dimensions.entrySet().iterator();

            while (iterator.hasNext()) {
                Entry<ResourceKey<WorldDimension>, WorldDimension> entry = (Entry) iterator.next();
                ResourceKey<WorldDimension> resourcekey = (ResourceKey) entry.getKey();

                iregistrywritable.register(resourcekey, (Object) (new WorldDimension(((WorldDimension) entry.getValue()).typeHolder(), ((WorldDimension) entry.getValue()).generator().withSeed(j))), this.dimensions.lifecycle((WorldDimension) entry.getValue()));
            }

            object = iregistrywritable;
        } else {
            object = this.dimensions;
        }

        GeneratorSettings generatorsettings;

        if (this.isDebug()) {
            generatorsettings = new GeneratorSettings(i, false, false, (IRegistry) object);
        } else {
            generatorsettings = new GeneratorSettings(i, this.generateFeatures(), this.generateBonusChest() && !flag, (IRegistry) object);
        }

        return generatorsettings;
    }

    public static OptionalLong parseSeed(String s) {
        s = s.trim();
        if (StringUtils.isEmpty(s)) {
            return OptionalLong.empty();
        } else {
            try {
                return OptionalLong.of(Long.parseLong(s));
            } catch (NumberFormatException numberformatexception) {
                return OptionalLong.of((long) s.hashCode());
            }
        }
    }
}
