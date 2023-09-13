package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.world.level.levelgen.structure.pools.WorldGenFeatureDefinedStructurePoolTemplate;

public class WorldGenFeatureVillageConfiguration implements WorldGenFeatureConfiguration {

    public static final Codec<WorldGenFeatureVillageConfiguration> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(WorldGenFeatureDefinedStructurePoolTemplate.CODEC.fieldOf("start_pool").forGetter(WorldGenFeatureVillageConfiguration::startPool), Codec.intRange(0, 7).fieldOf("size").forGetter(WorldGenFeatureVillageConfiguration::maxDepth)).apply(instance, WorldGenFeatureVillageConfiguration::new);
    });
    private final Holder<WorldGenFeatureDefinedStructurePoolTemplate> startPool;
    private final int maxDepth;

    public WorldGenFeatureVillageConfiguration(Holder<WorldGenFeatureDefinedStructurePoolTemplate> holder, int i) {
        this.startPool = holder;
        this.maxDepth = i;
    }

    public int maxDepth() {
        return this.maxDepth;
    }

    public Holder<WorldGenFeatureDefinedStructurePoolTemplate> startPool() {
        return this.startPool;
    }
}
