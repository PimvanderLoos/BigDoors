package net.minecraft.world.level.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsFluid;
import net.minecraft.world.item.context.BlockActionContext;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockPropertyDoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.BlockStateEnum;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidType;
import net.minecraft.world.level.material.FluidTypes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.VoxelShapeCollision;

public class BlockTallSeaGrass extends BlockTallPlant implements IFluidContainer {

    public static final BlockStateEnum<BlockPropertyDoubleBlockHalf> b = BlockTallPlant.HALF;
    protected static final VoxelShape c = Block.a(2.0D, 0.0D, 2.0D, 14.0D, 16.0D, 14.0D);

    public BlockTallSeaGrass(BlockBase.Info blockbase_info) {
        super(blockbase_info);
    }

    @Override
    public VoxelShape b(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        return BlockTallSeaGrass.c;
    }

    @Override
    protected boolean c(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return iblockdata.d(iblockaccess, blockposition, EnumDirection.UP) && !iblockdata.a(Blocks.MAGMA_BLOCK);
    }

    @Nullable
    @Override
    public IBlockData getPlacedState(BlockActionContext blockactioncontext) {
        IBlockData iblockdata = super.getPlacedState(blockactioncontext);

        if (iblockdata != null) {
            Fluid fluid = blockactioncontext.getWorld().getFluid(blockactioncontext.getClickPosition().up());

            if (fluid.a((Tag) TagsFluid.WATER) && fluid.e() == 8) {
                return iblockdata;
            }
        }

        return null;
    }

    @Override
    public boolean canPlace(IBlockData iblockdata, IWorldReader iworldreader, BlockPosition blockposition) {
        if (iblockdata.get(BlockTallSeaGrass.b) == BlockPropertyDoubleBlockHalf.UPPER) {
            IBlockData iblockdata1 = iworldreader.getType(blockposition.down());

            return iblockdata1.a((Block) this) && iblockdata1.get(BlockTallSeaGrass.b) == BlockPropertyDoubleBlockHalf.LOWER;
        } else {
            Fluid fluid = iworldreader.getFluid(blockposition);

            return super.canPlace(iblockdata, iworldreader, blockposition) && fluid.a((Tag) TagsFluid.WATER) && fluid.e() == 8;
        }
    }

    @Override
    public Fluid d(IBlockData iblockdata) {
        return FluidTypes.WATER.a(false);
    }

    @Override
    public boolean canPlace(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata, FluidType fluidtype) {
        return false;
    }

    @Override
    public boolean place(GeneratorAccess generatoraccess, BlockPosition blockposition, IBlockData iblockdata, Fluid fluid) {
        return false;
    }
}
