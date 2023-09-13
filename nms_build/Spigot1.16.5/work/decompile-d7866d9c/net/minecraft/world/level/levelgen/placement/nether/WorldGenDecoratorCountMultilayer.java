package net.minecraft.world.level.levelgen.placement.nether;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.levelgen.HeightMap;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenDecoratorFrequencyConfiguration;
import net.minecraft.world.level.levelgen.placement.WorldGenDecorator;
import net.minecraft.world.level.levelgen.placement.WorldGenDecoratorContext;

public class WorldGenDecoratorCountMultilayer extends WorldGenDecorator<WorldGenDecoratorFrequencyConfiguration> {

    public WorldGenDecoratorCountMultilayer(Codec<WorldGenDecoratorFrequencyConfiguration> codec) {
        super(codec);
    }

    public Stream<BlockPosition> a(WorldGenDecoratorContext worldgendecoratorcontext, Random random, WorldGenDecoratorFrequencyConfiguration worldgendecoratorfrequencyconfiguration, BlockPosition blockposition) {
        List<BlockPosition> list = Lists.newArrayList();
        int i = 0;

        boolean flag;

        do {
            flag = false;

            for (int j = 0; j < worldgendecoratorfrequencyconfiguration.a().a(random); ++j) {
                int k = random.nextInt(16) + blockposition.getX();
                int l = random.nextInt(16) + blockposition.getZ();
                int i1 = worldgendecoratorcontext.a(HeightMap.Type.MOTION_BLOCKING, k, l);
                int j1 = a(worldgendecoratorcontext, k, i1, l, i);

                if (j1 != Integer.MAX_VALUE) {
                    list.add(new BlockPosition(k, j1, l));
                    flag = true;
                }
            }

            ++i;
        } while (flag);

        return list.stream();
    }

    private static int a(WorldGenDecoratorContext worldgendecoratorcontext, int i, int j, int k, int l) {
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition(i, j, k);
        int i1 = 0;
        IBlockData iblockdata = worldgendecoratorcontext.a(blockposition_mutableblockposition);

        for (int j1 = j; j1 >= 1; --j1) {
            blockposition_mutableblockposition.p(j1 - 1);
            IBlockData iblockdata1 = worldgendecoratorcontext.a(blockposition_mutableblockposition);

            if (!a(iblockdata1) && a(iblockdata) && !iblockdata1.a(Blocks.BEDROCK)) {
                if (i1 == l) {
                    return blockposition_mutableblockposition.getY() + 1;
                }

                ++i1;
            }

            iblockdata = iblockdata1;
        }

        return Integer.MAX_VALUE;
    }

    private static boolean a(IBlockData iblockdata) {
        return iblockdata.isAir() || iblockdata.a(Blocks.WATER) || iblockdata.a(Blocks.LAVA);
    }
}
