package net.minecraft.world.level.levelgen.placement;

import com.mojang.serialization.Codec;
import net.minecraft.world.level.levelgen.HeightMap;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureDecoratorConfiguration;

public class WorldGenDecoratorHeightmap<DC extends WorldGenFeatureDecoratorConfiguration> extends WorldGenDecoratorHeight<DC> {

    public WorldGenDecoratorHeightmap(Codec<DC> codec) {
        super(codec);
    }

    @Override
    protected HeightMap.Type a(DC dc) {
        return HeightMap.Type.MOTION_BLOCKING;
    }
}
