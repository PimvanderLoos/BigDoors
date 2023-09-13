package net.minecraft.world.level.block;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.server.level.WorldServer;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.util.RandomSource;
import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.entity.decoration.EntityItemFrame;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.entity.TileEntityComparator;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.BlockStateList;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockProperties;
import net.minecraft.world.level.block.state.properties.BlockPropertyComparatorMode;
import net.minecraft.world.level.block.state.properties.BlockStateEnum;
import net.minecraft.world.phys.AxisAlignedBB;
import net.minecraft.world.phys.MovingObjectPositionBlock;
import net.minecraft.world.ticks.TickListPriority;

public class BlockRedstoneComparator extends BlockDiodeAbstract implements ITileEntity {

    public static final BlockStateEnum<BlockPropertyComparatorMode> MODE = BlockProperties.MODE_COMPARATOR;

    public BlockRedstoneComparator(BlockBase.Info blockbase_info) {
        super(blockbase_info);
        this.registerDefaultState((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) this.stateDefinition.any()).setValue(BlockRedstoneComparator.FACING, EnumDirection.NORTH)).setValue(BlockRedstoneComparator.POWERED, false)).setValue(BlockRedstoneComparator.MODE, BlockPropertyComparatorMode.COMPARE));
    }

    @Override
    protected int getDelay(IBlockData iblockdata) {
        return 2;
    }

    @Override
    protected int getOutputSignal(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata) {
        TileEntity tileentity = iblockaccess.getBlockEntity(blockposition);

        return tileentity instanceof TileEntityComparator ? ((TileEntityComparator) tileentity).getOutputSignal() : 0;
    }

    private int calculateOutputSignal(World world, BlockPosition blockposition, IBlockData iblockdata) {
        int i = this.getInputSignal(world, blockposition, iblockdata);

        if (i == 0) {
            return 0;
        } else {
            int j = this.getAlternateSignal(world, blockposition, iblockdata);

            return j > i ? 0 : (iblockdata.getValue(BlockRedstoneComparator.MODE) == BlockPropertyComparatorMode.SUBTRACT ? i - j : i);
        }
    }

    @Override
    protected boolean shouldTurnOn(World world, BlockPosition blockposition, IBlockData iblockdata) {
        int i = this.getInputSignal(world, blockposition, iblockdata);

        if (i == 0) {
            return false;
        } else {
            int j = this.getAlternateSignal(world, blockposition, iblockdata);

            return i > j ? true : i == j && iblockdata.getValue(BlockRedstoneComparator.MODE) == BlockPropertyComparatorMode.COMPARE;
        }
    }

    @Override
    protected int getInputSignal(World world, BlockPosition blockposition, IBlockData iblockdata) {
        int i = super.getInputSignal(world, blockposition, iblockdata);
        EnumDirection enumdirection = (EnumDirection) iblockdata.getValue(BlockRedstoneComparator.FACING);
        BlockPosition blockposition1 = blockposition.relative(enumdirection);
        IBlockData iblockdata1 = world.getBlockState(blockposition1);

        if (iblockdata1.hasAnalogOutputSignal()) {
            i = iblockdata1.getAnalogOutputSignal(world, blockposition1);
        } else if (i < 15 && iblockdata1.isRedstoneConductor(world, blockposition1)) {
            blockposition1 = blockposition1.relative(enumdirection);
            iblockdata1 = world.getBlockState(blockposition1);
            EntityItemFrame entityitemframe = this.getItemFrame(world, enumdirection, blockposition1);
            int j = Math.max(entityitemframe == null ? Integer.MIN_VALUE : entityitemframe.getAnalogOutput(), iblockdata1.hasAnalogOutputSignal() ? iblockdata1.getAnalogOutputSignal(world, blockposition1) : Integer.MIN_VALUE);

            if (j != Integer.MIN_VALUE) {
                i = j;
            }
        }

        return i;
    }

    @Nullable
    private EntityItemFrame getItemFrame(World world, EnumDirection enumdirection, BlockPosition blockposition) {
        List<EntityItemFrame> list = world.getEntitiesOfClass(EntityItemFrame.class, new AxisAlignedBB((double) blockposition.getX(), (double) blockposition.getY(), (double) blockposition.getZ(), (double) (blockposition.getX() + 1), (double) (blockposition.getY() + 1), (double) (blockposition.getZ() + 1)), (entityitemframe) -> {
            return entityitemframe != null && entityitemframe.getDirection() == enumdirection;
        });

        return list.size() == 1 ? (EntityItemFrame) list.get(0) : null;
    }

    @Override
    public EnumInteractionResult use(IBlockData iblockdata, World world, BlockPosition blockposition, EntityHuman entityhuman, EnumHand enumhand, MovingObjectPositionBlock movingobjectpositionblock) {
        if (!entityhuman.getAbilities().mayBuild) {
            return EnumInteractionResult.PASS;
        } else {
            iblockdata = (IBlockData) iblockdata.cycle(BlockRedstoneComparator.MODE);
            float f = iblockdata.getValue(BlockRedstoneComparator.MODE) == BlockPropertyComparatorMode.SUBTRACT ? 0.55F : 0.5F;

            world.playSound(entityhuman, blockposition, SoundEffects.COMPARATOR_CLICK, SoundCategory.BLOCKS, 0.3F, f);
            world.setBlock(blockposition, iblockdata, 2);
            this.refreshOutputState(world, blockposition, iblockdata);
            return EnumInteractionResult.sidedSuccess(world.isClientSide);
        }
    }

    @Override
    protected void checkTickOnNeighbor(World world, BlockPosition blockposition, IBlockData iblockdata) {
        if (!world.getBlockTicks().willTickThisTick(blockposition, this)) {
            int i = this.calculateOutputSignal(world, blockposition, iblockdata);
            TileEntity tileentity = world.getBlockEntity(blockposition);
            int j = tileentity instanceof TileEntityComparator ? ((TileEntityComparator) tileentity).getOutputSignal() : 0;

            if (i != j || (Boolean) iblockdata.getValue(BlockRedstoneComparator.POWERED) != this.shouldTurnOn(world, blockposition, iblockdata)) {
                TickListPriority ticklistpriority = this.shouldPrioritize(world, blockposition, iblockdata) ? TickListPriority.HIGH : TickListPriority.NORMAL;

                world.scheduleTick(blockposition, (Block) this, 2, ticklistpriority);
            }

        }
    }

    private void refreshOutputState(World world, BlockPosition blockposition, IBlockData iblockdata) {
        int i = this.calculateOutputSignal(world, blockposition, iblockdata);
        TileEntity tileentity = world.getBlockEntity(blockposition);
        int j = 0;

        if (tileentity instanceof TileEntityComparator) {
            TileEntityComparator tileentitycomparator = (TileEntityComparator) tileentity;

            j = tileentitycomparator.getOutputSignal();
            tileentitycomparator.setOutputSignal(i);
        }

        if (j != i || iblockdata.getValue(BlockRedstoneComparator.MODE) == BlockPropertyComparatorMode.COMPARE) {
            boolean flag = this.shouldTurnOn(world, blockposition, iblockdata);
            boolean flag1 = (Boolean) iblockdata.getValue(BlockRedstoneComparator.POWERED);

            if (flag1 && !flag) {
                world.setBlock(blockposition, (IBlockData) iblockdata.setValue(BlockRedstoneComparator.POWERED, false), 2);
            } else if (!flag1 && flag) {
                world.setBlock(blockposition, (IBlockData) iblockdata.setValue(BlockRedstoneComparator.POWERED, true), 2);
            }

            this.updateNeighborsInFront(world, blockposition, iblockdata);
        }

    }

    @Override
    public void tick(IBlockData iblockdata, WorldServer worldserver, BlockPosition blockposition, RandomSource randomsource) {
        this.refreshOutputState(worldserver, blockposition, iblockdata);
    }

    @Override
    public boolean triggerEvent(IBlockData iblockdata, World world, BlockPosition blockposition, int i, int j) {
        super.triggerEvent(iblockdata, world, blockposition, i, j);
        TileEntity tileentity = world.getBlockEntity(blockposition);

        return tileentity != null && tileentity.triggerEvent(i, j);
    }

    @Override
    public TileEntity newBlockEntity(BlockPosition blockposition, IBlockData iblockdata) {
        return new TileEntityComparator(blockposition, iblockdata);
    }

    @Override
    protected void createBlockStateDefinition(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.add(BlockRedstoneComparator.FACING, BlockRedstoneComparator.MODE, BlockRedstoneComparator.POWERED);
    }
}
