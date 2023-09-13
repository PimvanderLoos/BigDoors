package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.world.level.levelgen.feature.WorldGenFeatureConfigured;

public class WorldGenFeatureChoiceConfiguration implements WorldGenFeatureConfiguration {

    public static final Codec<WorldGenFeatureChoiceConfiguration> a = RecordCodecBuilder.create((instance) -> {
        return instance.group(WorldGenFeatureConfigured.b.fieldOf("feature_true").forGetter((worldgenfeaturechoiceconfiguration) -> {
            return worldgenfeaturechoiceconfiguration.b;
        }), WorldGenFeatureConfigured.b.fieldOf("feature_false").forGetter((worldgenfeaturechoiceconfiguration) -> {
            return worldgenfeaturechoiceconfiguration.c;
        })).apply(instance, WorldGenFeatureChoiceConfiguration::new);
    });
    public final Supplier<WorldGenFeatureConfigured<?, ?>> b;
    public final Supplier<WorldGenFeatureConfigured<?, ?>> c;

    public WorldGenFeatureChoiceConfiguration(Supplier<WorldGenFeatureConfigured<?, ?>> supplier, Supplier<WorldGenFeatureConfigured<?, ?>> supplier1) {
        this.b = supplier;
        this.c = supplier1;
    }

    @Override
    public Stream<WorldGenFeatureConfigured<?, ?>> an_() {
        return Stream.concat(((WorldGenFeatureConfigured) this.b.get()).d(), ((WorldGenFeatureConfigured) this.c.get()).d());
    }
}
