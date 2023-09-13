package net.minecraft.server;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class WorldGenFeatureVillageConfiguration implements WorldGenFeatureConfiguration {

    public static final Codec<WorldGenFeatureVillageConfiguration> a = RecordCodecBuilder.create((instance) -> {
        return instance.group(MinecraftKey.a.fieldOf("start_pool").forGetter(WorldGenFeatureVillageConfiguration::b), Codec.INT.fieldOf("size").forGetter(WorldGenFeatureVillageConfiguration::a)).apply(instance, WorldGenFeatureVillageConfiguration::new);
    });
    public final MinecraftKey b;
    public final int c;

    public WorldGenFeatureVillageConfiguration(MinecraftKey minecraftkey, int i) {
        this.b = minecraftkey;
        this.c = i;
    }

    public int a() {
        return this.c;
    }

    public MinecraftKey b() {
        return this.b;
    }
}
