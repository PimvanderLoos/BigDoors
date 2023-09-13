package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.EnumDirection;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.WorldGenFeatureStateProvider;

public class GrowingPlantConfiguration implements WorldGenFeatureConfiguration {

    public static final Codec<GrowingPlantConfiguration> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(SimpleWeightedRandomList.a(IntProvider.CODEC).fieldOf("height_distribution").forGetter((growingplantconfiguration) -> {
            return growingplantconfiguration.heightDistribution;
        }), EnumDirection.CODEC.fieldOf("direction").forGetter((growingplantconfiguration) -> {
            return growingplantconfiguration.direction;
        }), WorldGenFeatureStateProvider.CODEC.fieldOf("body_provider").forGetter((growingplantconfiguration) -> {
            return growingplantconfiguration.bodyProvider;
        }), WorldGenFeatureStateProvider.CODEC.fieldOf("head_provider").forGetter((growingplantconfiguration) -> {
            return growingplantconfiguration.headProvider;
        }), Codec.BOOL.fieldOf("allow_water").forGetter((growingplantconfiguration) -> {
            return growingplantconfiguration.allowWater;
        })).apply(instance, GrowingPlantConfiguration::new);
    });
    public final SimpleWeightedRandomList<IntProvider> heightDistribution;
    public final EnumDirection direction;
    public final WorldGenFeatureStateProvider bodyProvider;
    public final WorldGenFeatureStateProvider headProvider;
    public final boolean allowWater;

    public GrowingPlantConfiguration(SimpleWeightedRandomList<IntProvider> simpleweightedrandomlist, EnumDirection enumdirection, WorldGenFeatureStateProvider worldgenfeaturestateprovider, WorldGenFeatureStateProvider worldgenfeaturestateprovider1, boolean flag) {
        this.heightDistribution = simpleweightedrandomlist;
        this.direction = enumdirection;
        this.bodyProvider = worldgenfeaturestateprovider;
        this.headProvider = worldgenfeaturestateprovider1;
        this.allowWater = flag;
    }
}
