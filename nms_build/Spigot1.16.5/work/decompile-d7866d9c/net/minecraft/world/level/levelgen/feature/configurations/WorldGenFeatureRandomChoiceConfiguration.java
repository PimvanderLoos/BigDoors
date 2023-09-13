package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.world.level.levelgen.feature.WorldGenFeatureConfigured;
import net.minecraft.world.level.levelgen.feature.WorldGenFeatureRandomChoiceConfigurationWeight;

public class WorldGenFeatureRandomChoiceConfiguration implements WorldGenFeatureConfiguration {

    public static final Codec<WorldGenFeatureRandomChoiceConfiguration> a = RecordCodecBuilder.create((instance) -> {
        return instance.apply2(WorldGenFeatureRandomChoiceConfiguration::new, WorldGenFeatureRandomChoiceConfigurationWeight.a.listOf().fieldOf("features").forGetter((worldgenfeaturerandomchoiceconfiguration) -> {
            return worldgenfeaturerandomchoiceconfiguration.b;
        }), WorldGenFeatureConfigured.b.fieldOf("default").forGetter((worldgenfeaturerandomchoiceconfiguration) -> {
            return worldgenfeaturerandomchoiceconfiguration.c;
        }));
    });
    public final List<WorldGenFeatureRandomChoiceConfigurationWeight> b;
    public final Supplier<WorldGenFeatureConfigured<?, ?>> c;

    public WorldGenFeatureRandomChoiceConfiguration(List<WorldGenFeatureRandomChoiceConfigurationWeight> list, WorldGenFeatureConfigured<?, ?> worldgenfeatureconfigured) {
        this(list, () -> {
            return worldgenfeatureconfigured;
        });
    }

    private WorldGenFeatureRandomChoiceConfiguration(List<WorldGenFeatureRandomChoiceConfigurationWeight> list, Supplier<WorldGenFeatureConfigured<?, ?>> supplier) {
        this.b = list;
        this.c = supplier;
    }

    @Override
    public Stream<WorldGenFeatureConfigured<?, ?>> an_() {
        return Stream.concat(this.b.stream().flatMap((worldgenfeaturerandomchoiceconfigurationweight) -> {
            return ((WorldGenFeatureConfigured) worldgenfeaturerandomchoiceconfigurationweight.b.get()).d();
        }), ((WorldGenFeatureConfigured) this.c.get()).d());
    }
}
