package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.stateproviders.RuleBasedBlockStateProvider;

public record WorldGenFeatureCircleConfiguration(RuleBasedBlockStateProvider stateProvider, BlockPredicate target, IntProvider radius, int halfHeight) implements WorldGenFeatureConfiguration {

    public static final Codec<WorldGenFeatureCircleConfiguration> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(RuleBasedBlockStateProvider.CODEC.fieldOf("state_provider").forGetter(WorldGenFeatureCircleConfiguration::stateProvider), BlockPredicate.CODEC.fieldOf("target").forGetter(WorldGenFeatureCircleConfiguration::target), IntProvider.codec(0, 8).fieldOf("radius").forGetter(WorldGenFeatureCircleConfiguration::radius), Codec.intRange(0, 4).fieldOf("half_height").forGetter(WorldGenFeatureCircleConfiguration::halfHeight)).apply(instance, WorldGenFeatureCircleConfiguration::new);
    });
}
