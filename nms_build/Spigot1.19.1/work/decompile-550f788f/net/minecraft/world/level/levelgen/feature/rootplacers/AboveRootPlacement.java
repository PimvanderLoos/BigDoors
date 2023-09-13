package net.minecraft.world.level.levelgen.feature.rootplacers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.levelgen.feature.stateproviders.WorldGenFeatureStateProvider;

public record AboveRootPlacement(WorldGenFeatureStateProvider aboveRootProvider, float aboveRootPlacementChance) {

    public static final Codec<AboveRootPlacement> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(WorldGenFeatureStateProvider.CODEC.fieldOf("above_root_provider").forGetter((aboverootplacement) -> {
            return aboverootplacement.aboveRootProvider;
        }), Codec.floatRange(0.0F, 1.0F).fieldOf("above_root_placement_chance").forGetter((aboverootplacement) -> {
            return aboverootplacement.aboveRootPlacementChance;
        })).apply(instance, AboveRootPlacement::new);
    });
}
