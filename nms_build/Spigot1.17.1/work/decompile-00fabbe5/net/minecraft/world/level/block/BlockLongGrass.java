package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.BlockPosition;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.VoxelShapeCollision;

public class BlockLongGrass extends BlockPlant implements IBlockFragilePlantElement {

    protected static final float AABB_OFFSET = 6.0F;
    protected static final VoxelShape SHAPE = Block.a(2.0D, 0.0D, 2.0D, 14.0D, 13.0D, 14.0D);

    protected BlockLongGrass(BlockBase.Info blockbase_info) {
        super(blockbase_info);
    }

    @Override
    public VoxelShape a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        return BlockLongGrass.SHAPE;
    }

    @Override
    public boolean a(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata, boolean flag) {
        return true;
    }

    @Override
    public boolean a(World world, Random random, BlockPosition blockposition, IBlockData iblockdata) {
        return true;
    }

    @Override
    public void a(WorldServer worldserver, Random random, BlockPosition blockposition, IBlockData iblockdata) {
        BlockTallPlant blocktallplant = (BlockTallPlant) (iblockdata.a(Blocks.FERN) ? Blocks.LARGE_FERN : Blocks.TALL_GRASS);

        if (blocktallplant.getBlockData().canPlace(worldserver, blockposition) && worldserver.isEmpty(blockposition.up())) {
            BlockTallPlant.a(worldserver, blocktallplant.getBlockData(), blockposition, 2);
        }

    }

    @Override
    public BlockBase.EnumRandomOffset S_() {
        return BlockBase.EnumRandomOffset.XYZ;
    }
}
