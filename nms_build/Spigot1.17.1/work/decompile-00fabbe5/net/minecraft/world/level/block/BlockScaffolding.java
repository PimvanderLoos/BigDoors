package net.minecraft.world.level.block;

import java.util.Iterator;
import java.util.Random;
import net.minecraft.core.BaseBlockPosition;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.item.EntityFallingBlock;
import net.minecraft.world.item.context.BlockActionContext;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.BlockStateList;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockProperties;
import net.minecraft.world.level.block.state.properties.BlockStateBoolean;
import net.minecraft.world.level.block.state.properties.BlockStateInteger;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidTypes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.VoxelShapeCollision;
import net.minecraft.world.phys.shapes.VoxelShapes;

public class BlockScaffolding extends Block implements IBlockWaterlogged {

    private static final int TICK_DELAY = 1;
    private static final VoxelShape STABLE_SHAPE;
    private static final VoxelShape UNSTABLE_SHAPE;
    private static final VoxelShape UNSTABLE_SHAPE_BOTTOM = Block.a(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D);
    private static final VoxelShape BELOW_BLOCK = VoxelShapes.b().a(0.0D, -1.0D, 0.0D);
    public static final int STABILITY_MAX_DISTANCE = 7;
    public static final BlockStateInteger DISTANCE = BlockProperties.STABILITY_DISTANCE;
    public static final BlockStateBoolean WATERLOGGED = BlockProperties.WATERLOGGED;
    public static final BlockStateBoolean BOTTOM = BlockProperties.BOTTOM;

    protected BlockScaffolding(BlockBase.Info blockbase_info) {
        super(blockbase_info);
        this.k((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) this.stateDefinition.getBlockData()).set(BlockScaffolding.DISTANCE, 7)).set(BlockScaffolding.WATERLOGGED, false)).set(BlockScaffolding.BOTTOM, false));
    }

    @Override
    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockScaffolding.DISTANCE, BlockScaffolding.WATERLOGGED, BlockScaffolding.BOTTOM);
    }

    @Override
    public VoxelShape a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        return !voxelshapecollision.b(iblockdata.getBlock().getItem()) ? ((Boolean) iblockdata.get(BlockScaffolding.BOTTOM) ? BlockScaffolding.UNSTABLE_SHAPE : BlockScaffolding.STABLE_SHAPE) : VoxelShapes.b();
    }

    @Override
    public VoxelShape a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return VoxelShapes.b();
    }

    @Override
    public boolean a(IBlockData iblockdata, BlockActionContext blockactioncontext) {
        return blockactioncontext.getItemStack().a(this.getItem());
    }

    @Override
    public IBlockData getPlacedState(BlockActionContext blockactioncontext) {
        BlockPosition blockposition = blockactioncontext.getClickPosition();
        World world = blockactioncontext.getWorld();
        int i = a((IBlockAccess) world, blockposition);

        return (IBlockData) ((IBlockData) ((IBlockData) this.getBlockData().set(BlockScaffolding.WATERLOGGED, world.getFluid(blockposition).getType() == FluidTypes.WATER)).set(BlockScaffolding.DISTANCE, i)).set(BlockScaffolding.BOTTOM, this.a(world, blockposition, i));
    }

    @Override
    public void onPlace(IBlockData iblockdata, World world, BlockPosition blockposition, IBlockData iblockdata1, boolean flag) {
        if (!world.isClientSide) {
            world.getBlockTickList().a(blockposition, this, 1);
        }

    }

    @Override
    public IBlockData updateState(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        if ((Boolean) iblockdata.get(BlockScaffolding.WATERLOGGED)) {
            generatoraccess.getFluidTickList().a(blockposition, FluidTypes.WATER, FluidTypes.WATER.a((IWorldReader) generatoraccess));
        }

        if (!generatoraccess.isClientSide()) {
            generatoraccess.getBlockTickList().a(blockposition, this, 1);
        }

        return iblockdata;
    }

    @Override
    public void tickAlways(IBlockData iblockdata, WorldServer worldserver, BlockPosition blockposition, Random random) {
        int i = a((IBlockAccess) worldserver, blockposition);
        IBlockData iblockdata1 = (IBlockData) ((IBlockData) iblockdata.set(BlockScaffolding.DISTANCE, i)).set(BlockScaffolding.BOTTOM, this.a(worldserver, blockposition, i));

        if ((Integer) iblockdata1.get(BlockScaffolding.DISTANCE) == 7) {
            if ((Integer) iblockdata.get(BlockScaffolding.DISTANCE) == 7) {
                worldserver.addEntity(new EntityFallingBlock(worldserver, (double) blockposition.getX() + 0.5D, (double) blockposition.getY(), (double) blockposition.getZ() + 0.5D, (IBlockData) iblockdata1.set(BlockScaffolding.WATERLOGGED, false)));
            } else {
                worldserver.b(blockposition, true);
            }
        } else if (iblockdata != iblockdata1) {
            worldserver.setTypeAndData(blockposition, iblockdata1, 3);
        }

    }

    @Override
    public boolean canPlace(IBlockData iblockdata, IWorldReader iworldreader, BlockPosition blockposition) {
        return a((IBlockAccess) iworldreader, blockposition) < 7;
    }

    @Override
    public VoxelShape c(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        return voxelshapecollision.a(VoxelShapes.b(), blockposition, true) && !voxelshapecollision.b() ? BlockScaffolding.STABLE_SHAPE : ((Integer) iblockdata.get(BlockScaffolding.DISTANCE) != 0 && (Boolean) iblockdata.get(BlockScaffolding.BOTTOM) && voxelshapecollision.a(BlockScaffolding.BELOW_BLOCK, blockposition, true) ? BlockScaffolding.UNSTABLE_SHAPE_BOTTOM : VoxelShapes.a());
    }

    @Override
    public Fluid c_(IBlockData iblockdata) {
        return (Boolean) iblockdata.get(BlockScaffolding.WATERLOGGED) ? FluidTypes.WATER.a(false) : super.c_(iblockdata);
    }

    private boolean a(IBlockAccess iblockaccess, BlockPosition blockposition, int i) {
        return i > 0 && !iblockaccess.getType(blockposition.down()).a((Block) this);
    }

    public static int a(IBlockAccess iblockaccess, BlockPosition blockposition) {
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = blockposition.i().c(EnumDirection.DOWN);
        IBlockData iblockdata = iblockaccess.getType(blockposition_mutableblockposition);
        int i = 7;

        if (iblockdata.a(Blocks.SCAFFOLDING)) {
            i = (Integer) iblockdata.get(BlockScaffolding.DISTANCE);
        } else if (iblockdata.d(iblockaccess, blockposition_mutableblockposition, EnumDirection.UP)) {
            return 0;
        }

        Iterator iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

        while (iterator.hasNext()) {
            EnumDirection enumdirection = (EnumDirection) iterator.next();
            IBlockData iblockdata1 = iblockaccess.getType(blockposition_mutableblockposition.a((BaseBlockPosition) blockposition, enumdirection));

            if (iblockdata1.a(Blocks.SCAFFOLDING)) {
                i = Math.min(i, (Integer) iblockdata1.get(BlockScaffolding.DISTANCE) + 1);
                if (i == 1) {
                    break;
                }
            }
        }

        return i;
    }

    static {
        VoxelShape voxelshape = Block.a(0.0D, 14.0D, 0.0D, 16.0D, 16.0D, 16.0D);
        VoxelShape voxelshape1 = Block.a(0.0D, 0.0D, 0.0D, 2.0D, 16.0D, 2.0D);
        VoxelShape voxelshape2 = Block.a(14.0D, 0.0D, 0.0D, 16.0D, 16.0D, 2.0D);
        VoxelShape voxelshape3 = Block.a(0.0D, 0.0D, 14.0D, 2.0D, 16.0D, 16.0D);
        VoxelShape voxelshape4 = Block.a(14.0D, 0.0D, 14.0D, 16.0D, 16.0D, 16.0D);

        STABLE_SHAPE = VoxelShapes.a(voxelshape, voxelshape1, voxelshape2, voxelshape3, voxelshape4);
        VoxelShape voxelshape5 = Block.a(0.0D, 0.0D, 0.0D, 2.0D, 2.0D, 16.0D);
        VoxelShape voxelshape6 = Block.a(14.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D);
        VoxelShape voxelshape7 = Block.a(0.0D, 0.0D, 14.0D, 16.0D, 2.0D, 16.0D);
        VoxelShape voxelshape8 = Block.a(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 2.0D);

        UNSTABLE_SHAPE = VoxelShapes.a(BlockScaffolding.UNSTABLE_SHAPE_BOTTOM, BlockScaffolding.STABLE_SHAPE, voxelshape6, voxelshape5, voxelshape8, voxelshape7);
    }
}
