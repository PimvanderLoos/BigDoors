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
    protected static final VoxelShape SHAPE = Block.a(2.0D, 0.0D, 2.0D, 14.0D, 13.0D, 14.0D);

    protected BlockDeadBush(BlockBase.Info blockbase_info) {
        super(blockbase_info);
    }

    @Override
    public VoxelShape a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        return BlockDeadBush.SHAPE;
    }

    @Override
    protected boolean d(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return iblockdata.a(Blocks.SAND) || iblockdata.a(Blocks.RED_SAND) || iblockdata.a(Blocks.TERRACOTTA) || iblockdata.a(Blocks.WHITE_TERRACOTTA) || iblockdata.a(Blocks.ORANGE_TERRACOTTA) || iblockdata.a(Blocks.MAGENTA_TERRACOTTA) || iblockdata.a(Blocks.LIGHT_BLUE_TERRACOTTA) || iblockdata.a(Blocks.YELLOW_TERRACOTTA) || iblockdata.a(Blocks.LIME_TERRACOTTA) || iblockdata.a(Blocks.PINK_TERRACOTTA) || iblockdata.a(Blocks.GRAY_TERRACOTTA) || iblockdata.a(Blocks.LIGHT_GRAY_TERRACOTTA) || iblockdata.a(Blocks.CYAN_TERRACOTTA) || iblockdata.a(Blocks.PURPLE_TERRACOTTA) || iblockdata.a(Blocks.BLUE_TERRACOTTA) || iblockdata.a(Blocks.BROWN_TERRACOTTA) || iblockdata.a(Blocks.GREEN_TERRACOTTA) || iblockdata.a(Blocks.RED_TERRACOTTA) || iblockdata.a(Blocks.BLACK_TERRACOTTA) || iblockdata.a((Tag) TagsBlock.DIRT);
    }
}
