package net.minecraft.server;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;

public class WorldGenFeatureRandomChoiceConfigurationWeight<FC extends WorldGenFeatureConfiguration> {

    public static final Codec<WorldGenFeatureRandomChoiceConfigurationWeight<?>> a = RecordCodecBuilder.create((instance) -> {
        return instance.group(WorldGenFeatureConfigured.b.fieldOf("feature").forGetter((worldgenfeaturerandomchoiceconfigurationweight) -> {
            return worldgenfeaturerandomchoiceconfigurationweight.b;
        }), Codec.FLOAT.fieldOf("chance").forGetter((worldgenfeaturerandomchoiceconfigurationweight) -> {
            return worldgenfeaturerandomchoiceconfigurationweight.c;
        })).apply(instance, WorldGenFeatureRandomChoiceConfigurationWeight::new);
    });
    public final WorldGenFeatureConfigured<FC, ?> b;
    public final float c;

    public WorldGenFeatureRandomChoiceConfigurationWeight(WorldGenFeatureConfigured<FC, ?> worldgenfeatureconfigured, float f) {
        this.b = worldgenfeatureconfigured;
        this.c = f;
    }

    public boolean a(GeneratorAccessSeed generatoraccessseed, StructureManager structuremanager, ChunkGenerator chunkgenerator, Random random, BlockPosition blockposition) {
        return this.b.a(generatoraccessseed, structuremanager, chunkgenerator, random, blockposition);
    }
}
