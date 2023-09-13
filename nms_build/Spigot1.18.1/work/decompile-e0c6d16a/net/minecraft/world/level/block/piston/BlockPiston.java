package net.minecraft.world.level.block.piston;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.server.level.WorldServer;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsBlock;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockActionContext;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BlockDirectional;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EnumBlockMirror;
import net.minecraft.world.level.block.EnumBlockRotation;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.BlockStateList;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockProperties;
import net.minecraft.world.level.block.state.properties.BlockPropertyPistonType;
import net.minecraft.world.level.block.state.properties.BlockStateBoolean;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.EnumPistonReaction;
import net.minecraft.world.level.pathfinder.PathMode;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.VoxelShapeCollision;
import net.minecraft.world.phys.shapes.VoxelShapes;

public class BlockPiston extends BlockDirectional {

    public static final BlockStateBoolean EXTENDED = BlockProperties.EXTENDED;
    public static final int TRIGGER_EXTEND = 0;
    public static final int TRIGGER_CONTRACT = 1;
    public static final int TRIGGER_DROP = 2;
    public static final float PLATFORM_THICKNESS = 4.0F;
    protected static final VoxelShape EAST_AABB = Block.box(0.0D, 0.0D, 0.0D, 12.0D, 16.0D, 16.0D);
    protected static final VoxelShape WEST_AABB = Block.box(4.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    protected static final VoxelShape SOUTH_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 12.0D);
    protected static final VoxelShape NORTH_AABB = Block.box(0.0D, 0.0D, 4.0D, 16.0D, 16.0D, 16.0D);
    protected static final VoxelShape UP_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 12.0D, 16.0D);
    protected static final VoxelShape DOWN_AABB = Block.box(0.0D, 4.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    private final boolean isSticky;

    public BlockPiston(boolean flag, BlockBase.Info blockbase_info) {
        super(blockbase_info);
        this.registerDefaultState((IBlockData) ((IBlockData) ((IBlockData) this.stateDefinition.any()).setValue(BlockPiston.FACING, EnumDirection.NORTH)).setValue(BlockPiston.EXTENDED, false));
        this.isSticky = flag;
    }

    @Override
    public VoxelShape getShape(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        if ((Boolean) iblockdata.getValue(BlockPiston.EXTENDED)) {
            switch ((EnumDirection) iblockdata.getValue(BlockPiston.FACING)) {
                case DOWN:
                    return BlockPiston.DOWN_AABB;
                case UP:
                default:
                    return BlockPiston.UP_AABB;
                case NORTH:
                    return BlockPiston.NORTH_AABB;
                case SOUTH:
                    return BlockPiston.SOUTH_AABB;
                case WEST:
                    return BlockPiston.WEST_AABB;
                case EAST:
                    return BlockPiston.EAST_AABB;
            }
        } else {
            return VoxelShapes.block();
        }
    }

    @Override
    public void setPlacedBy(World world, BlockPosition blockposition, IBlockData iblockdata, EntityLiving entityliving, ItemStack itemstack) {
        if (!world.isClientSide) {
            this.checkIfExtend(world, blockposition, iblockdata);
        }

    }

    @Override
    public void neighborChanged(IBlockData iblockdata, World world, BlockPosition blockposition, Block block, BlockPosition blockposition1, boolean flag) {
        if (!world.isClientSide) {
            this.checkIfExtend(world, blockposition, iblockdata);
        }

    }

    @Override
    public void onPlace(IBlockData iblockdata, World world, BlockPosition blockposition, IBlockData iblockdata1, boolean flag) {
        if (!iblockdata1.is(iblockdata.getBlock())) {
            if (!world.isClientSide && world.getBlockEntity(blockposition) == null) {
                this.checkIfExtend(world, blockposition, iblockdata);
            }

        }
    }

    @Override
    public IBlockData getStateForPlacement(BlockActionContext blockactioncontext) {
        return (IBlockData) ((IBlockData) this.defaultBlockState().setValue(BlockPiston.FACING, blockactioncontext.getNearestLookingDirection().getOpposite())).setValue(BlockPiston.EXTENDED, false);
    }

    private void checkIfExtend(World world, BlockPosition blockposition, IBlockData iblockdata) {
        EnumDirection enumdirection = (EnumDirection) iblockdata.getValue(BlockPiston.FACING);
        boolean flag = this.getNeighborSignal(world, blockposition, enumdirection);

        if (flag && !(Boolean) iblockdata.getValue(BlockPiston.EXTENDED)) {
            if ((new PistonExtendsChecker(world, blockposition, enumdirection, true)).resolve()) {
                world.blockEvent(blockposition, this, 0, enumdirection.get3DDataValue());
            }
        } else if (!flag && (Boolean) iblockdata.getValue(BlockPiston.EXTENDED)) {
            BlockPosition blockposition1 = blockposition.relative(enumdirection, 2);
            IBlockData iblockdata1 = world.getBlockState(blockposition1);
            byte b0 = 1;

            if (iblockdata1.is(Blocks.MOVING_PISTON) && iblockdata1.getValue(BlockPiston.FACING) == enumdirection) {
                TileEntity tileentity = world.getBlockEntity(blockposition1);

                if (tileentity instanceof TileEntityPiston) {
                    TileEntityPiston tileentitypiston = (TileEntityPiston) tileentity;

                    if (tileentitypiston.isExtending() && (tileentitypiston.getProgress(0.0F) < 0.5F || world.getGameTime() == tileentitypiston.getLastTicked() || ((WorldServer) world).isHandlingTick())) {
                        b0 = 2;
                    }
                }
            }

            world.blockEvent(blockposition, this, b0, enumdirection.get3DDataValue());
        }

    }

    private boolean getNeighborSignal(World world, BlockPosition blockposition, EnumDirection enumdirection) {
        EnumDirection[] aenumdirection = EnumDirection.values();
        int i = aenumdirection.length;

        int j;

        for (j = 0; j < i; ++j) {
            EnumDirection enumdirection1 = aenumdirection[j];

            if (enumdirection1 != enumdirection && world.hasSignal(blockposition.relative(enumdirection1), enumdirection1)) {
                return true;
            }
        }

        if (world.hasSignal(blockposition, EnumDirection.DOWN)) {
            return true;
        } else {
            BlockPosition blockposition1 = blockposition.above();
            EnumDirection[] aenumdirection1 = EnumDirection.values();

            j = aenumdirection1.length;

            for (int k = 0; k < j; ++k) {
                EnumDirection enumdirection2 = aenumdirection1[k];

                if (enumdirection2 != EnumDirection.DOWN && world.hasSignal(blockposition1.relative(enumdirection2), enumdirection2)) {
                    return true;
                }
            }

            return false;
        }
    }

    @Override
    public boolean triggerEvent(IBlockData iblockdata, World world, BlockPosition blockposition, int i, int j) {
        EnumDirection enumdirection = (EnumDirection) iblockdata.getValue(BlockPiston.FACING);

        if (!world.isClientSide) {
            boolean flag = this.getNeighborSignal(world, blockposition, enumdirection);

            if (flag && (i == 1 || i == 2)) {
                world.setBlock(blockposition, (IBlockData) iblockdata.setValue(BlockPiston.EXTENDED, true), 2);
                return false;
            }

            if (!flag && i == 0) {
                return false;
            }
        }

        if (i == 0) {
            if (!this.moveBlocks(world, blockposition, enumdirection, true)) {
                return false;
            }

            world.setBlock(blockposition, (IBlockData) iblockdata.setValue(BlockPiston.EXTENDED, true), 67);
            world.playSound((EntityHuman) null, blockposition, SoundEffects.PISTON_EXTEND, SoundCategory.BLOCKS, 0.5F, world.random.nextFloat() * 0.25F + 0.6F);
            world.gameEvent(GameEvent.PISTON_EXTEND, blockposition);
        } else if (i == 1 || i == 2) {
            TileEntity tileentity = world.getBlockEntity(blockposition.relative(enumdirection));

            if (tileentity instanceof TileEntityPiston) {
                ((TileEntityPiston) tileentity).finalTick();
            }

            IBlockData iblockdata1 = (IBlockData) ((IBlockData) Blocks.MOVING_PISTON.defaultBlockState().setValue(BlockPistonMoving.FACING, enumdirection)).setValue(BlockPistonMoving.TYPE, this.isSticky ? BlockPropertyPistonType.STICKY : BlockPropertyPistonType.DEFAULT);

            world.setBlock(blockposition, iblockdata1, 20);
            world.setBlockEntity(BlockPistonMoving.newMovingBlockEntity(blockposition, iblockdata1, (IBlockData) this.defaultBlockState().setValue(BlockPiston.FACING, EnumDirection.from3DDataValue(j & 7)), enumdirection, false, true));
            world.blockUpdated(blockposition, iblockdata1.getBlock());
            iblockdata1.updateNeighbourShapes(world, blockposition, 2);
            if (this.isSticky) {
                BlockPosition blockposition1 = blockposition.offset(enumdirection.getStepX() * 2, enumdirection.getStepY() * 2, enumdirection.getStepZ() * 2);
                IBlockData iblockdata2 = world.getBlockState(blockposition1);
                boolean flag1 = false;

                if (iblockdata2.is(Blocks.MOVING_PISTON)) {
                    TileEntity tileentity1 = world.getBlockEntity(blockposition1);

                    if (tileentity1 instanceof TileEntityPiston) {
                        TileEntityPiston tileentitypiston = (TileEntityPiston) tileentity1;

                        if (tileentitypiston.getDirection() == enumdirection && tileentitypiston.isExtending()) {
                            tileentitypiston.finalTick();
                            flag1 = true;
                        }
                    }
                }

                if (!flag1) {
                    if (i == 1 && !iblockdata2.isAir() && isPushable(iblockdata2, world, blockposition1, enumdirection.getOpposite(), false, enumdirection) && (iblockdata2.getPistonPushReaction() == EnumPistonReaction.NORMAL || iblockdata2.is(Blocks.PISTON) || iblockdata2.is(Blocks.STICKY_PISTON))) {
                        this.moveBlocks(world, blockposition, enumdirection, false);
                    } else {
                        world.removeBlock(blockposition.relative(enumdirection), false);
                    }
                }
            } else {
                world.removeBlock(blockposition.relative(enumdirection), false);
            }

            world.playSound((EntityHuman) null, blockposition, SoundEffects.PISTON_CONTRACT, SoundCategory.BLOCKS, 0.5F, world.random.nextFloat() * 0.15F + 0.6F);
            world.gameEvent(GameEvent.PISTON_CONTRACT, blockposition);
        }

        return true;
    }

    public static boolean isPushable(IBlockData iblockdata, World world, BlockPosition blockposition, EnumDirection enumdirection, boolean flag, EnumDirection enumdirection1) {
        if (blockposition.getY() >= world.getMinBuildHeight() && blockposition.getY() <= world.getMaxBuildHeight() - 1 && world.getWorldBorder().isWithinBounds(blockposition)) {
            if (iblockdata.isAir()) {
                return true;
            } else if (!iblockdata.is(Blocks.OBSIDIAN) && !iblockdata.is(Blocks.CRYING_OBSIDIAN) && !iblockdata.is(Blocks.RESPAWN_ANCHOR)) {
                if (enumdirection == EnumDirection.DOWN && blockposition.getY() == world.getMinBuildHeight()) {
                    return false;
                } else if (enumdirection == EnumDirection.UP && blockposition.getY() == world.getMaxBuildHeight() - 1) {
                    return false;
                } else {
                    if (!iblockdata.is(Blocks.PISTON) && !iblockdata.is(Blocks.STICKY_PISTON)) {
                        if (iblockdata.getDestroySpeed(world, blockposition) == -1.0F) {
                            return false;
                        }

                        switch (iblockdata.getPistonPushReaction()) {
                            case BLOCK:
                                return false;
                            case DESTROY:
                                return flag;
                            case PUSH_ONLY:
                                return enumdirection == enumdirection1;
                        }
                    } else if ((Boolean) iblockdata.getValue(BlockPiston.EXTENDED)) {
                        return false;
                    }

                    return !iblockdata.hasBlockEntity();
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private boolean moveBlocks(World world, BlockPosition blockposition, EnumDirection enumdirection, boolean flag) {
        BlockPosition blockposition1 = blockposition.relative(enumdirection);

        if (!flag && world.getBlockState(blockposition1).is(Blocks.PISTON_HEAD)) {
            world.setBlock(blockposition1, Blocks.AIR.defaultBlockState(), 20);
        }

        PistonExtendsChecker pistonextendschecker = new PistonExtendsChecker(world, blockposition, enumdirection, flag);

        if (!pistonextendschecker.resolve()) {
            return false;
        } else {
            Map<BlockPosition, IBlockData> map = Maps.newHashMap();
            List<BlockPosition> list = pistonextendschecker.getToPush();
            List<IBlockData> list1 = Lists.newArrayList();

            for (int i = 0; i < list.size(); ++i) {
                BlockPosition blockposition2 = (BlockPosition) list.get(i);
                IBlockData iblockdata = world.getBlockState(blockposition2);

                list1.add(iblockdata);
                map.put(blockposition2, iblockdata);
            }

            List<BlockPosition> list2 = pistonextendschecker.getToDestroy();
            IBlockData[] aiblockdata = new IBlockData[list.size() + list2.size()];
            EnumDirection enumdirection1 = flag ? enumdirection : enumdirection.getOpposite();
            int j = 0;

            BlockPosition blockposition3;
            int k;
            IBlockData iblockdata1;

            for (k = list2.size() - 1; k >= 0; --k) {
                blockposition3 = (BlockPosition) list2.get(k);
                iblockdata1 = world.getBlockState(blockposition3);
                TileEntity tileentity = iblockdata1.hasBlockEntity() ? world.getBlockEntity(blockposition3) : null;

                dropResources(iblockdata1, world, blockposition3, tileentity);
                world.setBlock(blockposition3, Blocks.AIR.defaultBlockState(), 18);
                if (!iblockdata1.is((Tag) TagsBlock.FIRE)) {
                    world.addDestroyBlockEffect(blockposition3, iblockdata1);
                }

                aiblockdata[j++] = iblockdata1;
            }

            for (k = list.size() - 1; k >= 0; --k) {
                blockposition3 = (BlockPosition) list.get(k);
                iblockdata1 = world.getBlockState(blockposition3);
                blockposition3 = blockposition3.relative(enumdirection1);
                map.remove(blockposition3);
                IBlockData iblockdata2 = (IBlockData) Blocks.MOVING_PISTON.defaultBlockState().setValue(BlockPiston.FACING, enumdirection);

                world.setBlock(blockposition3, iblockdata2, 68);
                world.setBlockEntity(BlockPistonMoving.newMovingBlockEntity(blockposition3, iblockdata2, (IBlockData) list1.get(k), enumdirection, flag, false));
                aiblockdata[j++] = iblockdata1;
            }

            if (flag) {
                BlockPropertyPistonType blockpropertypistontype = this.isSticky ? BlockPropertyPistonType.STICKY : BlockPropertyPistonType.DEFAULT;
                IBlockData iblockdata3 = (IBlockData) ((IBlockData) Blocks.PISTON_HEAD.defaultBlockState().setValue(BlockPistonExtension.FACING, enumdirection)).setValue(BlockPistonExtension.TYPE, blockpropertypistontype);

                iblockdata1 = (IBlockData) ((IBlockData) Blocks.MOVING_PISTON.defaultBlockState().setValue(BlockPistonMoving.FACING, enumdirection)).setValue(BlockPistonMoving.TYPE, this.isSticky ? BlockPropertyPistonType.STICKY : BlockPropertyPistonType.DEFAULT);
                map.remove(blockposition1);
                world.setBlock(blockposition1, iblockdata1, 68);
                world.setBlockEntity(BlockPistonMoving.newMovingBlockEntity(blockposition1, iblockdata1, iblockdata3, enumdirection, true, true));
            }

            IBlockData iblockdata4 = Blocks.AIR.defaultBlockState();
            Iterator iterator = map.keySet().iterator();

            while (iterator.hasNext()) {
                BlockPosition blockposition4 = (BlockPosition) iterator.next();

                world.setBlock(blockposition4, iblockdata4, 82);
            }

            iterator = map.entrySet().iterator();

            BlockPosition blockposition5;

            while (iterator.hasNext()) {
                Entry<BlockPosition, IBlockData> entry = (Entry) iterator.next();

                blockposition5 = (BlockPosition) entry.getKey();
                IBlockData iblockdata5 = (IBlockData) entry.getValue();

                iblockdata5.updateIndirectNeighbourShapes(world, blockposition5, 2);
                iblockdata4.updateNeighbourShapes(world, blockposition5, 2);
                iblockdata4.updateIndirectNeighbourShapes(world, blockposition5, 2);
            }

            j = 0;

            int l;

            for (l = list2.size() - 1; l >= 0; --l) {
                iblockdata1 = aiblockdata[j++];
                blockposition5 = (BlockPosition) list2.get(l);
                iblockdata1.updateIndirectNeighbourShapes(world, blockposition5, 2);
                world.updateNeighborsAt(blockposition5, iblockdata1.getBlock());
            }

            for (l = list.size() - 1; l >= 0; --l) {
                world.updateNeighborsAt((BlockPosition) list.get(l), aiblockdata[j++].getBlock());
            }

            if (flag) {
                world.updateNeighborsAt(blockposition1, Blocks.PISTON_HEAD);
            }

            return true;
        }
    }

    @Override
    public IBlockData rotate(IBlockData iblockdata, EnumBlockRotation enumblockrotation) {
        return (IBlockData) iblockdata.setValue(BlockPiston.FACING, enumblockrotation.rotate((EnumDirection) iblockdata.getValue(BlockPiston.FACING)));
    }

    @Override
    public IBlockData mirror(IBlockData iblockdata, EnumBlockMirror enumblockmirror) {
        return iblockdata.rotate(enumblockmirror.getRotation((EnumDirection) iblockdata.getValue(BlockPiston.FACING)));
    }

    @Override
    protected void createBlockStateDefinition(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.add(BlockPiston.FACING, BlockPiston.EXTENDED);
    }

    @Override
    public boolean useShapeForLightOcclusion(IBlockData iblockdata) {
        return (Boolean) iblockdata.getValue(BlockPiston.EXTENDED);
    }

    @Override
    public boolean isPathfindable(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, PathMode pathmode) {
        return false;
    }
}
