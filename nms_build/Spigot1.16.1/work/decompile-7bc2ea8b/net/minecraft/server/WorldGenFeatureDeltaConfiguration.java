package net.minecraft.server;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;

public class WorldGenFeatureDeltaConfiguration implements WorldGenFeatureConfiguration {

    public static final Codec<WorldGenFeatureDeltaConfiguration> a = RecordCodecBuilder.create((instance) -> {
        return instance.group(IBlockData.b.fieldOf("contents").forGetter((worldgenfeaturedeltaconfiguration) -> {
            return worldgenfeaturedeltaconfiguration.b;
        }), IBlockData.b.fieldOf("rim").forGetter((worldgenfeaturedeltaconfiguration) -> {
            return worldgenfeaturedeltaconfiguration.c;
        }), Codec.INT.fieldOf("minimum_radius").forGetter((worldgenfeaturedeltaconfiguration) -> {
            return worldgenfeaturedeltaconfiguration.d;
        }), Codec.INT.fieldOf("maximum_radius").forGetter((worldgenfeaturedeltaconfiguration) -> {
            return worldgenfeaturedeltaconfiguration.e;
        }), Codec.INT.fieldOf("maximum_rim").forGetter((worldgenfeaturedeltaconfiguration) -> {
            return worldgenfeaturedeltaconfiguration.f;
        })).apply(instance, WorldGenFeatureDeltaConfiguration::new);
    });
    public final IBlockData b;
    public final IBlockData c;
    public final int d;
    public final int e;
    public final int f;

    public WorldGenFeatureDeltaConfiguration(IBlockData iblockdata, IBlockData iblockdata1, int i, int j, int k) {
        this.b = iblockdata;
        this.c = iblockdata1;
        this.d = i;
        this.e = j;
        this.f = k;
    }

    public static class a {

        Optional<IBlockData> a = Optional.empty();
        Optional<IBlockData> b = Optional.empty();
        int c;
        int d;
        int e;

        public a() {}

        public WorldGenFeatureDeltaConfiguration.a a(int i, int j) {
            this.c = i;
            this.d = j;
            return this;
        }

        public WorldGenFeatureDeltaConfiguration.a a(IBlockData iblockdata) {
            this.a = Optional.of(iblockdata);
            return this;
        }

        public WorldGenFeatureDeltaConfiguration.a a(IBlockData iblockdata, int i) {
            this.b = Optional.of(iblockdata);
            this.e = i;
            return this;
        }

        public WorldGenFeatureDeltaConfiguration a() {
            if (!this.a.isPresent()) {
                throw new IllegalArgumentException("Missing contents");
            } else if (!this.b.isPresent()) {
                throw new IllegalArgumentException("Missing rim");
            } else if (this.c > this.d) {
                throw new IllegalArgumentException("Minimum radius cannot be greater than maximum radius");
            } else {
                return new WorldGenFeatureDeltaConfiguration((IBlockData) this.a.get(), (IBlockData) this.b.get(), this.c, this.d, this.e);
            }
        }
    }
}
