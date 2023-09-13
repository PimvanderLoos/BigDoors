package net.minecraft.world.level.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.server.level.WorldServer;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsBlock;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockActionContext;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.BlockStateList;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockProperties;
import net.minecraft.world.level.block.state.properties.BlockPropertyDoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.BlockStateBoolean;
import net.minecraft.world.level.block.state.properties.BlockStateDirection;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidType;
import net.minecraft.world.level.material.FluidTypes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.VoxelShapeCollision;

public class SmallDripleafBlock extends BlockTallPlant implements IBlockFragilePlantElement, IBlockWaterlogged {

    private static final BlockStateBoolean WATERLOGGED = BlockProperties.WATERLOGGED;
    public static final BlockStateDirection FACING = BlockProperties.HORIZONTAL_FACING;
    protected static final float AABB_OFFSET = 6.0F;
    protected static final VoxelShape SHAPE = Block.a(2.0D, 0.0D, 2.0D, 14.0D, 13.0D, 14.0D);

    public SmallDripleafBlock(BlockBase.Info blockbase_info) {
        super(blockbase_info);
        this.k((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) this.stateDefinition.getBlockData()).set(SmallDripleafBlock.HALF, BlockPropertyDoubleBlockHalf.LOWER)).set(SmallDripleafBlock.WATERLOGGED, false)).set(SmallDripleafBlock.FACING, EnumDirection.NORTH));
    }

    @Override
    public VoxelShape a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        return SmallDripleafBlock.SHAPE;
    }

    @Override
    protected boolean d(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return iblockdata.a((Tag) TagsBlock.SMALL_DRIPLEAF_PLACEABLE) || iblockaccess.getFluid(blockposition.up()).a((FluidType) FluidTypes.WATER) && super.d(iblockdata, iblockaccess, blockposition);
    }

    @Nullable
    @Override
    public IBlockData getPlacedState(BlockActionContext blockactioncontext) {
        IBlockData iblockdata = super.getPlacedState(blockactioncontext);

        return iblockdata != null ? a((IWorldReader) blockactioncontext.getWorld(), blockactioncontext.getClickPosition(), (IBlockData) iblockdata.set(SmallDripleafBlock.FACING, blockactioncontext.g().opposite())) : null;
    }

    @Override
    public void postPlace(World world, BlockPosition blockposition, IBlockData iblockdata, EntityLiving entityliving, ItemStack itemstack) {
        if (!world.isClientSide()) {
            BlockPosition blockposition1 = blockposition.up();
            IBlockData iblockdata1 = BlockTallPlant.a((IWorldReader) world, blockposition1, (IBlockData) ((IBlockData) this.getBlockData().set(SmallDripleafBlock.HALF, BlockPropertyDoubleBlockHalf.UPPER)).set(SmallDripleafBlock.FACING, (EnumDirection) iblockdata.get(SmallDripleafBlock.FACING)));

            world.setTypeAndData(blockposition1, iblockdata1, 3);
        }

    }

    @Override
    public Fluid c_(IBlockData iblockdata) {
        return (Boolean) iblockdata.get(SmallDripleafBlock.WATERLOGGED) ? FluidTypes.WATER.a(false) : super.c_(iblockdata);
    }

    @Override
    public boolean canPlace(IBlockData iblockdata, IWorldReader iworldreader, BlockPosition blockposition) {
        if (iblockdata.get(SmallDripleafBlock.HALF) == BlockPropertyDoubleBlockHalf.UPPER) {
            return super.canPlace(iblockdata, iworldreader, blockposition);
        } else {
            BlockPosition blockposition1 = blockposition.down();
            IBlockData iblockdata1 = iworldreader.getType(blockposition1);

            return this.d(iblockdata1, iworldreader, blockposition1);
        }
    }

    @Override
    public IBlockData updateState(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        if ((Boolean) iblockdata.get(SmallDripleafBlock.WATERLOGGED)) {
            generatoraccess.getFluidTickList().a(blockposition, FluidTypes.WATER, FluidTypes.WATER.a((IWorldReader) generatoraccess));
        }

        return super.updateState(iblockdata, enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1);
    }

    @Override
    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(SmallDripleafBlock.HALF, SmallDripleafBlock.WATERLOGGED, SmallDripleafBlock.FACING);
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
        BlockPosition blockposition1;

        if (iblockdata.get(BlockTallPlant.HALF) == BlockPropertyDoubleBlockHalf.LOWER) {
            blockposition1 = blockposition.up();
            worldserver.setTypeAndData(blockposition1, worldserver.getFluid(blockposition1).getBlockData(), 18);
            BigDripleafBlock.a((GeneratorAccess) worldserver, random, blockposition, (EnumDirection) iblockdata.get(SmallDripleafBlock.FACING));
        } else {
            blockposition1 = blockposition.down();
            this.a(worldserver, random, blockposition1, worldserver.getType(blockposition1));
        }

    }

    @Override
    public IBlockData a(IBlockData iblockdata, EnumBlockRotation enumblockrotation) {
        return (IBlockData) iblockdata.set(SmallDripleafBlock.FACING, enumblockrotation.a((EnumDirection) iblockdata.get(SmallDripleafBlock.FACING)));
    }

    @Override
    public IBlockData a(IBlockData iblockdata, EnumBlockMirror enumblockmirror) {
        return iblockdata.a(enumblockmirror.a((EnumDirection) iblockdata.get(SmallDripleafBlock.FACING)));
    }

    @Override
    public BlockBase.EnumRandomOffset S_() {
        return BlockBase.EnumRandomOffset.XYZ;
    }

    @Override
    public float X_() {
        return 0.1F;
    }
}
