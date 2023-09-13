package net.minecraft.world.level.levelgen.placement;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.biome.BiomeBase;

public class WorldGenDecoratorCountNoiseBiased extends RepeatingDecorator<WorldGenDecoratorNoiseConfiguration> {

    public WorldGenDecoratorCountNoiseBiased(Codec<WorldGenDecoratorNoiseConfiguration> codec) {
        super(codec);
    }

    protected int a(Random random, WorldGenDecoratorNoiseConfiguration worldgendecoratornoiseconfiguration, BlockPosition blockposition) {
        double d0 = BiomeBase.BIOME_INFO_NOISE.a((double) blockposition.getX() / worldgendecoratornoiseconfiguration.noiseFactor, (double) blockposition.getZ() / worldgendecoratornoiseconfiguration.noiseFactor, false);

        return (int) Math.ceil((d0 + worldgendecoratornoiseconfiguration.noiseOffset) * (double) worldgendecoratornoiseconfiguration.noiseToCountRatio);
    }
}
