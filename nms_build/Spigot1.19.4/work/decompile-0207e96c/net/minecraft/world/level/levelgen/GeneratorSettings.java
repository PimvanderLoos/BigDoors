package net.minecraft.world.level.levelgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.IRegistryCustom;
import net.minecraft.core.registries.Registries;

public record GeneratorSettings(WorldOptions options, WorldDimensions dimensions) {

    public static final Codec<GeneratorSettings> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(WorldOptions.CODEC.forGetter(GeneratorSettings::options), WorldDimensions.CODEC.forGetter(GeneratorSettings::dimensions)).apply(instance, instance.stable(GeneratorSettings::new));
    });

    public static <T> DataResult<T> encode(DynamicOps<T> dynamicops, WorldOptions worldoptions, WorldDimensions worlddimensions) {
        return GeneratorSettings.CODEC.encodeStart(dynamicops, new GeneratorSettings(worldoptions, worlddimensions));
    }

    public static <T> DataResult<T> encode(DynamicOps<T> dynamicops, WorldOptions worldoptions, IRegistryCustom iregistrycustom) {
        return encode(dynamicops, worldoptions, new WorldDimensions(iregistrycustom.registryOrThrow(Registries.LEVEL_STEM)));
    }
}
