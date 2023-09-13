package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.IntSpread;
import net.minecraft.world.level.block.state.IBlockData;

public class WorldGenFeatureRadiusConfiguration implements WorldGenFeatureConfiguration {

    public static final Codec<WorldGenFeatureRadiusConfiguration> a = RecordCodecBuilder.create((instance) -> {
        return instance.group(IBlockData.b.fieldOf("target").forGetter((worldgenfeatureradiusconfiguration) -> {
            return worldgenfeatureradiusconfiguration.b;
        }), IBlockData.b.fieldOf("state").forGetter((worldgenfeatureradiusconfiguration) -> {
            return worldgenfeatureradiusconfiguration.c;
        }), IntSpread.a.fieldOf("radius").forGetter((worldgenfeatureradiusconfiguration) -> {
            return worldgenfeatureradiusconfiguration.d;
        })).apply(instance, WorldGenFeatureRadiusConfiguration::new);
    });
    public final IBlockData b;
    public final IBlockData c;
    private final IntSpread d;

    public WorldGenFeatureRadiusConfiguration(IBlockData iblockdata, IBlockData iblockdata1, IntSpread intspread) {
        this.b = iblockdata;
        this.c = iblockdata1;
        this.d = intspread;
    }

    public IntSpread b() {
        return this.d;
    }
}
