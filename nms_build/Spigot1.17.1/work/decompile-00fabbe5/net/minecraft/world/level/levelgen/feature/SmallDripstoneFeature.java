package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Iterator;
import java.util.Random;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.util.MathHelper;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.levelgen.feature.configurations.SmallDripstoneConfiguration;

public class SmallDripstoneFeature extends WorldGenerator<SmallDripstoneConfiguration> {

    public SmallDripstoneFeature(Codec<SmallDripstoneConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean generate(FeaturePlaceContext<SmallDripstoneConfiguration> featureplacecontext) {
        GeneratorAccessSeed generatoraccessseed = featureplacecontext.a();
        BlockPosition blockposition = featureplacecontext.d();
        Random random = featureplacecontext.c();
        SmallDripstoneConfiguration smalldripstoneconfiguration = (SmallDripstoneConfiguration) featureplacecontext.e();

        if (!DripstoneUtils.a((GeneratorAccess) generatoraccessseed, blockposition)) {
            return false;
        } else {
            int i = MathHelper.b(random, 1, smalldripstoneconfiguration.maxPlacements);
            boolean flag = false;

            for (int j = 0; j < i; ++j) {
                BlockPosition blockposition1 = a(random, blockposition, smalldripstoneconfiguration);

                if (a(generatoraccessseed, random, blockposition1, smalldripstoneconfiguration)) {
                    flag = true;
                }
            }

            return flag;
        }
    }

    private static boolean a(GeneratorAccessSeed generatoraccessseed, Random random, BlockPosition blockposition, SmallDripstoneConfiguration smalldripstoneconfiguration) {
        EnumDirection enumdirection = EnumDirection.a(random);
        EnumDirection enumdirection1 = random.nextBoolean() ? EnumDirection.UP : EnumDirection.DOWN;
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = blockposition.i();

        for (int i = 0; i < smalldripstoneconfiguration.emptySpaceSearchRadius; ++i) {
            if (!DripstoneUtils.a((GeneratorAccess) generatoraccessseed, (BlockPosition) blockposition_mutableblockposition)) {
                return false;
            }

            if (a(generatoraccessseed, random, blockposition_mutableblockposition, enumdirection1, smalldripstoneconfiguration)) {
                return true;
            }

            if (a(generatoraccessseed, random, blockposition_mutableblockposition, enumdirection1.opposite(), smalldripstoneconfiguration)) {
                return true;
            }

            blockposition_mutableblockposition.c(enumdirection);
        }

        return false;
    }

    private static boolean a(GeneratorAccessSeed generatoraccessseed, Random random, BlockPosition blockposition, EnumDirection enumdirection, SmallDripstoneConfiguration smalldripstoneconfiguration) {
        if (!DripstoneUtils.a((GeneratorAccess) generatoraccessseed, blockposition)) {
            return false;
        } else {
            BlockPosition blockposition1 = blockposition.shift(enumdirection.opposite());
            IBlockData iblockdata = generatoraccessseed.getType(blockposition1);

            if (!DripstoneUtils.b(iblockdata)) {
                return false;
            } else {
                a(generatoraccessseed, random, blockposition1);
                int i = random.nextFloat() < smalldripstoneconfiguration.chanceOfTallerDripstone && DripstoneUtils.a((GeneratorAccess) generatoraccessseed, blockposition.shift(enumdirection)) ? 2 : 1;

                DripstoneUtils.a(generatoraccessseed, blockposition, enumdirection, i, false);
                return true;
            }
        }
    }

    private static void a(GeneratorAccessSeed generatoraccessseed, Random random, BlockPosition blockposition) {
        DripstoneUtils.a(generatoraccessseed, blockposition);
        Iterator iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

        while (iterator.hasNext()) {
            EnumDirection enumdirection = (EnumDirection) iterator.next();

            if (random.nextFloat() >= 0.3F) {
                BlockPosition blockposition1 = blockposition.shift(enumdirection);

                DripstoneUtils.a(generatoraccessseed, blockposition1);
                if (!random.nextBoolean()) {
                    BlockPosition blockposition2 = blockposition1.shift(EnumDirection.a(random));

                    DripstoneUtils.a(generatoraccessseed, blockposition2);
                    if (!random.nextBoolean()) {
                        BlockPosition blockposition3 = blockposition2.shift(EnumDirection.a(random));

                        DripstoneUtils.a(generatoraccessseed, blockposition3);
                    }
                }
            }
        }

    }

    private static BlockPosition a(Random random, BlockPosition blockposition, SmallDripstoneConfiguration smalldripstoneconfiguration) {
        return blockposition.c(MathHelper.b(random, -smalldripstoneconfiguration.maxOffsetFromOrigin, smalldripstoneconfiguration.maxOffsetFromOrigin), MathHelper.b(random, -smalldripstoneconfiguration.maxOffsetFromOrigin, smalldripstoneconfiguration.maxOffsetFromOrigin), MathHelper.b(random, -smalldripstoneconfiguration.maxOffsetFromOrigin, smalldripstoneconfiguration.maxOffsetFromOrigin));
    }
}
