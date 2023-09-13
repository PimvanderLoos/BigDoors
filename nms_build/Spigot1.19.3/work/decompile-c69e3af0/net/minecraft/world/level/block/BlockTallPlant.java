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
import net.minecraft.world.level.material.FluidType;
import net.minecraft.world.level.material.FluidTypes;

public class BlockTallPlant extends BlockPlant {

    public static final BlockStateEnum<BlockPropertyDoubleBlockHalf> HALF = BlockProperties.DOUBLE_BLOCK_HALF;

    public BlockTallPlant(BlockBase.Info blockbase_info) {
        super(blockbase_info);
        this.registerDefaultState((IBlockData) ((IBlockData) this.stateDefinition.any()).setValue(BlockTallPlant.HALF, BlockPropertyDoubleBlockHalf.LOWER));
    }

    @Override
    public IBlockData updateShape(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        BlockPropertyDoubleBlockHalf blockpropertydoubleblockhalf = (BlockPropertyDoubleBlockHalf) iblockdata.getValue(BlockTallPlant.HALF);

        return enumdirection.getAxis() == EnumDirection.EnumAxis.Y && blockpropertydoubleblockhalf == BlockPropertyDoubleBlockHalf.LOWER == (enumdirection == EnumDirection.UP) && (!iblockdata1.is((Block) this) || iblockdata1.getValue(BlockTallPlant.HALF) == blockpropertydoubleblockhalf) ? Blocks.AIR.defaultBlockState() : (blockpropertydoubleblockhalf == BlockPropertyDoubleBlockHalf.LOWER && enumdirection == EnumDirection.DOWN && !iblockdata.canSurvive(generatoraccess, blockposition) ? Blocks.AIR.defaultBlockState() : super.updateShape(iblockdata, enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1));
    }

    @Nullable
    @Override
    public IBlockData getStateForPlacement(BlockActionContext blockactioncontext) {
        BlockPosition blockposition = blockactioncontext.getClickedPos();
        World world = blockactioncontext.getLevel();

        return blockposition.getY() < world.getMaxBuildHeight() - 1 && world.getBlockState(blockposition.above()).canBeReplaced(blockactioncontext) ? super.getStateForPlacement(blockactioncontext) : null;
    }

    @Override
    public void setPlacedBy(World world, BlockPosition blockposition, IBlockData iblockdata, EntityLiving entityliving, ItemStack itemstack) {
        BlockPosition blockposition1 = blockposition.above();

        world.setBlock(blockposition1, copyWaterloggedFrom(world, blockposition1, (IBlockData) this.defaultBlockState().setValue(BlockTallPlant.HALF, BlockPropertyDoubleBlockHalf.UPPER)), 3);
    }

    @Override
    public boolean canSurvive(IBlockData iblockdata, IWorldReader iworldreader, BlockPosition blockposition) {
        if (iblockdata.getValue(BlockTallPlant.HALF) != BlockPropertyDoubleBlockHalf.UPPER) {
            return super.canSurvive(iblockdata, iworldreader, blockposition);
        } else {
            IBlockData iblockdata1 = iworldreader.getBlockState(blockposition.below());

            return iblockdata1.is((Block) this) && iblockdata1.getValue(BlockTallPlant.HALF) == BlockPropertyDoubleBlockHalf.LOWER;
        }
    }

    public static void placeAt(GeneratorAccess generatoraccess, IBlockData iblockdata, BlockPosition blockposition, int i) {
        BlockPosition blockposition1 = blockposition.above();

        generatoraccess.setBlock(blockposition, copyWaterloggedFrom(generatoraccess, blockposition, (IBlockData) iblockdata.setValue(BlockTallPlant.HALF, BlockPropertyDoubleBlockHalf.LOWER)), i);
        generatoraccess.setBlock(blockposition1, copyWaterloggedFrom(generatoraccess, blockposition1, (IBlockData) iblockdata.setValue(BlockTallPlant.HALF, BlockPropertyDoubleBlockHalf.UPPER)), i);
    }

    public static IBlockData copyWaterloggedFrom(IWorldReader iworldreader, BlockPosition blockposition, IBlockData iblockdata) {
        return iblockdata.hasProperty(BlockProperties.WATERLOGGED) ? (IBlockData) iblockdata.setValue(BlockProperties.WATERLOGGED, iworldreader.isWaterAt(blockposition)) : iblockdata;
    }

    @Override
    public void playerWillDestroy(World world, BlockPosition blockposition, IBlockData iblockdata, EntityHuman entityhuman) {
        if (!world.isClientSide) {
            if (entityhuman.isCreative()) {
                preventCreativeDropFromBottomPart(world, blockposition, iblockdata, entityhuman);
            } else {
                dropResources(iblockdata, world, blockposition, (TileEntity) null, entityhuman, entityhuman.getMainHandItem());
            }
        }

        super.playerWillDestroy(world, blockposition, iblockdata, entityhuman);
    }

    @Override
    public void playerDestroy(World world, EntityHuman entityhuman, BlockPosition blockposition, IBlockData iblockdata, @Nullable TileEntity tileentity, ItemStack itemstack) {
        super.playerDestroy(world, entityhuman, blockposition, Blocks.AIR.defaultBlockState(), tileentity, itemstack);
    }

    protected static void preventCreativeDropFromBottomPart(World world, BlockPosition blockposition, IBlockData iblockdata, EntityHuman entityhuman) {
        BlockPropertyDoubleBlockHalf blockpropertydoubleblockhalf = (BlockPropertyDoubleBlockHalf) iblockdata.getValue(BlockTallPlant.HALF);

        if (blockpropertydoubleblockhalf == BlockPropertyDoubleBlockHalf.UPPER) {
            BlockPosition blockposition1 = blockposition.below();
            IBlockData iblockdata1 = world.getBlockState(blockposition1);

            if (iblockdata1.is(iblockdata.getBlock()) && iblockdata1.getValue(BlockTallPlant.HALF) == BlockPropertyDoubleBlockHalf.LOWER) {
                IBlockData iblockdata2 = iblockdata1.getFluidState().is((FluidType) FluidTypes.WATER) ? Blocks.WATER.defaultBlockState() : Blocks.AIR.defaultBlockState();

                world.setBlock(blockposition1, iblockdata2, 35);
                world.levelEvent(entityhuman, 2001, blockposition1, Block.getId(iblockdata1));
            }
        }

    }

    @Override
    protected void createBlockStateDefinition(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.add(BlockTallPlant.HALF);
    }

    @Override
    public long getSeed(IBlockData iblockdata, BlockPosition blockposition) {
        return MathHelper.getSeed(blockposition.getX(), blockposition.below(iblockdata.getValue(BlockTallPlant.HALF) == BlockPropertyDoubleBlockHalf.LOWER ? 0 : 1).getY(), blockposition.getZ());
    }
}
