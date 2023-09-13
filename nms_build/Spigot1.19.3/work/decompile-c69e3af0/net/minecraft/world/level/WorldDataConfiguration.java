package net.minecraft.world.level;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;

public record WorldDataConfiguration(DataPackConfiguration dataPacks, FeatureFlagSet enabledFeatures) {

    public static final String ENABLED_FEATURES_ID = "enabled_features";
    public static final Codec<WorldDataConfiguration> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(DataPackConfiguration.CODEC.optionalFieldOf("DataPacks", DataPackConfiguration.DEFAULT).forGetter(WorldDataConfiguration::dataPacks), FeatureFlags.CODEC.optionalFieldOf("enabled_features", FeatureFlags.DEFAULT_FLAGS).forGetter(WorldDataConfiguration::enabledFeatures)).apply(instance, WorldDataConfiguration::new);
    });
    public static final WorldDataConfiguration DEFAULT = new WorldDataConfiguration(DataPackConfiguration.DEFAULT, FeatureFlags.DEFAULT_FLAGS);

    public WorldDataConfiguration expandFeatures(FeatureFlagSet featureflagset) {
        return new WorldDataConfiguration(this.dataPacks, this.enabledFeatures.join(featureflagset));
    }
}
