package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Iterator;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.Predicate;
import net.minecraft.core.BaseBlockPosition;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.levelgen.Column;
import net.minecraft.world.level.levelgen.feature.configurations.UnderwaterMagmaConfiguration;
import net.minecraft.world.phys.AxisAlignedBB;

public class UnderwaterMagmaFeature extends WorldGenerator<UnderwaterMagmaConfiguration> {

    public UnderwaterMagmaFeature(Codec<UnderwaterMagmaConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<UnderwaterMagmaConfiguration> featureplacecontext) {
        GeneratorAccessSeed generatoraccessseed = featureplacecontext.level();
        BlockPosition blockposition = featureplacecontext.origin();
        UnderwaterMagmaConfiguration underwatermagmaconfiguration = (UnderwaterMagmaConfiguration) featureplacecontext.config();
        RandomSource randomsource = featureplacecontext.random();
        OptionalInt optionalint = getFloorY(generatoraccessseed, blockposition, underwatermagmaconfiguration);

        if (!optionalint.isPresent()) {
            return false;
        } else {
            BlockPosition blockposition1 = blockposition.atY(optionalint.getAsInt());
            BaseBlockPosition baseblockposition = new BaseBlockPosition(underwatermagmaconfiguration.placementRadiusAroundFloor, underwatermagmaconfiguration.placementRadiusAroundFloor, underwatermagmaconfiguration.placementRadiusAroundFloor);
            AxisAlignedBB axisalignedbb = new AxisAlignedBB(blockposition1.subtract(baseblockposition), blockposition1.offset(baseblockposition));

            return BlockPosition.betweenClosedStream(axisalignedbb).filter((blockposition2) -> {
                return randomsource.nextFloat() < underwatermagmaconfiguration.placementProbabilityPerValidPosition;
            }).filter((blockposition2) -> {
                return this.isValidPlacement(generatoraccessseed, blockposition2);
            }).mapToInt((blockposition2) -> {
                generatoraccessseed.setBlock(blockposition2, Blocks.MAGMA_BLOCK.defaultBlockState(), 2);
                return 1;
            }).sum() > 0;
        }
    }

    private static OptionalInt getFloorY(GeneratorAccessSeed generatoraccessseed, BlockPosition blockposition, UnderwaterMagmaConfiguration underwatermagmaconfiguration) {
        Predicate<IBlockData> predicate = (iblockdata) -> {
            return iblockdata.is(Blocks.WATER);
        };
        Predicate<IBlockData> predicate1 = (iblockdata) -> {
            return !iblockdata.is(Blocks.WATER);
        };
        Optional<Column> optional = Column.scan(generatoraccessseed, blockposition, underwatermagmaconfiguration.floorSearchRange, predicate, predicate1);

        return (OptionalInt) optional.map(Column::getFloor).orElseGet(OptionalInt::empty);
    }

    private boolean isValidPlacement(GeneratorAccessSeed generatoraccessseed, BlockPosition blockposition) {
        if (!this.isWaterOrAir(generatoraccessseed, blockposition) && !this.isWaterOrAir(generatoraccessseed, blockposition.below())) {
            Iterator iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

            EnumDirection enumdirection;

            do {
                if (!iterator.hasNext()) {
                    return true;
                }

                enumdirection = (EnumDirection) iterator.next();
            } while (!this.isWaterOrAir(generatoraccessseed, blockposition.relative(enumdirection)));

            return false;
        } else {
            return false;
        }
    }

    private boolean isWaterOrAir(GeneratorAccess generatoraccess, BlockPosition blockposition) {
        IBlockData iblockdata = generatoraccess.getBlockState(blockposition);

        return iblockdata.is(Blocks.WATER) || iblockdata.isAir();
    }
}
