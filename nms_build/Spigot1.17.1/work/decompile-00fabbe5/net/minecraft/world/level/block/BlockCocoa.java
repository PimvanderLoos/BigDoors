package net.minecraft.world.level.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.server.level.WorldServer;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsBlock;
import net.minecraft.world.item.context.BlockActionContext;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.BlockStateList;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockProperties;
import net.minecraft.world.level.block.state.properties.BlockStateInteger;
import net.minecraft.world.level.pathfinder.PathMode;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.VoxelShapeCollision;

public class BlockCocoa extends BlockFacingHorizontal implements IBlockFragilePlantElement {

    public static final int MAX_AGE = 2;
    public static final BlockStateInteger AGE = BlockProperties.AGE_2;
    protected static final int AGE_0_WIDTH = 4;
    protected static final int AGE_0_HEIGHT = 5;
    protected static final int AGE_0_HALFWIDTH = 2;
    protected static final int AGE_1_WIDTH = 6;
    protected static final int AGE_1_HEIGHT = 7;
    protected static final int AGE_1_HALFWIDTH = 3;
    protected static final int AGE_2_WIDTH = 8;
    protected static final int AGE_2_HEIGHT = 9;
    protected static final int AGE_2_HALFWIDTH = 4;
    protected static final VoxelShape[] EAST_AABB = new VoxelShape[]{Block.a(11.0D, 7.0D, 6.0D, 15.0D, 12.0D, 10.0D), Block.a(9.0D, 5.0D, 5.0D, 15.0D, 12.0D, 11.0D), Block.a(7.0D, 3.0D, 4.0D, 15.0D, 12.0D, 12.0D)};
    protected static final VoxelShape[] WEST_AABB = new VoxelShape[]{Block.a(1.0D, 7.0D, 6.0D, 5.0D, 12.0D, 10.0D), Block.a(1.0D, 5.0D, 5.0D, 7.0D, 12.0D, 11.0D), Block.a(1.0D, 3.0D, 4.0D, 9.0D, 12.0D, 12.0D)};
    protected static final VoxelShape[] NORTH_AABB = new VoxelShape[]{Block.a(6.0D, 7.0D, 1.0D, 10.0D, 12.0D, 5.0D), Block.a(5.0D, 5.0D, 1.0D, 11.0D, 12.0D, 7.0D), Block.a(4.0D, 3.0D, 1.0D, 12.0D, 12.0D, 9.0D)};
    protected static final VoxelShape[] SOUTH_AABB = new VoxelShape[]{Block.a(6.0D, 7.0D, 11.0D, 10.0D, 12.0D, 15.0D), Block.a(5.0D, 5.0D, 9.0D, 11.0D, 12.0D, 15.0D), Block.a(4.0D, 3.0D, 7.0D, 12.0D, 12.0D, 15.0D)};

    public BlockCocoa(BlockBase.Info blockbase_info) {
        super(blockbase_info);
        this.k((IBlockData) ((IBlockData) ((IBlockData) this.stateDefinition.getBlockData()).set(BlockCocoa.FACING, EnumDirection.NORTH)).set(BlockCocoa.AGE, 0));
    }

    @Override
    public boolean isTicking(IBlockData iblockdata) {
        return (Integer) iblockdata.get(BlockCocoa.AGE) < 2;
    }

    @Override
    public void tick(IBlockData iblockdata, WorldServer worldserver, BlockPosition blockposition, Random random) {
        if (worldserver.random.nextInt(5) == 0) {
            int i = (Integer) iblockdata.get(BlockCocoa.AGE);

            if (i < 2) {
                worldserver.setTypeAndData(blockposition, (IBlockData) iblockdata.set(BlockCocoa.AGE, i + 1), 2);
            }
        }

    }

    @Override
    public boolean canPlace(IBlockData iblockdata, IWorldReader iworldreader, BlockPosition blockposition) {
        IBlockData iblockdata1 = iworldreader.getType(blockposition.shift((EnumDirection) iblockdata.get(BlockCocoa.FACING)));

        return iblockdata1.a((Tag) TagsBlock.JUNGLE_LOGS);
    }

    @Override
    public VoxelShape a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        int i = (Integer) iblockdata.get(BlockCocoa.AGE);

        switch ((EnumDirection) iblockdata.get(BlockCocoa.FACING)) {
            case SOUTH:
                return BlockCocoa.SOUTH_AABB[i];
            case NORTH:
            default:
                return BlockCocoa.NORTH_AABB[i];
            case WEST:
                return BlockCocoa.WEST_AABB[i];
            case EAST:
                return BlockCocoa.EAST_AABB[i];
        }
    }

    @Nullable
    @Override
    public IBlockData getPlacedState(BlockActionContext blockactioncontext) {
        IBlockData iblockdata = this.getBlockData();
        World world = blockactioncontext.getWorld();
        BlockPosition blockposition = blockactioncontext.getClickPosition();
        EnumDirection[] aenumdirection = blockactioncontext.f();
        int i = aenumdirection.length;

        for (int j = 0; j < i; ++j) {
            EnumDirection enumdirection = aenumdirection[j];

            if (enumdirection.n().d()) {
                iblockdata = (IBlockData) iblockdata.set(BlockCocoa.FACING, enumdirection);
                if (iblockdata.canPlace(world, blockposition)) {
                    return iblockdata;
                }
            }
        }

        return null;
    }

    @Override
    public IBlockData updateState(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        return enumdirection == iblockdata.get(BlockCocoa.FACING) && !iblockdata.canPlace(generatoraccess, blockposition) ? Blocks.AIR.getBlockData() : super.updateState(iblockdata, enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1);
    }

    @Override
    public boolean a(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata, boolean flag) {
        return (Integer) iblockdata.get(BlockCocoa.AGE) < 2;
    }

    @Override
    public boolean a(World world, Random random, BlockPosition blockposition, IBlockData iblockdata) {
        return true;
    }

    @Override
    public void a(WorldServer worldserver, Random random, BlockPosition blockposition, IBlockData iblockdata) {
        worldserver.setTypeAndData(blockposition, (IBlockData) iblockdata.set(BlockCocoa.AGE, (Integer) iblockdata.get(BlockCocoa.AGE) + 1), 2);
    }

    @Override
    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockCocoa.FACING, BlockCocoa.AGE);
    }

    @Override
    public boolean a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, PathMode pathmode) {
        return false;
    }
}
