package net.minecraft.world.level.block;

import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.material.EnumPistonReaction;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.VoxelShapeCollision;

public class BlockStructureVoid extends Block {

    private static final double SIZE = 5.0D;
    private static final VoxelShape SHAPE = Block.a(5.0D, 5.0D, 5.0D, 11.0D, 11.0D, 11.0D);

    protected BlockStructureVoid(BlockBase.Info blockbase_info) {
        super(blockbase_info);
    }

    @Override
    public EnumRenderType b_(IBlockData iblockdata) {
        return EnumRenderType.INVISIBLE;
    }

    @Override
    public VoxelShape a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        return BlockStructureVoid.SHAPE;
    }

    @Override
    public float b(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return 1.0F;
    }

    @Override
    public EnumPistonReaction getPushReaction(IBlockData iblockdata) {
        return EnumPistonReaction.DESTROY;
    }
}
