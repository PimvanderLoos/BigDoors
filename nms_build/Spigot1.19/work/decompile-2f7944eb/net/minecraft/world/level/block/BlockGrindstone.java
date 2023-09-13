package net.minecraft.world.level.block;

import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.stats.StatisticList;
import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.ITileInventory;
import net.minecraft.world.TileInventory;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.inventory.ContainerAccess;
import net.minecraft.world.inventory.ContainerGrindstone;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.BlockStateList;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockPropertyAttachPosition;
import net.minecraft.world.level.pathfinder.PathMode;
import net.minecraft.world.phys.MovingObjectPositionBlock;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.VoxelShapeCollision;
import net.minecraft.world.phys.shapes.VoxelShapes;

public class BlockGrindstone extends BlockAttachable {

    public static final VoxelShape FLOOR_NORTH_SOUTH_LEFT_POST = Block.box(2.0D, 0.0D, 6.0D, 4.0D, 7.0D, 10.0D);
    public static final VoxelShape FLOOR_NORTH_SOUTH_RIGHT_POST = Block.box(12.0D, 0.0D, 6.0D, 14.0D, 7.0D, 10.0D);
    public static final VoxelShape FLOOR_NORTH_SOUTH_LEFT_PIVOT = Block.box(2.0D, 7.0D, 5.0D, 4.0D, 13.0D, 11.0D);
    public static final VoxelShape FLOOR_NORTH_SOUTH_RIGHT_PIVOT = Block.box(12.0D, 7.0D, 5.0D, 14.0D, 13.0D, 11.0D);
    public static final VoxelShape FLOOR_NORTH_SOUTH_LEFT_LEG = VoxelShapes.or(BlockGrindstone.FLOOR_NORTH_SOUTH_LEFT_POST, BlockGrindstone.FLOOR_NORTH_SOUTH_LEFT_PIVOT);
    public static final VoxelShape FLOOR_NORTH_SOUTH_RIGHT_LEG = VoxelShapes.or(BlockGrindstone.FLOOR_NORTH_SOUTH_RIGHT_POST, BlockGrindstone.FLOOR_NORTH_SOUTH_RIGHT_PIVOT);
    public static final VoxelShape FLOOR_NORTH_SOUTH_ALL_LEGS = VoxelShapes.or(BlockGrindstone.FLOOR_NORTH_SOUTH_LEFT_LEG, BlockGrindstone.FLOOR_NORTH_SOUTH_RIGHT_LEG);
    public static final VoxelShape FLOOR_NORTH_SOUTH_GRINDSTONE = VoxelShapes.or(BlockGrindstone.FLOOR_NORTH_SOUTH_ALL_LEGS, Block.box(4.0D, 4.0D, 2.0D, 12.0D, 16.0D, 14.0D));
    public static final VoxelShape FLOOR_EAST_WEST_LEFT_POST = Block.box(6.0D, 0.0D, 2.0D, 10.0D, 7.0D, 4.0D);
    public static final VoxelShape FLOOR_EAST_WEST_RIGHT_POST = Block.box(6.0D, 0.0D, 12.0D, 10.0D, 7.0D, 14.0D);
    public static final VoxelShape FLOOR_EAST_WEST_LEFT_PIVOT = Block.box(5.0D, 7.0D, 2.0D, 11.0D, 13.0D, 4.0D);
    public static final VoxelShape FLOOR_EAST_WEST_RIGHT_PIVOT = Block.box(5.0D, 7.0D, 12.0D, 11.0D, 13.0D, 14.0D);
    public static final VoxelShape FLOOR_EAST_WEST_LEFT_LEG = VoxelShapes.or(BlockGrindstone.FLOOR_EAST_WEST_LEFT_POST, BlockGrindstone.FLOOR_EAST_WEST_LEFT_PIVOT);
    public static final VoxelShape FLOOR_EAST_WEST_RIGHT_LEG = VoxelShapes.or(BlockGrindstone.FLOOR_EAST_WEST_RIGHT_POST, BlockGrindstone.FLOOR_EAST_WEST_RIGHT_PIVOT);
    public static final VoxelShape FLOOR_EAST_WEST_ALL_LEGS = VoxelShapes.or(BlockGrindstone.FLOOR_EAST_WEST_LEFT_LEG, BlockGrindstone.FLOOR_EAST_WEST_RIGHT_LEG);
    public static final VoxelShape FLOOR_EAST_WEST_GRINDSTONE = VoxelShapes.or(BlockGrindstone.FLOOR_EAST_WEST_ALL_LEGS, Block.box(2.0D, 4.0D, 4.0D, 14.0D, 16.0D, 12.0D));
    public static final VoxelShape WALL_SOUTH_LEFT_POST = Block.box(2.0D, 6.0D, 0.0D, 4.0D, 10.0D, 7.0D);
    public static final VoxelShape WALL_SOUTH_RIGHT_POST = Block.box(12.0D, 6.0D, 0.0D, 14.0D, 10.0D, 7.0D);
    public static final VoxelShape WALL_SOUTH_LEFT_PIVOT = Block.box(2.0D, 5.0D, 7.0D, 4.0D, 11.0D, 13.0D);
    public static final VoxelShape WALL_SOUTH_RIGHT_PIVOT = Block.box(12.0D, 5.0D, 7.0D, 14.0D, 11.0D, 13.0D);
    public static final VoxelShape WALL_SOUTH_LEFT_LEG = VoxelShapes.or(BlockGrindstone.WALL_SOUTH_LEFT_POST, BlockGrindstone.WALL_SOUTH_LEFT_PIVOT);
    public static final VoxelShape WALL_SOUTH_RIGHT_LEG = VoxelShapes.or(BlockGrindstone.WALL_SOUTH_RIGHT_POST, BlockGrindstone.WALL_SOUTH_RIGHT_PIVOT);
    public static final VoxelShape WALL_SOUTH_ALL_LEGS = VoxelShapes.or(BlockGrindstone.WALL_SOUTH_LEFT_LEG, BlockGrindstone.WALL_SOUTH_RIGHT_LEG);
    public static final VoxelShape WALL_SOUTH_GRINDSTONE = VoxelShapes.or(BlockGrindstone.WALL_SOUTH_ALL_LEGS, Block.box(4.0D, 2.0D, 4.0D, 12.0D, 14.0D, 16.0D));
    public static final VoxelShape WALL_NORTH_LEFT_POST = Block.box(2.0D, 6.0D, 7.0D, 4.0D, 10.0D, 16.0D);
    public static final VoxelShape WALL_NORTH_RIGHT_POST = Block.box(12.0D, 6.0D, 7.0D, 14.0D, 10.0D, 16.0D);
    public static final VoxelShape WALL_NORTH_LEFT_PIVOT = Block.box(2.0D, 5.0D, 3.0D, 4.0D, 11.0D, 9.0D);
    public static final VoxelShape WALL_NORTH_RIGHT_PIVOT = Block.box(12.0D, 5.0D, 3.0D, 14.0D, 11.0D, 9.0D);
    public static final VoxelShape WALL_NORTH_LEFT_LEG = VoxelShapes.or(BlockGrindstone.WALL_NORTH_LEFT_POST, BlockGrindstone.WALL_NORTH_LEFT_PIVOT);
    public static final VoxelShape WALL_NORTH_RIGHT_LEG = VoxelShapes.or(BlockGrindstone.WALL_NORTH_RIGHT_POST, BlockGrindstone.WALL_NORTH_RIGHT_PIVOT);
    public static final VoxelShape WALL_NORTH_ALL_LEGS = VoxelShapes.or(BlockGrindstone.WALL_NORTH_LEFT_LEG, BlockGrindstone.WALL_NORTH_RIGHT_LEG);
    public static final VoxelShape WALL_NORTH_GRINDSTONE = VoxelShapes.or(BlockGrindstone.WALL_NORTH_ALL_LEGS, Block.box(4.0D, 2.0D, 0.0D, 12.0D, 14.0D, 12.0D));
    public static final VoxelShape WALL_WEST_LEFT_POST = Block.box(7.0D, 6.0D, 2.0D, 16.0D, 10.0D, 4.0D);
    public static final VoxelShape WALL_WEST_RIGHT_POST = Block.box(7.0D, 6.0D, 12.0D, 16.0D, 10.0D, 14.0D);
    public static final VoxelShape WALL_WEST_LEFT_PIVOT = Block.box(3.0D, 5.0D, 2.0D, 9.0D, 11.0D, 4.0D);
    public static final VoxelShape WALL_WEST_RIGHT_PIVOT = Block.box(3.0D, 5.0D, 12.0D, 9.0D, 11.0D, 14.0D);
    public static final VoxelShape WALL_WEST_LEFT_LEG = VoxelShapes.or(BlockGrindstone.WALL_WEST_LEFT_POST, BlockGrindstone.WALL_WEST_LEFT_PIVOT);
    public static final VoxelShape WALL_WEST_RIGHT_LEG = VoxelShapes.or(BlockGrindstone.WALL_WEST_RIGHT_POST, BlockGrindstone.WALL_WEST_RIGHT_PIVOT);
    public static final VoxelShape WALL_WEST_ALL_LEGS = VoxelShapes.or(BlockGrindstone.WALL_WEST_LEFT_LEG, BlockGrindstone.WALL_WEST_RIGHT_LEG);
    public static final VoxelShape WALL_WEST_GRINDSTONE = VoxelShapes.or(BlockGrindstone.WALL_WEST_ALL_LEGS, Block.box(0.0D, 2.0D, 4.0D, 12.0D, 14.0D, 12.0D));
    public static final VoxelShape WALL_EAST_LEFT_POST = Block.box(0.0D, 6.0D, 2.0D, 9.0D, 10.0D, 4.0D);
    public static final VoxelShape WALL_EAST_RIGHT_POST = Block.box(0.0D, 6.0D, 12.0D, 9.0D, 10.0D, 14.0D);
    public static final VoxelShape WALL_EAST_LEFT_PIVOT = Block.box(7.0D, 5.0D, 2.0D, 13.0D, 11.0D, 4.0D);
    public static final VoxelShape WALL_EAST_RIGHT_PIVOT = Block.box(7.0D, 5.0D, 12.0D, 13.0D, 11.0D, 14.0D);
    public static final VoxelShape WALL_EAST_LEFT_LEG = VoxelShapes.or(BlockGrindstone.WALL_EAST_LEFT_POST, BlockGrindstone.WALL_EAST_LEFT_PIVOT);
    public static final VoxelShape WALL_EAST_RIGHT_LEG = VoxelShapes.or(BlockGrindstone.WALL_EAST_RIGHT_POST, BlockGrindstone.WALL_EAST_RIGHT_PIVOT);
    public static final VoxelShape WALL_EAST_ALL_LEGS = VoxelShapes.or(BlockGrindstone.WALL_EAST_LEFT_LEG, BlockGrindstone.WALL_EAST_RIGHT_LEG);
    public static final VoxelShape WALL_EAST_GRINDSTONE = VoxelShapes.or(BlockGrindstone.WALL_EAST_ALL_LEGS, Block.box(4.0D, 2.0D, 4.0D, 16.0D, 14.0D, 12.0D));
    public static final VoxelShape CEILING_NORTH_SOUTH_LEFT_POST = Block.box(2.0D, 9.0D, 6.0D, 4.0D, 16.0D, 10.0D);
    public static final VoxelShape CEILING_NORTH_SOUTH_RIGHT_POST = Block.box(12.0D, 9.0D, 6.0D, 14.0D, 16.0D, 10.0D);
    public static final VoxelShape CEILING_NORTH_SOUTH_LEFT_PIVOT = Block.box(2.0D, 3.0D, 5.0D, 4.0D, 9.0D, 11.0D);
    public static final VoxelShape CEILING_NORTH_SOUTH_RIGHT_PIVOT = Block.box(12.0D, 3.0D, 5.0D, 14.0D, 9.0D, 11.0D);
    public static final VoxelShape CEILING_NORTH_SOUTH_LEFT_LEG = VoxelShapes.or(BlockGrindstone.CEILING_NORTH_SOUTH_LEFT_POST, BlockGrindstone.CEILING_NORTH_SOUTH_LEFT_PIVOT);
    public static final VoxelShape CEILING_NORTH_SOUTH_RIGHT_LEG = VoxelShapes.or(BlockGrindstone.CEILING_NORTH_SOUTH_RIGHT_POST, BlockGrindstone.CEILING_NORTH_SOUTH_RIGHT_PIVOT);
    public static final VoxelShape CEILING_NORTH_SOUTH_ALL_LEGS = VoxelShapes.or(BlockGrindstone.CEILING_NORTH_SOUTH_LEFT_LEG, BlockGrindstone.CEILING_NORTH_SOUTH_RIGHT_LEG);
    public static final VoxelShape CEILING_NORTH_SOUTH_GRINDSTONE = VoxelShapes.or(BlockGrindstone.CEILING_NORTH_SOUTH_ALL_LEGS, Block.box(4.0D, 0.0D, 2.0D, 12.0D, 12.0D, 14.0D));
    public static final VoxelShape CEILING_EAST_WEST_LEFT_POST = Block.box(6.0D, 9.0D, 2.0D, 10.0D, 16.0D, 4.0D);
    public static final VoxelShape CEILING_EAST_WEST_RIGHT_POST = Block.box(6.0D, 9.0D, 12.0D, 10.0D, 16.0D, 14.0D);
    public static final VoxelShape CEILING_EAST_WEST_LEFT_PIVOT = Block.box(5.0D, 3.0D, 2.0D, 11.0D, 9.0D, 4.0D);
    public static final VoxelShape CEILING_EAST_WEST_RIGHT_PIVOT = Block.box(5.0D, 3.0D, 12.0D, 11.0D, 9.0D, 14.0D);
    public static final VoxelShape CEILING_EAST_WEST_LEFT_LEG = VoxelShapes.or(BlockGrindstone.CEILING_EAST_WEST_LEFT_POST, BlockGrindstone.CEILING_EAST_WEST_LEFT_PIVOT);
    public static final VoxelShape CEILING_EAST_WEST_RIGHT_LEG = VoxelShapes.or(BlockGrindstone.CEILING_EAST_WEST_RIGHT_POST, BlockGrindstone.CEILING_EAST_WEST_RIGHT_PIVOT);
    public static final VoxelShape CEILING_EAST_WEST_ALL_LEGS = VoxelShapes.or(BlockGrindstone.CEILING_EAST_WEST_LEFT_LEG, BlockGrindstone.CEILING_EAST_WEST_RIGHT_LEG);
    public static final VoxelShape CEILING_EAST_WEST_GRINDSTONE = VoxelShapes.or(BlockGrindstone.CEILING_EAST_WEST_ALL_LEGS, Block.box(2.0D, 0.0D, 4.0D, 14.0D, 12.0D, 12.0D));
    private static final IChatBaseComponent CONTAINER_TITLE = IChatBaseComponent.translatable("container.grindstone_title");

    protected BlockGrindstone(BlockBase.Info blockbase_info) {
        super(blockbase_info);
        this.registerDefaultState((IBlockData) ((IBlockData) ((IBlockData) this.stateDefinition.any()).setValue(BlockGrindstone.FACING, EnumDirection.NORTH)).setValue(BlockGrindstone.FACE, BlockPropertyAttachPosition.WALL));
    }

    @Override
    public EnumRenderType getRenderShape(IBlockData iblockdata) {
        return EnumRenderType.MODEL;
    }

    private VoxelShape getVoxelShape(IBlockData iblockdata) {
        EnumDirection enumdirection = (EnumDirection) iblockdata.getValue(BlockGrindstone.FACING);

        switch ((BlockPropertyAttachPosition) iblockdata.getValue(BlockGrindstone.FACE)) {
            case FLOOR:
                if (enumdirection != EnumDirection.NORTH && enumdirection != EnumDirection.SOUTH) {
                    return BlockGrindstone.FLOOR_EAST_WEST_GRINDSTONE;
                }

                return BlockGrindstone.FLOOR_NORTH_SOUTH_GRINDSTONE;
            case WALL:
                if (enumdirection == EnumDirection.NORTH) {
                    return BlockGrindstone.WALL_NORTH_GRINDSTONE;
                } else if (enumdirection == EnumDirection.SOUTH) {
                    return BlockGrindstone.WALL_SOUTH_GRINDSTONE;
                } else {
                    if (enumdirection == EnumDirection.EAST) {
                        return BlockGrindstone.WALL_EAST_GRINDSTONE;
                    }

                    return BlockGrindstone.WALL_WEST_GRINDSTONE;
                }
            case CEILING:
                if (enumdirection != EnumDirection.NORTH && enumdirection != EnumDirection.SOUTH) {
                    return BlockGrindstone.CEILING_EAST_WEST_GRINDSTONE;
                }

                return BlockGrindstone.CEILING_NORTH_SOUTH_GRINDSTONE;
            default:
                return BlockGrindstone.FLOOR_EAST_WEST_GRINDSTONE;
        }
    }

    @Override
    public VoxelShape getCollisionShape(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        return this.getVoxelShape(iblockdata);
    }

    @Override
    public VoxelShape getShape(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        return this.getVoxelShape(iblockdata);
    }

    @Override
    public boolean canSurvive(IBlockData iblockdata, IWorldReader iworldreader, BlockPosition blockposition) {
        return true;
    }

    @Override
    public EnumInteractionResult use(IBlockData iblockdata, World world, BlockPosition blockposition, EntityHuman entityhuman, EnumHand enumhand, MovingObjectPositionBlock movingobjectpositionblock) {
        if (world.isClientSide) {
            return EnumInteractionResult.SUCCESS;
        } else {
            entityhuman.openMenu(iblockdata.getMenuProvider(world, blockposition));
            entityhuman.awardStat(StatisticList.INTERACT_WITH_GRINDSTONE);
            return EnumInteractionResult.CONSUME;
        }
    }

    @Override
    public ITileInventory getMenuProvider(IBlockData iblockdata, World world, BlockPosition blockposition) {
        return new TileInventory((i, playerinventory, entityhuman) -> {
            return new ContainerGrindstone(i, playerinventory, ContainerAccess.create(world, blockposition));
        }, BlockGrindstone.CONTAINER_TITLE);
    }

    @Override
    public IBlockData rotate(IBlockData iblockdata, EnumBlockRotation enumblockrotation) {
        return (IBlockData) iblockdata.setValue(BlockGrindstone.FACING, enumblockrotation.rotate((EnumDirection) iblockdata.getValue(BlockGrindstone.FACING)));
    }

    @Override
    public IBlockData mirror(IBlockData iblockdata, EnumBlockMirror enumblockmirror) {
        return iblockdata.rotate(enumblockmirror.getRotation((EnumDirection) iblockdata.getValue(BlockGrindstone.FACING)));
    }

    @Override
    protected void createBlockStateDefinition(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.add(BlockGrindstone.FACING, BlockGrindstone.FACE);
    }

    @Override
    public boolean isPathfindable(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, PathMode pathmode) {
        return false;
    }
}
