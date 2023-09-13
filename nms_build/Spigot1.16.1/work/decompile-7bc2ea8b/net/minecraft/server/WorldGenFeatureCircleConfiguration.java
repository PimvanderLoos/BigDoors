package net.minecraft.server;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;

public class WorldGenFeatureCircleConfiguration implements WorldGenFeatureConfiguration {

    public static final Codec<WorldGenFeatureCircleConfiguration> a = RecordCodecBuilder.create((instance) -> {
        return instance.group(IBlockData.b.fieldOf("state").forGetter((worldgenfeaturecircleconfiguration) -> {
            return worldgenfeaturecircleconfiguration.b;
        }), Codec.INT.fieldOf("radius").withDefault(0).forGetter((worldgenfeaturecircleconfiguration) -> {
            return worldgenfeaturecircleconfiguration.c;
        }), Codec.INT.fieldOf("y_size").withDefault(0).forGetter((worldgenfeaturecircleconfiguration) -> {
            return worldgenfeaturecircleconfiguration.d;
        }), IBlockData.b.listOf().fieldOf("targets").forGetter((worldgenfeaturecircleconfiguration) -> {
            return worldgenfeaturecircleconfiguration.e;
        })).apply(instance, WorldGenFeatureCircleConfiguration::new);
    });
    public final IBlockData b;
    public final int c;
    public final int d;
    public final List<IBlockData> e;

    public WorldGenFeatureCircleConfiguration(IBlockData iblockdata, int i, int j, List<IBlockData> list) {
        this.b = iblockdata;
        this.c = i;
        this.d = j;
        this.e = list;
    }
}
