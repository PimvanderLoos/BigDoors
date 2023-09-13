package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.stateproviders.WorldGenFeatureStateProvider;

public record BlockColumnConfiguration(List<BlockColumnConfiguration.a> b, EnumDirection c, BlockPredicate d, boolean e) implements WorldGenFeatureConfiguration {

    private final List<BlockColumnConfiguration.a> layers;
    private final EnumDirection direction;
    private final BlockPredicate allowedPlacement;
    private final boolean prioritizeTip;
    public static final Codec<BlockColumnConfiguration> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(BlockColumnConfiguration.a.CODEC.listOf().fieldOf("layers").forGetter(BlockColumnConfiguration::layers), EnumDirection.CODEC.fieldOf("direction").forGetter(BlockColumnConfiguration::direction), BlockPredicate.CODEC.fieldOf("allowed_placement").forGetter(BlockColumnConfiguration::allowedPlacement), Codec.BOOL.fieldOf("prioritize_tip").forGetter(BlockColumnConfiguration::prioritizeTip)).apply(instance, BlockColumnConfiguration::new);
    });

    public BlockColumnConfiguration(List<BlockColumnConfiguration.a> list, EnumDirection enumdirection, BlockPredicate blockpredicate, boolean flag) {
        this.layers = list;
        this.direction = enumdirection;
        this.allowedPlacement = blockpredicate;
        this.prioritizeTip = flag;
    }

    public static BlockColumnConfiguration.a layer(IntProvider intprovider, WorldGenFeatureStateProvider worldgenfeaturestateprovider) {
        return new BlockColumnConfiguration.a(intprovider, worldgenfeaturestateprovider);
    }

    public static BlockColumnConfiguration simple(IntProvider intprovider, WorldGenFeatureStateProvider worldgenfeaturestateprovider) {
        return new BlockColumnConfiguration(List.of(layer(intprovider, worldgenfeaturestateprovider)), EnumDirection.UP, BlockPredicate.matchesBlock(Blocks.AIR, BlockPosition.ZERO), false);
    }

    public List<BlockColumnConfiguration.a> layers() {
        return this.layers;
    }

    public EnumDirection direction() {
        return this.direction;
    }

    public BlockPredicate allowedPlacement() {
        return this.allowedPlacement;
    }

    public boolean prioritizeTip() {
        return this.prioritizeTip;
    }

    public static record a(IntProvider b, WorldGenFeatureStateProvider c) {

        private final IntProvider height;
        private final WorldGenFeatureStateProvider state;
        public static final Codec<BlockColumnConfiguration.a> CODEC = RecordCodecBuilder.create((instance) -> {
            return instance.group(IntProvider.NON_NEGATIVE_CODEC.fieldOf("height").forGetter(BlockColumnConfiguration.a::height), WorldGenFeatureStateProvider.CODEC.fieldOf("provider").forGetter(BlockColumnConfiguration.a::state)).apply(instance, BlockColumnConfiguration.a::new);
        });

        public a(IntProvider intprovider, WorldGenFeatureStateProvider worldgenfeaturestateprovider) {
            this.height = intprovider;
            this.state = worldgenfeaturestateprovider;
        }

        public IntProvider height() {
            return this.height;
        }

        public WorldGenFeatureStateProvider state() {
            return this.state;
        }
    }
}
