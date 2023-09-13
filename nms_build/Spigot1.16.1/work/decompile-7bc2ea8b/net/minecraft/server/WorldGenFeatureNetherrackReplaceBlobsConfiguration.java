package net.minecraft.server;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class WorldGenFeatureNetherrackReplaceBlobsConfiguration implements WorldGenFeatureConfiguration {

    public static final Codec<WorldGenFeatureNetherrackReplaceBlobsConfiguration> a = RecordCodecBuilder.create((instance) -> {
        return instance.group(IBlockData.b.fieldOf("target").forGetter((worldgenfeaturenetherrackreplaceblobsconfiguration) -> {
            return worldgenfeaturenetherrackreplaceblobsconfiguration.b;
        }), IBlockData.b.fieldOf("state").forGetter((worldgenfeaturenetherrackreplaceblobsconfiguration) -> {
            return worldgenfeaturenetherrackreplaceblobsconfiguration.c;
        }), BaseBlockPosition.c.fieldOf("minimum_reach").forGetter((worldgenfeaturenetherrackreplaceblobsconfiguration) -> {
            return worldgenfeaturenetherrackreplaceblobsconfiguration.d;
        }), BaseBlockPosition.c.fieldOf("maximum_reach").forGetter((worldgenfeaturenetherrackreplaceblobsconfiguration) -> {
            return worldgenfeaturenetherrackreplaceblobsconfiguration.e;
        })).apply(instance, WorldGenFeatureNetherrackReplaceBlobsConfiguration::new);
    });
    public final IBlockData b;
    public final IBlockData c;
    public final BaseBlockPosition d;
    public final BaseBlockPosition e;

    public WorldGenFeatureNetherrackReplaceBlobsConfiguration(IBlockData iblockdata, IBlockData iblockdata1, BaseBlockPosition baseblockposition, BaseBlockPosition baseblockposition1) {
        this.b = iblockdata;
        this.c = iblockdata1;
        this.d = baseblockposition;
        this.e = baseblockposition1;
    }

    public static class a {

        private IBlockData a;
        private IBlockData b;
        private BaseBlockPosition c;
        private BaseBlockPosition d;

        public a() {
            this.a = Blocks.AIR.getBlockData();
            this.b = Blocks.AIR.getBlockData();
            this.c = BaseBlockPosition.ZERO;
            this.d = BaseBlockPosition.ZERO;
        }

        public WorldGenFeatureNetherrackReplaceBlobsConfiguration.a a(IBlockData iblockdata) {
            this.a = iblockdata;
            return this;
        }

        public WorldGenFeatureNetherrackReplaceBlobsConfiguration.a b(IBlockData iblockdata) {
            this.b = iblockdata;
            return this;
        }

        public WorldGenFeatureNetherrackReplaceBlobsConfiguration.a a(BaseBlockPosition baseblockposition) {
            this.c = baseblockposition;
            return this;
        }

        public WorldGenFeatureNetherrackReplaceBlobsConfiguration.a b(BaseBlockPosition baseblockposition) {
            this.d = baseblockposition;
            return this;
        }

        public WorldGenFeatureNetherrackReplaceBlobsConfiguration a() {
            if (this.c.getX() >= 0 && this.c.getY() >= 0 && this.c.getZ() >= 0) {
                if (this.c.getX() <= this.d.getX() && this.c.getY() <= this.d.getY() && this.c.getZ() <= this.d.getZ()) {
                    return new WorldGenFeatureNetherrackReplaceBlobsConfiguration(this.a, this.b, this.c, this.d);
                } else {
                    throw new IllegalArgumentException("Maximum reach must be greater than minimum reach for each axis");
                }
            } else {
                throw new IllegalArgumentException("Minimum reach cannot be less than zero");
            }
        }
    }
}
