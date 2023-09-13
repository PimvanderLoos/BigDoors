package net.minecraft.server;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class WorldGenFeatureBasaltColumnsConfiguration implements WorldGenFeatureConfiguration {

    public static final Codec<WorldGenFeatureBasaltColumnsConfiguration> a = RecordCodecBuilder.create((instance) -> {
        return instance.group(Codec.INT.fieldOf("minimum_reach").forGetter((worldgenfeaturebasaltcolumnsconfiguration) -> {
            return worldgenfeaturebasaltcolumnsconfiguration.b;
        }), Codec.INT.fieldOf("maximum_reach").forGetter((worldgenfeaturebasaltcolumnsconfiguration) -> {
            return worldgenfeaturebasaltcolumnsconfiguration.c;
        }), Codec.INT.fieldOf("minimum_height").forGetter((worldgenfeaturebasaltcolumnsconfiguration) -> {
            return worldgenfeaturebasaltcolumnsconfiguration.d;
        }), Codec.INT.fieldOf("maximum_height").forGetter((worldgenfeaturebasaltcolumnsconfiguration) -> {
            return worldgenfeaturebasaltcolumnsconfiguration.e;
        })).apply(instance, WorldGenFeatureBasaltColumnsConfiguration::new);
    });
    public final int b;
    public final int c;
    public final int d;
    public final int e;

    public WorldGenFeatureBasaltColumnsConfiguration(int i, int j, int k, int l) {
        this.b = i;
        this.c = j;
        this.d = k;
        this.e = l;
    }

    public static class a {

        private int a;
        private int b;
        private int c;
        private int d;

        public a() {}

        public WorldGenFeatureBasaltColumnsConfiguration.a a(int i) {
            this.a = i;
            this.b = i;
            return this;
        }

        public WorldGenFeatureBasaltColumnsConfiguration.a a(int i, int j) {
            this.a = i;
            this.b = j;
            return this;
        }

        public WorldGenFeatureBasaltColumnsConfiguration.a b(int i, int j) {
            this.c = i;
            this.d = j;
            return this;
        }

        public WorldGenFeatureBasaltColumnsConfiguration a() {
            if (this.c < 1) {
                throw new IllegalArgumentException("Minimum height cannot be less than 1");
            } else if (this.a < 0) {
                throw new IllegalArgumentException("Minimum reach cannot be negative");
            } else if (this.a <= this.b && this.c <= this.d) {
                return new WorldGenFeatureBasaltColumnsConfiguration(this.a, this.b, this.c, this.d);
            } else {
                throw new IllegalArgumentException("Minimum reach/height cannot be greater than maximum width/height");
            }
        }
    }
}
