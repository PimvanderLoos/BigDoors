package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;

public class WorldGenFeatureShipwreckConfiguration implements WorldGenFeatureConfiguration {

    public static final Codec<WorldGenFeatureShipwreckConfiguration> a = Codec.BOOL.fieldOf("is_beached").orElse(false).xmap(WorldGenFeatureShipwreckConfiguration::new, (worldgenfeatureshipwreckconfiguration) -> {
        return worldgenfeatureshipwreckconfiguration.b;
    }).codec();
    public final boolean b;

    public WorldGenFeatureShipwreckConfiguration(boolean flag) {
        this.b = flag;
    }
}
