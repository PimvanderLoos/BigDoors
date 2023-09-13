package net.minecraft.world.level.levelgen.presets;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.core.Holder;
import net.minecraft.core.IRegistry;
import net.minecraft.core.IRegistryWritable;
import net.minecraft.core.RegistryMaterials;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.dimension.WorldDimension;
import net.minecraft.world.level.levelgen.GeneratorSettings;

public class WorldPreset {

    public static final Codec<WorldPreset> DIRECT_CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(Codec.unboundedMap(ResourceKey.codec(IRegistry.LEVEL_STEM_REGISTRY), WorldDimension.CODEC).fieldOf("dimensions").forGetter((worldpreset) -> {
            return worldpreset.dimensions;
        })).apply(instance, WorldPreset::new);
    }).flatXmap(WorldPreset::requireOverworld, WorldPreset::requireOverworld);
    public static final Codec<Holder<WorldPreset>> CODEC = RegistryFileCodec.create(IRegistry.WORLD_PRESET_REGISTRY, WorldPreset.DIRECT_CODEC);
    private final Map<ResourceKey<WorldDimension>, WorldDimension> dimensions;

    public WorldPreset(Map<ResourceKey<WorldDimension>, WorldDimension> map) {
        this.dimensions = map;
    }

    private IRegistry<WorldDimension> createRegistry() {
        IRegistryWritable<WorldDimension> iregistrywritable = new RegistryMaterials<>(IRegistry.LEVEL_STEM_REGISTRY, Lifecycle.experimental(), (Function) null);

        WorldDimension.keysInOrder(this.dimensions.keySet().stream()).forEach((resourcekey) -> {
            WorldDimension worlddimension = (WorldDimension) this.dimensions.get(resourcekey);

            if (worlddimension != null) {
                iregistrywritable.register(resourcekey, (Object) worlddimension, Lifecycle.stable());
            }

        });
        return iregistrywritable.freeze();
    }

    public GeneratorSettings createWorldGenSettings(long i, boolean flag, boolean flag1) {
        return new GeneratorSettings(i, flag, flag1, this.createRegistry());
    }

    public GeneratorSettings recreateWorldGenSettings(GeneratorSettings generatorsettings) {
        return this.createWorldGenSettings(generatorsettings.seed(), generatorsettings.generateStructures(), generatorsettings.generateBonusChest());
    }

    public Optional<WorldDimension> overworld() {
        return Optional.ofNullable((WorldDimension) this.dimensions.get(WorldDimension.OVERWORLD));
    }

    public WorldDimension overworldOrThrow() {
        return (WorldDimension) this.overworld().orElseThrow(() -> {
            return new IllegalStateException("Can't find overworld in this preset");
        });
    }

    private static DataResult<WorldPreset> requireOverworld(WorldPreset worldpreset) {
        return worldpreset.overworld().isEmpty() ? DataResult.error("Missing overworld dimension") : DataResult.success(worldpreset, Lifecycle.stable());
    }
}
