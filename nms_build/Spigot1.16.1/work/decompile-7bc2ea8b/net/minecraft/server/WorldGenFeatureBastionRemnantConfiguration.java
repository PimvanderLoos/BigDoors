package net.minecraft.server;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

public class WorldGenFeatureBastionRemnantConfiguration implements WorldGenFeatureConfiguration {

    public static final Codec<WorldGenFeatureBastionRemnantConfiguration> a = WorldGenFeatureVillageConfiguration.a.listOf().xmap(WorldGenFeatureBastionRemnantConfiguration::new, (worldgenfeaturebastionremnantconfiguration) -> {
        return worldgenfeaturebastionremnantconfiguration.b;
    });
    private final List<WorldGenFeatureVillageConfiguration> b;

    public WorldGenFeatureBastionRemnantConfiguration(Map<String, Integer> map) {
        this((List) map.entrySet().stream().map((entry) -> {
            return new WorldGenFeatureVillageConfiguration(new MinecraftKey((String) entry.getKey()), (Integer) entry.getValue());
        }).collect(Collectors.toList()));
    }

    private WorldGenFeatureBastionRemnantConfiguration(List<WorldGenFeatureVillageConfiguration> list) {
        this.b = list;
    }

    public WorldGenFeatureVillageConfiguration a(Random random) {
        return (WorldGenFeatureVillageConfiguration) this.b.get(random.nextInt(this.b.size()));
    }
}
