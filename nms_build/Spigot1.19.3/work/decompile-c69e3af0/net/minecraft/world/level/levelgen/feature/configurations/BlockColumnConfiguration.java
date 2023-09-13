package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.core.EnumDirection;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.stateproviders.WorldGenFeatureStateProvider;

public record BlockColumnConfiguration(List<BlockColumnConfiguration.a> layers, EnumDirection direction, BlockPredicate allowedPlacement, boolean prioritizeTip) implements WorldGenFeatureConfiguration {

    public static final Codec<BlockColumnConfiguration> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(BlockColumnConfiguration.a.CODEC.listOf().fieldOf("layers").forGetter(BlockColumnConfiguration::layers), EnumDirection.CODEC.fieldOf("direction").forGetter(BlockColumnConfiguration::direction), BlockPredicate.CODEC.fieldOf("allowed_placement").forGetter(BlockColumnConfiguration::allowedPlacement), Codec.BOOL.fieldOf("prioritize_tip").forGetter(BlockColumnConfiguration::prioritizeTip)).apply(instance, BlockColumnConfiguration::new);
    });

    public static BlockColumnConfiguration.a layer(IntProvider intprovider, WorldGenFeatureStateProvider worldgenfeaturestateprovider) {
        return new BlockColumnConfiguration.a(intprovider, worldgenfeaturestateprovider);
    }

    public static BlockColumnConfiguration simple(IntProvider intprovider, WorldGenFeatureStateProvider worldgenfeaturestateprovider) {
        return new BlockColumnConfiguration(List.of(layer(intprovider, worldgenfeaturestateprovider)), EnumDirection.UP, BlockPredicate.ONLY_IN_AIR_PREDICATE, false);
    }

    public static record a(IntProvider height, WorldGenFeatureStateProvider state) {

        public static final Codec<BlockColumnConfiguration.a> CODEC = RecordCodecBuilder.create((instance) -> {
            return instance.group(IntProvider.NON_NEGATIVE_CODEC.fieldOf("height").forGetter(BlockColumnConfiguration.a::height), WorldGenFeatureStateProvider.CODEC.fieldOf("provider").forGetter(BlockColumnConfiguration.a::state)).apply(instance, BlockColumnConfiguration.a::new);
        });
    }
}
