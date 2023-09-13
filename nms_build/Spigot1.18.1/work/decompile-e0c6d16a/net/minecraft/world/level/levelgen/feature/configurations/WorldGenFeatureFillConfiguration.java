package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.dimension.DimensionManager;

public class WorldGenFeatureFillConfiguration implements WorldGenFeatureConfiguration {

    public static final Codec<WorldGenFeatureFillConfiguration> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(Codec.intRange(0, DimensionManager.Y_SIZE).fieldOf("height").forGetter((worldgenfeaturefillconfiguration) -> {
            return worldgenfeaturefillconfiguration.height;
        }), IBlockData.CODEC.fieldOf("state").forGetter((worldgenfeaturefillconfiguration) -> {
            return worldgenfeaturefillconfiguration.state;
        })).apply(instance, WorldGenFeatureFillConfiguration::new);
    });
    public final int height;
    public final IBlockData state;

    public WorldGenFeatureFillConfiguration(int i, IBlockData iblockdata) {
        this.height = i;
        this.state = iblockdata;
    }
}
