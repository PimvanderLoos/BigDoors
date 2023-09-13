package net.minecraft.server;

import com.mojang.serialization.Codec;
import java.util.List;

public class WorldGenFeatureRandom2 implements WorldGenFeatureConfiguration {

    public static final Codec<WorldGenFeatureRandom2> a = WorldGenFeatureConfigured.b.listOf().fieldOf("features").xmap(WorldGenFeatureRandom2::new, (worldgenfeaturerandom2) -> {
        return worldgenfeaturerandom2.b;
    }).codec();
    public final List<WorldGenFeatureConfigured<?, ?>> b;

    public WorldGenFeatureRandom2(List<WorldGenFeatureConfigured<?, ?>> list) {
        this.b = list;
    }
}
