package net.minecraft.data.worldgen.features;

import com.google.common.collect.ImmutableList;
import net.minecraft.core.BlockPosition;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.level.levelgen.feature.WorldGenFeatureConfigured;
import net.minecraft.world.level.levelgen.feature.WorldGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenEndGatewayConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureEndSpikeConfiguration;

public class EndFeatures {

    public static final WorldGenFeatureConfigured<?, ?> END_SPIKE = FeatureUtils.register("end_spike", WorldGenerator.END_SPIKE.configured(new WorldGenFeatureEndSpikeConfiguration(false, ImmutableList.of(), (BlockPosition) null)));
    public static final WorldGenFeatureConfigured<?, ?> END_GATEWAY_RETURN = FeatureUtils.register("end_gateway_return", WorldGenerator.END_GATEWAY.configured(WorldGenEndGatewayConfiguration.knownExit(WorldServer.END_SPAWN_POINT, true)));
    public static final WorldGenFeatureConfigured<?, ?> END_GATEWAY_DELAYED = FeatureUtils.register("end_gateway_delayed", WorldGenerator.END_GATEWAY.configured(WorldGenEndGatewayConfiguration.delayedExitSearch()));
    public static final WorldGenFeatureConfigured<?, ?> CHORUS_PLANT = FeatureUtils.register("chorus_plant", WorldGenerator.CHORUS_PLANT.configured(WorldGenFeatureConfiguration.NONE));
    public static final WorldGenFeatureConfigured<?, ?> END_ISLAND = FeatureUtils.register("end_island", WorldGenerator.END_ISLAND.configured(WorldGenFeatureConfiguration.NONE));

    public EndFeatures() {}
}
