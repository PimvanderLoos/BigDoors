package net.minecraft.server;

import java.util.Random;
import javax.annotation.Nullable;

public class BlockBarrel extends BlockTileEntity {

    public static final BlockStateDirection a = BlockProperties.M;
    public static final BlockStateBoolean b = BlockProperties.u;

    public BlockBarrel(BlockBase.Info blockbase_info) {
        super(blockbase_info);
        this.j((IBlockData) ((IBlockData) ((IBlockData) this.blockStateList.getBlockData()).set(BlockBarrel.a, EnumDirection.NORTH)).set(BlockBarrel.b, false));
    }

    @Override
    public EnumInteractionResult interact(IBlockData iblockdata, World world, BlockPosition blockposition, EntityHuman entityhuman, EnumHand enumhand, MovingObjectPositionBlock movingobjectpositionblock) {
        if (world.isClientSide) {
            return EnumInteractionResult.SUCCESS;
        } else {
            TileEntity tileentity = world.getTileEntity(blockposition);

            if (tileentity instanceof TileEntityBarrel) {
                entityhuman.openContainer((TileEntityBarrel) tileentity);
                entityhuman.a(StatisticList.OPEN_BARREL);
                PiglinAI.a(entityhuman, true);
            }

            return EnumInteractionResult.CONSUME;
        }
    }

    @Override
    public void remove(IBlockData iblockdata, World world, BlockPosition blockposition, IBlockData iblockdata1, boolean flag) {
        if (!iblockdata.a(iblockdata1.getBlock())) {
            TileEntity tileentity = world.getTileEntity(blockposition);

            if (tileentity instanceof IInventory) {
                InventoryUtils.dropInventory(world, blockposition, (IInventory) tileentity);
                world.updateAdjacentComparators(blockposition, this);
            }

            super.remove(iblockdata, world, blockposition, iblockdata1, flag);
        }
    }

    @Override
    public void tickAlways(IBlockData iblockdata, WorldServer worldserver, BlockPosition blockposition, Random random) {
        TileEntity tileentity = worldserver.getTileEntity(blockposition);

        if (tileentity instanceof TileEntityBarrel) {
            ((TileEntityBarrel) tileentity).h();
        }

    }

    @Nullable
    @Override
    public TileEntity createTile(IBlockAccess iblockaccess) {
        return new TileEntityBarrel();
    }

    @Override
    public EnumRenderType b(IBlockData iblockdata) {
        return EnumRenderType.MODEL;
    }

    @Override
    public void postPlace(World world, BlockPosition blockposition, IBlockData iblockdata, @Nullable EntityLiving entityliving, ItemStack itemstack) {
        if (itemstack.hasName()) {
            TileEntity tileentity = world.getTileEntity(blockposition);

            if (tileentity instanceof TileEntityBarrel) {
                ((TileEntityBarrel) tileentity).setCustomName(itemstack.getName());
            }
        }

    }

    @Override
    public boolean isComplexRedstone(IBlockData iblockdata) {
        return true;
    }

    @Override
    public int a(IBlockData iblockdata, World world, BlockPosition blockposition) {
        return Container.a(world.getTileEntity(blockposition));
    }

    @Override
    public IBlockData a(IBlockData iblockdata, EnumBlockRotation enumblockrotation) {
        return (IBlockData) iblockdata.set(BlockBarrel.a, enumblockrotation.a((EnumDirection) iblockdata.get(BlockBarrel.a)));
    }

    @Override
    public IBlockData a(IBlockData iblockdata, EnumBlockMirror enumblockmirror) {
        return iblockdata.a(enumblockmirror.a((EnumDirection) iblockdata.get(BlockBarrel.a)));
    }

    @Override
    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockBarrel.a, BlockBarrel.b);
    }

    @Override
    public IBlockData getPlacedState(BlockActionContext blockactioncontext) {
        return (IBlockData) this.getBlockData().set(BlockBarrel.a, blockactioncontext.d().opposite());
    }
}
