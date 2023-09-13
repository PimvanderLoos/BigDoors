package net.minecraft.world.level.levelgen;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.function.Function;
import net.minecraft.core.Holder;
import net.minecraft.core.IRegistry;
import net.minecraft.core.IRegistryCustom;
import net.minecraft.core.IRegistryWritable;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.RegistryMaterials;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.World;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionManager;
import net.minecraft.world.level.dimension.WorldDimension;
import org.apache.commons.lang3.StringUtils;

public class GeneratorSettings {

    public static final Codec<GeneratorSettings> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(Codec.LONG.fieldOf("seed").stable().forGetter(GeneratorSettings::seed), Codec.BOOL.fieldOf("generate_features").orElse(true).stable().forGetter(GeneratorSettings::generateStructures), Codec.BOOL.fieldOf("bonus_chest").orElse(false).stable().forGetter(GeneratorSettings::generateBonusChest), RegistryCodecs.dataPackAwareCodec(IRegistry.LEVEL_STEM_REGISTRY, Lifecycle.stable(), WorldDimension.CODEC).xmap(WorldDimension::sortMap, Function.identity()).fieldOf("dimensions").forGetter(GeneratorSettings::dimensions), Codec.STRING.optionalFieldOf("legacy_custom_options").stable().forGetter((generatorsettings) -> {
            return generatorsettings.legacyCustomOptions;
        })).apply(instance, instance.stable(GeneratorSettings::new));
    }).comapFlatMap(GeneratorSettings::guardExperimental, Function.identity());
    private final long seed;
    private final boolean generateStructures;
    private final boolean generateBonusChest;
    private final IRegistry<WorldDimension> dimensions;
    private final Optional<String> legacyCustomOptions;

    private DataResult<GeneratorSettings> guardExperimental() {
        WorldDimension worlddimension = (WorldDimension) this.dimensions.get(WorldDimension.OVERWORLD);

        return worlddimension == null ? DataResult.error("Overworld settings missing") : (this.stable() ? DataResult.success(this, Lifecycle.stable()) : DataResult.success(this));
    }

    private boolean stable() {
        return WorldDimension.stable(this.dimensions);
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
        this.generateStructures = flag;
        this.generateBonusChest = flag1;
        this.dimensions = iregistry;
        this.legacyCustomOptions = optional;
    }

    public long seed() {
        return this.seed;
    }

    public boolean generateStructures() {
        return this.generateStructures;
    }

    public boolean generateBonusChest() {
        return this.generateBonusChest;
    }

    public static GeneratorSettings replaceOverworldGenerator(IRegistryCustom iregistrycustom, GeneratorSettings generatorsettings, ChunkGenerator chunkgenerator) {
        IRegistry<DimensionManager> iregistry = iregistrycustom.registryOrThrow(IRegistry.DIMENSION_TYPE_REGISTRY);
        IRegistry<WorldDimension> iregistry1 = withOverworld(iregistry, generatorsettings.dimensions(), chunkgenerator);

        return new GeneratorSettings(generatorsettings.seed(), generatorsettings.generateStructures(), generatorsettings.generateBonusChest(), iregistry1);
    }

    public static IRegistry<WorldDimension> withOverworld(IRegistry<DimensionManager> iregistry, IRegistry<WorldDimension> iregistry1, ChunkGenerator chunkgenerator) {
        WorldDimension worlddimension = (WorldDimension) iregistry1.get(WorldDimension.OVERWORLD);
        Holder<DimensionManager> holder = worlddimension == null ? iregistry.getOrCreateHolderOrThrow(BuiltinDimensionTypes.OVERWORLD) : worlddimension.typeHolder();

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
        return new GeneratorSettings(this.seed, this.generateStructures, true, this.dimensions, this.legacyCustomOptions);
    }

    public GeneratorSettings withStructuresToggled() {
        return new GeneratorSettings(this.seed, !this.generateStructures, this.generateBonusChest, this.dimensions);
    }

    public GeneratorSettings withBonusChestToggled() {
        return new GeneratorSettings(this.seed, this.generateStructures, !this.generateBonusChest, this.dimensions);
    }

    public GeneratorSettings withSeed(boolean flag, OptionalLong optionallong) {
        long i = optionallong.orElse(this.seed);
        Object object;

        if (optionallong.isPresent()) {
            IRegistryWritable<WorldDimension> iregistrywritable = new RegistryMaterials<>(IRegistry.LEVEL_STEM_REGISTRY, Lifecycle.experimental(), (Function) null);
            Iterator iterator = this.dimensions.entrySet().iterator();

            while (iterator.hasNext()) {
                Entry<ResourceKey<WorldDimension>, WorldDimension> entry = (Entry) iterator.next();
                ResourceKey<WorldDimension> resourcekey = (ResourceKey) entry.getKey();

                iregistrywritable.register(resourcekey, (Object) (new WorldDimension(((WorldDimension) entry.getValue()).typeHolder(), ((WorldDimension) entry.getValue()).generator())), this.dimensions.lifecycle((WorldDimension) entry.getValue()));
            }

            object = iregistrywritable;
        } else {
            object = this.dimensions;
        }

        GeneratorSettings generatorsettings;

        if (this.isDebug()) {
            generatorsettings = new GeneratorSettings(i, false, false, (IRegistry) object);
        } else {
            generatorsettings = new GeneratorSettings(i, this.generateStructures(), this.generateBonusChest() && !flag, (IRegistry) object);
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
