package net.minecraft.world.level.block;

import com.google.common.base.MoreObjects;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.server.level.WorldServer;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.player.EntityHuman;
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
import net.minecraft.world.level.block.state.properties.BlockStateBoolean;
import net.minecraft.world.level.block.state.properties.BlockStateDirection;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.VoxelShapeCollision;

public class BlockTripwireHook extends Block {

    public static final BlockStateDirection FACING = BlockFacingHorizontal.FACING;
    public static final BlockStateBoolean POWERED = BlockProperties.POWERED;
    public static final BlockStateBoolean ATTACHED = BlockProperties.ATTACHED;
    protected static final int WIRE_DIST_MIN = 1;
    protected static final int WIRE_DIST_MAX = 42;
    private static final int RECHECK_PERIOD = 10;
    protected static final int AABB_OFFSET = 3;
    protected static final VoxelShape NORTH_AABB = Block.box(5.0D, 0.0D, 10.0D, 11.0D, 10.0D, 16.0D);
    protected static final VoxelShape SOUTH_AABB = Block.box(5.0D, 0.0D, 0.0D, 11.0D, 10.0D, 6.0D);
    protected static final VoxelShape WEST_AABB = Block.box(10.0D, 0.0D, 5.0D, 16.0D, 10.0D, 11.0D);
    protected static final VoxelShape EAST_AABB = Block.box(0.0D, 0.0D, 5.0D, 6.0D, 10.0D, 11.0D);

    public BlockTripwireHook(BlockBase.Info blockbase_info) {
        super(blockbase_info);
        this.registerDefaultState((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) this.stateDefinition.any()).setValue(BlockTripwireHook.FACING, EnumDirection.NORTH)).setValue(BlockTripwireHook.POWERED, false)).setValue(BlockTripwireHook.ATTACHED, false));
    }

    @Override
    public VoxelShape getShape(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        switch ((EnumDirection) iblockdata.getValue(BlockTripwireHook.FACING)) {
            case EAST:
            default:
                return BlockTripwireHook.EAST_AABB;
            case WEST:
                return BlockTripwireHook.WEST_AABB;
            case SOUTH:
                return BlockTripwireHook.SOUTH_AABB;
            case NORTH:
                return BlockTripwireHook.NORTH_AABB;
        }
    }

    @Override
    public boolean canSurvive(IBlockData iblockdata, IWorldReader iworldreader, BlockPosition blockposition) {
        EnumDirection enumdirection = (EnumDirection) iblockdata.getValue(BlockTripwireHook.FACING);
        BlockPosition blockposition1 = blockposition.relative(enumdirection.getOpposite());
        IBlockData iblockdata1 = iworldreader.getBlockState(blockposition1);

        return enumdirection.getAxis().isHorizontal() && iblockdata1.isFaceSturdy(iworldreader, blockposition1, enumdirection);
    }

    @Override
    public IBlockData updateShape(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        return enumdirection.getOpposite() == iblockdata.getValue(BlockTripwireHook.FACING) && !iblockdata.canSurvive(generatoraccess, blockposition) ? Blocks.AIR.defaultBlockState() : super.updateShape(iblockdata, enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1);
    }

    @Nullable
    @Override
    public IBlockData getStateForPlacement(BlockActionContext blockactioncontext) {
        IBlockData iblockdata = (IBlockData) ((IBlockData) this.defaultBlockState().setValue(BlockTripwireHook.POWERED, false)).setValue(BlockTripwireHook.ATTACHED, false);
        World world = blockactioncontext.getLevel();
        BlockPosition blockposition = blockactioncontext.getClickedPos();
        EnumDirection[] aenumdirection = blockactioncontext.getNearestLookingDirections();
        EnumDirection[] aenumdirection1 = aenumdirection;
        int i = aenumdirection.length;

        for (int j = 0; j < i; ++j) {
            EnumDirection enumdirection = aenumdirection1[j];

            if (enumdirection.getAxis().isHorizontal()) {
                EnumDirection enumdirection1 = enumdirection.getOpposite();

                iblockdata = (IBlockData) iblockdata.setValue(BlockTripwireHook.FACING, enumdirection1);
                if (iblockdata.canSurvive(world, blockposition)) {
                    return iblockdata;
                }
            }
        }

        return null;
    }

    @Override
    public void setPlacedBy(World world, BlockPosition blockposition, IBlockData iblockdata, EntityLiving entityliving, ItemStack itemstack) {
        this.calculateState(world, blockposition, iblockdata, false, false, -1, (IBlockData) null);
    }

    public void calculateState(World world, BlockPosition blockposition, IBlockData iblockdata, boolean flag, boolean flag1, int i, @Nullable IBlockData iblockdata1) {
        EnumDirection enumdirection = (EnumDirection) iblockdata.getValue(BlockTripwireHook.FACING);
        boolean flag2 = (Boolean) iblockdata.getValue(BlockTripwireHook.ATTACHED);
        boolean flag3 = (Boolean) iblockdata.getValue(BlockTripwireHook.POWERED);
        boolean flag4 = !flag;
        boolean flag5 = false;
        int j = 0;
        IBlockData[] aiblockdata = new IBlockData[42];

        BlockPosition blockposition1;

        for (int k = 1; k < 42; ++k) {
            blockposition1 = blockposition.relative(enumdirection, k);
            IBlockData iblockdata2 = world.getBlockState(blockposition1);

            if (iblockdata2.is(Blocks.TRIPWIRE_HOOK)) {
                if (iblockdata2.getValue(BlockTripwireHook.FACING) == enumdirection.getOpposite()) {
                    j = k;
                }
                break;
            }

            if (!iblockdata2.is(Blocks.TRIPWIRE) && k != i) {
                aiblockdata[k] = null;
                flag4 = false;
            } else {
                if (k == i) {
                    iblockdata2 = (IBlockData) MoreObjects.firstNonNull(iblockdata1, iblockdata2);
                }

                boolean flag6 = !(Boolean) iblockdata2.getValue(BlockTripwire.DISARMED);
                boolean flag7 = (Boolean) iblockdata2.getValue(BlockTripwire.POWERED);

                flag5 |= flag6 && flag7;
                aiblockdata[k] = iblockdata2;
                if (k == i) {
                    world.scheduleTick(blockposition, (Block) this, 10);
                    flag4 &= flag6;
                }
            }
        }

        flag4 &= j > 1;
        flag5 &= flag4;
        IBlockData iblockdata3 = (IBlockData) ((IBlockData) this.defaultBlockState().setValue(BlockTripwireHook.ATTACHED, flag4)).setValue(BlockTripwireHook.POWERED, flag5);

        if (j > 0) {
            blockposition1 = blockposition.relative(enumdirection, j);
            EnumDirection enumdirection1 = enumdirection.getOpposite();

            world.setBlock(blockposition1, (IBlockData) iblockdata3.setValue(BlockTripwireHook.FACING, enumdirection1), 3);
            this.notifyNeighbors(world, blockposition1, enumdirection1);
            this.emitState(world, blockposition1, flag4, flag5, flag2, flag3);
        }

        this.emitState(world, blockposition, flag4, flag5, flag2, flag3);
        if (!flag) {
            world.setBlock(blockposition, (IBlockData) iblockdata3.setValue(BlockTripwireHook.FACING, enumdirection), 3);
            if (flag1) {
                this.notifyNeighbors(world, blockposition, enumdirection);
            }
        }

        if (flag2 != flag4) {
            for (int l = 1; l < j; ++l) {
                BlockPosition blockposition2 = blockposition.relative(enumdirection, l);
                IBlockData iblockdata4 = aiblockdata[l];

                if (iblockdata4 != null) {
                    world.setBlock(blockposition2, (IBlockData) iblockdata4.setValue(BlockTripwireHook.ATTACHED, flag4), 3);
                    if (!world.getBlockState(blockposition2).isAir()) {
                        ;
                    }
                }
            }
        }

    }

    @Override
    public void tick(IBlockData iblockdata, WorldServer worldserver, BlockPosition blockposition, RandomSource randomsource) {
        this.calculateState(worldserver, blockposition, iblockdata, false, true, -1, (IBlockData) null);
    }

    private void emitState(World world, BlockPosition blockposition, boolean flag, boolean flag1, boolean flag2, boolean flag3) {
        if (flag1 && !flag3) {
            world.playSound((EntityHuman) null, blockposition, SoundEffects.TRIPWIRE_CLICK_ON, SoundCategory.BLOCKS, 0.4F, 0.6F);
            world.gameEvent((Entity) null, GameEvent.BLOCK_ACTIVATE, blockposition);
        } else if (!flag1 && flag3) {
            world.playSound((EntityHuman) null, blockposition, SoundEffects.TRIPWIRE_CLICK_OFF, SoundCategory.BLOCKS, 0.4F, 0.5F);
            world.gameEvent((Entity) null, GameEvent.BLOCK_DEACTIVATE, blockposition);
        } else if (flag && !flag2) {
            world.playSound((EntityHuman) null, blockposition, SoundEffects.TRIPWIRE_ATTACH, SoundCategory.BLOCKS, 0.4F, 0.7F);
            world.gameEvent((Entity) null, GameEvent.BLOCK_ATTACH, blockposition);
        } else if (!flag && flag2) {
            world.playSound((EntityHuman) null, blockposition, SoundEffects.TRIPWIRE_DETACH, SoundCategory.BLOCKS, 0.4F, 1.2F / (world.random.nextFloat() * 0.2F + 0.9F));
            world.gameEvent((Entity) null, GameEvent.BLOCK_DETACH, blockposition);
        }

    }

    private void notifyNeighbors(World world, BlockPosition blockposition, EnumDirection enumdirection) {
        world.updateNeighborsAt(blockposition, this);
        world.updateNeighborsAt(blockposition.relative(enumdirection.getOpposite()), this);
    }

    @Override
    public void onRemove(IBlockData iblockdata, World world, BlockPosition blockposition, IBlockData iblockdata1, boolean flag) {
        if (!flag && !iblockdata.is(iblockdata1.getBlock())) {
            boolean flag1 = (Boolean) iblockdata.getValue(BlockTripwireHook.ATTACHED);
            boolean flag2 = (Boolean) iblockdata.getValue(BlockTripwireHook.POWERED);

            if (flag1 || flag2) {
                this.calculateState(world, blockposition, iblockdata, true, false, -1, (IBlockData) null);
            }

            if (flag2) {
                world.updateNeighborsAt(blockposition, this);
                world.updateNeighborsAt(blockposition.relative(((EnumDirection) iblockdata.getValue(BlockTripwireHook.FACING)).getOpposite()), this);
            }

            super.onRemove(iblockdata, world, blockposition, iblockdata1, flag);
        }
    }

    @Override
    public int getSignal(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
        return (Boolean) iblockdata.getValue(BlockTripwireHook.POWERED) ? 15 : 0;
    }

    @Override
    public int getDirectSignal(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
        return !(Boolean) iblockdata.getValue(BlockTripwireHook.POWERED) ? 0 : (iblockdata.getValue(BlockTripwireHook.FACING) == enumdirection ? 15 : 0);
    }

    @Override
    public boolean isSignalSource(IBlockData iblockdata) {
        return true;
    }

    @Override
    public IBlockData rotate(IBlockData iblockdata, EnumBlockRotation enumblockrotation) {
        return (IBlockData) iblockdata.setValue(BlockTripwireHook.FACING, enumblockrotation.rotate((EnumDirection) iblockdata.getValue(BlockTripwireHook.FACING)));
    }

    @Override
    public IBlockData mirror(IBlockData iblockdata, EnumBlockMirror enumblockmirror) {
        return iblockdata.rotate(enumblockmirror.getRotation((EnumDirection) iblockdata.getValue(BlockTripwireHook.FACING)));
    }

    @Override
    protected void createBlockStateDefinition(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.add(BlockTripwireHook.FACING, BlockTripwireHook.POWERED, BlockTripwireHook.ATTACHED);
    }
}
