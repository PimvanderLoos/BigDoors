package net.minecraft.server;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Supplier;

public class WorldGenFeatureVillageConfiguration implements WorldGenFeatureConfiguration {

    public static final Codec<WorldGenFeatureVillageConfiguration> a = RecordCodecBuilder.create((instance) -> {
        return instance.group(WorldGenFeatureDefinedStructurePoolTemplate.b.fieldOf("start_pool").forGetter(WorldGenFeatureVillageConfiguration::c), Codec.intRange(0, 7).fieldOf("size").forGetter(WorldGenFeatureVillageConfiguration::b)).apply(instance, WorldGenFeatureVillageConfiguration::new);
    });
    private final Supplier<WorldGenFeatureDefinedStructurePoolTemplate> b;
    private final int c;

    public WorldGenFeatureVillageConfiguration(Supplier<WorldGenFeatureDefinedStructurePoolTemplate> supplier, int i) {
        this.b = supplier;
        this.c = i;
    }

    public int b() {
        return this.c;
    }

    public Supplier<WorldGenFeatureDefinedStructurePoolTemplate> c() {
        return this.b;
    }
}
