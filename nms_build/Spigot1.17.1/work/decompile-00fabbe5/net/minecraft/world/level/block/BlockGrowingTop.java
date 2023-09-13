package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.BlockStateList;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockProperties;
import net.minecraft.world.level.block.state.properties.BlockStateInteger;
import net.minecraft.world.level.block.state.properties.IBlockState;
import net.minecraft.world.level.material.FluidTypes;
import net.minecraft.world.phys.shapes.VoxelShape;

public abstract class BlockGrowingTop extends BlockGrowingAbstract implements IBlockFragilePlantElement {

    public static final BlockStateInteger AGE = BlockProperties.AGE_25;
    public static final int MAX_AGE = 25;
    private final double growPerTickProbability;

    protected BlockGrowingTop(BlockBase.Info blockbase_info, EnumDirection enumdirection, VoxelShape voxelshape, boolean flag, double d0) {
        super(blockbase_info, enumdirection, voxelshape, flag);
        this.growPerTickProbability = d0;
        this.k((IBlockData) ((IBlockData) this.stateDefinition.getBlockData()).set(BlockGrowingTop.AGE, 0));
    }

    @Override
    public IBlockData a(GeneratorAccess generatoraccess) {
        return (IBlockData) this.getBlockData().set(BlockGrowingTop.AGE, generatoraccess.getRandom().nextInt(25));
    }

    @Override
    public boolean isTicking(IBlockData iblockdata) {
        return (Integer) iblockdata.get(BlockGrowingTop.AGE) < 25;
    }

    @Override
    public void tick(IBlockData iblockdata, WorldServer worldserver, BlockPosition blockposition, Random random) {
        if ((Integer) iblockdata.get(BlockGrowingTop.AGE) < 25 && random.nextDouble() < this.growPerTickProbability) {
            BlockPosition blockposition1 = blockposition.shift(this.growthDirection);

            if (this.g(worldserver.getType(blockposition1))) {
                worldserver.setTypeUpdate(blockposition1, this.a(iblockdata, worldserver.random));
            }
        }

    }

    protected IBlockData a(IBlockData iblockdata, Random random) {
        return (IBlockData) iblockdata.a((IBlockState) BlockGrowingTop.AGE);
    }

    protected IBlockData a(IBlockData iblockdata, IBlockData iblockdata1) {
        return iblockdata1;
    }

    @Override
    public IBlockData updateState(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        if (enumdirection == this.growthDirection.opposite() && !iblockdata.canPlace(generatoraccess, blockposition)) {
            generatoraccess.getBlockTickList().a(blockposition, this, 1);
        }

        if (enumdirection == this.growthDirection && (iblockdata1.a((Block) this) || iblockdata1.a(this.c()))) {
            return this.a(iblockdata, this.c().getBlockData());
        } else {
            if (this.scheduleFluidTicks) {
                generatoraccess.getFluidTickList().a(blockposition, FluidTypes.WATER, FluidTypes.WATER.a((IWorldReader) generatoraccess));
            }

            return super.updateState(iblockdata, enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1);
        }
    }

    @Override
    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockGrowingTop.AGE);
    }

    @Override
    public boolean a(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata, boolean flag) {
        return this.g(iblockaccess.getType(blockposition.shift(this.growthDirection)));
    }

    @Override
    public boolean a(World world, Random random, BlockPosition blockposition, IBlockData iblockdata) {
        return true;
    }

    @Override
    public void a(WorldServer worldserver, Random random, BlockPosition blockposition, IBlockData iblockdata) {
        BlockPosition blockposition1 = blockposition.shift(this.growthDirection);
        int i = Math.min((Integer) iblockdata.get(BlockGrowingTop.AGE) + 1, 25);
        int j = this.a(random);

        for (int k = 0; k < j && this.g(worldserver.getType(blockposition1)); ++k) {
            worldserver.setTypeUpdate(blockposition1, (IBlockData) iblockdata.set(BlockGrowingTop.AGE, i));
            blockposition1 = blockposition1.shift(this.growthDirection);
            i = Math.min(i + 1, 25);
        }

    }

    protected abstract int a(Random random);

    protected abstract boolean g(IBlockData iblockdata);

    @Override
    protected BlockGrowingTop d() {
        return this;
    }
}
