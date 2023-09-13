package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import net.minecraft.world.level.levelgen.feature.WorldGenFeatureRuinedPortal;

public class WorldGenFeatureRuinedPortalConfiguration implements WorldGenFeatureConfiguration {

    public static final Codec<WorldGenFeatureRuinedPortalConfiguration> a = WorldGenFeatureRuinedPortal.Type.h.fieldOf("portal_type").xmap(WorldGenFeatureRuinedPortalConfiguration::new, (worldgenfeatureruinedportalconfiguration) -> {
        return worldgenfeatureruinedportalconfiguration.b;
    }).codec();
    public final WorldGenFeatureRuinedPortal.Type b;

    public WorldGenFeatureRuinedPortalConfiguration(WorldGenFeatureRuinedPortal.Type worldgenfeatureruinedportal_type) {
        this.b = worldgenfeatureruinedportal_type;
    }
}
