package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureConfiguration;

public class WorldGenFeatureHugeFungiConfiguration implements WorldGenFeatureConfiguration {

    public static final Codec<WorldGenFeatureHugeFungiConfiguration> a = RecordCodecBuilder.create((instance) -> {
        return instance.group(IBlockData.b.fieldOf("valid_base_block").forGetter((worldgenfeaturehugefungiconfiguration) -> {
            return worldgenfeaturehugefungiconfiguration.f;
        }), IBlockData.b.fieldOf("stem_state").forGetter((worldgenfeaturehugefungiconfiguration) -> {
            return worldgenfeaturehugefungiconfiguration.g;
        }), IBlockData.b.fieldOf("hat_state").forGetter((worldgenfeaturehugefungiconfiguration) -> {
            return worldgenfeaturehugefungiconfiguration.h;
        }), IBlockData.b.fieldOf("decor_state").forGetter((worldgenfeaturehugefungiconfiguration) -> {
            return worldgenfeaturehugefungiconfiguration.i;
        }), Codec.BOOL.fieldOf("planted").orElse(false).forGetter((worldgenfeaturehugefungiconfiguration) -> {
            return worldgenfeaturehugefungiconfiguration.j;
        })).apply(instance, WorldGenFeatureHugeFungiConfiguration::new);
    });
    public static final WorldGenFeatureHugeFungiConfiguration CRIMSON_FUNGUS = new WorldGenFeatureHugeFungiConfiguration(Blocks.CRIMSON_NYLIUM.getBlockData(), Blocks.CRIMSON_STEM.getBlockData(), Blocks.NETHER_WART_BLOCK.getBlockData(), Blocks.SHROOMLIGHT.getBlockData(), true);
    public static final WorldGenFeatureHugeFungiConfiguration c = new WorldGenFeatureHugeFungiConfiguration(WorldGenFeatureHugeFungiConfiguration.CRIMSON_FUNGUS.f, WorldGenFeatureHugeFungiConfiguration.CRIMSON_FUNGUS.g, WorldGenFeatureHugeFungiConfiguration.CRIMSON_FUNGUS.h, WorldGenFeatureHugeFungiConfiguration.CRIMSON_FUNGUS.i, false);
    public static final WorldGenFeatureHugeFungiConfiguration WARPED_FUNGUS = new WorldGenFeatureHugeFungiConfiguration(Blocks.WARPED_NYLIUM.getBlockData(), Blocks.WARPED_STEM.getBlockData(), Blocks.WARPED_WART_BLOCK.getBlockData(), Blocks.SHROOMLIGHT.getBlockData(), true);
    public static final WorldGenFeatureHugeFungiConfiguration e = new WorldGenFeatureHugeFungiConfiguration(WorldGenFeatureHugeFungiConfiguration.WARPED_FUNGUS.f, WorldGenFeatureHugeFungiConfiguration.WARPED_FUNGUS.g, WorldGenFeatureHugeFungiConfiguration.WARPED_FUNGUS.h, WorldGenFeatureHugeFungiConfiguration.WARPED_FUNGUS.i, false);
    public final IBlockData f;
    public final IBlockData g;
    public final IBlockData h;
    public final IBlockData i;
    public final boolean j;

    public WorldGenFeatureHugeFungiConfiguration(IBlockData iblockdata, IBlockData iblockdata1, IBlockData iblockdata2, IBlockData iblockdata3, boolean flag) {
        this.f = iblockdata;
        this.g = iblockdata1;
        this.h = iblockdata2;
        this.i = iblockdata3;
        this.j = flag;
    }
}
