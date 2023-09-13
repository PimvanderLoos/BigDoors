package net.minecraft.world.level.levelgen.placement;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenDecoratorFrequencyConfiguration;

public class WorldGenDecoratorCount extends RepeatingDecorator<WorldGenDecoratorFrequencyConfiguration> {

    public WorldGenDecoratorCount(Codec<WorldGenDecoratorFrequencyConfiguration> codec) {
        super(codec);
    }

    protected int a(Random random, WorldGenDecoratorFrequencyConfiguration worldgendecoratorfrequencyconfiguration, BlockPosition blockposition) {
        return worldgendecoratorfrequencyconfiguration.a().a(random);
    }
}
