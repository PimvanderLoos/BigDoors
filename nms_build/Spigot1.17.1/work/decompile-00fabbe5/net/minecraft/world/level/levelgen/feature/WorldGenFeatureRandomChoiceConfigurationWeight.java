package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
import java.util.function.Supplier;
import net.minecraft.core.BlockPosition;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.chunk.ChunkGenerator;

public class WorldGenFeatureRandomChoiceConfigurationWeight {

    public static final Codec<WorldGenFeatureRandomChoiceConfigurationWeight> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(WorldGenFeatureConfigured.CODEC.fieldOf("feature").flatXmap(ExtraCodecs.c(), ExtraCodecs.c()).forGetter((worldgenfeaturerandomchoiceconfigurationweight) -> {
            return worldgenfeaturerandomchoiceconfigurationweight.feature;
        }), Codec.floatRange(0.0F, 1.0F).fieldOf("chance").forGetter((worldgenfeaturerandomchoiceconfigurationweight) -> {
            return worldgenfeaturerandomchoiceconfigurationweight.chance;
        })).apply(instance, WorldGenFeatureRandomChoiceConfigurationWeight::new);
    });
    public final Supplier<WorldGenFeatureConfigured<?, ?>> feature;
    public final float chance;

    public WorldGenFeatureRandomChoiceConfigurationWeight(WorldGenFeatureConfigured<?, ?> worldgenfeatureconfigured, float f) {
        this(() -> {
            return worldgenfeatureconfigured;
        }, f);
    }

    private WorldGenFeatureRandomChoiceConfigurationWeight(Supplier<WorldGenFeatureConfigured<?, ?>> supplier, float f) {
        this.feature = supplier;
        this.chance = f;
    }

    public boolean a(GeneratorAccessSeed generatoraccessseed, ChunkGenerator chunkgenerator, Random random, BlockPosition blockposition) {
        return ((WorldGenFeatureConfigured) this.feature.get()).a(generatoraccessseed, chunkgenerator, random, blockposition);
    }
}
