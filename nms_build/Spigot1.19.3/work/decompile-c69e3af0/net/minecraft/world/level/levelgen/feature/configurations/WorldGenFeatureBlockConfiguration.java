package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.levelgen.feature.stateproviders.WorldGenFeatureStateProvider;

public record WorldGenFeatureBlockConfiguration(WorldGenFeatureStateProvider toPlace) implements WorldGenFeatureConfiguration {

    public static final Codec<WorldGenFeatureBlockConfiguration> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(WorldGenFeatureStateProvider.CODEC.fieldOf("to_place").forGetter((worldgenfeatureblockconfiguration) -> {
            return worldgenfeatureblockconfiguration.toPlace;
        })).apply(instance, WorldGenFeatureBlockConfiguration::new);
    });
}
