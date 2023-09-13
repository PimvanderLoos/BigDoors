package net.minecraft.world.level.block;

import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.world.item.context.BlockActionContext;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.BlockStateList;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.material.EnumPistonReaction;
import net.minecraft.world.level.pathfinder.PathMode;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.VoxelShapeCollision;

public class BlockEndRod extends BlockDirectional {

    protected static final VoxelShape b = Block.a(6.0D, 0.0D, 6.0D, 10.0D, 16.0D, 10.0D);
    protected static final VoxelShape c = Block.a(6.0D, 6.0D, 0.0D, 10.0D, 10.0D, 16.0D);
    protected static final VoxelShape d = Block.a(0.0D, 6.0D, 6.0D, 16.0D, 10.0D, 10.0D);

    protected BlockEndRod(BlockBase.Info blockbase_info) {
        super(blockbase_info);
        this.j((IBlockData) ((IBlockData) this.blockStateList.getBlockData()).set(BlockEndRod.FACING, EnumDirection.UP));
    }

    @Override
    public IBlockData a(IBlockData iblockdata, EnumBlockRotation enumblockrotation) {
        return (IBlockData) iblockdata.set(BlockEndRod.FACING, enumblockrotation.a((EnumDirection) iblockdata.get(BlockEndRod.FACING)));
    }

    @Override
    public IBlockData a(IBlockData iblockdata, EnumBlockMirror enumblockmirror) {
        return (IBlockData) iblockdata.set(BlockEndRod.FACING, enumblockmirror.b((EnumDirection) iblockdata.get(BlockEndRod.FACING)));
    }

    @Override
    public VoxelShape b(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        switch (((EnumDirection) iblockdata.get(BlockEndRod.FACING)).n()) {
            case X:
            default:
                return BlockEndRod.d;
            case Z:
                return BlockEndRod.c;
            case Y:
                return BlockEndRod.b;
        }
    }

    @Override
    public IBlockData getPlacedState(BlockActionContext blockactioncontext) {
        EnumDirection enumdirection = blockactioncontext.getClickedFace();
        IBlockData iblockdata = blockactioncontext.getWorld().getType(blockactioncontext.getClickPosition().shift(enumdirection.opposite()));

        return iblockdata.a((Block) this) && iblockdata.get(BlockEndRod.FACING) == enumdirection ? (IBlockData) this.getBlockData().set(BlockEndRod.FACING, enumdirection.opposite()) : (IBlockData) this.getBlockData().set(BlockEndRod.FACING, enumdirection);
    }

    @Override
    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockEndRod.FACING);
    }

    @Override
    public EnumPistonReaction getPushReaction(IBlockData iblockdata) {
        return EnumPistonReaction.NORMAL;
    }

    @Override
    public boolean a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, PathMode pathmode) {
        return false;
    }
}
