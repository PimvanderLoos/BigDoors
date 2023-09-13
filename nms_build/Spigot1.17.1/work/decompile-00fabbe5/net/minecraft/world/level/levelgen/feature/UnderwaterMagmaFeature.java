package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Iterator;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Random;
import java.util.function.Predicate;
import net.minecraft.core.BaseBlockPosition;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
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
    public boolean generate(FeaturePlaceContext<UnderwaterMagmaConfiguration> featureplacecontext) {
        GeneratorAccessSeed generatoraccessseed = featureplacecontext.a();
        BlockPosition blockposition = featureplacecontext.d();
        UnderwaterMagmaConfiguration underwatermagmaconfiguration = (UnderwaterMagmaConfiguration) featureplacecontext.e();
        Random random = featureplacecontext.c();
        OptionalInt optionalint = a(generatoraccessseed, blockposition, underwatermagmaconfiguration);

        if (!optionalint.isPresent()) {
            return false;
        } else {
            BlockPosition blockposition1 = blockposition.h(optionalint.getAsInt());
            BaseBlockPosition baseblockposition = new BaseBlockPosition(underwatermagmaconfiguration.placementRadiusAroundFloor, underwatermagmaconfiguration.placementRadiusAroundFloor, underwatermagmaconfiguration.placementRadiusAroundFloor);
            AxisAlignedBB axisalignedbb = new AxisAlignedBB(blockposition1.e(baseblockposition), blockposition1.f(baseblockposition));

            return BlockPosition.a(axisalignedbb).filter((blockposition2) -> {
                return random.nextFloat() < underwatermagmaconfiguration.placementProbabilityPerValidPosition;
            }).filter((blockposition2) -> {
                return this.b(generatoraccessseed, blockposition2);
            }).mapToInt((blockposition2) -> {
                generatoraccessseed.setTypeAndData(blockposition2, Blocks.MAGMA_BLOCK.getBlockData(), 2);
                return 1;
            }).sum() > 0;
        }
    }

    private static OptionalInt a(GeneratorAccessSeed generatoraccessseed, BlockPosition blockposition, UnderwaterMagmaConfiguration underwatermagmaconfiguration) {
        Predicate<IBlockData> predicate = (iblockdata) -> {
            return iblockdata.a(Blocks.WATER);
        };
        Predicate<IBlockData> predicate1 = (iblockdata) -> {
            return !iblockdata.a(Blocks.WATER);
        };
        Optional<Column> optional = Column.a(generatoraccessseed, blockposition, underwatermagmaconfiguration.floorSearchRange, predicate, predicate1);

        return (OptionalInt) optional.map(Column::c).orElseGet(OptionalInt::empty);
    }

    private boolean b(GeneratorAccessSeed generatoraccessseed, BlockPosition blockposition) {
        if (!this.a((GeneratorAccess) generatoraccessseed, blockposition) && !this.a((GeneratorAccess) generatoraccessseed, blockposition.down())) {
            Iterator iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

            EnumDirection enumdirection;

            do {
                if (!iterator.hasNext()) {
                    return true;
                }

                enumdirection = (EnumDirection) iterator.next();
            } while (!this.a((GeneratorAccess) generatoraccessseed, blockposition.shift(enumdirection)));

            return false;
        } else {
            return false;
        }
    }

    private boolean a(GeneratorAccess generatoraccess, BlockPosition blockposition) {
        IBlockData iblockdata = generatoraccess.getType(blockposition);

        return iblockdata.a(Blocks.WATER) || iblockdata.isAir();
    }
}
