package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.util.IntSpread;
import net.minecraft.world.level.block.state.IBlockData;

public class WorldGenFeatureCircleConfiguration implements WorldGenFeatureConfiguration {

    public static final Codec<WorldGenFeatureCircleConfiguration> a = RecordCodecBuilder.create((instance) -> {
        return instance.group(IBlockData.b.fieldOf("state").forGetter((worldgenfeaturecircleconfiguration) -> {
            return worldgenfeaturecircleconfiguration.b;
        }), IntSpread.a(0, 4, 4).fieldOf("radius").forGetter((worldgenfeaturecircleconfiguration) -> {
            return worldgenfeaturecircleconfiguration.c;
        }), Codec.intRange(0, 4).fieldOf("half_height").forGetter((worldgenfeaturecircleconfiguration) -> {
            return worldgenfeaturecircleconfiguration.d;
        }), IBlockData.b.listOf().fieldOf("targets").forGetter((worldgenfeaturecircleconfiguration) -> {
            return worldgenfeaturecircleconfiguration.e;
        })).apply(instance, WorldGenFeatureCircleConfiguration::new);
    });
    public final IBlockData b;
    public final IntSpread c;
    public final int d;
    public final List<IBlockData> e;

    public WorldGenFeatureCircleConfiguration(IBlockData iblockdata, IntSpread intspread, int i, List<IBlockData> list) {
        this.b = iblockdata;
        this.c = intspread;
        this.d = i;
        this.e = list;
    }
}
