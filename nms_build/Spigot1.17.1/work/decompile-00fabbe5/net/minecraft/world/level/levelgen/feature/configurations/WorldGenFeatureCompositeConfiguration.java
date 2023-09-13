package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.core.IRegistry;
import net.minecraft.world.level.levelgen.feature.WorldGenFeatureConfigured;
import net.minecraft.world.level.levelgen.placement.WorldGenDecoratorConfigured;

public class WorldGenFeatureCompositeConfiguration implements WorldGenFeatureConfiguration {

    public static final Codec<WorldGenFeatureCompositeConfiguration> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(WorldGenFeatureConfigured.CODEC.fieldOf("feature").forGetter((worldgenfeaturecompositeconfiguration) -> {
            return worldgenfeaturecompositeconfiguration.feature;
        }), WorldGenDecoratorConfigured.CODEC.fieldOf("decorator").forGetter((worldgenfeaturecompositeconfiguration) -> {
            return worldgenfeaturecompositeconfiguration.decorator;
        })).apply(instance, WorldGenFeatureCompositeConfiguration::new);
    });
    public final Supplier<WorldGenFeatureConfigured<?, ?>> feature;
    public final WorldGenDecoratorConfigured<?> decorator;

    public WorldGenFeatureCompositeConfiguration(Supplier<WorldGenFeatureConfigured<?, ?>> supplier, WorldGenDecoratorConfigured<?> worldgendecoratorconfigured) {
        this.feature = supplier;
        this.decorator = worldgendecoratorconfigured;
    }

    public String toString() {
        return String.format("< %s [%s | %s] >", this.getClass().getSimpleName(), IRegistry.FEATURE.getKey(((WorldGenFeatureConfigured) this.feature.get()).b()), this.decorator);
    }

    @Override
    public Stream<WorldGenFeatureConfigured<?, ?>> ab_() {
        return ((WorldGenFeatureConfigured) this.feature.get()).d();
    }
}
