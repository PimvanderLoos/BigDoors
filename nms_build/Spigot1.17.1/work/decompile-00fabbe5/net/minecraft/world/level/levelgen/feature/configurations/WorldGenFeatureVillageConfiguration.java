package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Supplier;
import net.minecraft.world.level.levelgen.feature.structures.WorldGenFeatureDefinedStructurePoolTemplate;

public class WorldGenFeatureVillageConfiguration implements WorldGenFeatureConfiguration {

    public static final Codec<WorldGenFeatureVillageConfiguration> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(WorldGenFeatureDefinedStructurePoolTemplate.CODEC.fieldOf("start_pool").forGetter(WorldGenFeatureVillageConfiguration::c), Codec.intRange(0, 7).fieldOf("size").forGetter(WorldGenFeatureVillageConfiguration::b)).apply(instance, WorldGenFeatureVillageConfiguration::new);
    });
    private final Supplier<WorldGenFeatureDefinedStructurePoolTemplate> startPool;
    private final int maxDepth;

    public WorldGenFeatureVillageConfiguration(Supplier<WorldGenFeatureDefinedStructurePoolTemplate> supplier, int i) {
        this.startPool = supplier;
        this.maxDepth = i;
    }

    public int b() {
        return this.maxDepth;
    }

    public Supplier<WorldGenFeatureDefinedStructurePoolTemplate> c() {
        return this.startPool;
    }
}
