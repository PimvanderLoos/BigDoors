package net.minecraft.server;

import com.mojang.serialization.Codec;

public class WorldGenBuriedTreasureConfiguration implements WorldGenFeatureConfiguration {

    public static final Codec<WorldGenBuriedTreasureConfiguration> a = Codec.FLOAT.xmap(WorldGenBuriedTreasureConfiguration::new, (worldgenburiedtreasureconfiguration) -> {
        return worldgenburiedtreasureconfiguration.b;
    });
    public final float b;

    public WorldGenBuriedTreasureConfiguration(float f) {
        this.b = f;
    }
}
