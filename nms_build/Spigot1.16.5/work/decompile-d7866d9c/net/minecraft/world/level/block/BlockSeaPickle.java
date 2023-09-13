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
import net.minecraft.world.level.block.state.properties.BlockStateBoolean;
import net.minecraft.world.level.block.state.properties.BlockStateInteger;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidTypes;
import net.minecraft.world.level.pathfinder.PathMode;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.VoxelShapeCollision;

public class BlockSeaPickle extends BlockPlant implements IBlockFragilePlantElement, IBlockWaterlogged {

    public static final BlockStateInteger a = BlockProperties.ay;
    public static final BlockStateBoolean b = BlockProperties.C;
    protected static final VoxelShape c = Block.a(6.0D, 0.0D, 6.0D, 10.0D, 6.0D, 10.0D);
    protected static final VoxelShape d = Block.a(3.0D, 0.0D, 3.0D, 13.0D, 6.0D, 13.0D);
    protected static final VoxelShape e = Block.a(2.0D, 0.0D, 2.0D, 14.0D, 6.0D, 14.0D);
    protected static final VoxelShape f = Block.a(2.0D, 0.0D, 2.0D, 14.0D, 7.0D, 14.0D);

    protected BlockSeaPickle(BlockBase.Info blockbase_info) {
        super(blockbase_info);
        this.j((IBlockData) ((IBlockData) ((IBlockData) this.blockStateList.getBlockData()).set(BlockSeaPickle.a, 1)).set(BlockSeaPickle.b, true));
    }

    @Nullable
    @Override
    public IBlockData getPlacedState(BlockActionContext blockactioncontext) {
        IBlockData iblockdata = blockactioncontext.getWorld().getType(blockactioncontext.getClickPosition());

        if (iblockdata.a((Block) this)) {
            return (IBlockData) iblockdata.set(BlockSeaPickle.a, Math.min(4, (Integer) iblockdata.get(BlockSeaPickle.a) + 1));
        } else {
            Fluid fluid = blockactioncontext.getWorld().getFluid(blockactioncontext.getClickPosition());
            boolean flag = fluid.getType() == FluidTypes.WATER;

            return (IBlockData) super.getPlacedState(blockactioncontext).set(BlockSeaPickle.b, flag);
        }
    }

    public static boolean h(IBlockData iblockdata) {
        return !(Boolean) iblockdata.get(BlockSeaPickle.b);
    }

    @Override
    protected boolean c(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return !iblockdata.getCollisionShape(iblockaccess, blockposition).a(EnumDirection.UP).isEmpty() || iblockdata.d(iblockaccess, blockposition, EnumDirection.UP);
    }

    @Override
    public boolean canPlace(IBlockData iblockdata, IWorldReader iworldreader, BlockPosition blockposition) {
        BlockPosition blockposition1 = blockposition.down();

        return this.c(iworldreader.getType(blockposition1), (IBlockAccess) iworldreader, blockposition1);
    }

    @Override
    public IBlockData updateState(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        if (!iblockdata.canPlace(generatoraccess, blockposition)) {
            return Blocks.AIR.getBlockData();
        } else {
            if ((Boolean) iblockdata.get(BlockSeaPickle.b)) {
                generatoraccess.getFluidTickList().a(blockposition, FluidTypes.WATER, FluidTypes.WATER.a((IWorldReader) generatoraccess));
            }

            return super.updateState(iblockdata, enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1);
        }
    }

    @Override
    public boolean a(IBlockData iblockdata, BlockActionContext blockactioncontext) {
        return blockactioncontext.getItemStack().getItem() == this.getItem() && (Integer) iblockdata.get(BlockSeaPickle.a) < 4 ? true : super.a(iblockdata, blockactioncontext);
    }

    @Override
    public VoxelShape b(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        switch ((Integer) iblockdata.get(BlockSeaPickle.a)) {
            case 1:
            default:
                return BlockSeaPickle.c;
            case 2:
                return BlockSeaPickle.d;
            case 3:
                return BlockSeaPickle.e;
            case 4:
                return BlockSeaPickle.f;
        }
    }

    @Override
    public Fluid d(IBlockData iblockdata) {
        return (Boolean) iblockdata.get(BlockSeaPickle.b) ? FluidTypes.WATER.a(false) : super.d(iblockdata);
    }

    @Override
    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockSeaPickle.a, BlockSeaPickle.b);
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
        if (!h(iblockdata) && worldserver.getType(blockposition.down()).a((Tag) TagsBlock.CORAL_BLOCKS)) {
            boolean flag = true;
            int i = 1;
            boolean flag1 = true;
            int j = 0;
            int k = blockposition.getX() - 2;
            int l = 0;

            for (int i1 = 0; i1 < 5; ++i1) {
                for (int j1 = 0; j1 < i; ++j1) {
                    int k1 = 2 + blockposition.getY() - 1;

                    for (int l1 = k1 - 2; l1 < k1; ++l1) {
                        BlockPosition blockposition1 = new BlockPosition(k + i1, l1, blockposition.getZ() - l + j1);

                        if (blockposition1 != blockposition && random.nextInt(6) == 0 && worldserver.getType(blockposition1).a(Blocks.WATER)) {
                            IBlockData iblockdata1 = worldserver.getType(blockposition1.down());

                            if (iblockdata1.a((Tag) TagsBlock.CORAL_BLOCKS)) {
                                worldserver.setTypeAndData(blockposition1, (IBlockData) Blocks.SEA_PICKLE.getBlockData().set(BlockSeaPickle.a, random.nextInt(4) + 1), 3);
                            }
                        }
                    }
                }

                if (j < 2) {
                    i += 2;
                    ++l;
                } else {
                    i -= 2;
                    --l;
                }

                ++j;
            }

            worldserver.setTypeAndData(blockposition, (IBlockData) iblockdata.set(BlockSeaPickle.a, 4), 2);
        }

    }

    @Override
    public boolean a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, PathMode pathmode) {
        return false;
    }
}
