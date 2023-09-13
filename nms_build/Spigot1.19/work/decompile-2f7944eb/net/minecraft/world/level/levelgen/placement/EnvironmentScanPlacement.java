package net.minecraft.world.level.levelgen.placement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.stream.Stream;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;

public class EnvironmentScanPlacement extends PlacementModifier {

    private final EnumDirection directionOfSearch;
    private final BlockPredicate targetCondition;
    private final BlockPredicate allowedSearchCondition;
    private final int maxSteps;
    public static final Codec<EnvironmentScanPlacement> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(EnumDirection.VERTICAL_CODEC.fieldOf("direction_of_search").forGetter((environmentscanplacement) -> {
            return environmentscanplacement.directionOfSearch;
        }), BlockPredicate.CODEC.fieldOf("target_condition").forGetter((environmentscanplacement) -> {
            return environmentscanplacement.targetCondition;
        }), BlockPredicate.CODEC.optionalFieldOf("allowed_search_condition", BlockPredicate.alwaysTrue()).forGetter((environmentscanplacement) -> {
            return environmentscanplacement.allowedSearchCondition;
        }), Codec.intRange(1, 32).fieldOf("max_steps").forGetter((environmentscanplacement) -> {
            return environmentscanplacement.maxSteps;
        })).apply(instance, EnvironmentScanPlacement::new);
    });

    private EnvironmentScanPlacement(EnumDirection enumdirection, BlockPredicate blockpredicate, BlockPredicate blockpredicate1, int i) {
        this.directionOfSearch = enumdirection;
        this.targetCondition = blockpredicate;
        this.allowedSearchCondition = blockpredicate1;
        this.maxSteps = i;
    }

    public static EnvironmentScanPlacement scanningFor(EnumDirection enumdirection, BlockPredicate blockpredicate, BlockPredicate blockpredicate1, int i) {
        return new EnvironmentScanPlacement(enumdirection, blockpredicate, blockpredicate1, i);
    }

    public static EnvironmentScanPlacement scanningFor(EnumDirection enumdirection, BlockPredicate blockpredicate, int i) {
        return scanningFor(enumdirection, blockpredicate, BlockPredicate.alwaysTrue(), i);
    }

    @Override
    public Stream<BlockPosition> getPositions(PlacementContext placementcontext, RandomSource randomsource, BlockPosition blockposition) {
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = blockposition.mutable();
        GeneratorAccessSeed generatoraccessseed = placementcontext.getLevel();

        if (!this.allowedSearchCondition.test(generatoraccessseed, blockposition_mutableblockposition)) {
            return Stream.of();
        } else {
            int i = 0;

            while (true) {
                if (i < this.maxSteps) {
                    if (this.targetCondition.test(generatoraccessseed, blockposition_mutableblockposition)) {
                        return Stream.of(blockposition_mutableblockposition);
                    }

                    blockposition_mutableblockposition.move(this.directionOfSearch);
                    if (generatoraccessseed.isOutsideBuildHeight(blockposition_mutableblockposition.getY())) {
                        return Stream.of();
                    }

                    if (this.allowedSearchCondition.test(generatoraccessseed, blockposition_mutableblockposition)) {
                        ++i;
                        continue;
                    }
                }

                if (this.targetCondition.test(generatoraccessseed, blockposition_mutableblockposition)) {
                    return Stream.of(blockposition_mutableblockposition);
                }

                return Stream.of();
            }
        }
    }

    @Override
    public PlacementModifierType<?> type() {
        return PlacementModifierType.ENVIRONMENT_SCAN;
    }
}
