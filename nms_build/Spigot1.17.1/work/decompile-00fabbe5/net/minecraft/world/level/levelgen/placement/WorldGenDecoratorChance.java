package net.minecraft.world.level.levelgen.placement;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BlockPosition;

public class WorldGenDecoratorChance extends RepeatingDecorator<WorldGenDecoratorDungeonConfiguration> {

    public WorldGenDecoratorChance(Codec<WorldGenDecoratorDungeonConfiguration> codec) {
        super(codec);
    }

    protected int a(Random random, WorldGenDecoratorDungeonConfiguration worldgendecoratordungeonconfiguration, BlockPosition blockposition) {
        return random.nextFloat() < 1.0F / (float) worldgendecoratordungeonconfiguration.chance ? 1 : 0;
    }
}
