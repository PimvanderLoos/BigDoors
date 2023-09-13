package net.minecraft.world.level.levelgen.placement;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureDecoratorNoiseConfiguration;

public class WorldGenDecoratorCountNoise extends RepeatingDecorator<WorldGenFeatureDecoratorNoiseConfiguration> {

    public WorldGenDecoratorCountNoise(Codec<WorldGenFeatureDecoratorNoiseConfiguration> codec) {
        super(codec);
    }

    protected int a(Random random, WorldGenFeatureDecoratorNoiseConfiguration worldgenfeaturedecoratornoiseconfiguration, BlockPosition blockposition) {
        double d0 = BiomeBase.BIOME_INFO_NOISE.a((double) blockposition.getX() / 200.0D, (double) blockposition.getZ() / 200.0D, false);

        return d0 < worldgenfeaturedecoratornoiseconfiguration.noiseLevel ? worldgenfeaturedecoratornoiseconfiguration.belowNoise : worldgenfeaturedecoratornoiseconfiguration.aboveNoise;
    }
}
