package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.levelgen.feature.stateproviders.WorldGenFeatureStateProvider;

public class NetherForestVegetationConfig extends WorldGenFeatureBlockPileConfiguration {

    public static final Codec<NetherForestVegetationConfig> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(WorldGenFeatureStateProvider.CODEC.fieldOf("state_provider").forGetter((netherforestvegetationconfig) -> {
            return netherforestvegetationconfig.stateProvider;
        }), ExtraCodecs.POSITIVE_INT.fieldOf("spread_width").forGetter((netherforestvegetationconfig) -> {
            return netherforestvegetationconfig.spreadWidth;
        }), ExtraCodecs.POSITIVE_INT.fieldOf("spread_height").forGetter((netherforestvegetationconfig) -> {
            return netherforestvegetationconfig.spreadHeight;
        })).apply(instance, NetherForestVegetationConfig::new);
    });
    public final int spreadWidth;
    public final int spreadHeight;

    public NetherForestVegetationConfig(WorldGenFeatureStateProvider worldgenfeaturestateprovider, int i, int j) {
        super(worldgenfeaturestateprovider);
        this.spreadWidth = i;
        this.spreadHeight = j;
    }
}
