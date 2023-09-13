package net.minecraft.world.level.levelgen.carver;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.world.level.block.Block;
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
        }), CarverDebugSettings.CODEC.optionalFieldOf("debug_settings", CarverDebugSettings.DEFAULT).forGetter((worldgencarverconfiguration) -> {
            return worldgencarverconfiguration.debugSettings;
        }), RegistryCodecs.homogeneousList(Registries.BLOCK).fieldOf("replaceable").forGetter((worldgencarverconfiguration) -> {
            return worldgencarverconfiguration.replaceable;
        })).apply(instance, WorldGenCarverConfiguration::new);
    });
    public final HeightProvider y;
    public final FloatProvider yScale;
    public final VerticalAnchor lavaLevel;
    public final CarverDebugSettings debugSettings;
    public final HolderSet<Block> replaceable;

    public WorldGenCarverConfiguration(float f, HeightProvider heightprovider, FloatProvider floatprovider, VerticalAnchor verticalanchor, CarverDebugSettings carverdebugsettings, HolderSet<Block> holderset) {
        super(f);
        this.y = heightprovider;
        this.yScale = floatprovider;
        this.lavaLevel = verticalanchor;
        this.debugSettings = carverdebugsettings;
        this.replaceable = holderset;
    }
}
