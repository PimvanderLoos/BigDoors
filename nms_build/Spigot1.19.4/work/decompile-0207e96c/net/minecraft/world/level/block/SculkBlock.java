package net.minecraft.world.level.block;

import java.util.Iterator;
import net.minecraft.core.BlockPosition;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.util.MathHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockProperties;
import net.minecraft.world.level.material.FluidType;
import net.minecraft.world.level.material.FluidTypes;

public class SculkBlock extends DropExperienceBlock implements SculkBehaviour {

    public SculkBlock(BlockBase.Info blockbase_info) {
        super(blockbase_info, ConstantInt.of(1));
    }

    @Override
    public int attemptUseCharge(SculkSpreader.a sculkspreader_a, GeneratorAccess generatoraccess, BlockPosition blockposition, RandomSource randomsource, SculkSpreader sculkspreader, boolean flag) {
        int i = sculkspreader_a.getCharge();

        if (i != 0 && randomsource.nextInt(sculkspreader.chargeDecayRate()) == 0) {
            BlockPosition blockposition1 = sculkspreader_a.getPos();
            boolean flag1 = blockposition1.closerThan(blockposition, (double) sculkspreader.noGrowthRadius());

            if (!flag1 && canPlaceGrowth(generatoraccess, blockposition1)) {
                int j = sculkspreader.growthSpawnCost();

                if (randomsource.nextInt(j) < i) {
                    BlockPosition blockposition2 = blockposition1.above();
                    IBlockData iblockdata = this.getRandomGrowthState(generatoraccess, blockposition2, randomsource, sculkspreader.isWorldGeneration());

                    generatoraccess.setBlock(blockposition2, iblockdata, 3);
                    generatoraccess.playSound((EntityHuman) null, blockposition1, iblockdata.getSoundType().getPlaceSound(), SoundCategory.BLOCKS, 1.0F, 1.0F);
                }

                return Math.max(0, i - j);
            } else {
                return randomsource.nextInt(sculkspreader.additionalDecayRate()) != 0 ? i : i - (flag1 ? 1 : getDecayPenalty(sculkspreader, blockposition1, blockposition, i));
            }
        } else {
            return i;
        }
    }

    private static int getDecayPenalty(SculkSpreader sculkspreader, BlockPosition blockposition, BlockPosition blockposition1, int i) {
        int j = sculkspreader.noGrowthRadius();
        float f = MathHelper.square((float) Math.sqrt(blockposition.distSqr(blockposition1)) - (float) j);
        int k = MathHelper.square(24 - j);
        float f1 = Math.min(1.0F, f / (float) k);

        return Math.max(1, (int) ((float) i * f1 * 0.5F));
    }

    private IBlockData getRandomGrowthState(GeneratorAccess generatoraccess, BlockPosition blockposition, RandomSource randomsource, boolean flag) {
        IBlockData iblockdata;

        if (randomsource.nextInt(11) == 0) {
            iblockdata = (IBlockData) Blocks.SCULK_SHRIEKER.defaultBlockState().setValue(SculkShriekerBlock.CAN_SUMMON, flag);
        } else {
            iblockdata = Blocks.SCULK_SENSOR.defaultBlockState();
        }

        return iblockdata.hasProperty(BlockProperties.WATERLOGGED) && !generatoraccess.getFluidState(blockposition).isEmpty() ? (IBlockData) iblockdata.setValue(BlockProperties.WATERLOGGED, true) : iblockdata;
    }

    private static boolean canPlaceGrowth(GeneratorAccess generatoraccess, BlockPosition blockposition) {
        IBlockData iblockdata = generatoraccess.getBlockState(blockposition.above());

        if (!iblockdata.isAir() && (!iblockdata.is(Blocks.WATER) || !iblockdata.getFluidState().is((FluidType) FluidTypes.WATER))) {
            return false;
        } else {
            int i = 0;
            Iterator iterator = BlockPosition.betweenClosed(blockposition.offset(-4, 0, -4), blockposition.offset(4, 2, 4)).iterator();

            do {
                if (!iterator.hasNext()) {
                    return true;
                }

                BlockPosition blockposition1 = (BlockPosition) iterator.next();
                IBlockData iblockdata1 = generatoraccess.getBlockState(blockposition1);

                if (iblockdata1.is(Blocks.SCULK_SENSOR) || iblockdata1.is(Blocks.SCULK_SHRIEKER)) {
                    ++i;
                }
            } while (i <= 2);

            return false;
        }
    }

    @Override
    public boolean canChangeBlockStateOnSpread() {
        return false;
    }
}
