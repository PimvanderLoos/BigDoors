package net.minecraft.world.level.levelgen.placement;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BlockPosition;

public class WorldGenDecoratorLakeLava extends RepeatingDecorator<WorldGenDecoratorDungeonConfiguration> {

    public WorldGenDecoratorLakeLava(Codec<WorldGenDecoratorDungeonConfiguration> codec) {
        super(codec);
    }

    protected int a(Random random, WorldGenDecoratorDungeonConfiguration worldgendecoratordungeonconfiguration, BlockPosition blockposition) {
        return blockposition.getY() >= 63 && random.nextInt(10) != 0 ? 0 : 1;
    }
}
