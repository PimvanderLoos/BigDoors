package net.minecraft.world.level.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.util.MathHelper;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockActionContext;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.BlockStateList;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockProperties;
import net.minecraft.world.level.block.state.properties.BlockPropertyDoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.BlockStateEnum;

public class BlockTallPlant extends BlockPlant {

    public static final BlockStateEnum<BlockPropertyDoubleBlockHalf> HALF = BlockProperties.DOUBLE_BLOCK_HALF;

    public BlockTallPlant(BlockBase.Info blockbase_info) {
        super(blockbase_info);
        this.k((IBlockData) ((IBlockData) this.stateDefinition.getBlockData()).set(BlockTallPlant.HALF, BlockPropertyDoubleBlockHalf.LOWER));
    }

    @Override
    public IBlockData updateState(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        BlockPropertyDoubleBlockHalf blockpropertydoubleblockhalf = (BlockPropertyDoubleBlockHalf) iblockdata.get(BlockTallPlant.HALF);

        return enumdirection.n() == EnumDirection.EnumAxis.Y && blockpropertydoubleblockhalf == BlockPropertyDoubleBlockHalf.LOWER == (enumdirection == EnumDirection.UP) && (!iblockdata1.a((Block) this) || iblockdata1.get(BlockTallPlant.HALF) == blockpropertydoubleblockhalf) ? Blocks.AIR.getBlockData() : (blockpropertydoubleblockhalf == BlockPropertyDoubleBlockHalf.LOWER && enumdirection == EnumDirection.DOWN && !iblockdata.canPlace(generatoraccess, blockposition) ? Blocks.AIR.getBlockData() : super.updateState(iblockdata, enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1));
    }

    @Nullable
    @Override
    public IBlockData getPlacedState(BlockActionContext blockactioncontext) {
        BlockPosition blockposition = blockactioncontext.getClickPosition();
        World world = blockactioncontext.getWorld();

        return blockposition.getY() < world.getMaxBuildHeight() - 1 && world.getType(blockposition.up()).a(blockactioncontext) ? super.getPlacedState(blockactioncontext) : null;
    }

    @Override
    public void postPlace(World world, BlockPosition blockposition, IBlockData iblockdata, EntityLiving entityliving, ItemStack itemstack) {
        BlockPosition blockposition1 = blockposition.up();

        world.setTypeAndData(blockposition1, a((IWorldReader) world, blockposition1, (IBlockData) this.getBlockData().set(BlockTallPlant.HALF, BlockPropertyDoubleBlockHalf.UPPER)), 3);
    }

    @Override
    public boolean canPlace(IBlockData iblockdata, IWorldReader iworldreader, BlockPosition blockposition) {
        if (iblockdata.get(BlockTallPlant.HALF) != BlockPropertyDoubleBlockHalf.UPPER) {
            return super.canPlace(iblockdata, iworldreader, blockposition);
        } else {
            IBlockData iblockdata1 = iworldreader.getType(blockposition.down());

            return iblockdata1.a((Block) this) && iblockdata1.get(BlockTallPlant.HALF) == BlockPropertyDoubleBlockHalf.LOWER;
        }
    }

    public static void a(GeneratorAccess generatoraccess, IBlockData iblockdata, BlockPosition blockposition, int i) {
        BlockPosition blockposition1 = blockposition.up();

        generatoraccess.setTypeAndData(blockposition, a((IWorldReader) generatoraccess, blockposition, (IBlockData) iblockdata.set(BlockTallPlant.HALF, BlockPropertyDoubleBlockHalf.LOWER)), i);
        generatoraccess.setTypeAndData(blockposition1, a((IWorldReader) generatoraccess, blockposition1, (IBlockData) iblockdata.set(BlockTallPlant.HALF, BlockPropertyDoubleBlockHalf.UPPER)), i);
    }

    public static IBlockData a(IWorldReader iworldreader, BlockPosition blockposition, IBlockData iblockdata) {
        return iblockdata.b(BlockProperties.WATERLOGGED) ? (IBlockData) iblockdata.set(BlockProperties.WATERLOGGED, iworldreader.B(blockposition)) : iblockdata;
    }

    @Override
    public void a(World world, BlockPosition blockposition, IBlockData iblockdata, EntityHuman entityhuman) {
        if (!world.isClientSide) {
            if (entityhuman.isCreative()) {
                b(world, blockposition, iblockdata, entityhuman);
            } else {
                dropItems(iblockdata, world, blockposition, (TileEntity) null, entityhuman, entityhuman.getItemInMainHand());
            }
        }

        super.a(world, blockposition, iblockdata, entityhuman);
    }

    @Override
    public void a(World world, EntityHuman entityhuman, BlockPosition blockposition, IBlockData iblockdata, @Nullable TileEntity tileentity, ItemStack itemstack) {
        super.a(world, entityhuman, blockposition, Blocks.AIR.getBlockData(), tileentity, itemstack);
    }

    protected static void b(World world, BlockPosition blockposition, IBlockData iblockdata, EntityHuman entityhuman) {
        BlockPropertyDoubleBlockHalf blockpropertydoubleblockhalf = (BlockPropertyDoubleBlockHalf) iblockdata.get(BlockTallPlant.HALF);

        if (blockpropertydoubleblockhalf == BlockPropertyDoubleBlockHalf.UPPER) {
            BlockPosition blockposition1 = blockposition.down();
            IBlockData iblockdata1 = world.getType(blockposition1);

            if (iblockdata1.a(iblockdata.getBlock()) && iblockdata1.get(BlockTallPlant.HALF) == BlockPropertyDoubleBlockHalf.LOWER) {
                IBlockData iblockdata2 = iblockdata1.b(BlockProperties.WATERLOGGED) && (Boolean) iblockdata1.get(BlockProperties.WATERLOGGED) ? Blocks.WATER.getBlockData() : Blocks.AIR.getBlockData();

                world.setTypeAndData(blockposition1, iblockdata2, 35);
                world.a(entityhuman, 2001, blockposition1, Block.getCombinedId(iblockdata1));
            }
        }

    }

    @Override
    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockTallPlant.HALF);
    }

    @Override
    public BlockBase.EnumRandomOffset S_() {
        return BlockBase.EnumRandomOffset.XZ;
    }

    @Override
    public long a(IBlockData iblockdata, BlockPosition blockposition) {
        return MathHelper.c(blockposition.getX(), blockposition.down(iblockdata.get(BlockTallPlant.HALF) == BlockPropertyDoubleBlockHalf.LOWER ? 0 : 1).getY(), blockposition.getZ());
    }
}
