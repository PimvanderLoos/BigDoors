package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
import java.util.function.Supplier;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.chunk.ChunkGenerator;

public class WorldGenFeatureRandomChoiceConfigurationWeight {

    public static final Codec<WorldGenFeatureRandomChoiceConfigurationWeight> a = RecordCodecBuilder.create((instance) -> {
        return instance.group(WorldGenFeatureConfigured.b.fieldOf("feature").forGetter((worldgenfeaturerandomchoiceconfigurationweight) -> {
            return worldgenfeaturerandomchoiceconfigurationweight.b;
        }), Codec.floatRange(0.0F, 1.0F).fieldOf("chance").forGetter((worldgenfeaturerandomchoiceconfigurationweight) -> {
            return worldgenfeaturerandomchoiceconfigurationweight.c;
        })).apply(instance, WorldGenFeatureRandomChoiceConfigurationWeight::new);
    });
    public final Supplier<WorldGenFeatureConfigured<?, ?>> b;
    public final float c;

    public WorldGenFeatureRandomChoiceConfigurationWeight(WorldGenFeatureConfigured<?, ?> worldgenfeatureconfigured, float f) {
        this(() -> {
            return worldgenfeatureconfigured;
        }, f);
    }

    private WorldGenFeatureRandomChoiceConfigurationWeight(Supplier<WorldGenFeatureConfigured<?, ?>> supplier, float f) {
        this.b = supplier;
        this.c = f;
    }

    public boolean a(GeneratorAccessSeed generatoraccessseed, ChunkGenerator chunkgenerator, Random random, BlockPosition blockposition) {
        return ((WorldGenFeatureConfigured) this.b.get()).a(generatoraccessseed, chunkgenerator, random, blockposition);
    }
}
