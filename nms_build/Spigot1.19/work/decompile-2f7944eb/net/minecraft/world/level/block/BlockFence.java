package net.minecraft.world.level.block;

import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.tags.TagsBlock;
import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.ItemLeash;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockActionContext;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.BlockStateList;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.IBlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidType;
import net.minecraft.world.level.material.FluidTypes;
import net.minecraft.world.level.pathfinder.PathMode;
import net.minecraft.world.phys.MovingObjectPositionBlock;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.VoxelShapeCollision;

public class BlockFence extends BlockTall {

    private final VoxelShape[] occlusionByIndex;

    public BlockFence(BlockBase.Info blockbase_info) {
        super(2.0F, 2.0F, 16.0F, 16.0F, 24.0F, blockbase_info);
        this.registerDefaultState((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) this.stateDefinition.any()).setValue(BlockFence.NORTH, false)).setValue(BlockFence.EAST, false)).setValue(BlockFence.SOUTH, false)).setValue(BlockFence.WEST, false)).setValue(BlockFence.WATERLOGGED, false));
        this.occlusionByIndex = this.makeShapes(2.0F, 1.0F, 16.0F, 6.0F, 15.0F);
    }

    @Override
    public VoxelShape getOcclusionShape(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return this.occlusionByIndex[this.getAABBIndex(iblockdata)];
    }

    @Override
    public VoxelShape getVisualShape(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        return this.getShape(iblockdata, iblockaccess, blockposition, voxelshapecollision);
    }

    @Override
    public boolean isPathfindable(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, PathMode pathmode) {
        return false;
    }

    public boolean connectsTo(IBlockData iblockdata, boolean flag, EnumDirection enumdirection) {
        Block block = iblockdata.getBlock();
        boolean flag1 = this.isSameFence(iblockdata);
        boolean flag2 = block instanceof BlockFenceGate && BlockFenceGate.connectsToDirection(iblockdata, enumdirection);

        return !isExceptionForConnection(iblockdata) && flag || flag1 || flag2;
    }

    private boolean isSameFence(IBlockData iblockdata) {
        return iblockdata.is(TagsBlock.FENCES) && iblockdata.is(TagsBlock.WOODEN_FENCES) == this.defaultBlockState().is(TagsBlock.WOODEN_FENCES);
    }

    @Override
    public EnumInteractionResult use(IBlockData iblockdata, World world, BlockPosition blockposition, EntityHuman entityhuman, EnumHand enumhand, MovingObjectPositionBlock movingobjectpositionblock) {
        if (world.isClientSide) {
            ItemStack itemstack = entityhuman.getItemInHand(enumhand);

            return itemstack.is(Items.LEAD) ? EnumInteractionResult.SUCCESS : EnumInteractionResult.PASS;
        } else {
            return ItemLeash.bindPlayerMobs(entityhuman, world, blockposition);
        }
    }

    @Override
    public IBlockData getStateForPlacement(BlockActionContext blockactioncontext) {
        World world = blockactioncontext.getLevel();
        BlockPosition blockposition = blockactioncontext.getClickedPos();
        Fluid fluid = blockactioncontext.getLevel().getFluidState(blockactioncontext.getClickedPos());
        BlockPosition blockposition1 = blockposition.north();
        BlockPosition blockposition2 = blockposition.east();
        BlockPosition blockposition3 = blockposition.south();
        BlockPosition blockposition4 = blockposition.west();
        IBlockData iblockdata = world.getBlockState(blockposition1);
        IBlockData iblockdata1 = world.getBlockState(blockposition2);
        IBlockData iblockdata2 = world.getBlockState(blockposition3);
        IBlockData iblockdata3 = world.getBlockState(blockposition4);

        return (IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) super.getStateForPlacement(blockactioncontext).setValue(BlockFence.NORTH, this.connectsTo(iblockdata, iblockdata.isFaceSturdy(world, blockposition1, EnumDirection.SOUTH), EnumDirection.SOUTH))).setValue(BlockFence.EAST, this.connectsTo(iblockdata1, iblockdata1.isFaceSturdy(world, blockposition2, EnumDirection.WEST), EnumDirection.WEST))).setValue(BlockFence.SOUTH, this.connectsTo(iblockdata2, iblockdata2.isFaceSturdy(world, blockposition3, EnumDirection.NORTH), EnumDirection.NORTH))).setValue(BlockFence.WEST, this.connectsTo(iblockdata3, iblockdata3.isFaceSturdy(world, blockposition4, EnumDirection.EAST), EnumDirection.EAST))).setValue(BlockFence.WATERLOGGED, fluid.getType() == FluidTypes.WATER);
    }

    @Override
    public IBlockData updateShape(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        if ((Boolean) iblockdata.getValue(BlockFence.WATERLOGGED)) {
            generatoraccess.scheduleTick(blockposition, (FluidType) FluidTypes.WATER, FluidTypes.WATER.getTickDelay(generatoraccess));
        }

        return enumdirection.getAxis().getPlane() == EnumDirection.EnumDirectionLimit.HORIZONTAL ? (IBlockData) iblockdata.setValue((IBlockState) BlockFence.PROPERTY_BY_DIRECTION.get(enumdirection), this.connectsTo(iblockdata1, iblockdata1.isFaceSturdy(generatoraccess, blockposition1, enumdirection.getOpposite()), enumdirection.getOpposite())) : super.updateShape(iblockdata, enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1);
    }

    @Override
    protected void createBlockStateDefinition(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.add(BlockFence.NORTH, BlockFence.EAST, BlockFence.WEST, BlockFence.SOUTH, BlockFence.WATERLOGGED);
    }
}
