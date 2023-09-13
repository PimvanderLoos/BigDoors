package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Iterator;
import java.util.Optional;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.util.RandomSource;
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
        RandomSource randomsource = featureplacecontext.random();
        PointedDripstoneConfiguration pointeddripstoneconfiguration = (PointedDripstoneConfiguration) featureplacecontext.config();
        Optional<EnumDirection> optional = getTipDirection(generatoraccessseed, blockposition, randomsource);

        if (optional.isEmpty()) {
            return false;
        } else {
            BlockPosition blockposition1 = blockposition.relative(((EnumDirection) optional.get()).getOpposite());

            createPatchOfDripstoneBlocks(generatoraccessseed, randomsource, blockposition1, pointeddripstoneconfiguration);
            int i = randomsource.nextFloat() < pointeddripstoneconfiguration.chanceOfTallerDripstone && DripstoneUtils.isEmptyOrWater(generatoraccessseed.getBlockState(blockposition.relative((EnumDirection) optional.get()))) ? 2 : 1;

            DripstoneUtils.growPointedDripstone(generatoraccessseed, blockposition, (EnumDirection) optional.get(), i, false);
            return true;
        }
    }

    private static Optional<EnumDirection> getTipDirection(GeneratorAccess generatoraccess, BlockPosition blockposition, RandomSource randomsource) {
        boolean flag = DripstoneUtils.isDripstoneBase(generatoraccess.getBlockState(blockposition.above()));
        boolean flag1 = DripstoneUtils.isDripstoneBase(generatoraccess.getBlockState(blockposition.below()));

        return flag && flag1 ? Optional.of(randomsource.nextBoolean() ? EnumDirection.DOWN : EnumDirection.UP) : (flag ? Optional.of(EnumDirection.DOWN) : (flag1 ? Optional.of(EnumDirection.UP) : Optional.empty()));
    }

    private static void createPatchOfDripstoneBlocks(GeneratorAccess generatoraccess, RandomSource randomsource, BlockPosition blockposition, PointedDripstoneConfiguration pointeddripstoneconfiguration) {
        DripstoneUtils.placeDripstoneBlockIfPossible(generatoraccess, blockposition);
        Iterator iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

        while (iterator.hasNext()) {
            EnumDirection enumdirection = (EnumDirection) iterator.next();

            if (randomsource.nextFloat() <= pointeddripstoneconfiguration.chanceOfDirectionalSpread) {
                BlockPosition blockposition1 = blockposition.relative(enumdirection);

                DripstoneUtils.placeDripstoneBlockIfPossible(generatoraccess, blockposition1);
                if (randomsource.nextFloat() <= pointeddripstoneconfiguration.chanceOfSpreadRadius2) {
                    BlockPosition blockposition2 = blockposition1.relative(EnumDirection.getRandom(randomsource));

                    DripstoneUtils.placeDripstoneBlockIfPossible(generatoraccess, blockposition2);
                    if (randomsource.nextFloat() <= pointeddripstoneconfiguration.chanceOfSpreadRadius3) {
                        BlockPosition blockposition3 = blockposition2.relative(EnumDirection.getRandom(randomsource));

                        DripstoneUtils.placeDripstoneBlockIfPossible(generatoraccess, blockposition3);
                    }
                }
            }
        }

    }
}
