package net.minecraft.world.level.block;

import net.minecraft.core.BlockPosition;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.IMaterial;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.VoxelShapeCollision;

public class BlockCarrots extends BlockCrops {

    private static final VoxelShape[] SHAPE_BY_AGE = new VoxelShape[]{Block.a(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D), Block.a(0.0D, 0.0D, 0.0D, 16.0D, 3.0D, 16.0D), Block.a(0.0D, 0.0D, 0.0D, 16.0D, 4.0D, 16.0D), Block.a(0.0D, 0.0D, 0.0D, 16.0D, 5.0D, 16.0D), Block.a(0.0D, 0.0D, 0.0D, 16.0D, 6.0D, 16.0D), Block.a(0.0D, 0.0D, 0.0D, 16.0D, 7.0D, 16.0D), Block.a(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D), Block.a(0.0D, 0.0D, 0.0D, 16.0D, 9.0D, 16.0D)};

    public BlockCarrots(BlockBase.Info blockbase_info) {
        super(blockbase_info);
    }

    @Override
    protected IMaterial e() {
        return Items.CARROT;
    }

    @Override
    public VoxelShape a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        return BlockCarrots.SHAPE_BY_AGE[(Integer) iblockdata.get(this.c())];
    }
}
