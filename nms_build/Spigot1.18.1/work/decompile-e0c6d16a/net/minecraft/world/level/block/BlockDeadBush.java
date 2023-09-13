package net.minecraft.world.level.block;

import net.minecraft.core.BlockPosition;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsBlock;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.VoxelShapeCollision;

public class BlockDeadBush extends BlockPlant {

    protected static final float AABB_OFFSET = 6.0F;
    protected static final VoxelShape SHAPE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 13.0D, 14.0D);

    protected BlockDeadBush(BlockBase.Info blockbase_info) {
        super(blockbase_info);
    }

    @Override
    public VoxelShape getShape(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        return BlockDeadBush.SHAPE;
    }

    @Override
    protected boolean mayPlaceOn(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return iblockdata.is(Blocks.SAND) || iblockdata.is(Blocks.RED_SAND) || iblockdata.is(Blocks.TERRACOTTA) || iblockdata.is(Blocks.WHITE_TERRACOTTA) || iblockdata.is(Blocks.ORANGE_TERRACOTTA) || iblockdata.is(Blocks.MAGENTA_TERRACOTTA) || iblockdata.is(Blocks.LIGHT_BLUE_TERRACOTTA) || iblockdata.is(Blocks.YELLOW_TERRACOTTA) || iblockdata.is(Blocks.LIME_TERRACOTTA) || iblockdata.is(Blocks.PINK_TERRACOTTA) || iblockdata.is(Blocks.GRAY_TERRACOTTA) || iblockdata.is(Blocks.LIGHT_GRAY_TERRACOTTA) || iblockdata.is(Blocks.CYAN_TERRACOTTA) || iblockdata.is(Blocks.PURPLE_TERRACOTTA) || iblockdata.is(Blocks.BLUE_TERRACOTTA) || iblockdata.is(Blocks.BROWN_TERRACOTTA) || iblockdata.is(Blocks.GREEN_TERRACOTTA) || iblockdata.is(Blocks.RED_TERRACOTTA) || iblockdata.is(Blocks.BLACK_TERRACOTTA) || iblockdata.is((Tag) TagsBlock.DIRT);
    }
}
