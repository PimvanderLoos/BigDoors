package net.minecraft.world.level.block;

import net.minecraft.core.BlockPosition;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsBlock;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.VoxelShapeCollision;

public class BlockRoots extends BlockPlant {

    protected static final VoxelShape a = Block.a(2.0D, 0.0D, 2.0D, 14.0D, 13.0D, 14.0D);

    protected BlockRoots(BlockBase.Info blockbase_info) {
        super(blockbase_info);
    }

    @Override
    public VoxelShape b(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        return BlockRoots.a;
    }

    @Override
    protected boolean c(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return iblockdata.a((Tag) TagsBlock.NYLIUM) || iblockdata.a(Blocks.SOUL_SOIL) || super.c(iblockdata, iblockaccess, blockposition);
    }

    @Override
    public BlockBase.EnumRandomOffset ah_() {
        return BlockBase.EnumRandomOffset.XZ;
    }
}
