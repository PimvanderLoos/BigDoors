package net.minecraft.server;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class WorldGenFeatureDeltaConfiguration implements WorldGenFeatureConfiguration {

    public static final Codec<WorldGenFeatureDeltaConfiguration> a = RecordCodecBuilder.create((instance) -> {
        return instance.group(IBlockData.b.fieldOf("contents").forGetter((worldgenfeaturedeltaconfiguration) -> {
            return worldgenfeaturedeltaconfiguration.b;
        }), IBlockData.b.fieldOf("rim").forGetter((worldgenfeaturedeltaconfiguration) -> {
            return worldgenfeaturedeltaconfiguration.c;
        }), IntSpread.a(0, 8, 8).fieldOf("size").forGetter((worldgenfeaturedeltaconfiguration) -> {
            return worldgenfeaturedeltaconfiguration.d;
        }), IntSpread.a(0, 8, 8).fieldOf("rim_size").forGetter((worldgenfeaturedeltaconfiguration) -> {
            return worldgenfeaturedeltaconfiguration.e;
        })).apply(instance, WorldGenFeatureDeltaConfiguration::new);
    });
    private final IBlockData b;
    private final IBlockData c;
    private final IntSpread d;
    private final IntSpread e;

    public WorldGenFeatureDeltaConfiguration(IBlockData iblockdata, IBlockData iblockdata1, IntSpread intspread, IntSpread intspread1) {
        this.b = iblockdata;
        this.c = iblockdata1;
        this.d = intspread;
        this.e = intspread1;
    }

    public IBlockData b() {
        return this.b;
    }

    public IBlockData c() {
        return this.c;
    }

    public IntSpread d() {
        return this.d;
    }

    public IntSpread e() {
        return this.e;
    }
}
