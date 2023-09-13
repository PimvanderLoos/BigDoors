package net.minecraft.world.level.levelgen.placement;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureEmptyConfiguration2;

public class WorldGenDecoratorSpread32Above extends VerticalDecorator<WorldGenFeatureEmptyConfiguration2> {

    public WorldGenDecoratorSpread32Above(Codec<WorldGenFeatureEmptyConfiguration2> codec) {
        super(codec);
    }

    protected int a(WorldGenDecoratorContext worldgendecoratorcontext, Random random, WorldGenFeatureEmptyConfiguration2 worldgenfeatureemptyconfiguration2, int i) {
        return random.nextInt(Math.max(i, 0) + 32);
    }
}
