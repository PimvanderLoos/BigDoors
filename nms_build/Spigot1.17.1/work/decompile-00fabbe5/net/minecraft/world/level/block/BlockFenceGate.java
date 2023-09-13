package net.minecraft.world.level.block;

import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsBlock;
import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.context.BlockActionContext;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.BlockStateList;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockProperties;
import net.minecraft.world.level.block.state.properties.BlockStateBoolean;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.PathMode;
import net.minecraft.world.phys.MovingObjectPositionBlock;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.VoxelShapeCollision;
import net.minecraft.world.phys.shapes.VoxelShapes;

public class BlockFenceGate extends BlockFacingHorizontal {

    public static final BlockStateBoolean OPEN = BlockProperties.OPEN;
    public static final BlockStateBoolean POWERED = BlockProperties.POWERED;
    public static final BlockStateBoolean IN_WALL = BlockProperties.IN_WALL;
    protected static final VoxelShape Z_SHAPE = Block.a(0.0D, 0.0D, 6.0D, 16.0D, 16.0D, 10.0D);
    protected static final VoxelShape X_SHAPE = Block.a(6.0D, 0.0D, 0.0D, 10.0D, 16.0D, 16.0D);
    protected static final VoxelShape Z_SHAPE_LOW = Block.a(0.0D, 0.0D, 6.0D, 16.0D, 13.0D, 10.0D);
    protected static final VoxelShape X_SHAPE_LOW = Block.a(6.0D, 0.0D, 0.0D, 10.0D, 13.0D, 16.0D);
    protected static final VoxelShape Z_COLLISION_SHAPE = Block.a(0.0D, 0.0D, 6.0D, 16.0D, 24.0D, 10.0D);
    protected static final VoxelShape X_COLLISION_SHAPE = Block.a(6.0D, 0.0D, 0.0D, 10.0D, 24.0D, 16.0D);
    protected static final VoxelShape Z_OCCLUSION_SHAPE = VoxelShapes.a(Block.a(0.0D, 5.0D, 7.0D, 2.0D, 16.0D, 9.0D), Block.a(14.0D, 5.0D, 7.0D, 16.0D, 16.0D, 9.0D));
    protected static final VoxelShape X_OCCLUSION_SHAPE = VoxelShapes.a(Block.a(7.0D, 5.0D, 0.0D, 9.0D, 16.0D, 2.0D), Block.a(7.0D, 5.0D, 14.0D, 9.0D, 16.0D, 16.0D));
    protected static final VoxelShape Z_OCCLUSION_SHAPE_LOW = VoxelShapes.a(Block.a(0.0D, 2.0D, 7.0D, 2.0D, 13.0D, 9.0D), Block.a(14.0D, 2.0D, 7.0D, 16.0D, 13.0D, 9.0D));
    protected static final VoxelShape X_OCCLUSION_SHAPE_LOW = VoxelShapes.a(Block.a(7.0D, 2.0D, 0.0D, 9.0D, 13.0D, 2.0D), Block.a(7.0D, 2.0D, 14.0D, 9.0D, 13.0D, 16.0D));

    public BlockFenceGate(BlockBase.Info blockbase_info) {
        super(blockbase_info);
        this.k((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) this.stateDefinition.getBlockData()).set(BlockFenceGate.OPEN, false)).set(BlockFenceGate.POWERED, false)).set(BlockFenceGate.IN_WALL, false));
    }

    @Override
    public VoxelShape a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        return (Boolean) iblockdata.get(BlockFenceGate.IN_WALL) ? (((EnumDirection) iblockdata.get(BlockFenceGate.FACING)).n() == EnumDirection.EnumAxis.X ? BlockFenceGate.X_SHAPE_LOW : BlockFenceGate.Z_SHAPE_LOW) : (((EnumDirection) iblockdata.get(BlockFenceGate.FACING)).n() == EnumDirection.EnumAxis.X ? BlockFenceGate.X_SHAPE : BlockFenceGate.Z_SHAPE);
    }

    @Override
    public IBlockData updateState(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        EnumDirection.EnumAxis enumdirection_enumaxis = enumdirection.n();

        if (((EnumDirection) iblockdata.get(BlockFenceGate.FACING)).g().n() != enumdirection_enumaxis) {
            return super.updateState(iblockdata, enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1);
        } else {
            boolean flag = this.h(iblockdata1) || this.h(generatoraccess.getType(blockposition.shift(enumdirection.opposite())));

            return (IBlockData) iblockdata.set(BlockFenceGate.IN_WALL, flag);
        }
    }

    @Override
    public VoxelShape c(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        return (Boolean) iblockdata.get(BlockFenceGate.OPEN) ? VoxelShapes.a() : (((EnumDirection) iblockdata.get(BlockFenceGate.FACING)).n() == EnumDirection.EnumAxis.Z ? BlockFenceGate.Z_COLLISION_SHAPE : BlockFenceGate.X_COLLISION_SHAPE);
    }

    @Override
    public VoxelShape b_(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return (Boolean) iblockdata.get(BlockFenceGate.IN_WALL) ? (((EnumDirection) iblockdata.get(BlockFenceGate.FACING)).n() == EnumDirection.EnumAxis.X ? BlockFenceGate.X_OCCLUSION_SHAPE_LOW : BlockFenceGate.Z_OCCLUSION_SHAPE_LOW) : (((EnumDirection) iblockdata.get(BlockFenceGate.FACING)).n() == EnumDirection.EnumAxis.X ? BlockFenceGate.X_OCCLUSION_SHAPE : BlockFenceGate.Z_OCCLUSION_SHAPE);
    }

    @Override
    public boolean a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, PathMode pathmode) {
        switch (pathmode) {
            case LAND:
                return (Boolean) iblockdata.get(BlockFenceGate.OPEN);
            case WATER:
                return false;
            case AIR:
                return (Boolean) iblockdata.get(BlockFenceGate.OPEN);
            default:
                return false;
        }
    }

    @Override
    public IBlockData getPlacedState(BlockActionContext blockactioncontext) {
        World world = blockactioncontext.getWorld();
        BlockPosition blockposition = blockactioncontext.getClickPosition();
        boolean flag = world.isBlockIndirectlyPowered(blockposition);
        EnumDirection enumdirection = blockactioncontext.g();
        EnumDirection.EnumAxis enumdirection_enumaxis = enumdirection.n();
        boolean flag1 = enumdirection_enumaxis == EnumDirection.EnumAxis.Z && (this.h(world.getType(blockposition.west())) || this.h(world.getType(blockposition.east()))) || enumdirection_enumaxis == EnumDirection.EnumAxis.X && (this.h(world.getType(blockposition.north())) || this.h(world.getType(blockposition.south())));

        return (IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) this.getBlockData().set(BlockFenceGate.FACING, enumdirection)).set(BlockFenceGate.OPEN, flag)).set(BlockFenceGate.POWERED, flag)).set(BlockFenceGate.IN_WALL, flag1);
    }

    private boolean h(IBlockData iblockdata) {
        return iblockdata.a((Tag) TagsBlock.WALLS);
    }

    @Override
    public EnumInteractionResult interact(IBlockData iblockdata, World world, BlockPosition blockposition, EntityHuman entityhuman, EnumHand enumhand, MovingObjectPositionBlock movingobjectpositionblock) {
        if ((Boolean) iblockdata.get(BlockFenceGate.OPEN)) {
            iblockdata = (IBlockData) iblockdata.set(BlockFenceGate.OPEN, false);
            world.setTypeAndData(blockposition, iblockdata, 10);
        } else {
            EnumDirection enumdirection = entityhuman.getDirection();

            if (iblockdata.get(BlockFenceGate.FACING) == enumdirection.opposite()) {
                iblockdata = (IBlockData) iblockdata.set(BlockFenceGate.FACING, enumdirection);
            }

            iblockdata = (IBlockData) iblockdata.set(BlockFenceGate.OPEN, true);
            world.setTypeAndData(blockposition, iblockdata, 10);
        }

        boolean flag = (Boolean) iblockdata.get(BlockFenceGate.OPEN);

        world.a(entityhuman, flag ? 1008 : 1014, blockposition, 0);
        world.a((Entity) entityhuman, flag ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, blockposition);
        return EnumInteractionResult.a(world.isClientSide);
    }

    @Override
    public void doPhysics(IBlockData iblockdata, World world, BlockPosition blockposition, Block block, BlockPosition blockposition1, boolean flag) {
        if (!world.isClientSide) {
            boolean flag1 = world.isBlockIndirectlyPowered(blockposition);

            if ((Boolean) iblockdata.get(BlockFenceGate.POWERED) != flag1) {
                world.setTypeAndData(blockposition, (IBlockData) ((IBlockData) iblockdata.set(BlockFenceGate.POWERED, flag1)).set(BlockFenceGate.OPEN, flag1), 2);
                if ((Boolean) iblockdata.get(BlockFenceGate.OPEN) != flag1) {
                    world.a((EntityHuman) null, flag1 ? 1008 : 1014, blockposition, 0);
                    world.a(flag1 ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, blockposition);
                }
            }

        }
    }

    @Override
    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockFenceGate.FACING, BlockFenceGate.OPEN, BlockFenceGate.POWERED, BlockFenceGate.IN_WALL);
    }

    public static boolean a(IBlockData iblockdata, EnumDirection enumdirection) {
        return ((EnumDirection) iblockdata.get(BlockFenceGate.FACING)).n() == enumdirection.g().n();
    }
}
