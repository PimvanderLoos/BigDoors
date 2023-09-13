package net.minecraft.world.level.block;

import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsBlock;
import net.minecraft.world.item.context.BlockActionContext;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockProperties;
import net.minecraft.world.level.block.state.properties.BlockPropertyTrackPosition;
import net.minecraft.world.level.block.state.properties.BlockStateBoolean;
import net.minecraft.world.level.block.state.properties.IBlockState;
import net.minecraft.world.level.material.EnumPistonReaction;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidTypes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.VoxelShapeCollision;

public abstract class BlockMinecartTrackAbstract extends Block implements IBlockWaterlogged {

    protected static final VoxelShape FLAT_AABB = Block.a(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D);
    protected static final VoxelShape HALF_BLOCK_AABB = Block.a(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D);
    public static final BlockStateBoolean WATERLOGGED = BlockProperties.WATERLOGGED;
    private final boolean isStraight;

    public static boolean a(World world, BlockPosition blockposition) {
        return g(world.getType(blockposition));
    }

    public static boolean g(IBlockData iblockdata) {
        return iblockdata.a((Tag) TagsBlock.RAILS) && iblockdata.getBlock() instanceof BlockMinecartTrackAbstract;
    }

    protected BlockMinecartTrackAbstract(boolean flag, BlockBase.Info blockbase_info) {
        super(blockbase_info);
        this.isStraight = flag;
    }

    public boolean c() {
        return this.isStraight;
    }

    @Override
    public VoxelShape a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        BlockPropertyTrackPosition blockpropertytrackposition = iblockdata.a((Block) this) ? (BlockPropertyTrackPosition) iblockdata.get(this.d()) : null;

        return blockpropertytrackposition != null && blockpropertytrackposition.b() ? BlockMinecartTrackAbstract.HALF_BLOCK_AABB : BlockMinecartTrackAbstract.FLAT_AABB;
    }

    @Override
    public boolean canPlace(IBlockData iblockdata, IWorldReader iworldreader, BlockPosition blockposition) {
        return c(iworldreader, blockposition.down());
    }

    @Override
    public void onPlace(IBlockData iblockdata, World world, BlockPosition blockposition, IBlockData iblockdata1, boolean flag) {
        if (!iblockdata1.a(iblockdata.getBlock())) {
            this.a(iblockdata, world, blockposition, flag);
        }
    }

    protected IBlockData a(IBlockData iblockdata, World world, BlockPosition blockposition, boolean flag) {
        iblockdata = this.a(world, blockposition, iblockdata, true);
        if (this.isStraight) {
            iblockdata.doPhysics(world, blockposition, this, blockposition, flag);
        }

        return iblockdata;
    }

    @Override
    public void doPhysics(IBlockData iblockdata, World world, BlockPosition blockposition, Block block, BlockPosition blockposition1, boolean flag) {
        if (!world.isClientSide && world.getType(blockposition).a((Block) this)) {
            BlockPropertyTrackPosition blockpropertytrackposition = (BlockPropertyTrackPosition) iblockdata.get(this.d());

            if (a(blockposition, world, blockpropertytrackposition)) {
                c(iblockdata, world, blockposition);
                world.a(blockposition, flag);
            } else {
                this.a(iblockdata, world, blockposition, block);
            }

        }
    }

    private static boolean a(BlockPosition blockposition, World world, BlockPropertyTrackPosition blockpropertytrackposition) {
        if (!c(world, blockposition.down())) {
            return true;
        } else {
            switch (blockpropertytrackposition) {
                case ASCENDING_EAST:
                    return !c(world, blockposition.east());
                case ASCENDING_WEST:
                    return !c(world, blockposition.west());
                case ASCENDING_NORTH:
                    return !c(world, blockposition.north());
                case ASCENDING_SOUTH:
                    return !c(world, blockposition.south());
                default:
                    return false;
            }
        }
    }

    protected void a(IBlockData iblockdata, World world, BlockPosition blockposition, Block block) {}

    protected IBlockData a(World world, BlockPosition blockposition, IBlockData iblockdata, boolean flag) {
        if (world.isClientSide) {
            return iblockdata;
        } else {
            BlockPropertyTrackPosition blockpropertytrackposition = (BlockPropertyTrackPosition) iblockdata.get(this.d());

            return (new MinecartTrackLogic(world, blockposition, iblockdata)).a(world.isBlockIndirectlyPowered(blockposition), flag, blockpropertytrackposition).c();
        }
    }

    @Override
    public EnumPistonReaction getPushReaction(IBlockData iblockdata) {
        return EnumPistonReaction.NORMAL;
    }

    @Override
    public void remove(IBlockData iblockdata, World world, BlockPosition blockposition, IBlockData iblockdata1, boolean flag) {
        if (!flag) {
            super.remove(iblockdata, world, blockposition, iblockdata1, flag);
            if (((BlockPropertyTrackPosition) iblockdata.get(this.d())).b()) {
                world.applyPhysics(blockposition.up(), this);
            }

            if (this.isStraight) {
                world.applyPhysics(blockposition, this);
                world.applyPhysics(blockposition.down(), this);
            }

        }
    }

    @Override
    public IBlockData getPlacedState(BlockActionContext blockactioncontext) {
        Fluid fluid = blockactioncontext.getWorld().getFluid(blockactioncontext.getClickPosition());
        boolean flag = fluid.getType() == FluidTypes.WATER;
        IBlockData iblockdata = super.getBlockData();
        EnumDirection enumdirection = blockactioncontext.g();
        boolean flag1 = enumdirection == EnumDirection.EAST || enumdirection == EnumDirection.WEST;

        return (IBlockData) ((IBlockData) iblockdata.set(this.d(), flag1 ? BlockPropertyTrackPosition.EAST_WEST : BlockPropertyTrackPosition.NORTH_SOUTH)).set(BlockMinecartTrackAbstract.WATERLOGGED, flag);
    }

    public abstract IBlockState<BlockPropertyTrackPosition> d();

    @Override
    public IBlockData updateState(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        if ((Boolean) iblockdata.get(BlockMinecartTrackAbstract.WATERLOGGED)) {
            generatoraccess.getFluidTickList().a(blockposition, FluidTypes.WATER, FluidTypes.WATER.a((IWorldReader) generatoraccess));
        }

        return super.updateState(iblockdata, enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1);
    }

    @Override
    public Fluid c_(IBlockData iblockdata) {
        return (Boolean) iblockdata.get(BlockMinecartTrackAbstract.WATERLOGGED) ? FluidTypes.WATER.a(false) : super.c_(iblockdata);
    }
}
