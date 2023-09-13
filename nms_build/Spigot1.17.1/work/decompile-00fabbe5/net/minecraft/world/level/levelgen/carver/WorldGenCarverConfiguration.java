package net.minecraft.world.level.levelgen.carver;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureConfigurationChance;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;

public class WorldGenCarverConfiguration extends WorldGenFeatureConfigurationChance {

    public static final MapCodec<WorldGenCarverConfiguration> CODEC = RecordCodecBuilder.mapCodec((instance) -> {
        return instance.group(Codec.floatRange(0.0F, 1.0F).fieldOf("probability").forGetter((worldgencarverconfiguration) -> {
            return worldgencarverconfiguration.probability;
        }), HeightProvider.CODEC.fieldOf("y").forGetter((worldgencarverconfiguration) -> {
            return worldgencarverconfiguration.y;
        }), FloatProvider.CODEC.fieldOf("yScale").forGetter((worldgencarverconfiguration) -> {
            return worldgencarverconfiguration.yScale;
        }), VerticalAnchor.CODEC.fieldOf("lava_level").forGetter((worldgencarverconfiguration) -> {
            return worldgencarverconfiguration.lavaLevel;
        }), Codec.BOOL.fieldOf("aquifers_enabled").forGetter((worldgencarverconfiguration) -> {
            return worldgencarverconfiguration.aquifersEnabled;
        }), CarverDebugSettings.CODEC.optionalFieldOf("debug_settings", CarverDebugSettings.DEFAULT).forGetter((worldgencarverconfiguration) -> {
            return worldgencarverconfiguration.debugSettings;
        })).apply(instance, WorldGenCarverConfiguration::new);
    });
    public final HeightProvider y;
    public final FloatProvider yScale;
    public final VerticalAnchor lavaLevel;
    public final boolean aquifersEnabled;
    public final CarverDebugSettings debugSettings;

    public WorldGenCarverConfiguration(float f, HeightProvider heightprovider, FloatProvider floatprovider, VerticalAnchor verticalanchor, boolean flag, CarverDebugSettings carverdebugsettings) {
        super(f);
        this.y = heightprovider;
        this.yScale = floatprovider;
        this.lavaLevel = verticalanchor;
        this.aquifersEnabled = flag;
        this.debugSettings = carverdebugsettings;
    }
}
