package net.minecraft.world.level.block;

import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.server.level.WorldServer;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.entity.decoration.EntityItemFrame;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.TickListPriority;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.entity.TileEntityComparator;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.BlockStateList;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockProperties;
import net.minecraft.world.level.block.state.properties.BlockPropertyComparatorMode;
import net.minecraft.world.level.block.state.properties.BlockStateEnum;
import net.minecraft.world.level.block.state.properties.IBlockState;
import net.minecraft.world.phys.AxisAlignedBB;
import net.minecraft.world.phys.MovingObjectPositionBlock;

public class BlockRedstoneComparator extends BlockDiodeAbstract implements ITileEntity {

    public static final BlockStateEnum<BlockPropertyComparatorMode> MODE = BlockProperties.MODE_COMPARATOR;

    public BlockRedstoneComparator(BlockBase.Info blockbase_info) {
        super(blockbase_info);
        this.k((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) this.stateDefinition.getBlockData()).set(BlockRedstoneComparator.FACING, EnumDirection.NORTH)).set(BlockRedstoneComparator.POWERED, false)).set(BlockRedstoneComparator.MODE, BlockPropertyComparatorMode.COMPARE));
    }

    @Override
    protected int g(IBlockData iblockdata) {
        return 2;
    }

    @Override
    protected int b(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata) {
        TileEntity tileentity = iblockaccess.getTileEntity(blockposition);

        return tileentity instanceof TileEntityComparator ? ((TileEntityComparator) tileentity).d() : 0;
    }

    private int e(World world, BlockPosition blockposition, IBlockData iblockdata) {
        int i = this.b(world, blockposition, iblockdata);

        if (i == 0) {
            return 0;
        } else {
            int j = this.b((IWorldReader) world, blockposition, iblockdata);

            return j > i ? 0 : (iblockdata.get(BlockRedstoneComparator.MODE) == BlockPropertyComparatorMode.SUBTRACT ? i - j : i);
        }
    }

    @Override
    protected boolean a(World world, BlockPosition blockposition, IBlockData iblockdata) {
        int i = this.b(world, blockposition, iblockdata);

        if (i == 0) {
            return false;
        } else {
            int j = this.b((IWorldReader) world, blockposition, iblockdata);

            return i > j ? true : i == j && iblockdata.get(BlockRedstoneComparator.MODE) == BlockPropertyComparatorMode.COMPARE;
        }
    }

    @Override
    protected int b(World world, BlockPosition blockposition, IBlockData iblockdata) {
        int i = super.b(world, blockposition, iblockdata);
        EnumDirection enumdirection = (EnumDirection) iblockdata.get(BlockRedstoneComparator.FACING);
        BlockPosition blockposition1 = blockposition.shift(enumdirection);
        IBlockData iblockdata1 = world.getType(blockposition1);

        if (iblockdata1.isComplexRedstone()) {
            i = iblockdata1.a(world, blockposition1);
        } else if (i < 15 && iblockdata1.isOccluding(world, blockposition1)) {
            blockposition1 = blockposition1.shift(enumdirection);
            iblockdata1 = world.getType(blockposition1);
            EntityItemFrame entityitemframe = this.a(world, enumdirection, blockposition1);
            int j = Math.max(entityitemframe == null ? Integer.MIN_VALUE : entityitemframe.z(), iblockdata1.isComplexRedstone() ? iblockdata1.a(world, blockposition1) : Integer.MIN_VALUE);

            if (j != Integer.MIN_VALUE) {
                i = j;
            }
        }

        return i;
    }

    @Nullable
    private EntityItemFrame a(World world, EnumDirection enumdirection, BlockPosition blockposition) {
        List<EntityItemFrame> list = world.a(EntityItemFrame.class, new AxisAlignedBB((double) blockposition.getX(), (double) blockposition.getY(), (double) blockposition.getZ(), (double) (blockposition.getX() + 1), (double) (blockposition.getY() + 1), (double) (blockposition.getZ() + 1)), (entityitemframe) -> {
            return entityitemframe != null && entityitemframe.getDirection() == enumdirection;
        });

        return list.size() == 1 ? (EntityItemFrame) list.get(0) : null;
    }

    @Override
    public EnumInteractionResult interact(IBlockData iblockdata, World world, BlockPosition blockposition, EntityHuman entityhuman, EnumHand enumhand, MovingObjectPositionBlock movingobjectpositionblock) {
        if (!entityhuman.getAbilities().mayBuild) {
            return EnumInteractionResult.PASS;
        } else {
            iblockdata = (IBlockData) iblockdata.a((IBlockState) BlockRedstoneComparator.MODE);
            float f = iblockdata.get(BlockRedstoneComparator.MODE) == BlockPropertyComparatorMode.SUBTRACT ? 0.55F : 0.5F;

            world.playSound(entityhuman, blockposition, SoundEffects.COMPARATOR_CLICK, SoundCategory.BLOCKS, 0.3F, f);
            world.setTypeAndData(blockposition, iblockdata, 2);
            this.f(world, blockposition, iblockdata);
            return EnumInteractionResult.a(world.isClientSide);
        }
    }

    @Override
    protected void c(World world, BlockPosition blockposition, IBlockData iblockdata) {
        if (!world.getBlockTickList().b(blockposition, this)) {
            int i = this.e(world, blockposition, iblockdata);
            TileEntity tileentity = world.getTileEntity(blockposition);
            int j = tileentity instanceof TileEntityComparator ? ((TileEntityComparator) tileentity).d() : 0;

            if (i != j || (Boolean) iblockdata.get(BlockRedstoneComparator.POWERED) != this.a(world, blockposition, iblockdata)) {
                TickListPriority ticklistpriority = this.c((IBlockAccess) world, blockposition, iblockdata) ? TickListPriority.HIGH : TickListPriority.NORMAL;

                world.getBlockTickList().a(blockposition, this, 2, ticklistpriority);
            }

        }
    }

    private void f(World world, BlockPosition blockposition, IBlockData iblockdata) {
        int i = this.e(world, blockposition, iblockdata);
        TileEntity tileentity = world.getTileEntity(blockposition);
        int j = 0;

        if (tileentity instanceof TileEntityComparator) {
            TileEntityComparator tileentitycomparator = (TileEntityComparator) tileentity;

            j = tileentitycomparator.d();
            tileentitycomparator.a(i);
        }

        if (j != i || iblockdata.get(BlockRedstoneComparator.MODE) == BlockPropertyComparatorMode.COMPARE) {
            boolean flag = this.a(world, blockposition, iblockdata);
            boolean flag1 = (Boolean) iblockdata.get(BlockRedstoneComparator.POWERED);

            if (flag1 && !flag) {
                world.setTypeAndData(blockposition, (IBlockData) iblockdata.set(BlockRedstoneComparator.POWERED, false), 2);
            } else if (!flag1 && flag) {
                world.setTypeAndData(blockposition, (IBlockData) iblockdata.set(BlockRedstoneComparator.POWERED, true), 2);
            }

            this.d(world, blockposition, iblockdata);
        }

    }

    @Override
    public void tickAlways(IBlockData iblockdata, WorldServer worldserver, BlockPosition blockposition, Random random) {
        this.f((World) worldserver, blockposition, iblockdata);
    }

    @Override
    public boolean a(IBlockData iblockdata, World world, BlockPosition blockposition, int i, int j) {
        super.a(iblockdata, world, blockposition, i, j);
        TileEntity tileentity = world.getTileEntity(blockposition);

        return tileentity != null && tileentity.setProperty(i, j);
    }

    @Override
    public TileEntity createTile(BlockPosition blockposition, IBlockData iblockdata) {
        return new TileEntityComparator(blockposition, iblockdata);
    }

    @Override
    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockRedstoneComparator.FACING, BlockRedstoneComparator.MODE, BlockRedstoneComparator.POWERED);
    }
}
