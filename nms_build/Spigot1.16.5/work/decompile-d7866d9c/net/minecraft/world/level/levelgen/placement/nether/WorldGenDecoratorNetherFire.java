package net.minecraft.world.level.levelgen.placement.nether;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenDecoratorFrequencyConfiguration;
import net.minecraft.world.level.levelgen.placement.WorldGenDecoratorFeatureSimple;

public class WorldGenDecoratorNetherFire extends WorldGenDecoratorFeatureSimple<WorldGenDecoratorFrequencyConfiguration> {

    public WorldGenDecoratorNetherFire(Codec<WorldGenDecoratorFrequencyConfiguration> codec) {
        super(codec);
    }

    public Stream<BlockPosition> a(Random random, WorldGenDecoratorFrequencyConfiguration worldgendecoratorfrequencyconfiguration, BlockPosition blockposition) {
        List<BlockPosition> list = Lists.newArrayList();

        for (int i = 0; i < random.nextInt(random.nextInt(worldgendecoratorfrequencyconfiguration.a().a(random)) + 1) + 1; ++i) {
            int j = random.nextInt(16) + blockposition.getX();
            int k = random.nextInt(16) + blockposition.getZ();
            int l = random.nextInt(120) + 4;

            list.add(new BlockPosition(j, l, k));
        }

        return list.stream();
    }
}
