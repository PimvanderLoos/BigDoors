package net.minecraft.world.level.levelgen.feature.stateproviders;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Iterator;
import java.util.List;
import net.minecraft.core.BlockPosition;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;

public record RuleBasedBlockStateProvider(WorldGenFeatureStateProvider fallback, List<RuleBasedBlockStateProvider.a> rules) {

    public static final Codec<RuleBasedBlockStateProvider> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(WorldGenFeatureStateProvider.CODEC.fieldOf("fallback").forGetter(RuleBasedBlockStateProvider::fallback), RuleBasedBlockStateProvider.a.CODEC.listOf().fieldOf("rules").forGetter(RuleBasedBlockStateProvider::rules)).apply(instance, RuleBasedBlockStateProvider::new);
    });

    public static RuleBasedBlockStateProvider simple(WorldGenFeatureStateProvider worldgenfeaturestateprovider) {
        return new RuleBasedBlockStateProvider(worldgenfeaturestateprovider, List.of());
    }

    public static RuleBasedBlockStateProvider simple(Block block) {
        return simple((WorldGenFeatureStateProvider) WorldGenFeatureStateProvider.simple(block));
    }

    public IBlockData getState(GeneratorAccessSeed generatoraccessseed, RandomSource randomsource, BlockPosition blockposition) {
        Iterator iterator = this.rules.iterator();

        RuleBasedBlockStateProvider.a rulebasedblockstateprovider_a;

        do {
            if (!iterator.hasNext()) {
                return this.fallback.getState(randomsource, blockposition);
            }

            rulebasedblockstateprovider_a = (RuleBasedBlockStateProvider.a) iterator.next();
        } while (!rulebasedblockstateprovider_a.ifTrue().test(generatoraccessseed, blockposition));

        return rulebasedblockstateprovider_a.then().getState(randomsource, blockposition);
    }

    public static record a(BlockPredicate ifTrue, WorldGenFeatureStateProvider then) {

        public static final Codec<RuleBasedBlockStateProvider.a> CODEC = RecordCodecBuilder.create((instance) -> {
            return instance.group(BlockPredicate.CODEC.fieldOf("if_true").forGetter(RuleBasedBlockStateProvider.a::ifTrue), WorldGenFeatureStateProvider.CODEC.fieldOf("then").forGetter(RuleBasedBlockStateProvider.a::then)).apply(instance, RuleBasedBlockStateProvider.a::new);
        });
    }
}
