package net.minecraft.world.level.levelgen.placement;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureEmptyConfiguration2;

public class WorldGenDecoratorEndGateway extends VerticalDecorator<WorldGenFeatureEmptyConfiguration2> {

    public WorldGenDecoratorEndGateway(Codec<WorldGenFeatureEmptyConfiguration2> codec) {
        super(codec);
    }

    protected int a(WorldGenDecoratorContext worldgendecoratorcontext, Random random, WorldGenFeatureEmptyConfiguration2 worldgenfeatureemptyconfiguration2, int i) {
        return i + 3 + random.nextInt(7);
    }
}
