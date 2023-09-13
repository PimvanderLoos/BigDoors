package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureConfiguration;

public class WorldGenFeatureHugeFungiConfiguration implements WorldGenFeatureConfiguration {

    public static final Codec<WorldGenFeatureHugeFungiConfiguration> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(IBlockData.CODEC.fieldOf("valid_base_block").forGetter((worldgenfeaturehugefungiconfiguration) -> {
            return worldgenfeaturehugefungiconfiguration.validBaseState;
        }), IBlockData.CODEC.fieldOf("stem_state").forGetter((worldgenfeaturehugefungiconfiguration) -> {
            return worldgenfeaturehugefungiconfiguration.stemState;
        }), IBlockData.CODEC.fieldOf("hat_state").forGetter((worldgenfeaturehugefungiconfiguration) -> {
            return worldgenfeaturehugefungiconfiguration.hatState;
        }), IBlockData.CODEC.fieldOf("decor_state").forGetter((worldgenfeaturehugefungiconfiguration) -> {
            return worldgenfeaturehugefungiconfiguration.decorState;
        }), Codec.BOOL.fieldOf("planted").orElse(false).forGetter((worldgenfeaturehugefungiconfiguration) -> {
            return worldgenfeaturehugefungiconfiguration.planted;
        })).apply(instance, WorldGenFeatureHugeFungiConfiguration::new);
    });
    public final IBlockData validBaseState;
    public final IBlockData stemState;
    public final IBlockData hatState;
    public final IBlockData decorState;
    public final boolean planted;

    public WorldGenFeatureHugeFungiConfiguration(IBlockData iblockdata, IBlockData iblockdata1, IBlockData iblockdata2, IBlockData iblockdata3, boolean flag) {
        this.validBaseState = iblockdata;
        this.stemState = iblockdata1;
        this.hatState = iblockdata2;
        this.decorState = iblockdata3;
        this.planted = flag;
    }
}
