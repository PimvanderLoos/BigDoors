package net.minecraft.data.worldgen.features;

import com.google.common.collect.ImmutableList;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.Holder;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.level.levelgen.feature.WorldGenFeatureConfigured;
import net.minecraft.world.level.levelgen.feature.WorldGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenEndGatewayConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureEmptyConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureEndSpikeConfiguration;

public class EndFeatures {

    public static final Holder<WorldGenFeatureConfigured<WorldGenFeatureEndSpikeConfiguration, ?>> END_SPIKE = FeatureUtils.register("end_spike", WorldGenerator.END_SPIKE, new WorldGenFeatureEndSpikeConfiguration(false, ImmutableList.of(), (BlockPosition) null));
    public static final Holder<WorldGenFeatureConfigured<WorldGenEndGatewayConfiguration, ?>> END_GATEWAY_RETURN = FeatureUtils.register("end_gateway_return", WorldGenerator.END_GATEWAY, WorldGenEndGatewayConfiguration.knownExit(WorldServer.END_SPAWN_POINT, true));
    public static final Holder<WorldGenFeatureConfigured<WorldGenEndGatewayConfiguration, ?>> END_GATEWAY_DELAYED = FeatureUtils.register("end_gateway_delayed", WorldGenerator.END_GATEWAY, WorldGenEndGatewayConfiguration.delayedExitSearch());
    public static final Holder<WorldGenFeatureConfigured<WorldGenFeatureEmptyConfiguration, ?>> CHORUS_PLANT = FeatureUtils.register("chorus_plant", WorldGenerator.CHORUS_PLANT);
    public static final Holder<WorldGenFeatureConfigured<WorldGenFeatureEmptyConfiguration, ?>> END_ISLAND = FeatureUtils.register("end_island", WorldGenerator.END_ISLAND);

    public EndFeatures() {}
}
