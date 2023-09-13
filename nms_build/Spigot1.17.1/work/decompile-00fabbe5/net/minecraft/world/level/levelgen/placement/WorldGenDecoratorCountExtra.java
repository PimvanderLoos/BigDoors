package net.minecraft.world.level.levelgen.placement;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BlockPosition;

public class WorldGenDecoratorCountExtra extends RepeatingDecorator<WorldGenDecoratorFrequencyExtraChanceConfiguration> {

    public WorldGenDecoratorCountExtra(Codec<WorldGenDecoratorFrequencyExtraChanceConfiguration> codec) {
        super(codec);
    }

    protected int a(Random random, WorldGenDecoratorFrequencyExtraChanceConfiguration worldgendecoratorfrequencyextrachanceconfiguration, BlockPosition blockposition) {
        return worldgendecoratorfrequencyextrachanceconfiguration.count + (random.nextFloat() < worldgendecoratorfrequencyextrachanceconfiguration.extraChance ? worldgendecoratorfrequencyextrachanceconfiguration.extraCount : 0);
    }
}
