package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import net.minecraft.world.level.levelgen.feature.stateproviders.WorldGenFeatureStateProvider;

public class WorldGenFeatureBlockPileConfiguration implements WorldGenFeatureConfiguration {

    public static final Codec<WorldGenFeatureBlockPileConfiguration> CODEC = WorldGenFeatureStateProvider.CODEC.fieldOf("state_provider").xmap(WorldGenFeatureBlockPileConfiguration::new, (worldgenfeatureblockpileconfiguration) -> {
        return worldgenfeatureblockpileconfiguration.stateProvider;
    }).codec();
    public final WorldGenFeatureStateProvider stateProvider;

    public WorldGenFeatureBlockPileConfiguration(WorldGenFeatureStateProvider worldgenfeaturestateprovider) {
        this.stateProvider = worldgenfeaturestateprovider;
    }
}
