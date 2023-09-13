package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.block.state.IBlockData;

public class WorldGenFeatureDeltaConfiguration implements WorldGenFeatureConfiguration {

    public static final Codec<WorldGenFeatureDeltaConfiguration> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(IBlockData.CODEC.fieldOf("contents").forGetter((worldgenfeaturedeltaconfiguration) -> {
            return worldgenfeaturedeltaconfiguration.contents;
        }), IBlockData.CODEC.fieldOf("rim").forGetter((worldgenfeaturedeltaconfiguration) -> {
            return worldgenfeaturedeltaconfiguration.rim;
        }), IntProvider.codec(0, 16).fieldOf("size").forGetter((worldgenfeaturedeltaconfiguration) -> {
            return worldgenfeaturedeltaconfiguration.size;
        }), IntProvider.codec(0, 16).fieldOf("rim_size").forGetter((worldgenfeaturedeltaconfiguration) -> {
            return worldgenfeaturedeltaconfiguration.rimSize;
        })).apply(instance, WorldGenFeatureDeltaConfiguration::new);
    });
    private final IBlockData contents;
    private final IBlockData rim;
    private final IntProvider size;
    private final IntProvider rimSize;

    public WorldGenFeatureDeltaConfiguration(IBlockData iblockdata, IBlockData iblockdata1, IntProvider intprovider, IntProvider intprovider1) {
        this.contents = iblockdata;
        this.rim = iblockdata1;
        this.size = intprovider;
        this.rimSize = intprovider1;
    }

    public IBlockData contents() {
        return this.contents;
    }

    public IBlockData rim() {
        return this.rim;
    }

    public IntProvider size() {
        return this.size;
    }

    public IntProvider rimSize() {
        return this.rimSize;
    }
}
