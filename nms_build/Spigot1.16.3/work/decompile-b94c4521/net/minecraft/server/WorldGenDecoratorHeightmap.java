package net.minecraft.server;

import com.mojang.serialization.Codec;

public class WorldGenDecoratorHeightmap<DC extends WorldGenFeatureDecoratorConfiguration> extends WorldGenDecoratorHeight<DC> {

    public WorldGenDecoratorHeightmap(Codec<DC> codec) {
        super(codec);
    }

    @Override
    protected HeightMap.Type a(DC dc) {
        return HeightMap.Type.MOTION_BLOCKING;
    }
}
