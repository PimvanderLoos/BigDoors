package net.minecraft.world.level.levelgen.placement;

import com.mojang.serialization.Codec;
import net.minecraft.world.level.levelgen.HeightMap;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureDecoratorConfiguration;

public abstract class WorldGenDecoratorHeightAbstract<DC extends WorldGenFeatureDecoratorConfiguration> extends WorldGenDecorator<DC> {

    public WorldGenDecoratorHeightAbstract(Codec<DC> codec) {
        super(codec);
    }

    protected abstract HeightMap.Type a(DC dc);
}
