package net.minecraft.server;

import com.mojang.serialization.Codec;

public abstract class WorldGenDecoratorHeightAbstract<DC extends WorldGenFeatureDecoratorConfiguration> extends WorldGenDecorator<DC> {

    public WorldGenDecoratorHeightAbstract(Codec<DC> codec) {
        super(codec);
    }

    protected abstract HeightMap.Type a(DC dc);
}
