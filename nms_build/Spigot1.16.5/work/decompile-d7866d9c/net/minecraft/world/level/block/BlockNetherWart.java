package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.BlockPosition;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.BlockStateList;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockProperties;
import net.minecraft.world.level.block.state.properties.BlockStateInteger;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.VoxelShapeCollision;

public class BlockNetherWart extends BlockPlant {

    public static final BlockStateInteger AGE = BlockProperties.ag;
    private static final VoxelShape[] b = new VoxelShape[]{Block.a(0.0D, 0.0D, 0.0D, 16.0D, 5.0D, 16.0D), Block.a(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D), Block.a(0.0D, 0.0D, 0.0D, 16.0D, 11.0D, 16.0D), Block.a(0.0D, 0.0D, 0.0D, 16.0D, 14.0D, 16.0D)};

    protected BlockNetherWart(BlockBase.Info blockbase_info) {
        super(blockbase_info);
        this.j((IBlockData) ((IBlockData) this.blockStateList.getBlockData()).set(BlockNetherWart.AGE, 0));
    }

    @Override
    public VoxelShape b(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        return BlockNetherWart.b[(Integer) iblockdata.get(BlockNetherWart.AGE)];
    }

    @Override
    protected boolean c(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return iblockdata.a(Blocks.SOUL_SAND);
    }

    @Override
    public boolean isTicking(IBlockData iblockdata) {
        return (Integer) iblockdata.get(BlockNetherWart.AGE) < 3;
    }

    @Override
    public void tick(IBlockData iblockdata, WorldServer worldserver, BlockPosition blockposition, Random random) {
        int i = (Integer) iblockdata.get(BlockNetherWart.AGE);

        if (i < 3 && random.nextInt(10) == 0) {
            iblockdata = (IBlockData) iblockdata.set(BlockNetherWart.AGE, i + 1);
            worldserver.setTypeAndData(blockposition, iblockdata, 2);
        }

    }

    @Override
    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockNetherWart.AGE);
    }
}
