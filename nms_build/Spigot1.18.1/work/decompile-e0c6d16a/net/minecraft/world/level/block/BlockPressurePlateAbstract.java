package net.minecraft.world.level.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.EnumPistonReaction;
import net.minecraft.world.phys.AxisAlignedBB;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.VoxelShapeCollision;

public abstract class BlockPressurePlateAbstract extends Block {

    protected static final VoxelShape PRESSED_AABB = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 0.5D, 15.0D);
    protected static final VoxelShape AABB = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 1.0D, 15.0D);
    protected static final AxisAlignedBB TOUCH_AABB = new AxisAlignedBB(0.125D, 0.0D, 0.125D, 0.875D, 0.25D, 0.875D);

    protected BlockPressurePlateAbstract(BlockBase.Info blockbase_info) {
        super(blockbase_info);
    }

    @Override
    public VoxelShape getShape(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        return this.getSignalForState(iblockdata) > 0 ? BlockPressurePlateAbstract.PRESSED_AABB : BlockPressurePlateAbstract.AABB;
    }

    protected int getPressedTime() {
        return 20;
    }

    @Override
    public boolean isPossibleToRespawnInThis() {
        return true;
    }

    @Override
    public IBlockData updateShape(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        return enumdirection == EnumDirection.DOWN && !iblockdata.canSurvive(generatoraccess, blockposition) ? Blocks.AIR.defaultBlockState() : super.updateShape(iblockdata, enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1);
    }

    @Override
    public boolean canSurvive(IBlockData iblockdata, IWorldReader iworldreader, BlockPosition blockposition) {
        BlockPosition blockposition1 = blockposition.below();

        return canSupportRigidBlock(iworldreader, blockposition1) || canSupportCenter(iworldreader, blockposition1, EnumDirection.UP);
    }

    @Override
    public void tick(IBlockData iblockdata, WorldServer worldserver, BlockPosition blockposition, Random random) {
        int i = this.getSignalForState(iblockdata);

        if (i > 0) {
            this.checkPressed((Entity) null, worldserver, blockposition, iblockdata, i);
        }

    }

    @Override
    public void entityInside(IBlockData iblockdata, World world, BlockPosition blockposition, Entity entity) {
        if (!world.isClientSide) {
            int i = this.getSignalForState(iblockdata);

            if (i == 0) {
                this.checkPressed(entity, world, blockposition, iblockdata, i);
            }

        }
    }

    protected void checkPressed(@Nullable Entity entity, World world, BlockPosition blockposition, IBlockData iblockdata, int i) {
        int j = this.getSignalStrength(world, blockposition);
        boolean flag = i > 0;
        boolean flag1 = j > 0;

        if (i != j) {
            IBlockData iblockdata1 = this.setSignalForState(iblockdata, j);

            world.setBlock(blockposition, iblockdata1, 2);
            this.updateNeighbours(world, blockposition);
            world.setBlocksDirty(blockposition, iblockdata, iblockdata1);
        }

        if (!flag1 && flag) {
            this.playOffSound(world, blockposition);
            world.gameEvent(entity, GameEvent.BLOCK_UNPRESS, blockposition);
        } else if (flag1 && !flag) {
            this.playOnSound(world, blockposition);
            world.gameEvent(entity, GameEvent.BLOCK_PRESS, blockposition);
        }

        if (flag1) {
            world.scheduleTick(new BlockPosition(blockposition), (Block) this, this.getPressedTime());
        }

    }

    protected abstract void playOnSound(GeneratorAccess generatoraccess, BlockPosition blockposition);

    protected abstract void playOffSound(GeneratorAccess generatoraccess, BlockPosition blockposition);

    @Override
    public void onRemove(IBlockData iblockdata, World world, BlockPosition blockposition, IBlockData iblockdata1, boolean flag) {
        if (!flag && !iblockdata.is(iblockdata1.getBlock())) {
            if (this.getSignalForState(iblockdata) > 0) {
                this.updateNeighbours(world, blockposition);
            }

            super.onRemove(iblockdata, world, blockposition, iblockdata1, flag);
        }
    }

    protected void updateNeighbours(World world, BlockPosition blockposition) {
        world.updateNeighborsAt(blockposition, this);
        world.updateNeighborsAt(blockposition.below(), this);
    }

    @Override
    public int getSignal(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
        return this.getSignalForState(iblockdata);
    }

    @Override
    public int getDirectSignal(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
        return enumdirection == EnumDirection.UP ? this.getSignalForState(iblockdata) : 0;
    }

    @Override
    public boolean isSignalSource(IBlockData iblockdata) {
        return true;
    }

    @Override
    public EnumPistonReaction getPistonPushReaction(IBlockData iblockdata) {
        return EnumPistonReaction.DESTROY;
    }

    protected abstract int getSignalStrength(World world, BlockPosition blockposition);

    protected abstract int getSignalForState(IBlockData iblockdata);

    protected abstract IBlockData setSignalForState(IBlockData iblockdata, int i);
}
