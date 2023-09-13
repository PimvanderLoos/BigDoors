package net.minecraft.world.level.levelgen.placement;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.levelgen.HeightMap;

public class WaterDepthThresholdDecorator extends WorldGenDecorator<WaterDepthThresholdConfiguration> {

    public WaterDepthThresholdDecorator(Codec<WaterDepthThresholdConfiguration> codec) {
        super(codec);
    }

    public Stream<BlockPosition> a(WorldGenDecoratorContext worldgendecoratorcontext, Random random, WaterDepthThresholdConfiguration waterdepththresholdconfiguration, BlockPosition blockposition) {
        int i = worldgendecoratorcontext.a(HeightMap.Type.OCEAN_FLOOR, blockposition.getX(), blockposition.getZ());
        int j = worldgendecoratorcontext.a(HeightMap.Type.WORLD_SURFACE, blockposition.getX(), blockposition.getZ());

        return j - i > waterdepththresholdconfiguration.maxWaterDepth ? Stream.of() : Stream.of(blockposition);
    }
}
