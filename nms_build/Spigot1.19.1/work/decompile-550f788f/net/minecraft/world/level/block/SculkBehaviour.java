package net.minecraft.world.level.block;

import java.util.Collection;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.material.FluidType;
import net.minecraft.world.level.material.FluidTypes;

public interface SculkBehaviour {

    SculkBehaviour DEFAULT = new SculkBehaviour() {
        @Override
        public boolean attemptSpreadVein(GeneratorAccess generatoraccess, BlockPosition blockposition, IBlockData iblockdata, @Nullable Collection<EnumDirection> collection, boolean flag) {
            return collection == null ? ((SculkVeinBlock) Blocks.SCULK_VEIN).getSameSpaceSpreader().spreadAll(generatoraccess.getBlockState(blockposition), generatoraccess, blockposition, flag) > 0L : (!collection.isEmpty() ? (!iblockdata.isAir() && !iblockdata.getFluidState().is((FluidType) FluidTypes.WATER) ? false : SculkVeinBlock.regrow(generatoraccess, blockposition, iblockdata, collection)) : SculkBehaviour.super.attemptSpreadVein(generatoraccess, blockposition, iblockdata, collection, flag));
        }

        @Override
        public int attemptUseCharge(SculkSpreader.a sculkspreader_a, GeneratorAccess generatoraccess, BlockPosition blockposition, RandomSource randomsource, SculkSpreader sculkspreader, boolean flag) {
            return sculkspreader_a.getDecayDelay() > 0 ? sculkspreader_a.getCharge() : 0;
        }

        @Override
        public int updateDecayDelay(int i) {
            return Math.max(i - 1, 0);
        }
    };

    default byte getSculkSpreadDelay() {
        return 1;
    }

    default void onDischarged(GeneratorAccess generatoraccess, IBlockData iblockdata, BlockPosition blockposition, RandomSource randomsource) {}

    default boolean depositCharge(GeneratorAccess generatoraccess, BlockPosition blockposition, RandomSource randomsource) {
        return false;
    }

    default boolean attemptSpreadVein(GeneratorAccess generatoraccess, BlockPosition blockposition, IBlockData iblockdata, @Nullable Collection<EnumDirection> collection, boolean flag) {
        return ((MultifaceBlock) Blocks.SCULK_VEIN).getSpreader().spreadAll(iblockdata, generatoraccess, blockposition, flag) > 0L;
    }

    default boolean canChangeBlockStateOnSpread() {
        return true;
    }

    default int updateDecayDelay(int i) {
        return 1;
    }

    int attemptUseCharge(SculkSpreader.a sculkspreader_a, GeneratorAccess generatoraccess, BlockPosition blockposition, RandomSource randomsource, SculkSpreader sculkspreader, boolean flag);
}
