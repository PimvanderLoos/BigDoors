package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.block.state.IBlockData;

public class WorldGenFeatureCircleConfiguration implements WorldGenFeatureConfiguration {

    public static final Codec<WorldGenFeatureCircleConfiguration> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(IBlockData.CODEC.fieldOf("state").forGetter((worldgenfeaturecircleconfiguration) -> {
            return worldgenfeaturecircleconfiguration.state;
        }), IntProvider.b(0, 8).fieldOf("radius").forGetter((worldgenfeaturecircleconfiguration) -> {
            return worldgenfeaturecircleconfiguration.radius;
        }), Codec.intRange(0, 4).fieldOf("half_height").forGetter((worldgenfeaturecircleconfiguration) -> {
            return worldgenfeaturecircleconfiguration.halfHeight;
        }), IBlockData.CODEC.listOf().fieldOf("targets").forGetter((worldgenfeaturecircleconfiguration) -> {
            return worldgenfeaturecircleconfiguration.targets;
        })).apply(instance, WorldGenFeatureCircleConfiguration::new);
    });
    public final IBlockData state;
    public final IntProvider radius;
    public final int halfHeight;
    public final List<IBlockData> targets;

    public WorldGenFeatureCircleConfiguration(IBlockData iblockdata, IntProvider intprovider, int i, List<IBlockData> list) {
        this.state = iblockdata;
        this.radius = intprovider;
        this.halfHeight = i;
        this.targets = list;
    }
}
