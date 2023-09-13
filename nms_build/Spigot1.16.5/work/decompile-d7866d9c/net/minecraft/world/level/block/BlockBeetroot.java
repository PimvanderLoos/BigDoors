package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.BlockPosition;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.BlockStateList;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockProperties;
import net.minecraft.world.level.block.state.properties.BlockStateInteger;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.VoxelShapeCollision;

public class BlockBeetroot extends BlockCrops {

    public static final BlockStateInteger a = BlockProperties.ag;
    private static final VoxelShape[] c = new VoxelShape[]{Block.a(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D), Block.a(0.0D, 0.0D, 0.0D, 16.0D, 4.0D, 16.0D), Block.a(0.0D, 0.0D, 0.0D, 16.0D, 6.0D, 16.0D), Block.a(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D)};

    public BlockBeetroot(BlockBase.Info blockbase_info) {
        super(blockbase_info);
    }

    @Override
    public BlockStateInteger c() {
        return BlockBeetroot.a;
    }

    @Override
    public int d() {
        return 3;
    }

    @Override
    public void tick(IBlockData iblockdata, WorldServer worldserver, BlockPosition blockposition, Random random) {
        if (random.nextInt(3) != 0) {
            super.tick(iblockdata, worldserver, blockposition, random);
        }

    }

    @Override
    protected int a(World world) {
        return super.a(world) / 3;
    }

    @Override
    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockBeetroot.a);
    }

    @Override
    public VoxelShape b(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        return BlockBeetroot.c[(Integer) iblockdata.get(this.c())];
    }
}
