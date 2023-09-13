package net.minecraft.world.level.block;

import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.tags.Tag;
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
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.BlockStateList;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.IBlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidTypes;
import net.minecraft.world.level.pathfinder.PathMode;
import net.minecraft.world.phys.MovingObjectPositionBlock;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.VoxelShapeCollision;

public class BlockFence extends BlockTall {

    private final VoxelShape[] occlusionByIndex;

    public BlockFence(BlockBase.Info blockbase_info) {
        super(2.0F, 2.0F, 16.0F, 16.0F, 24.0F, blockbase_info);
        this.k((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) this.stateDefinition.getBlockData()).set(BlockFence.NORTH, false)).set(BlockFence.EAST, false)).set(BlockFence.SOUTH, false)).set(BlockFence.WEST, false)).set(BlockFence.WATERLOGGED, false));
        this.occlusionByIndex = this.a(2.0F, 1.0F, 16.0F, 6.0F, 15.0F);
    }

    @Override
    public VoxelShape b_(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return this.occlusionByIndex[this.g(iblockdata)];
    }

    @Override
    public VoxelShape b(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        return this.a(iblockdata, iblockaccess, blockposition, voxelshapecollision);
    }

    @Override
    public boolean a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, PathMode pathmode) {
        return false;
    }

    public boolean a(IBlockData iblockdata, boolean flag, EnumDirection enumdirection) {
        Block block = iblockdata.getBlock();
        boolean flag1 = this.h(iblockdata);
        boolean flag2 = block instanceof BlockFenceGate && BlockFenceGate.a(iblockdata, enumdirection);

        return !j(iblockdata) && flag || flag1 || flag2;
    }

    private boolean h(IBlockData iblockdata) {
        return iblockdata.a((Tag) TagsBlock.FENCES) && iblockdata.a((Tag) TagsBlock.WOODEN_FENCES) == this.getBlockData().a((Tag) TagsBlock.WOODEN_FENCES);
    }

    @Override
    public EnumInteractionResult interact(IBlockData iblockdata, World world, BlockPosition blockposition, EntityHuman entityhuman, EnumHand enumhand, MovingObjectPositionBlock movingobjectpositionblock) {
        if (world.isClientSide) {
            ItemStack itemstack = entityhuman.b(enumhand);

            return itemstack.a(Items.LEAD) ? EnumInteractionResult.SUCCESS : EnumInteractionResult.PASS;
        } else {
            return ItemLeash.a(entityhuman, world, blockposition);
        }
    }

    @Override
    public IBlockData getPlacedState(BlockActionContext blockactioncontext) {
        World world = blockactioncontext.getWorld();
        BlockPosition blockposition = blockactioncontext.getClickPosition();
        Fluid fluid = blockactioncontext.getWorld().getFluid(blockactioncontext.getClickPosition());
        BlockPosition blockposition1 = blockposition.north();
        BlockPosition blockposition2 = blockposition.east();
        BlockPosition blockposition3 = blockposition.south();
        BlockPosition blockposition4 = blockposition.west();
        IBlockData iblockdata = world.getType(blockposition1);
        IBlockData iblockdata1 = world.getType(blockposition2);
        IBlockData iblockdata2 = world.getType(blockposition3);
        IBlockData iblockdata3 = world.getType(blockposition4);

        return (IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) super.getPlacedState(blockactioncontext).set(BlockFence.NORTH, this.a(iblockdata, iblockdata.d(world, blockposition1, EnumDirection.SOUTH), EnumDirection.SOUTH))).set(BlockFence.EAST, this.a(iblockdata1, iblockdata1.d(world, blockposition2, EnumDirection.WEST), EnumDirection.WEST))).set(BlockFence.SOUTH, this.a(iblockdata2, iblockdata2.d(world, blockposition3, EnumDirection.NORTH), EnumDirection.NORTH))).set(BlockFence.WEST, this.a(iblockdata3, iblockdata3.d(world, blockposition4, EnumDirection.EAST), EnumDirection.EAST))).set(BlockFence.WATERLOGGED, fluid.getType() == FluidTypes.WATER);
    }

    @Override
    public IBlockData updateState(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        if ((Boolean) iblockdata.get(BlockFence.WATERLOGGED)) {
            generatoraccess.getFluidTickList().a(blockposition, FluidTypes.WATER, FluidTypes.WATER.a((IWorldReader) generatoraccess));
        }

        return enumdirection.n().e() == EnumDirection.EnumDirectionLimit.HORIZONTAL ? (IBlockData) iblockdata.set((IBlockState) BlockFence.PROPERTY_BY_DIRECTION.get(enumdirection), this.a(iblockdata1, iblockdata1.d(generatoraccess, blockposition1, enumdirection.opposite()), enumdirection.opposite())) : super.updateState(iblockdata, enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1);
    }

    @Override
    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockFence.NORTH, BlockFence.EAST, BlockFence.WEST, BlockFence.SOUTH, BlockFence.WATERLOGGED);
    }
}
