package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.core.IRegistry;
import net.minecraft.world.level.levelgen.feature.WorldGenFeatureConfigured;
import net.minecraft.world.level.levelgen.placement.WorldGenDecoratorConfigured;

public class WorldGenFeatureCompositeConfiguration implements WorldGenFeatureConfiguration {

    public static final Codec<WorldGenFeatureCompositeConfiguration> a = RecordCodecBuilder.create((instance) -> {
        return instance.group(WorldGenFeatureConfigured.b.fieldOf("feature").forGetter((worldgenfeaturecompositeconfiguration) -> {
            return worldgenfeaturecompositeconfiguration.b;
        }), WorldGenDecoratorConfigured.a.fieldOf("decorator").forGetter((worldgenfeaturecompositeconfiguration) -> {
            return worldgenfeaturecompositeconfiguration.c;
        })).apply(instance, WorldGenFeatureCompositeConfiguration::new);
    });
    public final Supplier<WorldGenFeatureConfigured<?, ?>> b;
    public final WorldGenDecoratorConfigured<?> c;

    public WorldGenFeatureCompositeConfiguration(Supplier<WorldGenFeatureConfigured<?, ?>> supplier, WorldGenDecoratorConfigured<?> worldgendecoratorconfigured) {
        this.b = supplier;
        this.c = worldgendecoratorconfigured;
    }

    public String toString() {
        return String.format("< %s [%s | %s] >", this.getClass().getSimpleName(), IRegistry.FEATURE.getKey(((WorldGenFeatureConfigured) this.b.get()).b()), this.c);
    }

    @Override
    public Stream<WorldGenFeatureConfigured<?, ?>> an_() {
        return ((WorldGenFeatureConfigured) this.b.get()).d();
    }
}
