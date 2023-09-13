package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.block.state.IBlockData;

public class WorldGenFeatureRadiusConfiguration implements WorldGenFeatureConfiguration {

    public static final Codec<WorldGenFeatureRadiusConfiguration> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(IBlockData.CODEC.fieldOf("target").forGetter((worldgenfeatureradiusconfiguration) -> {
            return worldgenfeatureradiusconfiguration.targetState;
        }), IBlockData.CODEC.fieldOf("state").forGetter((worldgenfeatureradiusconfiguration) -> {
            return worldgenfeatureradiusconfiguration.replaceState;
        }), IntProvider.codec(0, 12).fieldOf("radius").forGetter((worldgenfeatureradiusconfiguration) -> {
            return worldgenfeatureradiusconfiguration.radius;
        })).apply(instance, WorldGenFeatureRadiusConfiguration::new);
    });
    public final IBlockData targetState;
    public final IBlockData replaceState;
    private final IntProvider radius;

    public WorldGenFeatureRadiusConfiguration(IBlockData iblockdata, IBlockData iblockdata1, IntProvider intprovider) {
        this.targetState = iblockdata;
        this.replaceState = iblockdata1;
        this.radius = intprovider;
    }

    public IntProvider radius() {
        return this.radius;
    }
}
