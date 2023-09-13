package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Iterator;
import java.util.Optional;
import java.util.Random;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.levelgen.feature.configurations.PointedDripstoneConfiguration;

public class PointedDripstoneFeature extends WorldGenerator<PointedDripstoneConfiguration> {

    public PointedDripstoneFeature(Codec<PointedDripstoneConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<PointedDripstoneConfiguration> featureplacecontext) {
        GeneratorAccessSeed generatoraccessseed = featureplacecontext.level();
        BlockPosition blockposition = featureplacecontext.origin();
        Random random = featureplacecontext.random();
        PointedDripstoneConfiguration pointeddripstoneconfiguration = (PointedDripstoneConfiguration) featureplacecontext.config();
        Optional<EnumDirection> optional = getTipDirection(generatoraccessseed, blockposition, random);

        if (optional.isEmpty()) {
            return false;
        } else {
            BlockPosition blockposition1 = blockposition.relative(((EnumDirection) optional.get()).getOpposite());

            createPatchOfDripstoneBlocks(generatoraccessseed, random, blockposition1, pointeddripstoneconfiguration);
            int i = random.nextFloat() < pointeddripstoneconfiguration.chanceOfTallerDripstone && DripstoneUtils.isEmptyOrWater(generatoraccessseed.getBlockState(blockposition.relative((EnumDirection) optional.get()))) ? 2 : 1;

            DripstoneUtils.growPointedDripstone(generatoraccessseed, blockposition, (EnumDirection) optional.get(), i, false);
            return true;
        }
    }

    private static Optional<EnumDirection> getTipDirection(GeneratorAccess generatoraccess, BlockPosition blockposition, Random random) {
        boolean flag = DripstoneUtils.isDripstoneBase(generatoraccess.getBlockState(blockposition.above()));
        boolean flag1 = DripstoneUtils.isDripstoneBase(generatoraccess.getBlockState(blockposition.below()));

        return flag && flag1 ? Optional.of(random.nextBoolean() ? EnumDirection.DOWN : EnumDirection.UP) : (flag ? Optional.of(EnumDirection.DOWN) : (flag1 ? Optional.of(EnumDirection.UP) : Optional.empty()));
    }

    private static void createPatchOfDripstoneBlocks(GeneratorAccess generatoraccess, Random random, BlockPosition blockposition, PointedDripstoneConfiguration pointeddripstoneconfiguration) {
        DripstoneUtils.placeDripstoneBlockIfPossible(generatoraccess, blockposition);
        Iterator iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

        while (iterator.hasNext()) {
            EnumDirection enumdirection = (EnumDirection) iterator.next();

            if (random.nextFloat() <= pointeddripstoneconfiguration.chanceOfDirectionalSpread) {
                BlockPosition blockposition1 = blockposition.relative(enumdirection);

                DripstoneUtils.placeDripstoneBlockIfPossible(generatoraccess, blockposition1);
                if (random.nextFloat() <= pointeddripstoneconfiguration.chanceOfSpreadRadius2) {
                    BlockPosition blockposition2 = blockposition1.relative(EnumDirection.getRandom(random));

                    DripstoneUtils.placeDripstoneBlockIfPossible(generatoraccess, blockposition2);
                    if (random.nextFloat() <= pointeddripstoneconfiguration.chanceOfSpreadRadius3) {
                        BlockPosition blockposition3 = blockposition2.relative(EnumDirection.getRandom(random));

                        DripstoneUtils.placeDripstoneBlockIfPossible(generatoraccess, blockposition3);
                    }
                }
            }
        }

    }
}
