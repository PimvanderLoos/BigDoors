package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;

public class WorldGenFeatureShipwreckConfiguration implements WorldGenFeatureConfiguration {

    public static final Codec<WorldGenFeatureShipwreckConfiguration> CODEC = Codec.BOOL.fieldOf("is_beached").orElse(false).xmap(WorldGenFeatureShipwreckConfiguration::new, (worldgenfeatureshipwreckconfiguration) -> {
        return worldgenfeatureshipwreckconfiguration.isBeached;
    }).codec();
    public final boolean isBeached;

    public WorldGenFeatureShipwreckConfiguration(boolean flag) {
        this.isBeached = flag;
    }
}
