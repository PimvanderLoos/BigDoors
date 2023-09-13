package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.block.Blocks;
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
    public static final WorldGenFeatureHugeFungiConfiguration HUGE_CRIMSON_FUNGI_PLANTED_CONFIG = new WorldGenFeatureHugeFungiConfiguration(Blocks.CRIMSON_NYLIUM.getBlockData(), Blocks.CRIMSON_STEM.getBlockData(), Blocks.NETHER_WART_BLOCK.getBlockData(), Blocks.SHROOMLIGHT.getBlockData(), true);
    public static final WorldGenFeatureHugeFungiConfiguration HUGE_CRIMSON_FUNGI_NOT_PLANTED_CONFIG = new WorldGenFeatureHugeFungiConfiguration(WorldGenFeatureHugeFungiConfiguration.HUGE_CRIMSON_FUNGI_PLANTED_CONFIG.validBaseState, WorldGenFeatureHugeFungiConfiguration.HUGE_CRIMSON_FUNGI_PLANTED_CONFIG.stemState, WorldGenFeatureHugeFungiConfiguration.HUGE_CRIMSON_FUNGI_PLANTED_CONFIG.hatState, WorldGenFeatureHugeFungiConfiguration.HUGE_CRIMSON_FUNGI_PLANTED_CONFIG.decorState, false);
    public static final WorldGenFeatureHugeFungiConfiguration HUGE_WARPED_FUNGI_PLANTED_CONFIG = new WorldGenFeatureHugeFungiConfiguration(Blocks.WARPED_NYLIUM.getBlockData(), Blocks.WARPED_STEM.getBlockData(), Blocks.WARPED_WART_BLOCK.getBlockData(), Blocks.SHROOMLIGHT.getBlockData(), true);
    public static final WorldGenFeatureHugeFungiConfiguration HUGE_WARPED_FUNGI_NOT_PLANTED_CONFIG = new WorldGenFeatureHugeFungiConfiguration(WorldGenFeatureHugeFungiConfiguration.HUGE_WARPED_FUNGI_PLANTED_CONFIG.validBaseState, WorldGenFeatureHugeFungiConfiguration.HUGE_WARPED_FUNGI_PLANTED_CONFIG.stemState, WorldGenFeatureHugeFungiConfiguration.HUGE_WARPED_FUNGI_PLANTED_CONFIG.hatState, WorldGenFeatureHugeFungiConfiguration.HUGE_WARPED_FUNGI_PLANTED_CONFIG.decorState, false);
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
