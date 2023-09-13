package net.minecraft.data.worldgen.features;

import com.google.common.collect.ImmutableList;
import net.minecraft.core.BlockPosition;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.level.levelgen.feature.WorldGenFeatureConfigured;
import net.minecraft.world.level.levelgen.feature.WorldGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenEndGatewayConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureEndSpikeConfiguration;

public class EndFeatures {

    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> END_SPIKE = FeatureUtils.createKey("end_spike");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> END_GATEWAY_RETURN = FeatureUtils.createKey("end_gateway_return");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> END_GATEWAY_DELAYED = FeatureUtils.createKey("end_gateway_delayed");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> CHORUS_PLANT = FeatureUtils.createKey("chorus_plant");
    public static final ResourceKey<WorldGenFeatureConfigured<?, ?>> END_ISLAND = FeatureUtils.createKey("end_island");

    public EndFeatures() {}

    public static void bootstrap(BootstapContext<WorldGenFeatureConfigured<?, ?>> bootstapcontext) {
        FeatureUtils.register(bootstapcontext, EndFeatures.END_SPIKE, WorldGenerator.END_SPIKE, new WorldGenFeatureEndSpikeConfiguration(false, ImmutableList.of(), (BlockPosition) null));
        FeatureUtils.register(bootstapcontext, EndFeatures.END_GATEWAY_RETURN, WorldGenerator.END_GATEWAY, WorldGenEndGatewayConfiguration.knownExit(WorldServer.END_SPAWN_POINT, true));
        FeatureUtils.register(bootstapcontext, EndFeatures.END_GATEWAY_DELAYED, WorldGenerator.END_GATEWAY, WorldGenEndGatewayConfiguration.delayedExitSearch());
        FeatureUtils.register(bootstapcontext, EndFeatures.CHORUS_PLANT, WorldGenerator.CHORUS_PLANT);
        FeatureUtils.register(bootstapcontext, EndFeatures.END_ISLAND, WorldGenerator.END_ISLAND);
    }
}
